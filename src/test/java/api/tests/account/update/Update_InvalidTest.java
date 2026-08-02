package api.tests.account.update;

import data.DTO.Register;
import api.base.BaseAPIClient;
import api.endpoints.account.UpdateAccountEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;

import static data.expectations.Expectations.Http.*;
import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("UPDATE USER INFORMATION")
@Story("PUT: https://automationexercise.com/api/updateAccount")

public class Update_InvalidTest extends BaseAPIClient {
    @Description("Invalid Update User Information By Invalid Credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterInvalidExistEmail",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"})
    public void testNotUpdateAccount(Register register) {
        Response response = new UpdateAccountEndpoint()
                .update(register.toBuilder().password(register.getPassword()+"incorrect").build());

        JsonPath res = response.body().jsonPath();

        assertEquals(res.getInt("responseCode"), NOT_FOUND);
        assertEquals(res.getString("message"), NOT_FOUND_ACCOUNT);
    }
}
