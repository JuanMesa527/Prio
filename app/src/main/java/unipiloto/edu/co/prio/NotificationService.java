package unipiloto.edu.co.prio;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import androidx.core.app.NotificationCompat;

public class NotificationService extends IntentService {

    private static final String CHANNEL_ID = "prio_channel";
    public static final int NOTIFICATION_ID = 1011;

    public NotificationService() {
        super("NotificationService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            try {
                wait(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String message = intent.getStringExtra("MESSAGE");
        showText(message);
    }

    private void showText(final String message) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(R.drawable.logo)
                .setContentTitle("Prio")
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setVibrate(new long[]{0, 1000})
                .setAutoCancel(true);

        Intent notificationIntent = new Intent(this, MainActivity.class);
        PendingIntent actionPendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE);
        builder.setContentIntent(actionPendingIntent);

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.notify(NOTIFICATION_ID, builder.build());
    }
}