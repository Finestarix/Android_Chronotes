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
    public void onReceive(Context context, Intent i) {
        Log.d("DEBUG", "ONRECEIVE");
        String desc = i.getStringExtra("Title");
        String title = i.getStringExtra("Desc");
        Random rand = new Random();
        int id = rand.nextInt();

        NotificationCompat.Builder build = new NotificationCompat.Builder(context, "taskalarm")
                .setContentText(desc).setContentTitle(title)
                .setSmallIcon(R.drawable.ic_chronotes_dark).setPriority(NotificationCompat.PRIORITY_HIGH);
//        filter.
        NotificationManagerCompat manager = NotificationManagerCompat.from(context);

        manager.notify(id,build.build());
    }

}
