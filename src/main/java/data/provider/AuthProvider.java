package data.provider;

import data.DTO.Login;
import data.expectations.Expectation;
import org.testng.annotations.DataProvider;

import static data.expectations.Expectations.Http.*;
import static data.variables.AuthVariables.*;

public class AuthProvider {



    @DataProvider(name = "deletedAccount")
    public Login[] deleteAccount() {
        Login cur = LOGIN;
        return new Login[]{buildLogin(cur.getEmail(),cur.getPassword(),NOT_SUPPORTED,NOT_SUPPORTED_MESSAGE)
                .toBuilder()
                .personalInfo(cur.getPersonalInfo())
                .build()};
    }
    @DataProvider(name = "loginValidCredentials")
    public static Login[] loginValidData() {
        Login cur = LOGIN;
        return new Login[]{buildLogin(cur.getEmail(), cur.getPassword(), OK, USER_EXISTS)
                .toBuilder()
                .personalInfo(cur.getPersonalInfo())
                .build()};
    }

    @DataProvider(name = "loginInvalidIncorrectPassword")
    public static Login[] loginInvalidIncorrectPassword() {

        return new Login[]{buildLogin(LOGIN.getEmail(), INVALID_PASSWORD, NOT_FOUND, USER_NOT_FOUND)};
    }

    @DataProvider(name = "loginInvalidEmptyEmail")
    public static Login[] loginInvalidEmptyEmail() {
        return new Login[]{buildLogin(null, LOGIN.getPassword(), BAD_REQUEST, MISSING_CREDENTIALS)};
    }

    @DataProvider(name = "loginInvalidNotFoundEmail")
    public static Login[] loginInvalidNotFoundEmail() {
        return new Login[]{buildLogin(INVALID_EMAIL, INVALID_PASSWORD, NOT_FOUND, USER_NOT_FOUND)};
    }

    private static Login buildLogin(String email, String password, int statusCode, String message) {
        return Login.builder()
                .email(email)
                .password(password)
                .expectation(Expectation.builder()
                        .statusCode(statusCode)
                        .message(message)
                        .build())
                .build();
    }
}
