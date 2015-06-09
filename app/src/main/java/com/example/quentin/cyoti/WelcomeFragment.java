package com.example.quentin.cyoti;

import com.example.quentin.cyoti.adapters.ChallengeAdapter;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Hashtable;
import java.util.List;

public class WelcomeFragment extends Fragment {


    private final ParseUser currentUser;
    public int XPuser;
    public int levelNumber;
    public String titleLevel;
    public ImageView iv_user;

    private ArrayList<Friend> friends;
    private ListView listChallenges;
    private Hashtable<String,String> friendsDic;
    private ArrayList<Challenge> challenges;
    private ArrayList<String> dateChallenge;

    public WelcomeFragment(){
        currentUser = ParseUser.getCurrentUser();
        friends = new ArrayList<Friend>();
        challenges = new ArrayList<Challenge>();
        friendsDic = new Hashtable<String, String>();
        dateChallenge = new ArrayList<String>();
    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) rootView.findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText(struser);

        String txtUserID = currentUser.getObjectId().toString();

        // récupération des points d'xp du user
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(txtUserID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    XPuser = object.getInt("experience");

                    //on crée les contraintes pour récupérer le level du user
                    ParseQuery<ParseObject> levelQuery = ParseQuery.getQuery("Level");
                    levelQuery.whereLessThanOrEqualTo("scoreMin", XPuser);
                    levelQuery.whereGreaterThanOrEqualTo("scoreMax", XPuser);
                    levelQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                levelNumber = object.getInt("level");

                                //on récupère le level et on l'affiche
                                TextView txtLevel = (TextView) rootView.findViewById(R.id.txtLevel);
                                txtLevel.setText(""+levelNumber);


                                //on crée les contraintes pour récupérer le titre du user
                                ParseQuery<ParseObject> titleQuery = ParseQuery.getQuery("titleByLevel");
                                titleQuery.whereLessThanOrEqualTo("levelMin", levelNumber);
                                titleQuery.whereGreaterThanOrEqualTo("levelMax", levelNumber);
                                titleQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null) {
                                            titleLevel = object.getString("title");

                                            //on affiche le titre du user
                                            TextView txtLevel = (TextView) rootView.findViewById(R.id.txtTitle);
                                            txtLevel.setText("You are a great " + titleLevel);
                                        } else {
                                            Log.d("score", "Retrieved the object.");
                                        }
                                    }
                                });

                            } else {
                                Log.d("score", "Retrieved the object.");
                            }
                        }
                    });

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });

        // Getting own challenges acceptations and sent

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
        ParseQuery<ParseObject> queryOwnChallengesChallenger = ParseQuery.getQuery("Attributed_challenge");
        queryOwnChallengesChallenger.whereEqualTo("user_id_applicant", currentUser.getObjectId());

        ParseQuery<ParseObject> queryOwnChallengesChallenged = ParseQuery.getQuery("Attributed_challenge");
        queryOwnChallengesChallenged.whereEqualTo("user_id", currentUser.getObjectId());

        ParseQuery<ParseObject> queryChallenges = ParseQuery.or(Arrays.asList(queryOwnChallengesChallenger, queryOwnChallengesChallenged));
        queryChallenges.whereEqualTo("finish_date", null);
        queryChallenges.addDescendingOrder("createdAt");
        queryChallenges.setLimit(10);

        List<ParseObject> listTempObject = null;

        try {
            listTempObject = queryChallenges.find();
        } catch (ParseException e) {
            Log.d("queryFail", "Query Object has failed : " + e.toString());
        }

        for (int j=0; j<listTempObject.size(); j++) {
            String challengeID = listTempObject.get(j).getObjectId();
            String challengedID = listTempObject.get(j).getString("user_id");
            String challengerID = listTempObject.get(j).getString("user_id_applicant");
            Challenge currentChallenge = null;

            if (challengedID.equals(currentUser.getObjectId())) {
                currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, new Friend(currentUser.getUsername(), challengedID), new Friend(friendsDic.get(challengerID), challengerID), currentUser);

                ParseQuery<ParseObject> queryOneChallenge = ParseQuery.getQuery("Challenge");
                queryOneChallenge.whereEqualTo("objectId", listTempObject.get(j).get("challenge_id"));

                ParseObject tempChallenge = null;

                try {
                    tempChallenge = queryOneChallenge.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Object has failed : " + e.toString());
                }

                if (tempChallenge != null) {
                    currentChallenge.setDescription(friendsDic.get(challengerID) + " challenges " + currentUser.getUsername() + " to " + tempChallenge.get("challenge"));
                }
            } else if (challengerID.equals(currentUser.getObjectId())) {
                currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, new Friend(friendsDic.get(challengedID), challengedID), new Friend(currentUser.getUsername(), challengerID), currentUser);

                ParseQuery<ParseObject> queryOneChallenge = ParseQuery.getQuery("Challenge");
                queryOneChallenge.whereEqualTo("objectId", listTempObject.get(j).get("challenge_id"));

                ParseObject tempChallenge = null;

                try {
                    tempChallenge = queryOneChallenge.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Object has failed : " + e.toString());
                }

                if (tempChallenge != null) {
                    currentChallenge.setDescription(currentUser.getUsername() + " challenges " + friendsDic.get(challengedID) + " to " + tempChallenge.get("challenge"));
                }
            } else {
                ParseQuery<ParseObject> queryOneChallenge = ParseQuery.getQuery("Challenge");
                queryOneChallenge.whereEqualTo("objectId", listTempObject.get(j).get("challenge_id"));

                ParseObject tempChallenge = null;

                try {
                    tempChallenge = queryOneChallenge.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Object has failed : " + e.toString());
                }
                if (tempChallenge != null) {
                    ParseQuery<ParseObject> queryChallenger = ParseQuery.getQuery("_User");
                    queryChallenger.whereEqualTo("objectId", challengerID);

                    ParseQuery<ParseObject> queryChallenged = ParseQuery.getQuery("_User");
                    queryChallenged.whereEqualTo("objectId", challengedID);
                    ParseObject tempFriend = null;

                    if (challengedID.equals(currentUser.getObjectId())) {
                        try {
                            tempFriend = queryChallenger.getFirst();
                        } catch (ParseException e) {
                            Log.d("queryFail", "Query Object has failed : " + e.toString());
                        }

                        if (tempFriend != null) {
                            currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, new Friend(friendsDic.get(challengedID), challengedID), new Friend(friendsDic.get(challengerID), challengerID), currentUser);

                            currentChallenge.setDescription(tempFriend.getString("username") + " challenges " + currentUser.getUsername() + " to " + tempChallenge.get("challenge"));
                        }
                    } else if (challengerID.equals(currentUser.getObjectId())) {
                        try {
                            tempFriend = queryChallenged.getFirst();
                        } catch (ParseException e) {
                            Log.d("queryFail", "Query Object has failed : " + e.toString());
                        }

                        if (tempFriend != null) {
                            currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, new Friend(friendsDic.get(challengedID), challengedID), new Friend(friendsDic.get(challengerID), challengerID), currentUser);

                            currentChallenge.setDescription(currentUser.getUsername() + " challenges " + tempFriend.getString("username") + " to " + tempChallenge.get("challenge"));
                        }
                    }
                }
            }

            if (currentChallenge != null) {
                SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");
                String creationDate = format.format(currentChallenge.getCreatedDate());
                dateChallenge.add("Sent on " + creationDate);
            }

            challenges.add(currentChallenge);
        }

        // Fill the ListView
        listChallenges = (ListView) rootView.findViewById(R.id.lv_challenges);

        final ChallengeAdapter challengesAdapter = new ChallengeAdapter(rootView.getContext(),R.layout.listitem_news,challenges, dateChallenge, R.id.tv_challenge,R.id.tv_date);

        listChallenges.setAdapter(challengesAdapter);

        // Add on click listener
        listChallenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Challenge c = (Challenge) parent.getAdapter().getItem(position);
                if (c.isCurrentUserChallenger()) {
                    Intent i = new Intent(rootView.getContext(), DescriptionChallengeActivity.class);
                    i.putExtra("description",c.getDescription());
                    i.putExtra("challengeID",c.getChallengeID());
                    Log.d("challenger","oui");
                    startActivity(i);

                } else if (c.isCurrentUserChallenged()) {
                    Intent i = new Intent(rootView.getContext(), DescriptionChallengeActivity.class);
                    i.putExtra("description",c.getDescription());
                    i.putExtra("challengeID",c.getChallengeID());
                    Log.d("challenged", "oui");
                    startActivity(i);

                } else {
                    Intent i = new Intent(rootView.getContext(), DescriptionChallengeActivity.class);
                    i.putExtra("description",c.getDescription());
                    i.putExtra("challengeID",c.getChallengeID());
                    startActivity(i);
                }
            }
        });



        return rootView;
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
}
