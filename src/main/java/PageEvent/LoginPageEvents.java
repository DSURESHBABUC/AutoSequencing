package PageEvent;
import base.BaseTest;
import org.openqa.selenium.By;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;


import PageObject.LoginPageElements;
import utils.ElementFetch;

import java.time.Duration;

public class LoginPageEvents {
	ElementFetch ele = new ElementFetch();


        public void loginCredentials() {

            ele.getWebElement("XPATH", "//input[@id='i0116']")
                    .sendKeys("purna.chandra@tvsmotor.com");

            ele.getWebElement("XPATH", "//input[@id='idSIButton9']").click();

            WebDriverWait wait = new WebDriverWait(BaseTest.driver, Duration.ofSeconds(20));
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.id("i0118")))
                    .sendKeys("Oct2025@01");
        }

}
