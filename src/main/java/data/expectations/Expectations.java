package data.expectations;

/**
 * Single home for all expected values in the framework.
 * <p>
 * Nested {@link Http} holds API status codes and messages; nested {@link Ui}
 * holds UI page messages. Keep every expected value here so assertions never
 * rely on hardcoded literals in tests.
 */
public final class Expectations {

    private Expectations() {
        // constants holder - no instances
    }

    /**
     * Expected HTTP status codes and API response messages.
     */
    public static final class Http {

        private Http() {
            // constants holder - no instances
        }

        public static final int OK = 200;
        public static final int NOT_FOUND = 404;
        public static final int BAD_REQUEST = 400;
        public static final int NOT_SUPPORTED = 405;
        public static final int CREATED = 201;
        public static final int INTERNAL_SERVER_ERROR = 500;

        public static final String USER_EXISTS = "User exists!";
        public static final String USER_NOT_FOUND = "User not found!";
        public static final String MISSING_CREDENTIALS =
                "Bad request, email or password parameter is missing in POST request.";
        public static final String NOT_SUPPORTED_MESSAGE = "This request method is not supported.";
        public static final String DELETED_ACCOUNT = "Account deleted!";
        public static final String UPDATED_MESSAGE = "User updated!";
        public static final String NOT_FOUND_ACCOUNT = "Account not found!";
        public static final String CREATED_MESSAGE = "User created!";
        public static final String REQUIRED_REGISTER = "Bad request, %s parameter is missing in POST request.";
        public static final String VALID_FIELD = "Bad request: the %s parameter is invalid in the POST request.";
        public static final String EMAIL_EXISTS = "Email already exists!";
    }

    /**
     * Expected UI page messages.
     */
    public static final class Ui {

        private Ui() {
            // constants holder - no instances
        }

        public static class Register {
            public static final String TITLE = "ACCOUNT CREATED!";
            public static final String MESSAGE =
                    "Congratulations! Your new account has been successfully created!";
            public static final String EMAIL_EXISTS_MESSAGE = "Email Address already exist!";
        }

        public static class Delete {
            public static final String TITLE = "ACCOUNT DELETED!";
            public static final String MESSAGE = "Your account has been permanently deleted!";
        }

        public static class Order {
            public static final String TITLE = "Order Placed!";
            public static final String MESSAGE = "Congratulations! Your order has been confirmed!";
            public static final String ORDER_INVOICE = "Hi %s, Your total purchase amount is %d. Thank you";
        }

        public static class Cart {
            public static final String TITLE = "Added!";
            public static final String MESSAGE = "Your product has been added to cart.";
        }

        public static class ContactUs {
            public static final String MESSAGE = "Success! Your details have been submitted successfully.";
        }

        public static class Login {
            public static final String INCORRECT_CREDENTIALS = "Your email or password is incorrect!";
        }

        public static class TestCases {
            public static final String TITLE = "TEST CASES";
        }

        public static class HomePage {
            public static final String WELCOME_MESSAGE =
                    "Full-Fledged practice website for Automation Engineers";
        }

        public static class Footer {
            public static final String TITLE = "SUBSCRIPTION";
            public static final String SUCCESS_MESSAGE = "You have been successfully subscribed!";
        }
    }
}
