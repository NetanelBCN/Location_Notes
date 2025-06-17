package dev.netanelbcn.locationnotes.views;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.time.LocalDateTime;
import java.util.UUID;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.interfaces.OnImageUploadComplete;
import dev.netanelbcn.locationnotes.models.NoteItem;
import dev.netanelbcn.locationnotes.utilities.DataManager;
import dev.netanelbcn.locationnotes.utilities.Validator;

public class Note_Screen extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final int STORAGE_PERMISSION_REQUEST_CODE = 1002;

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
    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private String tempTitle;
    private String tempBody;
    private Uri selectedImageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_note_screen);
        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();
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
            Glide.with(this).load(dataManager.getCurrent().getNote_pic_url()).placeholder(R.drawable.baseline_add_photo_alternate_24).into(this.Note_SIV_picture);
        } else {
            LocalDateTime l = LocalDateTime.now();
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_MEDIA_IMAGES},
                        STORAGE_PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        } else {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                        STORAGE_PERMISSION_REQUEST_CODE);
            } else {
                openGallery();
            }
        }
    }

    private void openGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        intent.setType("image/*");
        startActivityForResult(intent, STORAGE_PERMISSION_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == STORAGE_PERMISSION_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            selectedImageUri = data.getData();
            Glide.with(this)
                    .load(selectedImageUri)
                    .placeholder(R.drawable.baseline_add_photo_alternate_24)
                    .into(Note_SIV_picture);
        }
    }

    private void setupSaveClick() {
        tempTitle = validator.isInputBarValueValid(Note_TIET_title) ?
                Note_TIET_title.getText().toString() : "Empty title";
        tempBody = validator.isInputBarValueValid(Note_TIET_body) ?
                Note_TIET_body.getText().toString() : "Empty body";

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    LOCATION_PERMISSION_REQUEST_CODE);
        }

        fetchLocationAndUploadImageAndSave(tempTitle, tempBody);
    }

    private void fetchLocationAndUploadImageAndSave(String title, String body) {
        LatLng fallback = new LatLng(31.7683, 35.2137);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            return;

        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(location -> {
                    LatLng latLng = (location != null)
                            ? new LatLng(location.getLatitude(), location.getLongitude())
                            : fallback;

                    if (selectedImageUri != null) {
                        uploadImageToFirebase(selectedImageUri, imageUrl ->
                                saveNoteWithLocation(title, body, latLng, imageUrl));
                    } else {
                        saveNoteWithLocation(title, body, latLng, null);
                    }
                });
    }

    private void setUIEnabled(boolean enabled) {
        Note_TIET_title.setEnabled(enabled);
        Note_TIET_body.setEnabled(enabled);
        Note_MB_attach.setEnabled(enabled);
        Note_MB_delete.setEnabled(enabled);
        Note_MB_save.setEnabled(enabled);
    }

    private void uploadImageToFirebase(Uri imageUri, OnImageUploadComplete callback) {
        setUIEnabled(false);
        Note_MB_attach.setText("Please wait, Uploading...");
        String path = "images/" + dataManager.getUserId() + "/" + UUID.randomUUID() + ".jpg";
        storageReference.child(path).putFile(imageUri)
                .addOnSuccessListener(taskSnapshot ->
                        storageReference.child(path).getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    setUIEnabled(true);
                                    Note_MB_attach.setText("Attach");
                                    callback.onComplete(uri.toString());
                                })
                                .addOnFailureListener(e -> {
                                    setUIEnabled(true);
                                    Note_MB_attach.setText("Attach");
                                    Toast.makeText(this, "Failed to get URL: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                                })
                )
                .addOnFailureListener(e -> {
                    setUIEnabled(true);
                    Note_MB_attach.setText("Attach");
                    Toast.makeText(this, "Upload failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private void saveNoteWithLocation(String title, String body, LatLng location, String imageUrl) {
        NoteItem note = new NoteItem()
                .setNote_title(title)
                .setNote_body(body)
                .setNote_date(LocalDateTime.now())
                .setNote_location(location)
                .setNote_pic_url(imageUrl);

        if (dataManager.getCurrent() == null) {
            dataManager.getNotes().add(note);
            dataManager.addNewNoteToDB(note);
        } else {
            note.setNote_id(dataManager.getCurrent().getNote_id());
            note.setNote_date(dataManager.getCurrent().getNote_date());
            note.setNote_last_date(LocalDateTime.now());
            dataManager.updateNote(note);
            dataManager.setCurrent(null);
        }
        finish();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE &&
                grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            fetchLocationAndUploadImageAndSave(tempTitle, tempBody);
        } else if (requestCode == STORAGE_PERMISSION_REQUEST_CODE) {
            boolean granted = false;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                granted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_MEDIA_IMAGES)
                        == PackageManager.PERMISSION_GRANTED;
            } else {
                granted = ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                        == PackageManager.PERMISSION_GRANTED;
            }
            if (granted) {
                openGallery();
            } else {
                Toast.makeText(this, "Storage permission is required to select images", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
