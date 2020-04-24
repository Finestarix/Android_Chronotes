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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.Objects;

import br.com.simplepass.loading_button_lib.customViews.CircularProgressButton;
import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.calendar.Calendar;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.GeneralHelper;
import edu.bluejack19_2.chronotes.utils.NetworkHandler;
import edu.bluejack19_2.chronotes.utils.PasswordHandler;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class LoginActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 0;
    private GoogleSignInClient googleSignInClient;

    private EditText emailEditText;
    private EditText passwordEditText;

    private String loginStatus;
    private CircularProgressButton loginButton;

    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        emailEditText = findViewById(R.id.login_email);
        passwordEditText = findViewById(R.id.login_password);

        sharedPreferences = getSharedPreferences("loginData", Context.MODE_PRIVATE);
        getData();

        loginButton = findViewById(R.id.login_button);
        loginButton.setOnClickListener(v -> {

            String email = emailEditText.getText().toString();
            String password = passwordEditText.getText().toString();

            GeneralHelper.disableEditText(emailEditText);
            GeneralHelper.disableEditText(passwordEditText);

            String errorMessage = "";
            if (GeneralHelper.isEmpty(email)
                    || GeneralHelper.isEmpty(password))
                errorMessage = "Please fill all field.";

            else if (!GeneralHelper.isEmail(email))
                errorMessage = "Invalid Email Format.";

            else if (!NetworkHandler.isConnectToInternet(this))
                errorMessage = "You're offline. Please connect to the internet.";

            if (!GeneralHelper.isEmpty(errorMessage)) {
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                GeneralHelper.enableEditText(emailEditText);
                GeneralHelper.enableEditText(passwordEditText);
                return;
            }

            loginStatus = "progress";

            @SuppressLint("StaticFieldLeak")
            AsyncTask<String, String, String> asyncTask = new AsyncTask<String, String, String>() {
                @Override
                protected String doInBackground(String... strings) {
                    FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
                    CollectionReference collectionReference = firebaseFirestore.collection(User.COLLECTION_NAME);
                    collectionReference.
                            whereEqualTo("email", email).
                            get().
                            addOnSuccessListener(queryDocumentSnapshots -> {

                                if (!queryDocumentSnapshots.iterator().hasNext()) {
                                    loginStatus = "no-data";
                                    return;
                                }

                                QueryDocumentSnapshot queryDocumentSnapshot = queryDocumentSnapshots.iterator().next();
                                User user = queryDocumentSnapshot.toObject(User.class);

                                String hashPasswordOriginal = user.getPassword();
                                loginStatus = (PasswordHandler.validatePassword(password, hashPasswordOriginal)) ? "success" : "no-data";
                            }).
                            addOnFailureListener(e -> loginStatus = "failed");

                    int counter = 0;
                    while (loginStatus.equals("progress")) {
                        counter++;
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ignored) {
                        }
                        if (counter > 5)
                            loginStatus = "failed";
                    }

                    return loginStatus;
                }

                @Override
                protected void onPostExecute(String status) {
                    if (loginStatus.equals("success")) {
                        Toast.makeText(getApplicationContext(), "Login success.", Toast.LENGTH_SHORT).show();
                        resetData();
                        goToHome();
                    } else if (loginStatus.equals("failed") || loginStatus.equals("no-data")) {
                        Toast.makeText(getApplicationContext(),
                                (loginStatus.equals("no-data")) ? "Invalid email or password." : "Login failed.",
                                Toast.LENGTH_SHORT).show();
                        saveData(email, password);
                        refreshLogin();
                    }

                    GeneralHelper.enableEditText(emailEditText);
                    GeneralHelper.enableEditText(passwordEditText);
                }
            };

            loginButton.startAnimation();
            asyncTask.execute();
        });

        SignInButton signInButton = findViewById(R.id.google_button);
        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        signInButton.setOnClickListener(v -> {
            googleSignIn();
        });

    }

    @Override
    protected void onStart() {
        GoogleSignInAccount googleSignInAccount = GoogleSignIn.getLastSignedInAccount(this);
//        if (googleSignInAccount != null) {
//            goToHome();
//        }
        super.onStart();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> googleSignInAccountTask = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleGoogleSignInResult(googleSignInAccountTask);
        }
    }

    private void handleGoogleSignInResult(Task<GoogleSignInAccount> googleSignInAccountTask) {
        try {
            GoogleSignInAccount account = googleSignInAccountTask.getResult(ApiException.class);
        } catch (ApiException e) {
            Toast.makeText(LoginActivity.this, "Failed Login Google", Toast.LENGTH_SHORT).show();
        }
    }

    private void googleSignIn() {
        Intent signGoogleSignIn = googleSignInClient.getSignInIntent();
        startActivityForResult(signGoogleSignIn, RC_SIGN_IN);
    }

    public void goToRegister(View view) {
        Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        intentToRegister.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToRegister);
    }

    public void goToHome() {
        Intent intentToCalendar = new Intent(LoginActivity.this, Calendar.class);
        intentToCalendar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToCalendar);
    }

    private void saveData(String email, String password) {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString("email", email);
        editor.putString("password", password);
        editor.apply();
    }

    private void getData() {
        emailEditText.setText(sharedPreferences.getString("email", ""));
        passwordEditText.setText(sharedPreferences.getString("password", ""));
    }

    private void resetData() {
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove("email");
        editor.remove("password");
        editor.apply();
    }

    private void refreshLogin() {
        Intent intentToLogin = getIntent();
        intentToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        startActivity(intentToLogin);
    }
}
