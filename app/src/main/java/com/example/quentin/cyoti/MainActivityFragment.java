package com.example.quentin.cyoti;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.parse.LogInCallback;
import com.parse.ParseException;
import com.facebook.FacebookSdk;
import com.parse.ParseTwitterUtils;
import com.parse.ParseUser;

import java.util.Collection;
import java.lang.String;

import android.content.Intent;
/*import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;*/


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //Déclaration des variables
    EditText username;
    EditText password;
    Button signUp;
    Button login;
    ImageButton twitter;
    String usernametxt;
    String passwordtxt;
    Collection<String> permissions;
    CallbackManager callbackManager;
    LoginButton loginButton;
    /*TwitterLoginButton loginButtonTwitter;*/

    private View rootView;

    public MainActivityFragment() {
    }
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //initialisation connexion avec facebook
        FacebookSdk.sdkInitialize(getActivity().getApplicationContext());
        callbackManager = CallbackManager.Factory.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragment_main, container, false);

        //Locate EditText in fragment_main.xml
        username = (EditText) rootView.findViewById(R.id.username);
        password = (EditText) rootView.findViewById(R.id.password);

        // Locate Buttons in fragment_main.xml
        login = (Button) rootView.findViewById(R.id.login);
        signUp = (Button) rootView.findViewById(R.id.signup);

        // Bouton de Login pour facebook
        loginButton = (LoginButton) rootView.findViewById(R.id.fb);
        loginButton.setBackgroundResource(R.drawable.ic_fb);

        loginButton.setReadPermissions("user_friends");
        // If using in a fragment
        loginButton.setFragment(this);

        twitter = (ImageButton) rootView.findViewById(R.id.twitter);
        //Bouton login pour twitter
      /*  loginButtonTwitter = (TwitterLoginButton) rootView.findViewById(R.id.twitter_login_button);
        loginButtonTwitter.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Intent intent = new Intent(rootView.getContext(), ChallengeActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully Logged in",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
            }
        });*/

        // Login Button Click Listener
        login.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();

                // Send data to Parse.com for verification
                ParseUser.logInInBackground(usernametxt, passwordtxt,
                        new LogInCallback() {
                            public void done(ParseUser user, ParseException e) {
                                if (user != null) {
                                    if (user.getBoolean("freeze_account") != true) {
                                        // If user exist and authenticated and not freeezed, send user to ChallengeActivity.class
                                        Intent intent = new Intent(rootView.getContext(), ChallengeActivity.class);
                                        startActivity(intent);
                                        Toast.makeText(getActivity().getApplicationContext(),
                                                "Successfully Logged in",
                                                Toast.LENGTH_SHORT).show();
                                        getActivity().finish();
                                    } else {
                                        Toast.makeText(
                                                getActivity().getApplicationContext(),
                                                "No such user exist, please signup",
                                                Toast.LENGTH_LONG).show();
                                    }

                                } else {
                                    Toast.makeText(
                                            getActivity().getApplicationContext(),
                                            "No such user exist, please signup",
                                            Toast.LENGTH_LONG).show();
                                }
                            }
                        });
            }
        });
        // Sign up Button Click Listener
        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle();

                Fragment fragment = new CreationUserFragment();
                fragment.setArguments(bundle);

                FragmentManager fragmentManager = getFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.container, fragment).commit();
            }
        });

        twitter.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                ParseTwitterUtils.logIn(getActivity(), new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Twitter login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Twitter!");
                        } else {
                            Log.d("MyApp", "User logged in through Twitter!");
                        }
                    }
                });
            }
        });

/*
        fb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ParseFacebookUtils.logInWithReadPermissionsInBackground(MainActivityFragment.this,permissions, new LogInCallback() {
                    @Override
                    public void done(ParseUser user, ParseException err) {
                        if (user == null) {
                            Log.d("MyApp", "Uh oh. The user cancelled the Facebook login.");
                        } else if (user.isNew()) {
                            Log.d("MyApp", "User signed up and logged in through Facebook!");
                        } else {
                            Log.d("MyApp", "User logged in through Facebook!");
                        }
                    }
                });
            }

        });*/

            // Callback registration
            loginButton.registerCallback(callbackManager,new FacebookCallback<LoginResult>()

            {
                @Override
                public void onSuccess (LoginResult loginResult){
                Intent intent = new Intent(rootView.getContext(), ChallengeActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully Logged in",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }

                @Override
                public void onCancel () {
                Toast.makeText(
                        getActivity().getApplicationContext(),
                        "No such user exist, please signup",
                        Toast.LENGTH_LONG).show();

            }

                @Override
                public void onError (FacebookException exception){
                Intent intent = new Intent(rootView.getContext(), ChallengeActivity.class);
                startActivity(intent);
                Toast.makeText(getActivity().getApplicationContext(),
                        "Successfully Logged in",
                        Toast.LENGTH_SHORT).show();
                getActivity().finish();
            }
            }

            );

            return rootView;
        }

        @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode, resultCode, data);
       //loginButtonTwitter.onActivityResult(requestCode, resultCode, data);
    }
}

