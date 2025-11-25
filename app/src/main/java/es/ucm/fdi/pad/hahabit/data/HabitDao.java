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

    @Query("SELECT * FROM habitos ORDER BY id ASC")
    LiveData<List<Habit>> getAllHabits();

    @Query("SELECT * FROM habitos WHERE id = :habitId")
    LiveData<Habit> getHabitById(int habitId);

    @Query("SELECT * FROM habitos WHERE daysFrequency LIKE '%' || :dayOfWeek || '%' ORDER BY id ASC")
    LiveData<List<Habit>> getHabitsByDay(String dayOfWeek);
}