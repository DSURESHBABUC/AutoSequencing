package com.tvsm.autoseq.tests.autoseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.autoseq.HomePage;
import com.tvsm.autoseq.pages.autoseq.MpsPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * MpsTest — test coverage for the MPS (Master Production Schedule) tab.
 * Application: https://uat-sns.tvsmotor.net/Autoseq
 *
 *   TC-MPS-001  MPS page loads after clicking the tab
 *   TC-MPS-002  Mps Plan sub-tab is clickable
 *   TC-MPS-003  Data import/validation table rows all show Success
 */
public class MpsTest extends BaseTest {

    private MpsPage mpsPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToPage() {
        page.navigate(ConfigReader.baseUrl());
        waitForAppLoad();
        mpsPage = new HomePage(page).goToMps();
        page.waitForTimeout(2000);
    }

    @Test(priority = 1, description = "TC-MPS-001: MPS page should load after clicking the MPS tab")
    public void mpsPageLoadsTest() {
        Assert.assertTrue(mpsPage.isMpsPageLoaded(), "MPS page did not load");
        logInfo("✅ MPS page loaded");
        captureScreenshot("TC-MPS-001_MpsPageLoaded");
    }

    @Test(priority = 2, dependsOnMethods = "mpsPageLoadsTest",
          description = "TC-MPS-002: Mps Plan sub-tab should be clickable")
    public void mpsPlanSubTabTest() {
        mpsPage.clickMpsPlan();
        page.waitForTimeout(2000);
        logInfo("✅ Mps Plan sub-tab clicked");
        captureScreenshot("TC-MPS-002_MpsPlanSubTab");
    }

    @Test(priority = 3, dependsOnMethods = "mpsPlanSubTabTest",
          description = "TC-MPS-003: Data import/validation table — all rows should show Success")
    public void dataImportValidationStatusTest() {
        int rowCount = mpsPage.getTableRowCount();
        logInfo("Data table rows: " + rowCount);
        if (rowCount == 0) { logInfo("No rows — skipping"); return; }
        List<String> failures = mpsPage.validateDataImportStatus();
        failures.forEach(f -> logInfo("❌ " + f));
        Assert.assertTrue(failures.isEmpty(),
                "Data import/validation failures:\n" + String.join("\n", failures));
        captureScreenshot("TC-MPS-003_DataImportStatus");
    }
}
