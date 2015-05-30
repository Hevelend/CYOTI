package com.example.quentin.cyoti;

import com.facebook.FacebookSdk;
import com.parse.Parse;
import com.parse.ParseACL;

import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import android.app.Application;

public class ParseApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        // Add your initialization code here
        Parse.initialize(this, "MHc2HO2gVNyI2uMdGmHUPwNKDjMMMSGroQENoUmW", "ghzcQLyYa0M5HiIdRchcnfC1EE7RnXhL229tCUfX");
        ParseUser.enableAutomaticUser();
        ParseACL defaultACL = new ParseACL();

        // If you would like all objects to be private by default, remove this
        // line.
        defaultACL.setPublicReadAccess(true);

        ParseACL.setDefaultACL(defaultACL, true);
    }

}
