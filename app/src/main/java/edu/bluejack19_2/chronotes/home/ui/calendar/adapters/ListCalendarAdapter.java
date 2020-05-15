package edu.bluejack19_2.chronotes.home.ui.calendar.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Paint;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.ui.calendar.ListCalendarFragment;
import edu.bluejack19_2.chronotes.home.ui.calendar.features.AddTask;
import edu.bluejack19_2.chronotes.home.ui.calendar.features.UpdateTask;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.utils.CSLHelper;
import edu.bluejack19_2.chronotes.utils.TaskHandler;

public class ListCalendarAdapter extends RecyclerView.Adapter<ListCalendarAdapter.MyViewHolder> {

   private static ArrayList<Task> tasks;
   private Context con;
   private ListCalendarAdapter lis;
   private boolean done = false;
   private TaskHandler hand;
   private int mode=2;
   public ListCalendarAdapter(Context con, ArrayList<Task> tasks){
       this.tasks = tasks;
       this.con = con;
       this.lis = this;
   }
    @NonNull
    @Override
    public ListCalendarAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(con).inflate(R.layout.list_item, parent, false);
        return new MyViewHolder(view, this.tasks);
    }

    @SuppressLint("NewApi")
    @Override
    public void onBindViewHolder(@NonNull ListCalendarAdapter.MyViewHolder holder, int position) {
        hand = TaskHandler.GetInstance();
        done = false;
        callSort();
        Task t = tasks.get(position);
        holder.complete.setTag(position);
        holder.title.setTag(t.getTaskId());
//        String str = tasks.get(position).getTitle();
//        for(String s : tasks.get(position).getTags()){
//            str += " " + s;
//        }
        holder.title.setText(tasks.get(position).getTitle());

        String[] du = tasks.get(position).getEnd().split(" ");
        holder.due.setText(du[1] + " " + du[2] + " " + du[5]);
        if(t.getPriority() == 1){
            holder.complete.setButtonTintList(CSLHelper.CheckBoxRed(con));
        }
        else if(t.getPriority() == 2){
            holder.complete.setButtonTintList(CSLHelper.CheckBoxOrange(con));
        }
        else if(t.getPriority() == 3){
            holder.complete.setButtonTintList(CSLHelper.CheckBoxYellow(con));
        }
        else if(t.getPriority() == 4){
            holder.complete.setButtonTintList(CSLHelper.CheckBoxDefault(con));
        }

        if(t.getCompleted()){
            Log.d("DEBUG",t.getTitle());
            holder.complete.setChecked(true);
            holder.title.setPaintFlags(holder.title.getPaintFlags()| Paint.STRIKE_THRU_TEXT_FLAG);
        }
        else{
            holder.complete.setChecked(false);
            holder.title.setPaintFlags(holder.title.getPaintFlags()& ~(Paint.STRIKE_THRU_TEXT_FLAG));
        }

        done = true;

        holder.title.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(con, UpdateTask.class);
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                i.putExtra("taskid", holder.title.getTag().toString());
                con.startActivity(i);
            }
        });
    }
    public void callSort(){
       if(mode == 1)tasks = hand.sortPriorityDesc(tasks);
       else if(mode == 2)tasks = hand.sortPriorityAsc(tasks);
       else if(mode == 3) tasks = hand.sortTitleDesc(tasks);
       else if(mode == 4) tasks = hand.sortTitleAsc(tasks);
    }
    public void priorDesc(){
       mode = 1;
    }
    public void priorAsc(){
       mode = 2;
    }
    public void titleDesc(){
        mode = 3;
    }
    public void titleAsc(){
        mode = 4;
    }

    public void setTask(ArrayList<Task> task){
       this.tasks = task;
    }

    @Override
    public int getItemCount() {
        return tasks.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

       public CheckBox complete;
       public TextView title, due;
       private ArrayList<Task> tas;

        public MyViewHolder(@NonNull View itemView, ArrayList<Task>tasks) {
            super(itemView);

            complete = itemView.findViewById(R.id.chk_completed);
            title = itemView.findViewById(R.id.tv_title);
            due =  itemView.findViewById(R.id.list_due);
            this.tas = tasks;
            complete.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {

                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean b) {
//                    if(!done)return;
                    int position = (int) buttonView.getTag();
                    if (buttonView.isChecked()) {
                        ListCalendarAdapter.tasks.get(position).setCompleted(true);

                    } else {
                        title.setPaintFlags(title.getPaintFlags() & (~Paint.STRIKE_THRU_TEXT_FLAG));
                        ListCalendarAdapter.tasks.get(position).setCompleted(false);
                    }
                    Task up = ListCalendarAdapter.tasks.get(position);
                    hand.updateTask(up, itemView.getContext());
                    ListCalendarFragment.update();
                    notifyDataSetChanged();
                }
            });
        }
    }
}
