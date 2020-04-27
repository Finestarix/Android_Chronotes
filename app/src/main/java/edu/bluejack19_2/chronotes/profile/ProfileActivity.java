package edu.bluejack19_2.chronotes.profile;

import android.content.ContentResolver;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.login_register.LoginActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.SessionStorage;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private ImageView profileImageView;
    private Uri imageUri;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private Button updateButton;
    private Button logoutButton;
    private EditText idEditText;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionStorage.getSessionStorage(this) == null) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_profile);

        initializeFirebase();

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        profileImageView = findViewById(R.id.icon_user_login);
        profileImageView.setOnClickListener(v -> openGallery());

        String user = SessionStorage.getSessionStorage(this);

        FirebaseStorage firebaseFirestore = FirebaseStorage.getInstance();
        StorageReference storageReference = firebaseFirestore.getReference();

        idEditText = findViewById(R.id.profile_id);
        nameEditText = findViewById(R.id.profile_name);
        emailEditText = findViewById(R.id.profile_email);
        passwordEditText = findViewById(R.id.profile_password);

        logoutButton = findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(v -> {
            SessionStorage.removeSessionStorage(ProfileActivity.this);
            goToLogin();
        });

        updateButton = findViewById(R.id.update_button);
        updateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO: Update Profile
            }
        });
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
                Glide.with(this).load(imageUri).apply(requestOptions).into(profileImageView);
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
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(User.COLLECTION_NAME);
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

//                    nameTextView.setText(user.getName());
//                    emailTextView.setText(user.getEmail());

                    Toast.makeText(getApplicationContext(), user.getPicture(), Toast.LENGTH_SHORT).show();

                    storageReference.
                            child(User.PHOTO_NAME + "/" + user.getPicture()).
                            getBytes(Long.MAX_VALUE).
                            addOnSuccessListener(bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                RequestOptions requestOptions = new RequestOptions().centerCrop().error(R.drawable.ic_failed);
//                                Glide.with(this).asBitmap().load(bitmap).apply(requestOptions).into(iconImageView);

//                                iconImageView.setBackgroundColor(Color.TRANSPARENT);

//                                emailTextView.setBackgroundColor(Color.TRANSPARENT);
//                                emailTextView.setTextColor(Color.WHITE);

//                                nameTextView.setBackgroundColor(Color.TRANSPARENT);
//                                nameTextView.setTextColor(Color.WHITE);

//                                mShimmerViewContainer.stopShimmerAnimation();
                            }).
                            addOnFailureListener(e -> {
                                Glide.with(this).load(R.drawable.ic_user).into(iconImageView);
//                                iconImageView.setBackgroundColor(Color.TRANSPARENT);

//                                emailTextView.setBackgroundColor(Color.TRANSPARENT);
//                                emailTextView.setTextColor(Color.WHITE);

//                                nameTextView.setBackgroundColor(Color.TRANSPARENT);
//                                nameTextView.setTextColor(Color.WHITE);

                                String errorMessage = "Failed to load data. Please check your internet connection.";
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

//                                mShimmerViewContainer.stopShimmerAnimation();
                            });
                }).
                addOnFailureListener(e -> {
                    Glide.with(this).load(R.drawable.ic_user).into(iconImageView);

//                    nameTextView.setText("User Name");
//                    nameTextView.setBackgroundColor(Color.TRANSPARENT);
//                    nameTextView.setTextColor(Color.WHITE);

//                    emailTextView.setText("User Email");
//                    emailTextView.setBackgroundColor(Color.TRANSPARENT);
//                    emailTextView.setTextColor(Color.WHITE);

                    String errorMessage = "Failed to load data. Please check your internet connection.";
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

//                    mShimmerViewContainer.stopShimmerAnimation();
                });
    }

    private void goToLogin() {
        Intent intentToLogin = new Intent(ProfileActivity.this, LoginActivity.class);
        intentToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }

}
