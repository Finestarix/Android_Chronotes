package edu.bluejack19_2.chronotes.home.ui.calendar;

import android.R.layout;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.Spinner;

import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Vector;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.ui.calendar.adapters.ListCalendarAdapter;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.TaskHandler;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class ListCalendarFragment extends Fragment {
    private ListCalendarAdapter adapter;
    private View v;
    private Spinner filter, sort;
    private ChipGroup cg;
    private TaskHandler hand;
    private ArrayList<Task> tasks;
    private Vector<String> tags, filterTags;
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);
        v = getView();
        setVariables();
        filterTags = new Vector<>();
        tags = new Vector<>();
        hand = TaskHandler.GetInstance();
//        hand.insertTask(to, getContext());
        tasks = hand.getAllTask(SessionStorage.getSessionStorage(getContext()), getContext(), val -> {
            if (val.size() == 0) {
//                title.setText("No Tasks!");
            } else {
//                title.setText("");
//                Task t = val.get(val.size() - 1);
                tasks = val;
                RecyclerView rv_calendar = v.findViewById(R.id.rv_calendar);
                adapter = new ListCalendarAdapter(getContext(), val);
                rv_calendar.setAdapter(adapter);
                rv_calendar.setLayoutManager(new LinearLayoutManager(getContext()));

                ArrayList<String> tampung;
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
                    c.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                        @Override
                        public void onCheckedChanged(CompoundButton but, boolean b) {
                            if(adapter == null)return;

                            if(but.isChecked()){
                                filterTags.add(s);
                            }
                            else{
                                if(filterTags.contains(s)){
                                    filterTags.remove(s);
                                }
                            }
                            adapter.setTask(hand.FilterTags(tasks, filterTags));
                            adapter.notifyDataSetChanged();
                            Log.d("DEBUG", "JUMLAH TAG: "+filterTags.size());
//                            filterTags
                        }
                    });
                    cg.addView(c);
                }
            }
        });


    }
    public void setVariables(){
        cg = v.findViewById(R.id.cg_tag);
        filter = v.findViewById(R.id.spinner_filter);
        sort = v.findViewById(R.id.spinner_sort);


        setSpinner();
    }
    public void setSpinner(){
        String[] r = getResources().getStringArray(R.array.filter_array);
        ArrayAdapter<CharSequence> adapt = ArrayAdapter.createFromResource(getContext(), R.array.filter_array, layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        filter.setAdapter(adapt);

        String[] sor = getResources().getStringArray(R.array.sort_array);
        adapt = ArrayAdapter.createFromResource(getContext(), R.array.sort_array, layout.simple_spinner_item);
        adapt.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        sort.setAdapter(adapt);

        filter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                String s = parent.getItemAtPosition(i).toString();
                if(adapter == null)return;
                if(s.equals(r[0])){
                    adapter.setTask(tasks);
                    adapter.notifyDataSetChanged();
                }
                else if (s.equals(r[1])){

                    adapter.setTask(hand.FilterToday(tasks));
                    adapter.notifyDataSetChanged();
                }
                else if (s.equals(r[2])){
                    adapter.setTask(hand.FilterThisWeek(tasks));
                    adapter.notifyDataSetChanged();
                }
                else if (s.equals(r[3])){
                    adapter.setTask(hand.FilterThisMonth(tasks));
                    adapter.notifyDataSetChanged();
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int i, long l) {
                String s = parent.getItemAtPosition(i).toString();
                if(adapter == null)return;
                if(s.equals(sor[0])){
                    adapter.priorAsc();
                }
                else if (s.equals(sor[1])){

                    adapter.priorDesc();
                }
                else if (s.equals(sor[2])){

                    adapter.titleDesc();
                }
                else if (s.equals(sor[3])){
                    adapter.titleAsc();
                }
                adapter.notifyDataSetChanged();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_list_calendar, container, false);
    }

}
