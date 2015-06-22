package com.example.quentin.cyoti;

import com.example.quentin.cyoti.adapters.ChallengeAdapter;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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

    private View rootView;
    private final ParseUser currentUser;
    private Friend friendUser;
    public int XPuser;
    public int levelNumber;
    public String titleLevel;
    public ImageView iv_user;

    private ArrayList<String> friends;
    private ListView listChallenges;
    private Hashtable<String,Friend> friendsDic;
    private ArrayList<Challenge> challenges;
    private ArrayList<String> datesChallenge;

    public WelcomeFragment(){
        currentUser = ParseUser.getCurrentUser();
        friends = (ArrayList<String>) currentUser.get("friend_list");
        challenges = new ArrayList<Challenge>();
        friendsDic = new Hashtable<String, Friend>();
        datesChallenge = new ArrayList<String>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Create a Friend object for the user
        ParseFile imageFile = currentUser.getParseFile("avatar");
        Bitmap avatarUser = null;
        byte[] data = null;
        if (imageFile != null) {
            try {
                data = imageFile.getData();
            } catch (ParseException e) {
                Log.d("avatarUser", "Error: " + e.getMessage());
            }
            if (data != null) {
                BitmapFactory.Options options = new BitmapFactory.Options();
                avatarUser = BitmapFactory.decodeByteArray(data, 0, data.length, options);
            }
        }

        friendUser = new Friend(rootView.getContext(),currentUser.getUsername(),currentUser.getObjectId(),avatarUser);

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) rootView.findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText(struser);

        String txtUserID = currentUser.getObjectId().toString();

        // récupération des points d'xp du user
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Experience");
        query.whereEqualTo("user_id", txtUserID);
        query.getFirstInBackground(new GetCallback<ParseObject>() {
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
                                txtLevel.setText("" + levelNumber);


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

        // Get actual friends infos of user
        // Create a Friend object for each friend and put it in the dictionary
        for (int i=0; i<friends.size(); i++) {
            ParseQuery<ParseObject> queryFriend = ParseQuery.getQuery("_User");
            queryFriend.whereEqualTo("username", friends.get(i));
            ParseObject friend = null;
            try {
                friend = queryFriend.getFirst();
                ParseFile imageFile2 = friend.getParseFile("avatar");
                Bitmap avatar = null;
                byte[] data2 = null;
                if (imageFile2 != null) {
                    try {
                        data2 = imageFile2.getData();
                    } catch (ParseException e) {
                        Log.d("avatar", "Error: " + e.getMessage());
                    }
                    if (data2 != null) {
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        avatar = BitmapFactory.decodeByteArray(data2, 0, data2.length, options);
                    }
                }

                Friend tempObjectFriend = new Friend(rootView.getContext(),friend.getString("username"),friend.getObjectId(),avatar);
                friendsDic.put(friend.getObjectId(),tempObjectFriend );
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
        queryChallenges.whereNotEqualTo("accepting_date", null);
        queryChallenges.addDescendingOrder("createdAt");
        queryChallenges.setLimit(10);

        List<ParseObject> listTempObject = null;

        try {
            listTempObject = queryChallenges.find();
        } catch (ParseException e) {
            Log.d("queryFail", "Query Object has failed : " + e.toString());
        }

        if (listTempObject != null) {
            for (int j=0; j<listTempObject.size(); j++) {
                String challengeID = listTempObject.get(j).getObjectId();
                String challengedID = listTempObject.get(j).getString("user_id");
                String challengerID = listTempObject.get(j).getString("user_id_applicant");
                Challenge currentChallenge = null;


                // Get challenge description
                ParseQuery<ParseObject> queryOneChallenge = ParseQuery.getQuery("Challenge");
                queryOneChallenge.whereEqualTo("objectId", listTempObject.get(j).get("challenge_id"));
                ParseObject tempChallenge = null;

                try {
                    tempChallenge = queryOneChallenge.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Object has failed : " + e.toString());
                }

                if (challengedID.equals(currentUser.getObjectId())) {

                    if (Collections.list(friendsDic.keys()).contains(challengerID)) {
                        currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, friendUser, friendsDic.get(challengerID), currentUser, null);

                        if (tempChallenge != null) {
                            currentChallenge.setDescription(friendsDic.get(challengerID).getFirstName() + " challenges " + currentUser.getUsername() + " to " + tempChallenge.get("challenge"));
                        }
                    } else {
                        // Query challenger name and avatar
                        ParseQuery<ParseObject> queryChallenger = ParseQuery.getQuery("_User");
                        queryChallenger.whereEqualTo("objectId", challengerID);
                        ParseObject tempFriend = null;

                        try {
                            tempFriend = queryChallenger.getFirst();
                        } catch (ParseException e) {
                            Log.d("queryFail", "Query Object has failed : " + e.toString());
                        }

                        if (tempFriend != null) {

                            ParseFile imageFile3 = tempFriend.getParseFile("avatar");
                            Bitmap tempAvatar = null;
                            byte[] data3 = null;
                            if (imageFile3 != null) {
                                try {
                                    data3 = imageFile3.getData();
                                } catch (ParseException e) {
                                    Log.d("tempAvatar", "Error: " + e.getMessage());
                                }
                                if (data3 != null) {
                                    BitmapFactory.Options options = new BitmapFactory.Options();
                                    tempAvatar = BitmapFactory.decodeByteArray(data3, 0, data3.length, options);
                                }
                            }

                            Friend tempNotFriend = new Friend(rootView.getContext(),tempFriend.getString("username"),tempFriend.getObjectId(),tempAvatar);

                            currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, friendUser, tempNotFriend, currentUser, null);

                            if (tempChallenge != null) {
                                currentChallenge.setDescription(tempNotFriend.getFirstName() + " challenges " + currentUser.getUsername() + " to " + tempChallenge.get("challenge"));
                            }
                        }
                    }
                } else if (challengerID.equals(currentUser.getObjectId())) {

                    if (Collections.list(friendsDic.keys()).contains(challengedID)) {
                        currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, friendsDic.get(challengedID), friendUser, currentUser, null);

                        if (tempChallenge != null) {
                            currentChallenge.setDescription(currentUser.getUsername() + " challenges " + friendsDic.get(challengedID).getFirstName() + " to " + tempChallenge.get("challenge"));
                        }
                    } else {
                        // Query challenged name (no need of avatar)
                        ParseQuery<ParseObject> queryChallenged = ParseQuery.getQuery("_User");
                        queryChallenged.whereEqualTo("objectId", challengedID);
                        ParseObject tempFriend = null;

                        try {
                            tempFriend = queryChallenged.getFirst();
                        } catch (ParseException e) {
                            Log.d("queryFail", "Query Object has failed : " + e.toString());
                        }

                        if (tempFriend != null) {
                            Friend tempNotFriend = new Friend(rootView.getContext(),tempFriend.getString("username"),tempFriend.getObjectId(),null);

                            currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), null, tempNotFriend, friendUser, currentUser, null);

                            if (tempChallenge != null) {
                                currentChallenge.setDescription(tempNotFriend.getFirstName() + " challenges " + currentUser.getUsername() + " to " + tempChallenge.get("challenge"));
                            }
                        }
                    }
                } else {
                    Log.d("impossible","user should always be challenger or challenged of every challenge in this fragment");
                }

                if (currentChallenge != null) {
                    SimpleDateFormat format = new SimpleDateFormat("EEE, dd MMM yyyy");
                    String creationDate = format.format(currentChallenge.getCreatedDate());
                    datesChallenge.add("Sent on " + creationDate);
                }

                challenges.add(currentChallenge);
            }
        }

        // Fill the ListView
        listChallenges = (ListView) rootView.findViewById(R.id.lv_challenges);

        final ChallengeAdapter challengesAdapter = new ChallengeAdapter(rootView.getContext(),R.layout.listitem_news,challenges, datesChallenge, R.id.tv_challenge,R.id.tv_date);

        listChallenges.setAdapter(challengesAdapter);

        // Add on click listener
        listChallenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Challenge c = (Challenge) parent.getAdapter().getItem(position);
                if (c.isCurrentUserChallenger()) {
                    Intent i = new Intent(rootView.getContext(), DescriptionChallengeActivity.class);
                    i.putExtra("description", c.getDescription());
                    i.putExtra("challengeID", c.getChallengeID());
                    i.putExtra("isCurrentUserChallenger","true");
                    i.putExtra("isCurrentUserChallenged","false");
                    startActivity(i);

                } else if (c.isCurrentUserChallenged()) {
                    Intent i = new Intent(rootView.getContext(), DescriptionChallengeActivity.class);
                    i.putExtra("description", c.getDescription());
                    i.putExtra("challengeID", c.getChallengeID());
                    i.putExtra("isCurrentUserChallenger","false");
                    i.putExtra("isCurrentUserChallenged","true");
                    startActivity(i);

                } else {
                    // should not happen in this fragment
                    Intent i = new Intent(rootView.getContext(), DescriptionChallengeActivity.class);
                    i.putExtra("description", c.getDescription());
                    i.putExtra("challengeID", c.getChallengeID());
                    i.putExtra("isCurrentUserChallenger","false");
                    i.putExtra("isCurrentUserChallenged","false");
                    startActivity(i);
                }
            }
        });



        return rootView;
    }
}
