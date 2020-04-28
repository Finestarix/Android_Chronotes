package edu.bluejack19_2.chronotes.calendar;

import android.net.Uri;
import android.os.Bundle;


import com.google.android.material.tabs.TabLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager.widget.ViewPager;

import android.view.Window;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.calendar.adapters.PagerAdapter;
import edu.bluejack19_2.chronotes.utils.SystemUIHelper;

public class Calendar_Activity extends AppCompatActivity implements Calendar_Fragment.OnFragmentInteractionListener, List_Calendar_Fragment.OnFragmentInteractionListener{

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        Window window = getWindow();
        SystemUIHelper.hideSystemUI(window);
        SystemUIHelper.changeStatusBarColor(window);

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ViewPager v = findViewById(R.id.calendarPager);

        TabLayout tab = findViewById(R.id.calendarTab);
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_calendar));
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_notes));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);
        PagerAdapter adapter = new PagerAdapter(getSupportFragmentManager(), tab.getTabCount());
        v.setAdapter(adapter);
        v.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tab));

        tab.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                v.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }
            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

//        tab.setupWithViewPager(v);
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }
}
