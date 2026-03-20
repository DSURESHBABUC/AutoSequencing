package utils;

import jakarta.mail.*;
import jakarta.mail.internet.*;
import jakarta.activation.DataHandler;
import jakarta.mail.util.ByteArrayDataSource;

import java.io.InputStream;
import java.nio.file.*;
import java.util.*;

/**
 * Robust SMTP sender for TestNG after-suite emails.
 * - Tries ports (default order): 25 (PLAIN) → 587 (STARTTLS) → 465 (SSL)
 * - 15s connect/read/write timeouts (fail fast)
 * - Expands ${ENV_*} placeholders from smtp.properties
 * - ENV wins over properties for username/password (ENV_MAIL_USER / ENV_MAIL_PASS)
 * - Helper to attach classpath resources (e.g., PPT in src/test/resources)
 *
 * smtp.properties (examples):
 *   mail.host=smtp.tvsmotor.com
 *   mail.preferredPorts=25,587,465
 *   mail.from=siddarth.suman@tvsmotor.com
 *   mail.to=siddarth.suman@tvsmotor.com
 *   mail.username=${ENV_MAIL_USER}
 *   mail.password=${ENV_MAIL_PASS}
 *   mail.debug=true
 *   mail.localhost=localhost
 */
public class EmailUtil {

    /** Send with filesystem attachments. */
    public static void sendEmailWithAttachment(
            Properties cfg, String subject, String htmlBody, List<Path> fileAttachments) {
        sendEmailWithAttachment(cfg, subject, htmlBody, fileAttachments, null);
    }

