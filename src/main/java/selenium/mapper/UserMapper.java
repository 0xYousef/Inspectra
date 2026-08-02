package selenium.mapper;

import data.DTO.PersonalInfo;
import data.DTO.Register;
import cache.models.UserInfo;
import org.bson.Document;

public class UserMapper {
    public static UserInfo mapPersonalInfoToUserInfo(PersonalInfo personalInfo) {
        return UserInfo.builder()
                .title(personalInfo.getTitle())
                .name(personalInfo.getName())
                .firstname(personalInfo.getFirstname())
                .lastname(personalInfo.getLastname())
                .company(personalInfo.getCompany())
                .address(personalInfo.getAddress())
                .address2(personalInfo.getAddress2())
                .country(personalInfo.getCountry())
                .city(personalInfo.getCity())
                .state(personalInfo.getState())
                .zipcode(personalInfo.getZipcode())
                .phone(personalInfo.getPhone())
                .build();
    }

    public static UserInfo mapRegisterToUserInfo(Register form) {
        return UserInfo.builder()
                .email(form.getEmail())
                .title(form.getTitle())
                .firstname(form.getFirstname())
                .lastname(form.getLastname())
                .name(form.getName())
                .company(form.getCompany())
                .address(form.getAddress())
                .address2(form.getAddress2())
                .country(form.getCountry())
                .city(form.getCity())
                .state(form.getState())
                .zipcode(form.getZipcode())
                .phone(form.getPhone())
                .build();
    }
    public static UserInfo mapDocumentToUser(Document document) {
        return UserInfo.builder()
                .email(document.getString("email"))
                .name(document.getString("name"))
                .firstname(document.getString("first_name"))
                .lastname(document.getString("last_name"))
                .address(document.getString("address1"))
                .address2(document.getString("address2"))
                .title(document.getString("title"))
                .company(document.getString("company"))
                .city(document.getString("city"))
                .state(document.getString("state"))
                .country(document.getString("country"))
                .zipcode(document.getString("zipcode"))
                .phone(document.getString("phone"))
                .build();
    }
}
