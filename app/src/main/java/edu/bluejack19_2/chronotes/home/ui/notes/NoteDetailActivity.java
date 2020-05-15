package edu.bluejack19_2.chronotes.home.ui.notes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.UUID;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.utils.GeneralHandler;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class NoteDetailActivity extends AppCompatActivity {

    private static final String INTENT_DATA = "note";

    private EditText titleEditText;
    private EditText contentEditText;

    private Note note;

    private NoteController noteController;

    private boolean isNewNote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!SessionStorage.isLoggedIn(this))
            goToPage(LoginActivity.class);

        else {
            setContentView(R.layout.activity_note_detail);
            setUIComponent();

            noteController = NoteController.getInstance();

            getIntentData();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_note_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        int itemId = item.getItemId();

        if (itemId == R.id.save) {
            setNote();

            if (isNewNote) insertNote();
            else updateNote();

        } else if (itemId == R.id.share)
            shareNote();

        else if (itemId == R.id.add_collaborator)
            showFragment(new NoteCollaboratorAdd());

        else if (itemId == R.id.view_collaborator)
            showFragment(new NoteCollaborator());

        else if (itemId == R.id.reminder)
            showFragment(new NoteReminder());

        else if (itemId == R.id.remove_note)
            removeNoteDialog();

        return super.onOptionsItemSelected(item);
    }

    private void setUIComponent() {
        titleEditText = findViewById(R.id.notes_detail_title);
        contentEditText = findViewById(R.id.notes_detail_content);
    }

    private void getIntentData() {
        String noteJSON = getIntent().getStringExtra(INTENT_DATA);

        isNewNote = noteJSON == null;

        if (noteJSON != null) {
            Gson gson = new Gson();
            note = gson.fromJson(noteJSON, Note.class);

            titleEditText.setText(note.getName());
            contentEditText.setText(note.getDetail());
        } else {
            String ID = UUID.randomUUID().toString();
            String tag = "Personal";
            String userID = SessionStorage.getSessionStorage(getApplicationContext());

            note = new Note(ID, "", "", "", tag, userID, new ArrayList<>());
            note.addUsers(userID);
        }
    }

    private void setNote() {
        String name = titleEditText.getText().toString();
        String content = contentEditText.getText().toString();

        note.setName(name);
        note.setDetail(content);
        note.setLastUpdate(GeneralHandler.getCurrentTime());
    }

    private void insertNote() {
        noteController.insertNewNote(processStatus -> {

            String message = (processStatus == ProcessStatus.SUCCESS) ?
                    getResources().getString(R.string.notes_message_insert_success) :
                    getResources().getString(R.string.notes_message_insert_failed);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            if (processStatus == ProcessStatus.SUCCESS)
                goToPage(HomeActivity.class);

        }, note);
    }

    private void updateNote() {
        noteController.updateNewNote(processStatus -> {

            String message = (processStatus == ProcessStatus.SUCCESS) ?
                    getResources().getString(R.string.notes_message_update_success) :
                    getResources().getString(R.string.notes_message_update_failed);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            if (processStatus == ProcessStatus.SUCCESS)
                goToPage(HomeActivity.class);

        }, note);
    }

    private void shareNote() {
        String shareStr = getResources().getString(R.string.notes_message_share) +
                titleEditText.getText().toString() + "\n" + contentEditText.getText().toString();

        Intent sendIntent = new Intent();
        sendIntent.setAction(Intent.ACTION_SEND);
        sendIntent.putExtra(Intent.EXTRA_TEXT, shareStr);
        sendIntent.setType("text/plain");

        Intent shareIntent = Intent.createChooser(sendIntent, null);
        startActivity(shareIntent);
    }

    private void removeNoteDialog() {
        new AlertDialog.Builder(this)
                .setTitle(getResources().getString(R.string.notes_message_dialog_remove_title))
                .setMessage(getResources().getString(R.string.notes_message_dialog_remove_content))
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> removeNote())
                .setNegativeButton(android.R.string.no, null)
                .show();
    }

    private void removeNote() {
        noteController.deleteNewNote(processStatus -> {

            String message = (processStatus == ProcessStatus.SUCCESS) ?
                    getResources().getString(R.string.notes_message_remove_success) :
                    getResources().getString(R.string.notes_message_remove_failed);
            Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

            if (processStatus == ProcessStatus.SUCCESS)
                goToPage(HomeActivity.class);

        }, note);
    }

    private void goToPage(Class aClass) {

        if (aClass == LoginActivity.class) {
            SessionStorage.removeSessionStorage(NoteDetailActivity.this);

            GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
            googleSignInClient.signOut();
        }

        Intent intent = new Intent(NoteDetailActivity.this, aClass);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }

    private void showFragment(DialogFragment dialogFragment) {

        Bundle bundle = new Bundle();
        bundle.putString(INTENT_DATA, note.getId());

        dialogFragment.setArguments(bundle);

        String tag = (dialogFragment instanceof NoteReminder) ?
                "Note Reminder" : (dialogFragment instanceof NoteCollaborator) ?
                "Note Collaborator" : "Note Collaborator Add";
        dialogFragment.show(getSupportFragmentManager(), tag);
    }

}

