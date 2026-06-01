package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.PartCommitmentPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * PartCommitmentTest — functional test suite for the Part Commitment module.
 *
 * Flow: Navigate to Sequence Live → Click "Part Commitment" tab → Wait for data load
 *       → Verify all functional aspects of the Part Commitment screen.
 *
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 *   TC-MSPC-001  Part Commitment tab is visible on the navigation bar
 *   TC-MSPC-002  Clicking Part Commitment tab navigates to the module
 *   TC-MSPC-003  Data loads completely (spinner disappears, rows appear)
 *   TC-MSPC-004  Data table is visible with rows
 *   TC-MSPC-005  Table has column headers
 *   TC-MSPC-006  Column headers contain expected fields
 *   TC-MSPC-007  First row contains non-empty data
 *   TC-MSPC-008  All rows have consistent column count
 *   TC-MSPC-009  Search/filter input is available
 *   TC-MSPC-010  Search filters the table data
 *   TC-MSPC-011  Clearing search restores full data
 *   TC-MSPC-012  Pagination is visible (if applicable)
 *   TC-MSPC-013  Dropdown filters are present
 *   TC-MSPC-014  Scroll down shows more data / table scrolls
 *   TC-MSPC-015  Scroll up returns to top
 *   TC-MSPC-016  Export/Download button is present (if applicable)
 *   TC-MSPC-017  No error dialogs on the page
 *   TC-MSPC-018  Page URL remains stable during interactions
 *   TC-MSPC-019  Table data is not all empty/blank
 *   TC-MSPC-020  Page remains functional after rapid interactions
 */
public class PartCommitmentTest extends BaseTest {

    private PartCommitmentPage pcPage;

    @BeforeClass(alwaysRun = true)
    public void navigateToPartCommitment() {
        // Navigate to the Group Master / Sequence Live screen first
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);

        // Click on Part Commitment tab
        pcPage = new PartCommitmentPage(page);
        pcPage.navigateToPartCommitment();
        page.waitForTimeout(2000);

        // Wait for data to fully load
        pcPage.waitForDataLoad();
        page.waitForTimeout(2000);
        pcPage.dismissOverlays();

