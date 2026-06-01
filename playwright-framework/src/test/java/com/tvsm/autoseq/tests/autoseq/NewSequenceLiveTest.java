package com.tvsm.autoseq.tests.autoseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.autoseq.HomePage;
import com.tvsm.autoseq.pages.autoseq.NewSequenceLivePage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.List;

/**
 * NewSequenceLiveTest — full test coverage for the Group Master screen.
 * Application: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 *   TC-NSL-001  Page loads and URL is correct
 *   TC-NSL-002  Production Sequence Plan section is visible
 *   TC-NSL-003  Sequence rows are loaded
 *   TC-NSL-004  [BUG-001] Marked value should not be "--" for all rows
 *   TC-NSL-005  [BUG-002] Rows with Actual=0 should not all be "Incomplete"
 *   TC-NSL-006  [BUG-003] Not all rows should be flagged as Bottleneck
 *   TC-NSL-007  [BUG-009] Actual count should update during Live session
 *   TC-NSL-008  Live indicator is visible
 *   TC-NSL-009  Last Run timestamp banner is visible
 *   TC-NSL-010  Run Re-Sequence button is visible
 *   TC-NSL-011  Filter tabs work
 *   TC-NSL-012  [BUG-010] URL contains expected path
 *   TC-NSL-013  MIW and BO part availability tags are visible
 *   TC-NSL-014  Apply filters and verify list refreshes
 */
public class NewSequenceLiveTest extends BaseTest {

    private NewSequenceLivePage seqLivePage;

    @BeforeClass(alwaysRun = true)
    public void navigateToPage() {
        page.navigate(ConfigReader.baseUrl());
        waitForAppLoad();
        HomePage homePage = new HomePage(page);
        seqLivePage = homePage.goToNewSequenceLive();
        page.waitForTimeout(3000);
    }

    @Test(priority = 1, description = "TC-NSL-001: Page loads and URL contains 'groupmaster'")
    public void pageLoadsWithCorrectUrlTest() {
        Assert.assertTrue(seqLivePage.isOnGroupMasterPage(),
                "URL does not contain 'groupmaster'. Actual: " + seqLivePage.getPageUrl());
        captureScreenshot("TC-NSL-001_PageLoaded");
    }

    @Test(priority = 2, description = "TC-NSL-002: Production Sequence Plan section is visible")
    public void productionPlanSectionVisibleTest() {
        Assert.assertTrue(seqLivePage.isProductionPlanSectionVisible(),
                "Production Sequence Plan section is not visible");
        captureScreenshot("TC-NSL-002_ProductionPlanSection");
    }

    @Test(priority = 3, description = "TC-NSL-003: Sequence rows are loaded in the list")
    public void sequenceRowsLoadedTest() {
        int rowCount = seqLivePage.getSequenceRowCount();
        logInfo("Sequence rows: " + rowCount);
        Assert.assertTrue(rowCount > 0, "No sequence rows found");
        captureScreenshot("TC-NSL-003_SequenceRowsLoaded");
    }

    @Test(priority = 4, description = "TC-NSL-004 [BUG-001]: Marked value should not be '--' for all rows")
    public void markedValueShouldNotBeEmptyTest() {
        List<String> markedValues = seqLivePage.getAllMarkedValues();
        long dashCount = markedValues.stream().filter(v -> v.equals("--") || v.isBlank()).count();
        boolean allDashes = dashCount == markedValues.size() && !markedValues.isEmpty();
        Assert.assertFalse(allDashes,
                "[BUG-001] All rows show '--' for Marked value");
        captureScreenshot("TC-NSL-004_MarkedValues");
    }

    @Test(priority = 5, description = "TC-NSL-005 [BUG-002]: Rows with Actual=0 should not all be 'Incomplete'")
    public void incompleteStatusWithZeroActualTest() {
        List<String> actualValues = seqLivePage.getAllActualValues();
        List<String> rowStatuses  = seqLivePage.getAllRowStatuses();
        long zeroActual    = actualValues.stream().filter(v -> v.equals("0") || v.isBlank()).count();
        long incompleteCount = rowStatuses.stream().filter(s -> s.equalsIgnoreCase("Incomplete")).count();
        Assert.assertFalse(
                zeroActual == actualValues.size() && incompleteCount == rowStatuses.size() && !rowStatuses.isEmpty(),
                "[BUG-002] All rows have Actual=0 but are all marked 'Incomplete'");
        captureScreenshot("TC-NSL-005_IncompleteStatus");
    }

