package com.tvsm.autoseq.email;

import com.tvsm.autoseq.confluence.TestCaseResult;
import com.tvsm.autoseq.confluence.TestSuiteResult;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * EmailBodyBuilder — produces a styled HTML email body
 * from SequenceLiveDateSearchTest results.
 *
 * Sections:
 *  1. Run metadata banner
 *  2. Overall summary (total / passed / failed / skipped / pass rate)
 *  3. Per-suite test case table
 *  4. Bug report table
 *  5. Footer
 */
public class EmailBodyBuilder {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm z");

    public String build(List<TestSuiteResult> suites, String browser, String env) {

        String runTime   = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(FMT);
        int totalPass    = suites.stream().mapToInt(s -> s.passed).sum();
        int totalFail    = suites.stream().mapToInt(s -> s.failed).sum();
        int totalSkip    = suites.stream().mapToInt(s -> s.skipped).sum();
        int total        = totalPass + totalFail + totalSkip;
        int passRate     = total > 0 ? (int) Math.round(100.0 * totalPass / total) : 0;
        String bannerBg  = totalFail == 0 ? "#1a7f37" : "#b91c1c";
        String statusTxt = totalFail == 0 ? "ALL TESTS PASSED" : totalFail + " TEST(S) FAILED";

        StringBuilder sb = new StringBuilder();

        // ── Outer wrapper ─────────────────────────────────────────────────
        sb.append("""
            <!DOCTYPE html>
            <html lang="en">
            <head>
              <meta charset="UTF-8"/>
              <meta name="viewport" content="width=device-width,initial-scale=1"/>
              <title>TVS AutoSeq — Test Results</title>
            </head>
            <body style="margin:0;padding:0;background:#f4f4f4;font-family:Segoe UI,Roboto,Arial,sans-serif;">
            <table width="100%%" cellpadding="0" cellspacing="0" style="background:#f4f4f4;padding:24px 0;">
              <tr><td align="center">
              <table width="680" cellpadding="0" cellspacing="0"
                     style="background:#ffffff;border-radius:8px;overflow:hidden;
                            box-shadow:0 2px 8px rgba(0,0,0,.12);">
            """);

        // ── Header banner ─────────────────────────────────────────────────
        sb.append("""
              <!-- HEADER -->
              <tr>
                <td style="background:%s;padding:24px 32px;">
                  <table width="100%%" cellpadding="0" cellspacing="0">
                    <tr>
                      <td>
                        <p style="margin:0;color:#fff;font-size:11px;letter-spacing:1px;
                                  text-transform:uppercase;opacity:.8;">
                          TVS Motor Company · AutoSequencing (SNS)
                        </p>
                        <h1 style="margin:6px 0 0;color:#fff;font-size:22px;font-weight:700;">
                          Playwright Test Results
                        </h1>
                      </td>
                      <td align="right">
                        <span style="display:inline-block;background:rgba(255,255,255,.2);
                                     color:#fff;font-size:13px;font-weight:700;
                                     padding:6px 14px;border-radius:20px;letter-spacing:.5px;">
                          %s
                        </span>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            """.formatted(bannerBg, statusTxt));

        // ── Run metadata ──────────────────────────────────────────────────
        sb.append("""
              <!-- META -->
              <tr>
                <td style="background:#f8f9fa;padding:12px 32px;border-bottom:1px solid #e5e7eb;">
                  <table cellpadding="0" cellspacing="0">
                    <tr>
                      <td style="padding-right:24px;font-size:12px;color:#6b7280;">
                        <strong style="color:#374151;">Environment</strong><br/>%s
                      </td>
                      <td style="padding-right:24px;font-size:12px;color:#6b7280;">
                        <strong style="color:#374151;">Browser</strong><br/>%s
                      </td>
                      <td style="padding-right:24px;font-size:12px;color:#6b7280;">
                        <strong style="color:#374151;">Run Time</strong><br/>%s
                      </td>
                      <td style="font-size:12px;color:#6b7280;">
                        <strong style="color:#374151;">URL</strong><br/>
                        <a href="https://uat-sns.tvsmotor.net/Autoseq/groupmaster"
                           style="color:#2563eb;">groupmaster</a>
                      </td>
                    </tr>
                  </table>
                </td>
              </tr>
            """.formatted(env, browser, runTime));

        // ── Summary cards ─────────────────────────────────────────────────
        sb.append("""
              <!-- SUMMARY -->
              <tr>
                <td style="padding:24px 32px 16px;">
                  <table width="100%%" cellpadding="0" cellspacing="8">
                    <tr>
                      %s
                      %s
                      %s
                      %s
                      %s
                    </tr>
                  </table>
                </td>
              </tr>
            """.formatted(
                summaryCard("Total",   String.valueOf(total),    "#374151", "#f3f4f6"),
                summaryCard("Passed",  String.valueOf(totalPass), "#ffffff", "#16a34a"),
                summaryCard("Failed",  String.valueOf(totalFail), "#ffffff", totalFail > 0 ? "#dc2626" : "#6b7280"),
                summaryCard("Skipped", String.valueOf(totalSkip), "#ffffff", "#d97706"),
                summaryCard("Pass Rate", passRate + "%",          "#ffffff", passRate == 100 ? "#16a34a" : "#2563eb")
        ));

        // ── Per-suite test tables ─────────────────────────────────────────
        sb.append("""
              <!-- TEST RESULTS -->
              <tr>
                <td style="padding:0 32px 24px;">
                  <h2 style="margin:0 0 12px;font-size:15px;color:#111827;
                              border-bottom:2px solid #e5e7eb;padding-bottom:8px;">
                    Test Case Results
                  </h2>
            """);

        for (TestSuiteResult suite : suites) {
            String suiteStatusColor = suite.failed == 0 ? "#16a34a" : "#dc2626";
            String suiteStatusText  = suite.failed == 0 ? "PASSED" : "FAILED";

            sb.append("""
                  <p style="margin:16px 0 6px;font-size:13px;font-weight:600;color:#374151;">
                    %s
                    <span style="font-size:11px;font-weight:700;color:%s;
                                 background:%s;padding:2px 8px;border-radius:10px;margin-left:8px;">
                      %s
                    </span>
                    <span style="font-size:11px;color:#6b7280;margin-left:8px;">
                      %d passed · %d failed · %d skipped
                    </span>
                  </p>
                """.formatted(
                    escHtml(suite.suiteName),
                    suiteStatusColor,
                    suite.failed == 0 ? "#dcfce7" : "#fee2e2",
                    suiteStatusText,
                    suite.passed, suite.failed, suite.skipped));

            // Test case table
            sb.append("""
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="border-collapse:collapse;font-size:12px;
                                border:1px solid #e5e7eb;border-radius:6px;overflow:hidden;">
                    <thead>
                      <tr style="background:#f9fafb;">
                        <th style="padding:8px 12px;text-align:left;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;width:30px;">#</th>
                        <th style="padding:8px 12px;text-align:left;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Test Case</th>
                        <th style="padding:8px 12px;text-align:center;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;width:90px;">Status</th>
                        <th style="padding:8px 12px;text-align:right;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;width:90px;">Duration</th>
                        <th style="padding:8px 12px;text-align:left;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Failure Reason</th>
                      </tr>
                    </thead>
                    <tbody>
                """);

            int idx = 1;
            for (TestCaseResult tc : suite.testCases) {
                String rowBg = (idx % 2 == 0) ? "#f9fafb" : "#ffffff";
                String[] statusStyle = switch (tc.status) {
                    case "PASSED"  -> new String[]{"✅ PASSED",  "#16a34a", "#dcfce7"};
                    case "FAILED"  -> new String[]{"❌ FAILED",  "#dc2626", "#fee2e2"};
                    default        -> new String[]{"⏭ SKIPPED", "#d97706", "#fef3c7"};
                };
                String reason = tc.failureReason != null
                        ? "<span style='color:#dc2626'>" + escHtml(tc.failureReason) + "</span>"
                        : "<span style='color:#9ca3af'>—</span>";

                sb.append("""
                      <tr style="background:%s;">
                        <td style="padding:8px 12px;color:#9ca3af;border-bottom:1px solid #f3f4f6;">%d</td>
                        <td style="padding:8px 12px;color:#111827;border-bottom:1px solid #f3f4f6;">
                          <code style="font-size:11px;background:#f3f4f6;padding:2px 5px;
                                       border-radius:3px;">%s</code>
                        </td>
                        <td style="padding:8px 12px;text-align:center;border-bottom:1px solid #f3f4f6;">
                          <span style="font-size:11px;font-weight:700;color:%s;
                                       background:%s;padding:2px 8px;border-radius:10px;">
                            %s
                          </span>
                        </td>
                        <td style="padding:8px 12px;text-align:right;color:#6b7280;
                                   border-bottom:1px solid #f3f4f6;">%d ms</td>
                        <td style="padding:8px 12px;font-size:11px;
                                   border-bottom:1px solid #f3f4f6;">%s</td>
                      </tr>
                    """.formatted(
                        rowBg, idx++,
                        escHtml(tc.testName),
                        statusStyle[1], statusStyle[2], statusStyle[0],
                        tc.durationMs, reason));
            }

            sb.append("    </tbody>\n  </table>\n");
        }

        sb.append("    </td>\n  </tr>\n");

        // ── Bug report table ──────────────────────────────────────────────
        sb.append(bugReportSection());

        // ── Footer ────────────────────────────────────────────────────────
        sb.append("""
              <!-- FOOTER -->
              <tr>
                <td style="background:#f8f9fa;padding:16px 32px;
                           border-top:1px solid #e5e7eb;text-align:center;">
                  <p style="margin:0;font-size:11px;color:#9ca3af;">
                    Generated by TVS AutoSequencing Playwright Framework &nbsp;·&nbsp;
                    Environment: UAT &nbsp;·&nbsp;
                    <a href="https://uat-sns.tvsmotor.net/Autoseq/groupmaster"
                       style="color:#2563eb;text-decoration:none;">Open Application</a>
                  </p>
                </td>
              </tr>
            </table>
            </td></tr></table>
            </body>
            </html>
            """);

        return sb.toString();
    }

