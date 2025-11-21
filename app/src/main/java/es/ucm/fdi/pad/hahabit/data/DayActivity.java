package es.ucm.fdi.pad.hahabit.data;

import java.util.Date;

public class DayActivity {
    private Date date;
    private int completedHabits;

    public DayActivity(Date date, int completedHabits) {
        this.date = date;
        this.completedHabits = completedHabits;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public int getCompletedHabits() {
        return completedHabits;
    }

    public void setCompletedHabits(int completedHabits) {
        this.completedHabits = completedHabits;
    }

    public int getIntensityLevel() {
        if (completedHabits == 0) return 0;
        if (completedHabits <= 2) return 1;
        if (completedHabits <= 4) return 2;
        if (completedHabits <= 6) return 3;
        return 4;
    }
}
