package edu.bluejack19_2.chronotes.utils.firebase_callback;

import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface FirebaseCallbackBytes {
    void onCallback(byte[] bytes, ProcessStatus processStatus);
}
