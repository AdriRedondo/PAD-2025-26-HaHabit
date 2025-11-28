package es.ucm.fdi.pad.hahabit.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import java.util.Calendar;
import java.util.List;
import es.ucm.fdi.pad.hahabit.data.Habit;
import es.ucm.fdi.pad.hahabit.data.HabitCompletion;
import es.ucm.fdi.pad.hahabit.data.HabitRepository;

public class HomeViewModel extends AndroidViewModel {

    private final HabitRepository repository;
    private final MutableLiveData<Calendar> selectedDate;
    private final LiveData<List<Habit>> pendingHabits;
    private final LiveData<List<Habit>> completedHabits;
    private final LiveData<Boolean> isSelectedDateToday;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        repository = new HabitRepository(application);
        selectedDate = new MutableLiveData<>(Calendar.getInstance());

        // Obtener hábitos pendientes directamente de la BD con la query
        pendingHabits = Transformations.switchMap(selectedDate, date -> {
            int dayOfWeek = getDayOfWeekNumber(date);
            long dateStart = HabitRepository.getStartOfDay(date);
            android.util.Log.d("HomeViewModel", "Obteniendo pendientes para día: " + dayOfWeek + ", fecha: " + dateStart);
            return repository.getPendingHabitsByDay(dayOfWeek, dateStart);
        });

        // Obtener hábitos completados directamente de la BD con la query
        completedHabits = Transformations.switchMap(selectedDate, date -> {
            int dayOfWeek = getDayOfWeekNumber(date);
            long dateStart = HabitRepository.getStartOfDay(date);
            android.util.Log.d("HomeViewModel", "Obteniendo completados para día: " + dayOfWeek + ", fecha: " + dateStart);
            return repository.getCompletedHabitsByDay(dayOfWeek, dateStart);
        });

