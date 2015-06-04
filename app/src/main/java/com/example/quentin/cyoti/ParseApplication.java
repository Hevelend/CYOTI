package com.example.quentin.cyoti;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParsePush;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import android.app.Application;
import android.util.Log;
/*import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;*/

public class ParseApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    /*private static final String TWITTER_KEY = "6Yl3IY4vRn0ZbV64fd9YcoxAP";
    private static final String TWITTER_SECRET = "KcTZADAL2fkG00ihSkSoSPMM167roKTIkiPl9rKZo1L3j3343v";*/

    private final static String APPLICATION_ID = "MHc2HO2gVNyI2uMdGmHUPwNKDjMMMSGroQENoUmW";
    private final static String CLIENT_KEY = "ghzcQLyYa0M5HiIdRchcnfC1EE7RnXhL229tCUfX";


    @Override
    public void onCreate() {
        super.onCreate();
        /*TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));*/

        // Add your initialization code here
        //Parse.initialize(this, "MHc2HO2gVNyI2uMdGmHUPwNKDjMMMSGroQENoUmW", "ghzcQLyYa0M5HiIdRchcnfC1EE7RnXhL229tCUfX");
        Parse.initialize(this, APPLICATION_ID, CLIENT_KEY);
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        //Enable push notifications
        ParsePush.subscribeInBackground("", new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if (e == null) {
                    Log.d("com.parse.push", "successfully subscribed to the broadcast channel.");
                } else {
                    Log.e("com.parse.push", "failed to subscribe for push", e);
                }
            }
        });

        /*ParseTwitterUtils.initialize("TWITTER_KEY", "TWITTER_SECRET");*/

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
