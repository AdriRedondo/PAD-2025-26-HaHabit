package es.ucm.fdi.pad.hahabit.data;

import android.content.Context;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;

@Database(entities = {Habit.class, HabitCompletion.class}, version = 2, exportSchema = false)
public abstract class HabitDatabase extends RoomDatabase {

    public abstract HabitDao habitDao();
    public abstract HabitCompletionDao habitCompletionDao();

    private static volatile HabitDatabase INSTANCE;

    public static HabitDatabase getDatabase(final Context context) {
        if (INSTANCE == null) {
            synchronized (HabitDatabase.class) {
                if (INSTANCE == null) {

                    INSTANCE = Room.databaseBuilder(context.getApplicationContext(),
                                    HabitDatabase.class, "habit_database")
                            .fallbackToDestructiveMigration()
                            .build();
                }
            }
        }
        return INSTANCE;
    }
}