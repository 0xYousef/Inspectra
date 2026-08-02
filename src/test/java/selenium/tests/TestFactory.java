package selenium.tests;


import core.base.DEVICES;
import core.base.DRIVERS;
import org.testng.annotations.Factory;
import selenium.tests.contactus.TC06_ContactUs_Test;
import selenium.tests.login.TC02_Login_ValidTest;
import selenium.tests.login.TC03_Login_InvalidTest;
import selenium.tests.login.TC04_Logout_Test;
import selenium.tests.register.TC01_Registration_ValidTest;
import selenium.tests.register.TC05_Registration_InvalidTest;
import selenium.tests.testcases.TC07_TestCases_Test;
import selenium.tests.train.TestCurrentDev;


public class TestFactory {

    @Factory
    public Object[] createInstances() {
        DRIVERS driver = DRIVERS.CHROME;
        return new Object[] {
                new TestCurrentDev(driver, DEVICES.MAXSIZE),
                new TC01_Registration_ValidTest(),
                new TC02_Login_ValidTest(),
                new TC03_Login_InvalidTest(),
                new TC04_Logout_Test(),
                new TC05_Registration_InvalidTest(),
                new TC06_ContactUs_Test(),
                new TC07_TestCases_Test(),

        };
    }

}