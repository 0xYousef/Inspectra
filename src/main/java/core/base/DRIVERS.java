package core.base;

import core.utils.Configuration;
import core.utils.Downloads;
import data.exceptions.DriverNotFoundException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.safari.SafariDriver;

public enum DRIVERS {
    CHROME, FIREFOX, EDGE,  SAFARI;
    public  WebDriver getDriver() {
        return switch (this) {
            case CHROME -> new ChromeDriver(Downloads.applyTo(new ChromeOptions()));
            case FIREFOX -> {
                String geckoDriver = Configuration.get("browser.gecko.driver");
                if (geckoDriver != null && !geckoDriver.isBlank()) {
                    System.setProperty("webdriver.gecko.driver", geckoDriver);
                }
                FirefoxOptions options = new FirefoxOptions();
                String firefoxBinary = Configuration.get("browser.firefox.binary");
                if (firefoxBinary != null && !firefoxBinary.isBlank()) {
                    options.setBinary(firefoxBinary);
                }
                yield new FirefoxDriver(Downloads.applyTo(options));
            }
            case EDGE -> new EdgeDriver(Downloads.applyTo(new EdgeOptions()));
            case SAFARI -> new SafariDriver();
            default -> throw new DriverNotFoundException("Driver Not Found");
        };

    }
}
