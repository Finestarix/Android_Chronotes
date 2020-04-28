package edu.bluejack19_2.chronotes.controller;

import android.net.Uri;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Objects;

import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.firebase_callback.FirebaseCallbackBytes;
import edu.bluejack19_2.chronotes.utils.firebase_callback.FirebaseCallbackProcessStatus;
import edu.bluejack19_2.chronotes.utils.firebase_callback.FirebaseCallbackUser;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public class UserController {

    private static UserController userController;

    private StorageReference storageReference;
    private CollectionReference collectionReference;

    private ProcessStatus currentStatus;

    private UserController() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(User.COLLECTION_NAME);

        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference().child("users");
    }

    public static UserController getInstance() {
        if (userController == null) {
            userController = new UserController();
        }
        return userController;
    }

    public void getUserByID(FirebaseCallbackUser firebaseCallbackUser, String id) {
        collectionReference.
                whereEqualTo("id", id).
                get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(task.getResult()).isEmpty())
                            firebaseCallbackUser.onCallback(null, ProcessStatus.NOT_FOUND);
                        else {
                            QueryDocumentSnapshot queryDocumentSnapshot = Objects.requireNonNull(task.getResult()).iterator().next();
                            User user = queryDocumentSnapshot.toObject(User.class);
                            firebaseCallbackUser.onCallback(user, ProcessStatus.FOUND);
                        }
                    } else
                        firebaseCallbackUser.onCallback(null, ProcessStatus.FAILED);
                });
    }

    public void getUserByEmail(FirebaseCallbackUser firebaseCallbackUser, String email) {
        collectionReference.
                whereEqualTo("email", email).
                get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(task.getResult()).isEmpty())
                            firebaseCallbackUser.onCallback(null, ProcessStatus.NOT_FOUND);
                        else {
                            QueryDocumentSnapshot queryDocumentSnapshot = Objects.requireNonNull(task.getResult()).iterator().next();
                            User user = queryDocumentSnapshot.toObject(User.class);
                            firebaseCallbackUser.onCallback(user, ProcessStatus.FOUND);
                        }
                    } else
                        firebaseCallbackUser.onCallback(null, ProcessStatus.FAILED);
                });
    }

    public void findEmail(FirebaseCallbackProcessStatus firebaseCallbackProcessStatus, String email) {
        currentStatus = ProcessStatus.INIT;

        collectionReference.
                whereEqualTo("email", email).
                get().
                addOnCompleteListener(task -> {
                    currentStatus = (task.isSuccessful()) ?
                            (!Objects.requireNonNull(task.getResult()).iterator().hasNext()) ?
                                    ProcessStatus.NOT_FOUND : ProcessStatus.FOUND : ProcessStatus.FAILED;
                    firebaseCallbackProcessStatus.onCallback(currentStatus);
                });
    }

    public void getUserPicture(FirebaseCallbackBytes firebaseCallbackBytes, String picture) {
        storageReference.
                child(picture).
                getBytes(Long.MAX_VALUE).
                addOnCompleteListener(task -> firebaseCallbackBytes.onCallback(
                        (task.isSuccessful()) ? task.getResult() : null,
                        (task.isSuccessful()) ? ProcessStatus.SUCCESS : ProcessStatus.FAILED));
    }

    public void insertNewUser(FirebaseCallbackProcessStatus firebaseCallbackProcessStatus, User user) {
        currentStatus = ProcessStatus.INIT;

        collectionReference.
                document(User.DOCUMENT_NAME + user.getId()).
                set(user).
                addOnCompleteListener(task -> {
                    currentStatus = (task.isComplete()) ?
                            ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                    firebaseCallbackProcessStatus.onCallback(currentStatus);
                });
    }

    public void uploadPhoto(FirebaseCallbackUser firebaseCallbackUser, String pictureID, Uri uri) {
        storageReference.
                child(pictureID).
                putFile(uri).
                addOnSuccessListener(taskSnapshot -> firebaseCallbackUser.onCallback(null, ProcessStatus.SUCCESS)).
                addOnFailureListener(e -> firebaseCallbackUser.onCallback(null, ProcessStatus.FAILED));
    }

    public void updateUserByID(FirebaseCallbackProcessStatus firebaseCallbackProcessStatus, User user) {

        collectionReference.
                document(User.DOCUMENT_NAME + user.getId()).
                update("name", user.getName(),
                        "picture", user.getPicture()).
                addOnCompleteListener(task -> {
                    currentStatus = (task.isComplete()) ?
                            ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                    firebaseCallbackProcessStatus.onCallback(currentStatus);
                });
    }

}
