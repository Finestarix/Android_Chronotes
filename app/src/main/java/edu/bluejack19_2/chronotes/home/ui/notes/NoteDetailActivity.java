package edu.bluejack19_2.chronotes.home.ui.notes;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.chinalwb.are.AREditText;
import com.chinalwb.are.AREditor;
import com.chinalwb.are.styles.toolbar.ARE_ToolbarDefault;
import com.chinalwb.are.styles.toolbar.IARE_Toolbar;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentCenter;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentLeft;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_AlignmentRight;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_At;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Bold;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Hr;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Image;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Italic;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Link;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListBullet;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_ListNumber;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Quote;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Strikethrough;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Subscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Superscript;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Underline;
import com.chinalwb.are.styles.toolitems.ARE_ToolItem_Video;
import com.chinalwb.are.styles.toolitems.IARE_ToolItem;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Objects;
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
    private AREditText contentEditText;
    private IARE_Toolbar arEditor;

    private Note note;

    private NoteController noteController;

    private boolean isNewNote;
    private boolean scrollerAtEnd;

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

        else if (itemId == R.id.remove_note)
            removeNoteDialog();

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        goToPage(HomeActivity.class);
    }

    private void setUIComponent() {
        titleEditText = findViewById(R.id.notes_detail_title);
        contentEditText = findViewById(R.id.notes_detail_content);

        arEditor = findViewById(R.id.notes_detail_text_editor);

        setUITextEditor();
        initToolbarArrow();
    }

    private void setUITextEditor() {
        IARE_ToolItem bold = new ARE_ToolItem_Bold();
        IARE_ToolItem italic = new ARE_ToolItem_Italic();
        IARE_ToolItem underline = new ARE_ToolItem_Underline();
        IARE_ToolItem strikethrough = new ARE_ToolItem_Strikethrough();
        IARE_ToolItem quote = new ARE_ToolItem_Quote();
        IARE_ToolItem listNumber = new ARE_ToolItem_ListNumber();
        IARE_ToolItem listBullet = new ARE_ToolItem_ListBullet();
        IARE_ToolItem hr = new ARE_ToolItem_Hr();
        IARE_ToolItem link = new ARE_ToolItem_Link();
        IARE_ToolItem subscript = new ARE_ToolItem_Subscript();
        IARE_ToolItem superscript = new ARE_ToolItem_Superscript();
        IARE_ToolItem left = new ARE_ToolItem_AlignmentLeft();
        IARE_ToolItem center = new ARE_ToolItem_AlignmentCenter();
        IARE_ToolItem right = new ARE_ToolItem_AlignmentRight();
        IARE_ToolItem image = new ARE_ToolItem_Image();
        IARE_ToolItem video = new ARE_ToolItem_Video();
        IARE_ToolItem at = new ARE_ToolItem_At();

        arEditor.addToolbarItem(bold);
        arEditor.addToolbarItem(italic);
        arEditor.addToolbarItem(underline);
        arEditor.addToolbarItem(strikethrough);
        arEditor.addToolbarItem(quote);
        arEditor.addToolbarItem(listNumber);
        arEditor.addToolbarItem(listBullet);
        arEditor.addToolbarItem(hr);
        arEditor.addToolbarItem(link);
        arEditor.addToolbarItem(subscript);
        arEditor.addToolbarItem(superscript);
        arEditor.addToolbarItem(left);
        arEditor.addToolbarItem(center);
        arEditor.addToolbarItem(right);
        arEditor.addToolbarItem(image);
        arEditor.addToolbarItem(video);
        arEditor.addToolbarItem(at);

        contentEditText.setToolbar(arEditor);
    }

    private void initToolbarArrow() {
        final ImageView imageView = this.findViewById(R.id.notes_detail_text_editor_arrow);
        if (this.arEditor instanceof ARE_ToolbarDefault) {
            ((ARE_ToolbarDefault) arEditor).getViewTreeObserver().addOnScrollChangedListener(() -> {

                int scrollX = ((ARE_ToolbarDefault) arEditor).getScrollX();
                int scrollWidth = ((ARE_ToolbarDefault) arEditor).getWidth();
                int fullWidth = ((ARE_ToolbarDefault) arEditor).getChildAt(0).getWidth();

                if (scrollX + scrollWidth < fullWidth) {
                    imageView.setImageResource(R.drawable.ic_arrow_right);
                    scrollerAtEnd = false;
                } else {
                    imageView.setImageResource(R.drawable.ic_arrow_left);
                    scrollerAtEnd = true;
                }
            });
        }

        imageView.setOnClickListener(view -> {
            if (scrollerAtEnd) {
                ((ARE_ToolbarDefault) arEditor).smoothScrollBy(-Integer.MAX_VALUE, 0);
                scrollerAtEnd = false;
            } else {
                int hsWidth = ((ARE_ToolbarDefault) arEditor).getChildAt(0).getWidth();
                ((ARE_ToolbarDefault) arEditor).smoothScrollBy(hsWidth, 0);
                scrollerAtEnd = true;
            }
        });
    }

    private void getIntentData() {
        String noteJSON = getIntent().getStringExtra(INTENT_DATA);

        isNewNote = noteJSON == null;

        if (noteJSON != null) {
            Gson gson = new Gson();
            note = gson.fromJson(noteJSON, Note.class);

            titleEditText.setText(note.getName());
            contentEditText.fromHtml(note.getDetail());
        } else {
            String ID = UUID.randomUUID().toString();
            String tag = "Personal";
            String userID = SessionStorage.getSessionStorage(getApplicationContext());

            note = new Note(ID, "", "", "", tag, userID, new ArrayList<>());
            note.addUsers(userID);

            insertNoteNotExist();
        }
    }

    private void insertNoteNotExist() {
        noteController.insertNewNote(processStatus -> {

            if (processStatus == ProcessStatus.FAILED) {
                String message = getResources().getString(R.string.notes_message_insert_failed);
                Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();
                goToPage(HomeActivity.class);
            }

        }, note);
    }

    private void setNote() {
        String name = titleEditText.getText().toString();
        String content = contentEditText.getHtml();

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
                titleEditText.getText().toString() + "\n" + Objects.requireNonNull(contentEditText.getText()).toString();

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

        String tag = (dialogFragment instanceof NoteCollaborator) ?
                "Note Collaborator" : "Note Collaborator Add";
        dialogFragment.show(getSupportFragmentManager(), tag);
    }

}

