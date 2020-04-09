package rix.chronotes.login;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.res.ResourcesCompat;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import java.util.Objects;

import rix.chronotes.R;
import rix.chronotes.utils.GeneralHelper;

public class LoginActivity extends AppCompatActivity {

    ImageView imageViewTop;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Window window = getWindow();
        GeneralHelper.hideSystemUI(window);
        GeneralHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();
    }


}
