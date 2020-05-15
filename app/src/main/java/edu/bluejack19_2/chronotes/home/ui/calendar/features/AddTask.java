package edu.bluejack19_2.chronotes.home.ui.calendar.features;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.UUID;
import java.util.Vector;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.Alarm;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.TaskHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class AddTask extends AppCompatActivity {
    Spinner priority, repeat;
    TextView dp, et, err;
    TaskHandler hand;
    Button addTag, addNewTask;
    EditText etaddtag, title, detail;
    ChipGroup cg;
    ImageView back;
//    private SimpleDateFormat mFormatter = new SimpleDateFormat("MMMM dd yyyy hh:mm aa");

    private static final String TAG = "Sample";

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private SwitchDateTimeDialogFragment dateTimeFragment;
    private SwitchDateTimeDialogFragment dateTimeFragment2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        SetVariables();

        SetSpinners();
        SetDatePicker();

        back.setOnClickListener(view -> {
            Intent intentToHome = new Intent(AddTask.this, HomeActivity.class);
            intentToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentToHome);
        });
        dp.setOnClickListener(view -> {
            dateTimeFragment.startAtCalendarView();
            dateTimeFragment.setDefaultDateTime(new Date());
            dateTimeFragment.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        });
        et.setOnClickListener(view -> {
            dateTimeFragment2.startAtCalendarView();
            dateTimeFragment2.setDefaultDateTime(new Date());
            dateTimeFragment2.show(getSupportFragmentManager(), TAG_DATETIME_FRAGMENT);
        });
        cg = findViewById(R.id.cg_tag);
        Vector<String> tags = new Vector<>();
        ArrayList<Task> tasks = hand.getAllTask(SessionStorage.getSessionStorage(this), getApplicationContext(), val -> {
            ArrayList<String> tampung = new ArrayList<>();
            for(Task t: val){
                tampung= t.getTags();
                for(String s :tampung){
                    if(!tags.contains(s)){
                        tags.add(s);
                    }
                }
            }
            for (String s : tags){
                Chip c = (Chip) this.getLayoutInflater().inflate(R.layout.custom_chip_filter, null, false) ;
                c.setText(s);
                cg.addView(c);
            }
        });

        addTag.setOnClickListener(view -> {
            String tex = etaddtag.getText().toString();
            if(tex.isEmpty())return;
            Chip c = (Chip) getLayoutInflater().inflate(R.layout.custom_chip_filter, null, false) ;
            c.setText(tex);
            cg.addView(c);

            etaddtag.setText("");
        });
        addNewTask.setOnClickListener(view -> validate());

    }


    private boolean validate(){
        err.setText("");
        String Stitle = title.getText().toString();
        String Sdetail = detail.getText().toString();
        String SStart = dp.getText().toString();
        String SEnd = et.getText().toString();
        String SRepeat = repeat.getSelectedItem().toString();
        ArrayList<String> tags = new ArrayList<>();
        int Sprio = Integer.parseInt(priority.getSelectedItem().toString());
        List<Integer> chips =  cg.getCheckedChipIds();
//        Chip c= findViewById(chips.get(0));
//        c.getText();
        Log.d("DEBUG", getDateDiff(new Date(SStart), new Date(SEnd))+"");

        if(Stitle.isEmpty()){
            err.setText("Title Cannot Be Empty!");
        }
        else if(Sdetail.isEmpty()){
            err.setText("Detail Cannot Be Empty!");
        }
        else if (SStart.isEmpty()){
            err.setText("Start Date Must Be Filled!");
        }
        else if (SEnd.isEmpty()){
            err.setText("End Date Must Be Filled!");
        }
        else if(getDateDiff(new Date(SStart), new Date(SEnd)) <= 0){
            err.setText("End Date Can't Be Less Than Start Date");
        }
        else{
            for(int i : chips){
                Chip c = findViewById(i);
                tags.add(c.getText().toString());
            }

            ArrayList<String> users = new ArrayList<>();
            users.add(SessionStorage.getSessionStorage(this));
            Task t = new Task(UUID.randomUUID().toString(), users, SStart, SEnd, Stitle, Sdetail, SRepeat, tags, Sprio, false);
            hand.insertTask(t, this);
            go(t);

        }

        return true;
    }
    private void go(Task t){
        int Time = 10000;
        AlarmManager manager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(AddTask.this, Alarm.class);
        i.putExtra("Title", t.getTitle());
        i.putExtra("Desc", t.getDetail());

        PendingIntent pi = PendingIntent.getBroadcast(this,0,i,0);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pi);
        if(t.getRepeat().equals("Daily")){
            manager.setRepeating(AlarmManager.RTC_WAKEUP, new Date(t.getEnd()).getTime(),AlarmManager.INTERVAL_DAY,pi);
        }
        else if(t.getRepeat().equals("On Due Date")){
            manager.set(AlarmManager.RTC_WAKEUP, new Date(t.getEnd()).getTime(),pi);
        }
    }


    private void SetVariables() {
        hand = TaskHandler.GetInstance();
        dp = findViewById(R.id.tv_datePicker);
        et = findViewById(R.id.tv_datePicker2);
        addTag = findViewById(R.id.btn_add_tag);
        etaddtag = findViewById(R.id.et_Add_Tag);
        err = findViewById(R.id.tv_add_task_error);
        title = findViewById(R.id.et_Title);
        addNewTask = findViewById(R.id.btn_add_new_task);
        detail = findViewById(R.id.et_Detail);
        back = findViewById(R.id.iv_back);
    }

    private void SetSpinners() {
        Integer[] p = {1,2,3,4};
        priority = findViewById(R.id.spinner_priority);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, p);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(adapter);

