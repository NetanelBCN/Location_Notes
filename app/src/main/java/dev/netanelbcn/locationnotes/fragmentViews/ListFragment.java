package dev.netanelbcn.locationnotes.fragmentViews;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;

import java.util.List;

import dev.netanelbcn.locationnotes.R;
import dev.netanelbcn.locationnotes.databinding.FragmentListBinding;
import dev.netanelbcn.locationnotes.models.NoteItem;
import dev.netanelbcn.locationnotes.controllers.DataManager;
import dev.netanelbcn.locationnotes.views.Note_Screen;
import dev.netanelbcn.myrv.GenericAdapter;
import dev.netanelbcn.myrv.RecyclerViewUtils;

public class ListFragment extends Fragment {

    private FragmentListBinding binding;
    private FloatingActionButton Note_FAB_add;
    private RecyclerView ListRVNotes;
    private MaterialTextView note_card_title;
    private MaterialTextView note_card_description;
    private MaterialTextView note_card_MTV_created;
   private MaterialTextView note_card_MTV_attachedPic;
    private DataManager dataManager;
    private MaterialTextView ListMTVEmpty;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        postponeEnterTransition();
        binding = FragmentListBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        dataManager = DataManager.getInstance();
        findViews();
        setupClicks();
        List<NoteItem> notes = DataManager.getInstance().getNotes();
        adjustFillOrEmptyMode(notes);
        GenericAdapter<NoteItem> adapter = RecyclerViewUtils.setupRecyclerView(
                ListRVNotes,
                notes,
                R.layout.note_card,
                (holder, item, position) -> {
                    this.note_card_title = holder.itemView.findViewById(R.id.note_card_title);
                    this.note_card_description = holder.itemView.findViewById(R.id.note_card_description);
                    this.note_card_MTV_created = holder.itemView.findViewById(R.id.note_card_MTV_created);
                    this.note_card_MTV_attachedPic=holder.itemView.findViewById(R.id.note_card_MTV_attachedPic);
                    this.note_card_title.setText(item.getNote_title());
                    this.note_card_description.setText(item.getNote_body());
                    this.note_card_MTV_created.setText("Created at: " + DataManager.getInstance().parseDateToString(item.getNote_date()));
                    if(!item.getNote_pic_url().trim().isEmpty())
                       this.note_card_MTV_attachedPic.setText("Picture attached");
                    else
                        this.note_card_MTV_attachedPic.setText("");

                },
                (item, position) -> editNote(item)
        );
        this.dataManager.setAdapter(adapter);
        ListRVNotes.setLayoutManager(new GridLayoutManager(requireContext(), 2));
        ListRVNotes.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                ListRVNotes.getViewTreeObserver().removeOnPreDrawListener(this);
                startPostponedEnterTransition();
                return true;
            }
        });
        return root;
    }

    private void adjustFillOrEmptyMode(List<NoteItem> notes) {
        if (notes.isEmpty()) {
            ListMTVEmpty.setVisibility(View.VISIBLE);
            ListRVNotes.setVisibility(View.GONE);
        } else {
            ListMTVEmpty.setVisibility(View.GONE);
            ListRVNotes.setVisibility(View.VISIBLE);
        }
    }

    private void editNote(NoteItem item) {
        DataManager.getInstance().setCurrent(item);
        startActivity(new Intent(requireContext(), Note_Screen.class));
    }

    private void setupClicks() {
        this.Note_FAB_add.setOnClickListener(v -> {
            DataManager.getInstance().setCurrent(null);
            startActivity(new Intent(requireContext(), Note_Screen.class));
        });
    }

    private void findViews() {
        this.Note_FAB_add = binding.NoteFABAdd;
        this.ListRVNotes = binding.ListRVNotes;
        this.ListMTVEmpty = binding.ListMTVEmpty;

    }

    @Override
    public void onResume() {
        super.onResume();
        this.dataManager.getAdapter().notifyDataSetChanged();
        adjustFillOrEmptyMode(dataManager.getNotes());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
