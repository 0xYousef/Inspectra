package data.mapper;

import data.DTO.Login;
import data.DTO.Register;
import org.bson.Document;

public class RegisterMapper {
    public static Document toDocument(Register form) {
        Document doc = new Document();
        doc.append("title", form.getTitle())
                .append("name", form.getName())
                .append("email", form.getEmail())
                .append("password", form.getPassword())
                .append("birthday",form.getDay()+"/"+form.getMonth()+"/"+form.getYear())
                .append("firstname", form.getFirstname())
                .append("lastname", form.getLastname())
                .append("company", form.getCompany())
                .append("address", form.getAddress())
                .append("address2", form.getAddress2())
                .append("country", form.getCountry())
                .append("state", form.getState())
                .append("city", form.getCity())
                .append("zip code", form.getZipcode())
                .append("phone", form.getPhone()
        );
        return doc;
    }

    public static Login fromDocument(Document doc) {
        return Login.builder()
                .email(doc.getString("email"))
                .password(doc.getString("password"))
                .build();
    }
}
