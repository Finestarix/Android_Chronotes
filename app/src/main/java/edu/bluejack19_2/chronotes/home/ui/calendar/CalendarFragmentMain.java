package edu.bluejack19_2.chronotes.home.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.google.android.material.tabs.TabLayout;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.PagerAdapter;

public class CalendarFragmentMain extends Fragment  {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_calendar_main, container, false);

        ViewPager v = view.findViewById(R.id.calendarPager);

        TabLayout tab = view.findViewById(R.id.calendarTab);
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_calendar));
        tab.addTab(tab.newTab().setIcon(R.drawable.ic_notes));
        tab.setTabGravity(TabLayout.GRAVITY_FILL);

        assert getFragmentManager() != null;
        PagerAdapter adapter = new PagerAdapter(getFragmentManager(), tab.getTabCount());
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

        return view;
    }
}
