package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.HabitArea;

public class AreaTrackerAdapter extends RecyclerView.Adapter<AreaTrackerAdapter.AreaViewHolder> {

    private List<HabitArea> habitAreas = new ArrayList<>();

    @NonNull
    @Override
    public AreaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_area_tracker, parent, false);
        return new AreaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull AreaViewHolder holder, int position) {
        HabitArea area = habitAreas.get(position);
        holder.bind(area);
    }

    @Override
    public int getItemCount() {
        return habitAreas.size();
    }

    public void setHabitAreas(List<HabitArea> areas) {
        this.habitAreas = areas;
        notifyDataSetChanged();
    }

    static class AreaViewHolder extends RecyclerView.ViewHolder {
        private final TextView textAreaName;
        private final TextView textStreak;
        private final ActivityCalendarView activityCalendar;

        public AreaViewHolder(@NonNull View itemView) {
            super(itemView);
            textAreaName = itemView.findViewById(R.id.text_area_name);
            textStreak = itemView.findViewById(R.id.text_streak);
            activityCalendar = itemView.findViewById(R.id.activity_calendar);
        }

        public void bind(HabitArea area) {
            textAreaName.setText(area.getAreaName());

            String streakText = area.getCurrentStreak() + " día" +
                    (area.getCurrentStreak() != 1 ? "s" : "") + " seguido" +
                    (area.getCurrentStreak() != 1 ? "s" : "");
            textStreak.setText(streakText);

            activityCalendar.setActivities(area.getActivities());
        }
    }
}
