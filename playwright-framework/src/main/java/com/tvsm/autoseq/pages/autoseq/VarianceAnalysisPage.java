package com.tvsm.autoseq.pages.autoseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.base.BasePage;

import java.util.ArrayList;
import java.util.List;

/**
 * VarianceAnalysisPage — Page Object for the Report → Variance Analysis screen.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 * Flow covered:
 *   1. Click the "Report" module/tab in the top navigation
 *   2. Click "Variance Analysis" (sub-tab / menu item under Report)
 *   3. Verify the Variance Analysis view loads (filters + data table)
 *
 * Selectors are intentionally broad (multiple fallbacks) because the Angular
 * Material app renders tabs as either classic (.mat-tab-label-content) or
 * MDC (.mat-mdc-tab-label-content) elements depending on the build.
 */
public class VarianceAnalysisPage extends BasePage {

    // ── Report module / tab ───────────────────────────────────────────────
    private static final String[] REPORT_TAB = {
        ".mat-tab-label-content:has-text('Report')",
        ".mat-mdc-tab-label-content:has-text('Report')",
        "[role='tab']:has-text('Report')",
        "a:has-text('Report')",
        "button:has-text('Report')",
        "li:has-text('Report')"
    };

    // ── Variance Analysis entry (sub-tab or menu item under Report) ────────
    private static final String[] VARIANCE_ANALYSIS = {
        ".mat-tab-label-content:has-text('Variance Analysis')",
        ".mat-mdc-tab-label-content:has-text('Variance Analysis')",
        "[role='tab']:has-text('Variance Analysis')",
        ".mat-mdc-menu-item:has-text('Variance Analysis')",
        ".mat-menu-item:has-text('Variance Analysis')",
        "a:has-text('Variance Analysis')",
        "button:has-text('Variance Analysis')",
        "li:has-text('Variance Analysis')",
        "span:has-text('Variance Analysis')"
    };

    // ── Variance Analysis content indicators ──────────────────────────────
    private static final String[] PAGE_INDICATORS = {
        "mat-select",
        "button:has-text('Submit')",
        "input[placeholder*='Search']",
        "table tbody tr",
        ".mat-tab-label-content:has-text('Analysis')"
    };

    private static final String SUBMIT_BUTTON =
        "button:has-text('Submit'), button.btn:has-text('Submit')";

    // The Variance Analysis grid is a plain <table> inside the component,
    // with a two-row <thead> (grouped headers with colspan + sub-headers).
    private static final String VA_TABLE = "app-prod-delivery-assurance table";
    private static final String VA_BODY_ROWS = "app-prod-delivery-assurance table tbody tr";
    private static final String DATA_TABLE_ROWS = VA_BODY_ROWS;

