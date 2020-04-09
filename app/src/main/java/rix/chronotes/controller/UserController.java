package rix.chronotes.controller;

import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import rix.chronotes.model.User;

public class UserController {

    private static final String COLLECTION_NAME = "users";
    private static final String DOCUMENT_NAME = "users_";
    private static final String KEY_ID = "Id";
    private static final String KEY_NAME = "Name";
    private static final String KEY_EMAIL = "Email";
    private static final String KEY_PASSWORD = "Password";
    private static final String KEY_PICTURE = "Picture";
    private static final String DEFAULT_PICTURE = "users.png";

    private FirebaseFirestore firebaseFirestore;

    public UserController() {
        this.firebaseFirestore = FirebaseFirestore.getInstance();
    }

    public void insertUser(User user) {

        String userID = UUID.randomUUID().toString();

        Map<String, String> users = new HashMap<>();
        users.put(KEY_NAME, user.getName());
        users.put(KEY_EMAIL, user.getEmail());
        users.put(KEY_PASSWORD, user.getPassword());
        users.put(KEY_PICTURE, DEFAULT_PICTURE);

        firebaseFirestore.collection(COLLECTION_NAME).document(DOCUMENT_NAME + userID).set(users);
    }

}
