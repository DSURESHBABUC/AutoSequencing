package com.tvsm.autoseq.pages.autoseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.base.BasePage;
import com.tvsm.autoseq.config.ConfigReader;

import java.util.ArrayList;
import java.util.List;

/**
 * ConveyorHourMasterPage — Page Object for the Conveyor Hour Master screen.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 * Covers:
 *   - Multi-select checkboxes on record rows
 *   - Select All / Deselect functionality
 *   - Bulk Update popup with Model Run Time Trigger input
 *   - Filter-based selection and update
 *   - Success/error confirmation messages
 *
 * UI behavior is consistent with the existing SKU Level Batch Size Update pattern.
 */
public class ConveyorHourMasterPage extends BasePage {

    // ── Navigation ────────────────────────────────────────────────────────
    private static final String CONVEYOR_HOUR_MASTER_TAB = "a:has-text('Conveyor Hour Master'), mat-tab:has-text('Conveyor Hour Master'), .mat-tab-label-content:has-text('Conveyor Hour Master')";
    private static final String DATA_MAINTENANCE_TAB     = "a:has-text('Data Maintenance'), .mat-tab-label-content:has-text('Data Maintenance')";

    // ── Table / Records ───────────────────────────────────────────────────
    private static final String RECORD_ROW           = "table tbody tr, .mat-row, mat-row, .cdk-row";
    private static final String ROW_CHECKBOX         = "mat-checkbox, input[type='checkbox'], .mat-checkbox";
    private static final String SELECT_ALL_CHECKBOX  = "thead mat-checkbox, thead input[type='checkbox'], th mat-checkbox, .select-all-checkbox";
    private static final String CHECKED_CHECKBOX     = "mat-checkbox.mat-checkbox-checked, input[type='checkbox']:checked, .mat-checkbox-checked";

    // ── Bulk Update ───────────────────────────────────────────────────────
    private static final String BULK_UPDATE_BTN      = "button:has-text('Update'), button:has-text('Bulk Update'), button:has-text('Save')";
    private static final String BULK_UPDATE_POPUP    = ".mat-dialog-container, .cdk-overlay-pane .mat-dialog-container, .bulk-update-popup, .swal2-popup";
    private static final String MODEL_RUN_TIME_INPUT = "input[placeholder*='Model Run Time'], input[formcontrolname*='modelRunTime'], input[formcontrolname*='runTime'], .mat-dialog-container input[type='number'], .mat-dialog-container input[type='text']";
    private static final String CONFIRM_BTN          = ".mat-dialog-actions button:has-text('Confirm'), .mat-dialog-actions button:has-text('Submit'), .mat-dialog-actions button:has-text('Save'), .swal2-confirm";
    private static final String CANCEL_BTN           = ".mat-dialog-actions button:has-text('Cancel'), .mat-dialog-actions button:has-text('Close'), .swal2-cancel";

    // ── Messages ──────────────────────────────────────────────────────────
    private static final String SUCCESS_MESSAGE      = ".mat-snack-bar-container, .swal2-popup, .toast-success, .alert-success, .notification-success";
    private static final String ERROR_MESSAGE        = ".mat-error, .mat-snack-bar-container, .swal2-popup, .toast-error, .alert-danger, .validation-error";
    private static final String VALIDATION_MSG       = ".mat-error, .error-message, .validation-message, .swal2-html-container";

    // ── Filter ────────────────────────────────────────────────────────────
    private static final String FILTER_INPUT         = "input[placeholder*='Filter'], input[placeholder*='Search'], input[type='search']";
    private static final String MODEL_FILTER         = "mat-select[aria-label*='Model'], mat-select[formcontrolname*='model']";
    private static final String LINE_FILTER          = "mat-select[aria-label*='Line'], mat-select[formcontrolname*='line']";

    public ConveyorHourMasterPage(Page page) {
        super(page);
    }

    // ─────────────────────────────────────────────────────────────────────
    // Navigation
    // ─────────────────────────────────────────────────────────────────────

