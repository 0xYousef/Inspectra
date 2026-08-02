package data.expectations;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class Expectation {
    private String message;
    private int statusCode;
}
