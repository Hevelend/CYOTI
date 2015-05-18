package com.example.quentin.cyoti;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.metier.User;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class UserFragment extends Fragment {
    private View rootView;
    Button logout;
    private OnUserListener mCallback;

    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_user, container, false);

        // Retrieve current user from Parse.com
        ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) rootView.findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText("You are logged in as " + struser);

        // Locate Button in welcome.xml
        logout = (Button) rootView.findViewById(R.id.logout);

        // Logout Button Click Listener
        logout.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                // Logout current user
                ParseUser.logOut();
                getActivity().finish();
            }
        });

        // Ajout d'un ami si la liste est vide
        ParseQuery<ParseObject> query = ParseQuery.getQuery("User");
        query.selectKeys(Arrays.asList("friend_list"));

        try {
            List<ParseObject> results = query.find();
            int count = results.size();

            if (count == 0) {
                Log.d("ct", "No friends");
                ArrayList<String> friends = new ArrayList<String>();
                Friend friend1 = new Friend();
                Friend friend2 = new Friend("Vincent","Aunai","TiLodoss");
                friends.add(friend1.getFirstName());
                friends.add(friend2.getFirstName());
                currentUser.put("friend_list", friends);

                currentUser.saveInBackground(new SaveCallback() {
                    public void done(ParseException e) {
                        if (e == null) {
                            Log.d("chg", "Query object ok");
                        } else {
                            Log.d("chg", "The put request failed.");
                        }
                    }
                });

                User user = new User();

                user.setFirstName(currentUser.getUsername());


                for (int i =0;i<results.size();i++) {
                    user.addFriend(results.get(i).toString());
                }

                mCallback.onUserConnected(user);
            }

            else {
                Log.d("ctf", "Nombre amis :" + count);

                //intent.putExtra("user", bundleUser);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }


        //intent.putExtra("user", );

        return rootView;
    }


    // Attachement avec l'activité ChallengeActivity
    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);


        // On regarde si l'activité implémente bien l'interface de communication
        try {
            mCallback = (OnUserListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnHeadlineSelectedListener");
        }
    }


    // Interface de communication avec l'activiy parente
    public interface OnUserListener {
        public void onUserConnected(User user);
    }

}
