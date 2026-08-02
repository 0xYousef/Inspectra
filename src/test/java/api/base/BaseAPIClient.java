package api.base;

import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import io.restassured.filter.log.RequestLoggingFilter;
import io.restassured.filter.log.ResponseLoggingFilter;


import org.testng.annotations.BeforeSuite;
import core.utils.Configuration;

public class BaseAPIClient {

    @BeforeSuite
    public void setup() {
        RestAssured.baseURI = Configuration.get("api.base.url");
        RestAssured.filters(
                new ResponseBodyAttachmentFilter(),
                new AllureRestAssured(),
                new RequestLoggingFilter(),
                new ResponseLoggingFilter());
        }
    }
