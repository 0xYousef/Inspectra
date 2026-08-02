package api.endpoints.account;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import data.DTO.Register;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class CreateAccountEndpoint extends Endpoint {

    private static final String PATH = "/createAccount";
    private static final String SCHEMA = "schemas/login-response-schema.json";

    public Response create(Register form) {
        RequestSpecification request = newRequest()
                .accept(ContentType.JSON)
                .contentType(ContentType.MULTIPART);

        addMultipart(request, "title", form.getTitle());
        addMultipart(request, "email", form.getEmail());
        addMultipart(request, "password", form.getPassword());
        addMultipart(request, "name", form.getName());
        addMultipart(request, "firstname", form.getFirstname());
        addMultipart(request, "lastname", form.getLastname());
        addMultipart(request, "company", form.getCompany());
        addMultipart(request, "address1", form.getAddress());
        addMultipart(request, "address2", form.getAddress2());
        addMultipart(request, "country", form.getCountry());
        addMultipart(request, "state", form.getState());
        addMultipart(request, "city", form.getCity());
        addMultipart(request, "mobile_number", form.getPhone());
        addMultipart(request, "zipcode", form.getZipcode());
        if (form.getDay() != 0) {
            request.multiPart("birth_day", form.getDay());
        }
        if (form.getMonth() != 0) {
            request.multiPart("birth_month", form.getMonth());
        }
        if (form.getYear() != 0) {
            request.multiPart("birth_year", form.getYear());
        }

        return execute(request, HttpMethod.POST, PATH, SCHEMA, "Create Account Response Schema");
    }

    private void addMultipart(RequestSpecification request, String name, String value) {
        if (value != null) {
            request.multiPart(name, value);
        }
    }
}
