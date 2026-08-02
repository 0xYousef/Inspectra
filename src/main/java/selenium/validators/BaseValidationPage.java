package selenium.validators;

import lombok.RequiredArgsConstructor;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.HomePage;

@RequiredArgsConstructor
public abstract class BaseValidationPage {

    protected final WebDriver driver;

    public String getTitle() {
        String title_xpath = "//h2[@class='title text-center' and contains(@style, 'color: green')]";
        WebElement titleElement = driver.findElement(By.xpath(title_xpath));
        return titleElement.getText().strip();
    }

    public String getDescription() {
        String description_xpath = "//p[contains(@style, 'font-size: 20px')]";
        WebElement descriptionElement = driver.findElement(By.xpath(description_xpath));
        return descriptionElement.getText().strip();
    }

    public HomePage _continue() {
        String continue_xpath = "//a[@data-qa='continue-button']";
        WebElement continueElement = driver.findElement(By.xpath(continue_xpath));
        continueElement.click();
        return new HomePage(driver);
    }
}
