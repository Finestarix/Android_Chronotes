package edu.bluejack19_2.chronotes.home.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.controller.UserController;
import edu.bluejack19_2.chronotes.home.HomeActivity;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class NoteDetailActivity extends AppCompatActivity {

    private EditText titleEditText;
    private EditText contentEditText;

    private Note note;

    private NoteController noteController;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (SessionStorage.getSessionStorage(this) == null) {
            goToLogin();
            return;
        }

        setContentView(R.layout.activity_note_detail);

        noteController = NoteController.getInstance();

        String noteJSON = getIntent().getStringExtra("note");

        titleEditText = findViewById(R.id.notes_detail_title);
        contentEditText = findViewById(R.id.notes_detail_content);

        if (noteJSON != null) {
            Gson gson = new Gson();
            note = gson.fromJson(noteJSON, Note.class);

            titleEditText.setText(note.getName());
            contentEditText.setText(note.getDetail());
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_note_detail_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {

            case R.id.save:
                goToHome();
                return true;

            case R.id.share:
                shareNote();
                return true;

            case R.id.collaborator:
                return true;

            case R.id.add_reminder:
                return true;

            case R.id.remove_reminder:
                return true;

            case R.id.remove_note:
                removeNoteDialog();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private void goToHome() {
        Intent intentToHome = new Intent(NoteDetailActivity.this, HomeActivity.class);
        intentToHome.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToHome);
    }

    private void goToLogin() {
        SessionStorage.removeSessionStorage(NoteDetailActivity.this);

        GoogleSignInOptions googleSignInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        GoogleSignInClient googleSignInClient = GoogleSignIn.getClient(this, googleSignInOptions);
        googleSignInClient.signOut();

        Intent intentToLogin = new Intent(NoteDetailActivity.this, LoginActivity.class);
        intentToLogin.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intentToLogin);
    }

    private void shareNote() {
        String shareStr = "I want to share my notes with you!\nTitle: " +
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
                .setTitle("Remove Notes")
                .setMessage("Do you really want to remove this notes ?")
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) -> {
                    removeNote();
                })
                .setNegativeButton(android.R.string.no, null).show();
    }

    private void removeNote() {
        noteController.deleteNewNote(processStatus -> {
            if (processStatus == ProcessStatus.SUCCESS) {
                Toast.makeText(getApplicationContext(), "Remove Note Success", Toast.LENGTH_SHORT).show();
                goToHome();
            } else {
                Toast.makeText(getApplicationContext(), "Remove Note Failed", Toast.LENGTH_SHORT).show();
            }
        }, note);
    }
}
