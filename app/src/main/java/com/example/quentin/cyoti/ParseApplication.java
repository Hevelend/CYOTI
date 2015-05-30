package com.example.quentin.cyoti;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseFacebookUtils;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import android.app.Application;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import io.fabric.sdk.android.Fabric;

public class ParseApplication extends Application {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "6Yl3IY4vRn0ZbV64fd9YcoxAP";
    private static final String TWITTER_SECRET = "KcTZADAL2fkG00ihSkSoSPMM167roKTIkiPl9rKZo1L3j3343v";


    @Override
    public void onCreate() {
        super.onCreate();
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(authConfig));

        // Add your initialization code here
        Parse.initialize(this, "MHc2HO2gVNyI2uMdGmHUPwNKDjMMMSGroQENoUmW", "ghzcQLyYa0M5HiIdRchcnfC1EE7RnXhL229tCUfX");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        ParseTwitterUtils.initialize("TWITTER_KEY", "TWITTER_SECRET");

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
