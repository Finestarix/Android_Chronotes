package edu.bluejack19_2.chronotes.home.ui.calendar;

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
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.ListCalendarAdapter;
import edu.bluejack19_2.chronotes.home.ui.calendar.features.AddTask;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.TaskHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class CalendarFragment extends Fragment {

    private ListCalendarAdapter adapter;
    private CalendarView cv;
    private ArrayList<Task> tasks;
    private TaskHandler hand;
    private SimpleDateFormat dateFormat;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
        TextView title = v.findViewById(R.id.tv_calendar_message);
        hand = TaskHandler.GetInstance();
        tasks = hand.getAllTask(SessionStorage.getSessionStorage(getContext()), getContext(), val -> {
            if (val.size() == 0) {
                title.setText("No Tasks!");
            } else {
                title.setText("");
                tasks = val;

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
//                Calendar calendar = Calendar.getInstance();
//                calendar.add(Calendar.DAY_OF_MONTH, 4);
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
                tasks = hand.GetTodayTasks(clicked.getTime(), SessionStorage.getSessionStorage(getContext()), getContext(), val -> {
                    if (val.size() == 0) {
                        title.setText("No Tasks!");
                    } else {
                        title.setText("");
                        tasks = val;

                        adapter.setTask(val);
                        adapter.notifyDataSetChanged();
                    }
                });

//                int year = curr.get(Calendar.YEAR);
//                Calendar target = Calendar.getInstance();
//                target.setTime(new Date(ts.getEnd()));
                int targetWeek = clicked.get(Calendar.DAY_OF_YEAR);
                int targetYear = clicked.get(Calendar.YEAR);
//                int targetYear = target.get(Calendar.YEAR);
//
//                if(week == targetWeek && year == targetYear){
//                    filtered.add(ts);
//                }
                try {
                    cv.setDate(clicked);
                    Log.d("DEBUG", clicked.getTime().toString());
                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }


            }
        });
    }


}
