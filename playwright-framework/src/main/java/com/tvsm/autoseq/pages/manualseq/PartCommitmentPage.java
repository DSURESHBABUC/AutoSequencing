package com.tvsm.autoseq.pages.manualseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;

import java.util.ArrayList;
import java.util.List;

/**
 * PartCommitmentPage — Page Object for the Part Commitment module.
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest
 *
 * Covers: navigation to Part Commitment tab, data table, filters,
 * search, column headers, row data, pagination, and export.
 */
public class PartCommitmentPage extends BasePage {

    // ── Tab navigation selectors ──────────────────────────────────────────
    private static final String[] TAB_SELECTORS = {
        ".mat-tab-label-content:has-text('Part Commitment')",
        ".mat-mdc-tab-label-content:has-text('Part Commitment')",
        "[role='tab']:has-text('Part Commitment')",
        "a:has-text('Part Commitment')",
        "button:has-text('Part Commitment')",
        "li:has-text('Part Commitment')",
        ".mat-tab-label-content:has-text('PartCommitment')",
        "[role='tab']:has-text('PartCommitment')"
    };

    // ── Data table selectors ──────────────────────────────────────────────
    private static final String[] TABLE_SELECTORS = {
        "table", "mat-table", ".mat-table", ".cdk-table"
    };

    private static final String[] ROW_SELECTORS = {
        "table tbody tr", "mat-row", ".mat-row", ".cdk-row",
        "tr.mat-mdc-row", "table tr:not(:first-child)"
    };

    private static final String[] HEADER_SELECTORS = {
        "table thead th", "mat-header-cell", ".mat-header-cell",
        "th.mat-mdc-header-cell", "table th"
    };

    // ── Filter / Search selectors ─────────────────────────────────────────
    private static final String[] SEARCH_SELECTORS = {
        "input[placeholder*='Search' i]",
        "input[placeholder*='Filter' i]",
        "input[type='search']",
        "mat-form-field input",
        "input.mat-input-element"
    };

    // ── Loading indicators ────────────────────────────────────────────────
    private static final String SPINNER = ".mat-spinner, .mat-progress-spinner, .loading, .spinner, mat-spinner";
    private static final String PROGRESS_BAR = "mat-progress-bar, .mat-progress-bar, .progress-bar";

    public PartCommitmentPage(Page page) {
        super(page);
    }

    // ── Navigation ────────────────────────────────────────────────────────

    /**
     * Clicks the "Part Commitment" tab from the main navigation.
     * Tries multiple selectors to handle different Angular Material versions.
     */
    public PartCommitmentPage navigateToPartCommitment() {
        for (String selector : TAB_SELECTORS) {
            try {
                Locator tab = page.locator(selector).first();
                if (tab.count() > 0) {
                    tab.click(new Locator.ClickOptions().setForce(true));
                    System.out.println("✅ Clicked Part Commitment tab: " + selector);
                    return this;
                }
            } catch (Exception ignored) {}
        }
        // Fallback: try clicking by text content
        try {
            page.locator("text=Part Commitment").first().click();
            System.out.println("✅ Clicked Part Commitment via text selector");
        } catch (Exception e) {
            System.out.println("⚠️  Could not find Part Commitment tab: " + e.getMessage());
        }
        return this;
    }

    /**
     * Waits until the Part Commitment data is fully loaded.
     * Checks for spinner disappearance and table row appearance.
     */
    public void waitForDataLoad() {
        // Wait for any spinner/progress bar to disappear
        try {
            page.waitForSelector(SPINNER,
                    new Page.WaitForSelectorOptions().setTimeout(5000));
            page.locator(SPINNER).waitFor(new Locator.WaitForOptions()
                    .setState(com.microsoft.playwright.options.WaitForSelectorState.HIDDEN)
                    .setTimeout(30000));
            System.out.println("✅ Spinner disappeared — data loaded");
        } catch (Exception e) {
            System.out.println("ℹ️  No spinner detected or already hidden");
        }

        // Wait for table rows to appear
        for (String sel : ROW_SELECTORS) {
            try {
                page.waitForSelector(sel,
                        new Page.WaitForSelectorOptions().setTimeout(15000));
                System.out.println("✅ Data rows appeared: " + sel);
                return;
            } catch (Exception ignored) {}
        }
        System.out.println("⚠️  No table rows detected after waiting");
    }

