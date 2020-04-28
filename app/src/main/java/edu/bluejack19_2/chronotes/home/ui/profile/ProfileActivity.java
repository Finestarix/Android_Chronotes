package edu.bluejack19_2.chronotes.home.ui.profile;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHandler;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;
import edu.bluejack19_2.chronotes.utils.SystemUIHandler;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private StorageReference storageReference;
    private CollectionReference collectionReference;

    private ImageView profileImageView;
    private Uri imageUri;

    private CircularProgressButton updateButton;
    private EditText nameEditText;
    private EditText passwordEditText;

    private ProcessStatus updateStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionStorage.getSessionStorage(this) == null) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_profile);

        Window window = getWindow();
        SystemUIHandler.hideSystemUI(window);
        SystemUIHandler.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initializeFirebase();

        nameEditText = findViewById(R.id.profile_name);
        passwordEditText = findViewById(R.id.profile_password);

        profileImageView = findViewById(R.id.icon_user_login);
        profileImageView.setOnClickListener(v -> openGallery());

        ImageView backImageView = findViewById(R.id.back_icon);
        backImageView.setOnClickListener(v -> goToHome());

        Button logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            SessionStorage.removeSessionStorage(ProfileActivity.this);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();

            goToLogin();
        });

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {

            disableField();
            String name = nameEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            String errorMessage = validateString(name, password);
            if (!GeneralHandler.isEmpty(errorMessage)) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                enableField();
                return;
            }

            updateStatus = ProcessStatus.INIT;

            @SuppressLint("StaticFieldLeak")
            AsyncTask<ProcessStatus, ProcessStatus, ProcessStatus> asyncTask =
                    new AsyncTask<ProcessStatus, ProcessStatus, ProcessStatus>() {

                        @Override
                        protected ProcessStatus doInBackground(ProcessStatus... processStatuses) {

                            UserController userController = UserController.getInstance();
                            userController.getUserByID((user, processStatus) -> {

                                if (processStatus == ProcessStatus.NOT_FOUND) {
                                    goToLogin();

                                } else if (processStatus == ProcessStatus.FAILED) {
                                    updateStatus = processStatus;

                                } else if (processStatus == ProcessStatus.FOUND) {

                                    if (!PasswordHandler.validatePassword(password, user.getPassword()))
                                        updateStatus = ProcessStatus.INVALID;

                                    else {

                                        if (imageUri == null) {
                                            String id = SessionStorage.getSessionStorage(ProfileActivity.this);
                                            User userTemp = new User(id, name, user.getEmail(), password, user.getPicture());
                                            userController.updateUserByID(processStatusUpdate -> updateStatus = processStatusUpdate, userTemp);

                                        } else {
                                            String picture = UUID.randomUUID().toString() + "." +
                                                    GeneralHandler.getFileExtension(imageUri, getApplicationContext());

                                            userController.uploadPhoto((userNew, processStatusImage) -> {

                                                if (processStatusImage == ProcessStatus.SUCCESS) {
                                                    String id = SessionStorage.getSessionStorage(ProfileActivity.this);
                                                    User userTemp = new User(id, name, user.getEmail(), password, picture);
                                                    userController.updateUserByID(processStatusUpdate -> updateStatus = processStatusUpdate, userTemp);
                                                } else
                                                    updateStatus = processStatusImage;
                                            }, picture, imageUri);
                                        }
                                    }
                                }
                            }, SessionStorage.getSessionStorage(ProfileActivity.this));

                            while (updateStatus == ProcessStatus.INIT) {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException ignored) {
                                }
                            }

                            return updateStatus;
                        }

                        @Override
                        protected void onPostExecute(ProcessStatus processStatus) {
                            Toast.makeText(getApplicationContext(),
                                    (processStatus == ProcessStatus.INVALID) ? "Invalid user password." :
                                            (processStatus == ProcessStatus.FAILED) ? "Update profile failed." : "Update profile success.",
                                    Toast.LENGTH_SHORT).show();
                            goToProfile();
                        }
                    };

            updateButton.startAnimation();
            asyncTask.execute();
        });

        getCurrentUserData();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_IMAGE_REQUEST &&
                resultCode == RESULT_OK &&
                data != null &&
                data.getData() != null) {
            imageUri = data.getData();

            List<String> fileExtension = Arrays.asList("jpg", "jpeg", "png", "svg");
            String fileUploadExtension = getFileExtension(imageUri);

            if (fileExtension.contains(fileUploadExtension)) {
                RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.ic_loading_placeholder).error(R.drawable.ic_failed);
                Glide.with(getApplicationContext()).load(imageUri).apply(requestOptions).into(profileImageView);
            } else {
                Toast.makeText(getApplicationContext(), "Invalid photo extension.", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private String getFileExtension(Uri uri) {
        ContentResolver contentResolver = getContentResolver();
        MimeTypeMap mime = MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(contentResolver.getType(uri));
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private void initializeFirebase() {
        FirebaseStorage firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference(User.PHOTO_NAME);

        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(User.COLLECTION_NAME);
    }

    private String validateString(String name, String password) {
        String errorMessage = "";

        if (GeneralHandler.isEmpty(name)
                || GeneralHandler.isEmpty(password))
            errorMessage = "Please fill all field.";

        else if (NetworkHandler.isNotConnectToInternet(this))
            errorMessage = "You're offline. Please connect to the internet.";

        return errorMessage;
    }

    private void getCurrentUserData() {

        collectionReference.
                whereEqualTo("id", SessionStorage.getSessionStorage(this)).
                get().
                addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.iterator().hasNext()) {
                        goToLogin();
                        return;
                    }

                    QueryDocumentSnapshot queryDocumentSnapshot = queryDocumentSnapshots.iterator().next();
                    User user = queryDocumentSnapshot.toObject(User.class);

                    nameEditText.setText(user.getName());

                    storageReference.
                            child(user.getPicture()).
                            getBytes(Long.MAX_VALUE).
                            addOnSuccessListener(bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.ic_loading_placeholder).error(R.drawable.ic_failed);
                                Glide.with(getApplicationContext()).asBitmap().load(bitmap).apply(requestOptions).into(profileImageView);
                            }).
                            addOnFailureListener(e -> {
                                Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(profileImageView);

                                String errorMessage = "Failed to load data. Please check your internet connection.";
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                            });
                }).
                addOnFailureListener(e -> {
                    Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(profileImageView);

                    String errorMessage = "Failed to load data. Please check your internet connection.";
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                });
    }

    private void enableField() {
        GeneralHandler.enableEditText(nameEditText);
        GeneralHandler.enableEditText(passwordEditText);
    }

    private void disableField() {
        GeneralHandler.disableEditText(nameEditText);
        GeneralHandler.disableEditText(passwordEditText);
    }

    private void goToProfile() {
        Intent intentToProfile = getIntent();
        intentToProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToProfile);
    }

    private void goToLogin() {
        Intent intentToProfile = new Intent(ProfileActivity.this, LoginActivity.class);
        intentToProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToProfile);
    }

    private void goToHome() {
        Intent intentToHome = new Intent(ProfileActivity.this, HomeActivity.class);
        intentToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToHome);
    }

}
