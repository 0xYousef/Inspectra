package api.tests.account.add;

import data.DTO.Register;
import api.base.BaseAPIClient;
import data.exceptions.ExceptionHandler;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import org.testng.Assert;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;
import core.utils.AllureUtils;

import static data.expectations.Expectations.Http.*;
import static io.restassured.RestAssured.given;
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
        AllureUtils.attachJsonSchema("schemas/login-response-schema.json","Update Response Schema");
        Response response = registerNewUser(register)
                .when()
                .post("/createAccount")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                .extract()
                .response();;

        JsonPath res = response.body().jsonPath();

        assertEquals(res.getInt("responseCode"), BAD_REQUEST);
        assertEquals(res.getString("message"), String.format(REQUIRED_REGISTER,"password"));
    }
//    // Bug
//    @Description("Invalid Registration with a Null password")
//    @Severity(SeverityLevel.CRITICAL)
//    @Test(dataProvider = "RegisterInvalidInNullPassword",
//            dataProviderClass = RegisterProvider.class,
//            groups = {"API"})
//    public void invalidRegisterWithNullPassword(Register register) {
//        AllureUtils.attachJsonSchema("schemas/tests.login-response-schema.json","Update Response Schema");
//        Response response=null;
//        try {
//            response = registerNewUser(register).multiPart("password",register.getPassword())
//                    .when()
//                    .post("/createAccount")
//                    .then()
//                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/tests.login-response-schema.json"))
//                    .extract()
//                    .response();
//        }
//        catch (Exception e) {
//            return;
//        }finally {
//            Assert.assertNotNull(response);
//            JsonPath res = response.body().jsonPath();
//            assertEquals(res.getInt("responseCode"), BAD_REQUEST);
//            assertEquals(res.getString("message"), String.format(VALID_FIELD,"password"));
//        }
//    }
//
//    // Bug
//    @Description("Invalid Registration with a blanked password")
//    @Severity(SeverityLevel.CRITICAL)
//    @Test(dataProvider = "RegisterInvalidCompletedBlankPassword",
//            dataProviderClass = RegisterProvider.class,
//            groups = {"API"})
//    public void invalidRegisterWithBlankPassword(Register register) {
//        AllureUtils.attachJsonSchema("schemas/tests.login-response-schema.json","Update Response Schema");
//        Response response=null;
//        try {
//            response = registerNewUser(register).multiPart("password",register.getPassword())
//                    .when()
//                    .post("/createAccount")
//                    .then()
//                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/tests.login-response-schema.json"))
//                    .extract()
//                    .response();
//        }
//        catch (Exception e) {
//            return;
//        }finally {
//            Assert.assertNotNull(response);
//            JsonPath res = response.body().jsonPath();
//            assertEquals(res.getInt("responseCode"), BAD_REQUEST);
//            assertEquals(res.getString("message"), String.format(VALID_FIELD,"password"));
//        }
//    }
//
//
//    @Description("Invalid Registration with an empty password")
//    @Severity(SeverityLevel.CRITICAL)
//    @Test(dataProvider = "RegisterInvalidCompletedEmptyPassword",
//            dataProviderClass = RegisterProvider.class,
//            groups = {"API"})
//    public void invalidRegisterWithEmptyPassword(Register register) {
//        AllureUtils.attachJsonSchema("schemas/tests.login-response-schema.json","Update Response Schema");
//        Response response=null;
//        try {
//            response = registerNewUser(register).multiPart("password",register.getPassword())
//                    .when()
//                    .post("/createAccount")
//                    .then()
//                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/tests.login-response-schema.json"))
//                    .extract()
//                    .response();
//        }
//        catch (Exception e) {
//            return;
//        }finally {
//            Assert.assertNotNull(response);
//            JsonPath res = response.body().jsonPath();
//            assertEquals(res.getInt("responseCode"), BAD_REQUEST);
//            assertEquals(res.getString("message"), EMAIL_EXISTS);
//        }
//
//    }
//
    @Description("Invalid Registration with an E-mail already exists")
    @Severity(SeverityLevel.CRITICAL)
    @Test(dataProvider = "RegisterInvalidExistEmail",
            dataProviderClass = RegisterProvider.class,
            groups = {"API"})
    public void invalidRegisterWithExistEmail(Register register) {
        AllureUtils.attachJsonSchema("schemas/login-response-schema.json","Update Response Schema");
        Response response=null;
        try {
            response = registerNewUser(register).multiPart("password",register.getPassword())
                    .when()
                    .post("/createAccount")
                    .then()
                    .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                    .extract()
                    .response();
        }
        catch (Exception e) {
            ExceptionHandler.handleSilently(e, "registering user with existing email");
        }finally {
            Assert.assertNotNull(response);
            JsonPath res = response.body().jsonPath();

            assertEquals(res.getInt("responseCode"), BAD_REQUEST);
            assertEquals(res.getString("message"), EMAIL_EXISTS);
        }

    }























    private RequestSpecification registerNewUser(Register register) {
        return given()
                .contentType(ContentType.MULTIPART)
                .multiPart("title", register.getTitle())
                .multiPart("email", register.getEmail())
                .multiPart("name", register.getName())
                .multiPart("firstname", register.getFirstname())
                .multiPart("lastname", register.getLastname())
                .multiPart("address1", register.getAddress())
                .multiPart("country", register.getCountry())
                .multiPart("state", register.getState())
                .multiPart("city", register.getCity())
                .multiPart("mobile_number", register.getPhone())
                .multiPart("zipcode", register.getZipcode());

    }
}
