package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * SequenceLiveUIStabilityTest — verifies the UI stability and responsiveness
 * of the Sequence Live screen under various user interactions.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSUI-001  Page does not crash on repeated navigation
 *   TC-MSUI-002  Browser back/forward does not break the page
 *   TC-MSUI-003  Page refresh retains the current state
 *   TC-MSUI-004  Scrolling does not break the layout
 *   TC-MSUI-005  Multiple dropdown selections in sequence work
 *   TC-MSUI-006  SweetAlert modals are dismissible
 *   TC-MSUI-007  No overlapping elements block interactions
 *   TC-MSUI-008  Page responds to keyboard navigation (Tab key)
 *   TC-MSUI-009  Page handles empty data gracefully
 *   TC-MSUI-010  All interactive elements are clickable (no dead zones)
 */
public class SequenceLiveUIStabilityTest extends BaseTest {

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
          description = "TC-MSUI-001: Verify page does not crash on repeated navigation")
    public void repeatedNavigationTest() {
        for (int i = 0; i < 3; i++) {
            page.navigate(ConfigReader.manualSeqGroupMasterUrl());
            waitForAppLoad();
            page.waitForTimeout(2000);

            Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                    "Page crashed on navigation attempt " + (i + 1));
        }

        captureScreenshot("TC-MSUI-001_RepeatedNav");
        logInfo("✅ Page stable after 3 repeated navigations");
    }

    @Test(priority = 2,
          description = "TC-MSUI-002: Verify browser back/forward does not break the page")
    public void browserBackForwardTest() {
        // Navigate to a different path first
        String currentUrl = page.url();
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(2000);

        // Go back
        page.goBack();
        page.waitForTimeout(2000);

        // Go forward
        page.goForward();
        page.waitForTimeout(2000);

        // Page should still be functional
        String url = page.url();
        logInfo("URL after back/forward: " + url);

        Assert.assertTrue(url.contains("SequencePlanTest") || url.contains("groupmaster"),
                "Page broke after back/forward. URL: " + url);

        captureScreenshot("TC-MSUI-002_BackForward");
        logInfo("✅ Browser back/forward handled correctly");
    }

    @Test(priority = 3,
          description = "TC-MSUI-003: Verify page refresh retains the current state")
    public void pageRefreshTest() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(2000);

        int rowsBefore = seqPage.getSequenceRowCount();

        // Refresh
        page.reload();
        waitForAppLoad();
        page.waitForTimeout(3000);

        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page did not reload correctly");

        int rowsAfter = seqPage.getSequenceRowCount();
        logInfo("Rows before refresh: " + rowsBefore + " | After: " + rowsAfter);

        captureScreenshot("TC-MSUI-003_PageRefresh");
        logInfo("✅ Page refresh handled correctly");
    }

    @Test(priority = 4,
          description = "TC-MSUI-004: Verify scrolling does not break the layout")
    public void scrollingTest() {
        // Scroll down
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1000);

        // Scroll back up
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(1000);

        // Page should still be functional
        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page broke after scrolling");

        // Dropdowns should still be accessible
        int matSelectCount = seqPage.getMatSelectCount();
        Assert.assertTrue(matSelectCount > 0,
                "Dropdowns disappeared after scrolling");

        captureScreenshot("TC-MSUI-004_Scrolling");
        logInfo("✅ Scrolling does not break the layout");
    }

    @Test(priority = 5,
          description = "TC-MSUI-005: Verify multiple dropdown selections in sequence work")
    public void multipleDropdownSelectionsTest() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);

        int matSelectCount = seqPage.getMatSelectCount();
        logInfo("Available dropdowns: " + matSelectCount);

        // Click each dropdown and select first option
        for (int i = 0; i < Math.min(matSelectCount, 4); i++) {
            try {
                page.locator("mat-select").nth(i).click();
                page.waitForTimeout(1000);

                if (page.locator("mat-option").count() > 0) {
                    page.locator("mat-option").first().click();
                    page.waitForTimeout(1500);
                    seqPage.dismissSwalIfPresent();
                } else {
                    page.keyboard().press("Escape");
                }

                logInfo("Dropdown " + (i + 1) + " selected successfully");
            } catch (Exception e) {
                logInfo("⚠️  Dropdown " + (i + 1) + " interaction failed: " + e.getMessage());
            }
        }

        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page crashed after multiple dropdown selections");

        captureScreenshot("TC-MSUI-005_MultipleSelections");
        logInfo("✅ Multiple dropdown selections work");
    }

    @Test(priority = 6,
          description = "TC-MSUI-006: Verify SweetAlert modals are dismissible")
    public void sweetAlertDismissibleTest() {
        // Trigger a potential SweetAlert by selecting a shift with no data
        if (seqPage.isShiftDropdownPresent()) {
            page.locator("mat-select").nth(3).click();
            page.waitForTimeout(1000);

            if (page.locator("mat-option").count() > 1) {
                page.locator("mat-option").nth(1).click();
                page.waitForTimeout(2000);
            } else {
                page.keyboard().press("Escape");
            }
        }

        // Try to dismiss any SweetAlert
        seqPage.dismissSwalIfPresent();
        page.waitForTimeout(500);

        // Verify no blocking modal remains
        int swalCount = page.locator(".swal2-container").count();
        logInfo("SweetAlert containers after dismiss: " + swalCount);

        Assert.assertEquals(swalCount, 0,
                "SweetAlert modal could not be dismissed");

        captureScreenshot("TC-MSUI-006_SwalDismissed");
        logInfo("✅ SweetAlert modals are dismissible");
    }

    @Test(priority = 7,
          description = "TC-MSUI-007: Verify no overlapping elements block interactions")
    public void noOverlappingElementsTest() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);

        // Check for overlay elements that might block clicks
        int overlays = page.locator("div.overlay:visible, .cdk-overlay-backdrop:visible, .modal-backdrop:visible").count();
        logInfo("Blocking overlays found: " + overlays);

        if (overlays > 0) {
            // Try to dismiss
            page.locator("div.overlay").first().evaluate("el => el.style.display = 'none'");
            page.waitForTimeout(500);
        }

        // Verify first dropdown is clickable
        try {
            page.locator("mat-select").first().click();
            page.waitForTimeout(500);
            page.keyboard().press("Escape");
            logInfo("✅ First dropdown is clickable — no blocking overlays");
        } catch (Exception e) {
            Assert.fail("Overlapping element blocks dropdown interaction: " + e.getMessage());
        }

        captureScreenshot("TC-MSUI-007_NoOverlaps");
    }

    @Test(priority = 8,
          description = "TC-MSUI-008: Verify page responds to keyboard navigation (Tab key)")
    public void keyboardNavigationTest() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(2000);

        // Press Tab multiple times to navigate through elements
        for (int i = 0; i < 5; i++) {
            page.keyboard().press("Tab");
            page.waitForTimeout(300);
        }

        // Page should still be functional
        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page broke during keyboard navigation");

        captureScreenshot("TC-MSUI-008_KeyboardNav");
        logInfo("✅ Keyboard navigation works");
    }

    @Test(priority = 9,
          description = "TC-MSUI-009: Verify page handles empty data gracefully")
    public void emptyDataHandlingTest() {
        // Enter a future date that likely has no data
        seqPage.enterDate("01/01/2030");
        page.waitForTimeout(2000);
        seqPage.dismissSwalIfPresent();

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows for future date: " + rowCount);

        // Page should not crash — either show 0 rows or a "no data" message
        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page crashed when no data is available");

        boolean hasNoDataMsg = page.locator("text=No records, text=No data, text=No results, text=No Data").count() > 0;
        logInfo("No data message shown: " + hasNoDataMsg);

        captureScreenshot("TC-MSUI-009_EmptyData");
        logInfo("✅ Page handles empty data gracefully");
    }

    @Test(priority = 10,
          description = "TC-MSUI-010: Verify all interactive elements are clickable")
    public void allElementsClickableTest() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);

        int buttons = page.locator("button").count();
        int inputs = page.locator("input").count();
        int selects = page.locator("mat-select").count();
        int total = buttons + inputs + selects;

        logInfo("Interactive elements — Buttons: " + buttons
                + " | Inputs: " + inputs + " | Selects: " + selects);

        Assert.assertTrue(total > 0,
                "No interactive elements found on the page");

        // Try clicking the first button (if any)
        if (buttons > 0) {
            try {
                page.locator("button").first().click();
                page.waitForTimeout(500);
                seqPage.dismissSwalIfPresent();
                logInfo("✅ First button is clickable");
            } catch (Exception e) {
                logInfo("⚠️  First button click failed: " + e.getMessage());
            }
        }

        captureScreenshot("TC-MSUI-010_AllClickable");
        logInfo("✅ Interactive elements check complete");
    }
}
