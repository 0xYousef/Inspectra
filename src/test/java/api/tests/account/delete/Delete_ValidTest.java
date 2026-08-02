package api.tests.account.delete;

import data.DTO.Login;
import api.base.BaseAPIClient;
import api.endpoints.account.DeleteAccountEndpoint;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.annotations.Test;
import data.provider.AuthProvider;

import static data.expectations.Expectations.Http.*;
import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("DELETE OPERATION")
public class Delete_ValidTest extends BaseAPIClient {
    @Story("https://automationexercise.com/api/deleteAccount")
    @Description("Delete an account")
    @Severity(SeverityLevel.CRITICAL)
    @Test( dataProvider = "deletedAccount",
            dataProviderClass = AuthProvider.class
            , groups = {"API"})
    public void deleteAccount(Login form){
        Response response = new DeleteAccountEndpoint().delete(form);
        assertEquals(response.getBody().jsonPath().getInt("responseCode"), OK);
        assertEquals(response.getBody().jsonPath().get("message"), DELETED_ACCOUNT);
    }
}
