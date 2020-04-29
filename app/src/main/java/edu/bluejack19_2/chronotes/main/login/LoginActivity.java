package edu.bluejack19_2.chronotes.main.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;

import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.main.register.RegisterActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHandler;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;
import edu.bluejack19_2.chronotes.utils.SystemUIHandler;

public class LoginActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 0;
    private GoogleSignInClient googleSignInClient;

    private EditText emailEditText;
    private EditText passwordEditText;
    private CheckBox rememberCheckBox;

    private ProcessStatus loginStatus;
    private CircularProgressButton loginButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        sharedPreferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);

        if (!SessionStorage.getSessionStorage(this).equals("")) {
            goToHome();
            return;
        }

        setContentView(R.layout.activity_login);

        Window window = getWindow();
        SystemUIHandler.hideSystemUI(window);
        SystemUIHandler.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);
        rememberCheckBox = findViewById(R.id.remember_me);

        getData();
        if (emailEditText.getText().toString().equals("") && passwordEditText.getText().toString().equals(""))
            getDataRememberMe();

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {

            disableField();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            String errorMessage = validateString(email, password);
            if (!GeneralHandler.isEmpty(errorMessage)) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                enableField();
                return;
            }

            loginStatus = ProcessStatus.INIT;

            @SuppressLint("StaticFieldLeak")
            AsyncTask<ProcessStatus, ProcessStatus, ProcessStatus> asyncTask =
                    new AsyncTask<ProcessStatus, ProcessStatus, ProcessStatus>() {
                        @Override
                        protected ProcessStatus doInBackground(ProcessStatus... processStatuses) {

                            UserController userController = UserController.getInstance();
                            userController.getUserByEmail((user, processStatus) -> {

                                if (processStatus == ProcessStatus.FOUND) {

                                    String hashPasswordOriginal = user.getPassword();
                                    loginStatus = (PasswordHandler.validatePassword(password, hashPasswordOriginal)) ?
                                            ProcessStatus.SUCCESS : ProcessStatus.INVALID;

                                    if (loginStatus == ProcessStatus.SUCCESS)
                                        SessionStorage.setSessionStorage(LoginActivity.this, user.getId());

                                } else
                                    loginStatus = processStatus;
                            }, email);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }

                            if (loginStatus == ProcessStatus.INIT)
                                loginStatus = ProcessStatus.FAILED;

                            return loginStatus;
                        }

                        @Override
                        protected void onPostExecute(ProcessStatus processStatus) {

                            String message = (processStatus == ProcessStatus.SUCCESS) ?
                                    "Login success." : (processStatus == ProcessStatus.FAILED) ?
                                    "Login failed." : "Invalid email or password.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (processStatus == ProcessStatus.SUCCESS &&
                                    rememberCheckBox.isChecked())
                                saveDataRememberMe(email, password);
                            else
                                resetDataRememberMe();

                            if (processStatus == ProcessStatus.SUCCESS) {
                                resetData();
                                goToHome();
                            } else {
                                saveData(email, password);
                                goToLogin();
                            }

                            enableField();
                        }
                    };

            loginButton.startAnimation();
            asyncTask.execute();
        });

        SignInButton signInButton = findViewById(R.id.google_button);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        signInButton.setOnClickListener(v -> googleSignIn());

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(googleSignInAccountTask);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetData();
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> googleSignInAccountTask) {
        try {
            GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);

            if (account != null) {

                UserController userController = UserController.getInstance();

                String name = Objects.requireNonNull(account.getDisplayName());
                String email = Objects.requireNonNull(account.getEmail());

                userController.findEmail(emailStatus -> {
                    if (emailStatus == ProcessStatus.NOT_FOUND) {

                        String ID = UUID.randomUUID().toString();
                        User user = new User(ID, name, email, "", User.DEFAULT_PICTURE);

                        userController.insertNewUser(insertStatus -> {

                            if (insertStatus == ProcessStatus.SUCCESS) {
                                SessionStorage.setSessionStorage(LoginActivity.this, user.getId());
                                goToHome();
                            }

                            String message = (insertStatus == ProcessStatus.SUCCESS) ?
                                    "Login success." : "Login failed.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                        }, user);

                    } else if (emailStatus == ProcessStatus.FOUND) {

                        userController.getUserByEmail((user, processStatus) -> {

                            if (processStatus == ProcessStatus.FOUND) {
                                SessionStorage.setSessionStorage(LoginActivity.this, user.getId());
                                goToHome();
                            }

                            String message = (processStatus == ProcessStatus.FOUND) ?
                                    "Login success." : "Login failed.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                        }, email);
                    }
                }, email);
            }
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Login failed.", Toast.LENGTH_SHORT).show();
        }
    }

    private void googleSignIn() {
        Intent signGoogleSignIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signGoogleSignIn, RC_SIGN_IN);
    }

    private String validateString(String email, String password) {
        String errorMessage = "";

        if (GeneralHandler.isEmpty(email)
                || GeneralHandler.isEmpty(password))
            errorMessage = "Please fill all field.";

        else if (GeneralHandler.isNotEmail(email))
            errorMessage = "Invalid Email Format.";

        else if (NetworkHandler.isNotConnectToInternet(this))
            errorMessage = "You're offline. Please connect to the internet.";

        return errorMessage;
    }

    private void enableField() {
        GeneralHandler.enableCheckBox(rememberCheckBox);
        GeneralHandler.enableEditText(emailEditText);
        GeneralHandler.enableEditText(passwordEditText);
    }

    private void disableField() {
        GeneralHandler.disableCheckBox(rememberCheckBox);
        GeneralHandler.disableEditText(emailEditText);
        GeneralHandler.disableEditText(passwordEditText);
    }

    private void goToLogin() {
        Intent intentToLogin = getIntent();
        intentToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intentToLogin);
    }

    public void goToRegister(View view) {
        Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        intentToRegister.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToRegister);
    }

    public void goToHome() {
        Intent intentToCalendar = new Intent(LoginActivity.this, HomeActivity.class);
        intentToCalendar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToCalendar);
    }

    private void getData() {
        emailEditText.setText(sharedPreferences.getString("email", ""));
        passwordEditText.setText(sharedPreferences.getString("password", ""));
    }

    private void saveData(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void resetData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
    }

    private void getDataRememberMe() {
        emailEditText.setText(sharedPreferences.getString("email-remember", ""));
        passwordEditText.setText(sharedPreferences.getString("password-remember", ""));
    }

    private void saveDataRememberMe(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email-remember", email);
        editor.putString("password-remember", password);
        editor.apply();
    }

    private void resetDataRememberMe() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email-remember");
        editor.remove("password-remember");
        editor.apply();
    }

}
