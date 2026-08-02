package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class ReviewProduct {
    private String name;
    private String email;
    private String message;
    private String review;
    private int rating;
}
