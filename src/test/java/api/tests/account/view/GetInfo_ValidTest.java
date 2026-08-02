package api.tests.account.view;

import data.DTO.Login;
import api.base.BaseAPIClient;
import api.endpoints.account.GetUserDetailEndpoint;
import io.qameta.allure.*;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;
import data.provider.AuthProvider;

@Epic("ACCOUNT")
@Feature("GET USER INFORMATION")
public class GetInfo_ValidTest extends BaseAPIClient {

    @Story("Get user information by email")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "loginValidCredentials",
            dataProviderClass = AuthProvider.class
            , groups = {"API"}
    )
    public void getUserInfo(Login login) {
        Response response = new GetUserDetailEndpoint().getUserDetail(login);

        Assert.assertEquals(response.getBody().jsonPath().getInt("responseCode"),login.getExpectation().getStatusCode());
    }
}
