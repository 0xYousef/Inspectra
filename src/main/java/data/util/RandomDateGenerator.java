package data.util;

import java.time.LocalDate;
import java.util.Random;

public class RandomDateGenerator {
    private static final int MIN_YEAR = 1900;
    private static final int MAX_YEAR = 2021;
    private static final Random random = new Random();

    public static LocalDate generateRandomDateWithStaticYear() {
        int year = getRandomInRange(MIN_YEAR, MAX_YEAR);
        int month = getRandomInRange(1, 12); // Months 1-12
        int day = getRandomInRange(1, LocalDate.of(year, month, 1).lengthOfMonth());

        return LocalDate.of(year, month, day);
    }

    private static int getRandomInRange(int min, int max) {
        return random.nextInt(max - min + 1) + min;
    }
}