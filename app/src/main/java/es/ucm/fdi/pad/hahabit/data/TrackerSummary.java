package es.ucm.fdi.pad.hahabit.data;

public class TrackerSummary {
    private int totalActiveDays;
    private int bestStreak;
    private int totalCompletions;

    public TrackerSummary(int totalActiveDays, int bestStreak, int totalCompletions) {
        this.totalActiveDays = totalActiveDays;
        this.bestStreak = bestStreak;
        this.totalCompletions = totalCompletions;
    }

    public int getTotalActiveDays() { return totalActiveDays; }
    public int getBestStreak() { return bestStreak; }
    public int getTotalCompletions() { return totalCompletions; }
}
