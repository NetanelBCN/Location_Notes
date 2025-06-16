package dev.netanelbcn.locationnotes.models;

import com.google.android.gms.maps.model.LatLng;

import java.time.LocalDateTime;
import java.util.UUID;

public class NoteItem {
    private String note_title;
    private String note_body;

    public NoteItem setNote_id(String note_id) {
        this.note_id = note_id;
        return this;
    }

    private LocalDateTime note_date;
    private LocalDateTime note_last_date;

    private String note_pic_url;
    private LatLng note_location;




    public LocalDateTime getNote_last_date() {
        return note_last_date;
    }

    public NoteItem setNote_last_date(LocalDateTime note_last_date) {
        this.note_last_date = note_last_date;
        return this;
    }

    public String getNote_id() {
        return note_id;
    }

    private String note_id;

    public NoteItem() {
        this.note_id = UUID.randomUUID().toString().toUpperCase();
    }

    @Override
    public String toString() {
        return "NoteItem{" +
                "note_title='" + note_title + '\'' +
                ", note_body='" + note_body + '\'' +
                ", note_date='" + note_date + '\'' +
                ", note_pic_url='" + note_pic_url + '\'' +
                ", note_location=" + note_location +
                ", note_id='" + note_id + '\'' +
                '}';
    }

    public NoteItem(String note_title, String note_body, LocalDateTime note_date, LocalDateTime note_last_date, String note_pic_url, LatLng note_location, String note_id) {
        this.note_title = note_title;
        this.note_body = note_body;
        this.note_date = note_date;
        this.note_last_date = note_last_date;
        this.note_pic_url = note_pic_url;
        this.note_location = note_location;
        this.note_id = note_id;
    }

    public LatLng getNote_location() {
        return note_location;
    }

    public NoteItem setNote_location(LatLng note_location) {
        this.note_location = note_location;
        return this;
    }

    public String getNote_title() {
        return note_title;
    }

    public String getNote_body() {
        return note_body;
    }

    public NoteItem setNote_body(String note_body) {
        this.note_body = note_body;
        return this;
    }

    public LocalDateTime getNote_date() {
        return note_date;
    }


    public NoteItem setNote_date(LocalDateTime note_date) {
        this.note_date = note_date;
        this.note_last_date = note_date;
        return this;
    }

    public String getNote_pic_url() {
        return note_pic_url;
    }

    public NoteItem setNote_pic_url(String note_pic_url) {
        this.note_pic_url = note_pic_url;
        return this;
    }

    public NoteItem setNote_title(String note_title) {
        this.note_title = note_title;
        return this;
    }


}
