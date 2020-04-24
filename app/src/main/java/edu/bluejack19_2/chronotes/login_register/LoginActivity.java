package edu.bluejack19_2.chronotes.login_register;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
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

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.calendar.Calendar;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class LoginActivity extends AppCompatActivity {

    private final int RC_SIGN_IN = 0;
    SignInButton signInButton;
    GoogleSignInClient googleSignInClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Button buttonLogin = findViewById(R.id.login_button);
        buttonLogin.setOnClickListener(v -> {
            goToHome();
        });

        signInButton = findViewById(R.id.google_button);
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


}