    public VarianceAnalysisPage(Page page) {
        super(page);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────────────

    /** Clicks the "Report" module/tab in the top navigation. */
    public VarianceAnalysisPage clickReportModule() {
        if (!clickFirstVisible(REPORT_TAB, "Report module")) {
            throw new RuntimeException("Could not find the 'Report' module/tab");
        }
        page.waitForTimeout(2000);
        return this;
    }

    /** Clicks the "Variance Analysis" entry under the Report module. */
    public VarianceAnalysisPage clickVarianceAnalysis() {
        if (!clickFirstVisible(VARIANCE_ANALYSIS, "Variance Analysis")) {
            throw new RuntimeException("Could not find the 'Variance Analysis' option under Report");
        }
        page.waitForTimeout(2500);
        return this;
    }

    /** Convenience: Report module → Variance Analysis in one call. */
    public VarianceAnalysisPage openVarianceAnalysis() {
        clickReportModule();
        clickVarianceAnalysis();
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Verification helpers
    // ─────────────────────────────────────────────────────────────────────

    /** True if the Report module/tab is present in the navigation. */
    public boolean isReportTabVisible() {
        for (String sel : REPORT_TAB) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    /** True if the Variance Analysis content appears to have loaded. */
    public boolean isVarianceAnalysisLoaded() {
        for (String sel : PAGE_INDICATORS) {
            try {
                if (page.locator(sel).count() > 0
                        && page.locator(sel).first().isVisible()) {
                    return true;
                }
            } catch (Exception ignored) {}
        }
        return false;
    }

    /** Clicks the Submit button to load variance data (if present). */
    public VarianceAnalysisPage clickSubmit() {
        Locator submit = page.locator(SUBMIT_BUTTON).first();
        if (submit.count() > 0 && submit.isVisible()) {
            safeClick(submit, "Submit");
            page.waitForTimeout(2500);
        } else {
            System.out.println("ℹ️  Submit button not present — data may auto-load");
        }
        return this;
    }

    /** Returns the number of data rows currently shown in the variance table. */
    public int getTableRowCount() {
        return page.locator(DATA_TABLE_ROWS).count();
    }

    /**
     * Waits for the Variance Analysis data table (body rows) to render.
     * The grid auto-loads using the default plant/model/date filters, so no
     * Submit click is required.
     *
     * @return true if at least one body row appeared within the timeout
     */
    public boolean waitForDataTable(int timeoutMs) {
        long deadline = System.currentTimeMillis() + timeoutMs;
        // Wait for the table element to attach first.
        try {
            page.locator(VA_TABLE).first().waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.ATTACHED)
                    .setTimeout(timeoutMs));
        } catch (Exception ignored) {}

        // Then poll for body rows to appear (data loads asynchronously).
        while (System.currentTimeMillis() < deadline) {
            int rows = page.locator(VA_BODY_ROWS).count();
            if (rows > 0) {
                System.out.println("✅ Variance Analysis table loaded with " + rows + " rows");
                return true;
            }
            try { page.waitForLoadState(com.microsoft.playwright.options.LoadState.NETWORKIDLE,
                    new Page.WaitForLoadStateOptions().setTimeout(2000)); }
            catch (Exception ignored) {}
            page.waitForTimeout(1000);
        }
        System.out.println("⚠️  Variance Analysis table has no rows after "
                + timeoutMs + "ms");
        return false;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Column extraction / comparison
    // ─────────────────────────────────────────────────────────────────────

    /** Group header for the "Tool generated MPS Plan VS Edited MPS Plan" column. */
    public static final String GROUP_TOOL_MPS_VS_EDITED_MPS =
        "Tool generated MPS Plan VS Edited MPS Plan";

    /** Group header for the "Edited MPS Plan VS Tool Generated Sequence Plan" column. */
    public static final String GROUP_EDITED_MPS_VS_TOOL_SEQ =
        "Edited MPS Plan VS Tool Generated Sequence Plan";

    /** Group header for the "Tool generated Sequence Plan VS Changed Sequence Plan (Manual)" column. */
    public static final String GROUP_TOOL_SEQ_VS_CHANGED_SEQ =
        "Tool generated Sequence Plan VS Changed Sequence Plan";

    /**
     * Result of comparing two columns row by row.
     * {@link #status()} returns "Okay" when every row matches, "Not Okay" otherwise.
     */
    public static class ColumnComparison {
        public final boolean matched;
        public final int rowsCompared;
        public final List<String> mismatches;

        public ColumnComparison(boolean matched, int rowsCompared, List<String> mismatches) {
            this.matched = matched;
            this.rowsCompared = rowsCompared;
            this.mismatches = mismatches;
        }

        /** "Okay" if the two columns match exactly, otherwise "Not Okay". */
        public String status() {
            return matched ? "Okay" : "Not Okay";
        }
    }

    /**
     * Compares two extracted columns value-by-value.
     *
     * @return a {@link ColumnComparison} whose {@code status()} is "Okay" when
     *         the data matches between the two columns, else "Not Okay" (with
     *         the list of differing rows captured for reporting).
     */
    public ColumnComparison compareColumns(List<String> left, List<String> right) {
        List<String> mismatches = new ArrayList<>();

        if (left.isEmpty() || right.isEmpty()) {
            mismatches.add("One or both columns are empty (left=" + left.size()
                    + ", right=" + right.size() + ")");
            return new ColumnComparison(false, 0, mismatches);
        }
        if (left.size() != right.size()) {
            mismatches.add("Row counts differ (left=" + left.size()
                    + ", right=" + right.size() + ")");
        }

        int n = Math.min(left.size(), right.size());
        boolean matched = left.size() == right.size();
        for (int i = 0; i < n; i++) {
            String l = left.get(i).trim();
            String r = right.get(i).trim();
            if (!l.equals(r)) {
                matched = false;
                mismatches.add("Row " + (i + 1) + ": '" + l + "' != '" + r + "'");
            }
        }
        return new ColumnComparison(matched, n, mismatches);
    }

    /**
     * Extracts the sub-column inside a header group whose sub-header label
     * contains all of the supplied (normalised) keywords. Falls back to the
     * group's first sub-column if no keyword match is found.
     *
     * @param groupHeader the top-level (row-1) group header text
     * @param keywords    keywords that must all appear in the sub-header label
     * @return ordered list of cell values for the matched sub-column
     */
    public List<String> getGroupSubColumn(String groupHeader, String... keywords) {
        int[] range = groupFlatRange(groupHeader);   // {startIndex, span}
        if (range == null) {
            System.out.println("⚠️  Group header not found: " + groupHeader);
            return new ArrayList<>();
        }

        List<String> subHeaders = subHeaderTexts();
        int target = -1;
        for (int i = range[0]; i < range[0] + range[1] && i < subHeaders.size(); i++) {
            String s = normalise(subHeaders.get(i));
            boolean all = true;
            for (String k : keywords) {
                if (!s.contains(normalise(k))) { all = false; break; }
            }
            if (all) { target = i; break; }
        }
        if (target < 0) target = range[0];   // fallback: first sub-column of the group

        System.out.println("📊 '" + groupHeader + "' → sub-column "
                + java.util.Arrays.toString(keywords) + " at flat column index " + target);
        return columnValues(target);
    }

    /**
     * Extracts the "Edited MPS Plan" figure (the "MPS Plan (Final)" /
     * "MPS Plan (Final Plan)" sub-column) inside a given header group.
     */
    public List<String> getEditedMpsPlanColumn(String groupHeader) {
        return getGroupSubColumn(groupHeader, "mpsplan", "final");
    }

    /**
     * Extracts the "Tool Generated Sequence Plan" figure (the
     * "Sequence Plan (From Tool)" sub-column) inside a given header group.
     * This figure is shared between:
     *   - "Edited MPS Plan VS Tool Generated Sequence Plan"           (2nd sub-col)
     *   - "Tool generated Sequence Plan VS Changed Sequence Plan (Manual)" (1st sub-col)
     */
    public List<String> getToolSequencePlanColumn(String groupHeader) {
        return getGroupSubColumn(groupHeader, "sequenceplan", "fromtool");
    }

    /**
     * Generic: extract every body cell value for a flat (colspan-expanded)
     * column index.
     */
    public List<String> columnValues(int columnIndex) {
        List<String> values = new ArrayList<>();
        Locator rows = page.locator(VA_BODY_ROWS);
        int rowCount = rows.count();
        for (int r = 0; r < rowCount; r++) {
            Locator cells = rows.nth(r).locator("td");
            if (columnIndex < cells.count()) {
                values.add(cells.nth(columnIndex).innerText().trim());
            }
        }
        return values;
    }

    /**
     * Locates a row-1 group header by (whitespace/case-insensitive) text and
     * returns its flat column range as {startIndex, span} accounting for the
     * colspans of the headers preceding it. Returns null if not found.
     */
    private int[] groupFlatRange(String groupHeader) {
        String target = normalise(groupHeader);
        Locator row1 = page.locator(VA_TABLE + " thead tr").first();
        Locator cells = row1.locator("th");
        int count = cells.count();
        int flat = 0;
        for (int i = 0; i < count; i++) {
            Locator cell = cells.nth(i);
            String text;
            try { text = normalise(cell.innerText()); } catch (Exception e) { text = ""; }
            int span = 1;
            try {
                String cs = cell.getAttribute("colspan");
                if (cs != null && !cs.isBlank()) span = Integer.parseInt(cs.trim());
            } catch (Exception ignored) {}

            if (!target.isEmpty() && text.contains(target)) {
                return new int[]{flat, span};
            }
            flat += span;
        }
        return null;
    }

    /**
     * Returns the row-2 sub-header texts aligned to flat column indices.
     * (Each sub-header cell spans a single column, so its position equals the
     * flat body-cell index.)
     */
    private List<String> subHeaderTexts() {
        List<String> subs = new ArrayList<>();
        Locator headerRows = page.locator(VA_TABLE + " thead tr");
        if (headerRows.count() < 2) return subs;
        Locator cells = headerRows.nth(1).locator("th");
        int count = cells.count();
        for (int i = 0; i < count; i++) {
            try { subs.add(cells.nth(i).innerText().trim()); }
            catch (Exception e) { subs.add(""); }
        }
        return subs;
    }

    /** Normalises label text: lowercase, strip all non-alphanumeric characters. */
    private String normalise(String s) {
        if (s == null) return "";
        return s.toLowerCase().replaceAll("[^a-z0-9]", "");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Internal helper
    // ─────────────────────────────────────────────────────────────────────

    private boolean clickFirstVisible(String[] selectors, String name) {
        for (String sel : selectors) {
            try {
                Locator loc = page.locator(sel).first();
                loc.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE)
                        .setTimeout(5000));
                loc.scrollIntoViewIfNeeded();
                safeClick(loc, name);
                System.out.println("✅ Clicked " + name + " via: " + sel);
                return true;
            } catch (Exception ignored) {}
        }
        return false;
    }
}
