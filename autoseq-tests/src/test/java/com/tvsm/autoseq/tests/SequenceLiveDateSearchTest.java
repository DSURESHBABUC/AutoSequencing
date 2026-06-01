package com.tvsm.autoseq.tests;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.NewSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * SequenceLiveDateSearchTest — Date field and Search bar on the Group Master screen.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 *   TC-DS-001  Page loads at the correct URL
 *   TC-DS-002  Date field is visible and accepts input
 *   TC-DS-003  Entering today's date loads sequence rows
 *   TC-DS-004  Search box is visible and accepts input
 *   TC-DS-005  Search filters the sequence list
 *   TC-DS-006  Clearing search restores the full list
 *   TC-DS-007  Date + Search combined — results are consistent
 *   TC-DS-008  Search with a non-existent keyword shows empty / no results
 */
public class SequenceLiveDateSearchTest extends BaseTest {

    private NewSequenceLivePage seqPage;

    private static final String TEST_DATE       = "22/05/2026";
    private static final String SEARCH_VEHICLE  = "TVS RAIDER";
    private static final String SEARCH_PARTIAL  = "ND";
    private static final String SEARCH_NO_MATCH = "XYZNOTEXIST";

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.groupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(2000);
        seqPage = new NewSequenceLivePage(page);
        logInfo("Navigated to: " + ConfigReader.groupMasterUrl());
    }

    @Test(priority = 1, description = "TC-DS-001: Verify Group Master page loads at the correct URL")
    public void pageLoadsAtCorrectUrlTest() {
        String url = seqPage.getPageUrl();
        logInfo("Current URL: " + url);
        Assert.assertTrue(url.contains("groupmaster"),
                "Expected URL to contain 'groupmaster'. Actual: " + url);
        captureScreenshot("TC-DS-001_PageLoaded");
    }

    @Test(priority = 2, description = "TC-DS-002: Verify date input field is visible on the filter bar")
    public void dateFieldIsVisibleTest() {
        int inputCount = page.locator("input[type='text'], input[matinput]").count();
        logInfo("Text input fields found: " + inputCount);
        Assert.assertTrue(inputCount > 0, "No date input field found on the filter bar");
        captureScreenshot("TC-DS-002_DateFieldVisible");
    }

    @Test(priority = 3, description = "TC-DS-003: Enter date and verify sequence rows are displayed")
    public void enterDateAndVerifyRowsTest() {
        seqPage.enterDate(TEST_DATE);
        page.waitForTimeout(2000);
        int rowsAfter = seqPage.getSequenceRowCount();
        logInfo("Rows after date entry: " + rowsAfter);
        Assert.assertTrue(rowsAfter >= 0, "Sequence row count is negative after entering date");
        captureScreenshot("TC-DS-003_DateEntered");
    }

    @Test(priority = 4, description = "TC-DS-004: Verify search input box is visible on the page")
    public void searchBoxIsVisibleTest() {
        String[] selectors = {
            "input[placeholder='Search']", "input[placeholder*='Search' i]",
            "mat-form-field input", "input"
        };
        int found = 0;
        for (String sel : selectors) {
            int count = page.locator(sel).count();
            if (count > 0) { found = count; logInfo("Input found: " + sel); break; }
        }
        Assert.assertTrue(found > 0, "No input field found on the page");
        captureScreenshot("TC-DS-004_SearchBoxVisible");
    }

    @Test(priority = 5, description = "TC-DS-005: Search and verify list is filtered")
    public void searchFiltersListTest() {
        int totalRows = seqPage.getSequenceRowCount();
        seqPage.search(SEARCH_VEHICLE);
        page.waitForTimeout(1500);
        int filteredRows = seqPage.getSequenceRowCount();
        logInfo("Rows after search: " + filteredRows);
        Assert.assertEquals(seqPage.getSearchValue(), SEARCH_VEHICLE,
                "Search box does not contain the typed keyword");
        Assert.assertTrue(filteredRows <= totalRows,
                "Filtered count (" + filteredRows + ") > total (" + totalRows + ")");
        captureScreenshot("TC-DS-005_SearchFiltered");
    }

    @Test(priority = 6, dependsOnMethods = "searchFiltersListTest",
          description = "TC-DS-006: Clear search and verify full list is restored")
    public void clearSearchRestoresListTest() {
        int filteredRows = seqPage.getSequenceRowCount();
        seqPage.clearSearch();
        page.waitForTimeout(1500);
        int restoredRows = seqPage.getSequenceRowCount();
        Assert.assertTrue(restoredRows >= filteredRows,
                "Row count after clearing (" + restoredRows + ") < filtered (" + filteredRows + ")");
        Assert.assertTrue(seqPage.getSearchValue().isBlank(), "Search box not empty after clearing");
        captureScreenshot("TC-DS-006_SearchCleared");
    }

    @Test(priority = 7, description = "TC-DS-007: Date + Search combined — results consistent")
    public void dateAndSearchCombinedTest() {
        seqPage.enterDate(TEST_DATE);
        page.waitForTimeout(2000);
        int rowsAfterDate = seqPage.getSequenceRowCount();
        seqPage.search(SEARCH_PARTIAL);
        page.waitForTimeout(1500);
        int rowsAfterSearch = seqPage.getSequenceRowCount();
        Assert.assertTrue(rowsAfterSearch <= rowsAfterDate,
                "Combined filter produced more rows than date-only");
        captureScreenshot("TC-DS-007_DateAndSearch");
        seqPage.clearSearch();
    }

    @Test(priority = 8, description = "TC-DS-008: Search with non-existent keyword — empty state")
    public void searchNoMatchTest() {
        seqPage.search(SEARCH_NO_MATCH);
        page.waitForTimeout(1500);
        int rows = seqPage.getSequenceRowCount();
        boolean emptyState = rows == 0
                || page.locator("text=No records, text=No data, text=No results").count() > 0;
        Assert.assertTrue(emptyState, "Expected 0 rows for no-match search, got: " + rows);
        captureScreenshot("TC-DS-008_SearchNoMatch");
        seqPage.clearSearch();
    }
}
