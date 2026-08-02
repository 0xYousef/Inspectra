package api.tests.account.update;

import data.DTO.Register;
import api.base.BaseAPIClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import data.mongo.RegisterRepository;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;
import core.utils.AllureUtils;

import static data.expectations.Expectations.Http.OK;
import static data.expectations.Expectations.Http.UPDATED_MESSAGE;
import static io.restassured.RestAssured.given;
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
        AllureUtils.attachJsonSchema("schemas/login-response-schema.json","Update Response Schema");
        Response response = updateUserByEmail(register.getEmail(),register);
        JsonPath res = response.body().jsonPath();


        assertEquals(res.getInt("responseCode"), OK);
        assertEquals(res.getString("message"), UPDATED_MESSAGE);
        System.out.println("Password"+register.getPassword());
        if (res.getInt("responseCode") == OK)
            repository.updateUserByEmail(register.getEmail(),register);
    }



    private Response updateUserByEmail(String email, Register register) {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.MULTIPART)
                .multiPart("email", register.getEmail())
                .multiPart("password", register.getPassword())
                .multiPart("name", register.getName())
                .multiPart("title", register.getTitle())
                .multiPart("first_name", register.getFirstname())
                .multiPart("last_name", register.getLastname())
                .multiPart("company", register.getCompany())
                .multiPart("address1", register.getAddress())
                .multiPart("address2", register.getAddress2())
                .multiPart("country", register.getCountry())
                .multiPart("state", register.getState())
                .multiPart("city", register.getCity())
                .multiPart("phone", register.getPhone())
                .multiPart("zipcode", register.getZipcode())
                .when()
                .put("/updateAccount")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                .extract()
                .response();

    }
}
