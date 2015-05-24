package com.example.quentin.cyoti;

import android.graphics.Color;
import android.graphics.Typeface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.ParseException;
import com.parse.ParseObject;
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
    private Friend friendSelected;
    private ListView listFriends;
    private ParseObject tempObject;
    private String tempObjectID = "idTest";
    private ParseUser currentUser;
    private String themeID;
    private boolean friendsCollected = false;

    public ProposeChallengeFragment() {
        tempFriends = new ArrayList<String>();
        friends = new ArrayList<Friend>();
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

        if (!friendsCollected) getFriends();

        listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
        listFriends.setClickable(true);

        ArrayAdapter<Friend> friendAdapter = new FriendAdapter(rootView.getContext(), R.layout.listitem_friend, friends);

        listFriends.setAdapter(friendAdapter);

        listFriends.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend f = (Friend) parent.getAdapter().getItem(position);

                TextView tvFriend = (TextView) view.findViewById(R.id.tv_friend);

                if (f.isSelected()) {
                    f.setSelected(false);
                    parent.getChildAt(position).setBackgroundColor(0);
                    tvFriend.setTypeface(null, Typeface.NORMAL);
                } else {
                    friendSelected = f;
                    f.setSelected(true);
                    tvFriend.setTypeface(null, Typeface.BOLD);
                    parent.getChildAt(position).setBackgroundColor(Color.LTGRAY);
                }
            }
        });

        Button btSendChallenge = (Button) rootView.findViewById(R.id.bt_sendChallenge);
        btSendChallenge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (friendSelected != null) {
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

                    mychallenge.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("chg", "Query object ok");
                                saveMyID(mychallenge.getObjectId().toString());
                            } else {
                                Log.d("chg", "The getFirst request failed.");
                            }
                        }
                    });

                    String tempChallengeID = tempObjectID;

                    // Attribute a challenge to a friend
                    ParseObject myattributed = new ParseObject("Attributed_challenge");
                    myattributed.put("challenge_id", tempChallengeID);
                    myattributed.put("user_id", currentUser.getObjectId());
                    myattributed.put("user_id_applicant", friendSelected.getFirstName());
                    myattributed.put("sending_date", new Date());
                    myattributed.saveInBackground();
                }
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

        friendsCollected = true;
    }

    public void saveMyID(String myid) {
        tempObjectID = myid;
    }
}
