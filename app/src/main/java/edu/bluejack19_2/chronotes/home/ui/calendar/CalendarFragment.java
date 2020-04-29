package edu.bluejack19_2.chronotes.home.ui.calendar;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import edu.bluejack19_2.chronotes.R;

public class CalendarFragment extends Fragment {

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        // Default Fragment for Navigation Drawer
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }
}