//        String[] r = {"None","On Due Date","Daily","Weekly"};
        repeat = findViewById(R.id.spinner_repeat);
        ArrayAdapter<CharSequence> Sadapter = ArrayAdapter.createFromResource(this,R.array.repeat_array, android.R.layout.simple_spinner_item);
        Sadapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        repeat.setAdapter(Sadapter);
    }

    public long getDateDiff(Date date1, Date date2) {
        long diffInMillies = date2.getTime() - date1.getTime();
        return diffInMillies;
    }
    private void SetDatePicker() {
        dateTimeFragment = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        dateTimeFragment2 = (SwitchDateTimeDialogFragment) getSupportFragmentManager().findFragmentByTag(TAG_DATETIME_FRAGMENT);
        if(dateTimeFragment == null) {
            dateTimeFragment = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }
        if(dateTimeFragment2 == null) {
            dateTimeFragment2 = SwitchDateTimeDialogFragment.newInstance(
                    getString(R.string.label_datetime_dialog),
                    getString(android.R.string.ok),
                    getString(android.R.string.cancel)
            );
        }

        dateTimeFragment.setTimeZone(TimeZone.getDefault());
        dateTimeFragment2.setTimeZone(TimeZone.getDefault());

        final SimpleDateFormat myDateFormat = new SimpleDateFormat("d MMM yyyy HH:mm", Locale.getDefault());
        int Year = Calendar.getInstance().get(Calendar.YEAR);

        dateTimeFragment.set24HoursMode(true);
        dateTimeFragment.setMinimumDateTime(new GregorianCalendar(Year - 10, Calendar.JANUARY, 1).getTime());
        dateTimeFragment.setMaximumDateTime(new GregorianCalendar(Year+20, Calendar.DECEMBER, 31).getTime());

        dateTimeFragment2.set24HoursMode(true);
        dateTimeFragment2.setMinimumDateTime(new GregorianCalendar(Year - 10, Calendar.JANUARY, 1).getTime());
        dateTimeFragment2.setMaximumDateTime(new GregorianCalendar(Year+20, Calendar.DECEMBER, 31).getTime());


        try {
            dateTimeFragment.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }
        try {
            dateTimeFragment2.setSimpleDateMonthAndDayFormat(new SimpleDateFormat("MMMM dd", Locale.getDefault()));
        } catch (SwitchDateTimeDialogFragment.SimpleDateMonthAndDayFormatException e) {
            Log.e(TAG, e.getMessage());
        }

        // Set listener for date
        // Or use dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonClickListener() {
        dateTimeFragment.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
//                textView.setText(myDateFormat.format(date));
                Toast.makeText(getApplicationContext(),date.toString(), Toast.LENGTH_LONG).show();
                dp.setText(date.toString());
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
//                textView.setText("");
            }
        });
        dateTimeFragment2.setOnButtonClickListener(new SwitchDateTimeDialogFragment.OnButtonWithNeutralClickListener() {
            @Override
            public void onPositiveButtonClick(Date date) {
//                textView.setText(myDateFormat.format(date));
                et.setText(date.toString());
            }

            @Override
            public void onNegativeButtonClick(Date date) {
                // Do nothing
            }

            @Override
            public void onNeutralButtonClick(Date date) {
                // Optional if neutral button does'nt exists
//                textView.setText("");
            }
        });
    }

}
