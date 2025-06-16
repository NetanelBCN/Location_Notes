package dev.netanelbcn.locationnotes.utilities;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

public class Validator {
    private static Validator instance;

    private Validator() {

    }

    public boolean isInputBarValueValid(TextInputEditText inputEditText) {
        return inputEditText.getText() != null &&
                !inputEditText.getText().toString().trim().isEmpty();

    }
    public boolean isMailFormatValid(TextInputEditText mailElement) {
        if (mailElement == null || mailElement.getText() == null)
            return false;
        String email = mailElement.getText().toString().trim();
        return !email.isEmpty() && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    public LocalDateTime convertDateToLocalDateTime(Date date) {
        return LocalDateTime.ofInstant(date.toInstant(), ZoneId.systemDefault());
    }
    public  Date convertLocalDateTimeToDate(LocalDateTime localDateTime) {
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    public static Validator getInstance() {
        if (instance == null) {
            instance = new Validator();
        }
        return instance;
    }
}


