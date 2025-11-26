package es.ucm.fdi.pad.hahabit.notifications;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

import es.ucm.fdi.pad.hahabit.MainActivity;
import es.ucm.fdi.pad.hahabit.R;

public class ReminderBroadcastReceiver extends BroadcastReceiver {

    private static final String CHANNEL_ID = "habit_reminders";

    @Override
    public void onReceive(Context context, Intent intent) {
        int habitId = intent.getIntExtra("habit_id", -1);
        String habitTitle = intent.getStringExtra("habit_title");

        if (habitId == -1 || habitTitle == null) {
            return;
        }

        // Crear intent para abrir la app cuando se toque la notificación
        Intent mainIntent = new Intent(context, MainActivity.class);
        mainIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                context,
                habitId,
                mainIntent,
                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
        );

        // Crear la notificación
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_notifications_black_24dp)
                .setContentTitle("Recordatorio de hábito")
                .setContentText(habitTitle)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Mostrar la notificación
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(habitId, builder.build());
        }
    }
}
