package edu.bluejack19_2.chronotes.model;

import java.util.ArrayList;

public class Note {

    public static final String COLLECTION_NAME = "notes";
    public static final String DOCUMENT_NAME = "notes_";

    private String id;
    private String name;
    private String detail;
    private String lastUpdate;
    private String tag;
    private ArrayList<String> users;

    public Note() {
    }

    public Note(String id, String name, String detail, String lastUpdate, String tag, ArrayList<String> users) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.lastUpdate = lastUpdate;
        this.tag = tag;
        this.users = users;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDetail() {
        return detail;
    }

    public String getLastUpdate() {
        return lastUpdate;
    }

    public String getTag() {
        return tag;
    }

    public ArrayList<String> getUsers() {
        return users;
    }
}
