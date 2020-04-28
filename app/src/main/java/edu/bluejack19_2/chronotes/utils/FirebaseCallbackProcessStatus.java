package edu.bluejack19_2.chronotes.utils;

import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface FirebaseCallbackProcessStatus {
    void onCallback(ProcessStatus processStatus);
}
