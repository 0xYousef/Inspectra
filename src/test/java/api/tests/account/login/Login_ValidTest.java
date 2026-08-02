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
import org.testng.annotations.Test;

import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("LOGIN")
@Story("POST https://automationexercise.com/api/verifyLogin")
public class Login_ValidTest extends BaseAPIClient {

    @Description("Login By valid Credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Test(description = "Login By valid Credentials", dataProvider = "loginValidCredentials",
            dataProviderClass = AuthProvider.class,
            groups = {"API"})
    public void LoginByValidCredentials(Login form) {
        Response response = new VerifyLoginEndpoint().login(form);
        assertEquals(response.jsonPath().getInt("responseCode"), form.getExpectation().getStatusCode());
        assertEquals(response.jsonPath().get("message"), form.getExpectation().getMessage());
    }
}
