package PageEvent;

import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

public class MpsPageEvents {
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

    // ----------- MPS → Mps Plan -------------
    public void clickMpsPlan() {

        WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(30));
        JavascriptExecutor js = (JavascriptExecutor) BaseTest.driver;

        By mpsPlan = By.xpath(
                "//div[contains(@class,'mat-tab-label-content') and normalize-space()='Mps Plan']"
        );

        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(mpsPlan));
        js.executeScript("arguments[0].click();", element);

        System.out.println("✅ Clicked : Mps Plan");
    }
    public void validateDataImportStatus() {

        // Locate all rows in table
        List<WebElement> rows = BaseTest.driver.findElements(By.xpath("//table[@id='dataTable']/tbody/tr"));

        for (WebElement row : rows) {
            // Get 5th and 6th column values
            String importStatus = row.findElement(By.xpath("td[5]")).getText().trim();
            String validationStatus = row.findElement(By.xpath("td[6]")).getText().trim();

            if (!importStatus.equalsIgnoreCase("Success") || !validationStatus.equalsIgnoreCase("Success")) {
                System.out.println("❌ Row Failed: Import(" + importStatus + "), Validation(" + validationStatus + ")");
            } else {
                System.out.println("✅ Row Passed: Import(" + importStatus + "), Validation(" + validationStatus + ")");
            }
        }
    }
}
