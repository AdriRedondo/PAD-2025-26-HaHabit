package es.ucm.fdi.pad.hahabit.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.DiffUtil;
import androidx.recyclerview.widget.ListAdapter;
import androidx.recyclerview.widget.RecyclerView;
import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.Habit;

public class HabitAdapter extends ListAdapter<Habit, HabitAdapter.HabitViewHolder> {

    private OnHabitClickListener listener;

    public interface OnHabitClickListener {
        void onHabitClick(Habit habit);
        void onHabitChecked(Habit habit, boolean isChecked);
    }

    public HabitAdapter() {
        super(DIFF_CALLBACK);
    }

    private static final DiffUtil.ItemCallback<Habit> DIFF_CALLBACK = new DiffUtil.ItemCallback<Habit>() {
        @Override
        public boolean areItemsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getId() == newItem.getId();
        }

        @Override
        public boolean areContentsTheSame(@NonNull Habit oldItem, @NonNull Habit newItem) {
            return oldItem.getTitle().equals(newItem.getTitle()) &&
                    oldItem.isDone() == newItem.isDone() &&
                    oldItem.getArea().equals(newItem.getArea());
        }
    };

    public void setOnHabitClickListener(OnHabitClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public HabitViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_habit, parent, false);
        return new HabitViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull HabitViewHolder holder, int position) {
        Habit habit = getItem(position);

        holder.tvTitle.setText(habit.getTitle());
        holder.tvArea.setText(habit.getArea());
        holder.checkBox.setChecked(habit.isDone());

        // Configurar la barra de progreso
        if (habit.getProgress() != null && habit.getProgress() > 0) {
            holder.progressBar.setVisibility(View.VISIBLE);
            holder.progressBar.setProgress((int) (habit.getProgress() * 100));
        } else {
            holder.progressBar.setVisibility(View.GONE);
        }

        // Cambiar fondo según el área
        int backgroundRes = getBackgroundForArea(habit.getArea());
        holder.cardContainer.setBackgroundResource(backgroundRes);

        // Click en el item completo
        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onHabitClick(habit);
            }
        });

        // Click en el checkbox
        holder.checkBox.setOnCheckedChangeListener(null);
        holder.checkBox.setChecked(habit.isDone());
        holder.checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (listener != null && buttonView.isPressed()) {
                listener.onHabitChecked(habit, isChecked);
            }
        });
    }

    private int getBackgroundForArea(String area) {
        if (area == null) return R.drawable.bg_habit_otros;

        switch (area.toLowerCase()) {
            case "cocina":
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

    static class HabitViewHolder extends RecyclerView.ViewHolder {
        TextView tvTitle, tvArea;
        CheckBox checkBox;
        ProgressBar progressBar;
        View cardContainer;

        HabitViewHolder(@NonNull View itemView) {
            super(itemView);
            tvTitle = itemView.findViewById(R.id.tvHabitTitle);
            tvArea = itemView.findViewById(R.id.tvHabitArea);
            checkBox = itemView.findViewById(R.id.checkboxHabit);
            progressBar = itemView.findViewById(R.id.progressBarHabit);
            cardContainer = itemView.findViewById(R.id.habitCardContainer);
        }
    }
}