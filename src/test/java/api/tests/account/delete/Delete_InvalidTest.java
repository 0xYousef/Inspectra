package api.tests.account.delete;

import data.DTO.Login;
import api.base.BaseAPIClient;
import api.endpoints.account.VerifyLoginEndpoint;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import data.provider.AuthProvider;

import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("DELETE OPERATION")
public class Delete_InvalidTest extends BaseAPIClient {
    @Story("https://automationexercise.com/api/verifyLogin")
    @Description("Invalid Delete an account with /verifyLogin")
    @Severity(SeverityLevel.CRITICAL)
    @Test( dataProvider = "deletedAccount",
            dataProviderClass = AuthProvider.class
            , groups = {"API"})
    public void LoginByInValidCredentials(Login form){
        Response response = new VerifyLoginEndpoint().deleteNotAllowed(form);
        assertEquals(response.getBody().jsonPath().getInt("responseCode"), form.getExpectation().getStatusCode());
        assertEquals(response.getBody().jsonPath().get("message"), form.getExpectation().getMessage());
    }
}
