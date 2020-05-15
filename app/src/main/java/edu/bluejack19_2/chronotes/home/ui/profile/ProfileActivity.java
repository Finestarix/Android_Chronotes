package edu.bluejack19_2.chronotes.home.ui.profile;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressImageButton;
import de.hdodenhof.circleimageview.CircleImageView;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHandler;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.SystemUIHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class ProfileActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 1;

    private Uri imageUri;

    private CircleImageView profileImageView;
    private ImageView backImageView;
    private EditText nameEditText;
    private EditText passwordEditText;
    private Button updateButton;
    private Button logoutButton;

    private ProcessStatus updateStatus;
    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SessionStorage.isLoggedIn(this))
            goToPage(LoginActivity.class);

        else {
            disableActionBar();
            setContentView(R.layout.activity_profile);
            setUIComponent();

            userController = UserController.getInstance();
            profileImageView.setOnClickListener(v -> openGallery());
            backImageView.setOnClickListener(v -> goToPage(HomeActivity.class));
            logoutButton.setOnClickListener(v -> goToPage(LoginActivity.class));
            updateButton.setOnClickListener(v -> updateUserData());

            getCurrentUserData();
        }

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
            String fileUploadExtension = GeneralHandler.getFileExtension(imageUri, getApplicationContext());

            if (fileExtension.contains(fileUploadExtension)) {
                RequestOptions requestOptions = new RequestOptions().centerCrop().placeholder(R.drawable.ic_loading_placeholder).error(R.drawable.ic_failed);
                Glide.with(getApplicationContext()).load(imageUri).apply(requestOptions).into(profileImageView);
            } else {
                String message = getResources().getString(R.string.profile_message_invalid_extension);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void disableActionBar() {
        Window window = getWindow();
        SystemUIHandler.hideSystemUI(window);
        SystemUIHandler.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void setUIComponent() {
        backImageView = findViewById(R.id.iv_profile_back);
        profileImageView = findViewById(R.id.iv_profile_icon);
        nameEditText = findViewById(R.id.et_profile_name);
        passwordEditText = findViewById(R.id.et_profile_password);
        updateButton = findViewById(R.id.bt_profile_update);
        logoutButton = findViewById(R.id.bt_profile_logout);
    }

    private void getCurrentUserData() {

        startUpdate();

        userController.getUserByID((user, processStatus) -> {

            if (processStatus == ProcessStatus.NOT_FOUND)
                goToPage(LoginActivity.class);

            else if (processStatus == ProcessStatus.FAILED) {

                Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(profileImageView);

                endUpdate();
                String errorMessage = getResources().getString(R.string.profile_message_error_load_data);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

            } else {

                nameEditText.setText(user.getName());

                userController.getUserPicture((bytes, processStatusImage) -> {

                    if (processStatusImage == ProcessStatus.FAILED) {

                        Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(profileImageView);

                        String errorMessage = getResources().getString(R.string.profile_message_error_load_data);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    } else {
                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                        RequestOptions requestOptions = new RequestOptions().centerCrop().
                                placeholder(R.drawable.ic_loading_placeholder).error(R.drawable.ic_failed);
                        Glide.with(getApplicationContext()).asBitmap().load(bitmap).apply(requestOptions).into(profileImageView);
                    }

                    endUpdate();

                }, user.getPicture());
            }
        }, SessionStorage.getSessionStorage(this));
    }

    private void updateUserData() {

        startUpdate();

        String name = nameEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String errorMessage = validateString(name, password);
        if (!GeneralHandler.isEmpty(errorMessage)) {
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            endUpdate();
            return;
        }

        updateStatus = ProcessStatus.INIT;
        userController.getUserByID((user, processStatus) -> {

            if (processStatus == ProcessStatus.NOT_FOUND)
                goToPage(LoginActivity.class);

            else if (processStatus == ProcessStatus.FAILED) {
                String message = getResources().getString(R.string.profile_message_failed);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                endUpdate();

            } else if (processStatus == ProcessStatus.FOUND) {

                if (!PasswordHandler.validatePassword(password, user.getPassword())) {
                    String message = getResources().getString(R.string.profile_message_invalid);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                    endUpdate();

                } else if (imageUri == null) {
                    String id = SessionStorage.getSessionStorage(ProfileActivity.this);
                    User userTemp = new User(id, name, user.getEmail(), password, user.getPicture());

                    userController.updateUserByID(processStatusUpdate -> {
                        String message = (processStatusUpdate == ProcessStatus.SUCCESS) ?
                                getResources().getString(R.string.profile_message_success) :
                                getResources().getString(R.string.profile_message_failed);
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                        endUpdate();
                    }, userTemp);

                } else {
                    String picture = UUID.randomUUID().toString() + "." +
                            GeneralHandler.getFileExtension(imageUri, getApplicationContext());

                    userController.uploadPhoto((userNew, processStatusImage) -> {

                        if (processStatusImage == ProcessStatus.SUCCESS) {
                            String id = SessionStorage.getSessionStorage(ProfileActivity.this);
                            User userTemp = new User(id, name, user.getEmail(), password, picture);

                            userController.updateUserByID(processStatusUpdate -> {
                                String message = (processStatusUpdate == ProcessStatus.SUCCESS) ?
                                        getResources().getString(R.string.profile_message_success) :
                                        getResources().getString(R.string.profile_message_failed);
                                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                                endUpdate();
                            }, userTemp);

                        } else {
                            String message = getResources().getString(R.string.profile_message_failed);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                            endUpdate();
                        }
                    }, picture, imageUri);
                }
            }
        }, SessionStorage.getSessionStorage(ProfileActivity.this));

    }

    private void startUpdate() {
        backImageView.setEnabled(false);
        nameEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        updateButton.setEnabled(false);
        logoutButton.setEnabled(false);

        updateButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_gray));
        logoutButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_gray));
    }

    private void endUpdate() {
        backImageView.setEnabled(true);
        nameEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        updateButton.setEnabled(true);
        logoutButton.setEnabled(true);

        updateButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_blue));
        logoutButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_red));
    }

    private void openGallery() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, PICK_IMAGE_REQUEST);
    }

    private String validateString(String name, String password) {
        String errorMessage = "";

        if (GeneralHandler.isEmpty(name)
                || GeneralHandler.isEmpty(password))
            errorMessage = getResources().getString(R.string.profile_message_empty_field);

        else if (GeneralHandler.isNotAlphaNumeric(password))
            errorMessage = getResources().getString(R.string.profile_message_wrong_password);

        else if (NetworkHandler.isNotConnectToInternet(this))
            errorMessage = getResources().getString(R.string.profile_message_offline);

        return errorMessage;
    }

    private void goToPage(Class aClass) {

        if (aClass == LoginActivity.class) {
            SessionStorage.removeSessionStorage(ProfileActivity.this);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();
        }

        Intent intent = new Intent(ProfileActivity.this, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
