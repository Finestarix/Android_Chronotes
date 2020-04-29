package edu.bluejack19_2.chronotes.home.ui.calendar;

import android.os.Bundle;
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

import java.util.ArrayList;
import java.util.Calendar;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.ListCalendarAdapter;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.TaskHandler;

public class CalendarFragment extends Fragment {

    private ListCalendarAdapter adapter;
    private CalendarView cv;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_calendar, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        View v = getView();
//        ArrayList<String> users = new ArrayList<>();
//        ArrayList<String> tags = new ArrayList<>();
//        ArrayList<Integer> rem = new ArrayList<>();
//
//        tags.add("Ko Irvan");
//        tags.add("Ko Yohan");
//        tags.add("TPA");
//        tags.add("VidCon");
//        users.add("users_249fd6b3-e95a-4b52-a24d-23f752c85206");
//        users.add("users_0d3b5443-bb62-4c4d-9471-15090c5d359e");
//        rem.add(5);
//        rem.add(1);
//        rem.add(10);
//        Task t = new Task(users,"100000000","Cobain2","cobacobacoba\ncobacobacoba","Weekly",tags,1,"DB",rem);

        TextView title = v.findViewById(R.id.tv_calendar_message);
        TaskHandler hand = TaskHandler.GetInstance();

        ArrayList<Task> tasks = hand.getAllTask("users_0d3b5443-bb62-4c4d-9471-15090c5d359e", getContext(), val -> {
            if (val.size() == 0) {
                title.setText("No Tasks!");
            } else {
                title.setText("");
                Task t = val.get(val.size() - 1);

                RecyclerView rv_calendar = v.findViewById(R.id.rv_calendar);
                adapter = new ListCalendarAdapter(getContext(), val);
                rv_calendar.setAdapter(adapter);
                rv_calendar.setLayoutManager(new LinearLayoutManager(getContext()));
//                rv_calendar.post(new Runnable()
//                {
//                    @Override
//                    public void run() {
//                        Log.d("DEBUG","RUNNING");
//                        adapter.notifyDataSetChanged();
//                    }
//                });

            }
        });

//        hand.insertTask(t, getContext());

        FloatingActionButton fab = v.findViewById(R.id.fab);
        cv = v.findViewById(R.id.calendarView);

        fab.setImageResource(R.drawable.ic_add);
        fab.setBackgroundColor(getResources().getColor(R.color.White));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<EventDay> events = new ArrayList<>();

                Calendar calendar = Calendar.getInstance();
                calendar.add(Calendar.DAY_OF_MONTH, 4);
                events.add(new EventDay(calendar, R.drawable.ic_google));
                Calendar calendar3 = Calendar.getInstance();
                calendar3.add(Calendar.DAY_OF_MONTH, 7);
                events.add(new EventDay(calendar3, R.drawable.ic_calendar));

                cv.setEvents(events);
            }
        });

        cv.setOnDayClickListener(new OnDayClickListener() {
            @Override
            public void onDayClick(EventDay eventDay) {
                Calendar clicked = eventDay.getCalendar();

                try {
                    cv.setDate(clicked);
                } catch (OutOfDateRangeException e) {
                    e.printStackTrace();
                }

            }
        });
    }

}
