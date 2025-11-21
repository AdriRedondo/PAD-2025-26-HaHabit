package es.ucm.fdi.pad.hahabit.ui.tracker;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;

import es.ucm.fdi.pad.hahabit.data.DayActivity;
import es.ucm.fdi.pad.hahabit.data.HabitArea;

public class TrackerViewModel extends ViewModel {

    private final MutableLiveData<List<HabitArea>> habitAreas;

    public TrackerViewModel() {
        habitAreas = new MutableLiveData<>();
        habitAreas.setValue(generateSampleData());
    }

    public LiveData<List<HabitArea>> getHabitAreas() {
        return habitAreas;
    }

    private List<HabitArea> generateSampleData() {
        List<HabitArea> areas = new ArrayList<>();

        areas.add(new HabitArea("Deporte", 5, generateActivities(56, 0.7)));
        areas.add(new HabitArea("Estudio", 3, generateActivities(56, 0.6)));
        areas.add(new HabitArea("Cocina", 2, generateActivities(56, 0.4)));
        areas.add(new HabitArea("Otros", 1, generateActivities(56, 0.3)));

        return areas;
    }

    private List<DayActivity> generateActivities(int days, double activityProbability) {
        List<DayActivity> activities = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();
        Random random = new Random();

        for (int i = days - 1; i >= 0; i--) {
            calendar.setTime(new Date());
            calendar.add(Calendar.DAY_OF_YEAR, -i);

            int completedHabits = 0;
            if (random.nextDouble() < activityProbability) {
                completedHabits = random.nextInt(8) + 1;
            }

            activities.add(new DayActivity(calendar.getTime(), completedHabits));
        }

        return activities;
    }
}
