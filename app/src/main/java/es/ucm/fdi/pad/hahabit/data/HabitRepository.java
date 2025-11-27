package es.ucm.fdi.pad.hahabit.data;

import android.app.Application;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio que actúa como intermediario entre el ViewModel y la base de datos para la entidad Hábito.
 */
public class HabitRepository {

    private final HabitDao habitDao;
    private final HabitCompletionDao completionDao;
    private final LiveData<List<Habit>> allHabits;

    // ExecutorService para ejecutar tareas en un hilo secundario.
    // Room no permite operaciones de escritura en el hilo principal
    private final ExecutorService executorService;

    /**
     * Constructor del repositorio.
     *
     * @param application Contexto de la aplicación, necesario para acceder a la db
     */
    public HabitRepository(Application application) {
        HabitDatabase database = HabitDatabase.getDatabase(application);
        habitDao = database.habitDao();
        completionDao = database.habitCompletionDao();

        // Esto así porque el livedata ya se va actualizando, asi que mejor no crearlo de nuevo con getAllHabits() cada vez.
        allHabits = habitDao.getAllHabits();

        // Se crea un executor con un solo hilo para las operaciones de escritura
        executorService = Executors.newSingleThreadExecutor();
    }

    /**
     * Inserta un nuevo hábito en la db.
     *
     * Se ejecuta en un hilo secundario para no bloquear la interfaz.
     *
     * @param habit El hábito a insertar
     */
    public void insert(Habit habit) {
        executorService.execute(() -> habitDao.insert(habit));
    }

    /**
     * Obtiene todos los hábitos que haya en la db.
     *
     * Devuelve un LiveData, util para que se actualice sola la interfaz y que
     * no haga refrescar manualmente.
     *
     * @return LiveData con la lista de todos los hábitos
     */
    public LiveData<List<Habit>> getAllHabits() {
        return allHabits;
    }


    public LiveData<List<Habit>> getHabitsByDay(int dayOfWeek) {
        // dayOfWeek: 1=lunes, 2=martes, ..., 7=domingo
        return habitDao.getHabitsByDay(String.valueOf(dayOfWeek));
    }

    public LiveData<List<Habit>> getPendingHabitsByDay(int dayOfWeek, long date) {
        //return habitDao.getPendingHabitsByDay(String.valueOf(dayOfWeek), date);
        LiveData<List<Habit>> source = habitDao.getPendingHabitsByDay(String.valueOf(dayOfWeek), date);

        MediatorLiveData<List<Habit>> result = new MediatorLiveData<>();
        result.addSource(source, habits -> {
            if (habits != null) {
                result.setValue(filterHabitsByInterval(habits, date));
            }
        });

        return result;
    }

    public LiveData<List<Habit>> getCompletedHabitsByDay(int dayOfWeek, long date) {
        //return habitDao.getCompletedHabitsByDay(String.valueOf(dayOfWeek), date);
        LiveData<List<Habit>> source = habitDao.getCompletedHabitsByDay(String.valueOf(dayOfWeek), date);

        MediatorLiveData<List<Habit>> result = new MediatorLiveData<>();
        result.addSource(source, habits -> {
            if (habits != null) {
                result.setValue(filterHabitsByInterval(habits, date));
            }
        });

        return result;
    }

    // Método para filtrar hábitos con intervalo
    private List<Habit> filterHabitsByInterval(List<Habit> habits, long selectedDate) {
        List<Habit> filtered = new ArrayList<>();

        for (Habit habit : habits) {
            // Si es semanal (typeFrequency = 0), siempre incluir
            if (habit.getTypeFrequency() == 0) {
                filtered.add(habit);
            }
            // Si es por intervalo (typeFrequency = 1), verificar si debe mostrarse hoy
            else if (habit.getTypeFrequency() == 1) {
                if (shouldShowHabitOnDate(habit, selectedDate)) {
                    filtered.add(habit);
                }
            }
        }

        return filtered;
    }

    // Verifico si un habito con intervalo debe mostrarse en una fecha específica
    private boolean shouldShowHabitOnDate(Habit habit, long selectedDate) {
        Long startDate = habit.getStartDate();
        Integer interval = habit.getFrequency();

        if (startDate == null || interval == null || interval <= 0) {
            return false;
        }

        // Normalizo ambas fechas a medianoche
        long startMillis = getStartOfDayFromMillis(startDate);
        long selectedMillis = getStartOfDayFromMillis(selectedDate);

        // Calcular días transcurridos desde el inicio
        long diffMillis = selectedMillis - startMillis;
        long daysPassed = diffMillis / (24 * 60 * 60 * 1000);

        // Si es múltiplo del intervalo (o es el día de inicio), mostrar
        return daysPassed >= 0 && daysPassed % interval == 0;
    }

    //Normalizo una fecha a medianoche
    private long getStartOfDayFromMillis(long timeInMillis) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(timeInMillis);
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }
    public LiveData<Habit> getHabitById(int id) {
        return habitDao.getHabitById(id);
    }

    public void update(Habit habit) {
        executorService.execute(() -> habitDao.update(habit));
    }

    public void delete(Habit habit) {
        executorService.execute(() -> habitDao.delete(habit));
    }


    // ********* metodos de HabitCompletion **************
    public void insertCompletion(HabitCompletion completion) {
        executorService.execute(() -> completionDao.insert(completion));
    }

    public void deleteCompletion(int habitId, long date) {
        executorService.execute(() -> completionDao.deleteByHabitAndDate(habitId, date));
    }

    public LiveData<List<HabitCompletion>> getCompletionsSince(long startDate) {
        return completionDao.getCompletionsSince(startDate);
    }

    public LiveData<List<HabitCompletion>> getCompletionsByAreaSince(String area, long startDate) {
        return completionDao.getCompletionsByAreaSince(area, startDate);
    }

    public LiveData<List<String>> getAllAreasFromHabits() {
        return completionDao.getAllAreasFromHabits();
    }

    public static long getStartOfDay(Calendar calendar) {
        Calendar cal = (Calendar) calendar.clone();
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        return cal.getTimeInMillis();
    }

    public static long getTodayStart() {
        return getStartOfDay(Calendar.getInstance());
    }
}
