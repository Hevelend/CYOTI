package com.example.quentin.cyoti.utilities;

import android.content.Context;
import android.support.v4.app.NotificationCompat;
import com.example.quentin.cyoti.R;

/**
 * Created by Vincent on 03/06/2015.
 */
public class NotificationGenerator {
    private static boolean GENERATE_NOTIFICATIONS = true;

    private final static int NEW_CHALLENGE_REQUEST = 1;
    private final static int NEW_FRIEND_REQUEST = 2;
    private final static int NEW_EVIDENCE_RECEIVED = 3;
    private final static int NEW_VOTE_FOR_CHALLENGE = 4;

    private int notificationID;

    private NotificationCompat.Builder notifBuilder;

    public NotificationGenerator() {}

    public NotificationCompat.Builder createNotification(int notification, Context ctx) {
        switch(notification) {
            case NEW_CHALLENGE_REQUEST:
                notificationID = 001;
                notifBuilder = new NotificationCompat.Builder(ctx);
                notifBuilder.setSmallIcon(R.drawable.ic_app);
                notifBuilder.setContentTitle("New challenge !");
                notifBuilder.setContentText("Test description");
                return notifBuilder;

            case NEW_EVIDENCE_RECEIVED:
                return null;

            case NEW_FRIEND_REQUEST:
                return null;

            case NEW_VOTE_FOR_CHALLENGE:
                return null;

            default: return null;
        }
    }


    public int getNotificationID() { return this.notificationID; }

}
