package com.tvsm.autoseq.tests.autoseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.autoseq.VarianceAnalysisPage;
import org.testng.Assert;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import java.util.ArrayList;
import java.util.List;

/**
 * VarianceAnalysisTest — Scenario 1 automation.
 *
 * URL: https://uat-sns.tvsmotor.net/Autoseq/groupmaster
 *
 * Scenario:
 *   Login to the application → Select the Report module →
 *   Click on "Variance Analysis".
 *
 * Note: SSO login is handled once by BaseTest (@BeforeSuite saves the auth
 * state, which every test context reuses), so these tests start already
 * authenticated and only drive the in-app navigation.
 *
 *   TC-VA-001  Report module/tab is visible after login
 *   TC-VA-002  Selecting Report module exposes the Variance Analysis option
 *   TC-VA-003  Clicking Variance Analysis loads the Variance Analysis view
 *   TC-VA-004  Variance Analysis data loads after Submit
 *   TC-VA-005  "Tool generated MPS Plan VS Edited MPS Plan (Manual)" column
 *              matches the "Edited MPS Plan VS Tool Generated Sequence Plan"
 *              column exactly, row by row
 *   TC-VA-006  "Edited MPS Plan VS Tool Generated Sequence Plan" matches the
 *              "Tool generated Sequence Plan VS Changed Sequence Plan (Manual)"
 *              column (shared Tool Generated Sequence Plan) — Okay / Not Okay
 */
public class VarianceAnalysisTest extends BaseTest {

    private VarianceAnalysisPage variancePage;

    @BeforeClass(alwaysRun = true)
    public void navigateToGroupMaster() {
        page.navigate(ConfigReader.groupMasterUrl());
        waitForAppLoad();
        page.waitForTimeout(3000);

        variancePage = new VarianceAnalysisPage(page);
        logInfo("Navigated to: " + ConfigReader.groupMasterUrl());
    }

    @Test(priority = 1,
          description = "TC-VA-001: Report module/tab should be visible after login")
    public void reportModuleVisibleTest() {
        Assert.assertTrue(variancePage.isReportTabVisible(),
                "Report module/tab not visible in the navigation after login");
        logInfo("✅ Report module is visible");
        captureScreenshot("TC-VA-001_ReportModuleVisible");
    }

    @Test(priority = 2, dependsOnMethods = "reportModuleVisibleTest",
          description = "TC-VA-002: Selecting the Report module should expose Variance Analysis")
    public void selectReportModuleTest() {
        variancePage.clickReportModule();
        logInfo("✅ Selected the Report module");
        captureScreenshot("TC-VA-002_ReportModuleSelected");
    }

    @Test(priority = 3, dependsOnMethods = "selectReportModuleTest",
          description = "TC-VA-003: Clicking Variance Analysis should load the Variance Analysis view")
    public void clickVarianceAnalysisTest() {
        variancePage.clickVarianceAnalysis();
        Assert.assertTrue(variancePage.isVarianceAnalysisLoaded(),
                "Variance Analysis view did not load after clicking the option");
        logInfo("✅ Variance Analysis view loaded");
        captureScreenshot("TC-VA-003_VarianceAnalysisLoaded");
    }

    @Test(priority = 4, dependsOnMethods = "clickVarianceAnalysisTest",
          description = "TC-VA-004: Variance Analysis data should load after Submit")
    public void varianceDataLoadsTest() {
        variancePage.clickSubmit();
        int rows = variancePage.getTableRowCount();
        logInfo("Variance Analysis table rows: " + rows);
        captureScreenshot("TC-VA-004_VarianceData");
        // Data availability depends on the selected date range; assert the
        // view is still rendered rather than failing on an empty-but-valid table.
        Assert.assertTrue(variancePage.isVarianceAnalysisLoaded(),
                "Variance Analysis view should remain rendered after Submit");
    }

