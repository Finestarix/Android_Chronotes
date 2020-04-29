package edu.bluejack19_2.chronotes.home.ui.calendar.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import edu.bluejack19_2.chronotes.home.ui.calendar.CalendarFragment;
import edu.bluejack19_2.chronotes.home.ui.calendar.ListCalendarFragment;

public class PagerAdapter extends FragmentStatePagerAdapter{

    int noOfTabs;

    public PagerAdapter(@NonNull FragmentManager fm, int tabs) {
        super(fm,tabs);
        noOfTabs = tabs;
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        switch(position){
            case 0:
                CalendarFragment c = new CalendarFragment();
                return c;
            case 1:
                ListCalendarFragment lc = new ListCalendarFragment();
                return lc;
            default:
                return null;

        }
    }

    @Override
    public int getCount() {
        return noOfTabs;
    }
}
