package rix.chronotes.calendar;

import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.view.View;
import android.view.Window;

import java.util.Objects;

import rix.chronotes.R;
import rix.chronotes.utils.GeneralHelper;

public class Calendar extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Window window = getWindow();
        GeneralHelper.hideSystemUI(window);
        GeneralHelper.changeStatusBarColor(window);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

//        Objects.requireNonNull(getSupportActionBar()).hide();

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
    }

}
