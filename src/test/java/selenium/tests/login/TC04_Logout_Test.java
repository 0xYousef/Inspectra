package selenium.tests.login;

import data.DTO.Login;
import core.base.BaseUITest;
import org.testng.annotations.Test;
import selenium.pages.HomePage;
import selenium.components.Header;
import data.provider.AuthProvider;

import static core.base.DRIVERS.CHROME;
import static org.testng.Assert.*;

public class TC04_Logout_Test extends BaseUITest {

    public TC04_Logout_Test() {
        super(CHROME);
    }

    @Test(dataProvider = "loginValidCredentials",dataProviderClass = AuthProvider.class)
    public void logoutValidCredentials(Login form) {
        HomePage homePage = new HomePage(driver);
        Header header = homePage.navigateTo().LoginPage().login(form).correctLogin();
        assertNotNull(header.getLoggedInUsername());
        homePage.navigateTo().Logout();
        assertNull(header.getLoggedInUsername());
    }
}
