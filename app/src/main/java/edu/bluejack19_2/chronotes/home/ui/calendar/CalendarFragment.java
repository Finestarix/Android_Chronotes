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
        TextView title = v.findViewById(R.id.tv_calendar_message);
        hand = TaskHandler.GetInstance();
        tasks = hand.getAllTask(SessionStorage.getSessionStorage(getContext()), getContext(), val -> {
            if (val.size() == 0) {
//                title.setText("No Tasks!");
            } else {
                title.setText("");
                tasks = val;

                ArrayList<Task> icons = new ArrayList<>();
                Calendar c = Calendar.getInstance();
                Calendar d = Calendar.getInstance();
                Calendar min = Calendar.getInstance();
                Calendar max = Calendar.getInstance();

                for(Task ts:val){
                    if(!ts.getCompleted()){
                        c.setTime(new Date(ts.getStart()));
                        int day1 = c.get(Calendar.DAY_OF_YEAR);
                        int year1 = c.get(Calendar.YEAR);
                        if((day1 < min.get(Calendar.DAY_OF_YEAR) && year1 == min.get(Calendar.YEAR))|| year1 < min.get(Calendar.YEAR)){
                            min.setTime(c.getTime());
                        }

                        d.setTime(new Date(ts.getEnd()));
                        int day2 = d.get(Calendar.DAY_OF_YEAR);
                        int year2 = d.get(Calendar.YEAR);
//                        Log.d("DEBUG", day2 + " " + year2 + " " + max.get(Calendar.YEAR) + max.get(Calendar.DAY_OF_YEAR));
                        if((day2 > max.get(Calendar.DAY_OF_YEAR) && year2 == max.get(Calendar.YEAR)) || year2 > max.get(Calendar.YEAR)){
//                            Log.d("DEBUG","???");
                            max.setTime(d.getTime());
                        }
                    }
//                    Log.d("DEBUG", "MINI "+new Date(min.getTime().toString())+" "+new Date(max.getTime().toString()));
                }
//                Log.d("DEBUG", min+" "+max);
//                c.setTime(min);
//                d.setTime(max);
                Log.d("DEBUG", "MINI "+new Date(min.getTime().toString())+" "+new Date(max.getTime().toString()));
                ArrayList<Task> today = new ArrayList<>();
                while((min.get(Calendar.DAY_OF_YEAR) <= max.get(Calendar.DAY_OF_YEAR) && min.get(Calendar.YEAR) == max.get(Calendar.YEAR)) ||min.get(Calendar.YEAR) < max.get(Calendar.YEAR) ){
//                    Log.d("DEBUG", "ADDING EVENTS "+min.getTime());
//                    today = hand.GetTodayTasks(c.getTime(), SessionStorage.getSessionStorage(getContext()), getContext(), new TaskListener() {
//                        @Override
//                        public void callBack(ArrayList<Task> val) {
                            Log.d("DEBUG", "ADDING fbase EVENTS "+min.getTime());
//                            if(val!= null){
                                Calendar v = Calendar.getInstance();
                                v.setTime(min.getTime());
                                events.add(new EventDay(v, R.drawable.ic_list_calendar));
//                            }
//                        }
//                    }
//                );

                    min.add(Calendar.DATE,1);
                }
                cv.setEvents(events);



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
                Calendar clicked = eventDay.getCalendar();
                Log.d("DEBUG", "RUNNING ON DAY");
                Calendar curr = Calendar.getInstance();
                int week = curr.get(Calendar.DAY_OF_YEAR);
                int year = curr.get(Calendar.YEAR);

                tasks = hand.GetTodayTasks(clicked.getTime(), SessionStorage.getSessionStorage(getContext()), getContext(), new TaskListener() {
                    @Override
                    public void callBack(ArrayList<Task> val) {
                        if (val.size() == 0) {
                            adapter.setTask(new ArrayList<Task>());
                            adapter.notifyDataSetChanged();
                        } else {
                            title.setText("");
                            tasks = val;

                            adapter.setTask(val);
                            adapter.notifyDataSetChanged();

                        }
                    }
                });
            }
        });
    }
    private void go(){
        int Time = 10000;
        manager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(getContext(), Alarm.class);
        PendingIntent pi = PendingIntent.getBroadcast(getContext(),0,i,0);

//        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.CUPCAKE)
//        manager.setInexactRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(),AlarmManager.INTERVAL_FIFTEEN_MINUTES,pi);
        manager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis()+1000,10000,pi);


    }


}
