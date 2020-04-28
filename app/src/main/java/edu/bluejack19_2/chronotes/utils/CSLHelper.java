package edu.bluejack19_2.chronotes.utils;

import android.content.Context;
import android.content.res.ColorStateList;

import edu.bluejack19_2.chronotes.R;

public class CSLHelper {

    public static ColorStateList CheckBoxRed(Context con){
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        con.getResources().getColor(R.color.Crimson),
                        con.getResources().getColor(R.color.DarkGray),
                }
        );
    }

    public static ColorStateList CheckBoxOrange(Context con) {
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked}, // checked
                },
                new int[]{
                        con.getResources().getColor(R.color.DarkOrange),
                        con.getResources().getColor(R.color.DarkGray),
                }
        );
    }

    public static ColorStateList CheckBoxYellow(Context con){
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        con.getResources().getColor(R.color.Gold),
                        con.getResources().getColor(R.color.DarkGray),
                }
        );
    }

    public static ColorStateList CheckBoxDefault(Context con){
        return new ColorStateList(
                new int[][]{
                        new int[]{-android.R.attr.state_checked}, // unchecked
                        new int[]{android.R.attr.state_checked} , // checked
                },
                new int[]{
                        con.getResources().getColor(R.color.LightGreen),
                        con.getResources().getColor(R.color.DarkGray),
                }
        );
    }
}
