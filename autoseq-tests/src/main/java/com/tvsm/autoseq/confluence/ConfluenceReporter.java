package com.tvsm.autoseq.confluence;

import com.tvsm.autoseq.config.ConfigReader;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

import java.util.ArrayList;
import java.util.List;

/**
 * ConfluenceReporter — TestNG IReporter that publishes test results
 * to a Confluence page after the suite completes.
 *
 * Registration: add to testng.xml listeners section.
 * Toggle:       set confluence.enabled=true in config.properties
 *               (or pass -Dconfluence.enabled=true at runtime).
 *
 * Required config keys:
 *   confluence.base.url       https://your-domain.atlassian.net/wiki
 *   confluence.space.key      QA
 *   confluence.page.title     TVS AutoSeq — Playwright Test Results
 *   confluence.parent.page.id (optional) parent page ID
 *   confluence.username       your-email@tvsmotor.com
 *   confluence.api.token      your-api-token
 */
public class ConfluenceReporter implements IReporter {

    @Override
    public void generateReport(List<XmlSuite> xmlSuites,
                               List<ISuite>   suites,
                               String         outputDirectory) {

        if (!ConfigReader.confluenceEnabled()) {
            System.out.println("ℹ️  Confluence reporting disabled. "
                    + "Set confluence.enabled=true to publish results.");
            return;
        }

        System.out.println("📤 Publishing test results to Confluence...");

        // ── 1. Collect results ────────────────────────────────────────────
        List<TestSuiteResult> suiteResults = new ArrayList<>();

        for (ISuite suite : suites) {
            for (ISuiteResult sr : suite.getResults().values()) {
                ITestContext ctx = sr.getTestContext();
                TestSuiteResult suiteResult = new TestSuiteResult(ctx.getName());

                // Passed tests
                for (ITestResult r : ctx.getPassedTests().getAllResults()) {
                    suiteResult.add(new TestCaseResult(
                            r.getMethod().getMethodName(),
                            "PASSED",
                            r.getEndMillis() - r.getStartMillis(),
                            null));
                }

                // Failed tests
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

                // Skipped tests
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

        // ── 2. Build HTML ─────────────────────────────────────────────────
        String browser = ConfigReader.browser();
        String html    = new ConfluencePageBuilder().build(suiteResults, browser, "UAT");

        // ── 3. Publish to Confluence ──────────────────────────────────────
        try {
            ConfluenceClient client = new ConfluenceClient();
            client.publishPage(
                    ConfigReader.confluenceSpaceKey(),
                    ConfigReader.confluencePageTitle(),
                    ConfigReader.confluenceParentPageId(),
                    html);

            System.out.println("✅ Results published to Confluence: "
                    + ConfigReader.confluenceBaseUrl()
                    + "/display/" + ConfigReader.confluenceSpaceKey()
                    + "/" + ConfigReader.confluencePageTitle()
                            .replace(" ", "+"));

        } catch (Exception e) {
            System.out.println("❌ Failed to publish to Confluence: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
