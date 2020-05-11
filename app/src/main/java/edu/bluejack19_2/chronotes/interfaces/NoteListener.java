package edu.bluejack19_2.chronotes.interfaces;

import java.util.ArrayList;

import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface NoteListener {
    void onCallback(ArrayList<Note> notes, ProcessStatus processStatus);
}
