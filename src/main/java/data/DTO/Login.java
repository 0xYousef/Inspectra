package data.DTO;

import data.expectations.Expectation;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder(toBuilder = true)
public class Login {
    private String email;
    private String password;
    private PersonalInfo personalInfo;
    private Expectation expectation;

    @Override
    public String toString() {
        return personalInfo == null ? email : personalInfo.getDetails(email);
    }
}
