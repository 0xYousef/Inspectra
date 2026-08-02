package api.tests.account.update;

import data.DTO.Register;
import api.base.BaseAPIClient;
import api.endpoints.account.UpdateAccountEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import data.mongo.RegisterRepository;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;

import static data.expectations.Expectations.Http.OK;
import static data.expectations.Expectations.Http.UPDATED_MESSAGE;
import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("UPDATE USER INFORMATION")
@Story("PUT: https://automationexercise.com/api/updateAccount")
public class Update_ValidTest extends BaseAPIClient {
    private RegisterRepository repository;


    @BeforeTest
    public void setUp() {
        repository = new RegisterRepository();
    }

    @Description("Update User Information By valid Credentials")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterInvalidExistEmail",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"})
    public void testUpdateAccount(Register register) {
        Response response = new UpdateAccountEndpoint().update(register);
        JsonPath res = response.body().jsonPath();

        assertEquals(res.getInt("responseCode"), OK);
        assertEquals(res.getString("message"), UPDATED_MESSAGE);
        if (res.getInt("responseCode") == OK)
            repository.updateUserByEmail(register.getEmail(),register);
    }
}
