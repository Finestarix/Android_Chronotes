package edu.bluejack19_2.chronotes.home.ui.notes;

import android.app.Dialog;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
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

    private static final String FRAGMENT_TAG = "Note Collaborator";
    private static final String INTENT_DATA = "note";

    private RecyclerView recyclerView;
    private CollaboratorNotesAdapter collaboratorNotesAdapter;

    private UserController userController;
    private NoteController noteController;

    private Button closeButton;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view =  inflater.inflate(R.layout.fragment_notes_collaborator, container);
        setUIComponent(view);

        closeButton.setOnClickListener(v -> closeDialog());

        collaboratorNotesAdapter = new CollaboratorNotesAdapter(this.getActivity());
        collaboratorNotesAdapter.setCollaborators(null);
        collaboratorNotesAdapter.notifyDataSetChanged();

        recyclerView.setAdapter(collaboratorNotesAdapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getActivity()));

        noteController = NoteController.getInstance();
        userController = UserController.getInstance();
        loadData();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
            Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void setUIComponent(View view) {
        recyclerView = view.findViewById(R.id.rv_notes_reminders);
        closeButton = view.findViewById(R.id.button_notes_close);
    }

    private void loadData() {
        String noteID = requireArguments().getString(INTENT_DATA);

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

                        } else {
                            String message = getResources().getString(R.string.notes_message_collaborator_failed);
                            Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
                        }

                    }, c);
                }
            }
        }, noteID);
    }

    private void closeDialog() {
        Fragment fragment = requireFragmentManager().findFragmentByTag(FRAGMENT_TAG);
        if (fragment != null) {
            DialogFragment df = (DialogFragment) fragment;
            df.dismiss();
        }
    }

}
