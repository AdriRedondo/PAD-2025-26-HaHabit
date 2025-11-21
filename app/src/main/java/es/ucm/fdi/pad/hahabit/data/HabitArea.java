package es.ucm.fdi.pad.hahabit.data;

import java.util.List;

public class HabitArea {
    private String areaName;
    private int currentStreak;
    private List<DayActivity> activities;

    public HabitArea(String areaName, int currentStreak, List<DayActivity> activities) {
        this.areaName = areaName;
        this.currentStreak = currentStreak;
        this.activities = activities;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public int getCurrentStreak() {
        return currentStreak;
    }

    public void setCurrentStreak(int currentStreak) {
        this.currentStreak = currentStreak;
    }

    public List<DayActivity> getActivities() {
        return activities;
    }

    public void setActivities(List<DayActivity> activities) {
        this.activities = activities;
    }
}
