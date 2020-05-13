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
    private String masterUser;
    private ArrayList<String> users;

    public Note() {
        users = new ArrayList<>();
    }

    public Note(String id, String name, String detail, String lastUpdate, String tag, String masterUser, ArrayList<String> users) {
        this.id = id;
        this.name = name;
        this.detail = detail;
        this.lastUpdate = lastUpdate;
        this.tag = tag;
        this.users = users;
        this.masterUser = masterUser;
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

    public String getMasterUser() {
        return masterUser;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDetail(String detail) {
        this.detail = detail;
    }

    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public void setMasterUser(String masterUser) {
        this.masterUser = masterUser;
    }

    public void setUsers(ArrayList<String> users) {
        this.users = users;
    }

    public void addUsers(String user) {
        this.users.add(user);
    }

    public void removeUsers(String user) {
        this.users.remove(user);
    }
}