    // ── Helpers ───────────────────────────────────────────────────────────

    private String summaryCard(String label, String value, String textColor, String bgColor) {
        return """
            <td align="center" style="background:%s;border-radius:8px;
                                      padding:14px 10px;width:20%%;">
              <p style="margin:0;font-size:22px;font-weight:700;color:%s;">%s</p>
              <p style="margin:4px 0 0;font-size:11px;color:%s;opacity:.85;">%s</p>
            </td>
            """.formatted(bgColor, textColor, value, textColor, label);
    }

    private String bugReportSection() {
        return """
              <!-- BUG REPORT -->
              <tr>
                <td style="padding:0 32px 24px;">
                  <h2 style="margin:0 0 12px;font-size:15px;color:#111827;
                              border-bottom:2px solid #e5e7eb;padding-bottom:8px;">
                    Bug Report — New Sequence Live Screen
                  </h2>
                  <p style="margin:0 0 10px;font-size:12px;color:#6b7280;">
                    Bugs identified from screenshot analysis of
                    <a href="https://uat-sns.tvsmotor.net/Autoseq/groupmaster"
                       style="color:#2563eb;">groupmaster</a>
                  </p>
                  <table width="100%%" cellpadding="0" cellspacing="0"
                         style="border-collapse:collapse;font-size:12px;
                                border:1px solid #e5e7eb;border-radius:6px;overflow:hidden;">
                    <thead>
                      <tr style="background:#f9fafb;">
                        <th style="padding:8px 12px;text-align:left;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Bug ID</th>
                        <th style="padding:8px 12px;text-align:left;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Description</th>
                        <th style="padding:8px 12px;text-align:center;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Severity</th>
                        <th style="padding:8px 12px;text-align:center;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Priority</th>
                        <th style="padding:8px 12px;text-align:center;color:#6b7280;
                                   font-weight:600;border-bottom:1px solid #e5e7eb;">Status</th>
                      </tr>
                    </thead>
                    <tbody>
                      %s
                      %s
                      %s
                      %s
                      %s
                      %s
                      %s
                      %s
                      %s
                      %s
                    </tbody>
                  </table>
                </td>
              </tr>
            """.formatted(
                bugRow("BUG-001", "Marked value shows -- for all rows",                    "High",   "P1", 1),
                bugRow("BUG-002", "All rows show Incomplete with Actual = 0",              "High",   "P1", 2),
                bugRow("BUG-003", "All rows flagged as Expected - Bottleneck",             "High",   "P1", 3),
                bugRow("BUG-004", "Seq 2 missing MIW tag — inconsistent with other rows", "Medium", "P2", 4),
                bugRow("BUG-005", "Live banner Plan 80 vs Seq 1 Plan 40 mismatch",        "Medium", "P2", 5),
                bugRow("BUG-006", "L-a / L-b labels have no legend or tooltip",           "Low",    "P3", 6),
                bugRow("BUG-007", "Last Model Run timestamp identical across all rows",    "Medium", "P2", 7),
                bugRow("BUG-008", "Inconsistent timestamp format on same screen",          "Low",    "P3", 8),
                bugRow("BUG-009", "Actual count stays 0 during live session",              "High",   "P1", 9),
                bugRow("BUG-010", "URL /groupmaster does not match tab name",              "Low",    "P3", 10)
        );
    }

