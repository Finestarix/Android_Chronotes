package rix.chronotes.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.Objects;

import rix.chronotes.R;
import rix.chronotes.calendar.Calendar;
import rix.chronotes.register.RegisterActivity;
import rix.chronotes.utils.SystemUIHelper;

public class LoginActivity extends AppCompatActivity {

    ImageView imageViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        Button buttonLogin = findViewById(R.id.login_button);
        buttonLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intentToCalendar = new Intent(LoginActivity.this, Calendar.class);
                intentToCalendar.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intentToCalendar);
            }
        });
    }

    public void goToRegister(View view) {
        Intent intentToRegister = new Intent(LoginActivity.this, RegisterActivity.class);
        intentToRegister.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToRegister);
    }

}
