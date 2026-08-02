package api.tests.account.add;

import data.DTO.Register;
import api.base.BaseAPIClient;
import api.endpoints.account.CreateAccountEndpoint;
import io.qameta.allure.*;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import data.mongo.RegisterRepository;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;

import static data.expectations.Expectations.Http.*;
import static org.testng.Assert.assertEquals;

@Epic("ACCOUNT")
@Feature("ADD NEW USER")
@Story("POST: https://automationexercise.com/api/createAccount")
public class Registration_ValidTest extends BaseAPIClient {
    private RegisterRepository registerRepository;
    @BeforeTest
    public void beforeTest() {
        registerRepository = new RegisterRepository();
    }

    @Description("Add New User with Full Information")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterValidFullInfo",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"},priority = 2)
    public void testAddNewUserWithFullInfo(Register register) {
        Response response = new CreateAccountEndpoint().create(register);

        JsonPath res = response.body().jsonPath();
        if (res.getInt("responseCode")==CREATED)
            registerRepository.insertUser(register);
        assertEquals(res.getInt("responseCode"), CREATED);
        assertEquals(res.getString("message"), CREATED_MESSAGE);
    }


    @Description("Add New User with Mandatory Information")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterValidMinInfo",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"},priority = 1)
    public void testAddNewUserWithMinInfo(Register register) {
        Response response = new CreateAccountEndpoint().create(register);

        JsonPath res = response.body().jsonPath();
        if (res.getInt("responseCode")==CREATED)
            registerRepository.insertUser(register);
        assertEquals(res.getInt("responseCode"), CREATED);
        assertEquals(res.getString("message"), CREATED_MESSAGE);


    }
}
