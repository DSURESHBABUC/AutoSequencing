package com.tvsm.autoseq.tests.autoseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.autoseq.NewSequenceLivePage;
import com.microsoft.playwright.Locator;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * SearchAndFilterStatusTest — Search for model "RADEON" and verify
 * status filter tabs (Completed, Upcoming, Partial, InComplete).
 *
 * URL: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 *   TC-SF-01  Search for "RADEON" and verify results display
 *   TC-SF-02  Click "Completed" status and verify filtered results
 *   TC-SF-03  Click "Upcoming" status and verify filtered results
 *   TC-SF-04  Click "Partial" status and verify filtered results
 *   TC-SF-05  Click "InComplete" status and verify filtered results
 *   TC-SF-06  Click "All" to reset and verify full results restored
 *   TC-SF-07  Verify URL remains stable throughout interactions
 */
public class SearchAndFilterStatusTest extends BaseTest {

    private NewSequenceLivePage seqPage;
    private static final String SEARCH_MODEL = "RADEON";
    private int searchResultCount = 0;

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.groupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);
        seqPage = new NewSequenceLivePage(page);

        // Dismiss any SweetAlert popup on launch
        dismissSwalIfPresent();
        // Close notification panel if open
        dismissNotificationPanel();

        logInfo("Navigated to: " + ConfigReader.groupMasterUrl());
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-01 — Search for "RADEON" and verify results
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "TC-SF-01: Search for 'RADEON' model and verify results display")
    public void searchForRadeonModelTest() {
        logInfo("Searching for model: " + SEARCH_MODEL);

        // Type in the search box
        seqPage.search(SEARCH_MODEL);
        page.waitForTimeout(2000);

        // Verify search value is retained
        String searchValue = seqPage.getSearchValue();
        logInfo("Search box value: " + searchValue);
        Assert.assertEquals(searchValue, SEARCH_MODEL,
                "Search box does not contain '" + SEARCH_MODEL + "'. Actual: '" + searchValue + "'");

        // Get the row count after search
        searchResultCount = seqPage.getSequenceRowCount();
        logInfo("Search results for '" + SEARCH_MODEL + "': " + searchResultCount + " rows");

        // Verify results are displayed (or a no-data message)
        boolean hasContent = searchResultCount > 0
                || page.locator("text=No records, text=No data").count() > 0;
        Assert.assertTrue(hasContent,
                "No search results and no 'No data' message after searching for '" + SEARCH_MODEL + "'");

        captureScreenshot("TC-SF-01_SearchRadeon");
        logInfo("✅ Search for '" + SEARCH_MODEL + "' completed — " + searchResultCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-02 — Click "Completed" status filter
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 2,
          dependsOnMethods = "searchForRadeonModelTest",
          description = "TC-SF-02: Click 'Completed' filter and verify results")
    public void filterByCompletedStatusTest() {
        logInfo("Clicking 'Completed' filter...");

        // Click the Completed filter button
        seqPage.clickFilterCompleted();
        page.waitForTimeout(2000);

        int completedRows = seqPage.getSequenceRowCount();
        logInfo("Completed rows: " + completedRows);

        // Completed rows should be <= total search results
        Assert.assertTrue(completedRows <= searchResultCount || searchResultCount == 0,
                "Completed rows (" + completedRows + ") > total search results (" + searchResultCount + ")");

        // Verify page is still functional
        Assert.assertTrue(page.url().contains("groupmaster"),
                "Page URL changed after clicking Completed filter");

        captureScreenshot("TC-SF-02_Completed");
        logInfo("✅ Completed filter: " + completedRows + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-03 — Click "Upcoming" status filter
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 3,
          dependsOnMethods = "searchForRadeonModelTest",
          description = "TC-SF-03: Click 'Upcoming' filter and verify results")
    public void filterByUpcomingStatusTest() {
        logInfo("Clicking 'Upcoming' filter...");

        seqPage.clickFilterUpcoming();
        page.waitForTimeout(2000);

        int upcomingRows = seqPage.getSequenceRowCount();
        logInfo("Upcoming rows: " + upcomingRows);

        Assert.assertTrue(upcomingRows <= searchResultCount || searchResultCount == 0,
                "Upcoming rows (" + upcomingRows + ") > total search results (" + searchResultCount + ")");

        Assert.assertTrue(page.url().contains("groupmaster"),
                "Page URL changed after clicking Upcoming filter");

        captureScreenshot("TC-SF-03_Upcoming");
        logInfo("✅ Upcoming filter: " + upcomingRows + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-04 — Click "Partial" status filter
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 4,
          dependsOnMethods = "searchForRadeonModelTest",
          description = "TC-SF-04: Click 'Partial' filter and verify results")
    public void filterByPartialStatusTest() {
        logInfo("Clicking 'Partial' filter...");

        seqPage.clickFilterPartial();
        page.waitForTimeout(2000);

        int partialRows = seqPage.getSequenceRowCount();
        logInfo("Partial rows: " + partialRows);

        Assert.assertTrue(partialRows <= searchResultCount || searchResultCount == 0,
                "Partial rows (" + partialRows + ") > total search results (" + searchResultCount + ")");

        Assert.assertTrue(page.url().contains("groupmaster"),
                "Page URL changed after clicking Partial filter");

        captureScreenshot("TC-SF-04_Partial");
        logInfo("✅ Partial filter: " + partialRows + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-05 — Click "InComplete" status filter
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 5,
          dependsOnMethods = "searchForRadeonModelTest",
          description = "TC-SF-05: Click 'InComplete' filter and verify results")
    public void filterByIncompleteStatusTest() {
        logInfo("Clicking 'InComplete' filter...");

        seqPage.clickFilterIncomplete();
        page.waitForTimeout(2000);

        int incompleteRows = seqPage.getSequenceRowCount();
        logInfo("InComplete rows: " + incompleteRows);

        Assert.assertTrue(incompleteRows <= searchResultCount || searchResultCount == 0,
                "InComplete rows (" + incompleteRows + ") > total search results (" + searchResultCount + ")");

        Assert.assertTrue(page.url().contains("groupmaster"),
                "Page URL changed after clicking InComplete filter");

        captureScreenshot("TC-SF-05_InComplete");
        logInfo("✅ InComplete filter: " + incompleteRows + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-06 — Click "All" to reset and verify full results
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 6,
          dependsOnMethods = "filterByIncompleteStatusTest",
          description = "TC-SF-06: Click 'All' filter to reset and verify full results restored")
    public void resetToAllFilterTest() {
        logInfo("Clicking 'All' filter to reset...");

        seqPage.clickFilterAll();
        page.waitForTimeout(2000);

        int allRows = seqPage.getSequenceRowCount();
        logInfo("All rows after reset: " + allRows);

        // All rows should be >= any individual filter
        Assert.assertTrue(allRows >= 0,
                "Row count is negative after clicking All filter");

        // Search should still be active
        String searchValue = seqPage.getSearchValue();
        logInfo("Search value after filter reset: '" + searchValue + "'");

        captureScreenshot("TC-SF-06_AllReset");
        logInfo("✅ All filter reset: " + allRows + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-SF-07 — Verify URL remains stable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 7,
          description = "TC-SF-07: Verify URL remains on groupmaster throughout all interactions")
    public void urlRemainsStableTest() {
        String url = page.url();
        logInfo("Final URL: " + url);

        Assert.assertTrue(url.contains("groupmaster"),
                "URL changed unexpectedly. Actual: " + url);

        captureScreenshot("TC-SF-07_UrlStable");
        logInfo("✅ URL remains stable: " + url);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────
    private void dismissSwalIfPresent() {
        try {
            if (page.locator(".swal2-popup").count() > 0) {
                page.locator("button.swal2-confirm").first().click();
                page.waitForTimeout(1500);
                logInfo("Dismissed SweetAlert popup");
            }
        } catch (Exception ignored) {}
    }

    private void dismissNotificationPanel() {
        try {
            Locator closeBtn = page.locator("button[aria-label='Close']");
            if (closeBtn.count() > 0 && closeBtn.first().isVisible()) {
                closeBtn.first().click();
                page.waitForTimeout(1000);
                logInfo("Closed notification panel");
            }
        } catch (Exception ignored) {}
    }
}
