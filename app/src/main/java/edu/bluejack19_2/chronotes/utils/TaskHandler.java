package edu.bluejack19_2.chronotes.utils;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;

import edu.bluejack19_2.chronotes.interfaces.TaskListener;
import edu.bluejack19_2.chronotes.model.Task;

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
            .addOnSuccessListener(aVoid -> Toast.makeText(con, "Task Successfully Added", Toast.LENGTH_SHORT).show()
            ).addOnFailureListener(e -> Toast.makeText(con, "Task Failed To Add! Please Try Again!", Toast.LENGTH_LONG).show());
    }
    public ArrayList<Task> getAllTask(String UserId, Context con, TaskListener listener){
        ArrayList<Task> tasks = new ArrayList<>();
        ref.whereArrayContains("tags","VidCon").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
    public ArrayList<Task> sortPriorityDesc(ArrayList<Task> tasks){
        Collections.sort(tasks, Task.PriorityDescending);
        Log.d("DEBUG","COMPARING");
        return tasks;
    }






}
