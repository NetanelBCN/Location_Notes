package dev.netanelbcn.locationnotes.utilities;

import com.google.android.material.textfield.TextInputEditText;

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
            return true;
        String email = mailElement.getText().toString().trim();
        return email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    public static Validator getInstance() {
        if (instance == null) {
            instance = new Validator();
        }
        return instance;
    }
}


