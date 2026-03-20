package PageEvent;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;


public class DataMantanancePageEvents{
    // ---------------- SAFE CLICK (COMMON METHOD) ----------------
    public void safeClick(WebElement element, String name) {

        try {
            if (element.isDisplayed() && element.isEnabled()) {
                JavascriptExecutor js = (JavascriptExecutor) BaseTest.driver;
                js.executeScript("arguments[0].click();", element);
                System.out.println(" Clicked: " + name);
            } else {
                System.out.println(" Element disabled or not clickable: " + name);
            }
        } catch (Exception e) {
            System.out.println("⚠️ Failed to click (exception): " + name);
        }
    }
public void clickMainTab(String tabName) {

    WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(40));
    JavascriptExecutor js = (JavascriptExecutor) BaseTest.driver;

    By tabLocator = By.xpath(
            "//div[contains(@class,'mat-tab-label-content') and normalize-space()='" + tabName + "']"
    );

    WebElement tab = wait.until(ExpectedConditions.elementToBeClickable(tabLocator));
    js.executeScript("arguments[0].click();", tab);

    System.out.println("✅ Clicked main tab : " + tabName);
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
