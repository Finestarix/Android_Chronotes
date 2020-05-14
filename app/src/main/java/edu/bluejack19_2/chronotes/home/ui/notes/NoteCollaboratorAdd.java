package edu.bluejack19_2.chronotes.home.ui.notes;

import android.app.Dialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import com.google.gson.Gson;

import java.util.Objects;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.ui.notes.adapter.CollaboratorNotesAdapter;
import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;

public class NoteCollaboratorAdd extends DialogFragment {

    private EditText emailEditText;
    private Button addButton;

    private NoteController noteController;
    private UserController userController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_notes_collaborator_add, container);

        String noteID = requireArguments().getString("note");

        noteController = NoteController.getInstance();
        userController = UserController.getInstance();

        emailEditText = view.findViewById(R.id.notes_email);
        addButton = view.findViewById(R.id.notes_add_collaborator);

        addButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString();
            userController.getUserByEmail((user, processStatus) -> {
                if (processStatus == ProcessStatus.FOUND) {
                    noteController.addCollaborator(processStatusNote -> {
                        if (processStatusNote == ProcessStatus.SUCCESS) {
                            Toast.makeText(view.getContext(), "Collaborator Added", Toast.LENGTH_SHORT).show();
                            closeDialog();
                        } else {
                            Toast.makeText(view.getContext(), "Failed to Add Collaborator", Toast.LENGTH_SHORT).show();
                        }
                    }, noteID, user.getId());
                } else {
                    Toast.makeText(view.getContext(), "Email Not Found", Toast.LENGTH_SHORT).show();
                }
            },  email);
        });

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Dialog dialog = getDialog();
        if (dialog != null)
            Objects.requireNonNull(Objects.requireNonNull(dialog).getWindow()).setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
    }

    private void closeDialog() {
        Fragment prev = requireFragmentManager().findFragmentByTag("Note Collaborator Add");
        if (prev != null) {
            DialogFragment df = (DialogFragment) prev;
            df.dismiss();
        }
    }
}
