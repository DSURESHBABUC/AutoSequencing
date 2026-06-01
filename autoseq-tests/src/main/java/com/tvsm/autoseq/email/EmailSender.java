package com.tvsm.autoseq.email;

import com.tvsm.autoseq.config.ConfigReader;
import jakarta.mail.*;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeBodyPart;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.internet.MimeMultipart;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * EmailSender — sends the HTML test report via SMTP.
 *
 * Supports:
 *  - STARTTLS (port 587, default for Gmail / Office 365)
 *  - SSL      (port 465)
 *  - Plain    (port 25, corporate relay)
 *
 * Optionally attaches the ExtentReports HTML file.
 */
public class EmailSender {

    /**
     * Sends the HTML body to the configured recipient.
     * Attaches the ExtentReports HTML file if it exists.
     *
     * @param subject   email subject line
     * @param htmlBody  fully-formed HTML email body
     */
    public void send(String subject, String htmlBody) throws Exception {

        Properties props = buildSmtpProperties();
        Session session  = buildSession(props);

        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(ConfigReader.emailFrom()));
        message.setRecipients(Message.RecipientType.TO,
                InternetAddress.parse(ConfigReader.emailTo()));
        message.setSubject(subject);

        // ── Multipart: HTML body + optional attachment ────────────────────
        MimeMultipart multipart = new MimeMultipart();

        // HTML body part
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        // Attach ExtentReports HTML if it exists
        Path reportFile = Paths.get(ConfigReader.reportDir(), "PlaywrightTestReport.html");
        if (Files.exists(reportFile)) {
            MimeBodyPart attachPart = new MimeBodyPart();
            attachPart.attachFile(reportFile.toFile());
            attachPart.setFileName("PlaywrightTestReport.html");
            multipart.addBodyPart(attachPart);
            System.out.println("📎 Attaching report: " + reportFile.toAbsolutePath());
        }

        message.setContent(multipart);
        Transport.send(message);
    }

    // ── SMTP configuration ────────────────────────────────────────────────

    private Properties buildSmtpProperties() {
        Properties props = new Properties();
        props.put("mail.smtp.host",            ConfigReader.emailSmtpHost());
        props.put("mail.smtp.port",            String.valueOf(ConfigReader.emailSmtpPort()));
        props.put("mail.smtp.auth",            String.valueOf(ConfigReader.emailSmtpAuth()));
        props.put("mail.smtp.starttls.enable", String.valueOf(ConfigReader.emailSmtpStartTls()));
        props.put("mail.smtp.connectiontimeout", "15000");
        props.put("mail.smtp.timeout",           "15000");
        props.put("mail.smtp.writetimeout",      "15000");

        // SSL on port 465
        if (ConfigReader.emailSmtpPort() == 465) {
            props.put("mail.smtp.ssl.enable",  "true");
            props.put("mail.smtp.starttls.enable", "false");
        }
        return props;
    }

    private Session buildSession(Properties props) {
        boolean auth = ConfigReader.emailSmtpAuth();
        if (auth) {
            String user = ConfigReader.emailUsername();
            String pass = ConfigReader.emailPassword();
            return Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(user, pass);
                }
            });
        }
        return Session.getInstance(props);
    }
}
