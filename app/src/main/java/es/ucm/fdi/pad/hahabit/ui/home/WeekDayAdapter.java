package es.ucm.fdi.pad.hahabit.ui.home;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import es.ucm.fdi.pad.hahabit.R;

public class WeekDayAdapter extends RecyclerView.Adapter<WeekDayAdapter.DayViewHolder> {

    private final List<Calendar> weekDays = new ArrayList<>();
    private Calendar selectedDate;
    private final OnDayClickListener listener;

    private static final String[] DAY_NAMES = {"LUN", "MAR", "MIÉ", "JUE", "VIE", "SÁB", "DOM"};

    public interface OnDayClickListener {
        void onDayClick(Calendar date);
    }

    public WeekDayAdapter(OnDayClickListener listener) {
        this.listener = listener;
        this.selectedDate = Calendar.getInstance();
    }

    public void setWeekFromDate(Calendar centerDate) {
        weekDays.clear();

        Calendar cal = (Calendar) centerDate.clone();

        // Log para depurar
        android.util.Log.d("WeekDayAdapter", "setWeekFromDate recibido: " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1));

        // Calcular el lunes de esta semana
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        int daysToSubtract;
        if (dayOfWeek == Calendar.SUNDAY) {
            daysToSubtract = 6;
        } else {
            daysToSubtract = dayOfWeek - Calendar.MONDAY;
        }
        cal.add(Calendar.DAY_OF_MONTH, -daysToSubtract);

        android.util.Log.d("WeekDayAdapter", "Lunes calculado: " + cal.get(Calendar.DAY_OF_MONTH) + "/" + (cal.get(Calendar.MONTH)+1));

        // Añadir los 7 días (lunes a domingo)
        for (int i = 0; i < 7; i++) {
            Calendar day = (Calendar) cal.clone();
            weekDays.add(day);
            android.util.Log.d("WeekDayAdapter", "Día añadido: " + day.get(Calendar.DAY_OF_MONTH));
            cal.add(Calendar.DAY_OF_MONTH, 1);
        }

        notifyDataSetChanged();
    }

    public void setSelectedDate(Calendar date) {
        this.selectedDate = (Calendar) date.clone();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public DayViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_week_day, parent, false);
        return new DayViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull DayViewHolder holder, int position) {
        Calendar day = weekDays.get(position);

        // Nombre del día (LUN, MAR, etc.) - position corresponde a lunes=0, martes=1, etc.
        holder.tvDayName.setText(DAY_NAMES[position]);

        // Número del día del mes
        holder.tvDayNumber.setText(String.valueOf(day.get(Calendar.DAY_OF_MONTH)));

        // Verificar si es el día seleccionado o el día de hoy
        boolean isSelected = isSameDay(day, selectedDate);
        boolean isToday = isSameDay(day, Calendar.getInstance());

        if (isSelected) {
            holder.itemView.setBackgroundResource(R.drawable.bg_day_selected);
            holder.tvDayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.white));
        } else if (isToday) {
            holder.itemView.setBackgroundResource(R.drawable.bg_day_today);
            holder.tvDayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.primary));
        } else {
            holder.itemView.setBackgroundResource(R.drawable.bg_day_normal);
            holder.tvDayName.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_secondary));
            holder.tvDayNumber.setTextColor(ContextCompat.getColor(holder.itemView.getContext(), R.color.text_primary));
        }

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onDayClick(day);
            }
        });
    }

    @Override
    public int getItemCount() {
        return weekDays.size();
    }

    private boolean isSameDay(Calendar cal1, Calendar cal2) {
        if (cal1 == null || cal2 == null) return false;
        return cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR);
    }

    static class DayViewHolder extends RecyclerView.ViewHolder {
        TextView tvDayName, tvDayNumber;

        DayViewHolder(@NonNull View itemView) {
            super(itemView);
            tvDayName = itemView.findViewById(R.id.tvDayName);
            tvDayNumber = itemView.findViewById(R.id.tvDayNumber);
        }
    }
}