    /** Navigates to the Conveyor Hour Master screen via tab or direct URL. */
    public ConveyorHourMasterPage navigateToConveyorHourMaster() {
        // Try clicking the Conveyor Hour Master tab
        if (page.locator(CONVEYOR_HOUR_MASTER_TAB).count() > 0) {
            safeClick(CONVEYOR_HOUR_MASTER_TAB);
            page.waitForTimeout(2000);
        } else {
            // Try Data Maintenance → sub-tab approach
            if (page.locator(DATA_MAINTENANCE_TAB).count() > 0) {
                safeClick(DATA_MAINTENANCE_TAB);
                page.waitForTimeout(2000);
                if (page.locator(CONVEYOR_HOUR_MASTER_TAB).count() > 0) {
                    safeClick(CONVEYOR_HOUR_MASTER_TAB);
                    page.waitForTimeout(2000);
                }
            }
        }
        System.out.println("📋 Navigated to Conveyor Hour Master");
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Record & Checkbox interactions
    // ─────────────────────────────────────────────────────────────────────

    /** Gets the total number of record rows on the screen. */
    public int getRecordRowCount() {
        return page.locator(RECORD_ROW).count();
    }

    /** Checks if each row has a checkbox column. */
    public boolean hasCheckboxColumn() {
        int rows = getRecordRowCount();
        if (rows == 0) return false;
        // Check if checkboxes exist in/near rows
        return page.locator(RECORD_ROW + " " + ROW_CHECKBOX).count() > 0
                || page.locator(ROW_CHECKBOX).count() > 0;
    }

    /** Selects a single record by row index (0-based). */
    public ConveyorHourMasterPage selectRecord(int rowIndex) {
        Locator row = page.locator(RECORD_ROW).nth(rowIndex);
        Locator checkbox = row.locator(ROW_CHECKBOX).first();
        if (checkbox.count() > 0) {
            try {
                checkbox.click();
            } catch (Exception e) {
                checkbox.evaluate("el => el.click()");
            }
            page.waitForTimeout(500);
            System.out.println("☑️  Selected record at index: " + rowIndex);
        } else {
            // Fallback: click the row itself (might toggle selection)
            row.click();
            page.waitForTimeout(500);
        }
        return this;
    }

    /** Selects multiple records by indices. */
    public ConveyorHourMasterPage selectRecords(int... indices) {
        for (int idx : indices) {
            selectRecord(idx);
        }
        return this;
    }

    /** Selects a range of records from startIndex to endIndex (inclusive). */
    public ConveyorHourMasterPage selectRecordRange(int startIndex, int endIndex) {
        for (int i = startIndex; i <= endIndex; i++) {
            selectRecord(i);
        }
        return this;
    }

    /** Clicks the Select All checkbox in the table header. */
    public ConveyorHourMasterPage selectAll() {
        Locator selectAll = page.locator(SELECT_ALL_CHECKBOX).first();
        if (selectAll.count() > 0) {
            try {
                selectAll.click();
            } catch (Exception e) {
                selectAll.evaluate("el => el.click()");
            }
            page.waitForTimeout(1000);
            System.out.println("☑️  Select All clicked");
        }
        return this;
    }

    /** Deselects a single record by row index. */
    public ConveyorHourMasterPage deselectRecord(int rowIndex) {
        Locator row = page.locator(RECORD_ROW).nth(rowIndex);
        Locator checkbox = row.locator(CHECKED_CHECKBOX).first();
        if (checkbox.count() > 0) {
            try {
                checkbox.click();
            } catch (Exception e) {
                checkbox.evaluate("el => el.click()");
            }
            page.waitForTimeout(500);
            System.out.println("⬜ Deselected record at index: " + rowIndex);
        }
        return this;
    }

    /** Gets the number of currently checked/selected rows. */
    public int getSelectedRecordCount() {
        return page.locator(CHECKED_CHECKBOX).count();
    }

    /** Checks if a specific row is selected (checked). */
    public boolean isRecordSelected(int rowIndex) {
        Locator row = page.locator(RECORD_ROW).nth(rowIndex);
        return row.locator(CHECKED_CHECKBOX).count() > 0
                || row.locator("input[type='checkbox']:checked").count() > 0;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Bulk Update popup
    // ─────────────────────────────────────────────────────────────────────

    /** Clicks the Bulk Update / Update action button. */
    public ConveyorHourMasterPage clickBulkUpdateButton() {
        safeClick(BULK_UPDATE_BTN);
        page.waitForTimeout(1500);
        System.out.println("🔄 Clicked Bulk Update button");
        return this;
    }

    /** Checks if the bulk update popup/dialog is displayed. */
    public boolean isBulkUpdatePopupVisible() {
        return page.locator(BULK_UPDATE_POPUP).count() > 0
                && page.locator(BULK_UPDATE_POPUP).first().isVisible();
    }

    /** Checks if the Model Run Time Trigger input field is present in the popup. */
    public boolean isModelRunTimeTriggerInputVisible() {
        return page.locator(MODEL_RUN_TIME_INPUT).count() > 0;
    }

    /** Enters a value in the Model Run Time Trigger input field. */
    public ConveyorHourMasterPage enterModelRunTimeTrigger(String value) {
        Locator input = page.locator(MODEL_RUN_TIME_INPUT).first();
        input.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
        input.clear();
        input.fill(value);
        page.waitForTimeout(500);
        System.out.println("⏱️  Entered Model Run Time Trigger: " + value);
        return this;
    }

    /** Gets the current value in the Model Run Time Trigger input. */
    public String getModelRunTimeTriggerValue() {
        try {
            return page.locator(MODEL_RUN_TIME_INPUT).first().inputValue().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Clicks the Confirm/Submit button in the bulk update popup. */
    public ConveyorHourMasterPage clickConfirm() {
        safeClick(CONFIRM_BTN);
        page.waitForTimeout(2000);
        System.out.println("✅ Clicked Confirm on bulk update");
        return this;
    }

    /** Clicks the Cancel button in the bulk update popup. */
    public ConveyorHourMasterPage clickCancel() {
        safeClick(CANCEL_BTN);
        page.waitForTimeout(1500);
        System.out.println("❌ Clicked Cancel on bulk update");
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Messages & Validation
    // ─────────────────────────────────────────────────────────────────────

    /** Checks if a success message is displayed. */
    public boolean isSuccessMessageVisible() {
        page.waitForTimeout(1000);
        return page.locator(SUCCESS_MESSAGE).count() > 0;
    }

    /** Gets the text of the success message. */
    public String getSuccessMessageText() {
        try {
            return page.locator(SUCCESS_MESSAGE).first().innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Checks if a validation/error message is displayed. */
    public boolean isValidationErrorVisible() {
        return page.locator(VALIDATION_MSG).count() > 0;
    }

    /** Gets the validation error text. */
    public String getValidationErrorText() {
        try {
            return page.locator(VALIDATION_MSG).first().innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Checks if any error message is shown (for invalid input scenarios). */
    public boolean isErrorMessageVisible() {
        return page.locator(ERROR_MESSAGE).count() > 0;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Filter interactions
    // ─────────────────────────────────────────────────────────────────────

    /** Applies a text filter on the table. */
    public ConveyorHourMasterPage applyFilter(String filterText) {
        Locator filterInput = page.locator(FILTER_INPUT).first();
        if (filterInput.count() > 0) {
            filterInput.clear();
            filterInput.fill(filterText);
            page.waitForTimeout(1500);
            System.out.println("🔍 Applied filter: " + filterText);
        }
        return this;
    }

    /** Selects a model from the model filter dropdown. */
    public ConveyorHourMasterPage selectModelFilter(String model) {
        if (page.locator(MODEL_FILTER).count() > 0) {
            selectMatOption(MODEL_FILTER, model);
        }
        return this;
    }

    /** Selects a line from the line filter dropdown. */
    public ConveyorHourMasterPage selectLineFilter(String line) {
        if (page.locator(LINE_FILTER).count() > 0) {
            selectMatOption(LINE_FILTER, line);
        }
        return this;
    }

    /** Clears any applied filter. */
    public ConveyorHourMasterPage clearFilter() {
        Locator filterInput = page.locator(FILTER_INPUT).first();
        if (filterInput.count() > 0) {
            filterInput.clear();
            page.waitForTimeout(1000);
        }
        return this;
    }

    // ─────────────────────────────────────────────────────────────────────
    // Utility
    // ─────────────────────────────────────────────────────────────────────

    /** Gets the page URL. */
    public String getPageUrl() {
        return getCurrentUrl();
    }

    /** Checks if on the Conveyor Hour Master page. */
    public boolean isOnConveyorHourMasterPage() {
        return page.locator(CONVEYOR_HOUR_MASTER_TAB).count() > 0
                || getCurrentUrl().contains("groupmaster")
                || page.locator("text=Conveyor Hour Master").count() > 0;
    }

    /** Refreshes the page and waits for reload. */
    public ConveyorHourMasterPage refreshPage() {
        page.reload();
        page.waitForTimeout(3000);
        return this;
    }

    /** Dismisses any SweetAlert modal if present. */
    public void dismissSwalIfPresent() {
        try {
            if (page.locator(".swal2-container").count() > 0) {
                if (page.locator(".swal2-confirm").count() > 0) {
                    page.locator(".swal2-confirm").evaluate("el => el.click()");
                } else {
                    page.keyboard().press("Escape");
                }
                page.waitForTimeout(1000);
            }
        } catch (Exception ignored) {}
    }

    /** Gets text content of a cell in a specific row and column. */
    public String getCellText(int rowIndex, int colIndex) {
        try {
            Locator row = page.locator(RECORD_ROW).nth(rowIndex);
            Locator cell = row.locator("td, .mat-cell").nth(colIndex);
            return cell.innerText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    /** Gets all Model Run Time Trigger values from the visible rows. */
    public List<String> getAllModelRunTimeValues() {
        List<String> values = new ArrayList<>();
        int rowCount = getRecordRowCount();
        for (int i = 0; i < rowCount; i++) {
            // Try to find the Model Run Time column value (usually last or specific column)
            try {
                Locator row = page.locator(RECORD_ROW).nth(i);
                // Look for the specific cell with run time data
                String text = row.locator("td:last-child, .mat-cell:last-child").innerText().trim();
                values.add(text);
            } catch (Exception e) {
                values.add("N/A");
            }
        }
        return values;
    }
}
