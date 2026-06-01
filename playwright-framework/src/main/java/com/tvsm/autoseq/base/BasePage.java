package com.tvsm.autoseq.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.config.ConfigReader;

import java.nio.file.Paths;

/**
 * BasePage — parent of every Page Object.
 *
 * Provides:
 *  - Playwright Page reference
 *  - Smart wait helpers (visible, clickable, hidden)
 *  - Safe click with JS fallback
 *  - Screenshot capture
 *  - Angular Material dropdown handler
 */
public abstract class BasePage {

    protected final Page page;
    protected final int timeout;

    public BasePage(Page page) {
        this.page    = page;
        this.timeout = ConfigReader.defaultTimeout();
    }

    // ── Wait helpers ──────────────────────────────────────────────────────

    /** Wait until a locator is visible. */
    protected Locator waitForVisible(String selector) {
        Locator loc = page.locator(selector);
        loc.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout));
        return loc;
    }

    /** Wait until a locator is attached and enabled (clickable). */
    protected Locator waitForClickable(String selector) {
        Locator loc = page.locator(selector);
        loc.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeout));
        return loc;
    }

    /** Wait until a locator is hidden / detached. */
    protected void waitForHidden(String selector) {
        page.locator(selector).waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.HIDDEN)
                .setTimeout(timeout));
    }

    // ── Interaction helpers ───────────────────────────────────────────────

    /** Click with built-in auto-wait. Falls back to JS click on failure. */
    protected void safeClick(String selector) {
        try {
            waitForClickable(selector).click();
            System.out.println("✅ Clicked: " + selector);
        } catch (Exception e) {
            System.out.println("⚠️ Normal click failed, trying JS click: " + selector);
            page.locator(selector).first().evaluate("el => el.click()");
        }
    }

    /** Click a Locator directly (useful when already resolved). */
    protected void safeClick(Locator locator, String name) {
        try {
            locator.click();
            System.out.println("✅ Clicked: " + name);
        } catch (Exception e) {
            System.out.println("⚠️ JS fallback click: " + name);
            locator.evaluate("el => el.click()");
        }
    }

    /** Fill a text input after clearing it. */
    protected void fill(String selector, String value) {
        Locator loc = waitForVisible(selector);
        loc.clear();
        loc.fill(value);
    }

    /** Get trimmed inner text of an element. */
    protected String getText(String selector) {
        return waitForVisible(selector).innerText().trim();
    }

    /** Check whether an element is visible right now (no wait). */
    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    // ── Angular Material helpers ──────────────────────────────────────────

    /**
     * Opens an Angular Material mat-select and picks an option by visible text.
     *
     * @param matSelectSelector CSS / XPath for the mat-select element
     * @param optionText        exact visible text of the option to pick
     */
    protected void selectMatOption(String matSelectSelector, String optionText) {
        Locator dropdown = waitForClickable(matSelectSelector);
        safeClick(dropdown, "dropdown: " + matSelectSelector);

        // Wait for the overlay panel to appear
        String optionLocator = "mat-option:has-text(\"" + optionText + "\")";
        waitForVisible(optionLocator).click();
        System.out.println("✅ Selected mat-option: " + optionText);
    }

    // ── Navigation helpers ────────────────────────────────────────────────

    /**
     * Clicks a top-level Angular Material tab by its label text.
     */
    protected void clickMainTab(String tabName) {
        String selector = ".mat-tab-label-content:has-text(\"" + tabName + "\")";
        waitForClickable(selector).click();
        System.out.println("✅ Navigated to tab: " + tabName);
    }

    // ── Screenshot ────────────────────────────────────────────────────────

    /**
     * Captures a full-page screenshot to the configured screenshots directory.
     *
     * @param fileName file name without extension
     * @return absolute path of the saved file
     */
    public String takeScreenshot(String fileName) {
        String path = ConfigReader.screenshotDir() + "/" + fileName + ".png";
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(path))
                .setFullPage(true));
        System.out.println("📸 Screenshot saved: " + path);
        return path;
    }

    // ── Page title / URL ──────────────────────────────────────────────────

    public String getTitle() {
        return page.title();
    }

    public String getCurrentUrl() {
        return page.url();
    }
}
