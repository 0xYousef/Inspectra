package data.provider;

import data.DTO.Login;
import data.DTO.Register;
import com.github.javafaker.Faker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.annotations.DataProvider;
import data.util.RandomDateGenerator;

import java.time.LocalDate;

import static data.variables.AuthVariables.*;
import static data.variables.UserVariables.*;

public class RegisterProvider {

    private static final Logger log = LoggerFactory.getLogger(RegisterProvider.class);

    @DataProvider(name = "RegisterValidFullInfo")
    public Register[] registerValidFullInfo() {
        return new Register[]{buildFullInfo()};
    }

    @DataProvider(name = "RegisterValidMinInfo")
    public Register[] registerValidMinInfo() {
        return new Register[]{buildMandatoryInfo()};
    }

    @DataProvider(name = "RegisterInvalidInNullPassword")
    public Register[] registerValidInCompletedPassword() {
        return new Register[]{buildMandatoryInfo().toBuilder().password(null).build()};
    }
    @DataProvider(name = "RegisterInvalidCompletedBlankPassword")
    public Register[] registerInvalidCompletedBlankPassword() {
        return new Register[]{buildMandatoryInfo().toBuilder().password(" ").build()};
    }
    // Here

    @DataProvider(name = "RegisterInvalidCompletedEmptyPassword")
    public Register[] registerInvalidRegistersEmptyPassword() {
        return new Register[]{buildMandatoryInfo().toBuilder().password("").build()};
    }

    @DataProvider(name = "RegisterInvalidExistEmail")
    public Register[] registerInvalidExistEmail() {
        Login form = LOGIN;
        return new Register[]{buildFullInfo().toBuilder().email(form.getEmail()).password(form.getPassword()).build()};
    }


    private Register buildMandatoryInfo() {
        Faker faker = new Faker();
        return Register.builder()
                .title(title)
                .name(register_name)
                .email(faker.internet().emailAddress())
                .password(register_password)
                .firstname(firstName)
                .lastname(lastName)
                .address(address)
                .country(country)
                .state(state)
                .city(city)
                .zipcode(zip)
                .phone(phone)
                .build();
    }

    private Register buildFullInfo() {
        Faker faker = new Faker();

        LocalDate date = RandomDateGenerator.generateRandomDateWithStaticYear();
        log.info("date: " + date);
        int day = date.getDayOfMonth();
        int month = date.getMonthValue();
        int year = date.getYear();

        return buildMandatoryInfo().toBuilder()
                .email(faker.internet().emailAddress())
                .day(day)
                .month(month)
                .year(year)
                .isNewsletter(true)
                .isSpecialOffers(true)
                .company(company)
                .address2(address2)
                .build();
    }
}
