package es.ucm.fdi.pad.hahabit.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface HabitCompletionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(HabitCompletion completion);

    @Delete
    void delete(HabitCompletion completion);

    @Query("DELETE FROM habit_completions WHERE habitId = :habitId AND completionDate = :date")
    void deleteByHabitAndDate(int habitId, long date);

    @Query("SELECT * FROM habit_completions WHERE completionDate >= :startDate ORDER BY completionDate ASC")
    LiveData<List<HabitCompletion>> getCompletionsSince(long startDate);

    @Query("SELECT * FROM habit_completions WHERE area = :area AND completionDate >= :startDate ORDER BY completionDate ASC")
    LiveData<List<HabitCompletion>> getCompletionsByAreaSince(String area, long startDate);

    @Query("SELECT DISTINCT area FROM habit_completions")
    LiveData<List<String>> getAllAreas();

    @Query("SELECT DISTINCT area FROM habitos")
    LiveData<List<String>> getAllAreasFromHabits();

    @Query("SELECT COUNT(*) FROM habit_completions WHERE area = :area AND completionDate = :date")
    int getCompletionCountByAreaAndDate(String area, long date);

    @Query("SELECT EXISTS(SELECT 1 FROM habit_completions WHERE habitId = :habitId AND completionDate = :date)")
    boolean existsCompletion(int habitId, long date);
}
