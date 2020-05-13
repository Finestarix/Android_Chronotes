package edu.bluejack19_2.chronotes.home.ui.notes;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.ui.notes.adapter.CollaboratorNotesAdapter;
import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public class NoteCollaborator extends DialogFragment {

    private RecyclerView recyclerView;
    private CollaboratorNotesAdapter collaboratorNotesAdapter;

    private Note note;

    private UserController userController;
    private NoteController noteController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_notes_collaborator, container);

        collaboratorNotesAdapter = new CollaboratorNotesAdapter(this.getActivity());
        collaboratorNotesAdapter.setCollaborators(null);
        collaboratorNotesAdapter.notifyDataSetChanged();

        noteController = NoteController.getInstance();
        userController = UserController.getInstance();

        recyclerView = view.findViewById(R.id.rv_notes_reminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));
        recyclerView.setAdapter(collaboratorNotesAdapter);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        String noteID = requireArguments().getString("note");

        noteController.getNotesByID((notes, processStatusNote) -> {
            if (processStatusNote == ProcessStatus.FOUND) {
                CollaboratorNotesAdapter.setNote(notes);
                ArrayList<String> collaborators = notes.getUsers();
                ArrayList<User> usersCollaborators = new ArrayList<>();

                for (String c : Objects.requireNonNull(collaborators)) {
                    userController.getUserByID((user, processStatus) -> {
                        if (processStatus == ProcessStatus.FOUND){
                            usersCollaborators.add(user);
                            collaboratorNotesAdapter.setCollaborators(usersCollaborators);
                            collaboratorNotesAdapter.notifyDataSetChanged();
                        }
                    }, c);
                }
            }
        }, noteID);
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
            Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }
}
