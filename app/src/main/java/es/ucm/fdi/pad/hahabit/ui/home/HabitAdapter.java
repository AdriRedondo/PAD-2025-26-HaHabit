package es.ucm.fdi.pad.hahabit.ui.home;

import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
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
import java.util.ArrayList;
import java.util.List;
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
        void onHabitDelete(Habit habit);
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
                    oldItem.getType().equals(newItem.getType());

            // Si es un temporizador, también comparar el estado
            if ("timer".equals(oldItem.getType())) {
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
        String type = habit.getType();

        if ("list".equals(type)) {
            return VIEW_TYPE_LIST;
        } else if ("timer".equals(type)) {
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

        // Botón de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitDelete(habit);
            }
        });
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

        // Parsear los items de la lista desde JSON
        List<ListItemAdapter.ListItem> items = parseListItems(habit.getListItems());

        // Contar items completados
        int totalItems = items.size();
        int completedItems = 0;
        for (ListItemAdapter.ListItem item : items) {
            if (item.completed) {
                completedItems++;
            }
        }

        holder.tvListProgress.setText(completedItems + "/" + totalItems);

        // Configurar el adapter de la lista
        if (holder.listItemAdapter == null) {
            holder.listItemAdapter = new ListItemAdapter();
            holder.rvListItems.setAdapter(holder.listItemAdapter);
        }

        holder.listItemAdapter.setItems(items);

        // Listener para guardar cambios
        holder.listItemAdapter.setOnItemChangeListener(updatedItems -> {
            String json = serializeListItems(updatedItems);
            habit.setListItems(json);

            // Actualizar el progreso
            int total = updatedItems.size();
            int completed = 0;
            for (ListItemAdapter.ListItem item : updatedItems) {
                if (item.completed) {
                    completed++;
                }
            }
            holder.tvListProgress.setText(completed + "/" + total);

            // Notificar al listener para guardar en BD
            if (listener != null) {
                listener.onHabitChecked(habit, false); // Usamos este callback para actualizar
            }
        });

        // Mostrar el RecyclerView y botón
        holder.rvListItems.setVisibility(View.VISIBLE);
        holder.btnAddListItem.setVisibility(View.VISIBLE);
        holder.btnAddListItem.setEnabled(true);
        holder.btnAddListItem.setText("+ Añadir item");

        // Botón para añadir nuevos items
        holder.btnAddListItem.setOnClickListener(v -> {
            holder.listItemAdapter.addItem();
        });

        // Botón de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitDelete(habit);
            }
        });
    }

    // Parsear JSON a lista de items
    private List<ListItemAdapter.ListItem> parseListItems(String json) {
        List<ListItemAdapter.ListItem> items = new ArrayList<>();
        if (json == null || json.isEmpty()) {
            return items;
        }

        try {
            // Parsing manual simple (sin librerías externas)
            json = json.trim();
            if (json.startsWith("[")) json = json.substring(1);
            if (json.endsWith("]")) json = json.substring(0, json.length() - 1);

            String[] itemsArray = json.split("\\},\\{");
            for (String itemStr : itemsArray) {
                itemStr = itemStr.replace("{", "").replace("}", "");
                String text = "";
                boolean completed = false;

                String[] parts = itemStr.split(",");
                for (String part : parts) {
                    if (part.contains("\"text\"")) {
                        text = part.split(":")[1].replace("\"", "").trim();
                    } else if (part.contains("\"completed\"")) {
                        completed = part.split(":")[1].trim().equals("true");
                    }
                }

                items.add(new ListItemAdapter.ListItem(text, completed));
            }
        } catch (Exception e) {
            android.util.Log.e("HabitAdapter", "Error parsing list items: " + e.getMessage());
        }

        return items;
    }

    // Serializar lista de items a JSON
    private String serializeListItems(List<ListItemAdapter.ListItem> items) {
        if (items == null || items.isEmpty()) {
            return "";
        }

        StringBuilder json = new StringBuilder("[");
        for (int i = 0; i < items.size(); i++) {
            ListItemAdapter.ListItem item = items.get(i);
            json.append("{\"text\":\"").append(item.text.replace("\"", "\\\""))
                .append("\",\"completed\":").append(item.completed).append("}");
            if (i < items.size() - 1) {
                json.append(",");
            }
        }
        json.append("]");

        return json.toString();
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

        // Capturar valores iniciales para evitar referencias obsoletas
        final long initialElapsed = habit.getTimerElapsed() != null ? habit.getTimerElapsed() : 0L;
        final long initialStartTime = habit.getTimerStartTime() != null ? habit.getTimerStartTime() : 0L;
        final long timerTarget = habit.getTimerTarget() != null ? habit.getTimerTarget() : 0L;
        final boolean isRunning = habit.isTimerRunning();

        // Función para actualizar el display (CUENTA REGRESIVA)
        if (isRunning && initialStartTime > 0) {
            Runnable updateDisplay = new Runnable() {
                @Override
                public void run() {
                    // Calcular elapsed desde valores iniciales capturados
                    long currentElapsed = initialElapsed + (System.currentTimeMillis() - initialStartTime);

                    // Calcular tiempo restante (cuenta regresiva)
                    long timeRemaining = 0;
                    if (timerTarget > 0) {
                        timeRemaining = Math.max(0, timerTarget - currentElapsed);
                    }

                    holder.tvTimerDisplay.setText(formatTimeMinutes(timeRemaining));

                    // Continuar actualizando si hay tiempo restante
                    if (timeRemaining > 0) {
                        holder.handler.postDelayed(this, 1000);
                    }
                }
            };
            holder.updateRunnable = updateDisplay;
            holder.handler.post(updateDisplay);
        } else {
            // Cuando está pausado, mostrar el tiempo restante
            long timeRemaining = 0;
            if (timerTarget > 0) {
                timeRemaining = Math.max(0, timerTarget - initialElapsed);
            }
            holder.tvTimerDisplay.setText(formatTimeMinutes(timeRemaining));
        }

        // Establecer icono inicial según estado
        if (isRunning) {
            holder.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            holder.isShowingPlayIcon = false;
        } else {
            holder.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
            holder.isShowingPlayIcon = true;
        }

        holder.btnPlayPause.setOnClickListener(v -> {
            // Alternar el icono inmediatamente
            holder.isShowingPlayIcon = !holder.isShowingPlayIcon;
            if (holder.isShowingPlayIcon) {
                // Cambiar a play (está pausado)
                holder.btnPlayPause.setImageResource(android.R.drawable.ic_media_play);
                // Detener el runnable
                holder.stopTimer();
            } else {
                // Cambiar a pause (está corriendo)
                holder.btnPlayPause.setImageResource(android.R.drawable.ic_media_pause);
            }

            if (listener != null) {
                listener.onTimerToggle(habit);
            }
        });

        holder.btnResetTimer.setOnClickListener(v -> {
            if (listener != null) {
                listener.onTimerReset(habit);
            }
        });

        // Botón de eliminar
        holder.btnDelete.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitDelete(habit);
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

    // Formatear milisegundos a MM:SS (para cuenta regresiva)
    private String formatTimeMinutes(long millis) {
        long seconds = millis / 1000;
        long minutes = seconds / 60;
        long secs = seconds % 60;
        return String.format(Locale.getDefault(), "%02d:%02d", minutes, secs);
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
        ImageButton btnDelete;

        NormalViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            checkBox = itemView.findViewById(R.id.checkboxHabit);
            progressBar = itemView.findViewById(R.id.progressBarHabit);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);
            btnDelete = itemView.findViewById(R.id.btnDeleteHabit);
        }
    }

    // ViewHolder para hábitos tipo lista
    static class ListViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArea, tvListProgress;
        RecyclerView rvListItems;
        Button btnAddListItem;
        View cardContainer;
        ListItemAdapter listItemAdapter;
        ImageButton btnDelete;

        ListViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            tvListProgress = itemView.findViewById(R.id.tvListProgress);
            rvListItems = itemView.findViewById(R.id.rvListItems);
            btnAddListItem = itemView.findViewById(R.id.btnAddListItem);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);
            btnDelete = itemView.findViewById(R.id.btnDeleteHabit);

            rvListItems.setLayoutManager(new LinearLayoutManager(itemView.getContext()));
        }
    }

    // ViewHolder para hábitos tipo temporizador
    static class TimerViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArea, tvTimerDisplay;
        ImageButton btnPlayPause;
        Button btnResetTimer;
        View cardContainer;
        ImageButton btnDelete;

        Handler handler;
        Runnable updateRunnable;
        boolean isShowingPlayIcon = true; // Rastrea qué icono se está mostrando

        TimerViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            tvTimerDisplay = itemView.findViewById(R.id.tvTimerDisplay);
            btnPlayPause = itemView.findViewById(R.id.btnPlayPause);
            btnResetTimer = itemView.findViewById(R.id.btnResetTimer);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);
            btnDelete = itemView.findViewById(R.id.btnDeleteHabit);

            handler = new Handler(Looper.getMainLooper());
        }

        void stopTimer() {
            if (handler != null && updateRunnable != null) {
                handler.removeCallbacks(updateRunnable);
            }
        }
    }
}
