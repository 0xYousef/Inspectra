package core.utils;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class TimeDateConfiguration {
    private static final DateTimeFormatter DATETIMEFORMATTER = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    public static String NOW(){
        return LocalDateTime.now().format(DATETIMEFORMATTER);
    }
}
