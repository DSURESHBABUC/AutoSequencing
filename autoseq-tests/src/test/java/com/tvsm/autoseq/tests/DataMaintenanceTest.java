package com.tvsm.autoseq.tests;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.DataMaintenancePage;
import com.tvsm.autoseq.pages.HomePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * DataMaintenanceTest — verifies navigation to all 24 Data Maintenance sub-screens.
 * Application: https://uat-sns.tvsmotor.net/Autoseq
 *
 *   TC-DM-001  Data Maintenance tab loads
 *   TC-DM-002  All sidebar items are visible
 *   TC-DM-003  Each sub-screen opens without error (data-driven)
 */
public class DataMaintenanceTest extends BaseTest {

    private DataMaintenancePage dmPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToPage() {
        page.navigate(ConfigReader.baseUrl());
        waitForAppLoad();
        dmPage = new HomePage(page).goToDataMaintenance();
        page.waitForTimeout(2000);
    }

    @Test(priority = 1, description = "TC-DM-001: Data Maintenance page should load")
    public void dataMaintenancePageLoadsTest() {
        Assert.assertTrue(dmPage.isDataMaintenanceLoaded(), "Data Maintenance page did not load");
        logInfo("✅ Data Maintenance page loaded");
        captureScreenshot("TC-DM-001_DataMaintenanceLoaded");
    }

    @Test(priority = 2, dependsOnMethods = "dataMaintenancePageLoadsTest",
          description = "TC-DM-002: All expected sidebar items should be visible")
    public void allSidebarItemsVisibleTest() {
        int visibleCount = dmPage.getVisibleItemCount();
        logInfo("Visible sidebar items: " + visibleCount);
        Assert.assertTrue(visibleCount > 0, "No sidebar items visible");
        for (String item : DataMaintenancePage.ALL_ITEMS) {
            boolean visible = dmPage.isItemVisible(item);
            logInfo((visible ? "✅" : "❌") + " Item: " + item);
            Assert.assertTrue(visible, "Sidebar item not visible: " + item);
        }
        captureScreenshot("TC-DM-002_SidebarItems");
    }

    @DataProvider(name = "dataMaintenanceItems")
    public Object[][] dataMaintenanceItems() {
        return DataMaintenancePage.ALL_ITEMS.stream()
                .map(item -> new Object[]{item})
                .toArray(Object[][]::new);
    }

    @Test(priority = 3, dataProvider = "dataMaintenanceItems",
          dependsOnMethods = "allSidebarItemsVisibleTest",
          description = "TC-DM-003: Each Data Maintenance sub-screen should open without error")
    public void eachSubScreenOpensTest(String itemName) {
        logInfo("Opening: " + itemName);
        dmPage.clickItem(itemName);
        String url = dmPage.getCurrentUrl();
        Assert.assertTrue(url.contains("Autoseq"),
                "Navigated away from app after clicking: " + itemName + " | URL: " + url);
        logInfo("✅ Opened: " + itemName);
        captureScreenshot("TC-DM-003_" + itemName.replaceAll("[^a-zA-Z0-9]", "_"));
        new HomePage(page).goToDataMaintenance();
        page.waitForTimeout(1500);
    }
}
