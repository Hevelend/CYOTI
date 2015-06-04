package com.example.quentin.cyoti;

import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParsePush;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.Date;


/**
 * A placeholder fragment containing a simple view.
 */
public class ProposeChallengeFragment extends Fragment {
    private View rootView;
    private ArrayList<String> tempFriends;
    private ArrayList<Friend> friends;
    private ArrayList<Friend> friendsSelected;
    private ListView listFriends;
    private ArrayAdapter<Friend> friendAdapter;
    private ParseObject tempObject;
    private String tempObjectID = "idTest";
    private ParseUser currentUser;
    private String themeID;

    public ProposeChallengeFragment() {
        tempFriends = new ArrayList<String>();
        friends = new ArrayList<Friend>();
        friendsSelected = new ArrayList<Friend>();
        currentUser = ParseUser.getCurrentUser();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_propose_challenge, container, false);

        final EditText writeChallenge = (EditText)rootView.findViewById(R.id.et_challenge);

        Spinner spinner = (Spinner)rootView.findViewById(R.id.sp_challenge_themes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.challenge_themes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                themeID = (String) parent.getAdapter().getItem(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

//        Bundle args = getArguments();
//
//        if (args != null) {
//            Log.d("args", "ProposeChallengeFragment - args non null");
//            this.friends = (ArrayList<String>) args.get("friends");
//        }
//
//        if (friends.size() == 0) {
//            Log.d("friendsNull", "liste friends vide");
//        }

        getFriends();

        listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
        listFriends.setClickable(true);

        friendAdapter = new FriendAdapter(rootView.getContext(), R.layout.listitem_friend, friends);

        listFriends.setAdapter(friendAdapter);

        listFriends.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend f = (Friend) parent.getAdapter().getItem(position);
                CheckBox cbFriend = (CheckBox) view.findViewById(R.id.cb_friendCheck);

                cbFriend.toggle();

                if (f.isSelected()) {
                    friendsSelected.remove(f);
                    f.setSelected(false);
                    ((View) cbFriend.getParent()).setBackgroundColor(0);
                } else {
                    friendsSelected.add(f);
                    f.setSelected(true);

                    /* TODO : Changer la couleur du background après la nouvelle interface */
                    ((View) cbFriend.getParent()).setBackgroundColor(Color.LTGRAY);
                }
            }
        });

        Button btSendChallenge = (Button) rootView.findViewById(R.id.bt_sendChallenge);
        btSendChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendsSelected != null || friendsSelected.size() != 0) {
                    // Add challenge in database
                    final ParseObject mychallenge = new ParseObject("Challenge");

                    if (writeChallenge.getText().toString().equals(writeChallenge.getHint())) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "You have to write a description !", Toast.LENGTH_SHORT).show();
                    } else {
                        mychallenge.put("challenge", writeChallenge.getText().toString());
                    }

                    if (themeID.equals("Choose a theme for your challenge")) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "You have to choose a theme !", Toast.LENGTH_SHORT).show();
                    } else {
                        mychallenge.put("theme_id", themeID);
                    }

                    try {
                        mychallenge.save();

                        for (int i = 0; i < friendsSelected.size(); i++) {
                            ParseObject myattributed = new ParseObject("Attributed_challenge");
                            ParseQuery<ParseObject> queryFriend = ParseQuery.getQuery("_User");
                            queryFriend.whereEqualTo("username", friendsSelected.get(i).getFirstName());

                            ParseObject friend = null;

                            try {
                                friend = queryFriend.getFirst();
                                myattributed.put("user_id", friend.getObjectId());
                                myattributed.put("challenge_id", mychallenge.getObjectId());
                                myattributed.put("user_id_applicant", currentUser.getObjectId());
                                myattributed.put("sending_date", new Date());
                                myattributed.save();

                                // Create notification
                                ParseQuery<ParseObject> userQuery = ParseQuery.getQuery("_User");
                                userQuery.whereEqualTo("objectId", friend.getObjectId());

                                ParseQuery pushQuery = ParseInstallation.getQuery();
                                pushQuery.whereEqualTo("user", userQuery);

                                // Send notification
                                ParsePush push = new ParsePush();
                                push.setQuery(pushQuery);
                                push.setMessage(currentUser.getUsername() + "challenged you !");
                                push.sendInBackground();

                            } catch (ParseException e) {
                                Log.d("attChal", e.getMessage());
                                Toast.makeText(getActivity().getApplicationContext(),
                                        "Problem attributing challenge. Please try later.", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } catch (ParseException e) {
                        Log.d("addChal", e.getMessage());
                        Toast.makeText(getActivity().getApplicationContext(),
                                "Problem sending challenge. Please try later.", Toast.LENGTH_SHORT).show();
                    }
                }

                // Unselect friends
                for (int i=0; i<friendsSelected.size(); i++) {
                    friendsSelected.get(i).setSelected(false);
                }

                // Clear friends list
                friendsSelected.clear();

                Toast.makeText(getActivity().getApplicationContext(),
                        "Challenge sent !", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }

//    public void updateFriendsList(ArrayList<String> friends) {
//        this.friends = friends;
//        listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
//
//        FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(),
//                                                        R.layout.listitem_friend,
//                                                        this.friends);
//
//        listFriends.setAdapter(friendAdapter);
//    }


    public void getFriends() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        friends.clear();

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

    public void saveMyID(String myid) {
        tempObjectID = myid;
    }

}
