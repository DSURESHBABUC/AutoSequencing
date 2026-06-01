package com.tvsm.autoseq.base;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.config.ConfigReader;
import java.nio.file.Paths;

public abstract class BasePage {

    protected final Page page;
    protected final int timeout;

    public BasePage(Page page) {
        this.page    = page;
        this.timeout = ConfigReader.defaultTimeout();
    }

    protected Locator waitForVisible(String selector) {
        Locator loc = page.locator(selector);
        loc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
        return loc;
    }

    protected Locator waitForClickable(String selector) {
        Locator loc = page.locator(selector);
        loc.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
        return loc;
    }

    protected void waitForHidden(String selector) {
        page.locator(selector).waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(timeout));
    }

    protected void safeClick(String selector) {
        try {
            waitForClickable(selector).click();
        } catch (Exception e) {
            page.locator(selector).first().evaluate("el => el.click()");
        }
    }

    protected void safeClick(Locator locator, String name) {
        try {
            locator.click();
        } catch (Exception e) {
            locator.evaluate("el => el.click()");
        }
    }

    protected void fill(String selector, String value) {
        Locator loc = waitForVisible(selector);
        loc.clear();
        loc.fill(value);
    }

    protected String getText(String selector) {
        return waitForVisible(selector).innerText().trim();
    }

    protected boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    protected void selectMatOption(String matSelectSelector, String optionText) {
        Locator dropdown = waitForClickable(matSelectSelector);
        safeClick(dropdown, "dropdown: " + matSelectSelector);
        String optionLocator = "mat-option:has-text(\"" + optionText + "\")";
        waitForVisible(optionLocator).click();
    }

    protected void clickMainTab(String tabName) {
        String selector = ".mat-tab-label-content:has-text(\"" + tabName + "\")";
        waitForClickable(selector).click();
    }

    public String takeScreenshot(String fileName) {
        String path = ConfigReader.screenshotDir() + "/" + fileName + ".png";
        page.screenshot(new Page.ScreenshotOptions().setPath(Paths.get(path)).setFullPage(true));
        return path;
    }

    public String getTitle()      { return page.title(); }
    public String getCurrentUrl() { return page.url(); }
}
