package com.tvsm.autoseq.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

/**
 * Reads config.properties from the classpath.
 * System properties (-Dkey=value) always override file values.
 */
public class ConfigReader {

    private static final Properties props = new Properties();

    static {
        try (InputStream in = ConfigReader.class
                .getClassLoader()
                .getResourceAsStream("config.properties")) {
            if (in != null) {
                props.load(in);
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load config.properties", e);
        }
    }

    /** Returns system property first, then config file value, then the supplied default. */
    public static String get(String key, String defaultValue) {
        String sys = System.getProperty(key);
        if (sys != null && !sys.isBlank()) return sys.trim();
        return props.getProperty(key, defaultValue).trim();
    }

    public static String get(String key) {
        return get(key, "");
    }

    // ── Convenience accessors ──────────────────────────────────────────────

    public static String baseUrl() {
        return get("base.url", "https://uat-sns.tvsmotor.net/Autoseq/login");
    }

    public static String groupMasterUrl() {
        return get("groupmaster.url", "https://uat-sns.tvsmotor.net/Autoseq/groupmaster");
    }

    public static String knowledgeGraphUrl() {
        return get("knowledgegraph.url", "https://uat-eyeq.tvsmotor.net/knowledgegraph2.0");
    }

    public static String dvpSummaryUrl() {
        return get("dvpsummary.url", "https://uat-eyeq.tvsmotor.net/knowledgegraphdvpsummary");
    }

    // ── Manual Sequencing (QAS) ───────────────────────────────────────────

    public static String manualSeqBaseUrl() {
        return get("manualseq.base.url", "https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/login");
    }

    public static String manualSeqGroupMasterUrl() {
        return get("manualseq.groupmaster.url", "https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/groupmaster");
    }

    public static String manualSeqHomeUrl() {
        return get("manualseq.home.url", "https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest/");
    }

    public static String browser() {
        return get("browser", "chromium");
    }

    public static boolean headless() {
        return Boolean.parseBoolean(get("headless", "false"));
    }

    public static int defaultTimeout() {
        return Integer.parseInt(get("default.timeout.ms", "30000"));
    }

    public static String username() {
        return get("app.username", "d.sureshbabu@tvsmotor.com");
    }

    public static String password() {
        return get("app.password", "Ramesh@1983");
    }

    public static String screenshotDir() {
        return get("screenshot.dir", "reports/screenshots");
    }

    public static String reportDir() {
        return get("report.dir", "reports");
    }

    // ── Confluence ────────────────────────────────────────────────────────

    public static boolean confluenceEnabled() {
        return Boolean.parseBoolean(get("confluence.enabled", "false"));
    }

    public static String confluenceBaseUrl() {
        return get("confluence.base.url", "");
    }

    public static String confluenceSpaceKey() {
        return get("confluence.space.key", "QA");
    }

    public static String confluencePageTitle() {
        return get("confluence.page.title", "TVS AutoSeq — Playwright Test Results");
    }

    public static String confluenceParentPageId() {
        return get("confluence.parent.page.id", "");
    }

    public static String confluenceUsername() {
        return get("confluence.username", "");
    }

    public static String confluenceApiToken() {
        return get("confluence.api.token", "");
    }

    // ── Email ─────────────────────────────────────────────────────────────

    public static boolean emailEnabled() {
        return Boolean.parseBoolean(get("email.enabled", "false"));
    }

    public static String emailSmtpHost() {
        return get("email.smtp.host", "smtp.gmail.com");
    }

    public static int emailSmtpPort() {
        return Integer.parseInt(get("email.smtp.port", "587"));
    }

    public static boolean emailSmtpAuth() {
        return Boolean.parseBoolean(get("email.smtp.auth", "true"));
    }

    public static boolean emailSmtpStartTls() {
        return Boolean.parseBoolean(get("email.smtp.starttls", "true"));
    }

    public static String emailFrom() {
        return get("email.from", "");
    }

    public static String emailTo() {
        return get("email.to", "");
    }

    public static String emailUsername() {
        return get("email.username", "");
    }

    public static String emailPassword() {
        return get("email.password", "");
    }
}
