package edu.bluejack19_2.chronotes.login_register;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHelper;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    public CircularProgressButton registerButton;

    private String registerStatus;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        nameEditText = findViewById(R.id.register_name);
        emailEditText = findViewById(R.id.register_email);
        passwordEditText = findViewById(R.id.register_password);

        sharedPreferences = getSharedPreferences("registerData", Context.MODE_PRIVATE);
        getData();

        registerButton = findViewById(R.id.register_button);
        registerButton.setOnClickListener(v -> {

            String name = nameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            disableEditText(nameEditText);
            disableEditText(emailEditText);
            disableEditText(passwordEditText);

            String errorMessage = "";
            if (GeneralHelper.isEmpty(name)
                    || GeneralHelper.isEmpty(email)
                    || GeneralHelper.isEmpty(password))
                errorMessage = "Fill All Field !";

            else if (!GeneralHelper.isEmail(email))
                errorMessage = "Invalid Email Format !";

            if (!GeneralHelper.isEmpty(errorMessage)) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                enableEditText(nameEditText);
                enableEditText(emailEditText);
                enableEditText(passwordEditText);
                return;
            }

            String userID = UUID.randomUUID().toString();
            String hashPassword = PasswordHandler.generateStrongPasswordHash(password);

            Map<String, String> users = new HashMap<>();
            users.put(User.KEY_ID, userID);
            users.put(User.KEY_NAME, name);
            users.put(User.KEY_EMAIL, email);
            users.put(User.KEY_PASSWORD, hashPassword);
            users.put(User.KEY_PICTURE, User.DEFAULT_PICTURE);

            registerStatus = "progress";

            @SuppressLint("StaticFieldLeak")
            AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();

                    firebaseFirestore.collection(User.COLLECTION_NAME).document(User.DOCUMENT_NAME + userID).set(users).
                            addOnSuccessListener(aVoid -> registerStatus = "success").
                            addOnFailureListener(e -> registerStatus = "failed");

                    int counter = 0;
                    while (registerStatus.equals("progress")) {
                        counter++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        if (counter > 5)
                            registerStatus = "failed";
                    }

                    return registerStatus;
                }

                @Override
                protected void onPostExecute(String status) {
                    if (registerStatus.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Register Success !", Toast.LENGTH_SHORT).show();
                        resetData();
                        goToLogin();
                    } else if (registerStatus.equals("failed")) {
                        Toast.makeText(getApplicationContext(), "Register Failed !", Toast.LENGTH_SHORT).show();
                        saveData(name, email, password);
                        refreshRegister();
                    }

                    enableEditText(nameEditText);
                    enableEditText(emailEditText);
                    enableEditText(passwordEditText);
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

    private void enableEditText(EditText editText) {
        editText.setEnabled(true);
    }

    private void disableEditText(EditText editText) {
        editText.setEnabled(false);
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
        editor.remove("name");
        editor.remove("email");
        editor.remove("password");
        editor.apply();
    }

    private void refreshRegister() {
        Intent intentToRegister = getIntent();
        intentToRegister.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intentToRegister);
    }
}



