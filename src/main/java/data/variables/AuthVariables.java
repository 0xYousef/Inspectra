package data.variables;

import data.DTO.Login;
import com.github.javafaker.Faker;

import static data.util.EmailGenerator.generateUser;

public class AuthVariables {
    private static final Faker faker = new Faker();

    public static String INVALID_EMAIL = faker.internet().emailAddress();
    public static String INVALID_PASSWORD =faker.internet().password();


    public static final Login LOGIN = generateUser();


    public static String ACCESS_TOKEN = "";
    public static String REFRESH_TOKEN = "";

}