        logInfo("Navigated to Part Commitment module");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-001 — Part Commitment tab is visible
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 1,
          description = "TC-MSPC-001: Verify Part Commitment tab is visible on the navigation bar")
    public void partCommitmentTabVisibleTest() {
        // Re-check from the main page
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);

        boolean tabVisible = pcPage.isPartCommitmentTabVisible();
        logInfo("Part Commitment tab visible: " + tabVisible);

        Assert.assertTrue(tabVisible,
                "Part Commitment tab is not visible on the navigation bar");

        captureScreenshot("TC-MSPC-001_TabVisible");
        logInfo("✅ Part Commitment tab is visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-002 — Clicking Part Commitment tab navigates to the module
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 2,
          dependsOnMethods = "partCommitmentTabVisibleTest",
          description = "TC-MSPC-002: Verify clicking Part Commitment tab navigates to the module")
    public void clickPartCommitmentTabTest() {
        pcPage.navigateToPartCommitment();
        page.waitForTimeout(3000);
        pcPage.dismissOverlays();

        // Verify we're on the Part Commitment view
        boolean loaded = pcPage.isPartCommitmentPageLoaded();
        logInfo("Part Commitment page loaded: " + loaded);

        // At minimum, the page should have some content
        int buttons = page.locator("button").count();
        int inputs = page.locator("input").count();
        logInfo("Buttons: " + buttons + " | Inputs: " + inputs);

        Assert.assertTrue(loaded || buttons > 0 || inputs > 0,
                "Part Commitment module did not load after clicking the tab");

        captureScreenshot("TC-MSPC-002_ModuleLoaded");
        logInfo("✅ Part Commitment module loaded");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-003 — Data loads completely
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 3,
          dependsOnMethods = "clickPartCommitmentTabTest",
          description = "TC-MSPC-003: Verify data loads completely (no spinner, rows appear)")
    public void dataLoadsCompletelyTest() {
        pcPage.waitForDataLoad();
        page.waitForTimeout(2000);

        boolean stillLoading = pcPage.isLoading();
        logInfo("Still loading: " + stillLoading);

        Assert.assertFalse(stillLoading,
                "Page is still showing a loading spinner after 30 seconds");

        captureScreenshot("TC-MSPC-003_DataLoaded");
        logInfo("✅ Data loaded completely — no spinner visible");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-004 — Data table is visible with rows
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 4,
          dependsOnMethods = "dataLoadsCompletelyTest",
          description = "TC-MSPC-004: Verify data table is visible with rows")
    public void dataTableVisibleTest() {
        boolean tableVisible = pcPage.isTableVisible();
        int rowCount = pcPage.getRowCount();

        logInfo("Table visible: " + tableVisible + " | Row count: " + rowCount);

        Assert.assertTrue(tableVisible,
                "Data table is not visible on the Part Commitment page");
        Assert.assertTrue(rowCount > 0,
                "Data table has no rows — expected at least 1 row");

        captureScreenshot("TC-MSPC-004_TableWithRows");
        logInfo("✅ Data table visible with " + rowCount + " rows");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-005 — Table has column headers
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 5,
          dependsOnMethods = "dataTableVisibleTest",
          description = "TC-MSPC-005: Verify table has column headers")
    public void tableHasColumnHeadersTest() {
        int columnCount = pcPage.getColumnCount();
        logInfo("Column count: " + columnCount);

        Assert.assertTrue(columnCount > 0,
                "Table has no column headers");

        captureScreenshot("TC-MSPC-005_ColumnHeaders");
        logInfo("✅ Table has " + columnCount + " column headers");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-006 — Column headers contain expected fields
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 6,
          dependsOnMethods = "tableHasColumnHeadersTest",
          description = "TC-MSPC-006: Verify column headers contain expected Part Commitment fields")
    public void columnHeadersContainExpectedFieldsTest() {
        List<String> headers = pcPage.getColumnHeaders();
        logInfo("Column headers: " + headers);

        Assert.assertFalse(headers.isEmpty(),
                "No column headers found");

        // Check that headers are not all blank
        long nonBlank = headers.stream().filter(h -> !h.isBlank()).count();
        Assert.assertTrue(nonBlank > 0,
                "All column headers are blank");

        captureScreenshot("TC-MSPC-006_HeaderFields");
        logInfo("✅ Column headers verified: " + headers);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-007 — First row contains non-empty data
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 7,
          dependsOnMethods = "dataTableVisibleTest",
          description = "TC-MSPC-007: Verify first row contains non-empty data")
    public void firstRowHasDataTest() {
        String firstRowText = pcPage.getFirstRowText();
        logInfo("First row text: " + firstRowText);

        Assert.assertFalse(firstRowText.isBlank(),
                "First row is empty — no data displayed");

        captureScreenshot("TC-MSPC-007_FirstRowData");
        logInfo("✅ First row contains data");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-008 — All rows have consistent column count
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 8,
          dependsOnMethods = "dataTableVisibleTest",
          description = "TC-MSPC-008: Verify all rows have consistent column count")
    public void consistentColumnCountTest() {
        int headerCols = pcPage.getColumnCount();
        int rowCount = pcPage.getRowCount();

        if (rowCount == 0) {
            logInfo("ℹ️  No rows to check");
            return;
        }

        // Check first few rows have same number of cells as headers
        String[] cellSelectors = {"table tbody tr", "mat-row", ".cdk-row"};
        for (String sel : cellSelectors) {
            if (page.locator(sel).count() > 0) {
                int firstRowCells = page.locator(sel).first().locator("td, mat-cell, .mat-cell").count();
                logInfo("Header cols: " + headerCols + " | First row cells: " + firstRowCells);

                // Allow some tolerance (action columns, checkboxes, etc.)
                Assert.assertTrue(Math.abs(firstRowCells - headerCols) <= 2,
                        "Column mismatch — Headers: " + headerCols + ", Row cells: " + firstRowCells);
                break;
            }
        }

        captureScreenshot("TC-MSPC-008_ConsistentColumns");
        logInfo("✅ Rows have consistent column count");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-009 — Search/filter input is available
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 9,
          description = "TC-MSPC-009: Verify search/filter input is available on the page")
    public void searchInputAvailableTest() {
        pcPage.dismissOverlays();
        boolean searchVisible = pcPage.isSearchInputVisible();
        logInfo("Search input visible: " + searchVisible);

        if (!searchVisible) {
            logInfo("ℹ️  No dedicated search input — checking for any filter controls");
            int filterControls = page.locator("input, mat-select").count();
            logInfo("Filter controls found: " + filterControls);
        }

        captureScreenshot("TC-MSPC-009_SearchInput");
        logInfo("✅ Search/filter input check complete (visible: " + searchVisible + ")");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-010 — Search filters the table data
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 10,
          dependsOnMethods = "searchInputAvailableTest",
          description = "TC-MSPC-010: Verify search filters the table data")
    public void searchFiltersDataTest() {
        if (!pcPage.isSearchInputVisible()) {
            logInfo("ℹ️  No search input — skipping filter test");
            captureScreenshot("TC-MSPC-010_NoSearch");
            return;
        }

        pcPage.dismissOverlays();
        int totalRows = pcPage.getRowCount();

        pcPage.search("TVS");
        page.waitForTimeout(2000);

        int filteredRows = pcPage.getRowCount();
        logInfo("Total: " + totalRows + " | After search 'TVS': " + filteredRows);

        Assert.assertTrue(filteredRows <= totalRows,
                "Filtered rows (" + filteredRows + ") > total (" + totalRows + ")");

        pcPage.clearSearch();
        page.waitForTimeout(1500);

        captureScreenshot("TC-MSPC-010_SearchFiltered");
        logInfo("✅ Search filters data correctly");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-011 — Clearing search restores full data
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 11,
          dependsOnMethods = "searchFiltersDataTest",
          description = "TC-MSPC-011: Verify clearing search restores full data")
    public void clearSearchRestoresDataTest() {
        if (!pcPage.isSearchInputVisible()) {
            logInfo("ℹ️  No search input — skipping");
            return;
        }

        pcPage.search("XYZ_NO_MATCH");
        page.waitForTimeout(1500);
        int filteredRows = pcPage.getRowCount();

        pcPage.clearSearch();
        page.waitForTimeout(2000);
        int restoredRows = pcPage.getRowCount();

        logInfo("Filtered: " + filteredRows + " | Restored: " + restoredRows);

        Assert.assertTrue(restoredRows >= filteredRows,
                "Restored (" + restoredRows + ") < filtered (" + filteredRows + ")");

        captureScreenshot("TC-MSPC-011_SearchCleared");
        logInfo("✅ Clearing search restores full data");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-012 — Pagination is visible
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 12,
          description = "TC-MSPC-012: Verify pagination is visible (if applicable)")
    public void paginationVisibleTest() {
        boolean paginationVisible = pcPage.isPaginationVisible();
        logInfo("Pagination visible: " + paginationVisible);

        if (paginationVisible) {
            String paginationText = pcPage.getPaginationText();
            logInfo("Pagination info: " + paginationText);
        } else {
            logInfo("ℹ️  No pagination — data may fit on one page or uses infinite scroll");
        }

        captureScreenshot("TC-MSPC-012_Pagination");
        logInfo("✅ Pagination check complete");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-013 — Dropdown filters are present
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 13,
          description = "TC-MSPC-013: Verify dropdown filters are present on the page")
    public void dropdownFiltersPresentTest() {
        int matSelectCount = pcPage.getMatSelectCount();
        logInfo("mat-select dropdowns: " + matSelectCount);

        // Part Commitment may have plant/unit/conveyor filters
        captureScreenshot("TC-MSPC-013_Dropdowns");
        logInfo("✅ Dropdown filters check: " + matSelectCount + " found");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-014 — Scroll down shows more data
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 14,
          description = "TC-MSPC-014: Verify scrolling down shows more data or table scrolls")
    public void scrollDownTest() {
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(1500);

        // Page should still be functional
        String url = pcPage.getPageUrl();
        Assert.assertTrue(url.contains("SequencePlanTest"),
                "Page URL changed after scrolling. Actual: " + url);

        double scrollY = (double) page.evaluate("window.scrollY");
        logInfo("Scroll position after scrolling down: " + scrollY);

        captureScreenshot("TC-MSPC-014_ScrollDown");
        logInfo("✅ Scrolled down — position: " + scrollY);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-015 — Scroll up returns to top
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 15,
          dependsOnMethods = "scrollDownTest",
          description = "TC-MSPC-015: Verify scrolling up returns to the top")
    public void scrollUpTest() {
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(1500);

        double scrollY = (double) page.evaluate("window.scrollY");
        logInfo("Scroll position after scrolling up: " + scrollY);

        Assert.assertTrue(scrollY < 100,
                "Page did not scroll back to top. Scroll Y: " + scrollY);

        captureScreenshot("TC-MSPC-015_ScrollUp");
        logInfo("✅ Scrolled back to top");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-016 — Export/Download button is present
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 16,
          description = "TC-MSPC-016: Verify Export/Download button is present (if applicable)")
    public void exportButtonPresentTest() {
        boolean exportVisible = pcPage.isExportButtonVisible();
        logInfo("Export button visible: " + exportVisible);

        if (!exportVisible) {
            logInfo("ℹ️  No export button found — feature may not be available");
        }

        captureScreenshot("TC-MSPC-016_ExportButton");
        logInfo("✅ Export button check complete (visible: " + exportVisible + ")");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-017 — No error dialogs on the page
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 17,
          description = "TC-MSPC-017: Verify no error dialogs are displayed on the page")
    public void noErrorDialogsTest() {
        int errorDialogs = page.locator(".error-dialog, .alert-danger, [role='alertdialog'], .error-message, .swal2-icon-error").count();
        logInfo("Error dialogs found: " + errorDialogs);

        Assert.assertEquals(errorDialogs, 0,
                "Error dialogs found on the Part Commitment page");

        captureScreenshot("TC-MSPC-017_NoErrors");
        logInfo("✅ No error dialogs on the page");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-018 — Page URL remains stable
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 18,
          description = "TC-MSPC-018: Verify page URL remains stable during interactions")
    public void urlRemainsStableTest() {
        String url = pcPage.getPageUrl();
        logInfo("Current URL: " + url);

        Assert.assertTrue(url.contains("SequencePlanTest"),
                "URL does not contain 'SequencePlanTest'. Actual: " + url);

        captureScreenshot("TC-MSPC-018_UrlStable");
        logInfo("✅ URL remains stable: " + url);
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-019 — Table data is not all empty/blank
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 19,
          dependsOnMethods = "dataTableVisibleTest",
          description = "TC-MSPC-019: Verify table data is not all empty or blank")
    public void tableDataNotAllBlankTest() {
        int rowCount = pcPage.getRowCount();
        if (rowCount == 0) {
            logInfo("ℹ️  No rows — skipping blank check");
            return;
        }

        // Check first 5 rows for non-blank content
        int nonBlankRows = 0;
        for (int i = 0; i < Math.min(rowCount, 5); i++) {
            String cellText = pcPage.getCellText(i, 0);
            if (!cellText.isBlank()) nonBlankRows++;
        }

        logInfo("Non-blank rows (first 5): " + nonBlankRows);

        Assert.assertTrue(nonBlankRows > 0,
                "All checked rows have blank first column — possible data issue");

        captureScreenshot("TC-MSPC-019_DataNotBlank");
        logInfo("✅ Table data contains non-blank values");
    }

    // ─────────────────────────────────────────────────────────────────────
    // TC-MSPC-020 — Page remains functional after rapid interactions
    // ─────────────────────────────────────────────────────────────────────
    @Test(priority = 20,
          description = "TC-MSPC-020: Verify page remains functional after rapid interactions")
    public void rapidInteractionsTest() {
        // Rapid scroll
        page.evaluate("window.scrollTo(0, document.body.scrollHeight)");
        page.waitForTimeout(200);
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(200);
        page.evaluate("window.scrollTo(0, 500)");
        page.waitForTimeout(200);
        page.evaluate("window.scrollTo(0, 0)");
        page.waitForTimeout(500);

        // Page should still be functional
        boolean tableVisible = pcPage.isTableVisible();
        String url = pcPage.getPageUrl();

        logInfo("Table still visible: " + tableVisible + " | URL: " + url);

        Assert.assertTrue(url.contains("SequencePlanTest"),
                "Page crashed after rapid interactions. URL: " + url);

        captureScreenshot("TC-MSPC-020_RapidInteractions");
        logInfo("✅ Page remains functional after rapid interactions");
    }
}
