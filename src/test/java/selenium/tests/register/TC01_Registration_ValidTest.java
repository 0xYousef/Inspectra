package selenium.tests.register;

import data.DTO.Register;
import core.base.BaseUITest;
import data.expectations.Expectations;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.testng.asserts.SoftAssert;
import selenium.pages.HomePage;
import selenium.pages.authentication.LoginPage;

import data.provider.RegisterProvider;
import selenium.validators.ValidationPage;

import static core.base.DRIVERS.CHROME;

public class TC01_Registration_ValidTest extends BaseUITest {

    private HomePage homePage;
    private SoftAssert softAssert;


    public TC01_Registration_ValidTest() {
        super(CHROME);
    }


    @BeforeClass
    public void beforeTest() {
        softAssert = new SoftAssert();
        homePage = new HomePage(driver);
        homePage.open();
    }

    @Test(dataProvider = "RegisterValidMinInfo", dataProviderClass = RegisterProvider.class)
    public void registerUI_validTest(Register form)
    {             String verify_message = homePage.verify();
        String expected_message = Expectations.Ui.HomePage.WELCOME_MESSAGE;
        softAssert.assertEquals(verify_message, expected_message);

        LoginPage loginPage = homePage.navigateTo().LoginPage();
        softAssert.assertTrue(loginPage.isSignupVisible());

        ValidationPage validationPage = loginPage.register(form).clickOnSignup().fillMandatoryRegisterForm(form).submit();
        softAssert.assertEquals(validationPage.getTitle(), Expectations.Ui.Register.TITLE);

        homePage = validationPage._continue();
        validationPage = homePage.navigateTo().DeleteAccountPage();
        softAssert.assertEquals(validationPage.getTitle(), Expectations.Ui.Delete.TITLE);


    }



}
