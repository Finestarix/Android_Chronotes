package edu.bluejack19_2.chronotes.main.register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHandler;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.SystemUIHandler;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private ProcessStatus registerStatus;
    private CircularProgressButton registerButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = getWindow();
        SystemUIHandler.hideSystemUI(window);
        SystemUIHandler.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        nameEditText = findViewById(R.id.register_name);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);

        sharedPreferences = getSharedPreferences("registerData", Context.MODE_PRIVATE);
        getData();

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {

            disableField();
            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            String errorMessage = validateString(name, email, password);
            if (!GeneralHandler.isEmpty(errorMessage)) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                enableField();
                return;
            }

            String userID = UUID.randomUUID().toString();
            String hashPassword = PasswordHandler.generateStrongPasswordHash(password);
            User user = new User(userID, name, email, hashPassword, User.DEFAULT_PICTURE);
            Log.d("Testing", user.getId());

            registerStatus = ProcessStatus.INIT;

            @SuppressLint("StaticFieldLeak")
            AsyncTask<ProcessStatus, ProcessStatus, ProcessStatus> asyncTask =
                    new AsyncTask<ProcessStatus, ProcessStatus, ProcessStatus>() {

                        @Override
                        protected ProcessStatus doInBackground(ProcessStatus... processStatuses) {

                            UserController userController = UserController.getInstance();
                            userController.findEmail(emailStatus -> {
                                if (emailStatus == ProcessStatus.NOT_FOUND)
                                    userController.insertNewUser(insertStatus -> registerStatus = insertStatus, user);
                                else
                                    registerStatus = emailStatus;
                            }, email);

                            try {
                                Thread.sleep(5000);
                            } catch (InterruptedException ignored) {
                            }

                            if (registerStatus == ProcessStatus.INIT)
                                registerStatus = ProcessStatus.FAILED;

                            return registerStatus;
                        }

                        @Override
                        protected void onPostExecute(ProcessStatus result) {

                            String message = (result == ProcessStatus.SUCCESS) ?
                                    "Register success." : (result == ProcessStatus.FAILED) ?
                                    "Register failed." : "The email account already exists.";
                            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                            if (result == ProcessStatus.SUCCESS)
                                goToLogin();
                            else {
                                saveData(name, email, password);
                                goToRegister();
                            }

                            enableField();
                        }
                    };

            registerButton.startAnimation();
            asyncTask.execute();
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        resetData();
    }

    private void goToLogin() {
        Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }

    public void goToLogin(View view) {
        Intent intentToLogin = new Intent(RegisterActivity.this, LoginActivity.class);
        intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }

    private String validateString(String name, String email, String password) {
        String errorMessage = "";

        if (GeneralHandler.isEmpty(name)
                || GeneralHandler.isEmpty(email)
                || GeneralHandler.isEmpty(password))
            errorMessage = "Please fill all field.";

        else if (GeneralHandler.isNotEmail(email))
            errorMessage = "Invalid Email Format.";

        else if (NetworkHandler.isNotConnectToInternet(this))
            errorMessage = "You're offline. Please connect to the internet.";

        return errorMessage;
    }

    private void enableField() {
        GeneralHandler.enableEditText(nameEditText);
        GeneralHandler.enableEditText(emailEditText);
        GeneralHandler.enableEditText(passwordEditText);
    }

    private void disableField() {
        GeneralHandler.disableEditText(nameEditText);
        GeneralHandler.disableEditText(emailEditText);
        GeneralHandler.disableEditText(passwordEditText);
    }

    private void saveData(String name, String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void getData() {
        nameEditText.setText(sharedPreferences.getString("name", ""));
        emailEditText.setText(sharedPreferences.getString("email", ""));
        passwordEditText.setText(sharedPreferences.getString("password", ""));
    }

    private void resetData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.clear();
        editor.apply();
    }

    private void goToRegister() {
        Intent intentToRegister = getIntent();
        intentToRegister.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        startActivity(intentToRegister);
    }

}



