package edu.bluejack19_2.chronotes.home;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.login_register.LoginActivity;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.profile.ProfileActivity;
import edu.bluejack19_2.chronotes.utils.SessionStorage;

public class HomeActivity extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private View view;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;
    private FirebaseFirestore firebaseFirestore;
    private CollectionReference collectionReference;

    private ShimmerFrameLayout mShimmerViewContainer;
    private TextView nameTextView;
    private TextView emailTextView;
    private ImageView iconImageView;
    private Uri iconUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionStorage.getSessionStorage(this) == null) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_home);

        initializeFirebase();
        initializeNavigationDrawer();

        mShimmerViewContainer = view.findViewById(R.id.shimmer_view_container);
        mShimmerViewContainer.startShimmerAnimation();

        nameTextView = view.findViewById(R.id.name_user_login);
        emailTextView = view.findViewById(R.id.email_user_login);
        iconImageView = view.findViewById(R.id.icon_user_login);
        iconImageView.setOnClickListener(v -> goToProfile());

        getCurrentUserData();

        // TODO: Change Profile by Google Sign In
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

    private void initializeFirebase() {
        firebaseStorage = FirebaseStorage.getInstance();
        storageReference = firebaseStorage.getReference();

        firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(User.COLLECTION_NAME);
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

    private void getCurrentUserData() {

        collectionReference.
                whereEqualTo("id", SessionStorage.getSessionStorage(this)).
                get().
                addOnSuccessListener(queryDocumentSnapshots -> {

                    if (!queryDocumentSnapshots.iterator().hasNext()) {
                        goToLogin();
                        return;
                    }

                    QueryDocumentSnapshot queryDocumentSnapshot = queryDocumentSnapshots.iterator().next();
                    User user = queryDocumentSnapshot.toObject(User.class);

                    nameTextView.setText(user.getName());
                    emailTextView.setText(user.getEmail());

                    Toast.makeText(getApplicationContext(), user.getPicture(), Toast.LENGTH_SHORT).show();

                    storageReference.
                            child(User.PHOTO_NAME + "/" + user.getPicture()).
                            getBytes(Long.MAX_VALUE).
                            addOnSuccessListener(bytes -> {
                                Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

                                RequestOptions requestOptions = new RequestOptions().centerCrop().error(R.drawable.ic_failed);
                                Glide.with(this).asBitmap().load(bitmap).apply(requestOptions).into(iconImageView);

                                iconImageView.setBackgroundColor(Color.TRANSPARENT);

                                emailTextView.setBackgroundColor(Color.TRANSPARENT);
                                emailTextView.setTextColor(Color.WHITE);

                                nameTextView.setBackgroundColor(Color.TRANSPARENT);
                                nameTextView.setTextColor(Color.WHITE);

                                mShimmerViewContainer.stopShimmerAnimation();
                            }).
                            addOnFailureListener(e -> {
                                Glide.with(this).load(R.drawable.ic_user).into(iconImageView);
                                iconImageView.setBackgroundColor(Color.TRANSPARENT);

                                emailTextView.setBackgroundColor(Color.TRANSPARENT);
                                emailTextView.setTextColor(Color.WHITE);

                                nameTextView.setBackgroundColor(Color.TRANSPARENT);
                                nameTextView.setTextColor(Color.WHITE);

                                String errorMessage = "Failed to load data. Please check your internet connection.";
                                Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

                                mShimmerViewContainer.stopShimmerAnimation();
                            });
                }).
                addOnFailureListener(e -> {
                    Glide.with(this).load(R.drawable.ic_user).into(iconImageView);

                    nameTextView.setText("User Name");
                    nameTextView.setBackgroundColor(Color.TRANSPARENT);
                    nameTextView.setTextColor(Color.WHITE);

                    emailTextView.setText("User Email");
                    emailTextView.setBackgroundColor(Color.TRANSPARENT);
                    emailTextView.setTextColor(Color.WHITE);

                    String errorMessage = "Failed to load data. Please check your internet connection.";
                    Toast.makeText(getApplicationContext(), errorMessage, Toast.LENGTH_SHORT).show();

                    mShimmerViewContainer.stopShimmerAnimation();
                });
    }

    private void goToLogin() {
        Intent intentToLogin = new Intent(HomeActivity.this, LoginActivity.class);
        intentToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }

    private void goToProfile() {
        Intent intentToLogin = new Intent(HomeActivity.this, ProfileActivity.class);
        intentToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }
}
