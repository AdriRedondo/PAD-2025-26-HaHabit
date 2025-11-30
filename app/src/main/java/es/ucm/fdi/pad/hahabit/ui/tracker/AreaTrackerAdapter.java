package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.content.Context;
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

        public void areaNameHelper(String areaName){
            switch(areaName){
                case "Cocinar":
                    textAreaName.setText(R.string.cooking);
                    break;
                case "Estudio":
                    textAreaName.setText(R.string.study);
                    break;
                case "Deporte":
                    textAreaName.setText(R.string.sports);
                    break;
                case "Otros":
                    textAreaName.setText(R.string.others);
                    break;
                default:
                    textAreaName.setText("Unknown");
            }
        }

        public void bind(HabitArea area) {
            areaNameHelper(area.getAreaName());

            int streak = area.getCurrentStreak();
            String streakText;

            Context context = itemView.getContext();

            if (streak == 1) {
                streakText = streak + " " + context.getString(R.string.dia) + " " + context.getString(R.string.seguido);
            } else {
                streakText = streak + " " + context.getString(R.string.dias) + " " + context.getString(R.string.seguido);
            }

            textStreak.setText(streakText);

            activityCalendar.setActivities(area.getActivities());
        }
    }
}
