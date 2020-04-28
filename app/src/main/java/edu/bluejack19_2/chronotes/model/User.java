package edu.bluejack19_2.chronotes.model;

import com.google.firebase.firestore.Exclude;

public class User {

    public static final String COLLECTION_NAME = "users";
    public static final String DOCUMENT_NAME = "users_";
    public static final String PHOTO_NAME = "users";
    public static final String DEFAULT_PICTURE = "users.png";

    private String id;
    private String name;
    private String email;
    private String password;
    private String picture;

    public User() {
    }

    public User(String id, String name, String email, String password, String picture) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.password = password;
        this.picture = picture;
    }


    @Exclude
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getPicture() {
        return picture;
    }

}
