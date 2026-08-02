package data.DTO;

import lombok.Builder;
import lombok.Data;

@Data @Builder
public class PersonalInfo {
    private String title, name;
    private String firstname, lastname;
    private String company,address, address2;
    private String country, state, city, zipcode, phone;

    public String getDetails(String email){
        return String.format(
                "%s. %s %s\n%s\n%s\n%s\n%s\n%s %s %s\n%s\n%s",
                title, firstname, lastname,
                email,
                company,
                address,
                address2,
                city, state, zipcode,
                country,
                phone
        );
    }
}
