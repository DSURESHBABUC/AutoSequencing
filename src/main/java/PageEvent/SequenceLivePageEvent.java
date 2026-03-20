package PageEvent;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebElement;
import utils.ElementFetch;
import base.BaseTest;
import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
public class SequenceLivePageEvent {
    WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(25));
    JavascriptExecutor js = (JavascriptExecutor) BaseTest.driver;

    // ---------------- SAFE MAT-SELECT HANDLER ----------------
    public void selectMatOption(String selectXpath, String optionText) {

        WebElement dropdown = wait.until(
                ExpectedConditions.elementToBeClickable(By.xpath(selectXpath))
        );

        if (dropdown.isDisplayed() && dropdown.isEnabled()) {
            js.executeScript("arguments[0].click();", dropdown);
            System.out.println("🟩 Opened dropdown: " + selectXpath);
        } else {
            System.out.println("❌ Dropdown disabled: " + selectXpath);
            return;
        }

        // Select option
        By optionXpath = By.xpath("//mat-option//span[normalize-space()='" + optionText + "']");
        WebElement option = wait.until(ExpectedConditions.elementToBeClickable(optionXpath));

        js.executeScript("arguments[0].click();", option);
        System.out.println("✅ Selected option: " + optionText);
    }

    // ---------------- SELECT PLANT ----------------
    public void selectPlant() {
        selectMatOption("//mat-select[@id='mat-select-0']", "Plant 2");
    }

    // ---------------- SELECT UNIT ----------------
    public void selectUnit() {
        selectMatOption("//mat-select[@id='mat-select-2']", "EA");
    }

    // ---------------- SELECT CONVEYOR ----------------
    public void selectConveyor() {
        selectMatOption("//mat-select[@id='mat-select-4']", "2ECON400");
    }

    // ---------------- SELECT SHIFT ----------------
    public void selectShift() {
        selectMatOption("//mat-select[@id='mat-select-6']", "ALL");
    }

    // ---------------- ENTER DATE ----------------
    public void enterDate(String date) {

        WebElement dateInput = wait.until(
                ExpectedConditions.visibilityOfElementLocated(By.id("mat-input-0"))
        );

        dateInput.clear();
        dateInput.sendKeys(date);

        System.out.println("📅 Entered date: " + date);
    }

    // ---------------- MASTER METHOD ----------------
    public void fillAllFilters() {

        selectPlant();
        selectUnit();
        selectConveyor();
        enterDate("12/12/2025");
        selectShift();

        System.out.println("🎉 All dropdowns + date selected successfully!");
    }
}