        // Detectar si la fecha seleccionada es hoy
        isSelectedDateToday = Transformations.map(selectedDate, date -> {
            if (date == null) return true;
            Calendar today = Calendar.getInstance();
            return isSameDay(date, today);
        });
    }

    private boolean isSameDay(Calendar date1, Calendar date2) {
        return date1.get(Calendar.YEAR) == date2.get(Calendar.YEAR) &&
               date1.get(Calendar.DAY_OF_YEAR) == date2.get(Calendar.DAY_OF_YEAR);
    }

    // Convertir Calendar.DAY_OF_WEEK a nuestro formato (1=lunes, 7=domingo)
    private int getDayOfWeekNumber(Calendar date) {
        int calendarDay = date.get(Calendar.DAY_OF_WEEK);
        // Calendar: SUNDAY=1, MONDAY=2, ..., SATURDAY=7
        // Nosotros: LUNES=1, MARTES=2, ..., DOMINGO=7
        if (calendarDay == Calendar.SUNDAY) {
            return 7;
        } else {
            return calendarDay - 1;
        }
    }

    public LiveData<List<Habit>> getPendingHabits() {
        return pendingHabits;
    }

    public LiveData<List<Habit>> getCompletedHabits() {
        return completedHabits;
    }

    public LiveData<Boolean> getIsSelectedDateToday() {
        return isSelectedDateToday;
    }

    public LiveData<Calendar> getSelectedDate() {
        return selectedDate;
    }

    public void setSelectedDate(Calendar date) {
        selectedDate.setValue(date);
    }

    public void moveWeek(int direction) {
        Calendar current = selectedDate.getValue();
        if (current != null) {
            Calendar newDate = Calendar.getInstance();
            newDate.setTimeInMillis(current.getTimeInMillis());
            newDate.add(Calendar.WEEK_OF_YEAR, direction);
            selectedDate.setValue(newDate);
        }
    }

    public void insert(Habit habit) {
        repository.insert(habit);
    }

    public void update(Habit habit) {
        repository.update(habit);
    }

    public void delete(Habit habit) {
        repository.delete(habit);
    }

    // Método para probar - insertar hábitos de prueba
    public void insertTestHabit() {
        Habit testHabit = new Habit(
                "Hacer ejercicio",
                "Deporte",
                "diario",
                "normal",  // habitType
                0.0,
                false,
                1,
                "1,2,3,4,5",  // Lunes a viernes
                5,
                false,  // reminderEnabled
                "",      // reminderTime
                System.currentTimeMillis(),
                0,
                0,
                0
        );
        repository.insert(testHabit);

        Habit testHabit2 = new Habit(
                "Estudiar Android",
                "Estudio",
                "diario",
                "normal",  // habitType
                0.0,
                false,
                1,
                "1,2,3,4,5,6,7",  // Todos los días
                7,
                false,  // reminderEnabled
                "",      // reminderTime
                System.currentTimeMillis(),
                0,
                0,
                0
        );
        repository.insert(testHabit2);

        Habit testHabit3 = new Habit(
                "Cocinar algo nuevo",
                "Cocina",
                "semanal",
                "normal",  // habitType
                0.0,
                false,
                2,
                "6,7",  // Fines de semana
                2,
                false,  // reminderEnabled
                "",      // reminderTime
                System.currentTimeMillis(),
                0,
                0,
                0
        );
        repository.insert(testHabit3);
    }

    public void markHabitCompleted(Habit habit, boolean isCompleted) {
        // NO modificamos el campo done del hábito, solo gestionamos el HabitCompletion
        Calendar current = selectedDate.getValue();
        long dateStart = current != null ? HabitRepository.getStartOfDay(current) : HabitRepository.getTodayStart();

        if (isCompleted) {
            HabitCompletion completion = new HabitCompletion(
                    habit.getId(),
                    habit.getArea(),
                    dateStart
            );
            repository.insertCompletion(completion);
        } else {
            repository.deleteCompletion(habit.getId(), dateStart);
        }
    }

    // MÉTODO DE PRUEBA - Crear hábitos de ejemplo de cada tipo
    public void createTestHabits() {
        // 1. Hábito normal (tipo checkbox)
        Habit normalHabit = new Habit(
                "Hacer ejercicio",
                "Deporte",
                "diario",
                "normal",  // habitType
                0.0,
                false,
                0,  // typeFrequency: 0 = semanal
                "1,2,3,4,5",  // Lunes a viernes
                5,
                false,
                "",
                System.currentTimeMillis(),
                0,
                0,
                0
        );
        repository.insert(normalHabit);

        // 2. Hábito tipo lista
        Habit listHabit = new Habit(
                "Lista de compras",
                "Cocinar",
                "diario",
                "list",  // habitType
                0.0,
                false,
                0,
                "1,2,3,4,5,6,7",  // Todos los días
                7,
                false,
                "",
                System.currentTimeMillis(),
                0,
                0,
                0
        );
        // Agregar items de ejemplo en formato JSON
        listHabit.setListItems("[{\"text\":\"Comprar leche\",\"completed\":false},{\"text\":\"Comprar pan\",\"completed\":false},{\"text\":\"Comprar huevos\",\"completed\":true}]");
        repository.insert(listHabit);

        // 3. Hábito tipo temporizador
        Habit timerHabit = new Habit(
                "Meditar",
                "Salud",
                "diario",
                "timer",  // habitType
                0.0,
                false,
                0,
                "1,2,3,4,5,6,7",  // Todos los días
                7,
                false,
                "",
                System.currentTimeMillis(),
                0,
                0,
                0
        );
        // Configurar temporizador con 10 minutos de objetivo (600000 ms)
        timerHabit.setTimerElapsed(0L);
        timerHabit.setTimerRunning(false);
        timerHabit.setTimerTarget(600000L);  // 10 minutos
        repository.insert(timerHabit);

        android.util.Log.d("HomeViewModel", "Hábitos de prueba creados: normal, lista y temporizador");
    }

    // Métodos para manejar el temporizador
    public void toggleTimer(Habit habit) {
        if (habit.isTimerRunning()) {
            // Pausar el temporizador
            long currentTime = System.currentTimeMillis();
            long additionalTime = currentTime - habit.getTimerStartTime();
            habit.setTimerElapsed(habit.getTimerElapsed() + additionalTime);
            habit.setTimerRunning(false);
            habit.setTimerStartTime(null);
        } else {
            // Iniciar/reanudar el temporizador
            habit.setTimerRunning(true);
            habit.setTimerStartTime(System.currentTimeMillis());
        }
        update(habit);
        android.util.Log.d("HomeViewModel", "Timer toggled: " + habit.getTitle() + " - Running: " + habit.isTimerRunning());
    }

    public void resetTimer(Habit habit) {
        habit.setTimerElapsed(0L);
        habit.setTimerRunning(false);
        habit.setTimerStartTime(null);
        update(habit);
        android.util.Log.d("HomeViewModel", "Timer reset: " + habit.getTitle());
    }
}