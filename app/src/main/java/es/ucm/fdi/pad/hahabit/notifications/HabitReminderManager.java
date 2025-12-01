package es.ucm.fdi.pad.hahabit.notifications;

import android.app.AlarmManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;

import java.util.Calendar;

import es.ucm.fdi.pad.hahabit.data.Habit;

public class HabitReminderManager {

    private static final String CHANNEL_ID = "habit_reminders";
    private static final String CHANNEL_NAME = "Recordatorios de Hábitos";
    private static final String CHANNEL_DESCRIPTION = "Notificaciones para recordar tus hábitos";

    private final Context context;

    public HabitReminderManager(Context context) {
        this.context = context;
        createNotificationChannel();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_HIGH
            );
            channel.setDescription(CHANNEL_DESCRIPTION);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);
            if (notificationManager != null) {
                notificationManager.createNotificationChannel(channel);
            }
        }
    }

    public void scheduleReminder(Habit habit) {
        if (!habit.isReminderEnabled() || habit.getReminderTime() == null || habit.getReminderTime().isEmpty()) {
            return;
        }

        try {
            // Parsear la hora del recordatorio (formato HH:mm)
            String[] timeParts = habit.getReminderTime().split(":");
            int hour = Integer.parseInt(timeParts[0]);
            int minute = Integer.parseInt(timeParts[1]);

            // Configurar el calendario para la hora del recordatorio
            Calendar calendar = Calendar.getInstance();
            calendar.set(Calendar.HOUR_OF_DAY, hour);
            calendar.set(Calendar.MINUTE, minute);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);

            // Si la hora ya pasó hoy, programar para mañana
            if (calendar.getTimeInMillis() <= System.currentTimeMillis()) {
                calendar.add(Calendar.DAY_OF_MONTH, 1);
            }

            // Crear el intent para la notificación
            Intent intent = new Intent(context, ReminderBroadcastReceiver.class);
            intent.putExtra("habit_id", habit.getId());
            intent.putExtra("habit_title", habit.getTitle());

            PendingIntent pendingIntent = PendingIntent.getBroadcast(
                    context,
                    habit.getId(),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
            );

            // Programar la alarma
            AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
            if (alarmManager != null) {
                alarmManager.setInexactRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent
                );
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
