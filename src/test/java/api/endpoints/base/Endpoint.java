package api.endpoints.base;

import core.utils.AllureUtils;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

/**
 * Base class for Endpoint Object Model classes.
 * <p>
 * Each endpoint owns the HTTP method, path, request building and response schema
 * of one API endpoint, and returns the raw RestAssured {@link Response} so tests
 * stay focused on behavior assertions.
 */
public abstract class Endpoint {

    /**
     * Starts a new request against the global RestAssured configuration
     * (base URI and filters are configured once by {@code BaseAPIClient}).
     */
    protected RequestSpecification newRequest() {
        return given();
    }

    /**
     * Attaches the response schema to the Allure report, sends the request and
     * validates the response body against the schema.
     *
     * @param request  the built request
     * @param method   the HTTP method to send
     * @param path     the endpoint path (e.g. {@code /verifyLogin})
     * @param schema   the classpath schema (e.g. {@code schemas/login-response-schema.json})
     * @param label    the Allure attachment label
     * @return the validated response
     */
    protected Response execute(RequestSpecification request, HttpMethod method, String path,
                               String schema, String label) {
        AllureUtils.attachJsonSchema(schema, label);
        Response response = request.request(method.name(), path);
        response.then().body(matchesJsonSchemaInClasspath(schema));
        return response;
    }
}
