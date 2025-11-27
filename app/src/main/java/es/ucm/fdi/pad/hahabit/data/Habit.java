package es.ucm.fdi.pad.hahabit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habitos")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String area;
    private String type;
    private Double progress;
    private boolean done;
    private int typeFrequency;
    private String daysFrequency;
    private Integer frequency;
    private boolean reminderEnabled;
    private String reminderTime;

    private Long startDate;

    // Constructor
    public Habit(String title, String area, String type, Double progress, boolean done,
                 int typeFrequency, String daysFrequency, Integer frequency
                , boolean reminderEnabled, String reminderTime, Long startDate) {
        this.title = title;
        this.area = area;
        this.type = type;
        this.progress = progress;
        this.done = done;
        this.typeFrequency = typeFrequency;
        this.daysFrequency = daysFrequency;
        this.frequency = frequency;
        this.reminderEnabled = reminderEnabled;
        this.reminderTime = reminderTime;
        this.startDate = startDate;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public Double getProgress() { return progress; }
    public void setProgress(Double progress) { this.progress = progress; }

    public boolean isDone() { return done; }
    public void setDone(boolean done) { this.done = done; }

    public int getTypeFrequency() { return typeFrequency; }
    public void setTypeFrequency(int typeFrequency) { this.typeFrequency = typeFrequency; }

    public String getDaysFrequency() { return daysFrequency; }
    public void setDaysFrequency(String daysFrequency) { this.daysFrequency = daysFrequency; }

    public Integer getFrequency() { return frequency; }
    public void setFrequency(Integer frequency) { this.frequency = frequency; }

    public boolean isReminderEnabled() { return reminderEnabled; }
    public void setReminderEnabled(boolean reminderEnabled) { this.reminderEnabled = reminderEnabled; }

    public String getReminderTime() { return reminderTime; }
    public void setReminderTime(String reminderTime) { this.reminderTime = reminderTime; }

    public Long getStartDate() { return startDate; }
    public void setStartDate(Long startDate) { this.startDate = startDate; }
}