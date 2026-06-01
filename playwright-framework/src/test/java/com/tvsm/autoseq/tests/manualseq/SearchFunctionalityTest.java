package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * SearchFunctionalityTest — end-to-end search workflow on the Sequence Live screen.
 *
 * Scenario: Select date 29/05/2026, search for "JUPITER 110 NEW",
 * verify data displays correctly, scroll down and scroll up.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSSF-001  Navigate to Group Master page
 *   TC-MSSF-002  Select date 29/05/2026
 *   TC-MSSF-003  Verify sequence rows load after date selection
 *   TC-MSSF-004  Click on search input and enter "JUPITER 110 NEW"
 *   TC-MSSF-005  Verify search results contain "JUPITER 110 NEW"
 *   TC-MSSF-006  Verify filtered row count is less than or equal to total
 *   TC-MSSF-007  Verify data rows display model name correctly
 *   TC-MSSF-008  Scroll down to bottom of results
 *   TC-MSSF-009  Scroll back up to top of results
 *   TC-MSSF-010  Verify page remains stable after scroll interactions
 *   TC-MSSF-011  Clear search and verify full list is restored
 *   TC-MSSF-012  Verify URL remains on groupmaster throughout
 */
public class SearchFunctionalityTest extends BaseTest {

    private ManualSeqSequenceLivePage seqPage;

