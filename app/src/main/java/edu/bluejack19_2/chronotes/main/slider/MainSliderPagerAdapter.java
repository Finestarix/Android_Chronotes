package edu.bluejack19_2.chronotes.main.slider;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

public class MainSliderPagerAdapter extends FragmentPagerAdapter {

    public MainSliderPagerAdapter(@NonNull FragmentManager fragmentManager, int behavior) {
        super(fragmentManager, behavior);
    }

    @NonNull
    @Override
    public Fragment getItem(int position) {
        return MainSliderFragment.newInstance(position);
    }

    @Override
    public int getCount() {
        return MainResource.TOTAL_PAGE;
    }


}
