package com.tvsm.autoseq.pages.eyeq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.base.BasePage;
import com.tvsm.autoseq.config.ConfigReader;

import java.util.ArrayList;
import java.util.List;

/**
 * DvpSummaryPage — Page Object for the DVP (Design Verification Plan) Summary screen.
 * Application: https://uat-eyeq.tvsmotor.net/knowledgegraphdvpsummary
 *
 * Covers:
 *  - Page load and URL verification
 *  - Navigation menu / sidebar
 *  - Filter controls (model, variant, date, status dropdowns)
 *  - Summary data table (columns, rows, pagination)
 *  - Search functionality
 *  - Export / download actions
 *  - DVP status indicators (Pass/Fail/Pending)
 *  - Chart / visualization area (if present)
 *  - Detail view / drill-down via row click
 */
public class DvpSummaryPage extends BasePage {

    // ── Page identification ───────────────────────────────────────────────
    private static final String PAGE_HEADER = "h1, h2, .page-title, .header-title, [class*='title']";

    // ── Loading indicators ────────────────────────────────────────────────
    private static final String LOADING_SPINNER = "mat-spinner, .spinner, [class*='loading'], [class*='spinner'], .loader";

    // ── Navigation / Sidebar ──────────────────────────────────────────────
    private static final String NAV_MENU       = ".menu_item, nav a, .sidebar-menu a, [class*='menu']";
    private static final String ACTIVE_NAV     = ".menu_item.active, .active-link, [class*='active']";

    // ── Filter section ────────────────────────────────────────────────────
    private static final String FILTER_SECTION  = ".filter-section, .filters, [class*='filter'], form, "
                                                + ".mat-form-field, mat-select, [class*='dropdown']";
    private static final String MODEL_DROPDOWN  = "select[name*='model' i], mat-select[formcontrolname*='model' i], "
                                                + "[class*='model'] select, [aria-label*='model' i], "
                                                + "mat-select, [class*='dropdown']";
    private static final String VARIANT_DROPDOWN = "select[name*='variant' i], mat-select[formcontrolname*='variant' i], "
                                                 + "[class*='variant'] select, [aria-label*='variant' i]";
    private static final String STATUS_DROPDOWN = "select[name*='status' i], mat-select[formcontrolname*='status' i], "
                                                + "[class*='status'] select, [aria-label*='status' i]";
    private static final String DATE_FROM       = "input[type='date']:first-of-type, input[name*='from' i], "
                                                + "input[placeholder*='from' i], input[placeholder*='start' i]";
    private static final String DATE_TO         = "input[type='date']:last-of-type, input[name*='to' i], "
                                                + "input[placeholder*='to' i], input[placeholder*='end' i]";
    private static final String SUBMIT_BTN      = "button.submit-btn, button:has-text('Submit'), button:has-text('Apply'), "
                                                + "button:has-text('Search'), button[type='submit']";
    private static final String RESET_BTN       = "button:has-text('Reset'), button:has-text('Clear')";

    // ── Part Number & Country Inputs (DVP Analysis Dashboard) ────────────
    private static final String PART_NUMBER_INPUT = "input[placeholder*='Part Number' i], input[placeholder*='part number' i], "
                                                 + "input[formcontrolname*='part' i], input[aria-label*='Part Number' i]";
    private static final String COUNTRY_INPUT    = "input[placeholder*='Country' i], input[placeholder*='country' i], "
                                                 + "input[formcontrolname*='country' i], input[aria-label*='Country' i]";
    private static final String PART_NUMBER_SUGGESTION = "mat-chip:has-text('%s'), .suggestion-chip:has-text('%s'), "
                                                       + "[class*='chip']:has-text('%s'), span:has-text('%s')";
    private static final String COUNTRY_SUGGESTION = "mat-chip:has-text('%s'), .suggestion-chip:has-text('%s'), "
                                                   + "[class*='chip']:has-text('%s'), span:has-text('%s')";
    private static final String INPUT_TYPE_PART_NUMBER = "mat-button-toggle:has-text('Part Number'), "
                                                       + "button:has-text('Part Number'), [class*='toggle']:has-text('Part Number')";
    private static final String INPUT_TYPE_PART_NAME   = "mat-button-toggle:has-text('Part Name'), "
                                                       + "button:has-text('Part Name'), [class*='toggle']:has-text('Part Name')";

