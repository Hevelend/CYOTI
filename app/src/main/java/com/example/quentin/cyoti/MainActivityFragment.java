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

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseFacebookUtils;
import com.parse.ParseUser;

import java.util.Collection;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    //Déclaration des variables
    EditText username;
    EditText password;
    Button signUp;
    Button login;
    ImageButton fb;
    String usernametxt;
    String passwordtxt;
    Collection<String> permissions;

    private View rootView;

    public MainActivityFragment() {
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
        fb = (ImageButton) rootView.findViewById(R.id.fb);

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

        });
        return rootView;
    }
}

