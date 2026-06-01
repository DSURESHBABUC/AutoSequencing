package com.tvsm.autoseq.base;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import com.microsoft.playwright.*;
import com.tvsm.autoseq.config.ConfigReader;
import org.testng.ITestResult;
import org.testng.annotations.*;

import java.io.File;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * BaseTest — parent of every test class.
 *
 * Auth strategy:
 *   @BeforeSuite → login once via SSO, save storageState to auth/state.json
 *   @BeforeTest  → open fresh browser context loaded from saved auth state
 *                  (no SSO re-login needed for subsequent <test> blocks)
 *   @AfterTest   → close context + browser
 *   @AfterSuite  → flush report
 */
public class BaseTest {

    // ── Shared auth state file path ───────────────────────────────────────
    private static final String AUTH_STATE_PATH = "auth/state.json";

    // ── Playwright objects ────────────────────────────────────────────────
    protected Playwright     playwright;
    protected Browser        browser;
    protected BrowserContext context;
    protected Page           page;

    // ── Reporting ─────────────────────────────────────────────────────────
    private static ExtentReports extent;
    protected      ExtentTest    logger;

    // ─────────────────────────────────────────────────────────────────────
    // SUITE SETUP — runs once: set up report + perform SSO login + save state
    // ─────────────────────────────────────────────────────────────────────
    @BeforeSuite(alwaysRun = true)
    public void setUpSuite() {
        // ── 1. Extent report ──────────────────────────────────────────────
        new File(ConfigReader.reportDir()).mkdirs();
        String reportPath = ConfigReader.reportDir() + File.separator + "PlaywrightTestReport.html";

        ExtentSparkReporter spark = new ExtentSparkReporter(reportPath);
        spark.config().setTheme(Theme.DARK);
        spark.config().setDocumentTitle("TVS AutoSeq — Playwright Report");
        spark.config().setReportName("Playwright Automation Results");

        extent = new ExtentReports();
        extent.attachReporter(spark);
        extent.setSystemInfo("Application", "TVS AutoSequencing (SNS)");
        extent.setSystemInfo("Environment", "UAT");
        extent.setSystemInfo("Browser", ConfigReader.browser());
        extent.setSystemInfo("Base URL", ConfigReader.baseUrl());

        // ── 2. Login once and save auth state ─────────────────────────────
        performLoginAndSaveState();
    }

