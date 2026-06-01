package com.tvsm.autoseq.pages.manualseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;

/**
 * ManualSeqHomePage — main app shell after login for Manual Sequencing (QAS).
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest
 */
public class ManualSeqHomePage extends BasePage {

    // Tab names — adjust if the QAS app uses different labels
    public static final String TAB_SEQUENCE_LIVE    = "Sequence Live";
    public static final String TAB_GROUP_MASTER     = "Group Master";
    public static final String TAB_MPS              = "MPS";
    public static final String TAB_REPORT           = "Report";
    public static final String TAB_DATA_MAINTENANCE = "Data Maintenance";

    public ManualSeqHomePage(Page page) {
        super(page);
    }

    public ManualSeqHomePage clickTab(String tabName) {
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

    public ManualSeqSequenceLivePage goToSequenceLive() {
        clickTab(TAB_SEQUENCE_LIVE);
        return new ManualSeqSequenceLivePage(page);
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

    public int getVisibleTabCount() {
        String[] selectors = {
            ".mat-tab-label-content",
            ".mat-mdc-tab-label-content",
            "[role='tab']"
        };
        for (String sel : selectors) {
            int count = page.locator(sel).count();
            if (count > 0) return count;
        }
        return 0;
    }

    public String getPageUrl() {
        return page.url();
    }
}
