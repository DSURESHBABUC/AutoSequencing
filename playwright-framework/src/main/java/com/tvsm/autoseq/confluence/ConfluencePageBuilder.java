package com.tvsm.autoseq.confluence;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * ConfluencePageBuilder — builds Confluence storage-format HTML
 * from a list of TestSuiteResult objects.
 *
 * Output includes:
 *  - Run metadata (date, environment, browser)
 *  - Overall summary panel (pass/fail/skip counts + pass rate)
 *  - Per-suite expandable sections with individual test rows
 *  - Bug report table (from the screenshot analysis)
 */
public class ConfluencePageBuilder {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm z");

    /**
     * Builds the full Confluence page HTML.
     *
     * @param suites  list of suite results collected by ConfluenceReporter
     * @param browser browser name used for the run
     * @param env     environment label e.g. "UAT"
     */
    public String build(List<TestSuiteResult> suites, String browser, String env) {
        String runTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(FMT);

        // Aggregate totals
        int totalPass = suites.stream().mapToInt(s -> s.passed).sum();
        int totalFail = suites.stream().mapToInt(s -> s.failed).sum();
        int totalSkip = suites.stream().mapToInt(s -> s.skipped).sum();
        int total     = totalPass + totalFail + totalSkip;
        int passRate  = total > 0 ? (int) Math.round(100.0 * totalPass / total) : 0;

        String panelColor = totalFail == 0 ? "green" : "red";

        StringBuilder sb = new StringBuilder();

        // ── Header info panel ─────────────────────────────────────────────
        sb.append("""
            <ac:structured-macro ac:name="info">
              <ac:parameter ac:name="title">Test Run Details</ac:parameter>
              <ac:rich-text-body>
                <p>
                  <strong>Application:</strong> TVS AutoSequencing (SNS) &nbsp;|&nbsp;
                  <strong>Environment:</strong> %s &nbsp;|&nbsp;
                  <strong>Browser:</strong> %s &nbsp;|&nbsp;
                  <strong>Run Time:</strong> %s
                </p>
              </ac:rich-text-body>
            </ac:structured-macro>
            """.formatted(env, browser, runTime));

        // ── Summary panel ─────────────────────────────────────────────────
        sb.append("""
            <ac:structured-macro ac:name="%s">
              <ac:parameter ac:name="title">Overall Test Summary</ac:parameter>
              <ac:rich-text-body>
                <table>
                  <tbody>
                    <tr>
                      <th>Total</th><th style="color:#006400">Passed</th>
                      <th style="color:#8B0000">Failed</th><th style="color:#FF8C00">Skipped</th>
                      <th>Pass Rate</th>
                    </tr>
                    <tr>
                      <td><strong>%d</strong></td>
                      <td><strong>%d</strong></td>
                      <td><strong>%d</strong></td>
                      <td><strong>%d</strong></td>
                      <td><strong>%d%%</strong></td>
                    </tr>
                  </tbody>
                </table>
              </ac:rich-text-body>
            </ac:structured-macro>
            """.formatted(panelColor, total, totalPass, totalFail, totalSkip, passRate));

        // ── Per-suite sections ────────────────────────────────────────────
        sb.append("<h2>Test Suite Results</h2>\n");

        for (TestSuiteResult suite : suites) {
            String suiteColor = suite.failed == 0 ? "#006400" : "#8B0000";
            String suiteStatus = suite.failed == 0 ? "✅ PASSED" : "❌ FAILED";

            // Expandable section per suite
            sb.append("""
                <ac:structured-macro ac:name="expand">
                  <ac:parameter ac:name="title">%s — %s (%d passed, %d failed, %d skipped)</ac:parameter>
                  <ac:rich-text-body>
                """.formatted(suite.suiteName, suiteStatus,
                    suite.passed, suite.failed, suite.skipped));

            // Test case table
            sb.append("""
                <table>
                  <thead>
                    <tr>
                      <th>#</th>
                      <th>Test Case</th>
                      <th>Status</th>
                      <th>Duration (ms)</th>
                      <th>Failure Reason</th>
                    </tr>
                  </thead>
                  <tbody>
                """);

            int idx = 1;
            for (TestCaseResult tc : suite.testCases) {
                String statusCell = switch (tc.status) {
                    case "PASSED"  -> "<td style=\"color:#006400\"><strong>✅ PASSED</strong></td>";
                    case "FAILED"  -> "<td style=\"color:#8B0000\"><strong>❌ FAILED</strong></td>";
                    default        -> "<td style=\"color:#FF8C00\"><strong>⏭ SKIPPED</strong></td>";
                };

                String reason = tc.failureReason != null
                        ? escapeHtml(tc.failureReason)
                        : "—";

                sb.append("""
                    <tr>
                      <td>%d</td>
                      <td><code>%s</code></td>
                      %s
                      <td>%d</td>
                      <td><small>%s</small></td>
                    </tr>
                    """.formatted(idx++, escapeHtml(tc.testName),
                        statusCell, tc.durationMs, reason));
            }

            sb.append("  </tbody>\n</table>\n");
            sb.append("  </ac:rich-text-body>\n</ac:structured-macro>\n");
        }

        // ── Bug report section ────────────────────────────────────────────
        sb.append(buildBugReportSection());

        // ── Footer ────────────────────────────────────────────────────────
        sb.append("""
            <hr/>
            <p><em>Generated automatically by TVS AutoSequencing Playwright Framework.
            Report path: <code>reports/PlaywrightTestReport.html</code></em></p>
            """);

        return sb.toString();
    }

