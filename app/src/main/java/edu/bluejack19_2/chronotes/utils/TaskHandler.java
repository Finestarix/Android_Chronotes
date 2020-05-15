package edu.bluejack19_2.chronotes.utils;

import android.content.Context;
import android.text.format.DateUtils;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.w3c.dom.Document;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Set;
import java.util.Vector;

import edu.bluejack19_2.chronotes.interfaces.StringCallback;
import edu.bluejack19_2.chronotes.interfaces.TaskListener;
import edu.bluejack19_2.chronotes.model.Task;
import edu.bluejack19_2.chronotes.model.User;

public class TaskHandler {
    private final String collectionName = "Tasks";

    private static TaskHandler h = null;
    private FirebaseFirestore db;
    private CollectionReference ref;
    private DocumentReference doc;

    private TaskHandler(){
        db = FirebaseFirestore.getInstance();
        ref = db.collection(collectionName);

    }
    public static TaskHandler GetInstance(){
        if(h == null){
            synchronized (TaskHandler.class){
                if(h == null){
                    h = new TaskHandler();
                }
            }
        }
        return h;
    }
    public void insertTask(Task t, Context con){
        ref.document(collectionName+"_"+ t.getTaskId()).set(t)
            .addOnSuccessListener(aVoid -> Toast.makeText(con, "Task " + t.getTitle()+" Successfully Added", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e -> Toast.makeText(con, "Task Failed To Add! Please Try Again!", Toast.LENGTH_LONG).show());
    }
    public ArrayList<Task> GetTodayTasks(Date date, String UserId, Context con, TaskListener listener){
        ArrayList<Task> tasks = getAllTask(UserId, con, val -> {
            ArrayList <Task> filtered = new ArrayList<>();

            Calendar c = Calendar.getInstance();
            c.setTime(date);
            int day = c.get(Calendar.DAY_OF_YEAR);
            int year = c.get(Calendar.YEAR);

            Calendar end = Calendar.getInstance();
            Calendar start = Calendar.getInstance();
            for(Task t:val){
                end.setTime(new Date(t.getEnd()));
                start.setTime(new Date(t.getStart()));

                if(year == start.get(Calendar.YEAR)){
                    if(year < end.get(Calendar.YEAR)){

                        filtered.add(t);
                    }
                    else if(day >= start.get(Calendar.DAY_OF_YEAR) && day <= end.get(Calendar.DAY_OF_YEAR)){

                        filtered.add(t);
                    }
                }
            }
            listener.callBack(filtered);
//                return filtered;
        });
        return null;
    }
    public ArrayList<String> getAllCollaboratorEmail(Task t, Context con, StringCallback cb){
        CollectionReference useref = db.collection("users");
        Log.d("DEBUG", "HERE1");
        ArrayList<String> ids = t.getUserId();
        ArrayList<String> emails = new ArrayList<>();

        useref.whereIn("id", ids).get()
                .addOnCompleteListener(task -> {
//                    Log.d("DEBUG", "HERE");
                    ids.clear();
                    if(task.isSuccessful()){
                        for(QueryDocumentSnapshot qd : task.getResult()){
                            String email = qd.getString("email");
                            String id = qd.getString("id");

                            ids.add(id);
                            emails.add(email);
                        }
                        cb.callBack(ids,emails);
                    }
                })
                .addOnFailureListener(e -> Toast.makeText(con, "Collaborators Failed To Load! Please Try Again!", Toast.LENGTH_LONG).show());;


        return null;
    }

    public ArrayList<Task> getAllTask(String UserId, Context con, TaskListener listener){
        ArrayList<Task> tasks = new ArrayList<>();

        ref.whereArrayContains("userId",UserId).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull com.google.android.gms.tasks.Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot doc: task.getResult()){
                        Task t = doc.toObject(Task.class);
                        tasks.add(t);
                    }
                    listener.callBack(tasks);
                }
            }
        }).addOnFailureListener(e -> Toast.makeText(con, "Task Failed To Load! Please Try Again!", Toast.LENGTH_LONG).show());
        return tasks;
    }
    public Task getTask(String TaskId, Context con, TaskListener listener){
        final Task[] t = new Task[1];
        DocumentReference refer = ref.document("Tasks_"+TaskId);
        ArrayList<Task> tasks = new ArrayList<>();
        refer.get().addOnSuccessListener(shot -> {

            if(shot.exists()){
                tasks.add(shot.toObject(Task.class));
                t[0] = shot.toObject(Task.class);
                listener.callBack(tasks);
            }
            else{
                Log.d("DEBUG","NO DATA");
            }
        }).addOnFailureListener(e -> Toast.makeText(con, "Task Failed To Load! Please Try Again!", Toast.LENGTH_LONG).show());

        return t[0];
    }

    public void updateTask(Task t, Context con){
        ref.document(collectionName+"_"+ t.getTaskId()).set(t)
                .addOnSuccessListener(aVoid -> Toast.makeText(con, "Task " + t.getTitle()+" Successfully Successfully Updated", Toast.LENGTH_SHORT).show()
                ).addOnFailureListener(e -> Toast.makeText(con, "Task Failed to update! Please Try Again!", Toast.LENGTH_LONG).show());
    }


    public ArrayList<Task> sortPriorityAsc(ArrayList<Task> tasks){
        Collections.sort(tasks, Collections.reverseOrder(Task.PriorityDescending));
        int index =0;
        for(Task ts : tasks){
            if(!ts.getCompleted()){
                break;
            }
            index++;
        }
        for(int z=0;z<index;z++){
            Task ts = tasks.get(0);
            tasks.remove(0);
            tasks.add(ts);
        }
        return tasks;
    }
    public ArrayList<Task> sortPriorityDesc(ArrayList<Task> tasks){
        Collections.sort(tasks,Task.PriorityDescending);
        return tasks;
    }
    public ArrayList<Task> sortTitleDesc(ArrayList<Task> tasks){
        Collections.sort(tasks,Task.TitleDescending);
        return tasks;
    }

    public ArrayList<Task> sortTitleAsc(ArrayList<Task> tasks){
        Collections.sort(tasks,Collections.reverseOrder(Task.TitleDescending));
        int index = 0;
        for(Task ts : tasks){
            if(!ts.getCompleted()){
                break;
            }
            index++;
        }
        for(int z=0;z<index;z++){
            Task ts = tasks.get(0);
            tasks.remove(0);
            tasks.add(ts);
        }
        return tasks;
    }
    public ArrayList<Task> FilterToday(ArrayList<Task> tasks){
        Calendar d = Calendar.getInstance();
        ArrayList<Task>filtered = new ArrayList<>();
        for(Task ts : tasks){
           Boolean b = DateUtils.isToday(new Date(ts.getEnd()).getTime());
           if(b){
               filtered.add(ts);
           }
        }
        return filtered;
    }
    public ArrayList<Task> FilterThisWeek(ArrayList<Task> tasks){
        Calendar d = Calendar.getInstance();
        ArrayList<Task>filtered = new ArrayList<>();
        for(Task ts : tasks){
            Calendar curr = Calendar.getInstance();
            int week = curr.get(Calendar.WEEK_OF_YEAR);
            int year = curr.get(Calendar.YEAR);
            Calendar target = Calendar.getInstance();
            target.setTime(new Date(ts.getEnd()));
            int targetWeek = target.get(Calendar.WEEK_OF_YEAR);
            int targetYear = target.get(Calendar.YEAR);

            if(week == targetWeek && year == targetYear){
                filtered.add(ts);
            }
        }
        return filtered;
    }
    public ArrayList<Task> FilterThisMonth(ArrayList<Task> tasks){
        Calendar d = Calendar.getInstance();
        ArrayList<Task>filtered = new ArrayList<>();
        for(Task ts : tasks){
            Calendar curr = Calendar.getInstance();
            int week = curr.get(Calendar.MONTH);
            int year = curr.get(Calendar.YEAR);
            Calendar target = Calendar.getInstance();
            target.setTime(new Date(ts.getEnd()));
            int targetWeek = target.get(Calendar.MONTH);
            int targetYear = target.get(Calendar.YEAR);

            if(week == targetWeek && year == targetYear){
                filtered.add(ts);
            }
        }
        return filtered;
    }
    public ArrayList<Task> FilterTags(ArrayList<Task> tasks, Vector<String> tags){
        if(tags.size() == 0)return tasks;
        ArrayList<Task>filtered = new ArrayList<>();
        for(Task ts : tasks) {
                if(ts.getTags().containsAll(tags)){
                    filtered.add(ts);
                }
//            }
        }
        return filtered;
    }


}
