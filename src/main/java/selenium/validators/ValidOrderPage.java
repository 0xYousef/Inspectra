package selenium.validators;

import selenium.pages.BasePage;
import data.expectations.Expectations;
import core.utils.Downloads;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import selenium.pages.HomePage;
import cache.services.CartServiceImpl;
import cache.services.SessionServiceImpl;

import static support.helpers.ReadFile.readTXTFile;

public class ValidOrderPage extends BasePage {
    private final CartServiceImpl cartService;
    private static SessionServiceImpl sessionService;
    private final int TOTAL_PRICE;

    public ValidOrderPage(WebDriver driver) {
        super(driver);

        cartService = new CartServiceImpl();
        sessionService = new SessionServiceImpl();
        TOTAL_PRICE = cartService.calculateCartTotal();
        cartService.completeOrder();

    }

    public static void  downloadInvoice() throws InterruptedException {
        String filePath = Downloads.file("invoice.txt");

        log.info("Download button is clicked");
        Thread.sleep(4000);
        String content = readTXTFile(filePath);
        log.info("content: {}", content);
        if (content.equals(Expectations.Ui.Order.ORDER_INVOICE.formatted("anon anon",9900))) {
            log.info("Invoices have been verified");
        }
        else {
            log.info("Invoices have not been verified");
        }
    }

    public String verifyOrderSuccess() {
        String success_xpath = "//div[@id='success_message']";
        WebElement successMessage = driver.findElement(By.xpath(success_xpath));
        return successMessage.getText();
    }

    public HomePage continueToHomePage() {
        cartService.completeOrder();
        return new HomePage(driver);
    }
}
