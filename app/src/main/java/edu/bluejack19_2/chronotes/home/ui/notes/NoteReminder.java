package edu.bluejack19_2.chronotes.home.ui.notes;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.home.ui.notes.notification.NoteReceiver;

public class NoteReminder extends DialogFragment {

    private NoteController noteController;
    private Switch aSwitch;
    private Button changeReminder;
    private TextView textView;
    private LinearLayout linearLayout;

    private SimpleDateFormat dateFormatter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes_reminder, container);

        noteController = NoteController.getInstance();

        aSwitch = view.findViewById(R.id.notes_switch);
        linearLayout = view.findViewById(R.id.notes_layout_reminder);
        linearLayout.setVisibility(View.GONE);

        createNotificationChannel();

        aSwitch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (!isChecked) {
                linearLayout.setVisibility(View.GONE);
            } else {
                linearLayout.setVisibility(View.VISIBLE);
            }
        });

        textView = view.findViewById(R.id.text_reminder);
        changeReminder = view.findViewById(R.id.button_notes_change);
        changeReminder.setOnClickListener(v -> {
            showDateDialog();
        });

        dateFormatter = new SimpleDateFormat("dd-MM-yyyy", Locale.US);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null)
            Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void showDateDialog() {
        Calendar newCalendar = Calendar.getInstance();
        DatePickerDialog datePickerDialog = new DatePickerDialog(Objects.requireNonNull(this.requireContext()),
                (view, year, monthOfYear, dayOfMonth) -> {

            Calendar newDate = Calendar.getInstance();
            newDate.set(year, monthOfYear, dayOfMonth);

            textView.setText(dateFormatter.format(newDate.getTime()));
            showTimeDialog();
        }, newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        datePickerDialog.show();
    }

    private void showTimeDialog() {
        Calendar calendar = Calendar.getInstance();
        TimePickerDialog timePickerDialog = new TimePickerDialog(this.getContext(), new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                // TODO: Fix if Minutes 00
                textView.setText(textView.getText().toString() + " " + hourOfDay + ":" + minute);

                dateFormatter = new SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.US);
                Date date = new Date();

                try {
                    date = dateFormatter.parse(textView.getText().toString());
                } catch (ParseException e) {
                }

                setNotification(date.getTime());

            }
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), DateFormat.is24HourFormat(this.getContext()));
        timePickerDialog.show();
    }

    private void setNotification(long time) {
        Toast.makeText(this.getContext(), "Notification", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent(this.getContext(), NoteReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this.getContext(), 0, intent, 0);

        AlarmManager alarmManager = (AlarmManager) requireActivity().getSystemService(Context.ALARM_SERVICE);

        Objects.requireNonNull(alarmManager).set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

//        PendingIntent pendingIntent2 = PendingIntent.getBroadcast(this.getContext(), 0, intent, 0);
//        alarmManager.cancel(pendingIntent2);
    }

    private void createNotificationChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "chronotes";
            String description = "Test Notification";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel notificationChannel = new NotificationChannel("chronotes", name, importance);
            notificationChannel.setDescription(description);

            NotificationManager notificationManager = requireActivity().getSystemService(NotificationManager.class);
            Objects.requireNonNull(notificationManager).createNotificationChannel(notificationChannel);
        }

    }


}
