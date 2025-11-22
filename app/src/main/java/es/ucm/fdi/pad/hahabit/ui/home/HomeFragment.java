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
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import es.ucm.fdi.pad.hahabit.R;
import es.ucm.fdi.pad.hahabit.data.Habit;

public class HomeFragment extends Fragment implements WeekDayAdapter.OnDayClickListener {

    private HomeViewModel viewModel;
    private WeekDayAdapter weekDayAdapter;
    private HabitAdapter habitAdapter;

    private MaterialCardView cardWeekCalendar, cardFullCalendar;
    private RecyclerView rvWeekDays, rvHabits;
    private CalendarView calendarView;
    private TextView tvMonthYear;
    private View emptyState;
    private FloatingActionButton fabAddHabit;
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
        calendarView = view.findViewById(R.id.calendarView);
        tvMonthYear = view.findViewById(R.id.tvMonthYear);
        emptyState = view.findViewById(R.id.emptyState);
        fabAddHabit = view.findViewById(R.id.fabAddHabit);
        btnPrevWeek = view.findViewById(R.id.btnPrevWeek);
        btnNextWeek = view.findViewById(R.id.btnNextWeek);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        viewModel.getSelectedDate().observe(getViewLifecycleOwner(), this::updateWeekDisplay);

        viewModel.getAllHabits().observe(getViewLifecycleOwner(), habits -> {
            if (habits != null && !habits.isEmpty()) {
                habitAdapter.submitList(habits);
                rvHabits.setVisibility(View.VISIBLE);
                emptyState.setVisibility(View.GONE);
            } else {
                rvHabits.setVisibility(View.GONE);
                emptyState.setVisibility(View.VISIBLE);
            }
        });
    }

    private void setupWeekCalendar() {
        weekDayAdapter = new WeekDayAdapter(this);

        GridLayoutManager layoutManager = new GridLayoutManager(requireContext(), 7);
        rvWeekDays.setLayoutManager(layoutManager);
        rvWeekDays.setAdapter(weekDayAdapter);

        weekDayAdapter.setWeekFromDate(Calendar.getInstance());
    }

    private void setupHabitsList() {
        habitAdapter = new HabitAdapter();
        habitAdapter.setOnHabitClickListener(new HabitAdapter.OnHabitClickListener() {
            @Override
            public void onHabitClick(Habit habit) {
                // TODO: Abrir detalle del hábito
            }

            @Override
            public void onHabitChecked(Habit habit, boolean isChecked) {
                habit.setDone(isChecked);
                viewModel.update(habit);
            }
        });

        rvHabits.setLayoutManager(new LinearLayoutManager(requireContext()));
        rvHabits.setAdapter(habitAdapter);
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

        fabAddHabit.setOnClickListener(v -> {
            // TODO: Abrir pantalla de crear hábito
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