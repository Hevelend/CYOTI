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

import com.example.quentin.cyoti.adapters.StringAdapter;
import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.metier.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;


public class UserFragment extends Fragment {
    private View rootView;
    private OnUserListener mCallback;
    private ParseObject tempObject = null;
    private ArrayList<String> challenges;
    private static User user = new User();
    private static List<String> friends = null;
    private boolean queryOK = false;
    private int i = 0;

    public UserFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_user, container, false);

        // Retrieve current user from Parse.com
        final ParseUser currentUser = ParseUser.getCurrentUser();

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) rootView.findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText("You are logged in as " + struser);

//////////////////////////////////////////////////////////////////////////////////////////////////////
                    /*TODO Ce bout de code sera à travailler plus tard... */

        // On cherche la liste d'amis du currentUser



//        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
//
//        try {
//            tempObject = query.get(currentUser.getObjectId());
//            Log.d("queryOK", "Results found in query");
//        } catch (ParseException e) {
//            Log.d("queryFail", "Query has failed : " + e.toString());
//        }
//
//        if (tempObject != null) {
//            Log.d("tpObj", "tempObject not null");
//            user.setFirstName(tempObject.getString("username"));
//
//            friends = tempObject.getList("friend_list");
//
//            if (friends.size() == 0) {
//                Log.d("tab", "Liste d'amis vide");
//            }
//
//            else {
//                for (int i = 0; i < friends.size(); i++) {
//                    user.addFriend(friends.get(i));
//                }
//            }
//
//            mCallback.onUserConnected(user);
//        }
//
//        else Log.d("tpObjNull", "tempObject is null");

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
                    + " must implement OnUserListener");
        }
    }


    // Interface de communication avec l'activiy parente
    public interface OnUserListener {
        public void onUserConnected(User user);
    }

    public void saveObject(ParseObject object) {
        tempObject = object;
    }

}
