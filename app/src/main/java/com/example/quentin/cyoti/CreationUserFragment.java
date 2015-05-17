package com.example.quentin.cyoti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class CreationUserFragment extends Fragment {

    public CreationUserFragment(){}

    Button signUp;
    EditText username;
    EditText password;
    EditText mail;
    String usernametxt;
    String passwordtxt;
    String mailtxt;
    View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.creation_user, container, false);

    // Locate EditTexts in main.xml
            username = (EditText) rootView.findViewById(R.id.username);
            password = (EditText) rootView.findViewById(R.id.password);
            mail = (EditText) rootView.findViewById(R.id.mail);

            // Locate Buttons in main.xml
            signUp = (Button) rootView.findViewById(R.id.signup);

        // Sign up Button Click Listener
        signUp.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Retrieve the text entered from the EditText
                usernametxt = username.getText().toString();
                passwordtxt = password.getText().toString();
                mailtxt = mail.getText().toString();

                // Force user to fill up the form
                if (usernametxt.equals("") || passwordtxt.equals("") || mailtxt.equals("")) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Please complete the sign up form",
                            Toast.LENGTH_LONG).show();

                } else {
                    // Save new user data into Parse.com Data Storage
                    ParseUser user = new ParseUser();
                    user.setUsername(usernametxt);
                    user.setPassword(passwordtxt);
                    user.setEmail(mailtxt);
                    user.put("mail_checking", false);
                    user.put("newsletter", true);
                    user.put("level", 0);
                    user.put("experience", 0);
                    user.put("facebook_account", false);
                    user.put("twitter_account", false);
                    user.put("newsletter", true);
                    user.put("freeze_account", false);
                    user.signUpInBackground(new SignUpCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                // Show a simple Toast message upon successful registration
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Successfully Signed up",
                                        Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(rootView.getContext(), ChallengeActivity.class);
                                startActivity(intent);
                            } else {
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Sign up Error", Toast.LENGTH_LONG)
                                        .show();
                            }
                        }
                    });
                }

            }
        });

        return rootView;
    }

}