package edu.bluejack19_2.chronotes.home.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import edu.bluejack19_2.chronotes.R;
import edu.bluejack19_2.chronotes.controller.NoteController;
import edu.bluejack19_2.chronotes.home.ui.notes.adapter.ListNotesAdapter;
import edu.bluejack19_2.chronotes.utils.ProcessStatus;
import edu.bluejack19_2.chronotes.utils.session.SessionStorage;

public class NotesFragment extends Fragment {

    private NoteController noteController;

    private LinearLayout messageLinearLayout;
    private ListNotesAdapter listNotesAdapter;
    private RecyclerView noteRecyclerView;

    private FloatingActionButton floatingActionButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);
        setUIComponent(view);
        messageLinearLayout.setVisibility(View.GONE);

        floatingActionButton.setOnClickListener(v -> goToPage());

        listNotesAdapter = new ListNotesAdapter(getContext());
        listNotesAdapter.setNotes(null);
        listNotesAdapter.notifyDataSetChanged();

        noteRecyclerView.setAdapter(listNotesAdapter);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        noteController = NoteController.getInstance();
        loadNote();

        return view;
    }

    private void setUIComponent(View view) {
        noteRecyclerView = view.findViewById(R.id.rv_notes);
        floatingActionButton = view.findViewById(R.id.fab_notes);
        messageLinearLayout = view.findViewById(R.id.layout_notes);
    }

    private void loadNote() {
        noteController.getNotesByUserID((notes, processStatus) -> {

            if (processStatus == ProcessStatus.FOUND) {

                listNotesAdapter.setNotes(notes);
                listNotesAdapter.notifyDataSetChanged();
            } else if (processStatus == ProcessStatus.NOT_FOUND)
                messageLinearLayout.setVisibility(View.VISIBLE);

        }, SessionStorage.getSessionStorage(requireContext()));
    }

    private void goToPage() {
        Intent intent = new Intent(getActivity(), NoteDetailActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}
