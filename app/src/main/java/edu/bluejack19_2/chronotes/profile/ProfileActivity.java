package edu.bluejack19_2.chronotes.profile;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.login_register.LoginActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHelper;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.SessionStorage;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private SharedPreferences sharedPreferences;

    private StorageReference storageReference;
    private CollectionReference collectionReference;

    private ImageView backImageView;
    private ImageView profileImageView;
    private Uri imageUri;

    private CircularProgressButton updateButton;
    private Button logoutButton;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private String updateStatus;
    private StorageTask storageTask;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("profileData", Context.MODE_PRIVATE);

        if (SessionStorage.getSessionStorage(this) == null) {
            goToProfile();
            return;
        }

        setContentView(R.layout.activity_profile);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        initializeFirebase();

        nameEditText = findViewById(R.id.profile_name);
        emailEditText = findViewById(R.id.profile_email);
        passwordEditText = findViewById(R.id.profile_password);

        getData();

        profileImageView = findViewById(R.id.icon_user_login);
        profileImageView.setOnClickListener(v -> openGallery());

        backImageView = findViewById(R.id.back_icon);
        backImageView.setOnClickListener(v -> {
            goToHome();
        });

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            SessionStorage.removeSessionStorage(ProfileActivity.this);
            goToProfile();
        });

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(v -> {

            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            String errorMessage = "";
            if (GeneralHelper.isEmpty(email)
                    || GeneralHelper.isEmpty(email)
                    || GeneralHelper.isEmpty(password))
                errorMessage = "Please fill all field.";

            else if (!GeneralHelper.isEmail(email))
                errorMessage = "Invalid Email Format.";

            else if (!NetworkHandler.isConnectToInternet(this))
                errorMessage = "You're offline. Please connect to the internet.";

            if (!GeneralHelper.isEmpty(errorMessage)) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                GeneralHelper.enableEditText(nameEditText);
                GeneralHelper.enableEditText(emailEditText);
                GeneralHelper.enableEditText(passwordEditText);
                return;
            }

            GeneralHelper.disableEditText(nameEditText);
            GeneralHelper.disableEditText(emailEditText);
            GeneralHelper.disableEditText(passwordEditText);

            updateStatus = "progress";

            @SuppressLint("StaticFieldLeak")
            AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {

                    collectionReference.
                            whereEqualTo("id", SessionStorage.getSessionStorage(ProfileActivity.this)).
                            get().
                            addOnSuccessListener(queryDocumentSnapshots -> {

                                QueryDocumentSnapshot queryDocumentSnapshot = queryDocumentSnapshots.iterator().next();
                                User user = queryDocumentSnapshot.toObject(User.class);

                                if (!PasswordHandler.validatePassword(password, user.getPassword()))
                                    updateStatus = "no-data";

                                else {
                                    String pictureID = UUID.randomUUID().toString();

                                    storageReference.
                                            child(pictureID + "." + getFileExtension(imageUri)).
                                            putFile(imageUri).
                                            addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                                    collectionReference.
                                                            document(User.DOCUMENT_NAME + user.getId()).
                                                            update("name", name, "email", email, "picture", pictureID + "." + getFileExtension(imageUri)).
                                                            addOnSuccessListener(aVoid -> updateStatus = "success").
                                                            addOnFailureListener(e -> updateStatus = "failed");
                                                }
                                            }).
                                            addOnFailureListener(e -> updateStatus = "failed");
                                }
                            }).
                            addOnFailureListener(e -> updateStatus = "failed");

                    int counter = 0;
                    while (updateStatus.equals("progress")) {
                        counter++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        if (counter > 8)
                            updateStatus = "failed";
                    }

                    return updateStatus;
                }

                @Override
                protected void onPostExecute(String status) {
                    Toast.makeText(getApplicationContext(),
                            (updateStatus.equals("no-data")) ? "Invalid user password." :
                                    (updateStatus.equals("success")) ? "Update profile failed." : "Update profile success.",
                            Toast.LENGTH_SHORT).show();
                    saveData(name, email);
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

    private void getCurrentUserData() {

        collectionReference.
                whereEqualTo("id", SessionStorage.getSessionStorage(this)).
                get().
                addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.iterator().hasNext()) {
                        goToProfile();
                        return;
                    }

                    QueryDocumentSnapshot queryDocumentSnapshot = queryDocumentSnapshots.iterator().next();
                    User user = queryDocumentSnapshot.toObject(User.class);

                    nameEditText.setText(user.getName());
                    emailEditText.setText(user.getEmail());

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

    private void goToProfile() {
        Intent intentToProfile = new Intent(ProfileActivity.this, LoginActivity.class);
        intentToProfile.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToProfile);
    }

    private void goToHome() {
        Intent intentToHome = new Intent(ProfileActivity.this, HomeActivity.class);
        intentToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToHome);
    }

    private void saveData(String name, String email) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.apply();
    }

    private void getData() {
        nameEditText.setText(sharedPreferences.getString("name", ""));
        emailEditText.setText(sharedPreferences.getString("email", ""));
    }

    private void resetData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("name");
        editor.remove("email");
        editor.apply();
    }

}
