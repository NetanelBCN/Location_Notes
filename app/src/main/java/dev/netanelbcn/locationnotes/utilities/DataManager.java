package dev.netanelbcn.locationnotes.utilities;

import android.annotation.SuppressLint;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import dev.netanelbcn.locationnotes.models.NoteItem;
import dev.netanelbcn.myrv.GenericAdapter;

public class DataManager {

    private ArrayList<NoteItem> notes;
    private GenericAdapter<NoteItem> adapter;


    private static DataManager instance;
    private NoteItem current;
    private final FBManager fbManager;

    private String userId;
    private String userName;


    private final LatLng defaultLocation = new LatLng(31.771959, 35.217018);

    public LatLng getDefaultLocation() {
        return defaultLocation;
    }

    public GenericAdapter<NoteItem> getAdapter() {
        return adapter;
    }

    public DataManager setAdapter(GenericAdapter<NoteItem> adapter) {
        this.adapter = adapter;
        return this;
    }

    public FirebaseDatabase getFBDb() {
        return this.fbManager.getFBDb();
    }

    public String getUserName() {
        return userName;
    }

    public DataManager setUserName(String userName) {
        this.userName = userName;
        return this;
    }

    public String getUserId() {
        return userId;
    }

    public FBManager getFbManager() {
        return fbManager;
    }

    public DataManager setUserId(String userId) {
        this.userId = userId;
        return this;
    }

    private DataManager() {
        fbManager = FBManager.getInstance();
        this.notes = new ArrayList<>();
    }


    public ArrayList<NoteItem> getNotes() {
        return notes;
    }

    public NoteItem getCurrent() {
        return current;
    }

    public DataManager setCurrent(NoteItem current) {
        this.current = current;
        return this;
    }

    public DataManager setNotes(ArrayList<NoteItem> notes) {
        this.notes = notes;
        return this;
    }

    public static DataManager getInstance() {
        if (instance == null) {
            instance = new DataManager();
        }
        return instance;
    }

    public void loadGeneralData() {
        notes.clear();
        getFBDb().getReference("users").child(userId).child("notes")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            try {
                                String noteTitle = snap.child("note_title").getValue(String.class);
                                String noteBody = snap.child("note_body").getValue(String.class);
                                String noteDateStr = snap.child("note_date").getValue(String.class);
                                String noteLastDateStr = snap.child("note_last_date").getValue(String.class);
                                String notePicUrl = snap.child("note_pic_url").getValue(String.class);
                                String noteId = snap.child("note_id").getValue(String.class);
                                Double lat = snap.child("note_location").child("latitude").getValue(Double.class);
                                Double lon = snap.child("note_location").child("longitude").getValue(Double.class);
                                if (noteTitle != null && noteBody != null && noteDateStr != null && lat != null && lon != null) {
                                    LocalDateTime noteDate = LocalDateTime.parse(noteDateStr);
                                    LocalDateTime noteLastDate = noteLastDateStr != null ? LocalDateTime.parse(noteLastDateStr) : noteDate;
                                    LatLng location = new LatLng(lat, lon);
                                    NoteItem note = new NoteItem(noteTitle, noteBody, noteDate, noteLastDate, notePicUrl, location, noteId);
                                    notes.add(note);
                                }
                            } catch (Exception e) {
                                Log.e("DataLoad", "Error parsing note: " + e.getMessage());
                            }
                        }
                        notes.sort((a, b) -> b.getNote_date().compareTo(a.getNote_date()));
                        if (dataLoadListener != null) dataLoadListener.onDataLoaded();
                    }

                    public void onCancelled(@NonNull DatabaseError error) {
                        Log.e("DataLoad", "Firebase error: " + error.getMessage());
                    }
                });
    }


    public void addNewNoteToDB(NoteItem note) {
        if (userId != null && !userId.isEmpty()) {
            Map<String, Object> noteData = new HashMap<>();
            noteData.put("note_title", note.getNote_title());
            noteData.put("note_body", note.getNote_body());
            noteData.put("note_date", note.getNote_date().toString());
            noteData.put("note_last_date", note.getNote_last_date().toString());
            noteData.put("note_pic_url", note.getNote_pic_url());
            noteData.put("note_id", note.getNote_id());
            if (note.getNote_location() != null) {
                Map<String, Double> locationData = new HashMap<>();
                locationData.put("latitude", note.getNote_location().latitude);
                locationData.put("longitude", note.getNote_location().longitude);
                noteData.put("note_location", locationData);
            }
            this.getFBDb().getReference("users")
                    .child(userId)
                    .child("notes")
                    .child(note.getNote_id())
                    .setValue(noteData)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Note saved successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to save note: " + e.getMessage()));
        } else {
            Log.e("Firebase", "User ID is null or empty");
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    public void deleteCurrentNote() {
        if (current == null) {
            Log.e("Firebase", "No current note to delete");
            return;
        }
        this.getNotes().remove(current);
        this.getAdapter().notifyDataSetChanged();
        if (userId != null && !userId.isEmpty()) {
            this.getFBDb().getReference("users")
                    .child(userId)
                    .child("notes")
                    .child(current.getNote_id())
                    .removeValue()
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Note deleted successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to delete note: " + e.getMessage()));
        } else {
            Log.e("Firebase", "User ID is null or empty");
        }
        this.setCurrent(null);
    }

    @SuppressLint("NotifyDataSetChanged")
    public void updateNote(NoteItem updatedNote) {
        if (current == null) {
            Log.e("Firebase", "No current note to update");
            return;
        }

        // Update local note
        this.getCurrent()
                .setNote_body(updatedNote.getNote_body())
                .setNote_title(updatedNote.getNote_title())
                .setNote_last_date(updatedNote.getNote_last_date());

        // Re-sort the list since last_date might have changed
        this.notes.sort((a, b) -> b.getNote_date().compareTo(a.getNote_date()));
        this.getAdapter().notifyDataSetChanged();
        // Update in Firebase
        if (userId != null && !userId.isEmpty()) {
            Map<String, Object> updates = new HashMap<>();
            updates.put("note_title", current.getNote_title());
            updates.put("note_body", current.getNote_body());
            updates.put("note_last_date", current.getNote_last_date().toString());

            this.getFBDb().getReference("users")
                    .child(userId)
                    .child("notes")
                    .child(current.getNote_id())
                    .updateChildren(updates)
                    .addOnSuccessListener(aVoid -> Log.d("Firebase", "Note updated successfully"))
                    .addOnFailureListener(e -> Log.e("Firebase", "Failed to update note: " + e.getMessage()));
        } else {
            Log.e("Firebase", "User ID is null or empty");
        }
    }

    public String parseDateToString(LocalDateTime noteDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        return noteDate.format(formatter);
    }

    public interface DataLoadListener {
        void onDataLoaded();
    }

    private DataLoadListener dataLoadListener;

    public void setDataLoadListener(DataLoadListener listener) {
        this.dataLoadListener = listener;
    }


}
