package edu.bluejack19_2.chronotes.home.ui.notes.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.model.Note;
import edu.bluejack19_2.chronotes.model.User;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class CollaboratorNotesAdapter extends RecyclerView.Adapter<CollaboratorNotesAdapter.CollaboratorViewHolder> {

    private static ArrayList<User> collaborators;
    private static Note note;
    private Context context;
    private NoteController noteController;


    public CollaboratorNotesAdapter(Context context) {
        this.context = context;
        this.noteController = NoteController.getInstance();
    }

    public static void setNote(Note note) {
        CollaboratorNotesAdapter.note = note;
    }

    public void setCollaborators(ArrayList<User> collaborators) {
        CollaboratorNotesAdapter.collaborators = collaborators;
    }

    @NonNull
    @Override
    public CollaboratorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_notes_collaborator, parent, false);
        return new CollaboratorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CollaboratorViewHolder holder, int position) {

        holder.nameTextView.setText(
                (!SessionStorage.getSessionStorage(context).equals(collaborators.get(position).getId())) ?
                        collaborators.get(position).getName() :
                        context.getResources().getString(R.string.notes_message_collaborator_adapter_you)
        );
        holder.emailTextView.setText(collaborators.get(position).getEmail());
        holder.removeImageView.setOnClickListener(v -> removeCollaborator(position));
    }

    private void removeCollaborator(int position) {
        if (SessionStorage.getSessionStorage(context).equals(collaborators.get(position).getId())) {
            Toast.makeText(context, context.getResources().getString(R.string.notes_message_collaborator_adapter_error_self), Toast.LENGTH_SHORT).show();
            return;
        } else if (!SessionStorage.getSessionStorage(context).equals(note.getMasterUser())) {
            Toast.makeText(context, context.getResources().getString(R.string.notes_message_collaborator_adapter_error_creator), Toast.LENGTH_SHORT).show();
            return;
        }

        new AlertDialog.Builder(context)
                .setTitle(R.string.notes_message_collaborator_dialog_remove_title)
                .setMessage(context.getResources().getString(R.string.notes_message_collaborator_dialog_remove_content) + "\n" + collaborators.get(position).getEmail())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton(android.R.string.yes, (dialog, whichButton) ->
                        noteController.removeCollaborator(processStatus -> {
                            if (processStatus == ProcessStatus.SUCCESS) {
                                Toast.makeText(
                                        context,
                                        context.getResources().getString(R.string.notes_message_collaborator_adapter_remove_success),
                                        Toast.LENGTH_SHORT).show();
                                collaborators.remove(collaborators.get(position));
                                notifyDataSetChanged();
                            }
                        }, note.getId(), collaborators.get(position).getId()))
                .setNegativeButton(android.R.string.no, null).show();
    }

    @Override
    public int getItemCount() {
        return (collaborators == null) ? 0 : collaborators.size();
    }

    static class CollaboratorViewHolder extends RecyclerView.ViewHolder {

        private TextView nameTextView;
        private TextView emailTextView;
        private ImageView removeImageView;

        private CollaboratorViewHolder(@NonNull View itemView) {
            super(itemView);

            nameTextView = itemView.findViewById(R.id.collaborator_name);
            emailTextView = itemView.findViewById(R.id.collaborator_email);
            removeImageView = itemView.findViewById(R.id.collaborator_remove);
        }
    }
}