    /**
     * Opens a temporary browser, completes the full Microsoft SSO login,
     * waits for the Angular app to load, then saves the browser storage
     * state (cookies + localStorage) to AUTH_STATE_PATH.
     *
     * All subsequent test contexts load this saved state — no SSO re-login.
     */
    private void performLoginAndSaveState() {
        Path authFile = Paths.get(AUTH_STATE_PATH);

        // Re-use existing state if it's less than 8 hours old
        if (Files.exists(authFile)) {
            try {
                long ageMs = System.currentTimeMillis()
                        - Files.getLastModifiedTime(authFile).toMillis();
                if (ageMs < 8 * 60 * 60 * 1000L) {
                    System.out.println("♻️  Reusing existing auth state: " + AUTH_STATE_PATH);
                    return;
                }
            } catch (Exception ignored) {}
        }

        System.out.println("🔐 Performing SSO login to generate auth state...");
        new File("auth").mkdirs();

        Playwright pw = Playwright.create();
        Browser    br = pw.chromium().launch(
                new BrowserType.LaunchOptions()
                        .setHeadless(false)
                        .setSlowMo(100));

        BrowserContext ctx = br.newContext(new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setIgnoreHTTPSErrors(true));
        ctx.setDefaultTimeout(60000);

        Page loginPage = ctx.newPage();

        try {
            // Navigate — MSAL will redirect to Microsoft login
            loginPage.navigate(ConfigReader.baseUrl());

            // Wait for Microsoft email field
            loginPage.waitForSelector("#i0116",
                    new Page.WaitForSelectorOptions().setTimeout(30000));
            loginPage.fill("#i0116", ConfigReader.username());

            // Next button — wait until enabled
            loginPage.waitForSelector("#idSIButton9:not([disabled])",
                    new Page.WaitForSelectorOptions().setTimeout(15000));
            loginPage.click("#idSIButton9");

            // Password field
            loginPage.waitForSelector("#i0118",
                    new Page.WaitForSelectorOptions().setTimeout(15000));
            loginPage.fill("#i0118", ConfigReader.password());

            // Sign In — triggers MSAL redirect; wrap in try/catch for navigation
            loginPage.waitForSelector("#idSIButton9:not([disabled])",
                    new Page.WaitForSelectorOptions().setTimeout(15000));
            try {
                loginPage.waitForNavigation(
                        new Page.WaitForNavigationOptions().setTimeout(30000),
                        () -> loginPage.evaluate("document.querySelector('#idSIButton9').click()")
                );
            } catch (Exception e) {
                System.out.println("ℹ️  Sign-in navigation: " + e.getMessage());
            }

            // KMSI "Stay signed in?" prompt — click Yes if present
            try {
                loginPage.waitForSelector("#idSIButton9",
                        new Page.WaitForSelectorOptions().setTimeout(8000));
                try {
                    loginPage.waitForNavigation(
                            new Page.WaitForNavigationOptions().setTimeout(20000),
                            () -> loginPage.evaluate("document.querySelector('#idSIButton9').click()")
                    );
                } catch (Exception e) {
                    System.out.println("ℹ️  KMSI navigation: " + e.getMessage());
                }
                System.out.println("✅ Handled KMSI prompt");
            } catch (Exception e) {
                System.out.println("ℹ️  No KMSI prompt");
            }

            // Wait for Angular app to fully load
            waitForAngularApp(loginPage);

            // Save auth state
            ctx.storageState(new BrowserContext.StorageStateOptions()
                    .setPath(Paths.get(AUTH_STATE_PATH)));
            System.out.println("💾 Auth state saved to: " + AUTH_STATE_PATH);

        } catch (Exception e) {
            System.out.println("⚠️  Auth state generation failed: " + e.getMessage());
            // Continue anyway — tests will attempt login themselves
        } finally {
            ctx.close();
            br.close();
            pw.close();
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // TEST SETUP — runs once per <test> block; loads saved auth state
    // ─────────────────────────────────────────────────────────────────────
    @Parameters({"browser", "headless"})
    @BeforeTest(alwaysRun = true)
    public void setUpBrowser(
            @Optional("chromium") String browserName,
            @Optional("false")    String headlessStr) {

        boolean headless = Boolean.parseBoolean(headlessStr);

        playwright = Playwright.create();

        BrowserType.LaunchOptions launchOptions = new BrowserType.LaunchOptions()
                .setHeadless(headless)
                .setSlowMo(headless ? 0 : 50);

        browser = switch (browserName.toLowerCase()) {
            case "firefox" -> playwright.firefox().launch(launchOptions);
            case "webkit"  -> playwright.webkit().launch(launchOptions);
            default        -> playwright.chromium().launch(launchOptions);
        };

        // Load saved auth state if it exists — skips SSO for this context
        Browser.NewContextOptions ctxOptions = new Browser.NewContextOptions()
                .setViewportSize(1440, 900)
                .setIgnoreHTTPSErrors(true);

        Path authFile = Paths.get(AUTH_STATE_PATH);
        if (Files.exists(authFile)) {
            ctxOptions.setStorageStatePath(authFile);
            System.out.println("🔑 Loaded auth state from: " + AUTH_STATE_PATH);
        } else {
            System.out.println("⚠️  No auth state found — browser will need to login");
        }

        context = browser.newContext(ctxOptions);
        context.setDefaultTimeout(ConfigReader.defaultTimeout());
        page = context.newPage();

        System.out.println("🚀 Browser launched: " + browserName + " | headless=" + headless);
    }

    // ─────────────────────────────────────────────────────────────────────
    // METHOD SETUP
    // ─────────────────────────────────────────────────────────────────────
    @BeforeMethod(alwaysRun = true)
    public void beforeMethod(Method method) {
        logger = extent.createTest(method.getName());
        System.out.println("\n▶ Starting test: " + method.getName());
    }

    // ─────────────────────────────────────────────────────────────────────
    // METHOD TEARDOWN
    // ─────────────────────────────────────────────────────────────────────
    @AfterMethod(alwaysRun = true)
    public void afterMethod(ITestResult result) {
        String name = result.getMethod().getMethodName();

        switch (result.getStatus()) {
            case ITestResult.FAILURE -> {
                String screenshotPath = null;
                try {
                    screenshotPath = captureScreenshot(name + "_FAIL");
                } catch (Exception ignored) {
                    System.out.println("⚠️  Could not capture screenshot");
                }
                logger.log(Status.FAIL,
                        MarkupHelper.createLabel(name + " — FAILED", ExtentColor.RED));
                logger.log(Status.FAIL, result.getThrowable().getMessage());
                if (screenshotPath != null) {
                    logger.addScreenCaptureFromPath(screenshotPath, "Failure Screenshot");
                }
                System.out.println("❌ FAILED: " + name);
            }
            case ITestResult.SKIP -> {
                logger.log(Status.SKIP,
                        MarkupHelper.createLabel(name + " — SKIPPED", ExtentColor.ORANGE));
                System.out.println("⏭ SKIPPED: " + name);
            }
            default -> {
                logger.log(Status.PASS,
                        MarkupHelper.createLabel(name + " — PASSED", ExtentColor.GREEN));
                System.out.println("✅ PASSED: " + name);
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────
    // TEST TEARDOWN
    // ─────────────────────────────────────────────────────────────────────
    @AfterTest(alwaysRun = true)
    public void tearDownBrowser() {
        if (context    != null) try { context.close();    } catch (Exception ignored) {}
        if (browser    != null) try { browser.close();    } catch (Exception ignored) {}
        if (playwright != null) try { playwright.close(); } catch (Exception ignored) {}
        System.out.println("🛑 Browser closed.");
    }

    // ─────────────────────────────────────────────────────────────────────
    // SUITE TEARDOWN
    // ─────────────────────────────────────────────────────────────────────
    @AfterSuite(alwaysRun = true)
    public void tearDownSuite() {
        if (extent != null) extent.flush();
        System.out.println("📊 Extent report flushed.");
    }

    // ─────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────

    /** Captures a full-page screenshot and returns the file path. */
    protected String captureScreenshot(String fileName) {
        String dir = ConfigReader.screenshotDir();
        new File(dir).mkdirs();
        String path = dir + File.separator + fileName + ".png";
        page.screenshot(new Page.ScreenshotOptions()
                .setPath(Paths.get(path))
                .setFullPage(true));
        return path;
    }

    /** Logs an info message to the current ExtentTest node. */
    protected void logInfo(String message) {
        if (logger != null) logger.log(Status.INFO, message);
        System.out.println("ℹ️  " + message);
    }

    /**
     * Waits for the Angular app shell to appear after login/navigation.
     * Tries multiple selectors that indicate the app has bootstrapped.
     */
    protected void waitForAppLoad() {
        waitForAngularApp(page);
    }

    private void waitForAngularApp(Page p) {
        String[] indicators = {
            ".mat-tab-label-content",
            ".mat-mdc-tab-label-content",
            "[role='tab']",
            "app-root",
            "nav"
        };
        for (String sel : indicators) {
            try {
                p.waitForSelector(sel,
                        new Page.WaitForSelectorOptions().setTimeout(20000));
                System.out.println("✅ App loaded — detected: " + sel);
                return;
            } catch (Exception ignored) {}
        }
        System.out.println("⚠️  App load indicator not found — continuing anyway");
    }
}
