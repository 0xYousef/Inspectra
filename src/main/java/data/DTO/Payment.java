package data.DTO;

import data.exceptions.InvalidDataException;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Builder(toBuilder = true)
public class Payment {
    private String cardName;
    private String cardNumber;
    private String cvc;
    private short month;
    private short year;
    public String date() {
        if (month < 1 || month > 12) {
            throw new InvalidDataException("Month must be between 1 and 12");
        }

        if (year < 0 || year > 99) {
            throw new InvalidDataException("Year must be between 0 and 99");
        }

        return String.format("%02d/%02d", month, year);
    }
}
