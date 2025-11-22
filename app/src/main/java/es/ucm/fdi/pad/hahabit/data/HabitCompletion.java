package es.ucm.fdi.pad.hahabit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

/*
 * Entidad para guardar los hábitos completados (se guarda una instancia de ellos con id, area y un date)
 */
@Entity(tableName = "habit_completions")
public class HabitCompletion {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private int habitId;
    private String area;
    private long completionDate; // timestamp del día (hora da igual)

    public HabitCompletion(int habitId, String area, long completionDate) {
        this.habitId = habitId;
        this.area = area;
        this.completionDate = completionDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getHabitId() { return habitId; }
    public void setHabitId(int habitId) { this.habitId = habitId; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public long getCompletionDate() { return completionDate; }
    public void setCompletionDate(long completionDate) { this.completionDate = completionDate; }
}
