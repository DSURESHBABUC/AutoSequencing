package com.tvsm.autoseq.tests.manualseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.manualseq.ManualSeqHomePage;
import com.tvsm.autoseq.pages.manualseq.ManualSeqLoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — verifies the Microsoft SSO login flow for Manual Sequencing (QAS).
 * Application: https://tvsmsrvrqas.tvsmotor.net/SequencePlanTest
 *
 *   TC-MSL-001  Login page loads and email field is visible
 *   TC-MSL-002  Enter email and click Next — password step appears
 *   TC-MSL-003  Enter password and sign in — app home loads
 *   TC-MSL-004  Navigation tabs are visible after login
 *   TC-MSL-005  URL contains SequencePlanTest after login
 */
public class LoginTest extends BaseTest {

    @Test(priority = 1,
          description = "TC-MSL-001: Verify login page loads and email field is visible")
    public void loginPageLoadsTest() {
        ManualSeqLoginPage loginPage = new ManualSeqLoginPage(page);
        loginPage.navigate();
        logInfo("Navigated to: " + ConfigReader.manualSeqBaseUrl());
        Assert.assertTrue(loginPage.isLoginPageLoaded(),
                "Login page did not load — email input not visible");
        logInfo("✅ Login page loaded successfully");
        captureScreenshot("TC-MSL-001_LoginPageLoaded");
    }

    @Test(priority = 2,
          dependsOnMethods = "loginPageLoadsTest",
          description = "TC-MSL-002: Enter email and click Next — password step should appear")
    public void enterEmailAndClickNextTest() {
        ManualSeqLoginPage loginPage = new ManualSeqLoginPage(page);
        loginPage.enterEmail(ConfigReader.username());
        loginPage.clickNext();
        logInfo("Entered email: " + ConfigReader.username());
        Assert.assertTrue(loginPage.isPasswordStepVisible(),
                "Password input did not appear after clicking Next");
        logInfo("✅ Password step is visible");
        captureScreenshot("TC-MSL-002_PasswordStep");
    }

    @Test(priority = 3,
          dependsOnMethods = "enterEmailAndClickNextTest",
          description = "TC-MSL-003: Enter password and sign in — app home should load")
    public void successfulLoginTest() {
        ManualSeqLoginPage loginPage = new ManualSeqLoginPage(page);
        ManualSeqHomePage  homePage  = new ManualSeqHomePage(page);
        loginPage.enterPassword(ConfigReader.password());
        loginPage.clickSignIn();
        loginPage.handleStaySignedIn();
        waitForAppLoad();
        Assert.assertTrue(homePage.isLoggedIn(),
                "App shell not visible after login");
        logInfo("✅ Successfully logged in to Manual Sequencing");
        captureScreenshot("TC-MSL-003_LoggedIn");
    }

    @Test(priority = 4,
          dependsOnMethods = "successfulLoginTest",
          description = "TC-MSL-004: Verify navigation tabs are visible after login")
    public void navigationTabsVisibleAfterLoginTest() {
        ManualSeqHomePage homePage = new ManualSeqHomePage(page);
        int tabCount = homePage.getVisibleTabCount();
        logInfo("Visible tabs after login: " + tabCount);
        Assert.assertTrue(tabCount > 0,
                "No navigation tabs visible after login");
        captureScreenshot("TC-MSL-004_TabsVisible");
    }

    @Test(priority = 5,
          dependsOnMethods = "successfulLoginTest",
          description = "TC-MSL-005: Verify URL contains SequencePlanTest after login")
    public void urlContainsAppPathTest() {
        ManualSeqHomePage homePage = new ManualSeqHomePage(page);
        String url = homePage.getPageUrl();
        logInfo("Current URL: " + url);
        Assert.assertTrue(url.contains("SequencePlanTest"),
                "URL does not contain 'SequencePlanTest'. Actual: " + url);
        captureScreenshot("TC-MSL-005_UrlVerified");
    }
}
