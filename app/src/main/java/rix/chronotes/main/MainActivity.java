package rix.chronotes.main;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import java.util.Objects;

import rix.chronotes.R;
import rix.chronotes.login.LoginActivity;
import rix.chronotes.main.slider.MainSliderPagerAdapter;
import rix.chronotes.utils.SystemUIHelper;

public class MainActivity extends AppCompatActivity {

    private ViewPager mainViewPager;
    private Button mainButton;
    private Button mainButtonSkip;
    private MainSliderPagerAdapter mainSliderPageAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);
        Objects.requireNonNull(getSupportActionBar()).hide();

        mainSliderPageAdapter = new MainSliderPagerAdapter(
                getSupportFragmentManager(),
                FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT
        );

        mainViewPager = findViewById(R.id.main_view_pager);
        mainViewPager.setAdapter(mainSliderPageAdapter);
        mainViewPager.addOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
            @Override
            public void onPageSelected(int currentPosition) {
                int mainSliderPageAdapterPosition = mainSliderPageAdapter.getCount() - 1;
                int mainButtonTextID =
                        (currentPosition == mainSliderPageAdapterPosition) ?
                                R.string.main_slider_fragment_continue :
                                R.string.main_slider_fragment_next;
                mainButton.setText(mainButtonTextID);
            }
        });

        TabLayout tabLayout = findViewById(R.id.main_tab_layout);
        tabLayout.setupWithViewPager(mainViewPager);

        mainButtonSkip = findViewById(R.id.main_button_skip);
        mainButtonSkip.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goToLogin();
            }
        });

        mainButton = findViewById(R.id.main_button);
        mainButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String buttonText = mainButton.getText().toString();
                if(buttonText.equals(getString(R.string.main_slider_fragment_continue)))
                    goToLogin();
                else if (mainViewPager.getCurrentItem() < mainSliderPageAdapter.getCount()) {
                    int mainViewPagerCurrentItem = mainViewPager.getCurrentItem() + 1;
                    mainViewPager.setCurrentItem(mainViewPagerCurrentItem);
                }
            }
        });
    }

    private void goToLogin() {
        Intent intentToLogin = new Intent(MainActivity.this, LoginActivity.class);
        intentToLogin.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }

}
