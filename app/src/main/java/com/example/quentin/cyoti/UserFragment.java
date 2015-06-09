package com.example.quentin.cyoti;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.StringAdapter;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.metier.User;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;


public class UserFragment extends Fragment {
    private View rootView;
    private final ParseUser currentUser;
    private ArrayList<Friend> friends;
    private ListView listChallenges;
    private Hashtable<String,String> friendsDic;
    private ArrayList<String> challenges;
    private List<ParseObject> listTempObject = null;

    private OnUserListener mCallback;
    private ParseObject tempObject = null;
    private static User user = new User();
    private boolean queryOK = false;
    private int i = 0;

    public UserFragment() {
        currentUser = ParseUser.getCurrentUser();
        friends = new ArrayList<Friend>();
        challenges = new ArrayList<String>();
        friendsDic = new Hashtable<String, String>();

        // Create notifications' installation
        createParseInstallation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_user, container, false);

        // Getting friends' challenges acceptations and achieved

        //reset of challenges list
        challenges.clear();

        // Get actual friends of user and friends IDs
        getFriends();
        for (int i=0; i<friends.size(); i++) {
            ParseQuery<ParseObject> queryFriend = ParseQuery.getQuery("_User");
            queryFriend.whereEqualTo("username", friends.get(i).getFirstName());
            ParseObject friend = null;
            try {
                friend = queryFriend.getFirst();
                friendsDic.put(friend.getObjectId(), friends.get(i).getFirstName());
            } catch (ParseException e) {
                Log.d("getFriendID", e.getMessage());
                Toast.makeText(getActivity().getApplicationContext(),
                        "Problem getting friends informations. Please try later.", Toast.LENGTH_SHORT).show();
            }

        }

        // Get challenges
        ParseQuery<ParseObject> queryChallengesChallenger = ParseQuery.getQuery("Attributed_challenge");
        queryChallengesChallenger.whereContainedIn("user_id_applicant", Collections.list(friendsDic.keys()));

        ParseQuery<ParseObject> queryChallengesChallenged = ParseQuery.getQuery("Attributed_challenge");
        queryChallengesChallenged.whereContainedIn("user_id", Collections.list(friendsDic.keys()));
        queryChallengesChallenged.whereNotEqualTo("user_id_applicant",currentUser.getObjectId());

        ParseQuery<ParseObject> queryChallenges = ParseQuery.or(Arrays.asList(queryChallengesChallenger, queryChallengesChallenged));
        queryChallenges.addDescendingOrder("createdAt");
        queryChallenges.setLimit(10);

        try {
            listTempObject = queryChallenges.find();
        } catch (ParseException e) {
            Log.d("queryFail", "Query Object has failed : " + e.toString());
        }

        for (int j=0; j<listTempObject.size(); j++) {
            ParseQuery<ParseObject> queryOneChallenge = ParseQuery.getQuery("Challenge");
            queryOneChallenge.whereEqualTo("objectId", listTempObject.get(j).get("challenge_id"));

            ParseQuery<ParseObject> queryChallenger = ParseQuery.getQuery("_User");
            queryChallenger.whereEqualTo("objectId", listTempObject.get(j).get("user_id_applicant"));

            ParseQuery<ParseObject> queryChallenged = ParseQuery.getQuery("_User");
            queryChallenged.whereEqualTo("objectId", listTempObject.get(j).get("user_id"));

            ParseObject tempChallenge = null;
            ParseObject tempFriend = null;
            String display = null;

            try {
                tempChallenge = queryOneChallenge.getFirst();
            } catch (ParseException e) {
                Log.d("queryFail", "Query Object has failed : " + e.toString());
            }

            if (Collections.list(friendsDic.keys()).contains(listTempObject.get(j).get("user_id"))) {
                try {
                    tempFriend = queryChallenger.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Object has failed : " + e.toString());
                }

                display = tempFriend.get("username") + " challenges " + friendsDic.get(listTempObject.get(j).get("user_id")) + " to " + tempChallenge.get("challenge");


            } else if (Collections.list(friendsDic.keys()).contains(listTempObject.get(j).get("user_id_applicant"))) {
                try {
                    tempFriend = queryChallenged.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Object has failed : " + e.toString());
                }

                display = friendsDic.get(listTempObject.get(j).get("user_id_applicant")) + " challenges " + tempFriend.get("username") + " to " + tempChallenge.get("challenge");
            }

            if (listTempObject.get(j).get("success") != null) {
                String tempSuccess = listTempObject.get(j).get("success").toString();
                 if (Boolean.parseBoolean(tempSuccess)) {display += " REUSSITE";} else {display += " ECHEC";}
            }

            challenges.add(display);

        }

        // Fill the ListView
        listChallenges = (ListView) rootView.findViewById(R.id.lv_challenges);

        final StringAdapter challengesAdapter = new StringAdapter(rootView.getContext(),
                R.layout.listitem_news,
                challenges,
                R.id.tv_challenge);

        listChallenges.setAdapter(challengesAdapter);



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

    public void getFriends() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

        ParseObject tempObject = null;
        ArrayList<String> tempFriends;

        try {
            tempObject = query.get(currentUser.getObjectId());
        } catch (ParseException e) {
            Log.d("queryFail", "Query has failed : " + e.toString());
        }

        if (tempObject != null) {
            tempFriends = (ArrayList<String>) tempObject.get("friend_list");


            if (tempFriends != null) {
                for (int i = 0; i < tempFriends.size(); i++) {
                    friends.add(new Friend(tempFriends.get(i)));
                }
            }
        }
    }

    public void createParseInstallation() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("newChallenge", true);
        installation.put("newFriend", true);
        installation.put("newEvidence", true);
        installation.put("newVote", true);
        installation.put("user_id", currentUser);

        installation.saveInBackground();
    }

}
