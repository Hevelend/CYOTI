package com.example.quentin.cyoti.utilities;

import android.util.Log;

import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SendCallback;

/**
 * Created by Vincent on 05/06/2015.
 */
public class PushNotification {
    public final static int NEW_CHALLENGE_NOTIFICATION = 1;
    public final static int NEW_FRIEND_NOTIFICATION    = 2;
    public final static int NEW_EVIDENCE_NOTIFICATION  = 3;

    public static void sendNotification(ParseUser sender, ParseObject receiver, int type) {
        switch (type) {
            case NEW_CHALLENGE_NOTIFICATION:
                sendNewChallengeNotification(sender, receiver);
                break;

            case NEW_FRIEND_NOTIFICATION:
                sendNewFriendNotification(sender, receiver);
                break;

            case NEW_EVIDENCE_NOTIFICATION:
                sendNewEvidenceNotification(sender, receiver);
        }
    }

    private static void sendNewChallengeNotification(ParseUser sender, ParseObject receiver) {
        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", receiver.get("username"));

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user_id", userQuery);

        // Send notification
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(sender.getUsername() + " challenged you !");
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("push", "Sending push ok");
            }
        });
    }

    private static void sendNewFriendNotification(ParseUser sender, ParseObject receiver) {
        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", receiver.get("username"));

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user_id", userQuery);

        // Send notification
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(sender.getUsername() + " wants to be your friend !");
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("push", "Sending push ok");
            }
        });
    }

    private static void sendNewEvidenceNotification(ParseUser sender, ParseObject reveicer) {
        ParseQuery userQuery = ParseUser.getQuery();
        userQuery.whereEqualTo("username", reveicer.get("username"));

        ParseQuery pushQuery = ParseInstallation.getQuery();
        pushQuery.whereMatchesQuery("user_id", userQuery);

        // Send notification
        ParsePush push = new ParsePush();
        push.setQuery(pushQuery);
        push.setMessage(sender.getUsername() + " added an evidence for your challenge !");
        push.sendInBackground(new SendCallback() {
            @Override
            public void done(ParseException e) {
                Log.d("push", "Sending push ok");
            }
        });
    }
}
