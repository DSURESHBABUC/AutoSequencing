package com.tvsm.autoseq.pages.manualseq;

import com.microsoft.playwright.BrowserContext;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;
import com.tvsm.autoseq.config.ConfigReader;

import java.io.File;
import java.nio.file.Paths;

/**
 * ManualSeqLoginPage — Microsoft SSO login page for Manual Sequencing (QAS).
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest
 *
 * MFA / 2FA strategy:
 *   Microsoft Entra ID enforces 2FA at the identity provider — we cannot
 *   disable it from the client. Instead, this page:
 *
 *     1. Reuses an existing storage state at auth/state.json (no login at all).
 *     2. If a fresh login is required, detects the MFA challenge and pauses
 *        the headful browser so the user can approve the prompt ONCE.
 *     3. On success, persists the QAS session back to auth/state.json so
 *        subsequent runs (within the MFA grace window) skip 2FA entirely.
 */
public class ManualSeqLoginPage extends BasePage {

    // Microsoft login DOM
    private static final String EMAIL_INPUT    = "#i0116";
    private static final String NEXT_BUTTON    = "#idSIButton9";
    private static final String PASSWORD_INPUT = "#i0118";
    private static final String SIGN_IN_BUTTON = "#idSIButton9";
    private static final String STAY_SIGNED_IN = "#idSIButton9";

    // MFA / verification selectors
    private static final String MFA_NUMBER_MATCH      = "#idRichContext_DisplaySign"; // number to enter in Authenticator
    private static final String MFA_OTC_INPUT         = "#idTxtBx_SAOTCC_OTC";        // SMS / TOTP one-time code box
    private static final String MFA_PROMPT_TITLE      = "#idDiv_SAASTO_Title";
    private static final String MFA_APPROVE_DESC      = "#idDiv_SAOTCC_Description";
    private static final String MFA_DONT_ASK_AGAIN    = "#idChkBx_SAOTCC_TD";

    private static final String AUTH_STATE_PATH = "auth/state.json";

    /** Hard timeout for waiting on the user to complete MFA, in milliseconds. */
    private static final int MFA_WAIT_MS = 180_000; // 3 minutes

