package edu.bluejack19_2.chronotes.utils;

import androidx.annotation.NonNull;

public enum ProcessStatus {
    INIT {
        @NonNull
        @Override
        public String toString() {
            return "Init";
        }
    },

    FOUND {
        @NonNull
        @Override
        public String toString() {
            return "Found";
        }
    },
    NOT_FOUND {
        @NonNull
        @Override
        public String toString() {
            return "Not Found";
        }
    },
    INVALID {
        @NonNull
        @Override
        public String toString() {
            return "Invalid";
        }
    },

    FAILED {
        @NonNull
        @Override
        public String toString() {
            return "Failed";
        }
    },
    SUCCESS {
        @NonNull
        @Override
        public String toString() {
            return "Success";
        }
    }
}
