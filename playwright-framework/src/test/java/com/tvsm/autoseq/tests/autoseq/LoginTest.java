package com.tvsm.autoseq.tests.autoseq;

import com.tvsm.autoseq.base.BaseTest;
import com.tvsm.autoseq.config.ConfigReader;
import com.tvsm.autoseq.pages.autoseq.HomePage;
import com.tvsm.autoseq.pages.autoseq.LoginPage;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTest — verifies the Microsoft SSO login flow.
 * Application: https://uat-sns.tvsmotor.net/Autoseq
 *
 *   TC-L-001  Login page loads and email field is visible
 *   TC-L-002  Enter email and click Next — password step appears
 *   TC-L-003  Enter password and sign in — app home loads
 *   TC-L-004  All main navigation tabs are visible after login
 */
public class LoginTest extends BaseTest {

    @Test(priority = 1,
          description = "TC-L-001: Verify login page loads and email field is visible")
    public void loginPageLoadsTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.navigate();
        logInfo("Navigated to: " + ConfigReader.baseUrl());
        Assert.assertTrue(loginPage.isLoginPageLoaded(),
                "Login page did not load — email input not visible");
        logInfo("✅ Login page loaded successfully");
        captureScreenshot("TC-L-001_LoginPageLoaded");
    }

    @Test(priority = 2,
          dependsOnMethods = "loginPageLoadsTest",
          description = "TC-L-002: Enter email and click Next — password step should appear")
    public void enterEmailAndClickNextTest() {
        LoginPage loginPage = new LoginPage(page);
        loginPage.enterEmail(ConfigReader.username());
        loginPage.clickNext();
        logInfo("Entered email: " + ConfigReader.username());
        Assert.assertTrue(loginPage.isPasswordStepVisible(),
                "Password input did not appear after clicking Next");
        logInfo("✅ Password step is visible");
        captureScreenshot("TC-L-002_PasswordStep");
    }

    @Test(priority = 3,
          dependsOnMethods = "enterEmailAndClickNextTest",
          description = "TC-L-003: Enter password and sign in — app home should load")
    public void successfulLoginTest() {
        LoginPage loginPage = new LoginPage(page);
        HomePage  homePage  = new HomePage(page);
        loginPage.enterPassword(ConfigReader.password());
        loginPage.clickSignIn();
        loginPage.handleStaySignedIn();
        waitForAppLoad();
        Assert.assertTrue(homePage.isLoggedIn(),
                "App shell not visible after login");
        logInfo("✅ Successfully logged in");
        captureScreenshot("TC-L-003_LoggedIn");
    }

    @Test(priority = 4,
          dependsOnMethods = "successfulLoginTest",
          description = "TC-L-004: Verify all main navigation tabs are visible after login")
    public void mainTabsVisibleAfterLoginTest() {
        HomePage homePage = new HomePage(page);
        String[] expectedTabs = {
                HomePage.TAB_SEQUENCE_LIVE,
                HomePage.TAB_NEW_SEQUENCE_LIVE,
                HomePage.TAB_MPS,
                HomePage.TAB_REPORT
        };
        for (String tab : expectedTabs) {
            Assert.assertTrue(homePage.isTabVisible(tab),
                    "Tab not visible after login: " + tab);
            logInfo("Tab visible: " + tab);
        }
        captureScreenshot("TC-L-004_MainTabsVisible");
    }
}
