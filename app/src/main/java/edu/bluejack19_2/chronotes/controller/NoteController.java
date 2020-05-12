package edu.bluejack19_2.chronotes.controller;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Objects;

import edu.bluejack19_2.chronotes.interfaces.NoteListener;
import edu.bluejack19_2.chronotes.interfaces.ProcessStatusListener;
import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public class NoteController {

    private static NoteController noteController;

    private CollectionReference collectionReference;

    private ProcessStatus currentStatus;

    private NoteController() {
        FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
        collectionReference = firebaseFirestore.collection(Note.COLLECTION_NAME);
    }

    public static NoteController getInstance() {
        if (noteController == null) {
            noteController = new NoteController();
        }
        return noteController;
    }

    public void getNotesByID(NoteListener noteListener, String id) {

        ArrayList<Note> notes = new ArrayList<>();

        collectionReference.
                whereArrayContains("users", id).
                get().
                addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        if (Objects.requireNonNull(task.getResult()).isEmpty())
                            noteListener.onCallback(null, ProcessStatus.NOT_FOUND);
                        else {

                            for (QueryDocumentSnapshot queryDocumentSnapshot : task.getResult()) {
                                Note note = queryDocumentSnapshot.toObject(Note.class);
                                notes.add(note);
                            }

                            noteListener.onCallback(notes, ProcessStatus.FOUND);
                        }
                    } else
                        noteListener.onCallback(null, ProcessStatus.FAILED);
                });
    }

    public void insertNewNote(ProcessStatusListener processStatusListener, Note note) {
        currentStatus = ProcessStatus.INIT;

        collectionReference.
                document(Note.DOCUMENT_NAME + note.getId()).
                set(note).
                addOnCompleteListener(task -> {
                    currentStatus = (task.isComplete()) ?
                            ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                    processStatusListener.onCallback(currentStatus);
                });
    }

    public void deleteNewNote(ProcessStatusListener processStatusListener, Note note) {
        currentStatus = ProcessStatus.INIT;

        collectionReference.
                document(Note.DOCUMENT_NAME + note.getId()).
                delete().
                addOnCompleteListener(task -> {
                    currentStatus = (task.isComplete()) ?
                            ProcessStatus.SUCCESS : ProcessStatus.FAILED;
                    processStatusListener.onCallback(currentStatus);
                });
    }

}