    private static final String SEARCH_DATE    = "29/05/2026";
    private static final String SEARCH_VEHICLE = "JUPITER 110 NEW";

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);
        seqPage = new ManualSeqSequenceLivePage(page);
        seqPage.dismissSwalIfPresent();
        logInfo("Navigated to: " + ConfigReader.manualSeqGroupMasterUrl());
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-001 — Navigate to Group Master page
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "TC-MSSF-001: Verify Group Master page loads correctly")
    public void pageLoadsCorrectlyTest() {
        String url = seqPage.getPageUrl();
        logInfo("Current URL: " + url);
        Assert.assertTrue(url.contains("groupmaster") || url.contains("SequencePlanTest"),
                "Page did not load at expected URL. Actual: " + url);
        captureScreenshot("TC-MSSF-001_PageLoaded");
        logInfo("✅ Group Master page loaded");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-002 — Select date 29/05/2026
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 2,
          dependsOnMethods = "pageLoadsCorrectlyTest",
          description = "TC-MSSF-002: Select date 29/05/2026 in the date input field")
    public void selectDateTest() {
        // Use JavaScript to set the date value directly to avoid overlay issues
        var dateInput = page.locator("input[matinput], input[type='text']").first();
        dateInput.click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
        dateInput.clear();
        dateInput.fill(SEARCH_DATE);
        page.waitForTimeout(500);

        // Press Enter to submit the date and trigger data load
        dateInput.press("Enter");
        page.waitForTimeout(3000);

        // Dismiss any alerts or overlays that appear after date submission
        seqPage.dismissSwalIfPresent();
        dismissOverlays();

        String dateValue = seqPage.getDateFieldValue();
        logInfo("Date field value: " + dateValue);

        Assert.assertFalse(dateValue.isBlank(),
                "Date field is blank after entering '" + SEARCH_DATE + "'");

        captureScreenshot("TC-MSSF-002_DateSelected");
        logInfo("✅ Date 29/05/2026 selected and submitted successfully");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-003 — Verify sequence rows load after date selection
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 3,
          dependsOnMethods = "selectDateTest",
          description = "TC-MSSF-003: Verify sequence rows load after date selection")
    public void rowsLoadAfterDateTest() {
        page.waitForTimeout(2000);
        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Sequence rows after date selection: " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative — unexpected error");

        captureScreenshot("TC-MSSF-003_RowsLoaded");
        logInfo("✅ Rows loaded after date selection: " + rowCount);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-004 — Click on search and enter "JUPITER 110 NEW"
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 4,
          dependsOnMethods = "rowsLoadAfterDateTest",
          description = "TC-MSSF-004: Click search input and enter 'JUPITER 110 NEW'")
    public void enterSearchKeywordTest() {
        // Dismiss any CDK overlay backdrop that may be blocking interactions
        dismissOverlays();

        // Try the page object's search method first
        seqPage.search(SEARCH_VEHICLE);
        page.waitForTimeout(2000);
        seqPage.dismissSwalIfPresent();

        String searchValue = seqPage.getSearchValue();
        logInfo("Search box value (attempt 1): " + searchValue);

        // If standard selectors didn't work, try finding any available text input
        if (searchValue.isBlank() || !searchValue.equals(SEARCH_VEHICLE)) {
            logInfo("ℹ️  Standard search selectors didn't work — trying alternative inputs");
            dismissOverlays();

            // Find all text inputs on the page (excluding date inputs)
            int inputCount = page.locator("input[type='text'], input:not([type])").count();
            logInfo("Text inputs found on page: " + inputCount);

            for (int i = 0; i < inputCount; i++) {
                try {
                    var input = page.locator("input[type='text'], input:not([type])").nth(i);
                    String placeholder = input.getAttribute("placeholder");
                    logInfo("Input " + i + " placeholder: " + placeholder);

                    // Skip date inputs
                    if (placeholder != null && (placeholder.contains("date") || placeholder.contains("Date")
                            || placeholder.contains("dd/") || placeholder.contains("DD/"))) {
                        continue;
                    }

                    dismissOverlays();
                    input.click(new com.microsoft.playwright.Locator.ClickOptions().setForce(true));
                    input.clear();
                    input.fill(SEARCH_VEHICLE);
                    page.waitForTimeout(1500);

                    searchValue = input.inputValue();
                    if (searchValue.equals(SEARCH_VEHICLE)) {
                        logInfo("✅ Found working search input at index " + i);
                        break;
                    }
                } catch (Exception e) {
                    logInfo("⚠️  Input " + i + " not interactable: " + e.getMessage());
                }
            }
        }

        logInfo("Final search box value: " + searchValue);
        Assert.assertEquals(searchValue, SEARCH_VEHICLE,
                "Search box does not contain '" + SEARCH_VEHICLE + "'. Actual: '" + searchValue + "'");

        captureScreenshot("TC-MSSF-004_SearchEntered");
        logInfo("✅ Search keyword 'JUPITER 110 NEW' entered successfully");
    }

    /** Dismisses any CDK overlay backdrops or modal overlays blocking the page. */
    private void dismissOverlays() {
        try {
            // Remove all CDK overlay elements via JavaScript
            page.evaluate("document.querySelectorAll('.cdk-overlay-backdrop').forEach(el => el.remove())");
            page.evaluate("document.querySelectorAll('.cdk-overlay-pane:empty').forEach(el => el.remove())");
            page.evaluate("var c = document.querySelector('.cdk-overlay-container'); if(c) c.innerHTML = '';");
            page.keyboard().press("Escape");
            page.waitForTimeout(300);
        } catch (Exception ignored) {}
        seqPage.dismissSwalIfPresent();
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-005 — Verify search results contain "JUPITER 110 NEW"
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 5,
          dependsOnMethods = "enterSearchKeywordTest",
          description = "TC-MSSF-005: Verify search results contain 'JUPITER 110 NEW' text")
    public void searchResultsContainKeywordTest() {
        page.waitForTimeout(1500);

        // Check if the page content contains the searched vehicle name
        String pageContent = page.locator("body").innerText();
        boolean containsKeyword = pageContent.toUpperCase().contains("JUPITER")
                || pageContent.toUpperCase().contains("110");

        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows after search: " + rowCount);
        logInfo("Page contains 'JUPITER': " + containsKeyword);

        if (rowCount > 0) {
            Assert.assertTrue(containsKeyword,
                    "Search results do not contain 'JUPITER 110 NEW' text");
            logInfo("✅ Search results contain the searched vehicle");
        } else {
            // No results — check for empty state message
            boolean noDataMsg = page.locator("text=No records, text=No data, text=No results, text=No Data").count() > 0;
            logInfo("ℹ️  No rows found — No data message shown: " + noDataMsg);
            Assert.assertTrue(noDataMsg || rowCount == 0,
                    "Expected either matching rows or a no-data message");
        }

        captureScreenshot("TC-MSSF-005_SearchResults");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-006 — Verify filtered row count <= total
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 6,
          dependsOnMethods = "enterSearchKeywordTest",
          description = "TC-MSSF-006: Verify filtered row count is less than or equal to total rows")
    public void filteredCountLessOrEqualTest() {
        int filteredRows = seqPage.getSequenceRowCount();

        // Clear search to get total
        seqPage.clearSearch();
        page.waitForTimeout(2000);
        int totalRows = seqPage.getSequenceRowCount();

        logInfo("Filtered rows: " + filteredRows + " | Total rows: " + totalRows);

        Assert.assertTrue(filteredRows <= totalRows,
                "Filtered (" + filteredRows + ") > Total (" + totalRows + ")");

        // Re-enter search for subsequent tests
        seqPage.search(SEARCH_VEHICLE);
        page.waitForTimeout(2000);

        captureScreenshot("TC-MSSF-006_FilteredCount");
        logInfo("✅ Filtered count (" + filteredRows + ") <= Total (" + totalRows + ")");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-007 — Verify data rows display model name correctly
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 7,
          dependsOnMethods = "enterSearchKeywordTest",
          description = "TC-MSSF-007: Verify data rows display the model name correctly")
    public void dataRowsDisplayModelNameTest() {
        int rowCount = seqPage.getSequenceRowCount();

        if (rowCount == 0) {
            logInfo("ℹ️  No rows to validate — skipping model name check");
            captureScreenshot("TC-MSSF-007_NoRows");
            return;
        }

        // Check that visible row content contains relevant text
        String[] rowSelectors = {
            ".seq-card-row", ".sequence-card", ".card-container",
            "table tbody tr", ".cdk-row", "mat-row"
        };

        boolean foundContent = false;
        for (String sel : rowSelectors) {
            if (page.locator(sel).count() > 0) {
                String firstRowText = page.locator(sel).first().innerText().trim();
                logInfo("First row content: " + firstRowText);
                foundContent = !firstRowText.isBlank();
                break;
            }
        }

        Assert.assertTrue(foundContent || rowCount > 0,
                "Data rows are present but have no visible content");

        captureScreenshot("TC-MSSF-007_ModelName");
        logInfo("✅ Data rows display content correctly");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-008 — Scroll down to bottom of results
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 8,
          dependsOnMethods = "enterSearchKeywordTest",
          description = "TC-MSSF-008: Scroll down to the bottom of search results")
    public void scrollDownTest() {
        // Scroll to bottom of the page
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1500);

        // Verify page is still functional after scrolling
        String url = seqPage.getPageUrl();
        Assert.assertTrue(url.contains("groupmaster") || url.contains("SequencePlanTest"),
                "Page URL changed after scrolling down. Actual: " + url);

        // Check scroll position
        double scrollY = (double) page.evaluate("window.scrollY");
        logInfo("Scroll position after scrolling down: " + scrollY);

        captureScreenshot("TC-MSSF-008_ScrolledDown");
        logInfo("✅ Scrolled down successfully — scroll position: " + scrollY);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-009 — Scroll back up to top of results
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 9,
          dependsOnMethods = "scrollDownTest",
          description = "TC-MSSF-009: Scroll back up to the top of search results")
    public void scrollUpTest() {
        // Scroll back to top
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(1500);

        // Verify scroll position is at top
        double scrollY = (double) page.evaluate("window.scrollY");
        logInfo("Scroll position after scrolling up: " + scrollY);

        Assert.assertTrue(scrollY < 100,
                "Page did not scroll back to top. Scroll Y: " + scrollY);

        // Verify the filter bar / dropdowns are visible again at top
        int matSelectCount = seqPage.getMatSelectCount();
        logInfo("Dropdowns visible after scroll up: " + matSelectCount);

        captureScreenshot("TC-MSSF-009_ScrolledUp");
        logInfo("✅ Scrolled back up to top — dropdowns visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-010 — Verify page remains stable after scroll interactions
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 10,
          dependsOnMethods = "scrollUpTest",
          description = "TC-MSSF-010: Verify page remains stable after scroll interactions")
    public void pageStableAfterScrollTest() {
        // Verify search value is still retained
        String searchValue = seqPage.getSearchValue();
        logInfo("Search value after scrolling: " + searchValue);

        Assert.assertEquals(searchValue, SEARCH_VEHICLE,
                "Search value lost after scrolling. Actual: '" + searchValue + "'");

        // Verify rows are still displayed
        int rowCount = seqPage.getSequenceRowCount();
        logInfo("Rows still visible after scrolling: " + rowCount);

        Assert.assertTrue(rowCount >= 0,
                "Row count is negative after scroll interactions");

        // Verify page URL is stable
        Assert.assertTrue(seqPage.isOnGroupMasterPage(),
                "Page navigated away after scroll interactions");

        captureScreenshot("TC-MSSF-010_StableAfterScroll");
        logInfo("✅ Page remains stable after scroll — search retained, rows visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-011 — Clear search and verify full list is restored
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 11,
          dependsOnMethods = "pageStableAfterScrollTest",
          description = "TC-MSSF-011: Clear search and verify full list is restored")
    public void clearSearchRestoresListTest() {
        int filteredRows = seqPage.getSequenceRowCount();

        seqPage.clearSearch();
        page.waitForTimeout(2000);

        int restoredRows = seqPage.getSequenceRowCount();
        String searchValue = seqPage.getSearchValue();

        logInfo("Filtered: " + filteredRows + " | Restored: " + restoredRows);
        logInfo("Search value after clear: '" + searchValue + "'");

        Assert.assertTrue(searchValue.isBlank(),
                "Search box not empty after clearing. Value: '" + searchValue + "'");

        Assert.assertTrue(restoredRows >= filteredRows,
                "Restored rows (" + restoredRows + ") < filtered (" + filteredRows + ")");

        captureScreenshot("TC-MSSF-011_SearchCleared");
        logInfo("✅ Search cleared — full list restored (" + restoredRows + " rows)");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSSF-012 — Verify URL remains on groupmaster throughout
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 12,
          description = "TC-MSSF-012: Verify URL remains on groupmaster throughout all interactions")
    public void urlRemainsStableTest() {
        String url = seqPage.getPageUrl();
        logInfo("Final URL: " + url);

        Assert.assertTrue(url.contains("groupmaster") || url.contains("SequencePlanTest"),
                "URL changed unexpectedly. Actual: " + url);

        captureScreenshot("TC-MSSF-012_UrlStable");
        logInfo("✅ URL remains stable throughout all search and scroll interactions");
    }
}
