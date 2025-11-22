package es.ucm.fdi.pad.hahabit;

import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.CalendarView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.card.MaterialCardView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;
import es.ucm.fdi.pad.hahabit.ui.home.HabitAdapter;
import es.ucm.fdi.pad.hahabit.ui.home.WeekDayAdapter;
import es.ucm.fdi.pad.hahabit.data.Habit;
import es.ucm.fdi.pad.hahabit.ui.home.HomeViewModel;

public class MainActivity extends AppCompatActivity implements WeekDayAdapter.OnDayClickListener {

    private HomeViewModel viewModel;
    private WeekDayAdapter weekDayAdapter;
    private HabitAdapter habitAdapter;

    private MaterialCardView cardWeekCalendar, cardFullCalendar;
    private RecyclerView rvWeekDays, rvHabits;
    private CalendarView calendarView;
    private TextView tvMonthYear;
    private View emptyState;
    private FloatingActionButton fabAddHabit;

    private boolean isCalendarExpanded = false;
    private GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initViews();
        setupViewModel();
        setupWeekCalendar();
        setupHabitsList();
        setupGestureDetector();
        setupClickListeners();
    }

    private void initViews() {
        cardWeekCalendar = findViewById(R.id.cardWeekCalendar);
        cardFullCalendar = findViewById(R.id.cardFullCalendar);
        rvWeekDays = findViewById(R.id.rvWeekDays);
        rvHabits = findViewById(R.id.rvHabits);
        calendarView = findViewById(R.id.calendarView);
        tvMonthYear = findViewById(R.id.tvMonthYear);
        emptyState = findViewById(R.id.emptyState);
        fabAddHabit = findViewById(R.id.fabAddHabit);
    }

    private void setupViewModel() {
        viewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Observar cambios en la fecha seleccionada
        viewModel.getSelectedDate().observe(this, this::updateWeekDisplay);

        // Observar cambios en los hábitos
        viewModel.getAllHabits().observe(this, habits -> {
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

        // GridLayoutManager con 7 columnas para mostrar todos los días
        GridLayoutManager layoutManager = new GridLayoutManager(this, 7);
        rvWeekDays.setLayoutManager(layoutManager);
        rvWeekDays.setAdapter(weekDayAdapter);

        // Inicializar con la semana actual
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

        rvHabits.setLayoutManager(new LinearLayoutManager(this));
        rvHabits.setAdapter(habitAdapter);
    }

    private void setupGestureDetector() {
        gestureDetector = new GestureDetector(this, new GestureDetector.SimpleOnGestureListener() {
            private static final int SWIPE_THRESHOLD = 100;
            private static final int SWIPE_VELOCITY_THRESHOLD = 100;

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                if (e1 == null || e2 == null) return false;

                float diffX = e2.getX() - e1.getX();

                if (Math.abs(diffX) > SWIPE_THRESHOLD && Math.abs(velocityX) > SWIPE_VELOCITY_THRESHOLD) {
                    if (diffX > 0) {
                        // Swipe derecha -> semana anterior
                        viewModel.moveWeek(-1);
                    } else {
                        // Swipe izquierda -> semana siguiente
                        viewModel.moveWeek(1);
                    }
                    return true;
                }
                return false;
            }
        });

        cardWeekCalendar.setOnTouchListener((v, event) -> {
            gestureDetector.onTouchEvent(event);
            return true;
        });
    }

    private void setupClickListeners() {
        // Click en la tarjeta para expandir/contraer calendario
        tvMonthYear.setOnClickListener(v -> toggleCalendar());

        // Selección de fecha en el calendario completo
        calendarView.setOnDateChangeListener((view, year, month, dayOfMonth) -> {
            Calendar selected = Calendar.getInstance();
            selected.set(year, month, dayOfMonth);
            viewModel.setSelectedDate(selected);
            toggleCalendar();
        });

        // FAB para añadir hábito
        fabAddHabit.setOnClickListener(v -> {
            // TODO: Abrir pantalla de crear hábito
        });
    }

    private void toggleCalendar() {
        isCalendarExpanded = !isCalendarExpanded;
        cardFullCalendar.setVisibility(isCalendarExpanded ? View.VISIBLE : View.GONE);
    }

    private void updateWeekDisplay(Calendar date) {
        // Actualizar el texto del mes y año
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", new Locale("es", "ES"));
        String monthYear = sdf.format(date.getTime());
        tvMonthYear.setText(monthYear.substring(0, 1).toUpperCase() + monthYear.substring(1));

        // Actualizar la semana mostrada
        weekDayAdapter.setWeekFromDate(date);
        weekDayAdapter.setSelectedDate(date);

        // Actualizar el CalendarView
        calendarView.setDate(date.getTimeInMillis());
    }

    @Override
    public void onDayClick(Calendar date) {
        viewModel.setSelectedDate(date);
    }
}