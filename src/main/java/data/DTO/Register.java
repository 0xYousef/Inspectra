package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data
@Builder(toBuilder = true)
public class Register {
    private String title, name, email, password;
    private int day, month, year;
    private String firstname, lastname;
    private boolean isNewsletter, isSpecialOffers;
    private String company, address, address2;
    private String country, state, city, zipcode, phone;
}
