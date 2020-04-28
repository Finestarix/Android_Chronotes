package edu.bluejack19_2.chronotes.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkHandler {

    public static boolean isNotConnectToInternet(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.
                getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = (connectivityManager != null) ?
                connectivityManager.getActiveNetworkInfo() : null;
        return networkInfo == null || !networkInfo.isAvailable() || !networkInfo.isConnected();
    }

}