    // ── Bug report table (from screenshot analysis) ───────────────────────

    private String buildBugReportSection() {
        return """
            <h2>Bug Report — New Sequence Live Screen</h2>
            <p>Bugs identified from screenshot analysis of
            <code>https://uat-sns.tvsmotor.net/Autoseq/groupmaster</code></p>
            <table>
              <thead>
                <tr>
                  <th>Bug ID</th><th>Description</th><th>Severity</th><th>Priority</th><th>Status</th>
                </tr>
              </thead>
              <tbody>
                <tr><td>BUG-001</td><td>Marked value shows <code>--</code> for all rows</td>
                    <td style="color:#8B0000">High</td><td>P1</td><td>Open</td></tr>
                <tr><td>BUG-002</td><td>All rows show <em>Incomplete</em> with Actual = 0</td>
                    <td style="color:#8B0000">High</td><td>P1</td><td>Open</td></tr>
                <tr><td>BUG-003</td><td>All rows flagged as <em>Expected - Bottleneck</em></td>
                    <td style="color:#8B0000">High</td><td>P1</td><td>Open</td></tr>
                <tr><td>BUG-004</td><td>Seq 2 missing MIW tag — inconsistent with other rows</td>
                    <td style="color:#FF8C00">Medium</td><td>P2</td><td>Open</td></tr>
                <tr><td>BUG-005</td><td>Live banner Plan 80 vs Seq 1 Plan 40 mismatch</td>
                    <td style="color:#FF8C00">Medium</td><td>P2</td><td>Open</td></tr>
                <tr><td>BUG-006</td><td>L-a / L-b labels have no legend or tooltip</td>
                    <td style="color:#006400">Low</td><td>P3</td><td>Open</td></tr>
                <tr><td>BUG-007</td><td>Last Model Run timestamp identical across all rows</td>
                    <td style="color:#FF8C00">Medium</td><td>P2</td><td>Open</td></tr>
                <tr><td>BUG-008</td><td>Inconsistent timestamp format on same screen</td>
                    <td style="color:#006400">Low</td><td>P3</td><td>Open</td></tr>
                <tr><td>BUG-009</td><td>Actual count stays 0 during live session</td>
                    <td style="color:#8B0000">High</td><td>P1</td><td>Open</td></tr>
                <tr><td>BUG-010</td><td>URL path <code>/groupmaster</code> does not match tab name</td>
                    <td style="color:#006400">Low</td><td>P3</td><td>Open</td></tr>
              </tbody>
            </table>
            """;
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
