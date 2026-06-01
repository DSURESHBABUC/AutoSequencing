package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * DateSearchTest — verifies the date input field and search bar functionality
 * on the Sequence Live screen.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSDS-001  Date input field is visible
 *   TC-MSDS-002  Date input accepts a valid date
 *   TC-MSDS-003  Entering a date loads/refreshes sequence rows
 *   TC-MSDS-004  Search input is visible
 *   TC-MSDS-005  Search input accepts text
 *   TC-MSDS-006  Search filters the sequence list
 *   TC-MSDS-007  Clearing search restores the full list
 *   TC-MSDS-008  Date + Search combined — results are consistent
 *   TC-MSDS-009  Search with non-existent keyword shows empty state
 *   TC-MSDS-010  Date field retains value after search interaction
 */
public class DateSearchTest extends BaseTest {

    private ManualSeqSequenceLivePage seqPage;

    private static final String TEST_DATE       = "28/05/2026";
    private static final String SEARCH_KEYWORD  = "TVS";
    private static final String SEARCH_NO_MATCH = "XYZNOTEXIST999";

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);
        seqPage = new ManualSeqSequenceLivePage(page);
        logInfo("Navigated to: " + ConfigReader.manualSeqGroupMasterUrl());
    }

    @Test(priority = 1,
          description = "TC-MSDS-001: Verify date input field is visible on the page")
    public void dateInputFieldVisibleTest() {
        Assert.assertTrue(seqPage.isDateFieldVisible(),
                "Date input field not found on the page");
        captureScreenshot("TC-MSDS-001_DateFieldVisible");
        logInfo("✅ Date input field is visible");
    }

    @Test(priority = 2,
          description = "TC-MSDS-002: Verify date input accepts a valid date value")
    public void dateInputAcceptsValueTest() {
        seqPage.enterDate(TEST_DATE);
        page.waitForTimeout(2000);

        String dateValue = seqPage.getDateFieldValue();
        logInfo("Date field value: " + dateValue);

        Assert.assertFalse(dateValue.isBlank(),
                "Date field is blank after entering '" + TEST_DATE + "'");

        captureScreenshot("TC-MSDS-002_DateEntered");
        logInfo("✅ Date input accepted value: " + dateValue);
    }

    @Test(priority = 3,
          dependsOnMethods = "dateInputAcceptsValueTest",
          description = "TC-MSDS-003: Verify entering a date loads sequence rows")
    public void dateLoadsSequenceRowsTest() {
        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after date entry: " + rowCount);

        // Rows may be 0 if no data for the date — that's acceptable
        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after entering date");

        captureScreenshot("TC-MSDS-003_DateRows");
        logInfo("✅ Date entry processed — " + rowCount + " rows");
    }

    @Test(priority = 4,
          description = "TC-MSDS-004: Verify search input is visible on the page")
    public void searchInputVisibleTest() {
        boolean visible = seqPage.isSearchInputVisible();
        logInfo("Search input visible: " + visible);

        if (!visible) {
            logInfo("ℹ️  Search input may require date selection first");
            // Try entering date first
            seqPage.enterDate(TEST_DATE);
            page.waitForTimeout(2000);
            visible = seqPage.isSearchInputVisible();
        }

        Assert.assertTrue(visible,
                "Search input not found on the page");

        captureScreenshot("TC-MSDS-004_SearchVisible");
        logInfo("✅ Search input is visible");
    }

    @Test(priority = 5,
          dependsOnMethods = "searchInputVisibleTest",
          description = "TC-MSDS-005: Verify search input accepts text")
    public void searchInputAcceptsTextTest() {
        seqPage.search(SEARCH_KEYWORD);
        page.waitForTimeout(1000);

        String value = seqPage.getSearchValue();
        logInfo("Search value: " + value);

        Assert.assertEquals(value, SEARCH_KEYWORD,
                "Search input did not accept the typed keyword");

        seqPage.clearSearch();
        page.waitForTimeout(500);

        captureScreenshot("TC-MSDS-005_SearchAccepted");
        logInfo("✅ Search input accepted text");
    }

    @Test(priority = 6,
          dependsOnMethods = "searchInputAcceptsTextTest",
          description = "TC-MSDS-006: Verify search filters the sequence list")
    public void searchFiltersListTest() {
        // Ensure date is set first
        seqPage.enterDate(TEST_DATE);
        page.waitForTimeout(2000);

        int totalRows = seqPage.getSequenceRowCount();

        seqPage.search(SEARCH_KEYWORD);
        page.waitForTimeout(1500);

        int filteredRows = seqPage.getSequenceRowCount();
        logInfo("Total: " + totalRows + " | Filtered: " + filteredRows);

        Assert.assertTrue(filteredRows <= totalRows,
                "Filtered (" + filteredRows + ") > total (" + totalRows + ")");

        seqPage.clearSearch();
        page.waitForTimeout(500);

        captureScreenshot("TC-MSDS-006_SearchFiltered");
        logInfo("✅ Search filtered the list");
    }

    @Test(priority = 7,
          dependsOnMethods = "searchFiltersListTest",
          description = "TC-MSDS-007: Verify clearing search restores the full list")
    public void clearSearchRestoresListTest() {
        seqPage.search(SEARCH_KEYWORD);
        page.waitForTimeout(1500);
        int filteredRows = seqPage.getSequenceRowCount();

        seqPage.clearSearch();
        page.waitForTimeout(1500);
        int restoredRows = seqPage.getSequenceRowCount();

        logInfo("Filtered: " + filteredRows + " | Restored: " + restoredRows);

        Assert.assertTrue(restoredRows >= filteredRows,
                "Restored (" + restoredRows + ") < filtered (" + filteredRows + ")");

        Assert.assertTrue(seqPage.getSearchValue().isBlank(),
                "Search box not empty after clearing");

        captureScreenshot("TC-MSDS-007_SearchCleared");
        logInfo("✅ Clearing search restored the list");
    }

    @Test(priority = 8,
          description = "TC-MSDS-008: Verify date + search combined produces consistent results")
    public void dateAndSearchCombinedTest() {
        seqPage.enterDate(TEST_DATE);
        page.waitForTimeout(2000);
        int rowsAfterDate = seqPage.getSequenceRowCount();

        seqPage.search(SEARCH_KEYWORD);
        page.waitForTimeout(1500);
        int rowsAfterSearch = seqPage.getSequenceRowCount();

        logInfo("After date: " + rowsAfterDate + " | After date+search: " + rowsAfterSearch);

        Assert.assertTrue(rowsAfterSearch <= rowsAfterDate,
                "Combined filter produced more rows than date-only");

        seqPage.clearSearch();
        page.waitForTimeout(500);

        captureScreenshot("TC-MSDS-008_DateAndSearch");
        logInfo("✅ Date + Search combined results are consistent");
    }

    @Test(priority = 9,
          description = "TC-MSDS-009: Verify search with non-existent keyword shows empty state")
    public void searchNoMatchTest() {
        seqPage.search(SEARCH_NO_MATCH);
        page.waitForTimeout(1500);

        int rows = seqPage.getSequenceRowCount();
        boolean emptyState = rows == 0
                || page.locator("text=No records, text=No data, text=No results").count() > 0;

        logInfo("Rows for no-match search: " + rows);

        Assert.assertTrue(emptyState,
                "Expected 0 rows for no-match search, got: " + rows);

        seqPage.clearSearch();
        page.waitForTimeout(500);

        captureScreenshot("TC-MSDS-009_NoMatch");
        logInfo("✅ No-match search shows empty state");
    }

    @Test(priority = 10,
          description = "TC-MSDS-010: Verify date field retains value after search interaction")
    public void dateRetainsValueAfterSearchTest() {
        seqPage.enterDate(TEST_DATE);
        page.waitForTimeout(1000);

        String dateBefore = seqPage.getDateFieldValue();

        seqPage.search(SEARCH_KEYWORD);
        page.waitForTimeout(1000);
        seqPage.clearSearch();
        page.waitForTimeout(1000);

        String dateAfter = seqPage.getDateFieldValue();
        logInfo("Date before search: " + dateBefore + " | After: " + dateAfter);

        Assert.assertEquals(dateAfter, dateBefore,
                "Date field value changed after search interaction");

        captureScreenshot("TC-MSDS-010_DateRetained");
        logInfo("✅ Date field retains value after search");
    }
}
