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

    private static final String INTENT_DATA = "note";

    private EditText emailEditText;
    private Button addButton;

    private NoteController noteController;
    private UserController userController;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes_collaborator_add, container);
        setUIComponent(view);

        noteController = NoteController.getInstance();
        userController = UserController.getInstance();

        addButton.setOnClickListener(v -> insertNewCollaborator());

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
        emailEditText = view.findViewById(R.id.notes_email);
        addButton = view.findViewById(R.id.notes_add_collaborator);
    }

    private void insertNewCollaborator() {

        addButton.setBackgroundColor(getResources().getColor(R.color.Gray));

        String noteID = requireArguments().getString(INTENT_DATA);
        String email = emailEditText.getText().toString();

        userController.getUserByEmail((user, processStatus) -> {

            if (processStatus == ProcessStatus.FOUND) {

                noteController.addCollaborator(processStatusNote -> {

                    String message = (processStatusNote == ProcessStatus.SUCCESS) ?
                            getResources().getString(R.string.notes_message_collaborator_add_success) :
                            getResources().getString(R.string.notes_message_collaborator_add_failed);
                    Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                    if (processStatusNote == ProcessStatus.SUCCESS)
                        emailEditText.setText("");

                    addButton.setBackgroundColor(getResources().getColor(R.color.backgroundLightColor));

                }, noteID, user.getId());

            } else {
                String message = getResources().getString(R.string.notes_message_collaborator_add_not_found);
                Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();

                addButton.setBackgroundColor(getResources().getColor(R.color.backgroundLightColor));
            }
        },  email);

    }

}
