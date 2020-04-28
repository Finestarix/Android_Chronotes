package edu.bluejack19_2.chronotes.controller;

import android.content.Context;
import android.net.Uri;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.Objects;
import java.util.UUID;

import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.FirebaseCallbackProcessStatus;
import edu.bluejack19_2.chronotes.utils.FirebaseCallbackUser;
import edu.bluejack19_2.chronotes.utils.GeneralHelper;
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
        storageReference = firebaseStorage.getReference();
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

    public void insertNewUser(FirebaseCallbackProcessStatus firebaseCallbackProcessStatus, User user) {
        currentStatus = ProcessStatus.INIT;

        collectionReference.
                document(User.DOCUMENT_NAME + user.getId()).
                set(user).
                addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        currentStatus = (task.isComplete()) ?
                                ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                        firebaseCallbackProcessStatus.onCallback(currentStatus);
                    }
                });
    }

    public void uploadPhoto(FirebaseCallbackUser firebaseCallbackUser, Uri uri, Context context) {

        String pictureID = UUID.randomUUID().toString();

        storageReference.
                child(pictureID + "." + GeneralHelper.getFileExtension(uri, context)).
                putFile(uri).
                addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if (task.isSuccessful()) {
                            User user = new User("", "", "", "", pictureID + "." + GeneralHelper.getFileExtension(uri, context));
                            firebaseCallbackUser.onCallback(user, ProcessStatus.SUCCESS);
                        }
                    }
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

}
