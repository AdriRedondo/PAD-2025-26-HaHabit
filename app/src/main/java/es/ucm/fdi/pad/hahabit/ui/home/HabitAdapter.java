package es.ucm.fdi.pad.hahabit.ui.home;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.button.MaterialButton;
import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.Habit;
import java.util.Locale;

public class HabitAdapter extends ListAdapter<Habit, RecyclerView.ViewHolder> {

    private static final int VIEW_TYPE_NORMAL = 0;
    private static final int VIEW_TYPE_LIST = 1;
    private static final int VIEW_TYPE_TIMER = 2;

    private OnHabitClickListener listener;
    private boolean isToday = true;
    private boolean isCompletedSection = false;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
        void onHabitChecked(Habit habit, boolean isChecked);
        void onTimerToggle(Habit habit);
        void onTimerReset(Habit habit);
    }

    public HabitAdapter() {
        super(DIFF_CALLBACK);
    }

    public HabitAdapter(boolean isCompletedSection) {
        super(DIFF_CALLBACK);
        this.isCompletedSection = isCompletedSection;
    }

    public void setIsToday(boolean isToday) {
        this.isToday = isToday;
        notifyDataSetChanged();
    }

    private static final DiffUtil.ItemCallback<Habit> DIFF_CALLBACK = new DiffUtil.ItemCallback<Habit>() {
        @Override
        public boolean areItemsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            // Comparar también el estado del temporizador para que se actualice
            boolean sameBasic = oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.getArea().equals(newItem.getArea()) &&
                    oldItem.getHabitType().equals(newItem.getHabitType());

            // Si es un temporizador, también comparar el estado
            if ("timer".equals(oldItem.getHabitType())) {
                return sameBasic &&
                       oldItem.isTimerRunning() == newItem.isTimerRunning() &&
                       oldItem.getTimerElapsed().equals(newItem.getTimerElapsed());
            }

            return sameBasic;
        }
    };

    public void setOnHabitClickListener(OnHabitClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Habit habit = getItem(position);
        String habitType = habit.getHabitType();

        if ("list".equals(habitType)) {
            return VIEW_TYPE_LIST;
        } else if ("timer".equals(habitType)) {
            return VIEW_TYPE_TIMER;
        } else {
            return VIEW_TYPE_NORMAL;
        }
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        switch (viewType) {
            case VIEW_TYPE_LIST:
                View listView = inflater.inflate(R.layout.item_habit_list, parent, false);
                return new ListViewHolder(listView);

            case VIEW_TYPE_TIMER:
                View timerView = inflater.inflate(R.layout.item_habit_timer, parent, false);
                return new TimerViewHolder(timerView);

            case VIEW_TYPE_NORMAL:
            default:
                View normalView = inflater.inflate(R.layout.item_habit, parent, false);
                return new NormalViewHolder(normalView);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Habit habit = getItem(position);

        if (holder instanceof NormalViewHolder) {
            bindNormalViewHolder((NormalViewHolder) holder, habit);
        } else if (holder instanceof ListViewHolder) {
            bindListViewHolder((ListViewHolder) holder, habit);
        } else if (holder instanceof TimerViewHolder) {
            bindTimerViewHolder((TimerViewHolder) holder, habit);
        }
    }

    private void bindNormalViewHolder(NormalViewHolder holder, Habit habit) {
        boolean isCompleted = isCompletedSection;

        holder.tvTitle.setText(habit.getTitle());
        holder.tvArea.setText(habit.getArea());
        holder.checkBox.setChecked(isCompleted);

        if (habit.getProgress() != null && habit.getProgress() > 0) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress((int) (habit.getProgress() * 100));
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }

        int backgroundRes = getBackgroundForArea(habit.getArea());
        holder.cardContainer.setBackgroundResource(backgroundRes);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habit);
            }
        });

        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(isCompleted);

        boolean shouldBeEnabled = isToday && !isCompletedSection;
        holder.checkBox.setEnabled(shouldBeEnabled);
        holder.checkBox.setAlpha(shouldBeEnabled ? 1.0f : 0.5f);

        if (shouldBeEnabled) {
            holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if (listener != null && buttonView.isPressed()) {
                    listener.onHabitChecked(habit, isChecked);
                }
            });
        } else {
            holder.checkBox.setOnCheckedChangeListener(null);
        }
    }

    private void bindListViewHolder(ListViewHolder holder, Habit habit) {
        holder.tvTitle.setText(habit.getTitle());
        holder.tvArea.setText(habit.getArea());

        int backgroundRes = getBackgroundForArea(habit.getArea());
        holder.cardContainer.setBackgroundResource(backgroundRes);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habit);
            }
        });

        // Parsear y mostrar los items de la lista
        String listItemsJson = habit.getListItems();
        int totalItems = 0;
        int completedItems = 0;

        if (listItemsJson != null && !listItemsJson.isEmpty()) {
            // Contar items completados (simple parsing)
            String[] items = listItemsJson.split("\\},\\{");
            totalItems = items.length;
            for (String item : items) {
                if (item.contains("\"completed\":true")) {
                    completedItems++;
                }
            }
        }

        holder.tvListProgress.setText(completedItems + "/" + totalItems);

        // Mostrar el RecyclerView y botón
        holder.rvListItems.setVisibility(View.VISIBLE);
        holder.btnAddListItem.setVisibility(View.VISIBLE);

        // Por ahora solo mostramos mensaje informativo
        holder.btnAddListItem.setText("Ver/Editar items (próximamente)");
        holder.btnAddListItem.setEnabled(false);
    }

    private void bindTimerViewHolder(TimerViewHolder holder, Habit habit) {
        holder.tvTitle.setText(habit.getTitle());
        holder.tvArea.setText(habit.getArea());

        int backgroundRes = getBackgroundForArea(habit.getArea());
        holder.cardContainer.setBackgroundResource(backgroundRes);

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habit);
            }
        });

        // Detener actualizaciones previas
        holder.stopTimer();

        // Función para actualizar el display
        Runnable updateDisplay = new Runnable() {
            @Override
            public void run() {
                if (habit.isTimerRunning() && habit.getTimerStartTime() != null) {
                    long currentElapsed = habit.getTimerElapsed() + (System.currentTimeMillis() - habit.getTimerStartTime());
                    holder.tvTimerDisplay.setText(formatTime(currentElapsed));

                    // Actualizar progreso
                    if (habit.getTimerTarget() != null && habit.getTimerTarget() > 0) {
                        int progress = (int) ((currentElapsed * 100) / habit.getTimerTarget());
                        holder.progressBarTimer.setProgress(Math.min(progress, 100));
                    }

                    // Programar siguiente actualización
                    holder.handler.postDelayed(this, 100);
                }
            }
        };
        holder.updateRunnable = updateDisplay;

        // Mostrar tiempo inicial
        long elapsedMillis = habit.getTimerElapsed();
        if (habit.isTimerRunning() && habit.getTimerStartTime() != null) {
            long currentElapsed = elapsedMillis + (System.currentTimeMillis() - habit.getTimerStartTime());
            holder.tvTimerDisplay.setText(formatTime(currentElapsed));
            // Iniciar actualizaciones automáticas
            holder.handler.post(updateDisplay);
        } else {
            holder.tvTimerDisplay.setText(formatTime(elapsedMillis));
        }

        // Cambiar icono según estado
        if (habit.isTimerRunning()) {
            holder.btnPlayPause.setIconResource(android.R.drawable.ic_media_pause);
        } else {
            holder.btnPlayPause.setIconResource(android.R.drawable.ic_media_play);
        }

        // Mostrar progreso si hay un objetivo
        if (habit.getTimerTarget() != null && habit.getTimerTarget() > 0) {
            holder.progressBarTimer.setVisibility(View.VISIBLE);
            int progress = (int) ((elapsedMillis * 100) / habit.getTimerTarget());
            holder.progressBarTimer.setProgress(Math.min(progress, 100));
        } else {
            holder.progressBarTimer.setVisibility(View.GONE);
        }

        holder.btnPlayPause.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimerToggle(habit);
            }
        });

        holder.btnResetTimer.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimerReset(habit);
            }
        });
    }

    // Formatear milisegundos a HH:MM:SS
    private String formatTime(long millis) {
        long seconds = millis / 1000;
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d:%02d", hours, minutes, secs);
    }

    private int getBackgroundForArea(String area) {
        if (area == null) return R.drawable.bg_habit_otros;

        switch (area.toLowerCase()) {
            case "cocinar":
                return R.drawable.bg_habit_cocina;
            case "deporte":
            case "salud":
                return R.drawable.bg_habit_deporte;
            case "estudio":
                return R.drawable.bg_habit_estudio;
            default:
                return R.drawable.bg_habit_otros;
        }
    }

    // ViewHolder para hábitos normales
    static class NormalViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArea;
        CheckBox checkBox;
        ProgressBar progressBar;
        View cardContainer;

        NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            checkBox = itemView.findViewById(R.id.checkboxHabit);
            progressBar = itemView.findViewById(R.id.progressBarHabit);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);
        }
    }

    // ViewHolder para hábitos tipo lista
    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArea, tvListProgress;
        RecyclerView rvListItems;
        Button btnAddListItem;
        View cardContainer;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            tvListProgress = itemView.findViewById(R.id.tvListProgress);
            rvListItems = itemView.findViewById(R.id.rvListItems);
            btnAddListItem = itemView.findViewById(R.id.btnAddListItem);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);

            rvListItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

    // ViewHolder para hábitos tipo temporizador
    static class TimerViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArea, tvTimerDisplay;
        MaterialButton btnPlayPause;
        Button btnResetTimer;
        ProgressBar progressBarTimer;
        View cardContainer;

        Handler handler;
        Runnable updateRunnable;

        TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            tvTimerDisplay = itemView.findViewById(R.id.tvTimerDisplay);
            btnPlayPause = itemView.findViewById(R.id.btnPlayPause);
            btnResetTimer = itemView.findViewById(R.id.btnResetTimer);
            progressBarTimer = itemView.findViewById(R.id.progressBarTimer);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);

            handler = new Handler(Looper.getMainLooper());
        }

        void stopTimer() {
            if (handler != null && updateRunnable != null) {
                handler.removeCallbacks(updateRunnable);
            }
        }
    }
}
