package com.tvsm.autoseq.pages.manualseq;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.tvsm.autoseq.base.BasePage;
import com.tvsm.autoseq.config.ConfigReader;

import java.util.ArrayList;
import java.util.List;

/**
 * ManualSeqSequenceLivePage — Page Object for the Sequence Live / Group Master screen.
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster
 *
 * Covers: filter dropdowns, date input, search, sequence rows, filter tabs,
 * live indicator, last run banner, run re-sequence button, and status badges.
 */
public class ManualSeqSequenceLivePage extends BasePage {

    // ── Selectors ─────────────────────────────────────────────────────────

    // Filter dropdowns (Angular Material mat-select)
    private static final String MAT_SELECT = "mat-select";

    // Date input
    private static final String DATE_INPUT = "input[matinput], input[type='text']";

    // Search input
    private static final String[] SEARCH_SELECTORS = {
        "input[placeholder='Search']",
        "input[placeholder*='Search' i]",
        "input[placeholder*='search' i]",
        "input[type='search']",
        "input[matinput][placeholder*='earch']",
        "mat-form-field input[type='text']",
        "mat-form-field input",
        ".search-input input",
        "input.mat-input-element",
        "input[aria-label*='earch']"
    };

    // Sequence rows
    private static final String[] ROW_SELECTORS = {
        ".seq-card-row", ".sequence-card", ".card-container",
        "table tbody tr", ".cdk-row", "mat-row",
        "[class*='row']"
    };

    // Filter tabs
    private static final String FILTER_ALL        = "button:has-text('All'), [role='tab']:has-text('All')";
    private static final String FILTER_COMPLETED  = "button:has-text('Completed'), [role='tab']:has-text('Completed')";
    private static final String FILTER_UPCOMING   = "button:has-text('Upcoming'), [role='tab']:has-text('Upcoming')";
    private static final String FILTER_PARTIAL    = "button:has-text('Partial'), [role='tab']:has-text('Partial')";
    private static final String FILTER_INCOMPLETE = "button:has-text('InComplete'), button:has-text('In Complete'), [role='tab']:has-text('InComplete')";

    // UI elements
    private static final String LIVE_INDICATOR       = ".live-indicator, .live-badge, :text('LIVE'), :text('Live')";
    private static final String LAST_RUN_BANNER      = ":text('Last Run'), :text('last run'), .last-run";
    private static final String RUN_RESEQUENCE_BTN   = "button:has-text('Run Re-Sequence'), button:has-text('Re-Sequence'), button:has-text('Resequence')";
    private static final String PRODUCTION_PLAN_SECTION = ":text('Production Sequence Plan'), :text('Sequence Plan'), .production-plan";

    public ManualSeqSequenceLivePage(Page page) {
        super(page);
    }