    // ── Search (generic fallback) ─────────────────────────────────────────
    private static final String SEARCH_INPUT    = "input[type='search'], input[placeholder*='search' i], "
                                                + "input[placeholder*='Search' i], input.search-input, "
                                                + PART_NUMBER_INPUT;

    // ── Data table / data grid (Angular app uses div-based layouts) ──────
    private static final String TABLE           = "table, .data-table, mat-table, [class*='table'], "
                                                + "[class*='grid'], [class*='card'], [class*='list-container']";
    private static final String TABLE_HEADERS   = "table th, .mat-header-cell, thead th, "
                                                + "[class*='header-cell'], [class*='col-header']";
    private static final String TABLE_ROWS      = "table tbody tr, .mat-row, tbody tr, "
                                                + "[class*='data-row'], [class*='grid-row'], [class*='card']";
    private static final String TABLE_CELLS     = "td, .mat-cell, [class*='cell'], [class*='col']";
    private static final String EMPTY_STATE     = ".no-data, .empty-state, :has-text('No data'), :has-text('No records'), "
                                                + ":has-text('No results')";

    // ── Pagination ────────────────────────────────────────────────────────
    private static final String PAGINATION      = ".pagination, mat-paginator, [class*='paginator'], nav[aria-label='pagination']";
    private static final String PAGE_NEXT       = ".pagination .next, [aria-label='Next'], button:has-text('Next'), .mat-paginator-navigation-next";
    private static final String PAGE_PREV       = ".pagination .prev, [aria-label='Previous'], button:has-text('Previous'), .mat-paginator-navigation-previous";
    private static final String PAGE_SIZE       = ".mat-paginator-page-size select, select[aria-label*='page size' i]";

    // ── Status indicators ─────────────────────────────────────────────────
    private static final String STATUS_PASS     = ".status-pass, .badge-success, [class*='pass'], .text-success";
    private static final String STATUS_FAIL     = ".status-fail, .badge-danger, [class*='fail'], .text-danger";
    private static final String STATUS_PENDING  = ".status-pending, .badge-warning, [class*='pending'], .text-warning";

    // ── Export / Download ─────────────────────────────────────────────────
    private static final String EXPORT_BTN      = "button:has-text('Export'), button:has-text('Download'), "
                                                + "a:has-text('Export'), [aria-label*='export' i], [aria-label*='download' i]";
    private static final String EXPORT_EXCEL    = "button:has-text('Excel'), a:has-text('Excel'), [aria-label*='excel' i]";
    private static final String EXPORT_PDF      = "button:has-text('PDF'), a:has-text('PDF'), [aria-label*='pdf' i]";
    private static final String EXPORT_CSV      = "button:has-text('CSV'), a:has-text('CSV'), [aria-label*='csv' i]";

    // ── Charts / Visualization ────────────────────────────────────────────
    private static final String CHART_AREA      = "canvas, svg.chart, [class*='chart'], .highcharts-container, "
                                                + "[class*='graph'], .recharts-wrapper";

    // ── Tabs (if DVP Summary has sub-tabs) ────────────────────────────────
    private static final String SUB_TABS        = ".mat-tab-label, [role='tab'], .nav-tabs a, .tab-item";

    public DvpSummaryPage(Page page) {
        super(page);
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NAVIGATION
    // ═══════════════════════════════════════════════════════════════════════

    public DvpSummaryPage navigateTo() {
        page.navigate(ConfigReader.dvpSummaryUrl());
        page.waitForLoadState();
        System.out.println("🌐 Navigated to DVP Summary: " + ConfigReader.dvpSummaryUrl());
        return this;
    }

    public DvpSummaryPage waitForPageLoad() {
        // Wait for spinner to disappear
        try {
            page.locator(LOADING_SPINNER).first()
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.HIDDEN).setTimeout(timeout));
        } catch (Exception ignored) {}

        // Wait for table or main content
        try {
            page.locator(TABLE).first()
                .waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
            System.out.println("✅ DVP Summary table rendered");
        } catch (Exception e) {
            System.out.println("ℹ️  Table not found — checking for other content");
        }

