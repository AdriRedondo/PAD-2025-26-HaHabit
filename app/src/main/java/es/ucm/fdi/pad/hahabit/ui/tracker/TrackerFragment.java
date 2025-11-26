package es.ucm.fdi.pad.hahabit.ui.tracker;

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
import es.ucm.fdi.pad.hahabit.network.PaperQuotesClient;

public class TrackerFragment extends Fragment {
    private static final String TAG = "TrackerFragment";
    private static final int STREAK_THRESHOLD_FOR_QUOTE = 1;

    private FragmentTrackerBinding binding;
    private AreaTrackerAdapter adapter;

    private boolean motivationalDialogShown = false;

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
        if (motivationalDialogShown || areas == null || areas.isEmpty()) {
            return;
        }

        HabitArea bestArea = null;
        int maxStreak = 0;

        // Buscamos el área con la racha más larga
        for (HabitArea area : areas) {
            int streak = area.getCurrentStreak();
            if (streak > maxStreak) {
                maxStreak = streak;
                bestArea = area;
            }
        }

        // Si la mejor racha supera el umbral, pedimos una cita a PaperQuotes
        if (bestArea != null && maxStreak >= STREAK_THRESHOLD_FOR_QUOTE) {
            motivationalDialogShown = true;
            fetchQuoteAndShowDialog(bestArea.getAreaName(), maxStreak);
        }
    }

    private void fetchQuoteAndShowDialog(String areaName, int streak) {
        Log.d(TAG, "Llamando a PaperQuotesClient.fetchQuoteOfTheDay...");

        PaperQuotesClient.fetchQuoteOfTheDay(new PaperQuotesClient.QuoteCallback() {
            @Override
            public void onSuccess(String quote, String author) {
                Log.d(TAG, "Quote recibida: \"" + quote + "\" de " + author);
                if (!isAdded()) {
                    Log.d(TAG, "Fragment no añadido, no se muestra diálogo");
                    return; // el fragment ya no está en pantalla
                }
                requireActivity().runOnUiThread(() ->
                        showMotivationalDialog(areaName, streak, quote, author)
                );
            }

            @Override
            public void onError(Exception e) {
                // Si quieres, aquí puedes mostrar un mensaje fijo
                // o simplemente no hacer nada para fallar en silencio.
            }
        });
    }

    private void showMotivationalDialog(String areaName,
                                        int streak,
                                        String quote,
                                        String author) {



        StringBuilder message = new StringBuilder();
        message.append("¡Enhorabuena! Llevas ")
                .append(streak)
                .append(" días seguidos en el área \"")
                .append(areaName)
                .append("\".\n\n")
                .append(quote);

        if (author != null && !author.isEmpty()) {
            message.append("\n\n— ").append(author);
        } else {
            message.append(""); // nada, no mostramos autor
        }

            new AlertDialog.Builder(requireContext())
                .setTitle("Sigue así ✨")
                .setMessage(message.toString())
                .setPositiveButton(android.R.string.ok, null)
                .show();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
