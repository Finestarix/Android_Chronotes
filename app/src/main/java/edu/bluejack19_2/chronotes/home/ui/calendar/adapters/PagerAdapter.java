package edu.bluejack19_2.chronotes.home.ui.calendar.adapters;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import edu.bluejack19_2.chronotes.home.ui.calendar.Calendar_Fragment;
import edu.bluejack19_2.chronotes.home.ui.calendar.List_Calendar_Fragment;

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
                Calendar_Fragment c = new Calendar_Fragment();
                return c;
            case 1:
                List_Calendar_Fragment lc = new List_Calendar_Fragment();
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
