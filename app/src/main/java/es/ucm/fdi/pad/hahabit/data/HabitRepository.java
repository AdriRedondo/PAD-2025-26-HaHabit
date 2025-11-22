package es.ucm.fdi.pad.hahabit.data;

import android.app.Application;

import androidx.lifecycle.LiveData;

import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Repositorio que actúa como intermediario entre el ViewModel y la base de datos para la entidad Hábito.
 */
public class HabitRepository {

    private final HabitDao habitDao;
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
    public LiveData<Habit> getHabitById(int id) {
        return habitDao.getHabitById(id);
    }

    public void update(Habit habit) {
        executorService.execute(() -> habitDao.update(habit));
    }

    public void delete(Habit habit) {
        executorService.execute(() -> habitDao.delete(habit));
    }


}
