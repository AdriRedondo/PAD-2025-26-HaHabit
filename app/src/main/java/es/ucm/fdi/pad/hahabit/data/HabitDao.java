package es.ucm.fdi.pad.hahabit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

import java.util.List;

@Dao
public interface HabitDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Habit habit);

    @Update
    void update(Habit habit);

    @Delete
    void delete(Habit habit);

    @Query("DELETE FROM habitos")
    void deleteAllHabits();

    @Query("SELECT * FROM habitos WHERE isDeleted = 0 ORDER BY id ASC")
    LiveData<List<Habit>> getAllHabits();

    @Query("SELECT * FROM habitos WHERE id = :habitId")
    LiveData<Habit> getHabitById(int habitId);

    @Query("SELECT * FROM habitos WHERE daysFrequency LIKE '%' || :dayOfWeek || '%' AND isDeleted = 0 ORDER BY id ASC")
    LiveData<List<Habit>> getHabitsByDay(String dayOfWeek);

    @Query("SELECT h.* FROM habitos h " + "WHERE (" +
            "  (h.typeFrequency = 0 AND h.daysFrequency LIKE '%' || :dayOfWeek || '%') " +
            "  OR h.typeFrequency = 1" +
            ") " +
           "AND h.isDeleted = 0 AND NOT EXISTS (SELECT 1 FROM habit_completions hc " +
           "WHERE hc.habitId = h.id AND hc.completionDate = :date) " +
           "ORDER BY h.id ASC")
    LiveData<List<Habit>> getPendingHabitsByDay(String dayOfWeek, long date);

    @Query("SELECT h.* FROM habitos h " +
            "INNER JOIN habit_completions hc ON h.id = hc.habitId " +
            "WHERE (" +
            "  (h.typeFrequency = 0 AND h.daysFrequency LIKE '%' || :dayOfWeek || '%') " +
            "  OR h.typeFrequency = 1" +
            ") " +
            "AND h.isDeleted = 0 AND hc.completionDate = :date " +
            "ORDER BY h.id ASC")
    LiveData<List<Habit>> getCompletedHabitsByDay(String dayOfWeek, long date);
}