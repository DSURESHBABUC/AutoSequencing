package base;

import java.io.File;
import java.lang.reflect.Method;
import java.time.Duration;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Parameters;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.markuputils.ExtentColor;
import com.aventstack.extentreports.markuputils.MarkupHelper;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import io.github.bonigarcia.wdm.WebDriverManager;
import utils.Constants;

public class BaseTest {

    public static WebDriver driver;
    public ExtentSparkReporter sparkReporter;
    public ExtentReports extent;
    public ExtentTest logger;

    // REPORT SETUP - runs once before all tests in this <test> (testng.xml)
    @BeforeTest
    public void beforeTestMethod() {
        sparkReporter = new ExtentSparkReporter(System.getProperty("user.dir")
                + File.separator + "reports" + File.separator + "TestReports.html");
        extent = new ExtentReports();
        extent.attachReporter(sparkReporter);

        sparkReporter.config().setTheme(Theme.DARK);
        extent.setSystemInfo("HostName", "Purna");
        extent.setSystemInfo("UserName", "Purna");
        sparkReporter.config().setDocumentTitle("Automation Report");
        sparkReporter.config().setReportName("Automation Tests Results by TVSM");
    }

    // DRIVER SETUP - runs once before all @Test methods (so browser is opened once)
    @Parameters("browser")
    @BeforeTest(dependsOnMethods = "beforeTestMethod")
    public void setUpDriverOnce(String browser) {
        setupDriver(browser);
        driver.manage().window().maximize();
        driver.get(Constants.url);
        driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(20));
    }

    // logger per-method - still runs before every test method to create test entry in report
    @BeforeMethod
    public void beforeMethod(Method testMethod) {
        logger = extent.createTest(testMethod.getName());
    }

    // report logging per method - but DO NOT quit the driver here
    @AfterMethod
    public void afterMethod(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.log(Status.FAIL, MarkupHelper.createLabel(result.getName() + " - Test Case Failed", ExtentColor.RED));
            logger.log(Status.FAIL, MarkupHelper.createLabel(result.getThrowable() + " - Test Case Failed", ExtentColor.RED));
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.log(Status.SKIP, MarkupHelper.createLabel(result.getName() + " - Test Case Skipped", ExtentColor.ORANGE));
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.log(Status.PASS, MarkupHelper.createLabel(result.getName() + " - Test Case PASS", ExtentColor.GREEN));
        }
        // IMPORTANT: do NOT call driver.quit() here — we want the browser to remain open for next test
    }

    // CLEANUP - quit driver and flush report after all tests in this <test> complete
    @AfterTest
    public void tearDownAll() {
        if (driver != null) {
            driver.quit();
        }
        extent.flush();
    }

    // keep this method as-is (or expand to support other browsers)
    public void setupDriver(String browser) {
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        // optionally add other browser branches (firefox, edge) using 'browser' parameter
    }
}
