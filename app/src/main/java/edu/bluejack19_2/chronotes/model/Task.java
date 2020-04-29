package edu.bluejack19_2.chronotes.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class Task {
    private String TaskId;
    private ArrayList<String> UserId;
    private String Due;
    private String Title;
    private String Detail;
    private String Repeat;
    private ArrayList<String> Tags;
    private Integer Priority;
    private String Group;
    private ArrayList<Integer> Reminders; //in minutes
    private Boolean Completed;

    public Task() {

    }

    public Task(ArrayList<String> userId, String due, String title, String detail, String repeat, ArrayList<String> tags, Integer priority, String group, ArrayList<Integer> reminders) {
        TaskId = UUID.randomUUID().toString();
        UserId = userId;
        Due = due;
        Title = title;
        Detail = detail;
        Repeat = repeat;
        Tags = tags;
        Priority = priority;
        Group = group;
        Reminders = reminders;
        Completed = false;
    }

    public Boolean getCompleted() {
        return Completed;
    }

    public void setCompleted(Boolean completed) {
        Completed = completed;
    }

    public static Comparator<Task> PriorityDescending = new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            Log.d("DEBUG", "COMPARING");
            int completedComp = Boolean.compare(a.getCompleted(), b.getCompleted());
//            int priorityComp = a.getPriority().compareTo(b.getPriority());
//
//            if(completedComp == 0){
//                return((priorityComp == 0) ? completedComp:priorityComp);
//            }
            return completedComp;
        }
    };

    public void setUserId(ArrayList<String> userId) {
        UserId = userId;
    }

    public void setDue(String due) {
        Due = due;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public void setDetail(String detail) {
        Detail = detail;
    }

    public void setRepeat(String repeat) {
        Repeat = repeat;
    }

    public void setTags(ArrayList<String> tags) {
        Tags = tags;
    }

    public void setPriority(Integer priority) {
        Priority = priority;
    }

    public void setGroup(String group) {
        Group = group;
    }

    public void setReminders(ArrayList<Integer> reminders) {
        Reminders = reminders;
    }

    public String getTaskId() {
        return TaskId;
    }

    public ArrayList<String> getUserId() {
        return UserId;
    }

    public String getDue() {
        return Due;
    }

    public String getTitle() {
        return Title;
    }

    public String getDetail() {
        return Detail;
    }

    public String getRepeat() {
        return Repeat;
    }

    public ArrayList<String> getTags() {
        return Tags;
    }

    public Integer getPriority() {
        return Priority;
    }

    public String getGroup() {
        return Group;
    }

    public ArrayList<Integer> getReminders() {
        return Reminders;
    }
}
