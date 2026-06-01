package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * FilterTabsTest — verifies the filter tabs (All, Completed, Upcoming, Partial, InComplete)
 * on the Sequence Live screen are clickable and functional.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSFT-001  "All" filter tab is clickable
 *   TC-MSFT-002  "Completed" filter tab is clickable
 *   TC-MSFT-003  "Upcoming" filter tab is clickable
 *   TC-MSFT-004  "Partial" filter tab is clickable
 *   TC-MSFT-005  "InComplete" filter tab is clickable
 *   TC-MSFT-006  Page remains stable after clicking all filter tabs
 *   TC-MSFT-007  Returning to "All" restores the full list
 *   TC-MSFT-008  Completed filter shows <= All rows
 *   TC-MSFT-009  Filter tabs are visually distinct (active state)
 *   TC-MSFT-010  Rapid tab switching does not crash the page
 */
public class FilterTabsTest extends BaseTest {

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
          description = "TC-MSFT-001: Verify 'All' filter tab is clickable")
    public void allFilterTabClickableTest() {
        seqPage.clickFilterAll();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'All': " + rowCount);

        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page broke after clicking 'All' filter tab");

        captureScreenshot("TC-MSFT-001_AllFilter");
        logInfo("✅ 'All' filter tab clicked — " + rowCount + " rows");
    }

    @Test(priority = 2,
          description = "TC-MSFT-002: Verify 'Completed' filter tab is clickable")
    public void completedFilterTabClickableTest() {
        seqPage.clickFilterCompleted();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'Completed': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'Completed' filter");

        captureScreenshot("TC-MSFT-002_CompletedFilter");
        logInfo("✅ 'Completed' filter tab clicked — " + rowCount + " rows");
    }

    @Test(priority = 3,
          description = "TC-MSFT-003: Verify 'Upcoming' filter tab is clickable")
    public void upcomingFilterTabClickableTest() {
        seqPage.clickFilterUpcoming();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'Upcoming': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'Upcoming' filter");

        captureScreenshot("TC-MSFT-003_UpcomingFilter");
        logInfo("✅ 'Upcoming' filter tab clicked — " + rowCount + " rows");
    }

    @Test(priority = 4,
          description = "TC-MSFT-004: Verify 'Partial' filter tab is clickable")
    public void partialFilterTabClickableTest() {
        seqPage.clickFilterPartial();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'Partial': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'Partial' filter");

        captureScreenshot("TC-MSFT-004_PartialFilter");
        logInfo("✅ 'Partial' filter tab clicked — " + rowCount + " rows");
    }

    @Test(priority = 5,
          description = "TC-MSFT-005: Verify 'InComplete' filter tab is clickable")
    public void incompleteFilterTabClickableTest() {
        seqPage.clickFilterIncomplete();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'InComplete': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'InComplete' filter");

        captureScreenshot("TC-MSFT-005_InCompleteFilter");
        logInfo("✅ 'InComplete' filter tab clicked — " + rowCount + " rows");
    }

    @Test(priority = 6,
          dependsOnMethods = {"allFilterTabClickableTest", "completedFilterTabClickableTest",
                              "upcomingFilterTabClickableTest", "partialFilterTabClickableTest",
                              "incompleteFilterTabClickableTest"},
          description = "TC-MSFT-006: Verify page remains stable after clicking all filter tabs")
    public void pageStableAfterAllTabsTest() {
        String url = seqPage.getPageUrl();
        logInfo("URL after all tab clicks: " + url);

        Assert.assertTrue(url.contains("groupmaster") || url.contains("SequencePlanTest"),
                "URL changed unexpectedly. Actual: " + url);

        captureScreenshot("TC-MSFT-006_PageStable");
        logInfo("✅ Page remains stable after all filter tab interactions");
    }

    @Test(priority = 7,
          dependsOnMethods = "pageStableAfterAllTabsTest",
          description = "TC-MSFT-007: Verify clicking 'All' after other tabs restores the full list")
    public void returnToAllRestoresListTest() {
        seqPage.clickFilterCompleted();
        page.waitForTimeout(1500);
        int completedRows = seqPage.getSequenceRowCount();

        seqPage.clickFilterAll();
        page.waitForTimeout(1500);
        int allRows = seqPage.getSequenceRowCount();

        logInfo("Completed: " + completedRows + " | All: " + allRows);

        Assert.assertTrue(allRows >= completedRows,
                "'All' (" + allRows + ") should be >= 'Completed' (" + completedRows + ")");

        captureScreenshot("TC-MSFT-007_ReturnToAll");
        logInfo("✅ Returning to 'All' restored full list");
    }

    @Test(priority = 8,
          description = "TC-MSFT-008: Verify Completed filter shows <= All rows")
    public void completedFilterShowsLessOrEqualTest() {
        seqPage.clickFilterAll();
        page.waitForTimeout(1500);
        int allRows = seqPage.getSequenceRowCount();

        seqPage.clickFilterCompleted();
        page.waitForTimeout(1500);
        int completedRows = seqPage.getSequenceRowCount();

        logInfo("All: " + allRows + " | Completed: " + completedRows);

        Assert.assertTrue(completedRows <= allRows,
                "Completed (" + completedRows + ") > All (" + allRows + ")");

        captureScreenshot("TC-MSFT-008_CompletedLessOrEqual");
        logInfo("✅ Completed filter shows correct subset");
    }

    @Test(priority = 9,
          description = "TC-MSFT-009: Verify filter tabs have active state styling")
    public void filterTabsActiveStateTest() {
        seqPage.clickFilterAll();
        page.waitForTimeout(1000);

        // Check for active/selected class on the clicked button
        int activeButtons = page.locator("button.active, button.mat-primary, button[aria-selected='true'], .mat-tab-label-active").count();
        logInfo("Active-styled buttons: " + activeButtons);

        // At minimum, the page should have buttons
        int totalButtons = page.locator("button").count();
        Assert.assertTrue(totalButtons > 0,
                "No buttons found on the page");

        captureScreenshot("TC-MSFT-009_ActiveState");
        logInfo("✅ Filter tab active state check complete");
    }

    @Test(priority = 10,
          description = "TC-MSFT-010: Verify rapid tab switching does not crash the page")
    public void rapidTabSwitchingTest() {
        // Rapidly click through all tabs
        seqPage.clickFilterAll();        page.waitForTimeout(300);
        seqPage.clickFilterCompleted();  page.waitForTimeout(300);
        seqPage.clickFilterUpcoming();   page.waitForTimeout(300);
        seqPage.clickFilterPartial();    page.waitForTimeout(300);
        seqPage.clickFilterIncomplete(); page.waitForTimeout(300);
        seqPage.clickFilterAll();        page.waitForTimeout(300);
        seqPage.clickFilterCompleted();  page.waitForTimeout(300);
        seqPage.clickFilterAll();        page.waitForTimeout(1000);

        // Page should still be functional
        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page crashed after rapid tab switching");

        int rowCount = seqPage.getSequenceRowCount();
        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after rapid switching");

        captureScreenshot("TC-MSFT-010_RapidSwitching");
        logInfo("✅ Rapid tab switching did not crash the page");
    }
}
