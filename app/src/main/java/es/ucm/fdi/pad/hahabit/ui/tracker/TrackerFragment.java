package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import es.ucm.fdi.pad.hahabit.databinding.FragmentTrackerBinding;

public class TrackerFragment extends Fragment {

    private FragmentTrackerBinding binding;
    private AreaTrackerAdapter adapter;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        TrackerViewModel trackerViewModel =
                new ViewModelProvider(this).get(TrackerViewModel.class);

        binding = FragmentTrackerBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        RecyclerView recyclerView = binding.recyclerViewAreas;
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setHasFixedSize(false);

        adapter = new AreaTrackerAdapter();
        recyclerView.setAdapter(adapter);

        trackerViewModel.getHabitAreas().observe(getViewLifecycleOwner(), habitAreas -> {
            if (habitAreas != null) {
                adapter.setHabitAreas(habitAreas);
            }
        });

        trackerViewModel.getSummary().observe(getViewLifecycleOwner(), summary -> {
            if (summary != null) {
                binding.textActiveDays.setText(String.valueOf(summary.getTotalActiveDays()));
                binding.textBestStreak.setText(String.valueOf(summary.getBestStreak()));
                binding.textTotalCompletions.setText(String.valueOf(summary.getTotalCompletions()));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
