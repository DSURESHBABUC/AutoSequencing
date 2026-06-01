package com.tvsm.autoseq.pages.autoseq;

import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;

import java.util.Arrays;
import java.util.List;

/**
 * DataMaintenancePage — Page Object for the Data Maintenance tab.
 * Application: https://uat-sns.tvsmotor.net/Autoseq
 */
public class DataMaintenancePage extends BasePage {

    private static final String PAGE_HEADER = ".mat-tab-label-content:has-text('Data Maintenance'), .page-title:has-text('Data Maintenance')";

    public static final List<String> ALL_ITEMS = Arrays.asList(
            "Stock Maintenance Details",
            "Shift active details",
            "Conveyor Hour Master",
            "Conveyor Active Hour Report",
            "Conveyor Location Master",
            "Model Line Balance",
            "Stagewise Leadtime",
            "Engine SKU Conveyor Priority",
            "Model Level Prioritization",
            "Variant Level Prioritization",
            "SKU Level Priority",
            "Rejection Buffer",
            "JIG Master",
            "Paint Part Type JIG Allocation",
            "JIG Density",
            "Packing Type",
            "Paint Shop Stock",
            "Part Vs Part Group",
            "Storage Location Master",
            "Variant Day Batch Check",
            "Conveyor Capacity Check",
            "Max Demand SKU Check",
            "Open Workorder",
            "Resource Grouping"
    );

    public DataMaintenancePage(Page page) {
        super(page);
    }

    public DataMaintenancePage clickItem(String itemName) {
        String selector = ".list-title:has-text(\"" + itemName + "\")";
        waitForVisible(selector).click();
        page.waitForTimeout(2000);
        return this;
    }

    public boolean isDataMaintenanceLoaded() {
        return page.locator(PAGE_HEADER).count() > 0
            || page.locator(".list-title").count() > 0;
    }

    public boolean isItemVisible(String itemName) {
        return isVisible(".list-title:has-text(\"" + itemName + "\")");
    }

    public int getVisibleItemCount() {
        return page.locator(".list-title").count();
    }
}
