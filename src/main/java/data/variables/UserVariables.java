package data.variables;

import com.github.javafaker.Faker;
import data.util.RandomValue;

import java.util.Arrays;

public class UserVariables {
    private static final Faker faker = new Faker();
    private static final RandomValue<String> values = new RandomValue<>(Arrays.asList("India","Canada","Australia","United States","Singapore","New Zealand"));
    private static final RandomValue<String> titles = new RandomValue<>(Arrays.asList("Mrs","Mr"));

    public static String title = titles.getRandomValue();
    public static String register_name = faker.name().name();
    public static String register_password = "auto12345678";


    public static String firstName = faker.name().firstName();
    public static String lastName = faker.name().lastName();
    public static String company = faker.company().name();

    public static String address = faker.address().fullAddress();
    public static String address2 = faker.address().secondaryAddress();


    public static String country = values.getRandomValue();
    public static String state = faker.address().state();
    public static String city = faker.address().city();
    public static String zip = faker.address().zipCode();

    public static String phone = faker.phoneNumber().phoneNumber();

}
