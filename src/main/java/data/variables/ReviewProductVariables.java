package data.variables;

import com.github.javafaker.Faker;

public class ReviewProductVariables {
    private static final Faker faker = new Faker();
    public static String name = faker.name().fullName();
    public static String email = faker.internet().emailAddress();
    public static String message = faker.lorem().paragraph();
}
