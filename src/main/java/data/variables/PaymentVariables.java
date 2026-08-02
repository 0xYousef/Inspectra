package data.variables;

import com.github.javafaker.Faker;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class PaymentVariables {
    private final static Faker faker = new Faker();

    public static String cardName = faker.name().name();
    public static String cardNumber = faker.business().creditCardNumber();
    public static String cvc = faker.number().digits(3);


    private static final String expiryDate = faker.business().creditCardExpiry();
    private static final LocalDate localExpiry = LocalDate.parse(expiryDate, DateTimeFormatter.ISO_LOCAL_DATE);

    public static String expiryMonth = String.format("%02d", localExpiry.getMonthValue()); // MM
    public static String expiryYear = String.valueOf(localExpiry.getYear());
}
