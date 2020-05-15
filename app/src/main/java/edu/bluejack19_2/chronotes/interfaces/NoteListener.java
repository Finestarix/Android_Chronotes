package edu.bluejack19_2.chronotes.interfaces;

import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface NoteListener {
    void onCallback(Note notes, ProcessStatus processStatus);
}
