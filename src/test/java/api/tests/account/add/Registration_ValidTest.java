package api.tests.account.add;

import data.DTO.Register;
import api.base.BaseAPIClient;
import io.qameta.allure.*;
import io.restassured.http.ContentType;
import io.restassured.module.jsv.JsonSchemaValidator;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import jdk.jfr.Description;
import data.mongo.RegisterRepository;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import data.provider.RegisterProvider;
import core.utils.AllureUtils;

import static data.expectations.Expectations.Http.*;
import static io.restassured.RestAssured.given;
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
        AllureUtils.attachJsonSchema("schemas/login-response-schema.json","Update Response Schema");
        Response response = addUser(register);

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
        AllureUtils.attachJsonSchema("schemas/login-response-schema.json","Update Response Schema");
        Response response = addUser(register);

        JsonPath res = response.body().jsonPath();
        if (res.getInt("responseCode")==CREATED)
            registerRepository.insertUser(register);
        assertEquals(res.getInt("responseCode"), CREATED);
        assertEquals(res.getString("message"), CREATED_MESSAGE);


    }






    private Response addUser(Register register) {
        boolean fullInfo = register.getTitle().isEmpty() && register.getCompany().isEmpty()
                && register.getAddress2().isEmpty() && (register.getDay() == 0)
                && (register.getMonth() == 0) && (register.getYear() == 0);
        RequestSpecification request;
        if (fullInfo)
           request = fillFullInfo(register);
        else
            request = fillMandatoryInfo(register);

        return request.when()
                .post("/createAccount")
                .then()
                .body(JsonSchemaValidator.matchesJsonSchemaInClasspath("schemas/login-response-schema.json"))
                .extract()
                .response();
    }


    private RequestSpecification fillMandatoryInfo(Register register) {
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.MULTIPART)
                .multiPart("title", register.getTitle())
                .multiPart("email", register.getEmail())
                .multiPart("password", register.getPassword())
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

    private RequestSpecification fillFullInfo(Register register){
        return given()
                .accept(ContentType.JSON)
                .contentType(ContentType.MULTIPART)

                .multiPart("email", register.getEmail())
                .multiPart("password", register.getPassword())
                .multiPart("name", register.getName())
                .multiPart("title", register.getTitle())
                .multiPart("birth_day",register.getDay())
                .multiPart("birth_month",register.getMonth())
                .multiPart("birth_year",register.getYear())
                .multiPart("firstname", register.getFirstname())
                .multiPart("lastname", register.getLastname())
                .multiPart("company", register.getCompany())
                .multiPart("address1", register.getAddress())
                .multiPart("address2", register.getAddress2())
                .multiPart("country", register.getCountry())
                .multiPart("state", register.getState())
                .multiPart("city", register.getCity())
                .multiPart("mobile_number", register.getPhone())
                .multiPart("zipcode", register.getZipcode());
    }

}
