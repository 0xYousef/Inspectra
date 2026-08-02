package data.variables;

import com.github.javafaker.Faker;

public class ContactUsVariables {
    private static final Faker faker = new Faker();

    public static String name = faker.name().name();
    public static String email = faker.internet().emailAddress();
    public static String subject = faker.expression("#{superhero.descriptor} Application");
    public static String message = faker.lorem().paragraph();
    public static String path = "src/main/resources/contact-us.txt";
}
