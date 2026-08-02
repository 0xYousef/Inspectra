package data.util;

import data.DTO.Login;
import data.mongo.AuthRepository;

public class EmailGenerator {
    private static final AuthRepository authRepository= new AuthRepository();
    private static final RandomValue<Login> validCredentials = new RandomValue<>(authRepository.getAllUsers());

    public static Login generateUser() {
        return validCredentials.getRandomValue();
    }

}
