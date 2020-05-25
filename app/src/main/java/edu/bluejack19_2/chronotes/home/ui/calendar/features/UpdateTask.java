package edu.bluejack19_2.chronotes.home.ui.calendar.features;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;
import com.kunzisoft.switchdatetime.SwitchDateTimeDialogFragment;

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
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.Alarm;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.ColaboratorAdapter;
import edu.bluejack19_2.chronotes.interfaces.StringCallback;
import edu.bluejack19_2.chronotes.interfaces.UserListener;
import edu.bluejack19_2.chronotes.main.MainActivity;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.TaskHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class UpdateTask extends AppCompatActivity {
    private static final String TAG = "Sample";

    private static final String TAG_DATETIME_FRAGMENT = "TAG_DATETIME_FRAGMENT";

    private SwitchDateTimeDialogFragment dateTimeFragment;
    private SwitchDateTimeDialogFragment dateTimeFragment2;

    Spinner priority, repeat;
    TextView dp, et, err;
    TaskHandler hand;
    Button addTag, addNewTask;
    EditText etaddtag, title, detail;
    ChipGroup cg;
    ImageView back, collab;
    Boolean doneTag, doneSelect;
    Task t;
    UpdateTask me;
    UserController usercontrol;
    ArrayList<String>ids, emails;
    ColaboratorAdapter adapt;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_task);
        me = this;
        Bundle bundle = getIntent().getExtras();
        String taskid = bundle.getString("taskid");
        SetVariables();
        createNotifChannel();
        usercontrol = UserController.getInstance();


        SetSpinners();
        SetDatePicker();
        doneTag = doneSelect = false;
        back.setOnClickListener(view -> {
            Intent intentToHome = new Intent(UpdateTask.this, HomeActivity.class);
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
            doneTag = true;

        });
        collab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder colab = new AlertDialog.Builder(UpdateTask.this);
                View dialog = getLayoutInflater().inflate(R.layout.collab_dialog,null);

                RecyclerView rvColab = dialog.findViewById(R.id.rv_collab);
                Button btnColab = dialog.findViewById(R.id.btn_add_collab);
                TextView tvcolav =dialog.findViewById(R.id.tv_add_collab_error);
                EditText addColab = dialog.findViewById(R.id.et_add_collab);
                hand.getAllCollaboratorEmail(t, getApplicationContext(), (id, email1) -> {
//                            Log.d("DEBUG", "ASDASD");
                    ids = id;
                    Log.d("DEBUG", ids.size()+" Start IDs");
                    emails = email1;

                    adapt = new ColaboratorAdapter(t, ids, emails,getApplicationContext(), t.getCreatedBy());
                    rvColab.setAdapter(adapt);
                    rvColab.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                });
                btnColab.setOnClickListener(colabo -> {
                    String email = addColab.getText().toString();
                    if(emails.contains(email)){
                        tvcolav.setText(R.string.calendar_message_update_already_collaborator);
                        return;
                    }
                    usercontrol.getUserByEmail(new UserListener() {
                        @Override
                        public void onCallback(User user, ProcessStatus status) {
                            if(status == ProcessStatus.NOT_FOUND){
                                tvcolav.setText(R.string.calendar_message_update_collaborator_not_found);
                            }
                            else{
                                addColab.setText("");
                                emails.add(user.getEmail());
//                                ids.add(user.getId());
                                Log.d("DEBUG", ids.size()+"aaaaa");

                                adapt.setEmails(emails);
//                                adapt.setIds(ids);

                                t.addUserId(user.getId());
                                hand.updateTask(t,getApplicationContext());
                                adapt.notifyDataSetChanged();
                            }
                        }
                    }, email);
                });

                colab.setView(dialog);
                AlertDialog fin = colab.create();
                fin.show();

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

        t = hand.getTask(taskid, this, val -> {
            Task a = val.get(0);
            title.setText(a.getTitle());
            detail.setText(a.getDetail());
            dp.setText(a.getStart());
            et.setText(a.getEnd());
            priority.setSelection(a.getPriority()-1);
            String[] r = {"None","On Due Date","Daily","Weekly"};
            int select = 0;

            String str = a.getRepeat();
            if(str.equals("None")){
                select = 0;
            }
            else if(str.equals("On Due Date")){
                select = 1;
            }
            else if(str.equals("Daily")){
                select = 2;
            }
            repeat.setSelection(select);
            t = a;
            doneSelect = true;
        });

        Thread tag = new Thread(new Runnable() {
            @Override
            public void run() {
                while(true){
                    if(doneTag && doneSelect){
                        for (String s : tags){
                            Chip c = (Chip) me.getLayoutInflater().inflate(R.layout.custom_chip_filter, null, false) ;

//                            this.cg.addView(c);
                            runOnUiThread(new Runnable() {
                                @Override
                                public void run() {
                                    c.setText(s);
                                    if(t.getTags().contains(c.getText()))c.setChecked(true);
                                    cg.addView(c);
                                }
                            });
                        }
                        break;
                    }
                }
            }
        });
        tag.start();
    }

    private void createNotifChannel(){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            CharSequence name = "task reminder";
            String desc = "Test Notification";
            int important = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel chan = new NotificationChannel("taskalarm",name,important);
            chan.setDescription(desc);

            NotificationManager manage = getSystemService(NotificationManager.class);
            manage.createNotificationChannel(chan);
        }
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
            err.setText(R.string.calendar_message_update_error_empty_title);
        }
        else if(Sdetail.isEmpty()){
            err.setText(R.string.calendar_message_update_error_empty_detail);
        }
        else if (SStart.isEmpty()){
            err.setText(R.string.calendar_message_update_error_empty_start_date);
        }
        else if (SEnd.isEmpty()){
            err.setText(R.string.calendar_message_update_error_empty_end_date);
        }
        else if(getDateDiff(new Date(SStart), new Date(SEnd)) <= 0){
            err.setText(R.string.calendar_message_update_error_date);
        }
        else{
            for(int i : chips){
                Chip c = findViewById(i);
                tags.add(c.getText().toString());
            }

            Task task = new Task(t.getTaskId(), t.getUserId(), SStart, SEnd, Stitle, Sdetail, SRepeat, tags, Sprio, t.getCompleted());
            hand.updateTask(task, this);
        }

        return true;
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
        collab = findViewById(R.id.iv_collab);
    }

    private void SetSpinners() {
        Integer[] p = {1,2,3,4};
        priority = findViewById(R.id.spinner_priority);
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_item, p);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        priority.setAdapter(adapter);

        String[] r = {"None","On Due Date","Daily","Weekly"};
        repeat = findViewById(R.id.spinner_repeat);
        ArrayAdapter<CharSequence> Sadapter = ArrayAdapter.createFromResource(this,R.array.repeat_array, android.R.layout.simple_spinner_item);
//        ArrayAdapter<String> Sadapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item,r);
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
