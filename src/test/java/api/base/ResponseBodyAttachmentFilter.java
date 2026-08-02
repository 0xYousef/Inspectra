package api.base;

import io.qameta.allure.Allure;
import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;

/**
 * Rest-Assured filter that attaches the raw response body as a clean {@code application/json}
 * Allure attachment on the current test. Never throws, so it cannot break a request.
 */
public class ResponseBodyAttachmentFilter implements Filter {
    private static final Logger log = LoggerFactory.getLogger(ResponseBodyAttachmentFilter.class);

    @Override
    public Response filter(FilterableRequestSpecification requestSpec,
                           FilterableResponseSpecification responseSpec,
                           FilterContext filterContext) {
        Response response = filterContext.next(requestSpec, responseSpec);
        attachJsonBody(response);
        return response;
    }

    private void attachJsonBody(Response response) {
        try {
            byte[] body = response.getBody().asByteArray();
            if (body == null || body.length == 0) {
                return;
            }
            String trimmed = new String(body, StandardCharsets.UTF_8).trim();
            if (!trimmed.startsWith("{")) {
                return;
            }
            Allure.addAttachment("Response JSON", "application/json",
                    new ByteArrayInputStream(body), ".json");
        } catch (Exception e) {
            log.warn("Could not attach response JSON: {}", e.getMessage());
        }
    }
}
