package edu.bluejack19_2.chronotes.home.ui.notes.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.Gson;

import java.io.Serializable;
import java.util.ArrayList;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.home.ui.notes.NoteDetailActivity;
import edu.bluejack19_2.chronotes.main.login.LoginActivity;
import edu.bluejack19_2.chronotes.model.Note;

public class ListNotesAdapter extends RecyclerView.Adapter<ListNotesAdapter.NoteViewHolder> {

    private static ArrayList<Note> notes;

    public void setNotes(ArrayList<Note> notes) {
        ListNotesAdapter.notes = notes;
    }

    private Context context;

    public ListNotesAdapter(Context context) {
        this.context = context;
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_notes, parent, false);
        return new ListNotesAdapter.NoteViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull NoteViewHolder holder, int position) {
        holder.titleTextView.setText(notes.get(position).getName());
        holder.dateTextView.setText(notes.get(position).getLastUpdate());
        holder.contentTextView.setText(notes.get(position).getDetail());
        holder.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Gson gson = new Gson();
                Note note = notes.get(position);
                String noteJSON = gson.toJson(note);

                Intent intentToDetail = new Intent(context, NoteDetailActivity.class);
                intentToDetail.putExtra("note", noteJSON);
                intentToDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                context.startActivity(intentToDetail);
            }
        });
    }

    @Override
    public int getItemCount() {
        return (notes == null) ? 0 : notes.size();
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {

        private TextView titleTextView;
        private TextView dateTextView;
        private TextView contentTextView;
        private CardView cardView;

        NoteViewHolder(@NonNull View itemView) {
            super(itemView);

            titleTextView = itemView.findViewById(R.id.notes_title);
            dateTextView = itemView.findViewById(R.id.notes_date);
            contentTextView = itemView.findViewById(R.id.notes_content);
            cardView = itemView.findViewById(R.id.cv_notes);
        }
    }

}
