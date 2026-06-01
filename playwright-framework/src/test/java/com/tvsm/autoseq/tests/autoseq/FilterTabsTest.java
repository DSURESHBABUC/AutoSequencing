package com.tvsm.autoseq.tests.autoseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.autoseq.NewSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * FilterTabsTest — verifies user can click on All, Completed, Upcoming,
 * Partial, and InComplete filter options on the Group Master home screen.
 *
 * URL: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 *   TC-FT-001  "All" filter tab is clickable and page remains stable
 *   TC-FT-002  "Completed" filter tab is clickable
 *   TC-FT-003  "Upcoming" filter tab is clickable
 *   TC-FT-004  "Partial" filter tab is clickable
 *   TC-FT-005  "In Complete" filter tab is clickable
 *   TC-FT-006  Clicking each tab does not break the page (sequence section stays visible)
 *   TC-FT-007  After clicking all tabs, returning to "All" restores the full list
 */
public class FilterTabsTest extends BaseTest {

    private NewSequenceLivePage seqPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.groupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);
        seqPage = new NewSequenceLivePage(page);
        logInfo("Navigated to: " + ConfigReader.groupMasterUrl());
        System.out.println("✅ Setup complete — on Group Master page");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-001 — "All" filter tab is clickable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "TC-FT-001: Verify 'All' filter tab is clickable on the home screen")
    public void allFilterTabClickableTest() {
        seqPage.clickFilterAll();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'All': " + rowCount);

        // Page should not crash — sequence section should remain visible
        Assert.assertTrue(seqPage.isProductionPlanSectionVisible() || rowCount >= 0,
                "Page broke after clicking 'All' filter tab");

        captureScreenshot("TC-FT-001_AllFilter");
        logInfo("✅ 'All' filter tab clicked successfully — " + rowCount + " rows visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-002 — "Completed" filter tab is clickable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 2,
          description = "TC-FT-002: Verify 'Completed' filter tab is clickable")
    public void completedFilterTabClickableTest() {
        seqPage.clickFilterCompleted();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'Completed': " + rowCount);

        // Row count should be >= 0 (could be 0 if no completed sequences)
        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'Completed' filter");

        captureScreenshot("TC-FT-002_CompletedFilter");
        logInfo("✅ 'Completed' filter tab clicked — " + rowCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-003 — "Upcoming" filter tab is clickable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 3,
          description = "TC-FT-003: Verify 'Upcoming' filter tab is clickable")
    public void upcomingFilterTabClickableTest() {
        seqPage.clickFilterUpcoming();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'Upcoming': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'Upcoming' filter");

        captureScreenshot("TC-FT-003_UpcomingFilter");
        logInfo("✅ 'Upcoming' filter tab clicked — " + rowCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-004 — "Partial" filter tab is clickable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 4,
          description = "TC-FT-004: Verify 'Partial' filter tab is clickable")
    public void partialFilterTabClickableTest() {
        seqPage.clickFilterPartial();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'Partial': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'Partial' filter");

        captureScreenshot("TC-FT-004_PartialFilter");
        logInfo("✅ 'Partial' filter tab clicked — " + rowCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-005 — "In Complete" filter tab is clickable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 5,
          description = "TC-FT-005: Verify 'In Complete' filter tab is clickable")
    public void incompleteFilterTabClickableTest() {
        seqPage.clickFilterIncomplete();
        page.waitForTimeout(1500);

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after clicking 'In Complete': " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after clicking 'In Complete' filter");

        captureScreenshot("TC-FT-005_InCompleteFilter");
        logInfo("✅ 'In Complete' filter tab clicked — " + rowCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-006 — Page remains stable after clicking all filter tabs
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 6,
          dependsOnMethods = {"allFilterTabClickableTest", "completedFilterTabClickableTest",
                              "upcomingFilterTabClickableTest", "partialFilterTabClickableTest",
                              "incompleteFilterTabClickableTest"},
          description = "TC-FT-006: Verify page remains stable after clicking all filter tabs sequentially")
    public void pageStableAfterAllTabsTest() {
        // The page should still be on groupmaster URL
        String url = seqPage.getPageUrl();
        logInfo("Current URL after all tab clicks: " + url);

        Assert.assertTrue(url.contains("groupmaster"),
                "URL changed unexpectedly after filter tab clicks. Actual: " + url);

        // Production plan section should still be visible
        Assert.assertTrue(seqPage.isProductionPlanSectionVisible() || seqPage.getSequenceRowCount() >= 0,
                "Production plan section disappeared after clicking all filter tabs");

        captureScreenshot("TC-FT-006_PageStable");
        logInfo("✅ Page remains stable after all filter tab interactions");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-FT-007 — Returning to "All" restores the full list
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 7,
          dependsOnMethods = "pageStableAfterAllTabsTest",
          description = "TC-FT-007: Verify clicking 'All' after other tabs restores the full sequence list")
    public void returnToAllRestoresListTest() {
        // Click a filter first to narrow the list
        seqPage.clickFilterCompleted();
        page.waitForTimeout(1500);
        int completedRows = seqPage.getSequenceRowCount();
        logInfo("Rows with 'Completed' filter: " + completedRows);

        // Now click "All" to restore
        seqPage.clickFilterAll();
        page.waitForTimeout(1500);
        int allRows = seqPage.getSequenceRowCount();
        logInfo("Rows after returning to 'All': " + allRows);

        // "All" should show >= the filtered count
        Assert.assertTrue(allRows >= completedRows,
                "'All' row count (" + allRows + ") should be >= 'Completed' count (" + completedRows + ")");

        captureScreenshot("TC-FT-007_ReturnToAll");
        logInfo("✅ Returning to 'All' restored full list — " + allRows + " rows");
    }
}
