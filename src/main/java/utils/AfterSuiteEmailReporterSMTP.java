package utils;

import Utils.EmailUtil;
import jakarta.mail.internet.MimeBodyPart;
import org.apache.poi.sl.usermodel.TextParagraph;
import org.apache.poi.xslf.usermodel.*;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ISuiteResult;
import org.testng.ITestContext;
import org.testng.xml.XmlSuite;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class AfterSuiteEmailReporterSMTP implements IReporter {
    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
        System.out.println("[AfterSuiteEmailReporter] generateReport invoked");

        // Load smtp.properties
        Properties cfg = new Properties();
        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream("smtp.properties")) {
            if (in == null) throw new RuntimeException("smtp.properties not found on classpath");
            cfg.load(in);
        } catch (Exception e) {
            throw new RuntimeException("Cannot load smtp.properties", e);
        }

//        String username = System.getenv("SMTP_USERNAME");
//        String password = System.getenv("SMTP_PASSWORD");
//        String host = System.getenv("SMTP_HOST");
//        String port = System.getenv("SMTP_PORT");
//        String toEmail = System.getenv("SMTP_TO");
//        String fromEmail = System.getenv("SMTP_FROM");
//
//        if (username != null && !username.isEmpty()) cfg.setProperty("mail.username", username);
//        if (password != null && !password.isEmpty()) cfg.setProperty("mail.password", password);
//        if (host != null && !host.isEmpty()) cfg.setProperty("mail.host", host);
//        if (port != null && !port.isEmpty()) cfg.setProperty("mail.preferredPorts", port);
//        if (port != null && !port.isEmpty()) cfg.setProperty("mail.preferredPorts", toEmail);
//        if (port != null && !port.isEmpty()) cfg.setProperty("mail.preferredPorts", fromEmail);

        System.out.println("[Email CFG] to=" + cfg.getProperty("mail.to") +
                " host=" + cfg.getProperty("mail.host") +
                " ports=" + cfg.getProperty("mail.preferredPorts", "25,587,465"));

        // === New Aggregation per Test Variant (i.e., per <test name="...">)
        Map<String, int[]> variantStats = new LinkedHashMap<>();
        List<String> topFailures = new ArrayList<>();

        for (ISuite suite : suites) {
            for (ISuiteResult sr : suite.getResults().values()) {
                ITestContext ctx = sr.getTestContext();
                String testName = ctx.getName(); // this matches <test name="..."> from uat.xml

                int passed = ctx.getPassedTests().size();
                int failed = ctx.getFailedTests().size();
                int skipped = ctx.getSkippedTests().size();
                int total = passed + failed + skipped;

                variantStats.put(testName, new int[]{total, passed, failed});

                ctx.getFailedTests().getAllResults().stream().limit(5 - topFailures.size()).forEach(r -> {
                    String name = r.getMethod().getMethodName();
                    String msg = (r.getThrowable() != null) ? r.getThrowable().toString() : "No message";
                    topFailures.add(name + " — " + msg);
                });
            }
        }

        // Overall summary
        int total = 0, passed = 0, failed = 0;
        for (int[] v : variantStats.values()) {
            total += v[0];
            passed += v[1];
            failed += v[2];
        }

        ZoneId ist = ZoneId.of("Asia/Kolkata");
        String when = ZonedDateTime.now(ist).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm z"));

        // === Build Variant HTML Table
        StringBuilder variantTable = new StringBuilder();
        variantTable.append("""
        <table style="border-collapse:collapse;margin-top:20px">
            <tr>
                <th style="text-align:left;padding:6px 10px;border-bottom:1px solid #ccc">Project Variant</th>
                <th style="text-align:left;padding:6px 10px;border-bottom:1px solid #ccc">Total Testcases Count</th>
                <th style="text-align:left;padding:6px 10px;border-bottom:1px solid #ccc;color:#15803d">Passed Testcases</th>
                <th style="text-align:left;padding:6px 10px;border-bottom:1px solid #ccc;color:#b91c1c">Failed Test Cases</th>
            </tr>
    """);

        for (Map.Entry<String, int[]> entry : variantStats.entrySet()) {
            String name = entry.getKey();
            int[] stats = entry.getValue();
            variantTable.append(String.format("""
            <tr>
                <td style="padding:6px 10px">%s</td>
                <td style="padding:6px 10px">%d</td>
                <td style="padding:6px 10px">%d</td>
                <td style="padding:6px 10px">%d</td>
            </tr>
        """, name, stats[0], stats[1], stats[2]));
        }

        variantTable.append(String.format("""
        <tr>
            <td style="padding:6px 10px"><strong>Total</strong></td>
            <td style="padding:6px 10px"><strong>%d</strong></td>
            <td style="padding:6px 10px"><strong>%d</strong></td>
            <td style="padding:6px 10px"><strong>%d</strong></td>
        </tr>
    </table>
    """, total, passed, failed));

        // === Email HTML Body
        String failuresHtml = failuresBlock(topFailures);
        String html = """
    <div style="font-family:Segoe UI,Roboto,Arial,sans-serif">
      <h2 style="margin:0 0 8px">API Automation — Test Summary</h2>
      <div style="color:#64748b;font-size:12px;margin:4px 0 12px">Generated: %s</div>
      %s
      %s
      <p style="font-size:12px;color:#64748b">Full details are in the attached Excel report and PPT summary.</p>
    </div>
    """.formatted(when, variantTable.toString(), failuresHtml);

        String prefix = Optional.ofNullable(cfg.getProperty("subject.prefix")).orElse("[API]");
        String subject = String.format("%s API Automation — %s (P:%d F:%d)", prefix, when, passed, failed);

        // === Attachments
        List<Path> files = new ArrayList<>();
        Path excel = Paths.get(cfg.getProperty("report.excel.path", "src/test/java/simulation/UAT_Tvsconnect_output.xlsx"));
        System.out.println("[Attach] Excel " + excel.toAbsolutePath() + " exists=" + Files.exists(excel));
        if (Files.exists(excel)) files.add(excel);

        List<MimeBodyPart> extra = new ArrayList<>();
        String pptResource = cfg.getProperty("ppt.resource.path", "reports/Report_Summary.pptx");

        try (InputStream in = Thread.currentThread().getContextClassLoader().getResourceAsStream(pptResource)) {
            if (in != null) {
                extra.add(EmailUtil.buildAttachmentFromResource(
                        in,
                        "Report_Summary.pptx",
                        "application/vnd.openxmlformats-officedocument.presentationml.presentation"));
                System.out.println("[Attach] PPT from classpath: " + pptResource);
            } else {
                Path tmp = generatePptSummary(total, passed, failed, 0, when);
                files.add(tmp);
                System.out.println("[Attach] PPT auto-generated: " + tmp.toAbsolutePath());
            }
        } catch (Exception e) {
            System.out.println("[Attach] PPT handling error: " + e);
        }

        // === Send
        EmailUtil.sendEmailWithAttachment(cfg, subject, html, files, extra);
    }
    private Path generatePptSummary(int total, int passed, int failed, int skipped, String when) throws Exception {
        XMLSlideShow ppt = new XMLSlideShow();
        XSLFSlide slide = ppt.createSlide();

        // Title Box
        XSLFTextBox title = slide.createTextBox();
        title.setAnchor(new java.awt.Rectangle(40, 40, 640, 60));
        XSLFTextParagraph tp = title.addNewTextParagraph();
        tp.setTextAlign(TextParagraph.TextAlign.LEFT);
        XSLFTextRun tr = tp.addNewTextRun();
        tr.setText("P360 API Automation — Summary");
        tr.setFontSize(28.0);

        // Body Box
        XSLFTextBox body = slide.createTextBox();
        body.setAnchor(new java.awt.Rectangle(40, 120, 640, 240));
        XSLFTextParagraph p;

        p = body.addNewTextParagraph(); p.addNewTextRun().setText("Generated: " + when);
        p = body.addNewTextParagraph(); p.addNewTextRun().setText("Total   : " + total);
        p = body.addNewTextParagraph(); p.addNewTextRun().setText("Passed  : " + passed);
        p = body.addNewTextParagraph(); p.addNewTextRun().setText("Failed  : " + failed);
        p = body.addNewTextParagraph(); p.addNewTextRun().setText("Skipped : " + skipped);

        // Save to temp file
        Path tmp = Files.createTempFile("P360_Summary-", ".pptx");
        try (java.io.OutputStream out = Files.newOutputStream(tmp)) {
            ppt.write(out);
        }
        ppt.close();
        return tmp;
    }
    private String failuresBlock(List<String> failures) {
        if (failures == null || failures.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        sb.append("Top Failures:\n");
        for (String f : failures) {
            sb.append(" - ").append(f).append("\n");
        }
        return sb.toString();
    }


}