    // ── Tab verification ──────────────────────────────────────────────────

    public boolean isPartCommitmentTabVisible() {
        for (String selector : TAB_SELECTORS) {
            if (page.locator(selector).count() > 0) return true;
        }
        return page.locator("text=Part Commitment").count() > 0;
    }

    public boolean isPartCommitmentPageLoaded() {
        // Check if we're on the Part Commitment view (table or content visible)
        for (String sel : TABLE_SELECTORS) {
            if (page.locator(sel).count() > 0) return true;
        }
        // Also check for any content area
        return page.locator(".part-commitment, [class*='part-commitment'], [class*='partcommitment']").count() > 0
                || getRowCount() > 0;
    }

    // ── Data Table ────────────────────────────────────────────────────────

    public boolean isTableVisible() {
        for (String sel : TABLE_SELECTORS) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    public int getRowCount() {
        for (String sel : ROW_SELECTORS) {
            int count = page.locator(sel).count();
            if (count > 0) return count;
        }
        return 0;
    }

    public int getColumnCount() {
        for (String sel : HEADER_SELECTORS) {
            int count = page.locator(sel).count();
            if (count > 0) return count;
        }
        return 0;
    }

    public List<String> getColumnHeaders() {
        List<String> headers = new ArrayList<>();
        for (String sel : HEADER_SELECTORS) {
            int count = page.locator(sel).count();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    headers.add(page.locator(sel).nth(i).innerText().trim());
                }
                break;
            }
        }
        return headers;
    }

    public String getCellText(int row, int col) {
        String[] cellSelectors = {"table tbody tr", "mat-row", ".cdk-row"};
        for (String sel : cellSelectors) {
            if (page.locator(sel).count() > row) {
                var cells = page.locator(sel).nth(row).locator("td, mat-cell, .mat-cell");
                if (cells.count() > col) {
                    return cells.nth(col).innerText().trim();
                }
            }
        }
        return "";
    }

    public String getFirstRowText() {
        for (String sel : ROW_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                return page.locator(sel).first().innerText().trim();
            }
        }
        return "";
    }

    // ── Search / Filter ───────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    public void search(String keyword) {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                var input = page.locator(sel).first();
                input.click(new Locator.ClickOptions().setForce(true));
                input.clear();
                input.fill(keyword);
                System.out.println("🔍 Searched in Part Commitment: " + keyword);
                return;
            }
        }
        System.out.println("⚠️  No search input found in Part Commitment");
    }

    public void clearSearch() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                var input = page.locator(sel).first();
                input.click(new Locator.ClickOptions().setForce(true));
                input.clear();
                System.out.println("🧹 Search cleared");
                return;
            }
        }
    }

    public String getSearchValue() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                return page.locator(sel).first().inputValue();
            }
        }
        return "";
    }

    // ── Dropdowns ─────────────────────────────────────────────────────────

    public int getMatSelectCount() {
        return page.locator("mat-select").count();
    }

    // ── Pagination ────────────────────────────────────────────────────────

    public boolean isPaginationVisible() {
        return page.locator("mat-paginator, .mat-paginator, .paginator, .pagination, [class*='paginator']").count() > 0;
    }

    public String getPaginationText() {
        var paginator = page.locator("mat-paginator, .mat-paginator, .paginator, .pagination");
        if (paginator.count() > 0) {
            return paginator.first().innerText().trim();
        }
        return "";
    }

    // ── Export / Actions ──────────────────────────────────────────────────

    public boolean isExportButtonVisible() {
        return page.locator("button:has-text('Export'), button:has-text('Download'), button:has-text('Excel'), [mattooltip*='Export' i]").count() > 0;
    }

    // ── Loading state ─────────────────────────────────────────────────────

    public boolean isLoading() {
        return page.locator(SPINNER).count() > 0 || page.locator(PROGRESS_BAR).count() > 0;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public String getPageUrl() {
        return page.url();
    }

    public void dismissOverlays() {
        try {
            page.evaluate("document.querySelectorAll('.cdk-overlay-backdrop').forEach(el => el.remove())");
            page.evaluate("var c = document.querySelector('.cdk-overlay-container'); if(c) c.innerHTML = '';");
            page.keyboard().press("Escape");
            page.waitForTimeout(300);
        } catch (Exception ignored) {}
    }
}
