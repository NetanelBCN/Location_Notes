package dev.netanelbcn.locationnotes.utilities;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;

import java.util.Locale;

import dev.netanelbcn.locationnotes.models.NoteItem;

public class DataManager {

    private ArrayList<NoteItem> notes;
    private static DataManager instance;
    private NoteItem current;
    private final LatLng defaultLocation = new LatLng(31.771959, 35.217018);

    public LatLng getDefaultLocation() {
        return defaultLocation;
    }

    private DataManager() {

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
            instance.loadGeneralData();
        }
        return instance;
    }

    public void loadGeneralData() {
        this.notes = new ArrayList<>(Arrays.asList(
                new NoteItem().setNote_title("Meeting")
                        .setNote_body("Call with client at 3 PM.")
                        .setNote_date(LocalDateTime.of(2002, 5, 12, 12, 1))
                        .setNote_pic_url("https://example.com/image1.jpg")
                        .setNote_location(new LatLng(31.771959, 35.217018)),

                new NoteItem().setNote_title("Groceries")
                        .setNote_body("Milk, Eggs, Bread")
                        .setNote_date(LocalDateTime.of(2012, 4, 11, 12, 1))
                        .setNote_pic_url("https://example.com/image2.jpg")
                        .setNote_location(new LatLng(32.0345, 34.8738)),

                new NoteItem().setNote_title("Groceriesdaassssssssas")
                        .setNote_body("Milk, Eggs, Breadsdsddadadadasdadadada")
                        .setNote_date(LocalDateTime.of(2022, 1, 2, 12, 1))
                        .setNote_pic_url("https://example.com/image2.jpg")
                        .setNote_location(new LatLng(32.0345, 34.8738))
        ));

        this.notes.sort((a, b) -> b.getNote_date().compareTo(a.getNote_date()));

    }


    public void addNewNote(NoteItem note) {
        this.getNotes().add(note);
        this.notes.sort((a, b) -> b.getNote_date().compareTo(a.getNote_date()));
        //add in firebase
    }

    public void deleteCurrentNote() {
        this.getNotes().remove(current);
        this.setCurrent(null);
        //add in firebase
    }

    public void updateNote(NoteItem updatedNote) {
        this.getCurrent().setNote_body(updatedNote.getNote_body()).setNote_title(updatedNote.getNote_title()).setNote_last_date(updatedNote.getNote_last_date());
        //update in firebase
    }


    public String parseDateToString(LocalDateTime noteDate) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM dd, yyyy", Locale.ENGLISH);
        return noteDate.format(formatter);
    }
}