    private String bugRow(String id, String desc, String severity, String priority, int rowNum) {
        String rowBg = (rowNum % 2 == 0) ? "#f9fafb" : "#ffffff";
        String sevColor = switch (severity) {
            case "High"   -> "#dc2626";
            case "Medium" -> "#d97706";
            default       -> "#16a34a";
        };
        String sevBg = switch (severity) {
            case "High"   -> "#fee2e2";
            case "Medium" -> "#fef3c7";
            default       -> "#dcfce7";
        };
        return """
            <tr style="background:%s;">
              <td style="padding:7px 12px;font-weight:600;color:#374151;
                         border-bottom:1px solid #f3f4f6;">%s</td>
              <td style="padding:7px 12px;color:#374151;
                         border-bottom:1px solid #f3f4f6;">%s</td>
              <td style="padding:7px 12px;text-align:center;
                         border-bottom:1px solid #f3f4f6;">
                <span style="font-size:11px;font-weight:700;color:%s;
                             background:%s;padding:2px 8px;border-radius:10px;">%s</span>
              </td>
              <td style="padding:7px 12px;text-align:center;color:#374151;
                         border-bottom:1px solid #f3f4f6;">%s</td>
              <td style="padding:7px 12px;text-align:center;
                         border-bottom:1px solid #f3f4f6;">
                <span style="font-size:11px;color:#d97706;background:#fef3c7;
                             padding:2px 8px;border-radius:10px;font-weight:600;">Open</span>
              </td>
            </tr>
            """.formatted(rowBg, id, escHtml(desc), sevColor, sevBg, severity, priority);
    }

    private String escHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;");
    }
}
