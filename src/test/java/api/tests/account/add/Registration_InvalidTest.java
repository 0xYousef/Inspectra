package api.tests.account.add;

import data.DTO.Register;
import api.base.BaseAPIClient;
import api.endpoints.account.CreateAccountEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;

import static data.expectations.Expectations.Http.*;
import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("ADD NEW USER")
@Story("POST https://automationexercise.com/api/createAccount")
public class Registration_InvalidTest extends BaseAPIClient {

    @Description("Invalid Registration with a missed password field")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterInvalidInNullPassword",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"})
    public void invalidRegisterWithIncompletedPasswordField(Register register) {
        Response response = new CreateAccountEndpoint().create(register);

        JsonPath res = response.body().jsonPath();

        assertEquals(res.getInt("responseCode"), BAD_REQUEST);
        assertEquals(res.getString("message"), String.format(REQUIRED_REGISTER,"password"));
    }

    @Description("Invalid Registration with an E-mail already exists")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterInvalidExistEmail",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"})
    public void invalidRegisterWithExistEmail(Register register) {
        Response response = new CreateAccountEndpoint().create(register);

        JsonPath res = response.body().jsonPath();

        assertEquals(res.getInt("responseCode"), BAD_REQUEST);
        assertEquals(res.getString("message"), EMAIL_EXISTS);
    }
}
