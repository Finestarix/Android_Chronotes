package edu.bluejack19_2.chronotes.main.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.Objects;
import java.util.UUID;

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

    private TextView loginTextView;
    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private UserController userController;
    private Button registerButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        disableActionBar();
        setContentView(R.layout.activity_register);
        setUIComponent();

        userController = UserController.getInstance();
        loginTextView.setOnClickListener(v -> goToPage(LoginActivity.class));
        registerButton.setOnClickListener(v -> basicRegister());
    }

    private void disableActionBar() {
        Window window = getWindow();
        SystemUIHandler.hideSystemUI(window);
        SystemUIHandler.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }

    private void setUIComponent() {
        loginTextView = findViewById(R.id.bt_register_to_login);
        nameEditText = findViewById(R.id.et_register_name);
        emailEditText = findViewById(R.id.et_register_email);
        passwordEditText = findViewById(R.id.et_register_password);
        registerButton = findViewById(R.id.bt_register);
    }

    private void basicRegister() {

        startRegister();
        String name = nameEditText.getText().toString();
        String email = emailEditText.getText().toString();
        String password = passwordEditText.getText().toString();

        String errorMessage = validateString(name, email, password);
        if (!GeneralHandler.isEmpty(errorMessage)) {
            Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
            endRegister();
            return;
        }

        String userID = UUID.randomUUID().toString();
        String hashPassword = PasswordHandler.generateStrongPasswordHash(password);

        User user = new User(userID, name, email, hashPassword, User.DEFAULT_PICTURE);

        userController.findEmail(emailStatus -> {

            if (emailStatus == ProcessStatus.NOT_FOUND)

                userController.insertNewUser(insertStatus -> {

                    String message = (insertStatus == ProcessStatus.SUCCESS) ?
                            getResources().getString(R.string.register_message_success) : getResources().getString(R.string.register_message_failed);
                    Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    if (insertStatus == ProcessStatus.SUCCESS) {
                        goToPage(LoginActivity.class);
                    }

                    endRegister();

                }, user);

            else {
                String message = (emailStatus == ProcessStatus.FOUND) ?
                        getResources().getString(R.string.register_message_exist_email) : getResources().getString(R.string.register_message_failed);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                endRegister();
            }

        }, email);
    }

    private String validateString(String name, String email, String password) {
        String errorMessage = "";

        if (GeneralHandler.isEmpty(name)
                || GeneralHandler.isEmpty(email)
                || GeneralHandler.isEmpty(password))
            errorMessage = getResources().getString(R.string.register_message_success);

        else if (GeneralHandler.isNotEmail(email))
            errorMessage = getResources().getString(R.string.register_message_wrong_email);

        else if (GeneralHandler.isNotAlphaNumeric(password))
            errorMessage = getResources().getString(R.string.register_message_wrong_password);

        else if (NetworkHandler.isNotConnectToInternet(this))
            errorMessage = getResources().getString(R.string.register_message_offline);

        return errorMessage;
    }

    private void startRegister() {
        loginTextView.setEnabled(false);
        nameEditText.setEnabled(false);
        emailEditText.setEnabled(false);
        passwordEditText.setEnabled(false);
        registerButton.setEnabled(false);

        registerButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_gray));
        loginTextView.setTextColor(getResources().getColor(R.color.Gray));
    }

    private void endRegister() {
        loginTextView.setEnabled(true);
        nameEditText.setEnabled(true);
        emailEditText.setEnabled(true);
        passwordEditText.setEnabled(true);
        registerButton.setEnabled(true);

        registerButton.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.bg_round_blue));
        loginTextView.setTextColor(getResources().getColor(R.color.backgroundLightColor));
    }

    private void goToPage(Class aClass) {
        Intent intent = new Intent(RegisterActivity.this, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}



