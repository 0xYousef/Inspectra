package data.DTO;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class ContactUs {
    private String name;
    private String email;
    private String subject;
    private String message;
    private String path;
}
