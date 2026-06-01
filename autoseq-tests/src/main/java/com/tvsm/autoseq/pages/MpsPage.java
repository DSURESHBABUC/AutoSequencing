package com.tvsm.autoseq.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;

import java.util.ArrayList;
import java.util.List;

/**
 * MpsPage — Page Object for the MPS (Master Production Schedule) tab.
 * Application: https://uat-sns.tvsmotor.net/Autoseq
 */
public class MpsPage extends BasePage {

    private static final String MPS_PLAN_TAB        = ".mat-tab-label-content:has-text('Mps Plan')";
    private static final String DATA_TABLE_ROWS      = "#dataTable tbody tr, table tbody tr";
    private static final String IMPORT_STATUS_COL   = "td:nth-child(5)";
    private static final String VALIDATE_STATUS_COL = "td:nth-child(6)";
    private static final String PAGE_HEADER         = ".mat-tab-label.mat-tab-label-active .mat-tab-label-content";

    public MpsPage(Page page) {
        super(page);
    }

    public MpsPage clickMpsPlan() {
        waitForClickable(MPS_PLAN_TAB).click();
        System.out.println("✅ Clicked Mps Plan sub-tab");
        return this;
    }

    public boolean isMpsPageLoaded() {
        return page.locator(PAGE_HEADER).count() > 0
            || page.locator(".mat-tab-label-content:has-text('MPS')").count() > 0;
    }

    public List<String> validateDataImportStatus() {
        List<String> failures = new ArrayList<>();
        Locator rows = page.locator(DATA_TABLE_ROWS);
        int count = rows.count();
        for (int i = 0; i < count; i++) {
            Locator row = rows.nth(i);
            String importStatus   = row.locator(IMPORT_STATUS_COL).innerText().trim();
            String validateStatus = row.locator(VALIDATE_STATUS_COL).innerText().trim();
            if (!importStatus.equalsIgnoreCase("Success")
                    || !validateStatus.equalsIgnoreCase("Success")) {
                failures.add("Row " + (i + 1)
                        + " — Import: " + importStatus
                        + ", Validation: " + validateStatus);
            }
        }
        return failures;
    }

    public int getTableRowCount() {
        return page.locator(DATA_TABLE_ROWS).count();
    }
}
