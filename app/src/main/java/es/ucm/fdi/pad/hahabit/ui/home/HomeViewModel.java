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
import es.ucm.fdi.pad.hahabit.data.HabitRepository;

public class HomeViewModel extends AndroidViewModel {

    private final HabitRepository repository;
    private final MutableLiveData<Calendar> selectedDate;
    private final LiveData<List<Habit>> habitsForSelectedDay;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        repository = new HabitRepository(application);
        selectedDate = new MutableLiveData<>(Calendar.getInstance());

        // Cuando cambia la fecha seleccionada, actualizar los hábitos filtrados
        habitsForSelectedDay = Transformations.switchMap(selectedDate, date -> {
            int dayOfWeek = getDayOfWeekNumber(date);
            android.util.Log.d("HomeViewModel", "Filtrando por día: " + dayOfWeek);
            return repository.getHabitsByDay(dayOfWeek);
        });
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

    public LiveData<List<Habit>> getHabitsForSelectedDay() {
        return habitsForSelectedDay;
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
                0.0,
                false,
                1,
                "1,2,3,4,5",  // Lunes a viernes
                5
        );
        repository.insert(testHabit);

        Habit testHabit2 = new Habit(
                "Estudiar Android",
                "Estudio",
                "diario",
                0.0,
                false,
                1,
                "1,2,3,4,5,6,7",  // Todos los días
                7
        );
        repository.insert(testHabit2);

        Habit testHabit3 = new Habit(
                "Cocinar algo nuevo",
                "Cocina",
                "semanal",
                0.0,
                false,
                2,
                "6,7",  // Fines de semana
                2
        );
        repository.insert(testHabit3);
    }
}