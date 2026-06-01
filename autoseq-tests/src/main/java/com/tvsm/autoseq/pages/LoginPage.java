package com.tvsm.autoseq.pages;

import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;
import com.tvsm.autoseq.config.ConfigReader;

/**
 * LoginPage — Microsoft SSO login page for TVS AutoSequencing.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/login
 */
public class LoginPage extends BasePage {

    private static final String EMAIL_INPUT    = "#i0116";
    private static final String NEXT_BUTTON    = "#idSIButton9";
    private static final String PASSWORD_INPUT = "#i0118";
    private static final String SIGN_IN_BUTTON = "#idSIButton9";
    private static final String STAY_SIGNED_IN = "#idSIButton9";

    public LoginPage(Page page) {
        super(page);
    }

    public LoginPage navigate() {
        page.navigate(ConfigReader.baseUrl());
        page.waitForSelector(EMAIL_INPUT,
                new Page.WaitForSelectorOptions().setTimeout(timeout));
        System.out.println("🌐 Navigated to: " + ConfigReader.baseUrl());
        return this;
    }

    public LoginPage enterEmail(String email) {
        waitForVisible(EMAIL_INPUT).fill(email);
        System.out.println("📧 Entered email: " + email);
        return this;
    }

    public LoginPage clickNext() {
        page.waitForSelector(NEXT_BUTTON + ":not([disabled])",
                new Page.WaitForSelectorOptions().setTimeout(timeout));
        page.locator(NEXT_BUTTON).click();
        System.out.println("➡️  Clicked Next");
        return this;
    }

    public LoginPage enterPassword(String password) {
        waitForVisible(PASSWORD_INPUT).fill(password);
        System.out.println("🔑 Entered password");
        return this;
    }

    public LoginPage clickSignIn() {
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

    public LoginPage handleStaySignedIn() {
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

    public void login(String email, String password) {
        navigate();
        enterEmail(email);
        clickNext();
        enterPassword(password);
        clickSignIn();
        handleStaySignedIn();
    }

    public void loginWithDefaultCredentials() {
        login(ConfigReader.username(), ConfigReader.password());
    }

    public boolean isLoginPageLoaded() {
        try {
            page.waitForSelector(EMAIL_INPUT,
                    new Page.WaitForSelectorOptions().setTimeout(timeout));
            return page.locator(EMAIL_INPUT).isVisible();
        } catch (Exception e) {
            return false;
        }
    }

    public boolean isPasswordStepVisible() {
        return isVisible(PASSWORD_INPUT);
    }
}
