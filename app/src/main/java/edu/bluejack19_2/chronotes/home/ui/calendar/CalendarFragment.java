package edu.bluejack19_2.chronotes.home.ui.calendar;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applandeo.materialcalendarview.CalendarView;
import com.applandeo.materialcalendarview.EventDay;
import com.applandeo.materialcalendarview.exceptions.OutOfDateRangeException;
import com.applandeo.materialcalendarview.listeners.OnDayClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.Alarm;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.ListCalendarAdapter;
import edu.bluejack19_2.chronotes.home.ui.calendar.features.AddTask;
import edu.bluejack19_2.chronotes.home.ui.calendar.features.UpdateTask;
import edu.bluejack19_2.chronotes.interfaces.TaskListener;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.TaskHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

import static androidx.core.content.ContextCompat.getSystemService;

public class CalendarFragment extends Fragment {

    private ListCalendarAdapter adapter;
    private CalendarView cv;
    private ArrayList<Task> tasks;
    ProgressBar bar;
    ArrayList<EventDay> events;
    private TaskHandler hand;
    AlarmManager manager;
    View v;
    private SimpleDateFormat dateFormat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        v=getView();
        events = new ArrayList<>();
        ProgressBar bar = v.findViewById(R.id.loading_spinner);
        hand = TaskHandler.GetInstance();
        tasks = hand.getAllTask(SessionStorage.getSessionStorage(getContext()), getContext(), val -> {
//            try {
//                ((ViewGroup)bar.getParent()).removeView(bar);
//            }catch(Exception e){
                bar.setVisibility(View.GONE);
//            }

            if (val.size() > 0) {

                tasks = val;

                Calendar c;

                for(Task ts:val){
                    if(!ts.getCompleted()){
                        go(ts);
                        c= Calendar.getInstance();
                        c.setTime(new Date(ts.getEnd()));
                        events.add(new EventDay(c, R.drawable.ic_list_calendar));
//
                    }
                }
//
                cv.setEvents(events);
//                Log.d("DEBUG", events.size()+"");

                RecyclerView rv_calendar = v.findViewById(R.id.rv_calendar);
                adapter = new ListCalendarAdapter(getContext(), val);
                rv_calendar.setAdapter(adapter);
                rv_calendar.setLayoutManager(new LinearLayoutManager(getContext()));
            }
        });

        FloatingActionButton fab = v.findViewById(R.id.fab);
        cv = v.findViewById(R.id.calendarView);

        fab.setImageResource(R.drawable.ic_add);
        fab.setBackgroundColor(getResources().getColor(R.color.White));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                sort();
                Intent i = new Intent(getActivity().getApplication(), AddTask.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(i);
//                ArrayList<EventDay> events = new ArrayList<>();
//
                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 4);
//                events.add(new EventDay(calendar, R.drawable.ic_google));
//                Calendar calendar3 = Calendar.getInstance();
//                calendar3.add(Calendar.DAY_OF_MONTH, 7);
//                events.add(new EventDay(calendar3, R.drawable.ic_calendar));
//
//                cv.setEvents(events);
            }
        });
        dateFormat = new SimpleDateFormat("DD MMMM");
        cv.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                bar.setVisibility(View.VISIBLE);
                Calendar clicked = eventDay.getCalendar();

                if(adapter == null){
                    bar.setVisibility(View.GONE);
                    return;
                }
                adapter.setTask(new ArrayList<Task>());
                adapter.notifyDataSetChanged();
                tasks = hand.GetTodayTasks(clicked.getTime(), SessionStorage.getSessionStorage(getContext()), getContext(), new TaskListener() {
                    @Override
                    public void callBack(ArrayList<Task> val) {
                        bar.setVisibility(View.GONE);
                        if (val.size() != 0) {
                            tasks = val;

                            adapter.setTask(val);
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        });
    }
    private void go(Task t){
        int Time = 10000;
        manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getContext(), Alarm.class);
        i.putExtra("Title", t.getTitle());
        i.putExtra("Desc", t.getDetail());

        PendingIntent pi = PendingIntent.getBroadcast(getContext(),0,i,0);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pi);
        if(t.getRepeat().equals("Daily")){
            manager.setRepeating(AlarmManager.RTC_WAKEUP, new Date(t.getEnd()).getTime(),AlarmManager.INTERVAL_DAY,pi);
        }
        else if(t.getRepeat().equals("On Due Date")){
            manager.set(AlarmManager.RTC_WAKEUP, new Date(t.getEnd()).getTime(),pi);
        }
    }


}
