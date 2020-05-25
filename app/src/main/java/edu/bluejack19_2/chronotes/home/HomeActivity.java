package edu.bluejack19_2.chronotes.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.facebook.shimmer.ShimmerFrameLayout;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.navigation.NavigationView;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.home.ui.profile.ProfileActivity;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private View view;

    private ShimmerFrameLayout mShimmerViewContainer;

    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView iconImageView;

    private UserController userController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getTheme().applyStyle(R.style.OverlayRed, true);

        if (!SessionStorage.isLoggedIn(this))
            goToPage(LoginActivity.class);

        else {
            setContentView(R.layout.activity_home);
            initializeNavigationDrawer();
            setUIComponent();

            userController = UserController.getInstance();

            iconImageView.setOnClickListener(v -> goToPage(ProfileActivity.class));

            mShimmerViewContainer.startShimmerAnimation();
            getCurrentUserData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    private void initializeNavigationDrawer() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);

        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_notes, R.id.nav_calendar, R.id.nav_setting)
                .setDrawerLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        view = navigationView.getHeaderView(0);
    }

    private void setUIComponent() {
        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        nameTextView = view.findViewById(R.id.name_user_login);
        emailTextView = view.findViewById(R.id.email_user_login);
        iconImageView = view.findViewById(R.id.iv_profile_icon);
    }

    private void getCurrentUserData() {

        userController.getUserByID((user, processStatus) -> {

            if (processStatus == ProcessStatus.NOT_FOUND) {
                goToPage(LoginActivity.class);

            } else if (processStatus == ProcessStatus.FAILED) {

                nameTextView.setText(R.string.nav_header_title);
                emailTextView.setText(R.string.nav_header_subtitle);
                Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(iconImageView);

                endGetCurrentUser();
                String errorMessage = getResources().getString(R.string.home_message_error_load_data);
                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

            } else {

                nameTextView.setText(user.getName());
                emailTextView.setText(user.getEmail());

                userController.getUserPicture((bytes, processStatusImage) -> {

                    if (processStatusImage == ProcessStatus.SUCCESS) {

                        Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                        RequestOptions requestOptions = new RequestOptions().centerCrop().error(R.drawable.ic_failed);
                        Glide.with(getApplicationContext()).asBitmap().load(bitmap).apply(requestOptions).into(iconImageView);

                    } else if (processStatusImage == ProcessStatus.FAILED) {
                        Glide.with(getApplicationContext()).load(R.drawable.ic_user).into(iconImageView);

                        String errorMessage = getResources().getString(R.string.home_message_error_load_image);
                        Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();
                    }

                    endGetCurrentUser();

                }, user.getPicture());
            }
        }, SessionStorage.getSessionStorage(this));
    }

    private void endGetCurrentUser() {
        iconImageView.setBackgroundColor(Color.TRANSPARENT);
        nameTextView.setBackgroundColor(Color.TRANSPARENT);
        emailTextView.setBackgroundColor(Color.TRANSPARENT);

        emailTextView.setTextColor(Color.WHITE);
        nameTextView.setTextColor(Color.WHITE);

        mShimmerViewContainer.stopShimmerAnimation();
    }

    private void goToPage(Class aClass) {

        if (aClass == LoginActivity.class) {
            SessionStorage.removeSessionStorage(HomeActivity.this);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();
        }

        Intent intent = new Intent(HomeActivity.this, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

}
