package edu.bluejack19_2.chronotes.main.slider;

import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import edu.bluejack19_2.chronotes.R;

public class MainSliderFragment extends Fragment {

    private static final String ARG_CURRENT_SLIDER = "current_slider";
    private int currentPosition;

    public MainSliderFragment() {
        currentPosition = -1;
    }

    static MainSliderFragment newInstance(int currentPosition) {
        Bundle bundle = new Bundle();
        bundle.putInt(ARG_CURRENT_SLIDER, currentPosition);

        MainSliderFragment mainSliderFragment = new MainSliderFragment();
        mainSliderFragment.setArguments(bundle);

        return mainSliderFragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null)
            currentPosition = getArguments().getInt(ARG_CURRENT_SLIDER);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
                             ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_main_slider, container, false);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        setImageViewIcon(view);
        setTextViewTitle(view);
        setTextViewDescription(view);
    }

    private void setImageViewIcon(@NonNull View view) {
        ImageView imageViewIcon = view.findViewById(R.id.main_slider_fragment_icon);
        int imageViewID = MainResource.ICON_LIST_ID[currentPosition];
        imageViewIcon.setImageResource(imageViewID);
    }

    private void setTextViewTitle(@NonNull View view) {
        TextView textViewTitle = view.findViewById(R.id.main_slider_fragment_title);
        int textViewTitleID = MainResource.TITLE_LIST_ID[currentPosition];
        textViewTitle.setText(textViewTitleID);
    }

    private void setTextViewDescription(@NonNull View view) {
        TextView textViewDescription = view.findViewById(R.id.main_slider_fragment_description);
        int textViewDescriptionID = MainResource.DESCRIPTION_LIST_ID[currentPosition];
        textViewDescription.setText(textViewDescriptionID);
    }

}
