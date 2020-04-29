package edu.bluejack19_2.chronotes.interfaces;

import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public interface BytesListener {
    void onCallback(byte[] bytes, ProcessStatus processStatus);
}
