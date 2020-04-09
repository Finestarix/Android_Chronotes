package rix.chronotes.main.slider;

import androidx.annotation.StringRes;

import rix.chronotes.R;

public class MainResource {

    public static final int TOTAL_PAGE = 4;

    @StringRes
    public static final int[] TITLE_LIST_ID = new int[]{
            R.string.main_slider_fragment_title_welcome,
            R.string.main_slider_fragment_title_calendar,
            R.string.main_slider_fragment_title_notes,
            R.string.main_slider_fragment_title_reminders
    };

    @StringRes
    public static final int[] DESCRIPTION_LIST_ID = new int[]{
            R.string.main_slider_fragment_description_welcome,
            R.string.main_slider_fragment_description_calendar,
            R.string.main_slider_fragment_description_notes,
            R.string.main_slider_fragment_description_reminders
    };

    @StringRes
    public static final int[] ICON_LIST_ID = new int[]{
            R.drawable.ic_chronotes_dark,
            R.drawable.ic_chronotes_dark,
            R.drawable.ic_chronotes_dark,
            R.drawable.ic_chronotes_dark
    };

}
