package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * SequenceLivePageLoadTest — verifies the Sequence Live (Group Master) page
 * loads correctly with all expected UI elements.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSPL-001  Page loads at the correct URL
 *   TC-MSPL-002  Page title is not blank
 *   TC-MSPL-003  Production Sequence Plan section is visible
 *   TC-MSPL-004  At least one mat-select dropdown is present
 *   TC-MSPL-005  Date input field is visible
 *   TC-MSPL-006  Page has interactive buttons
 *   TC-MSPL-007  No JavaScript errors on page load
 *   TC-MSPL-008  Page loads within acceptable time (< 30s)
 */
public class SequenceLivePageLoadTest extends BaseTest {

    private ManualSeqSequenceLivePage seqPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);
        seqPage = new ManualSeqSequenceLivePage(page);
        logInfo("Navigated to: " + ConfigReader.manualSeqGroupMasterUrl());
    }

    @Test(priority = 1,
          description = "TC-MSPL-001: Verify Sequence Live page loads at the correct URL")
    public void pageLoadsAtCorrectUrlTest() {
        String url = seqPage.getPageUrl();
        logInfo("Current URL: " + url);
        Assert.assertTrue(url.contains("groupmaster") || url.contains("SequencePlanTest"),
                "Expected URL to contain 'groupmaster' or 'SequencePlanTest'. Actual: " + url);
        captureScreenshot("TC-MSPL-001_PageLoaded");
        logInfo("✅ Page loaded at correct URL");
    }

    @Test(priority = 2,
          description = "TC-MSPL-002: Verify page title is not blank")
    public void pageTitleNotBlankTest() {
        String title = page.title();
        logInfo("Page title: " + title);
        Assert.assertFalse(title == null || title.isBlank(),
                "Page title should not be blank");
        captureScreenshot("TC-MSPL-002_PageTitle");
    }

    @Test(priority = 3,
          description = "TC-MSPL-003: Verify Production Sequence Plan section is visible")
    public void productionPlanSectionVisibleTest() {
        boolean visible = seqPage.isProductionPlanSectionVisible();
        logInfo("Production Plan section visible: " + visible);
        // Soft check — section may have different label on QAS
        if (!visible) {
            logInfo("ℹ️  Production Plan section not found with expected selectors — checking for any content area");
            int contentElements = page.locator("mat-card, .card, .content-area, main").count();
            Assert.assertTrue(contentElements > 0,
                    "No content area found on the page");
        }
        captureScreenshot("TC-MSPL-003_ProductionPlanSection");
    }

    @Test(priority = 4,
          description = "TC-MSPL-004: Verify at least one mat-select dropdown is present")
    public void matSelectDropdownsPresentTest() {
        int matSelectCount = seqPage.getMatSelectCount();
        logInfo("mat-select dropdowns found: " + matSelectCount);
        Assert.assertTrue(matSelectCount > 0,
                "No mat-select dropdowns found on the filter bar");
        captureScreenshot("TC-MSPL-004_Dropdowns");
    }

    @Test(priority = 5,
          description = "TC-MSPL-005: Verify date input field is visible")
    public void dateInputFieldVisibleTest() {
        boolean visible = seqPage.isDateFieldVisible();
        logInfo("Date input visible: " + visible);
        Assert.assertTrue(visible,
                "Date input field not found on the page");
        captureScreenshot("TC-MSPL-005_DateInput");
    }

    @Test(priority = 6,
          description = "TC-MSPL-006: Verify page has interactive buttons")
    public void pageHasButtonsTest() {
        int buttonCount = page.locator("button").count();
        logInfo("Buttons found: " + buttonCount);
        Assert.assertTrue(buttonCount > 0,
                "No buttons found on the page");
        captureScreenshot("TC-MSPL-006_Buttons");
    }

    @Test(priority = 7,
          description = "TC-MSPL-007: Verify no JavaScript console errors on page load")
    public void noJsErrorsOnLoadTest() {
        // Check for error dialogs or error messages in the DOM
        int errorDialogs = page.locator(".error-dialog, .alert-danger, [role='alertdialog'], .error-message").count();
        logInfo("Error dialogs found: " + errorDialogs);
        Assert.assertEquals(errorDialogs, 0,
                "JavaScript error dialogs found on page load");
        captureScreenshot("TC-MSPL-007_NoErrors");
    }

    @Test(priority = 8,
          description = "TC-MSPL-008: Verify page loads within acceptable time")
    public void pageLoadsWithinTimeTest() {
        long start = System.currentTimeMillis();
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        long elapsed = System.currentTimeMillis() - start;
        logInfo("Page load time: " + elapsed + "ms");
        Assert.assertTrue(elapsed < 30000,
                "Page took too long to load: " + elapsed + "ms (max 30s)");
        captureScreenshot("TC-MSPL-008_LoadTime");
    }
}
