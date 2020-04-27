package edu.bluejack19_2.chronotes.home.ui.setting;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import edu.bluejack19_2.chronotes.R;

public class SettingFragment extends Fragment {

    private TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_setting, container, false);

        textView = root.findViewById(R.id.text_setting);
        textView.setText("Setting Fragment");

        return root;
    }
}
