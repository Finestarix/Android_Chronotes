package edu.bluejack19_2.chronotes.main.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Objects;
import java.util.UUID;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.main.register.RegisterActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHandler;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.SystemUIHandler;
import edu.bluejack19_2.chronotes.utils.session.RememberMeStorage;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 0;
    private GoogleSignInClient googleSignInClient;

    private TextView registerTextView;
    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberCheckBox;
    private Button loginButton;
    private SignInButton googleButton;

    private UserController userController;

    private ProcessStatus loginStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionStorage.isLoggedIn(this))
            goToPage(HomeActivity.class);
        else {
            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);

            disableActionBar();
            setContentView(R.layout.activity_login);
            setUIComponent();

            getRememberMe();

            loginStatus = ProcessStatus.DONE;
            userController = UserController.getInstance();
            registerTextView.setOnClickListener(v -> goToPage(RegisterActivity.class));
            loginButton.setOnClickListener(v -> basicSignIn());
            googleButton.setOnClickListener(v -> googleSignIn());
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(googleSignInAccountTask);
        }
    }

    private void disableActionBar() {
        Window window = getWindow();
        SystemUIHandler.hideSystemUI(window);
        SystemUIHandler.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void setUIComponent() {
        registerTextView = findViewById(R.id.bt_login_to_register);
        emailEditText = findViewById(R.id.et_login_email);
        passwordEditText = findViewById(R.id.et_login_password);
        rememberCheckBox = findViewById(R.id.cb_login_remember_me);
        loginButton = findViewById(R.id.bt_login);
        googleButton = findViewById(R.id.google_button);
    }

    private void getRememberMe() {
        String email = RememberMeStorage.getRememberMeEmail(this);
        String password = RememberMeStorage.getRememberMePassword(this);

        emailEditText.setText(email);
        passwordEditText.setText(password);
    }

    private void basicSignIn() {

        startLogin();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String errorMessage = validateString(email, password);
        if (!GeneralHandler.isEmpty(errorMessage)) {
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            endLogin();
            return;
        }

        userController.getUserByEmail((user, processStatus) -> {

            if (processStatus == ProcessStatus.FOUND) {

                String hashPasswordOriginal = user.getPassword();
                loginStatus = (PasswordHandler.validatePassword(password, hashPasswordOriginal)) ?
                        ProcessStatus.SUCCESS : ProcessStatus.INVALID;

                if (loginStatus == ProcessStatus.SUCCESS) {
                    SessionStorage.setSessionStorage(LoginActivity.this, user.getId());

                    if (rememberCheckBox.isChecked())
                        RememberMeStorage.setRememberMe(this, email, password);
                    else
                        RememberMeStorage.removeRememberMe(this);
                }

            } else
                loginStatus = processStatus;

            String message = (loginStatus == ProcessStatus.SUCCESS) ?
                    getResources().getString(R.string.login_message_success) : (loginStatus == ProcessStatus.FAILED) ?
                    getResources().getString(R.string.login_message_failed) : getResources().getString(R.string.login_message_invalid);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            if (loginStatus == ProcessStatus.SUCCESS)
                goToPage(HomeActivity.class);

            endLogin();
        }, email);
    }

    private String validateString(String email, String password) {
        String errorMessage = "";

        if (GeneralHandler.isEmpty(email)
                || GeneralHandler.isEmpty(password))
            errorMessage = getResources().getString(R.string.login_message_empty_field);

        else if (GeneralHandler.isNotEmail(email))
            errorMessage = getResources().getString(R.string.login_message_wrong_email);

        else if (GeneralHandler.isNotAlphaNumeric(password))
            errorMessage = getResources().getString(R.string.login_message_wrong_password);

        else if (NetworkHandler.isNotConnectToInternet(this))
            errorMessage = getResources().getString(R.string.login_message_offline);

        return errorMessage;
    }

    private void googleSignIn() {
        if (loginStatus != ProcessStatus.DONE)
            return;

        startLogin();
        Intent signGoogleSignIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signGoogleSignIn, RC_SIGN_IN);
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> googleSignInAccountTask) {
        try {
            GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);

            if (account != null) {

                String name = Objects.requireNonNull(account.getDisplayName());
                String email = Objects.requireNonNull(account.getEmail());

                userController.findEmail(emailStatus -> {
                    if (emailStatus == ProcessStatus.NOT_FOUND) {

                        String ID = UUID.randomUUID().toString();
                        User user = new User(ID, name, email, "", User.DEFAULT_PICTURE);

                        userController.insertNewUser(insertStatus -> {

                            String message = (insertStatus == ProcessStatus.SUCCESS) ?
                                    getResources().getString(R.string.login_message_success) : getResources().getString(R.string.login_message_failed);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (insertStatus == ProcessStatus.SUCCESS) {
                                SessionStorage.setSessionStorage(LoginActivity.this, user.getId());
                                goToPage(HomeActivity.class);
                            }

                            endLogin();

                        }, user);

                    } else if (emailStatus == ProcessStatus.FOUND) {

                        userController.getUserByEmail((user, processStatus) -> {

                            String message = (processStatus == ProcessStatus.FOUND) ?
                                    getResources().getString(R.string.login_message_success) : getResources().getString(R.string.login_message_failed);
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (processStatus == ProcessStatus.FOUND) {
                                SessionStorage.setSessionStorage(LoginActivity.this, user.getId());
                                goToPage(HomeActivity.class);
                            }

                            endLogin();
                        }, email);
                    }
                }, email);
            }
        } catch (ApiException e) {
            String message = getResources().getString(R.string.login_message_failed);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
            endLogin();
        }
    }

    private void startLogin() {
        loginStatus = ProcessStatus.INIT;

        rememberCheckBox.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        loginButton.setEnabled(false);
        googleButton.setEnabled(false);
        registerTextView.setEnabled(false);

        loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_gray));
        registerTextView.setTextColor(getResources().getColor(R.color.Gray));
    }

    private void endLogin() {
        loginStatus = ProcessStatus.DONE;

        rememberCheckBox.setEnabled(true);
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        loginButton.setEnabled(true);
        googleButton.setEnabled(true);
        registerTextView.setEnabled(true);

        loginButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_blue));
        registerTextView.setTextColor(getResources().getColor(R.color.backgroundLightColor));
    }

    private void goToPage(Class aClass) {
        Intent intent = new Intent(LoginActivity.this, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