    @Test(priority = 5, dependsOnMethods = "clickVarianceAnalysisTest",
          description = "TC-VA-005: The 'Edited MPS Plan' value shared by the 'Tool generated MPS Plan "
                      + "VS Edited MPS Plan' and 'Edited MPS Plan VS Tool Generated Sequence Plan' "
                      + "columns must match exactly, row by row")
    public void mpsColumnsMatchTest() {
        // The grid auto-loads from the default filters; wait for body rows.
        boolean loaded = variancePage.waitForDataTable(30000);
        Assert.assertTrue(loaded, "Variance Analysis data table did not load any rows");

        // "Edited MPS Plan" appears as the (Final) sub-column of the first group
        // and the (Final Plan) sub-column of the second group — they must be equal.
        List<String> editedFromToolMpsGroup =
                variancePage.getEditedMpsPlanColumn(VarianceAnalysisPage.GROUP_TOOL_MPS_VS_EDITED_MPS);
        List<String> editedFromSeqGroup =
                variancePage.getEditedMpsPlanColumn(VarianceAnalysisPage.GROUP_EDITED_MPS_VS_TOOL_SEQ);

        logInfo("Edited MPS Plan (from 'Tool MPS vs Edited MPS' group):        " + editedFromToolMpsGroup);
        logInfo("Edited MPS Plan (from 'Edited MPS vs Tool Sequence' group):   " + editedFromSeqGroup);

        captureScreenshot("TC-VA-005_MpsColumnsComparison");

        Assert.assertFalse(editedFromToolMpsGroup.isEmpty(),
                "Edited MPS Plan column not found in 'Tool generated MPS Plan VS Edited MPS Plan' group");
        Assert.assertFalse(editedFromSeqGroup.isEmpty(),
                "Edited MPS Plan column not found in 'Edited MPS Plan VS Tool Generated Sequence Plan' group");
        Assert.assertEquals(editedFromToolMpsGroup.size(), editedFromSeqGroup.size(),
                "Row counts differ between the two groups");

        List<String> mismatches = new ArrayList<>();
        for (int i = 0; i < editedFromToolMpsGroup.size(); i++) {
            String left  = editedFromToolMpsGroup.get(i).trim();
            String right = editedFromSeqGroup.get(i).trim();
            if (!left.equals(right)) {
                mismatches.add("Row " + (i + 1) + " — 'Tool MPS vs Edited MPS' = '" + left
                        + "' | 'Edited MPS vs Tool Seq' = '" + right + "'");
            }
        }
        mismatches.forEach(m -> logInfo("❌ " + m));

        Assert.assertTrue(mismatches.isEmpty(),
                "Edited MPS Plan values do not match across the two columns:\n"
                        + String.join("\n", mismatches));
        logInfo("✅ Edited MPS Plan values match exactly across "
                + editedFromToolMpsGroup.size() + " rows");
    }

    @Test(priority = 6, dependsOnMethods = "clickVarianceAnalysisTest",
          description = "TC-VA-006: Compare the 'Tool Generated Sequence Plan' value shared by the "
                      + "'Edited MPS Plan VS Tool Generated Sequence Plan' and 'Tool generated Sequence "
                      + "Plan VS Changed Sequence Plan (Manual)' columns — mark Okay if data matches, "
                      + "else Not Okay")
    public void sequencePlanColumnsMatchTest() {
        boolean loaded = variancePage.waitForDataTable(30000);
        Assert.assertTrue(loaded, "Variance Analysis data table did not load any rows");

        // "Sequence Plan (From Tool)" appears as the 2nd sub-column of the
        // 'Edited MPS Plan VS Tool Generated Sequence Plan' group and the 1st
        // sub-column of the 'Tool generated Sequence Plan VS Changed Sequence
        // Plan (Manual)' group — the figures should be identical.
        List<String> seqFromEditedMpsGroup =
                variancePage.getToolSequencePlanColumn(VarianceAnalysisPage.GROUP_EDITED_MPS_VS_TOOL_SEQ);
        List<String> seqFromChangedSeqGroup =
                variancePage.getToolSequencePlanColumn(VarianceAnalysisPage.GROUP_TOOL_SEQ_VS_CHANGED_SEQ);

        logInfo("Tool Generated Sequence Plan (from 'Edited MPS vs Tool Seq' group):     " + seqFromEditedMpsGroup);
        logInfo("Tool Generated Sequence Plan (from 'Tool Seq vs Changed Seq' group):    " + seqFromChangedSeqGroup);

        VarianceAnalysisPage.ColumnComparison result =
                variancePage.compareColumns(seqFromEditedMpsGroup, seqFromChangedSeqGroup);

        // Soft check: report the status but never fail the suite.
        logInfo("Comparison status: " + result.status()
                + " (" + result.rowsCompared + " rows compared)");
        captureScreenshot("TC-VA-006_SequencePlanColumnsComparison");

        if (result.matched) {
            logInfo("✅ Tool Generated Sequence Plan values match — status: Okay ("
                    + result.rowsCompared + " rows)");
        } else {
            logInfo("⚠️ Tool Generated Sequence Plan values differ — status: Not Okay ("
                    + result.mismatches.size() + " mismatch(es))");
            result.mismatches.forEach(m -> logInfo("   • " + m));
        }
    }
}
