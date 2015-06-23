package com.example.quentin.cyoti;

import android.app.Activity;
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
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.ChallengeAdapter;
import com.example.quentin.cyoti.adapters.StringAdapter;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.metier.User;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.Hashtable;
import java.util.List;


public class UserFragment extends Fragment {
    private View rootView;
    private final ParseUser currentUser;
    private Friend friendUser;
    private ArrayList<String> datesChallenge;
    private ArrayList<String> friends;
    private ListView listChallenges;
    private Hashtable<String,Friend> friendsDic;
    private ArrayList<Challenge> challenges;

    private OnUserListener mCallback;
    private ParseObject tempObject = null;
    private static User user = new User();
    private boolean queryOK = false;
    private int i = 0;

    public UserFragment() {
        currentUser = ParseUser.getCurrentUser();
        datesChallenge = new ArrayList<String>();
        friends = (ArrayList<String>) currentUser.get("friend_list");
        challenges = new ArrayList<Challenge>();
        friendsDic = new Hashtable<String, Friend>();

        // Create notifications' installation
        createParseInstallation();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_user, container, false);

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

        friendUser = new Friend(rootView.getContext(), currentUser.getUsername(), currentUser.getObjectId(), avatarUser);


        // Getting friends challenges acceptations, sent ans finished

        //reset of challenges list
        challenges.clear();

        // Get actual friends infos of user
        // Create a Friend object for each friend and put it in the dictionary
        if (friends != null) {
            for (int i = 0; i < friends.size(); i++) {
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

                    Friend tempObjectFriend = new Friend(rootView.getContext(), friend.getString("username"), friend.getObjectId(), avatar);
                    friendsDic.put(friend.getObjectId(), tempObjectFriend);
                } catch (ParseException e) {
                    Log.d("getFriendID", e.getMessage());
                    Toast.makeText(getActivity().getApplicationContext(),
                            "Problem getting friends informations. Please try later.", Toast.LENGTH_SHORT).show();
                }

            }
        }

        // Get challenges
        ParseQuery<ParseObject> queryChallengesChallenger = ParseQuery.getQuery("Attributed_challenge");
        queryChallengesChallenger.whereContainedIn("user_id_applicant", Collections.list(friendsDic.keys()));

        ParseQuery<ParseObject> queryChallengesChallenged = ParseQuery.getQuery("Attributed_challenge");
        queryChallengesChallenged.whereContainedIn("user_id", Collections.list(friendsDic.keys()));
        queryChallengesChallenged.whereNotEqualTo("user_id_applicant", currentUser.getObjectId());

        ParseQuery<ParseObject> queryChallenges = ParseQuery.or(Arrays.asList(queryChallengesChallenger, queryChallengesChallenged));
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
            for (int j = 0; j < listTempObject.size(); j++) {
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

                if (tempChallenge != null) {
                    if (Collections.list(friendsDic.keys()).contains(challengerID)) {
                        if (Collections.list(friendsDic.keys()).contains(challengedID)) {
                            currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), (Date)listTempObject.get(j).get("finish_date"), friendsDic.get(challengedID), friendsDic.get(challengerID), currentUser, listTempObject.get(j).getString("success"));
                            currentChallenge.setDescription(friendsDic.get(challengerID).getFirstName() + " challenges " + friendsDic.get(challengedID).getFirstName() + " to " + tempChallenge.get("challenge"));
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
                                Friend tempNotFriend = new Friend(rootView.getContext(), tempFriend.getString("username"), tempFriend.getObjectId(), null);

                                currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), (Date)listTempObject.get(j).get("finish_date"), tempNotFriend, friendsDic.get(challengerID), currentUser, listTempObject.get(j).getString("success"));
                                currentChallenge.setDescription(friendsDic.get(challengerID).getFirstName() + " challenges " + tempNotFriend.getFirstName() + " to " + tempChallenge.get("challenge"));
                            }
                        }
                    } else {
                        if (Collections.list(friendsDic.keys()).contains(challengedID)) {
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

                                Friend tempNotFriend = new Friend(rootView.getContext(), tempFriend.getString("username"), tempFriend.getObjectId(), tempAvatar);

                                currentChallenge = new Challenge(challengeID, "null", listTempObject.get(j).getCreatedAt(), (Date)listTempObject.get(j).get("finish_date"), friendsDic.get(challengedID), tempNotFriend, currentUser, listTempObject.get(j).getString("success"));
                                currentChallenge.setDescription(tempNotFriend.getFirstName() + " challenges " + friendsDic.get(challengedID) + " to " + tempChallenge.get("challenge"));
                            }
                        } else {
                            Log.d("impossible","either challenger or challenged is a friend");
                        }
                    }
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

        final ChallengeAdapter challengesAdapter = new ChallengeAdapter(rootView.getContext(),R.layout.listitem_news, challenges, datesChallenge, R.id.tv_challenge,R.id.tv_date);

        listChallenges.setAdapter(challengesAdapter);

        // Add on click listener
        listChallenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long l) {
                Challenge c = (Challenge) parent.getAdapter().getItem(position);
                if (c.isCurrentUserChallenger()) {
                    // should not happen in this fragment
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

    public void createParseInstallation() {
        ParseInstallation installation = ParseInstallation.getCurrentInstallation();

        installation.put("newChallenge", true);
        installation.put("newFriend", true);
        installation.put("newEvidence", true);
        installation.put("newVote", true);
        installation.put("user_id", currentUser);

        installation.saveInBackground();
    }



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

}
