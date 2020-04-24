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

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Objects;
import java.util.UUID;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHelper;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class RegisterActivity extends AppCompatActivity {

    private EditText nameEditText;
    private EditText emailEditText;
    private EditText passwordEditText;

    private String registerStatus;
    private CircularProgressButton registerButton;

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

            GeneralHelper.disableEditText(nameEditText);
            GeneralHelper.disableEditText(emailEditText);
            GeneralHelper.disableEditText(passwordEditText);

            String errorMessage = "";
            if (GeneralHelper.isEmpty(name)
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

            String userID = UUID.randomUUID().toString();
            String hashPassword = PasswordHandler.generateStrongPasswordHash(password);

            User user = new User(userID, name, email, hashPassword, User.DEFAULT_PICTURE);

            registerStatus = "progress";

            @SuppressLint("StaticFieldLeak")
            AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    DocumentReference documentReference = firebaseFirestore.collection(User.COLLECTION_NAME).
                            document(User.DOCUMENT_NAME + userID);
                    documentReference.set(user).
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
                        Toast.makeText(getApplicationContext(), "Register success.", Toast.LENGTH_SHORT).show();
                        resetData();
                        goToLogin();
                    } else if (registerStatus.equals("failed")) {
                        Toast.makeText(getApplicationContext(), "Register failed.", Toast.LENGTH_SHORT).show();
                        saveData(name, email, password);
                        refreshRegister();
                    }

                    GeneralHelper.enableEditText(nameEditText);
                    GeneralHelper.enableEditText(emailEditText);
                    GeneralHelper.enableEditText(passwordEditText);
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
        intentToRegister.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intentToRegister);
    }
}



