package PageEvent;

import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utils.ElementFetch;
import base.BaseTest;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;

public class HomePageEvents {

    ElementFetch ele = new ElementFetch();

    // ---------------- LOGIN ----------------
    public void loginCredentials() {

        ele.getWebElement("XPATH", "//input[@id='i0116']")
                .sendKeys("purna.chandra@tvsmotor.com");

        ele.getWebElement("XPATH", "//input[@id='idSIButton9']").click();

        WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(20));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")))
                .sendKeys("Oct2025@01");
    }

    public void signIn() {
        ele.getWebElement("XPATH", "//input[@id='idSIButton9']").click();
    }

    // ---------------- SAFE CLICK (COMMON METHOD) ----------------
    public void safeClick(WebElement element, String name) {

        try {
            if (element.isDisplayed() && element.isEnabled()) {
                JavascriptExecutor js = (JavascriptExecutor) BaseTest.driver;
                js.executeScript("arguments[0].click();", element);
                System.out.println("✅ Clicked: " + name);
            } else {
                System.out.println("❌ Element disabled or not clickable: " + name);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to click (exception): " + name);
        }
    }

    // ----------- CLICK MAIN TAB (Reusable) -------------
    public void clickMainTab(String tabName) {

        WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(40));

        By tabLocator = By.xpath(
                "//div[contains(@class,'mat-tab-label-content') and normalize-space()='" + tabName + "']"
        );

        WebElement tab = wait.until(ExpectedConditions.visibilityOfElementLocated(tabLocator));
        safeClick(tab, tabName);
    }

    // ----------- MPS → Mps Plan -------------
    public void clickMpsPlan() {

        WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(30));

        By mpsPlan = By.xpath(
                "//div[contains(@class,'mat-tab-label-content') and normalize-space()='Mps Plan']"
        );

        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(mpsPlan));
        safeClick(element, "Mps Plan");
    }

    // ----------- Go to Data Maintenance -------------
    public void goToDataMaintenance() throws InterruptedException {
        clickMainTab("Data Maintenance");
        Thread.sleep(1500);
        System.out.println("✅ On Data Maintenance tab");
    }

    // ----------- Go to Sequence Live -------------
    public void goToSequenceLive() throws InterruptedException {
        clickMainTab("Sequence Live");
        Thread.sleep(1500);
    }

    // ----------- Click items inside Data Maintenance -------------
    public void clickDataMaintenanceItem(String itemName) throws InterruptedException {

        WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(30));

        By itemLocator = By.xpath("//div[@class='list-title' and normalize-space()='" + itemName + "']");
        WebElement element = wait.until(ExpectedConditions.visibilityOfElementLocated(itemLocator));

        safeClick(element, itemName);

        Thread.sleep(3000); // Wait after click
    }
}
