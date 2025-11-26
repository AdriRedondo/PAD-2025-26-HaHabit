package es.ucm.fdi.pad.hahabit.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CalendarView;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.Habit;

public class HomeFragment extends Fragment implements WeekDayAdapter.OnDayClickListener {

    private HomeViewModel viewModel;
    private WeekDayAdapter weekDayAdapter;
    private HabitAdapter habitAdapter;
    private HabitAdapter completedAdapter;

    private MaterialCardView cardWeekCalendar, cardFullCalendar;
    private RecyclerView rvWeekDays, rvHabits, rvCompletedHabits;
    private CalendarView calendarView;
    private TextView tvMonthYear, tvPendingTitle, tvCompletedTitle;
    private View emptyState;
    private ImageButton btnPrevWeek, btnNextWeek;

    private boolean isCalendarExpanded = false;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_home, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initViews(view);
        setupViewModel();
        setupWeekCalendar();
        setupHabitsList();
        setupClickListeners();
    }

    private void initViews(View view) {
        cardWeekCalendar = view.findViewById(R.id.cardWeekCalendar);
        cardFullCalendar = view.findViewById(R.id.cardFullCalendar);
        rvWeekDays = view.findViewById(R.id.rvWeekDays);
        rvHabits = view.findViewById(R.id.rvHabits);
        rvCompletedHabits = view.findViewById(R.id.rvCompletedHabits);
        calendarView = view.findViewById(R.id.calendarView);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        tvPendingTitle = view.findViewById(R.id.tvPendingTitle);
        tvCompletedTitle = view.findViewById(R.id.tvCompletedTitle);
        emptyState = view.findViewById(R.id.emptyState);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), this::updateWeekDisplay);

        // Observar hábitos pendientes
        viewModel.getPendingHabits().observe(getViewLifecycleOwner(), pendingHabits -> {
            android.util.Log.d("HomeFragment", "Hábitos pendientes: " + (pendingHabits != null ? pendingHabits.size() : 0));
            if (pendingHabits != null && !pendingHabits.isEmpty()) {
                habitAdapter.submitList(pendingHabits);
                rvHabits.setVisibility(View.VISIBLE);
                tvPendingTitle.setVisibility(View.VISIBLE);
            } else {
                habitAdapter.submitList(null);
                rvHabits.setVisibility(View.GONE);
                tvPendingTitle.setVisibility(View.GONE);
            }
            updateEmptyState();
        });

        // Observar hábitos completados
        viewModel.getCompletedHabits().observe(getViewLifecycleOwner(), completedHabits -> {
            android.util.Log.d("HomeFragment", "Hábitos completados: " + (completedHabits != null ? completedHabits.size() : 0));
            if (completedHabits != null && !completedHabits.isEmpty()) {
                completedAdapter.submitList(completedHabits);
                rvCompletedHabits.setVisibility(View.VISIBLE);
                tvCompletedTitle.setVisibility(View.VISIBLE);
            } else {
                completedAdapter.submitList(null);
                rvCompletedHabits.setVisibility(View.GONE);
                tvCompletedTitle.setVisibility(View.GONE);
            }
            updateEmptyState();
        });

        // Observar si la fecha seleccionada es hoy para habilitar/deshabilitar checkboxes
        viewModel.getIsSelectedDateToday().observe(getViewLifecycleOwner(), isToday -> {
            android.util.Log.d("HomeFragment", "¿Es hoy?: " + isToday);
            habitAdapter.setIsToday(isToday != null && isToday);
            completedAdapter.setIsToday(isToday != null && isToday);
        });

    }

    private void updateEmptyState() {
        boolean hasPending = rvHabits.getVisibility() == View.VISIBLE;
        boolean hasCompleted = rvCompletedHabits.getVisibility() == View.VISIBLE;

        if (!hasPending && !hasCompleted) {
            emptyState.setVisibility(View.VISIBLE);
        } else {
            emptyState.setVisibility(View.GONE);
        }
    }

    private void setupWeekCalendar() {
        weekDayAdapter = new WeekDayAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        rvWeekDays.setLayoutManager(layoutManager);
        rvWeekDays.setAdapter(weekDayAdapter);

        weekDayAdapter.setWeekFromDate(Calendar.getInstance());
    }

    private void setupHabitsList() {
        // Adapter para hábitos pendientes (pueden ser marcados)
        habitAdapter = new HabitAdapter(false); // false = no es sección de completados
        habitAdapter.setOnHabitClickListener(new HabitAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(Habit habit) {
                // TODO: Abrir detalle del hábito
            }

            @Override
            public void onHabitChecked(Habit habit, boolean isChecked) {
                viewModel.markHabitCompleted(habit, isChecked);
            }
        });

        rvHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHabits.setAdapter(habitAdapter);

        // Adapter para hábitos completados (NO pueden ser desmarcados)
        completedAdapter = new HabitAdapter(true); // true = es sección de completados, siempre disabled
        completedAdapter.setOnHabitClickListener(new HabitAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(Habit habit) {
                // TODO: Abrir detalle del hábito
            }

            @Override
            public void onHabitChecked(Habit habit, boolean isChecked) {
                // Este listener nunca debería llamarse porque los checkboxes están deshabilitados
                // pero por si acaso:
                viewModel.markHabitCompleted(habit, isChecked);
            }
        });

        rvCompletedHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvCompletedHabits.setAdapter(completedAdapter);
    }

    private void setupClickListeners() {
        tvMonthYear.setOnClickListener(v -> toggleCalendar());

        btnPrevWeek.setOnClickListener(v -> viewModel.moveWeek(-1));
        btnNextWeek.setOnClickListener(v -> viewModel.moveWeek(1));

        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            viewModel.setSelectedDate(selected);
            toggleCalendar();
        });
    }

    private void toggleCalendar() {
        isCalendarExpanded = !isCalendarExpanded;
        cardFullCalendar.setVisibility(isCalendarExpanded ? View.VISIBLE : View.GONE);
    }

    private void updateWeekDisplay(Calendar date) {
        // Log para depurar
        android.util.Log.d("HomeFragment", "updateWeekDisplay: " + date.get(Calendar.DAY_OF_MONTH) + "/" + (date.get(Calendar.MONTH)+1) + "/" + date.get(Calendar.YEAR));

        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String monthYear = sdf.format(date.getTime());
        tvMonthYear.setText(monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1));

        weekDayAdapter.setWeekFromDate(date);
        weekDayAdapter.setSelectedDate(date);

        calendarView.setDate(date.getTimeInMillis());
    }

    @Override
    public void onDayClick(Calendar date) {
        viewModel.setSelectedDate(date);
    }
}