package api.endpoints.account;

import api.endpoints.base.Endpoint;
import api.endpoints.base.HttpMethod;
import data.DTO.Register;
import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;

public class UpdateAccountEndpoint extends Endpoint {

    private static final String PATH = "/updateAccount";
    private static final String SCHEMA = "schemas/login-response-schema.json";

    public Response update(Register form) {
        RequestSpecification request = newRequest()
                .accept(ContentType.JSON)
                .contentType(ContentType.MULTIPART);

        addMultipart(request, "email", form.getEmail());
        addMultipart(request, "password", form.getPassword());
        addMultipart(request, "name", form.getName());
        addMultipart(request, "title", form.getTitle());
        addMultipart(request, "first_name", form.getFirstname());
        addMultipart(request, "last_name", form.getLastname());
        addMultipart(request, "company", form.getCompany());
        addMultipart(request, "address1", form.getAddress());
        addMultipart(request, "address2", form.getAddress2());
        addMultipart(request, "country", form.getCountry());
        addMultipart(request, "state", form.getState());
        addMultipart(request, "city", form.getCity());
        addMultipart(request, "phone", form.getPhone());
        addMultipart(request, "zipcode", form.getZipcode());

        return execute(request, HttpMethod.PUT, PATH, SCHEMA, "Update Account Response Schema");
    }

    private void addMultipart(RequestSpecification request, String name, String value) {
        if (value != null) {
            request.multiPart(name, value);
        }
    }
}
