package edu.bluejack19_2.chronotes.utils.firebase_callback;

import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface FirebaseCallbackUser {
    void onCallback(User user, ProcessStatus processStatus);
}
