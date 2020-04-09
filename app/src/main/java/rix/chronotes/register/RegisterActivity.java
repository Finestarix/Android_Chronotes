package rix.chronotes.register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import rix.chronotes.R;
import rix.chronotes.controller.UserController;
import rix.chronotes.login.LoginActivity;
import rix.chronotes.model.User;
import rix.chronotes.utils.GeneralHelper;
import rix.chronotes.utils.SystemUIHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;
    private Button registerButton;

    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        userController = new UserController();

        nameEditText = findViewById(R.id.register_name);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String name = nameEditText.getText().toString();
                String email = emailEditText.getText().toString();
                String password = passwordEditText.getText().toString();

                String errorMessage = "";
                if (GeneralHelper.isEmpty(name)
                        || GeneralHelper.isEmpty(email)
                        || GeneralHelper.isEmpty(password))
                    errorMessage = "Fill All Field !";

                if (!GeneralHelper.isEmpty(errorMessage)) {
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    return;
                }

                User user = new User(name, email, password);

                goToLogin();
            }
        });
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
}
