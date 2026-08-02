package api.endpoints.account;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import data.DTO.Login;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class VerifyLoginEndpoint extends Endpoint {

    private static final String PATH = "/verifyLogin";
    private static final String SCHEMA = "schemas/login-response-schema.json";

    public Response login(Login form) {
        return execute(request(form), HttpMethod.POST, PATH, SCHEMA, "Login Response Schema");
    }

    public Response deleteNotAllowed(Login form) {
        return execute(request(form), HttpMethod.DELETE, PATH, SCHEMA, "Login Response Schema");
    }

    private RequestSpecification request(Login form) {
        RequestSpecification request = newRequest().contentType(ContentType.MULTIPART);
        if (form.getEmail() != null) {
            request.multiPart("email", form.getEmail());
        }
        if (form.getPassword() != null) {
            request.multiPart("password", form.getPassword());
        }
        return request;
    }
}
