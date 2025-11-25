package es.ucm.fdi.pad.hahabit.ui.add;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import es.ucm.fdi.pad.hahabit.data.Habit;
import es.ucm.fdi.pad.hahabit.data.HabitRepository;

public class AddViewModel extends AndroidViewModel {
    private final MutableLiveData<String> title;

    private final MutableLiveData<String> area;
    private final MutableLiveData<String> type;

    private final MutableLiveData<Integer> typeFrequency;
    private final MutableLiveData<List<Integer>> selectedWeekDays;
    private final MutableLiveData<Integer> intervalDays;

    private final MutableLiveData<Long> time;

    private final HabitRepository repository;


    public AddViewModel(Application application) {

        super(application);

        title = new MutableLiveData<>();

        area = new MutableLiveData<>();
        type = new MutableLiveData<>();

        typeFrequency = new MutableLiveData<>();
        selectedWeekDays = new MutableLiveData<>(new ArrayList<>());
        intervalDays = new MutableLiveData<>(-1);

        time = new MutableLiveData<>();

        repository = new HabitRepository(application);
    }

    // setters
    public void setTitle(String t){
        title.setValue(t);
    }
    public LiveData<String> getTitle(){
        return title;
    }

    public void setArea(String a){
        area.setValue(a);
    }
    public LiveData<String> getArea(){
        return area;
    }

    public void setType(String t){
        type.setValue(t);
    }
    public LiveData<String> getType(){
        return type;
    }

    public void setTypeFrequency(int f){
        typeFrequency.setValue(f);
    }
    public LiveData<Integer> getTypeFrequency() {
        return typeFrequency;
    }

    public void setWeekDays(int weekDay) {

        List<Integer> weekDays = new ArrayList<>(selectedWeekDays.getValue());

        // si ya estaba seleccionado se elimina, si no, se añade
        if (weekDays.contains(weekDay)) weekDays.remove(Integer.valueOf(weekDay));
        else weekDays.add(weekDay);

        selectedWeekDays.setValue(weekDays);
    }

    public LiveData<List<Integer>> getSelectedWeekDays() {
        return selectedWeekDays;
    }

    public void setIntervalDays(int d){
        intervalDays.setValue(d);
    }
    public LiveData<Integer> getIntervalDays(){
        return intervalDays;
    }
    public void setTime(int hour, int minute){
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hour);
        calendar.set(Calendar.MINUTE, minute);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        time.setValue(calendar.getTimeInMillis());
    }

    public Habit buildHabit() {

        Integer freq = typeFrequency.getValue();
        Integer interval = intervalDays.getValue();

        // pasar la lista de los dias de la semana a un string si es semanal
        String dias = "";
        if (freq != null && freq == 0)    // 0 = semanal
            dias = selectedWeekDays.getValue().stream().map(String::valueOf).collect(Collectors.joining(","));
        // Si es intervalo de dias, limpio los dias semanales
        if (freq != null && freq == 1) {   // 1 = intervalo
            dias = "";
        }
        return new Habit(
                title.getValue(),
                area.getValue(),
                type.getValue(),
                0.0,                // progress
                false,                      // done
                freq,
                dias,                       // daysFrequency
                interval
        );
    }

    public void reset() { //para resetear la ventana despues de crear un habito
        title.setValue("");
        area.setValue("");
        type.setValue("");
        typeFrequency.setValue(0);
        selectedWeekDays.setValue(new ArrayList<>());
        intervalDays.setValue(-1);
        time.setValue(null);
    }


    public void insertHabit(Habit habit) {
        repository.insert(habit);
    }
}
