package es.ucm.fdi.pad.hahabit.data;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "habitos")
public class Habit {

    @PrimaryKey(autoGenerate = true)
    private int id;

    private String title;
    private String area;
    private String type; // "normal", "list", "timer"
    private Double progress;
    private boolean done;
    private int typeFrequency;
    private String daysFrequency;
    private Integer frequency;
    private boolean reminderEnabled;
    private String reminderTime;

    private Long startDate;

    // Campos para hábito tipo lista (JSON string con los items)
    private String listItems; // JSON: [{"text":"Item 1","completed":false},...]

    private Integer timerHours;    // Tiempo del temporizador en horas
    private Integer timerMinutes;  // Tiempo del temporizador en minutos
    private Integer timerSeconds;  // Tiempo del temporizador en segundos

    // Campos para hábito tipo temporizador
    private Long timerElapsed; // Tiempo transcurrido en milisegundos
    private Long timerStartTime; // Timestamp cuando se inició
    private boolean timerRunning; // Si está corriendo
    private Long timerTarget; // Duración objetivo en milisegundos (opcional)

    // Constructor
    public Habit(String title, String area, String type, Double progress, boolean done,
                 int typeFrequency, String daysFrequency, Integer frequency
                , boolean reminderEnabled, String reminderTime, Long startDate,
                 Integer timerHours, Integer timerMinutes, Integer timerSeconds) {
        this.title = title;
        this.area = area;
        this.type = type != null ? type : "normal";
        this.progress = progress;
        this.done = done;
        this.typeFrequency = typeFrequency;
        this.daysFrequency = daysFrequency;
        this.frequency = frequency;
        this.reminderEnabled = reminderEnabled;
        this.reminderTime = reminderTime;
        this.startDate = startDate;

        // Inicializar campos específicos según el tipo
        this.listItems = null;
        this.timerElapsed = 0L;
        this.timerStartTime = null;
        this.timerRunning = false;
        this.timerTarget = null;

        this.timerHours = timerHours;
        this.timerMinutes = timerMinutes;
        this.timerSeconds = timerSeconds;
    }

    // Getters y Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public String getType() { return type != null ? type : "normal"; }
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

    // Getters y Setters para campos de lista
    public String getListItems() { return listItems; }
    public void setListItems(String listItems) { this.listItems = listItems; }

    // Getters y Setters para campos de temporizador
    public Long getTimerElapsed() { return timerElapsed != null ? timerElapsed : 0L; }
    public void setTimerElapsed(Long timerElapsed) { this.timerElapsed = timerElapsed; }

    public Long getTimerStartTime() { return timerStartTime; }
    public void setTimerStartTime(Long timerStartTime) { this.timerStartTime = timerStartTime; }

    public boolean isTimerRunning() { return timerRunning; }
    public void setTimerRunning(boolean timerRunning) { this.timerRunning = timerRunning; }

    public Long getTimerTarget() { return timerTarget; }
    public void setTimerTarget(Long timerTarget) { this.timerTarget = timerTarget; }

    public Integer getTimerMinutes() { return timerMinutes; }
    public void setTimerMinutes(Integer timerMinutes) { this.timerMinutes = timerMinutes; }

    public Integer getTimerHours() { return timerHours; }

    public void setTimerHours(Integer timerHours) { this.timerHours = timerHours; }

    public Integer getTimerSeconds() { return timerSeconds; }
    public void setTimerSeconds(Integer timerSeconds) { this.timerSeconds = timerSeconds; }
}