package es.ucm.fdi.pad.hahabit.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import java.util.Calendar;
import java.util.List;
import es.ucm.fdi.pad.hahabit.data.Habit;
import es.ucm.fdi.pad.hahabit.data.HabitCompletion;
import es.ucm.fdi.pad.hahabit.data.HabitRepository;

public class HomeViewModel extends AndroidViewModel {

    private final HabitRepository repository;
    private final LiveData<List<Habit>> allHabits;
    private final MutableLiveData<Calendar> selectedDate;

    public HomeViewModel(@NonNull Application application) {
        super(application);

        // Crear el repository aquí dentro
        repository = new HabitRepository(application);
        allHabits = repository.getAllHabits();
        selectedDate = new MutableLiveData<>(Calendar.getInstance());
    }

    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
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

            android.util.Log.d("HomeViewModel", "moveWeek: direction=" + direction +
                    ", old=" + current.get(Calendar.DAY_OF_MONTH) + "/" + (current.get(Calendar.MONTH)+1) +
                    ", new=" + newDate.get(Calendar.DAY_OF_MONTH) + "/" + (newDate.get(Calendar.MONTH)+1));

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

    public void markHabitCompleted(Habit habit, boolean isCompleted) {
        habit.setDone(isCompleted);
        repository.update(habit);

        long todayStart = HabitRepository.getTodayStart();

        if (isCompleted) {
            HabitCompletion completion = new HabitCompletion(
                    habit.getId(),
                    habit.getArea(),
                    todayStart
            );
            repository.insertCompletion(completion);
        } else {
            repository.deleteCompletion(habit.getId(), todayStart);
        }
    }
}