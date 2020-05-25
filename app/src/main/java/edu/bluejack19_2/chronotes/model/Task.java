package edu.bluejack19_2.chronotes.model;

import android.util.Log;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.UUID;

public class Task {
    private String TaskId;
    private ArrayList<String> UserId;
    private String Start;
    private String End;
    private String Title;
    private String Detail;
    private String Repeat;
    private ArrayList<String> Tags;
    private Integer Priority;
    private Boolean Completed;
    private String CreatedBy;

    public Task() {

    }

    public Task(String TaskId, ArrayList<String> userId, String start, String end, String title, String detail, String repeat, ArrayList<String> tags, Integer priority, Boolean completed) {
        UserId = userId;
        this.TaskId = TaskId;
        Start = start;
        End = end;
        Title = title;
        Detail = detail;
        Repeat = repeat;
        Tags = tags;
        Priority = priority;
        Completed = completed;
        CreatedBy = userId.get(0);
    }

    public void setCreatedBy(String createdBy) {
        CreatedBy = createdBy;
    }

    public String getCreatedBy() {
        return CreatedBy;
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
            int priorityComp = a.getPriority().compareTo(b.getPriority());
//
            if(completedComp == 0){
                return((priorityComp == 0) ? completedComp:priorityComp);
            }
            return completedComp;
        }
    };
    public static Comparator<Task> TitleDescending = new Comparator<Task>() {
        @Override
        public int compare(Task a, Task b) {
            Log.d("DEBUG", "COMPARING");
            int completedComp = Boolean.compare(a.getCompleted(), b.getCompleted());
            int titleComp = a.getTitle().compareTo(b.getTitle());
//
            if(completedComp == 0){
                return((titleComp == 0) ? completedComp:titleComp);
            }
            return completedComp;
        }
    };

    public void setTaskId(String taskId) {
        TaskId = taskId;
    }

    public void setUserId(ArrayList<String> userId) {
        UserId = userId;
    }

    public void addUserId(String id){
        UserId.add(id);
    }
    public void removeUserId(String id){
        UserId.remove(id);
    }
    public void setStart(String start) {
        Start = start;
    }

    public void setEnd(String end) {
        End = end;
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

    public String getTaskId() {
        return TaskId;
    }

    public ArrayList<String> getUserId() {
        return UserId;
    }

    public String getStart() {
        return Start;
    }

    public String getEnd() {
        return End;
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
}
