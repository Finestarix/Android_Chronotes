package edu.bluejack19_2.chronotes.home.ui.notes;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
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

    private ListNotesAdapter listNotesAdapter;
    private NoteController noteController;
    private RecyclerView noteRecyclerView;
    private FloatingActionButton floatingActionButton;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_notes, container, false);

        noteController = NoteController.getInstance();
        listNotesAdapter = new ListNotesAdapter(getContext());
        listNotesAdapter.setNotes(null);
        listNotesAdapter.notifyDataSetChanged();

        noteRecyclerView = view.findViewById(R.id.rv_notes);
        noteRecyclerView.setAdapter(listNotesAdapter);
        noteRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        floatingActionButton = view.findViewById(R.id.fab_notes);
        floatingActionButton.setOnClickListener(v -> {
            Intent intentToDetail = new Intent(getActivity(), NoteDetailActivity.class);
            intentToDetail.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intentToDetail);
        });

        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        noteController.getNotesByUserID((notes, processStatus) -> {

            if (processStatus == ProcessStatus.FOUND) {
                listNotesAdapter.setNotes(notes);
                listNotesAdapter.notifyDataSetChanged();
            } else {
                // TODO: Add Error Message
            }

        }, SessionStorage.getSessionStorage(requireContext()));
    }
}
