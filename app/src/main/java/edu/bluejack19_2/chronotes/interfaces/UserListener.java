package edu.bluejack19_2.chronotes.interfaces;

import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface UserListener {
    void onCallback(User user, ProcessStatus processStatus);
}