    /** Send with filesystem attachments + extra MIME parts (e.g., classpath resources). */
    public static void sendEmailWithAttachment(
            Properties cfg, String subject, String htmlBody, List<Path> fileAttachments, List<MimeBodyPart> extraParts) {

        List<Mode> modes = preferredModes(cfg);  // e.g., [25,587,465]
        Exception last = null;

        for (Mode mode : modes) {
            try {
                Session session = buildSession(cfg, mode);
                boolean debug = getBool(cfg, "mail.debug", true);
                session.setDebug(debug);

                MimeMessage msg = buildMessage(session, cfg, subject, htmlBody, fileAttachments, extraParts);
                System.out.println("[SMTP] Trying " + mode.label + " → host=" + cfg.getProperty("mail.host"));
                Transport.send(msg);

                System.out.println("[SMTP] Sent OK via " + cfg.getProperty("mail.host") + " " + mode.label
                        + " from=" + cfg.getProperty("mail.from")
                        + " to=" + cfg.getProperty("mail.to"));
                return;
            } catch (Exception e) {
                last = e;
                System.out.println("[SMTP] Attempt failed on " + mode.label + " → "
                        + e.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        throw new RuntimeException("Failed to send email after trying: " + labels(modes), last);
    }

    /* ---------- Helpers ---------- */

    /** Build an attachment from a classpath resource stream (e.g., PPT/XLSX in src/test/resources). */
    public static MimeBodyPart buildAttachmentFromResource(InputStream in, String fileName, String contentType) throws Exception {
        MimeBodyPart part = new MimeBodyPart();
        ByteArrayDataSource ds = new ByteArrayDataSource(in, contentType);
        part.setDataHandler(new DataHandler(ds));
        part.setFileName(fileName);
        return part;
    }

    private static class Mode {
        final String port; final boolean starttls; final boolean ssl; final String label;
        Mode(String port, boolean starttls, boolean ssl, String label) {
            this.port = port; this.starttls = starttls; this.ssl = ssl; this.label = label;
        }
    }

    private static List<Mode> preferredModes(Properties cfg) {
        String pref = cfg.getProperty("mail.preferredPorts", "587,465,25").replace(" ", "");
        List<Mode> modes = new ArrayList<>();
        for (String p : pref.split(",")) {
            switch (p) {
                case "25"  -> modes.add(new Mode("25",  false, trueFalse(cfg.getProperty("forceNoSSLOn25")), "PLAIN:25"));
                case "587" -> modes.add(new Mode("587", true,  false, "STARTTLS:587"));
                case "465" -> modes.add(new Mode("465", false, true,  "SSL:465"));
                default    -> {}
            }
        }
        if (modes.isEmpty()) modes.add(new Mode("25", false, false, "PLAIN:25"));
        return modes;
    }

    private static boolean trueFalse(String v) { return v != null && (v.equals("true") || v.equals("1")); }

    private static Session buildSession(Properties cfg, Mode m) {
        // 1) Copy through any advanced SMTP flags you set in smtp.properties
        Properties props = new Properties();
        for (String k : cfg.stringPropertyNames()) {
            if (k.startsWith("mail.smtp.")) {
                props.put(k, cfg.getProperty(k));
            }
        }

        // 2) Essentials (these override to ensure the chosen mode is used)
        props.put("mail.smtp.host", cfg.getProperty("mail.host"));
        props.put("mail.smtp.port", m.port);
        props.put("mail.smtp.starttls.enable", String.valueOf(m.starttls));
        props.put("mail.smtp.ssl.enable", String.valueOf(m.ssl));

        // 3) Timeouts + EHLO host
        props.putIfAbsent("mail.smtp.connectiontimeout", "15000");
        props.putIfAbsent("mail.smtp.timeout",          "15000");
        props.putIfAbsent("mail.smtp.writetimeout",     "15000");
        props.putIfAbsent("mail.smtp.localhost",        cfg.getProperty("mail.localhost", "localhost"));

        // 4) Resolve creds: ENV wins; else use smtp.properties (trim + expand ${ENV_*} if present)
        String user = System.getenv("ENV_MAIL_USER");
        String pass = System.getenv("ENV_MAIL_PASS");
        if (user == null || user.isBlank()) user = resolveEnvPlaceholder(cfg.getProperty("mail.username"));
        if (pass == null || pass.isBlank()) pass = resolveEnvPlaceholder(cfg.getProperty("mail.password"));

        boolean haveCreds = user != null && !user.isBlank() && pass != null && !pass.isBlank();
        props.put("mail.smtp.auth", String.valueOf(haveCreds));

        // (Optional but helpful for O365 if both LOGIN and XOAUTH2 are offered)
        props.putIfAbsent("mail.smtp.auth.mechanisms", cfg.getProperty("mail.smtp.auth.mechanisms", "LOGIN"));
        props.putIfAbsent("mail.smtp.starttls.required", cfg.getProperty("mail.smtp.starttls.required", "true"));

        System.out.println("[SMTP] auth=" + haveCreds + " user=" + (user == null ? "<none>" : user));

        if (haveCreds) {
            final String u = user, p = pass;
            return Session.getInstance(props, new Authenticator() {
                @Override protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication(u, p);
                }
            });
        } else {
            return Session.getInstance(props);
        }
    }


    private static MimeMessage buildMessage(Session session, Properties cfg,
                                            String subject, String htmlBody,
                                            List<Path> fileAttachments,
                                            List<MimeBodyPart> extraParts) throws Exception {
        MimeMessage message = new MimeMessage(session);
        message.setFrom(new InternetAddress(cfg.getProperty("mail.from")));
        message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(cfg.getProperty("mail.to", "")));
        setIfPresent(message, Message.RecipientType.CC,  cfg.getProperty("mail.cc"));
        setIfPresent(message, Message.RecipientType.BCC, cfg.getProperty("mail.bcc"));
        message.setSubject(subject);

        MimeMultipart multipart = new MimeMultipart();

        // HTML body
        MimeBodyPart htmlPart = new MimeBodyPart();
        htmlPart.setContent(htmlBody, "text/html; charset=utf-8");
        multipart.addBodyPart(htmlPart);

        // Filesystem attachments
        if (fileAttachments != null) {
            for (Path p : fileAttachments) {
                if (p == null) continue;
                System.out.println("[Attach] " + p.toAbsolutePath() + " exists=" + Files.exists(p));
                if (Files.exists(p)) {
                    MimeBodyPart attach = new MimeBodyPart();
                    attach.attachFile(p.toFile());
                    attach.setFileName(p.getFileName().toString());
                    multipart.addBodyPart(attach);
                }
            }
        }

        // Extra MIME parts
        if (extraParts != null) {
            for (MimeBodyPart part : extraParts) {
                if (part != null) multipart.addBodyPart(part);
            }
        }

        message.setContent(multipart);
        return message;
    }

    private static void setIfPresent(MimeMessage msg, Message.RecipientType type, String csv) throws MessagingException {
        if (csv != null && !csv.isBlank()) msg.setRecipients(type, InternetAddress.parse(csv));
    }
    private static String firstNonBlank(String a, String b) { return (a != null && !a.isBlank()) ? a : ((b != null && !b.isBlank()) ? b : null); }
    private static String resolveEnvPlaceholder(String v) {
        if (v == null) return null;
        v = v.trim();
        if (v.startsWith("${") && v.endsWith("}")) {
            String key = v.substring(2, v.length() - 1);
            String fromEnv = System.getenv(key);
            return (fromEnv != null && !fromEnv.isBlank()) ? fromEnv : "";
        }
        return v;
    }
    private static boolean getBool(Properties cfg, String key, boolean def) {
        String v = cfg.getProperty(key);
        if (v == null) return def;
        return v.equalsIgnoreCase("true") || v.equalsIgnoreCase("yes") || v.equals("1");
    }
    private static String labels(List<Mode> modes) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < modes.size(); i++) { if (i > 0) sb.append(", "); sb.append(modes.get(i).label); }
        return sb.toString();
    }
}

