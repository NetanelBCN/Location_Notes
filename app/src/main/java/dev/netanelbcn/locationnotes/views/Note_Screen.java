package dev.netanelbcn.locationnotes.views;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.models.NoteItem;
import dev.netanelbcn.locationnotes.utilities.DataManager;
import dev.netanelbcn.locationnotes.utilities.Validator;

public class Note_Screen extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private TextInputEditText Note_TIET_title;
    private TextInputEditText Note_TIET_body;
    private MaterialTextView Note_MTV_date;
    private MaterialTextView Note_MTV_lastEditedDate;
    private ShapeableImageView Note_SIV_picture;
    private MaterialButton Note_MB_attach;
    private MaterialButton Note_MB_delete;
    private MaterialButton Note_MB_save;

    private DataManager dataManager;
    private Validator validator;
    private FusedLocationProviderClient fusedLocationClient;

    private String tempTitle;
    private String tempBody;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_screen);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        dataManager = DataManager.getInstance();
        validator = Validator.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        findViews();
        fillData();
        setupClicks();
    }

    private void findViews() {
        Note_TIET_title = findViewById(R.id.Note_TIET_title);
        Note_TIET_body = findViewById(R.id.Note_TIET_body);
        Note_MTV_date = findViewById(R.id.Note_MTV_date);
        Note_SIV_picture = findViewById(R.id.Note_SIV_picture);
        Note_MB_attach = findViewById(R.id.Note_MB_attach);
        Note_MB_delete = findViewById(R.id.Note_MB_delete);
        Note_MB_save = findViewById(R.id.Note_MB_save);
        Note_MTV_lastEditedDate = findViewById(R.id.Note_MTV_lastEditedDate);
    }

    private void fillData() {
        if (dataManager.getCurrent() != null) {
            this.Note_TIET_title.setText(dataManager.getCurrent().getNote_title());
            this.Note_TIET_body.setText(dataManager.getCurrent().getNote_body());
            this.Note_MTV_date.setText("Created date: " + dataManager.parseDateToString(dataManager.getCurrent().getNote_date()));
            this.Note_MTV_lastEditedDate.setText("Last edited date: " + dataManager.parseDateToString(dataManager.getCurrent().getNote_last_date()));
        } else {
            LocalDateTime l=LocalDateTime.now();
            this.Note_MTV_date.setText("Created date: " + dataManager.parseDateToString(l));
            this.Note_MTV_lastEditedDate.setText("Last edited date: " + dataManager.parseDateToString(l));

        }
    }

    private void setupClicks() {
        Note_MB_attach.setOnClickListener(v -> setupAttachClick());
        Note_MB_delete.setOnClickListener(v -> {
            dataManager.deleteCurrentNote();
            finish();
        });
        Note_MB_save.setOnClickListener(v -> setupSaveClick());
    }

    private void setupAttachClick() {
        // Implement image attachment logic if needed
    }

    private void setupSaveClick() {
        tempTitle = validator.isInputBarValueValid(Note_TIET_title) ?
                Note_TIET_title.getText().toString() : "Empty title";

        tempBody = validator.isInputBarValueValid(Note_TIET_body) ?
                Note_TIET_body.getText().toString() : "Empty body";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE
            );
            return;
        }

        fetchLocationAndSave(tempTitle, tempBody);
    }

    private void fetchLocationAndSave(String title, String body) {
        LatLng fallback = new LatLng(31.7683, 35.2137); // Jerusalem

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    LatLng latLng = (location != null)
                            ? new LatLng(location.getLatitude(), location.getLongitude())
                            : fallback;
                    saveNoteWithLocation(title, body, latLng);
                })
                .addOnFailureListener(e -> saveNoteWithLocation(title, body, fallback));
    }

    private void saveNoteWithLocation(String title, String body, LatLng location) {
        NoteItem note = new NoteItem()
                .setNote_title(title)
                .setNote_body(body)
                .setNote_date(LocalDateTime.now())
                .setNote_location(location);

        if (dataManager.getCurrent() == null) {
            dataManager.addNewNote(note);
        } else {
            NoteItem current = dataManager.getCurrent();
            current.setNote_title(title);
            current.setNote_body(body);
            current.setNote_last_date(LocalDateTime.now());
            current.setNote_location(location);
            dataManager.setCurrent(null);
        }

        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                fetchLocationAndSave(tempTitle, tempBody);
            } else {
                saveNoteWithLocation(tempTitle, tempBody, dataManager.getDefaultLocation());
            }
        }
    }
}
