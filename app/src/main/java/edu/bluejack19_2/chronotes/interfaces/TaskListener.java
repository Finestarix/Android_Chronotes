package edu.bluejack19_2.chronotes.interfaces;

import java.lang.reflect.Array;
import java.util.ArrayList;

import edu.bluejack19_2.chronotes.model.Task;

public interface TaskListener {
    void callBack(ArrayList<Task> val);
}
