package api.tests.account.update;

import data.DTO.Register;
import api.base.BaseAPIClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import jdk.jfr.Description;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;
import core.utils.AllureUtils;



import static data.expectations.Expectations.Http.*;
import static io.restassured.RestAssured.given;
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
        AllureUtils.attachJsonSchema("schemas/login-response-schema.json","Update Response Schema");
        Response response = updateUserByEmail(register.getEmail(),register);

        JsonPath res = response.body().jsonPath();

        assertEquals(res.getInt("responseCode"), NOT_FOUND);
        assertEquals(res.getString("message"), NOT_FOUND_ACCOUNT);
    }



    private Response updateUserByEmail(String email, Register register) {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.MULTIPART)
                .multiPart("email", register.getEmail())
                .multiPart("password", register.getPassword()+"incorrect")
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
