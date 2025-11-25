package es.ucm.fdi.pad.hahabit.ui.tracker;

import android.app.Application;

import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import es.ucm.fdi.pad.hahabit.data.DayActivity;
import es.ucm.fdi.pad.hahabit.data.HabitArea;
import es.ucm.fdi.pad.hahabit.data.HabitCompletion;
import es.ucm.fdi.pad.hahabit.data.HabitRepository;
import es.ucm.fdi.pad.hahabit.data.TrackerSummary;

public class TrackerViewModel extends AndroidViewModel {

    private final HabitRepository repository;
    private final MediatorLiveData<List<HabitArea>> habitAreas;
    private final MediatorLiveData<TrackerSummary> summary;
    private final int DAYS_TO_SHOW = 30;

    public TrackerViewModel(Application application) {
        super(application);
        repository = new HabitRepository(application);
        habitAreas = new MediatorLiveData<>();
        summary = new MediatorLiveData<>();

        loadTrackerData();
    }

    public LiveData<List<HabitArea>> getHabitAreas() {
        return habitAreas;
    }

    public LiveData<TrackerSummary> getSummary() {
        return summary;
    }

    private void loadTrackerData() {
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.DAY_OF_YEAR, -DAYS_TO_SHOW);
        long startDate = HabitRepository.getStartOfDay(cal);

        LiveData<List<String>> areasLiveData = repository.getAllAreasFromHabits();
        LiveData<List<HabitCompletion>> completionsLiveData = repository.getCompletionsSince(startDate);

        habitAreas.addSource(areasLiveData, areas -> {
            List<HabitCompletion> completions = completionsLiveData.getValue();
            if (areas != null) {
                updateData(areas, completions);
            }
        });

        habitAreas.addSource(completionsLiveData, completions -> {
            List<String> areas = areasLiveData.getValue();
            if (areas != null) {
                updateData(areas, completions);
            }
        });
    }

    private void updateData(List<String> areas, List<HabitCompletion> completions) {
        List<HabitArea> areasList = buildHabitAreas(areas, completions);
        habitAreas.setValue(areasList);
        summary.setValue(buildSummary(areasList, completions));
    }

    private TrackerSummary buildSummary(List<HabitArea> areas, List<HabitCompletion> completions) {
        int totalCompletions = completions != null ? completions.size() : 0;

        // Calcular días activos (días únicos con al menos una completion)
        Set<Long> activeDays = new HashSet<>();
        if (completions != null) {
            for (HabitCompletion c : completions) {
                activeDays.add(c.getCompletionDate());
            }
        }
        int totalActiveDays = activeDays.size();

        // Mejor racha entre todas las áreas
        int bestStreak = 0;
        for (HabitArea area : areas) {
            if (area.getCurrentStreak() > bestStreak) {
                bestStreak = area.getCurrentStreak();
            }
        }

        return new TrackerSummary(totalActiveDays, bestStreak, totalCompletions);
    }

    private List<HabitArea> buildHabitAreas(List<String> areas, List<HabitCompletion> completions) {
        List<HabitArea> result = new ArrayList<>();

        if (areas == null || areas.isEmpty()) {
            return result;
        }

        // Agrupar completions por área y fecha
        Map<String, Map<Long, Integer>> areaCompletions = new HashMap<>();
        for (String area : areas) {
            areaCompletions.put(area, new HashMap<>());
        }

        if (completions != null) {
            for (HabitCompletion completion : completions) {
                String area = completion.getArea();
                if (areaCompletions.containsKey(area)) {
                    Map<Long, Integer> dateMap = areaCompletions.get(area);
                    long date = completion.getCompletionDate();
                    dateMap.put(date, dateMap.getOrDefault(date, 0) + 1);
                }
            }
        }

        // Crear HabitArea para cada área
        for (String area : areas) {
            List<DayActivity> activities = generateActivitiesForArea(areaCompletions.get(area));
            int streak = calculateStreak(activities);
            result.add(new HabitArea(area, streak, activities));
        }

        return result;
    }

    private List<DayActivity> generateActivitiesForArea(Map<Long, Integer> completionsByDate) {
        List<DayActivity> activities = new ArrayList<>();
        Calendar calendar = Calendar.getInstance();

        for (int i = DAYS_TO_SHOW - 1; i >= 0; i--) {
            Calendar dayCal = (Calendar) calendar.clone();
            dayCal.add(Calendar.DAY_OF_YEAR, -i);
            long dayStart = HabitRepository.getStartOfDay(dayCal);

            int completedHabits = 0;
            if (completionsByDate != null && completionsByDate.containsKey(dayStart)) {
                completedHabits = completionsByDate.get(dayStart);
            }

            activities.add(new DayActivity(dayCal.getTime(), completedHabits));
        }

        return activities;
    }

    private int calculateStreak(List<DayActivity> activities) {
        int streak = 0;

        // Recorrer desde el día más reciente hacia atrás
        for (int i = activities.size() - 1; i >= 0; i--) {
            if (activities.get(i).getCompletedHabits() > 0) {
                streak++;
            } else {
                // Si encontramos un día sin actividad, paramos (excepto si es hoy)
                if (i < activities.size() - 1) {
                    break;
                }
            }
        }

        return streak;
    }
}
