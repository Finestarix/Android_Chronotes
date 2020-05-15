package edu.bluejack19_2.chronotes.home.ui.calendar.adapters;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import java.util.Date;
import java.util.Random;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.main.register.RegisterActivity;

public class Alarm extends BroadcastReceiver {
    public static Alarm a;

    public Alarm(){

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("DEBUG", "ONRECEIVE");

        Random rand = new Random();
        int id = rand.nextInt(300);
        NotificationCompat.Builder build = new NotificationCompat.Builder(context, "taskalarm")
                .setContentText("ALARM " + id + " " +(new Date(System.currentTimeMillis())).toString()).setContentTitle("Reminder")
                .setSmallIcon(R.drawable.ic_chronotes_dark).setPriority(NotificationCompat.PRIORITY_HIGH);
//        filter.
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(id,build.build());
    }

}
