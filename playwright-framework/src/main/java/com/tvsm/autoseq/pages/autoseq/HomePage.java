package com.tvsm.autoseq.pages.autoseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;

/**
 * HomePage — main app shell after login for TVS AutoSequencing.
 * Application: https://uat-sns.tvsmotor.net/Autoseq
 */
public class HomePage extends BasePage {

    public static final String TAB_SEQUENCE_LIVE         = "Sequence Live";
    public static final String TAB_NEW_SEQUENCE_LIVE     = "New Sequence Live";
    public static final String TAB_MPS                   = "MPS";
    public static final String TAB_PROD_PLAN_VS_SEQ_PLAN = "Production Plan Vs Sequence Plan";
    public static final String TAB_HEX1_SEQ_PLANNING     = "HEX1 Sequence Planning";
    public static final String TAB_REPORT                = "Report";
    public static final String TAB_USAGE_ANALYSIS        = "Usage Analysis";
    public static final String TAB_DATA_MAINTENANCE      = "Data Maintenance";

    public HomePage(Page page) {
        super(page);
    }

    public HomePage clickTab(String tabName) {
        String[] selectors = {
            ".mat-tab-label-content:has-text(\"" + tabName + "\")",
            ".mat-mdc-tab-label-content:has-text(\"" + tabName + "\")",
            "[role='tab']:has-text(\"" + tabName + "\")",
            "a:has-text(\"" + tabName + "\")",
            "button:has-text(\"" + tabName + "\")",
            "li:has-text(\"" + tabName + "\")"
        };
        for (String selector : selectors) {
            try {
                Locator loc = page.locator(selector).first();
                loc.waitFor(new Locator.WaitForOptions()
                        .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
                loc.click();
                System.out.println("✅ Clicked tab: " + tabName);
                return this;
            } catch (Exception ignored) {}
        }
        throw new RuntimeException("Could not find tab: " + tabName);
    }

    public NewSequenceLivePage goToNewSequenceLive() {
        clickTab(TAB_NEW_SEQUENCE_LIVE);
        return new NewSequenceLivePage(page);
    }

    public MpsPage goToMps() {
        clickTab(TAB_MPS);
        return new MpsPage(page);
    }

    public DataMaintenancePage goToDataMaintenance() {
        clickTab(TAB_DATA_MAINTENANCE);
        return new DataMaintenancePage(page);
    }

    public boolean isLoggedIn() {
        String[] indicators = {
            "app-root", ".mat-tab-label-content",
            ".mat-mdc-tab-label-content", "[role='tab']", "nav", ".navbar"
        };
        for (String sel : indicators) {
            try {
                page.waitForSelector(sel,
                        new Page.WaitForSelectorOptions().setTimeout(5000));
                if (page.locator(sel).count() > 0) return true;
            } catch (Exception ignored) {}
        }
        return false;
    }

    public boolean isTabVisible(String tabName) {
        String[] selectors = {
            ".mat-tab-label-content:has-text(\"" + tabName + "\")",
            ".mat-mdc-tab-label-content:has-text(\"" + tabName + "\")",
            "[role='tab']:has-text(\"" + tabName + "\")",
            "a:has-text(\"" + tabName + "\")"
        };
        for (String sel : selectors) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    public String getActiveTabName() {
        return page.locator(".mat-tab-label.mat-tab-label-active .mat-tab-label-content")
                   .innerText().trim();
    }
}