    // ── Navigation ────────────────────────────────────────────────────────

    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.manualSeqGroupMasterUrl());
        System.out.println("🌐 Navigated to: " + ConfigReader.manualSeqGroupMasterUrl());
    }

    public String getPageUrl() {
        return page.url();
    }

    public boolean isOnGroupMasterPage() {
        return page.url().contains("groupmaster") || page.url().contains("SequencePlanTest");
    }

    // ── Filter Dropdowns ──────────────────────────────────────────────────

    public int getMatSelectCount() {
        return page.locator(MAT_SELECT).count();
    }

    public boolean isPlantDropdownPresent() {
        return getMatSelectCount() >= 1;
    }

    public boolean isUnitDropdownPresent() {
        return getMatSelectCount() >= 2;
    }

    public boolean isConveyorDropdownPresent() {
        return getMatSelectCount() >= 3;
    }

    public boolean isShiftDropdownPresent() {
        return getMatSelectCount() >= 4;
    }

    public void selectPlant(String plantName) {
        if (page.locator(MAT_SELECT).count() >= 1) {
            selectMatOption(MAT_SELECT + ":nth-of-type(1)", plantName);
        }
    }

    public void selectUnit(String unitName) {
        if (page.locator(MAT_SELECT).count() >= 2) {
            page.locator(MAT_SELECT).nth(1).click();
            page.waitForTimeout(500);
            page.locator("mat-option:has-text(\"" + unitName + "\")").first().click();
        }
    }

    public void selectConveyor(String conveyorName) {
        if (page.locator(MAT_SELECT).count() >= 3) {
            page.locator(MAT_SELECT).nth(2).click();
            page.waitForTimeout(500);
            page.locator("mat-option:has-text(\"" + conveyorName + "\")").first().click();
        }
    }

    public void selectShift(String shiftName) {
        if (page.locator(MAT_SELECT).count() >= 4) {
            page.locator(MAT_SELECT).nth(3).click();
            page.waitForTimeout(500);
            page.locator("mat-option:has-text(\"" + shiftName + "\")").first().click();
        }
    }

    public List<String> getShiftDropdownOptions() {
        List<String> options = new ArrayList<>();
        if (page.locator(MAT_SELECT).count() >= 4) {
            page.locator(MAT_SELECT).nth(3).click();
            page.waitForTimeout(1000);
            int count = page.locator("mat-option").count();
            for (int i = 0; i < count; i++) {
                options.add(page.locator("mat-option").nth(i).innerText().trim());
            }
            page.keyboard().press("Escape");
        }
        return options;
    }

    // ── Date Input ────────────────────────────────────────────────────────

    public void enterDate(String date) {
        Locator dateInput = page.locator(DATE_INPUT).first();
        dateInput.click();
        dateInput.clear();
        dateInput.fill(date);
        page.keyboard().press("Enter");
        System.out.println("📅 Entered date: " + date);
    }

    public String getDateFieldValue() {
        return page.locator(DATE_INPUT).first().inputValue();
    }

    public boolean isDateFieldVisible() {
        return page.locator(DATE_INPUT).count() > 0;
    }

    // ── Search ────────────────────────────────────────────────────────────

    public boolean isSearchInputVisible() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) return true;
        }
        return false;
    }

    public void search(String keyword) {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                Locator input = page.locator(sel).first();
                input.click();
                input.clear();
                // Type character-by-character so that debounce/autocomplete handlers
                // don't drop trailing characters as the DOM re-renders.
                input.pressSequentially(keyword, new Locator.PressSequentiallyOptions().setDelay(80));
                page.waitForTimeout(800);

                // Verify the full value is present; if truncated, fall back to fill()
                String actual = input.inputValue();
                if (!keyword.equals(actual)) {
                    System.out.println("⚠️  Typed value '" + actual + "' != expected '" + keyword + "'. Re-filling.");
                    input.click();
                    input.fill("");
                    input.fill(keyword);
                    input.dispatchEvent("input");
                    page.waitForTimeout(500);
                }
                System.out.println("🔍 Searched: " + keyword);
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

    public void clearSearch() {
        for (String sel : SEARCH_SELECTORS) {
            if (page.locator(sel).count() > 0) {
                Locator input = page.locator(sel).first();
                input.click();
                input.clear();
                System.out.println("🧹 Search cleared");
                return;
            }
        }
    }

    // ── Sequence Rows ─────────────────────────────────────────────────────

    public int getSequenceRowCount() {
        for (String sel : ROW_SELECTORS) {
            int count = page.locator(sel).count();
            if (count > 0) return count;
        }
        return 0;
    }

    public List<String> getAllRowStatuses() {
        List<String> statuses = new ArrayList<>();
        String[] badgeSelectors = {".status-badge", ".badge", "[class*='status']"};
        for (String sel : badgeSelectors) {
            int count = page.locator(sel).count();
            if (count > 0) {
                for (int i = 0; i < count; i++) {
                    statuses.add(page.locator(sel).nth(i).innerText().trim());
                }
                break;
            }
        }
        return statuses;
    }

    // ── Filter Tabs ───────────────────────────────────────────────────────

    public void clickFilterAll() {
        clickFilterButton(FILTER_ALL, "All");
    }

    public void clickFilterCompleted() {
        clickFilterButton(FILTER_COMPLETED, "Completed");
    }

    public void clickFilterUpcoming() {
        clickFilterButton(FILTER_UPCOMING, "Upcoming");
    }

    public void clickFilterPartial() {
        clickFilterButton(FILTER_PARTIAL, "Partial");
    }

    public void clickFilterIncomplete() {
        clickFilterButton(FILTER_INCOMPLETE, "InComplete");
    }

    public boolean isFilterTabVisible(String tabText) {
        return page.locator("button:has-text('" + tabText + "'), [role='tab']:has-text('" + tabText + "')").count() > 0;
    }

    private void clickFilterButton(String selector, String name) {
        try {
            Locator btn = page.locator(selector).first();
            btn.click();
            System.out.println("✅ Clicked filter: " + name);
        } catch (Exception e) {
            System.out.println("⚠️  Filter button not found: " + name);
        }
    }

    // ── UI Elements ───────────────────────────────────────────────────────

    public boolean isProductionPlanSectionVisible() {
        return page.locator(PRODUCTION_PLAN_SECTION).count() > 0;
    }

    public boolean isLiveIndicatorVisible() {
        return page.locator(LIVE_INDICATOR).count() > 0;
    }

    public boolean isLastRunBannerVisible() {
        return page.locator(LAST_RUN_BANNER).count() > 0;
    }

    public String getLastRunText() {
        if (isLastRunBannerVisible()) {
            return page.locator(LAST_RUN_BANNER).first().innerText().trim();
        }
        return "";
    }

    public boolean isRunReSequenceButtonVisible() {
        return page.locator(RUN_RESEQUENCE_BTN).count() > 0;
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    public void dismissSwalIfPresent() {
        try {
            if (page.locator(".swal2-container").count() > 0) {
                if (page.locator(".swal2-confirm").count() > 0) {
                    page.locator(".swal2-confirm").evaluate("el => el.click()");
                } else if (page.locator(".swal2-close").count() > 0) {
                    page.locator(".swal2-close").evaluate("el => el.click()");
                } else {
                    page.keyboard().press("Escape");
                }
                page.waitForTimeout(1000);
            }
        } catch (Exception ignored) {}
    }
}
