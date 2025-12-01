package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

import es.ucm.fdi.pad.hahabit.data.HabitArea;
import es.ucm.fdi.pad.hahabit.databinding.FragmentTrackerBinding;
import es.ucm.fdi.pad.hahabit.network.ZenQuotesClient;
import es.ucm.fdi.pad.hahabit.R;

public class TrackerFragment extends Fragment {
    private static final String TAG = "TrackerFragment";
    private static final int STREAK_THRESHOLD_FOR_QUOTE = 5;
    private static final String PREFS_NAME = "TrackerPrefs";
    private static final String KEY_LAST_CELEBRATED_STREAK = "last_celebrated_streak_";

    private FragmentTrackerBinding binding;
    private AreaTrackerAdapter adapter;
    private TrackerViewModel trackerViewModel;

    private boolean motivationalDialogShown = false;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        trackerViewModel =
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

                checkAndShowMotivationalQuote(habitAreas);
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

    private void checkAndShowMotivationalQuote(List<HabitArea> areas) {
        Log.d(TAG, "checkAndShowMotivationalQuote - Areas: " + (areas == null ? "null" : areas.size()));

        if (motivationalDialogShown || areas == null || areas.isEmpty()) {
            Log.d(TAG, "Saliendo - dialogShown:" + motivationalDialogShown + " isEmpty:" + (areas == null || areas.isEmpty()));
            return;
        }

        SharedPreferences prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE);
        HabitArea bestArea = null;
        int maxStreak = 0;

        for (HabitArea area : areas) {
            int streak = area.getCurrentStreak();
            Log.d(TAG, "Área: " + area.getAreaName() + " Racha: " + streak);

            if (streak >= STREAK_THRESHOLD_FOR_QUOTE && streak > maxStreak) {
                String key = KEY_LAST_CELEBRATED_STREAK + area.getAreaName();
                int lastCelebratedStreak = prefs.getInt(key, 0);

                if (streak > lastCelebratedStreak) {
                    maxStreak = streak;
                    bestArea = area;
                }
            }
        }

        // mostrar el diálogo si encontramos una racha digna de celebración
        if (bestArea != null) {
            motivationalDialogShown = true;

            String key = KEY_LAST_CELEBRATED_STREAK + bestArea.getAreaName();
            prefs.edit().putInt(key, maxStreak).apply();

            fetchQuoteAndShowDialog(bestArea.getAreaName(), maxStreak);
        }
    }

    private void fetchQuoteAndShowDialog(String areaName, int streak) {
        ZenQuotesClient.fetchQuoteOfTheDay(new ZenQuotesClient.QuoteCallback() {
            @Override
            public void onSuccess(String quote, String author) {
                if (!isAdded()) {
                    return;
                }

                requireActivity().runOnUiThread(() ->
                        showMotivationalDialog(areaName, streak, quote, author)
                );
            }

            @Override
            public void onError(Exception e) {
                if (!isAdded()) {
                    return;
                }

                // Mostrar diálogo con mensaje predeterminado si falla la API
                requireActivity().runOnUiThread(() -> {
                    String fallbackQuote = "Success is the sum of small efforts repeated day in and day out.";
                    showMotivationalDialog(areaName, streak, fallbackQuote, "Robert Collier");
                });
            }
        });
    }

    private void showMotivationalDialog(String areaName,
                                        int streak,
                                        String quote,
                                        String author) {



        String felicitacion = getString(R.string.felicitacion);
        String diasSeguidos = getString(R.string.dias_seguidos);

        String message = felicitacion + " " + streak + " " + diasSeguidos + " \"" + areaName + "\".\n\n" +
                quote;

        if (author != null && !author.isEmpty()) {
            message += "\n\n— " + author;
        }

            new AlertDialog.Builder(requireContext())
                .setTitle(R.string.sigue_asi)
                .setMessage(message.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        motivationalDialogShown = false;

        if (trackerViewModel != null) {
            List<HabitArea> currentAreas = trackerViewModel.getHabitAreas().getValue();
            if (currentAreas != null) {
                checkAndShowMotivationalQuote(currentAreas);
            }
        }
    }
}
