package api.tests.account.login;

import api.base.BaseAPIClient;
import api.endpoints.account.VerifyLoginEndpoint;
import data.DTO.Login;
import data.provider.AuthProvider;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.testng.Assert;
import org.testng.annotations.Test;

@Epic("ACCOUNT")
@Feature("LOGIN")
@Story("POST https://automationexercise.com/api/verifyLogin")
public class Login_InvalidTest extends BaseAPIClient {

    @Description("Invalid tests.login with incorrect password")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "loginInvalidIncorrectPassword",
            dataProviderClass = AuthProvider.class,
            groups = {"API"})
    public void LoginByValidCredentials(Login form) {
        Response response = new VerifyLoginEndpoint().login(form);
        Assert.assertEquals(response.jsonPath().getInt("responseCode"),
                form.getExpectation().getStatusCode());
        Assert.assertEquals(response.jsonPath().getString("message"),
                form.getExpectation().getMessage());
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Invalid Login with Empty Email")
    @Test(dataProvider = "loginInvalidEmptyEmail", dataProviderClass = AuthProvider.class,
            groups = {"API"})
    public void LoginByEmptyEmail(Login form) {
        Response response = new VerifyLoginEndpoint().login(form);
        Assert.assertEquals(response.jsonPath().getInt("responseCode"), form.getExpectation().getStatusCode());
        Assert.assertEquals(response.jsonPath().getString("message"), form.getExpectation().getMessage());
    }

    @Severity(SeverityLevel.CRITICAL)
    @Description("Invalid Login with not found email")
    @Test(dataProvider = "loginInvalidNotFoundEmail", dataProviderClass = AuthProvider.class,
            groups = {"API"})
    public void LoginByNotFoundEmail(Login form) {
        Response response = new VerifyLoginEndpoint().login(form);
        Assert.assertEquals(response.jsonPath().getInt("responseCode"), form.getExpectation().getStatusCode());
        Assert.assertEquals(response.jsonPath().getString("message"), form.getExpectation().getMessage());
    }
}
