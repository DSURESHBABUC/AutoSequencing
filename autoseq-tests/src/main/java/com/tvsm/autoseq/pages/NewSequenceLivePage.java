package com.tvsm.autoseq.pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;
import com.tvsm.autoseq.base.BasePage;

import java.util.ArrayList;
import java.util.List;

/**
 * NewSequenceLivePage — Page Object for the Group Master / New Sequence Live screen.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 */
public class NewSequenceLivePage extends BasePage {

    private static final String DATE_INPUT_FIELD   = "input[placeholder='DD/MM/YYYY'], input[matinput][type='text']";
    private static final String DATE_PICKER_TOGGLE = "mat-datepicker-toggle button, button[aria-label='Open calendar']";
    private static final String DATE_PICKER_PANEL  = "mat-datepicker-content, .mat-datepicker-content";
    private static final String PLANT_DROPDOWN_SEL    = "mat-select[aria-label='Plant'], [aria-label='Plant']";
    private static final String UNIT_DROPDOWN_SEL     = "mat-select[aria-label='VA'], mat-select[aria-label='Unit']";
    private static final String CONVEYOR_DROPDOWN_SEL = "mat-select[aria-label='2VCON300'], mat-select[aria-label='Conveyor']";
    private static final String SHIFT_DROPDOWN_SEL    = "mat-select[aria-label='ALL'], mat-select[aria-label='Shift']";
    private static final String SEARCH_INPUT = "input[placeholder='Search']";
    private static final String LIVE_INDICATOR      = ".live-dot, .live-status, span.live-label";
    private static final String LAST_RUN_BANNER     = ".last-run-info, .sequence-last-run";
    private static final String RUN_RE_SEQUENCE_BTN = "button:has-text('Run Re-Sequence')";
    private static final String SEQ_ROWS         = ".seq-card-row, .sequence-card, .card-container";
    private static final String SEQ_STATUS_BADGE = ".status-badge, .incomplete-badge";
    private static final String SEQ_BOTTLENECK   = ".bottleneck-info, .part-availability-status";
    private static final String SEQ_MARKED_VALUE = ".marked-count, .marked-value";
    private static final String SEQ_ACTUAL_VALUE = ".actual-count, .actual-value";
    private static final String SEQ_MIW_TAG      = ".bottleneck-badge";
    private static final String SEQ_BO_TAG       = ".bottleneck-badge";
    private static final String FILTER_ALL        = "button:has-text('All')";
    private static final String FILTER_COMPLETED  = "button:has-text('Completed')";
    private static final String FILTER_UPCOMING   = "button:has-text('Upcoming')";
    private static final String FILTER_PARTIAL    = "button:has-text('Partial')";
    private static final String FILTER_INCOMPLETE = "button:has-text('In Complete')";

    public NewSequenceLivePage(Page page) {
        super(page);
    }

    public NewSequenceLivePage navigateDirect() {
        page.navigate("https://uat-sns.tvsmotor.net/Autoseq/groupmaster");
        page.waitForLoadState();
        return this;
    }