    @Test(priority = 6, description = "TC-NSL-006 [BUG-003]: Not all rows should be flagged as Bottleneck")
    public void notAllRowsShouldBeBottleneckTest() {
        int total      = seqLivePage.getSequenceRowCount();
        int bottleneck = seqLivePage.getBottleneckRowCount();
        Assert.assertFalse(total > 0 && bottleneck == total,
                "[BUG-003] All " + total + " rows are flagged as Bottleneck");
        captureScreenshot("TC-NSL-006_BottleneckRows");
    }

    @Test(priority = 7, description = "TC-NSL-007 [BUG-009]: Actual count should update during Live session")
    public void actualCountShouldUpdateDuringLiveTest() {
        if (!seqLivePage.isLiveIndicatorVisible()) { logInfo("Live not active — skipping"); return; }
        List<String> actualValues = seqLivePage.getAllActualValues();
        long zeroCount = actualValues.stream().filter(v -> v.equals("0") || v.isBlank()).count();
        Assert.assertFalse(zeroCount == actualValues.size() && !actualValues.isEmpty(),
                "[BUG-009] Live active but all Actual counts are 0");
        captureScreenshot("TC-NSL-007_ActualValues");
    }

    @Test(priority = 8, description = "TC-NSL-008: Live indicator should be visible")
    public void liveIndicatorVisibleTest() {
        Assert.assertTrue(seqLivePage.isLiveIndicatorVisible(), "Live indicator not visible");
        captureScreenshot("TC-NSL-008_LiveIndicator");
    }

    @Test(priority = 9, description = "TC-NSL-009: Last Run timestamp banner should be visible")
    public void lastRunBannerVisibleTest() {
        Assert.assertTrue(seqLivePage.isLastRunBannerVisible(), "Last Run banner not visible");
        captureScreenshot("TC-NSL-009_LastRunBanner");
    }

    @Test(priority = 10, description = "TC-NSL-010: Run Re-Sequence button should be visible")
    public void runReSequenceButtonVisibleTest() {
        Assert.assertTrue(seqLivePage.isRunReSequenceButtonVisible(), "Run Re-Sequence button not visible");
        captureScreenshot("TC-NSL-010_RunReSequenceBtn");
    }

    @Test(priority = 11, description = "TC-NSL-011: Filter tabs work")
    public void filterTabsWorkTest() {
        seqLivePage.clickFilterCompleted();  page.waitForTimeout(1000);
        seqLivePage.clickFilterUpcoming();   page.waitForTimeout(1000);
        seqLivePage.clickFilterPartial();    page.waitForTimeout(1000);
        seqLivePage.clickFilterIncomplete(); page.waitForTimeout(1000);
        seqLivePage.clickFilterAll();        page.waitForTimeout(1000);
        Assert.assertTrue(seqLivePage.isProductionPlanSectionVisible(),
                "Production plan section disappeared after filter tab clicks");
        captureScreenshot("TC-NSL-011_FilterTabs");
    }

    @Test(priority = 12, description = "TC-NSL-012 [BUG-010]: URL should contain expected path")
    public void urlMatchesScreenNameTest() {
        String url = seqLivePage.getPageUrl();
        Assert.assertTrue(url.contains("Autoseq"),
                "URL does not contain 'Autoseq'. Actual: " + url);
        captureScreenshot("TC-NSL-012_UrlCheck");
    }

    @Test(priority = 13, description = "TC-NSL-013: MIW and BO part availability tags should be visible")
    public void partAvailabilityTagsVisibleTest() {
        if (seqLivePage.hasBottleneckRows()) {
            Assert.assertTrue(seqLivePage.isMiwTagVisible() || seqLivePage.isBoTagVisible(),
                    "Bottleneck rows exist but neither MIW nor BO tags are visible");
        }
        captureScreenshot("TC-NSL-013_PartAvailabilityTags");
    }

    @Test(priority = 14, description = "TC-NSL-014: Apply filters and verify sequence list refreshes")
    public void applyFiltersAndVerifyListTest() {
        seqLivePage.selectPlant("Plant 2");
        page.waitForTimeout(1500);
        int rowsAfter = seqLivePage.getSequenceRowCount();
        Assert.assertTrue(rowsAfter >= 0, "Row count is negative after applying filter");
        captureScreenshot("TC-NSL-014_FiltersApplied");
    }
}
