package selenium.validators;


import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;


public class OrderValidationPage extends BaseValidationPage  {

    public OrderValidationPage(WebDriver driver) {
        super(driver);
    }


    public void downloadInvoice() {
        String download_xpath = "//a[contains(@href, '/download_invoice/')]";
        WebElement download = this.driver.findElement(By.xpath(download_xpath));
        download.click();
    }
}
