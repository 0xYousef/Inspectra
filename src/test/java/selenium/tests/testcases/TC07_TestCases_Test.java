package selenium.tests.testcases;

import core.base.BaseUITest;
import core.base.DRIVERS;
import data.expectations.Expectations;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.pages.testcases.TestCasePage;

import static org.testng.Assert.assertEquals;

public class TC07_TestCases_Test extends BaseUITest {

    public TC07_TestCases_Test() {
        super(DRIVERS.CHROME);
    }
    @Test
    public void verifyTestCasesPage() {
        HomePage homePage = new HomePage(driver);
        TestCasePage page = homePage.navigateTo().TestCasesPage();
        String expected_message = Expectations.Ui.TestCases.TITLE;
        assertEquals(page.getTitle(),expected_message);
    }




}
