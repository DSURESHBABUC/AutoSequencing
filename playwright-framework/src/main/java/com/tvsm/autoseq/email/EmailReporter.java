package com.tvsm.autoseq.email;

import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.confluence.TestCaseResult;
import com.tvsm.autoseq.confluence.TestSuiteResult;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * EmailReporter — TestNG IReporter that emails test results
 * to the configured recipient after the suite completes.
 *
 * Registration: add to testng.xml listeners section.
 * Toggle:       set email.enabled=true in config.properties
 *
 * Required config keys (config.properties):
 *   email.enabled        true
 *   email.smtp.host      smtp.gmail.com  (or your corporate SMTP)
 *   email.smtp.port      587
 *   email.smtp.auth      true
 *   email.smtp.starttls  true
 *   email.from           sender@tvsmotor.com
 *   email.to             d.sureshbabu@tvsmotor.com
 *   email.username       sender@tvsmotor.com
 *   email.password       your-smtp-app-password
 */
public class EmailReporter implements IReporter {

    private static final DateTimeFormatter SUBJECT_FMT =
            DateTimeFormatter.ofPattern("dd-MMM-yyyy HH:mm");

    @Override
    public void generateReport(List<XmlSuite> xmlSuites,
                               List<ISuite>   suites,
                               String         outputDirectory) {

        if (!ConfigReader.emailEnabled()) {
            System.out.println("ℹ️  Email reporting disabled. "
                    + "Set email.enabled=true in config.properties to send results.");
            return;
        }

        System.out.println("📧 Preparing test results email...");

        // ── 1. Collect results ────────────────────────────────────────────
        List<TestSuiteResult> suiteResults = new ArrayList<>();

        for (ISuite suite : suites) {
            for (ISuiteResult sr : suite.getResults().values()) {
                ITestContext ctx = sr.getTestContext();
                TestSuiteResult suiteResult = new TestSuiteResult(ctx.getName());

                for (ITestResult r : ctx.getPassedTests().getAllResults()) {
                    suiteResult.add(new TestCaseResult(
                            r.getMethod().getMethodName(),
                            "PASSED",
                            r.getEndMillis() - r.getStartMillis(),
                            null));
                }

                for (ITestResult r : ctx.getFailedTests().getAllResults()) {
                    String reason = r.getThrowable() != null
                            ? r.getThrowable().getMessage()
                            : "Unknown failure";
                    suiteResult.add(new TestCaseResult(
                            r.getMethod().getMethodName(),
                            "FAILED",
                            r.getEndMillis() - r.getStartMillis(),
                            reason));
                }

                for (ITestResult r : ctx.getSkippedTests().getAllResults()) {
                    suiteResult.add(new TestCaseResult(
                            r.getMethod().getMethodName(),
                            "SKIPPED",
                            0,
                            null));
                }

                suiteResults.add(suiteResult);
            }
        }

        // ── 2. Aggregate for subject line ─────────────────────────────────
        int totalPass = suiteResults.stream().mapToInt(s -> s.passed).sum();
        int totalFail = suiteResults.stream().mapToInt(s -> s.failed).sum();
        int total     = suiteResults.stream()
                .mapToInt(s -> s.passed + s.failed + s.skipped).sum();

        String runTime = ZonedDateTime.now(ZoneId.of("Asia/Kolkata")).format(SUBJECT_FMT);
        String status  = totalFail == 0 ? "✅ PASSED" : "❌ FAILED";
        String subject = String.format(
                "[TVS AutoSeq] %s — Playwright Results | %d/%d Passed | %s",
                status, totalPass, total, runTime);

        // ── 3. Build HTML body ────────────────────────────────────────────
        String browser = ConfigReader.browser();
        String html    = new EmailBodyBuilder().build(suiteResults, browser, "UAT");

        // ── 4. Send ───────────────────────────────────────────────────────
        try {
            new EmailSender().send(subject, html);
            System.out.println("✅ Email sent to: " + ConfigReader.emailTo());
            System.out.println("   Subject: " + subject);
        } catch (Exception e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
