package edu.bluejack19_2.chronotes.utils;

import edu.bluejack19_2.chronotes.model.User;

public interface FirebaseCallbackUser {
    void onCallback(User user, ProcessStatus processStatus);
}