    public NewSequenceLivePage enterDate(String date) {
        String[] candidates = {
            "input[placeholder='DD/MM/YYYY']",
            "input[matinput][type='text']",
            "input.mat-input-element[type='text']",
            "input[type='text']"
        };
        Locator dateField = null;
        for (String sel : candidates) {
            Locator loc = page.locator(sel).first();
            try {
                loc.waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE).setTimeout(5000));
                dateField = loc;
                break;
            } catch (Exception ignored) {}
        }
        if (dateField == null) throw new RuntimeException("Date input field not found");
        dateField.click(new Locator.ClickOptions().setClickCount(3));
        dateField.fill(date);
        dateField.press("Tab");
        System.out.println("📅 Date entered: " + date);
        return this;
    }

    public String getDateFieldValue() {
        try { return page.locator(DATE_INPUT_FIELD).first().inputValue().trim(); }
        catch (Exception e) { return ""; }
    }

    public NewSequenceLivePage search(String keyword) {
        Locator searchBox = page.locator(SEARCH_INPUT).first();
        searchBox.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(timeout));
        searchBox.click();
        searchBox.fill(keyword);
        page.waitForTimeout(1000);
        return this;
    }

    public NewSequenceLivePage clearSearch() {
        page.locator(SEARCH_INPUT).first().clear();
        page.waitForTimeout(1000);
        return this;
    }

    public String getSearchValue() {
        try { return page.locator(SEARCH_INPUT).first().inputValue().trim(); }
        catch (Exception e) { return ""; }
    }

    public NewSequenceLivePage selectPlant(String plant)     { selectMatOption(PLANT_DROPDOWN_SEL, plant);       return this; }
    public NewSequenceLivePage selectUnit(String unit)       { selectMatOption(UNIT_DROPDOWN_SEL, unit);         return this; }
    public NewSequenceLivePage selectConveyor(String conv)   { selectMatOption(CONVEYOR_DROPDOWN_SEL, conv);     return this; }
    public NewSequenceLivePage selectShift(String shift)     { selectMatOption(SHIFT_DROPDOWN_SEL, shift);       return this; }

    public NewSequenceLivePage clickFilterAll()        { safeClick(FILTER_ALL);        return this; }
    public NewSequenceLivePage clickFilterCompleted()  { safeClick(FILTER_COMPLETED);  return this; }
    public NewSequenceLivePage clickFilterUpcoming()   { safeClick(FILTER_UPCOMING);   return this; }
    public NewSequenceLivePage clickFilterPartial()    { safeClick(FILTER_PARTIAL);    return this; }
    public NewSequenceLivePage clickFilterIncomplete() { safeClick(FILTER_INCOMPLETE); return this; }

    public NewSequenceLivePage clickRunReSequence() {
        waitForClickable(RUN_RE_SEQUENCE_BTN).click();
        return this;
    }

    public boolean isRunReSequenceButtonVisible() { return page.locator(RUN_RE_SEQUENCE_BTN).count() > 0; }
    public boolean isLiveIndicatorVisible()       { return page.locator(LIVE_INDICATOR).count() > 0; }
    public boolean isLastRunBannerVisible()       { return page.locator(LAST_RUN_BANNER).count() > 0; }

    public String getLastRunText() {
        try { return page.locator(LAST_RUN_BANNER).first().innerText().trim(); }
        catch (Exception e) { return ""; }
    }

    public int getSequenceRowCount()   { return page.locator(SEQ_ROWS).count(); }
    public boolean hasBottleneckRows() { return page.locator(SEQ_BOTTLENECK).count() > 0; }
    public int getBottleneckRowCount() { return page.locator(SEQ_BOTTLENECK).count(); }
    public boolean isMiwTagVisible()   { return page.locator(SEQ_MIW_TAG).count() > 0; }
    public boolean isBoTagVisible()    { return page.locator(SEQ_BO_TAG).count() > 0; }

    public List<String> getAllActualValues() {
        List<String> values = new ArrayList<>();
        Locator rows = page.locator(SEQ_ROWS);
        for (int i = 0; i < rows.count(); i++) {
            try { values.add(rows.nth(i).locator(SEQ_ACTUAL_VALUE).innerText().trim()); }
            catch (Exception e) { values.add("N/A"); }
        }
        return values;
    }

    public List<String> getAllMarkedValues() {
        List<String> values = new ArrayList<>();
        Locator rows = page.locator(SEQ_ROWS);
        for (int i = 0; i < rows.count(); i++) {
            try { values.add(rows.nth(i).locator(SEQ_MARKED_VALUE).innerText().trim()); }
            catch (Exception e) { values.add("N/A"); }
        }
        return values;
    }

    public List<String> getAllRowStatuses() {
        List<String> statuses = new ArrayList<>();
        Locator badges = page.locator(SEQ_STATUS_BADGE);
        for (int i = 0; i < badges.count(); i++) {
            statuses.add(badges.nth(i).innerText().trim());
        }
        return statuses;
    }

    public boolean isProductionPlanSectionVisible() {
        return page.locator(".production-sequence-plan, .seq-plan-container, .sequence-plan-header").count() > 0
            || page.locator(".seq-card-row").count() > 0;
    }

    public boolean isOnGroupMasterPage() { return getCurrentUrl().contains("groupmaster"); }
    public String getPageUrl()           { return getCurrentUrl(); }
}