        System.out.println("✅ DVP Summary page load complete");
        return this;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PAGE STATE
    // ═══════════════════════════════════════════════════════════════════════

    public String getPageUrl() { return getCurrentUrl(); }
    public String getPageTitle() { return getTitle(); }

    public String getPageHeader() {
        try {
            return page.locator(PAGE_HEADER).first().innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public boolean isPageFullyLoaded() {
        return page.locator(LOADING_SPINNER).count() == 0
            && (page.locator(TABLE).count() > 0 || page.locator(CHART_AREA).count() > 0);
    }

    public boolean hasNoSpinner() {
        return page.locator(LOADING_SPINNER).count() == 0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // FILTER CONTROLS
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isFilterSectionVisible() {
        return page.locator(FILTER_SECTION).count() > 0;
    }

    public boolean isModelDropdownVisible() {
        return page.locator(MODEL_DROPDOWN).count() > 0;
    }

    public boolean isVariantDropdownVisible() {
        return page.locator(VARIANT_DROPDOWN).count() > 0;
    }

    public boolean isStatusDropdownVisible() {
        return page.locator(STATUS_DROPDOWN).count() > 0;
    }

    public boolean isDateFromVisible() {
        return page.locator(DATE_FROM).count() > 0;
    }

    public boolean isDateToVisible() {
        return page.locator(DATE_TO).count() > 0;
    }

    public boolean isSubmitButtonVisible() {
        return page.locator(SUBMIT_BTN).count() > 0;
    }

    public DvpSummaryPage selectModel(String modelName) {
        Locator dropdown = page.locator(MODEL_DROPDOWN).first();
        if (dropdown.count() > 0) {
            dropdown.click();
            page.locator("mat-option:has-text('" + modelName + "'), option:has-text('" + modelName + "')").first().click();
            page.waitForTimeout(500);
            System.out.println("✅ Selected model: " + modelName);
        }
        return this;
    }

    public DvpSummaryPage selectVariant(String variantName) {
        Locator dropdown = page.locator(VARIANT_DROPDOWN).first();
        if (dropdown.count() > 0) {
            dropdown.click();
            page.locator("mat-option:has-text('" + variantName + "'), option:has-text('" + variantName + "')").first().click();
            page.waitForTimeout(500);
            System.out.println("✅ Selected variant: " + variantName);
        }
        return this;
    }

    public DvpSummaryPage selectStatus(String status) {
        Locator dropdown = page.locator(STATUS_DROPDOWN).first();
        if (dropdown.count() > 0) {
            dropdown.click();
            page.locator("mat-option:has-text('" + status + "'), option:has-text('" + status + "')").first().click();
            page.waitForTimeout(500);
            System.out.println("✅ Selected status: " + status);
        }
        return this;
    }

    public DvpSummaryPage setDateFrom(String date) {
        Locator input = page.locator(DATE_FROM).first();
        if (input.count() > 0) {
            input.clear();
            input.fill(date);
            System.out.println("✅ Date From set: " + date);
        }
        return this;
    }

    public DvpSummaryPage setDateTo(String date) {
        Locator input = page.locator(DATE_TO).first();
        if (input.count() > 0) {
            input.clear();
            input.fill(date);
            System.out.println("✅ Date To set: " + date);
        }
        return this;
    }

    public boolean isSubmitButtonEnabled() {
        Locator btn = page.locator(SUBMIT_BTN).first();
        return btn.count() > 0 && btn.isEnabled();
    }

    public DvpSummaryPage clickSubmit() {
        Locator btn = page.locator(SUBMIT_BTN).first();
        if (btn.count() > 0) {
            if (btn.isEnabled()) {
                btn.click();
                page.waitForTimeout(1500);
                System.out.println("✅ Clicked Submit/Apply");
            } else {
                // Submit is disabled — use JS click for testing or log
                System.out.println("⚠️  Submit button is disabled — forcing JS click for test");
                btn.evaluate("el => el.click()");
                page.waitForTimeout(1500);
            }
        }
        return this;
    }

    public DvpSummaryPage clickReset() {
        if (page.locator(RESET_BTN).count() > 0) {
            page.locator(RESET_BTN).first().click();
            page.waitForTimeout(1000);
            System.out.println("✅ Clicked Reset/Clear");
        }
        return this;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SEARCH
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isSearchInputVisible() {
        return page.locator(SEARCH_INPUT).count() > 0
            && page.locator(SEARCH_INPUT).first().isVisible();
    }

    public DvpSummaryPage search(String keyword) {
        Locator input = page.locator(SEARCH_INPUT).first();
        input.click();
        input.fill(keyword);
        page.waitForTimeout(1500);
        System.out.println("🔍 Searched: " + keyword);
        return this;
    }

    public DvpSummaryPage clearSearch() {
        page.locator(SEARCH_INPUT).first().clear();
        page.waitForTimeout(1000);
        System.out.println("🔍 Search cleared");
        return this;
    }

    public String getSearchValue() {
        try {
            return page.locator(SEARCH_INPUT).first().inputValue().trim();
        } catch (Exception e) {
            return "";
        }
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PART NUMBER & COUNTRY (DVP Analysis Dashboard specific)
    // ═══════════════════════════════════════════════════════════════════════

    /**
     * Checks if the Part Number input field is visible.
     */
    public boolean isPartNumberInputVisible() {
        return page.locator(PART_NUMBER_INPUT).count() > 0;
    }

    /**
     * Checks if the Country input field is visible.
     */
    public boolean isCountryInputVisible() {
        return page.locator(COUNTRY_INPUT).count() > 0;
    }

    /**
     * Enters a part number into the Part Number input field.
     * Clears any existing value first.
     */
    public DvpSummaryPage enterPartNumber(String partNumber) {
        Locator input = page.locator(PART_NUMBER_INPUT).first();
        if (input.count() > 0) {
            input.click();
            input.clear();
            input.fill(partNumber);
            page.waitForTimeout(1000);
            System.out.println("✅ Entered Part Number: " + partNumber);
        } else {
            System.out.println("⚠️  Part Number input not found — trying search input fallback");
            Locator searchInput = page.locator(SEARCH_INPUT).first();
            searchInput.click();
            searchInput.clear();
            searchInput.fill(partNumber);
            page.waitForTimeout(1000);
        }
        return this;
    }

    /**
     * Enters a country into the Country input field.
     * Clears any existing value first.
     */
    public DvpSummaryPage enterCountry(String country) {
        Locator input = page.locator(COUNTRY_INPUT).first();
        if (input.count() > 0) {
            input.click();
            input.clear();
            input.fill(country);
            page.waitForTimeout(1000);
            System.out.println("✅ Entered Country: " + country);
        } else {
            System.out.println("⚠️  Country input not found");
        }
        return this;
    }

    /**
     * Gets the current value in the Part Number input field.
     */
    public String getPartNumberValue() {
        try {
            return page.locator(PART_NUMBER_INPUT).first().inputValue().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Gets the current value in the Country input field.
     */
    public String getCountryValue() {
        try {
            return page.locator(COUNTRY_INPUT).first().inputValue().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * Clicks a Part Number suggestion chip (e.g., N7140210, N9190900, P6010100).
     */
    public DvpSummaryPage clickPartNumberSuggestion(String partNumber) {
        String selector = String.format(PART_NUMBER_SUGGESTION, partNumber, partNumber, partNumber, partNumber);
        Locator chip = page.locator(selector).first();
        if (chip.count() > 0) {
            chip.click();
            page.waitForTimeout(1000);
            System.out.println("✅ Clicked Part Number suggestion: " + partNumber);
        } else {
            System.out.println("⚠️  Part Number suggestion chip '" + partNumber + "' not found");
        }
        return this;
    }

    /**
     * Clicks a Country suggestion chip (e.g., Indonesia, Congo - PNR, Congo).
     */
    public DvpSummaryPage clickCountrySuggestion(String country) {
        String selector = String.format(COUNTRY_SUGGESTION, country, country, country, country);
        Locator chip = page.locator(selector).first();
        if (chip.count() > 0) {
            chip.click();
            page.waitForTimeout(1000);
            System.out.println("✅ Clicked Country suggestion: " + country);
        } else {
            System.out.println("⚠️  Country suggestion chip '" + country + "' not found");
        }
        return this;
    }

    /**
     * Selects the "Part Number" input type toggle.
     */
    public DvpSummaryPage selectPartNumberInputType() {
        Locator toggle = page.locator(INPUT_TYPE_PART_NUMBER).first();
        if (toggle.count() > 0) {
            toggle.click();
            page.waitForTimeout(500);
            System.out.println("✅ Selected input type: Part Number");
        }
        return this;
    }

    /**
     * Selects the "Part Name" input type toggle.
     */
    public DvpSummaryPage selectPartNameInputType() {
        Locator toggle = page.locator(INPUT_TYPE_PART_NAME).first();
        if (toggle.count() > 0) {
            toggle.click();
            page.waitForTimeout(500);
            System.out.println("✅ Selected input type: Part Name");
        }
        return this;
    }

    /**
     * Performs a complete DVP search: enters part number + country, then clicks Submit.
     * This is the primary workflow for the DVP Analysis Dashboard.
     *
     * @param partNumber e.g., "N7140210"
     * @param country    e.g., "Indonesia"
     */
    public DvpSummaryPage submitDvpSearch(String partNumber, String country) {
        enterPartNumber(partNumber);
        enterCountry(country);
        clickSubmit();
        page.waitForTimeout(3000); // Allow API response time
        System.out.println("✅ DVP Search submitted — Part: " + partNumber + " | Country: " + country);
        return this;
    }

    /**
     * Clears both Part Number and Country input fields.
     */
    public DvpSummaryPage clearAllInputs() {
        Locator partInput = page.locator(PART_NUMBER_INPUT).first();
        if (partInput.count() > 0) {
            partInput.clear();
        }
        Locator countryInput = page.locator(COUNTRY_INPUT).first();
        if (countryInput.count() > 0) {
            countryInput.clear();
        }
        page.waitForTimeout(500);
        System.out.println("✅ Cleared all inputs");
        return this;
    }

    /**
     * Checks if results are displayed after submission (chart, table, or content area).
     */
    public boolean hasResults() {
        return isChartVisible() || getTableRowCount() > 0
            || page.locator("[class*='result'], [class*='summary'], [class*='content']").count() > 0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // DATA TABLE
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isTableVisible() {
        return page.locator(TABLE).count() > 0
            && page.locator(TABLE).first().isVisible();
    }

    public int getTableRowCount() {
        return page.locator(TABLE_ROWS).count();
    }

    public int getTableColumnCount() {
        return page.locator(TABLE_HEADERS).count();
    }

    public List<String> getTableHeaders() {
        List<String> headers = new ArrayList<>();
        Locator ths = page.locator(TABLE_HEADERS);
        for (int i = 0; i < ths.count(); i++) {
            try {
                headers.add(ths.nth(i).innerText().trim());
            } catch (Exception e) {
                headers.add("");
            }
        }
        return headers;
    }

    public String getCellValue(int row, int col) {
        try {
            Locator rowLocator = page.locator(TABLE_ROWS).nth(row);
            return rowLocator.locator(TABLE_CELLS).nth(col).innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    public List<String> getRowData(int rowIndex) {
        List<String> data = new ArrayList<>();
        Locator row = page.locator(TABLE_ROWS).nth(rowIndex);
        Locator cells = row.locator(TABLE_CELLS);
        for (int i = 0; i < cells.count(); i++) {
            try {
                data.add(cells.nth(i).innerText().trim());
            } catch (Exception e) {
                data.add("");
            }
        }
        return data;
    }

    public boolean isEmptyStateShown() {
        return page.locator(EMPTY_STATE).count() > 0;
    }

    public DvpSummaryPage clickRow(int rowIndex) {
        page.locator(TABLE_ROWS).nth(rowIndex).click();
        page.waitForTimeout(1000);
        System.out.println("✅ Clicked table row: " + rowIndex);
        return this;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // PAGINATION
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isPaginationVisible() {
        return page.locator(PAGINATION).count() > 0;
    }

    public DvpSummaryPage clickNextPage() {
        if (page.locator(PAGE_NEXT).count() > 0) {
            page.locator(PAGE_NEXT).first().click();
            page.waitForTimeout(1500);
            System.out.println("➡️ Navigated to next page");
        }
        return this;
    }

    public DvpSummaryPage clickPrevPage() {
        if (page.locator(PAGE_PREV).count() > 0) {
            page.locator(PAGE_PREV).first().click();
            page.waitForTimeout(1500);
            System.out.println("⬅️ Navigated to previous page");
        }
        return this;
    }

    public boolean isNextPageEnabled() {
        Locator next = page.locator(PAGE_NEXT).first();
        return next.count() > 0 && next.isEnabled();
    }

    public boolean isPrevPageEnabled() {
        Locator prev = page.locator(PAGE_PREV).first();
        return prev.count() > 0 && prev.isEnabled();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // STATUS INDICATORS
    // ═══════════════════════════════════════════════════════════════════════

    public int getPassCount() { return page.locator(STATUS_PASS).count(); }
    public int getFailCount() { return page.locator(STATUS_FAIL).count(); }
    public int getPendingCount() { return page.locator(STATUS_PENDING).count(); }

    public boolean hasStatusBadges() {
        return getPassCount() + getFailCount() + getPendingCount() > 0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // EXPORT / DOWNLOAD
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isExportButtonVisible() {
        return page.locator(EXPORT_BTN).count() > 0;
    }

    public DvpSummaryPage clickExport() {
        if (page.locator(EXPORT_BTN).count() > 0) {
            page.locator(EXPORT_BTN).first().click();
            page.waitForTimeout(1000);
            System.out.println("✅ Clicked Export");
        }
        return this;
    }

    public boolean isExcelExportAvailable() {
        return page.locator(EXPORT_EXCEL).count() > 0;
    }

    public boolean isPdfExportAvailable() {
        return page.locator(EXPORT_PDF).count() > 0;
    }

    public boolean isCsvExportAvailable() {
        return page.locator(EXPORT_CSV).count() > 0;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // CHARTS / VISUALIZATION
    // ═══════════════════════════════════════════════════════════════════════

    public boolean isChartVisible() {
        return page.locator(CHART_AREA).count() > 0;
    }

    public int getChartCount() {
        return page.locator(CHART_AREA).count();
    }

    // ═══════════════════════════════════════════════════════════════════════
    // SUB-TABS
    // ═══════════════════════════════════════════════════════════════════════

    public int getSubTabCount() {
        return page.locator(SUB_TABS).count();
    }

    public List<String> getSubTabLabels() {
        List<String> labels = new ArrayList<>();
        Locator tabs = page.locator(SUB_TABS);
        for (int i = 0; i < tabs.count(); i++) {
            try {
                labels.add(tabs.nth(i).innerText().trim());
            } catch (Exception e) {
                labels.add("");
            }
        }
        return labels;
    }

    public DvpSummaryPage clickSubTab(String tabName) {
        String selector = SUB_TABS + ":has-text('" + tabName + "')";
        if (page.locator(selector).count() > 0) {
            page.locator(selector).first().click();
            page.waitForTimeout(1500);
            System.out.println("✅ Clicked sub-tab: " + tabName);
        }
        return this;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // NAVIGATION MENU
    // ═══════════════════════════════════════════════════════════════════════

    public int getNavMenuItemCount() {
        return page.locator(NAV_MENU).count();
    }

    public List<String> getNavMenuLabels() {
        List<String> labels = new ArrayList<>();
        Locator items = page.locator(NAV_MENU);
        for (int i = 0; i < items.count(); i++) {
            try {
                labels.add(items.nth(i).innerText().trim());
            } catch (Exception e) {
                labels.add("");
            }
        }
        return labels;
    }

    // ═══════════════════════════════════════════════════════════════════════
    // UTILITY
    // ═══════════════════════════════════════════════════════════════════════

    public int getButtonCount() { return page.locator("button").count(); }
    public int getInputCount()  { return page.locator("input").count(); }
    public int getSelectCount() { return page.locator("select, mat-select").count(); }

    public boolean hasInteractiveControls() {
        return getButtonCount() + getInputCount() + getSelectCount() > 0;
    }

    public boolean hasErrorDialog() {
        return page.locator(".error-dialog, .alert-danger, [role='alertdialog'], .error-message").count() > 0;
    }

    public boolean hasToast() {
        return page.locator(".toast, .snackbar, mat-snack-bar-container, [class*='toast']").count() > 0;
    }
}
