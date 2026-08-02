package selenium.components;

import data.exceptions.ExceptionHandler;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Duration;

public class Scrolling {

    private static final Logger log = LoggerFactory.getLogger(Scrolling.class);
    private final WebDriver driver;

    public Scrolling(WebDriver driver) {
        this.driver = driver;
    }

    public void downWithMouse() {
        scrollWithMouse("window.scrollBy(0, window.innerHeight)");
        log.info("Scrolled down with mouse.");
    }

    public void upWithMouse() {
        scrollWithMouse("window.scrollTo({ top: 0, behavior: 'smooth' });");
        log.info("Scrolled up with mouse.");
    }

    public void downWithArrow() {
        scrollWithKeyboard(Keys.ARROW_DOWN);
        log.info("Scrolled down using arrow key.");
    }

    public void upWithArrow() {
        scrollWithKeyboard(Keys.ARROW_UP);
        log.info("Scrolled up using arrow key.");
    }

     public void scrollToTopByArrowUp() {
        String XPATH = "//a[@id='scrollUp']";
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(2));

        try {
            WebElement arrowUp = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath(XPATH)));
            log.info("Arrow up is visible, clicking...");
            arrowUp.click();
        } catch (TimeoutException e) {
            ExceptionHandler.handleSilently(e, "waiting for arrow-up visibility");
        } catch (ElementNotInteractableException e) {
            ExceptionHandler.handleSilently(e, "clicking arrow-up");
        } catch (NoSuchElementException e) {
            ExceptionHandler.handleSilently(e, "finding arrow-up element");
        }
    }

     private void scrollWithMouse(String script) {
        try {
            JavascriptExecutor js = (JavascriptExecutor) driver;
            js.executeScript(script);
        } catch (Exception e) {
            ExceptionHandler.handleSilently(e, "scrolling with mouse");
        }
    }

    private void scrollWithKeyboard(Keys key) {
        try {
            driver.findElement(By.tagName("body")).sendKeys(key);
        } catch (NoSuchElementException e) {
            ExceptionHandler.handleSilently(e, "sending key to body tag");
        }
    }
}