    public ManualSeqLoginPage(Page page) {
        super(page);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Public API
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Logs in only if the saved storage state is missing/expired. If the
     * existing context already has a valid QAS session, this is a no-op
     * and 2FA is NOT triggered.
     *
     * Use this from test @BeforeClass instead of loginWithDefaultCredentials()
     * to avoid hammering the Microsoft login flow on every run.
     */
    public void loginIfNeeded() {
        page.navigate(ConfigReader.manualSeqBaseUrl());
        page.waitForTimeout(2500);

        if (isAlreadyAuthenticated()) {
            System.out.println("♻️  Already authenticated — skipping login");
            return;
        }
        loginWithDefaultCredentials();
    }

    /** Convenience: runs the full login flow with credentials from config. */
    public void loginWithDefaultCredentials() {
        login(ConfigReader.username(), ConfigReader.password());
    }

    /** Full login flow including MFA handling and storage-state persistence. */
    public void login(String email, String password) {
        if (!page.url().contains("login.microsoftonline.com")
                && !isLoginPageLoaded()) {
            navigate();
        }
        enterEmail(email);
        clickNext();
        enterPassword(password);
        clickSignIn();
        handleMfaIfPresent();
        handleStaySignedIn();
        persistAuthState();
    }

    public ManualSeqLoginPage navigate() {
        page.navigate(ConfigReader.manualSeqBaseUrl());
        page.waitForSelector(EMAIL_INPUT,
                new Page.WaitForSelectorOptions().setTimeout(timeout));
        System.out.println("🌐 Navigated to: " + ConfigReader.manualSeqBaseUrl());
        return this;
    }

    public ManualSeqLoginPage enterEmail(String email) {
        waitForVisible(EMAIL_INPUT).fill(email);
        System.out.println("📧 Entered email: " + email);
        return this;
    }

    public ManualSeqLoginPage clickNext() {
        page.waitForSelector(NEXT_BUTTON + ":not([disabled])",
                new Page.WaitForSelectorOptions().setTimeout(timeout));
        page.locator(NEXT_BUTTON).click();
        System.out.println("➡️  Clicked Next");
        return this;
    }

    public ManualSeqLoginPage enterPassword(String password) {
        waitForVisible(PASSWORD_INPUT).fill(password);
        System.out.println("🔑 Entered password");
        return this;
    }

    public ManualSeqLoginPage clickSignIn() {
        page.waitForSelector(SIGN_IN_BUTTON + ":not([disabled])",
                new Page.WaitForSelectorOptions().setTimeout(timeout));
        try {
            page.waitForNavigation(() ->
                page.locator(SIGN_IN_BUTTON).evaluate("el => el.click()"));
        } catch (Exception e) {
            System.out.println("ℹ️  Navigation after sign-in: " + e.getMessage());
        }
        System.out.println("🔐 Clicked Sign In");
        return this;
    }

    public ManualSeqLoginPage handleStaySignedIn() {
        try {
            page.waitForSelector(STAY_SIGNED_IN,
                    new Page.WaitForSelectorOptions().setTimeout(8000));
            try {
                page.waitForNavigation(() ->
                    page.locator(STAY_SIGNED_IN).evaluate("el => el.click()"));
            } catch (Exception ignored) {}
            System.out.println("✅ Handled Stay Signed In prompt");
        } catch (Exception e) {
            System.out.println("ℹ️  No Stay Signed In prompt appeared");
        }
        return this;
    }

    public boolean isLoginPageLoaded() {
        try {
            page.waitForSelector(EMAIL_INPUT,
                    new Page.WaitForSelectorOptions().setTimeout(3000));
            return page.locator(EMAIL_INPUT).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordStepVisible() {
        return isVisible(PASSWORD_INPUT);
    }

    // ─────────────────────────────────────────────────────────────────────
    // 2FA / MFA handling
    // ─────────────────────────────────────────────────────────────────────

    /**
     * Detects Microsoft 2FA challenges (Authenticator app number match,
     * SMS one-time code, generic verification prompts) and waits for the
     * user to complete the challenge.
     *
     * The browser is headful for manualseq tests, so the operator can
     * approve the prompt on their phone or enter the code. We wait up to
     * MFA_WAIT_MS for the page to leave the Microsoft login domain.
     */
    public void handleMfaIfPresent() {
        // Give Microsoft a moment to render the next step
        try { page.waitForTimeout(1500); } catch (Exception ignored) {}

        boolean numberMatch = isVisible(MFA_NUMBER_MATCH);
        boolean otcInput    = isVisible(MFA_OTC_INPUT);
        boolean approveText = isVisible(MFA_APPROVE_DESC);
        boolean kmsiOnly    = isVisible(STAY_SIGNED_IN) && !approveText && !numberMatch && !otcInput;

        if (!(numberMatch || otcInput || approveText) || kmsiOnly) {
            // No MFA — likely already past it (KMSI / app redirect)
            return;
        }

        if (numberMatch) {
            String code = "(unknown)";
            try { code = page.locator(MFA_NUMBER_MATCH).innerText().trim(); } catch (Exception ignored) {}
            System.out.println("🔐 MFA — Authenticator number-match challenge.");
            System.out.println("    👉 Approve sign-in on your phone using code: " + code);
        } else if (otcInput) {
            System.out.println("🔐 MFA — One-time code required (SMS or Authenticator).");
            System.out.println("    👉 Enter the code in the browser to continue.");
        } else {
            System.out.println("🔐 MFA — Verification prompt detected.");
            System.out.println("    👉 Complete the prompt in the browser to continue.");
        }

        // Try to tick "Don't ask again on this device" so subsequent logins
        // within the grace window skip MFA entirely.
        try {
            if (isVisible(MFA_DONT_ASK_AGAIN)) {
                page.locator(MFA_DONT_ASK_AGAIN).check();
                System.out.println("☑️  Selected 'Don't ask again on this device'");
            }
        } catch (Exception ignored) {}

        // Wait for navigation off the login domain (success) or for the
        // password / email screen to disappear.
        long deadline = System.currentTimeMillis() + MFA_WAIT_MS;
        while (System.currentTimeMillis() < deadline) {
            String url = page.url();
            if (!url.contains("login.microsoftonline.com")
                    && !url.contains("login.live.com")) {
                System.out.println("✅ MFA completed — redirected to: " + url);
                return;
            }
            try { page.waitForTimeout(2000); } catch (Exception ignored) {}
        }
        System.out.println("⚠️  MFA wait timed out after " + (MFA_WAIT_MS / 1000) + "s — continuing");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Storage-state persistence (so we never solve 2FA twice in the window)
    // ─────────────────────────────────────────────────────────────────────

    /** Saves the current browser context's cookies + localStorage to disk. */
    public void persistAuthState() {
        try {
            BrowserContext ctx = page.context();
            new File("auth").mkdirs();
            ctx.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(Paths.get(AUTH_STATE_PATH)));
            System.out.println("💾 QAS auth state saved to: " + AUTH_STATE_PATH);
        } catch (Exception e) {
            System.out.println("⚠️  Could not save auth state: " + e.getMessage());
        }
    }

    /**
     * Returns true if the current page is on the QAS application (not the
     * Microsoft login domain) — i.e. the saved storage state is still valid
     * and no new login is required.
     */
    private boolean isAlreadyAuthenticated() {
        String url = page.url();
        if (url.contains("login.microsoftonline.com") || url.contains("login.live.com")) {
            return false;
        }
        if (!url.contains("tvsmsrvrqas.tvsmotor.net")) {
            return false;
        }
        // Email/password fields gone => we're past Microsoft auth
        try {
            return !page.locator(EMAIL_INPUT).first().isVisible()
                && !page.locator(PASSWORD_INPUT).first().isVisible();
        } catch (Exception e) {
            return true;
        }
    }
}
