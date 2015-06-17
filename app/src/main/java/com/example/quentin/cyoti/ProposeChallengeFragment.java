package com.example.quentin.cyoti;

import android.database.Cursor;
import android.graphics.Color;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v4.widget.SimpleCursorAdapter;
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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.utilities.Mail;
import com.example.quentin.cyoti.utilities.PushNotification;
import com.parse.GetCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

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
    private String themeID;

    private ParseUser currentUser;
    private ParseObject friendDB;

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

        if (friends.size() == 0) {
            Toast.makeText(getActivity().getApplicationContext(), "Retrivieving friends", Toast.LENGTH_SHORT).show();
        }

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
                        ParseACL accl = new ParseACL();
                        accl.setPublicWriteAccess(true);
                        accl.setPublicReadAccess(true);
                        mychallenge.setACL(accl);

                        mychallenge.save();

                        for (int i = 0; i < friendsSelected.size(); i++) {
                            ParseObject myattributed = new ParseObject("Attributed_challenge");
                            ParseQuery<ParseObject> queryFriend = ParseQuery.getQuery("_User");
                            queryFriend.whereEqualTo("username", friendsSelected.get(i).getFirstName());

                            try {
                                friendDB = queryFriend.getFirst();
                                myattributed.put("user_id", friendDB.getObjectId());
                                myattributed.put("challenge_id", mychallenge.getObjectId());
                                myattributed.put("user_id_applicant", currentUser.getObjectId());
                                myattributed.put("sending_date", new Date());

                                myattributed.setACL(accl);
                                myattributed.save();

                                if (PushNotification.isNotificationsAllowed(friendDB)) {
                                    PushNotification.sendNotification(currentUser, friendDB,
                                            PushNotification.NEW_CHALLENGE_NOTIFICATION);
                                }

                                Mail m = new Mail();

                                if (m.isMailingAllowed(friendDB)) {
                                    m.sendNewChallengeAsyncMail(m, currentUser, friendDB, writeChallenge.getText().toString());
                                }

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
                    friendAdapter.notifyDataSetChanged();
                }

                // Clear friends list
                friendsSelected.clear();

                Toast.makeText(getActivity().getApplicationContext(),
                        "Challenge sent !", Toast.LENGTH_SHORT).show();
            }
        });

        return rootView;
    }


    public void getFriends() {
        ParseQuery<ParseObject> queryFriends = ParseQuery.getQuery("_User");
        friends.clear();

        queryFriends.getInBackground(currentUser.getObjectId(), new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if (parseObject == null) {
                    Toast.makeText(getActivity().getApplicationContext(),
                                    "Problem retrieving friends", Toast.LENGTH_SHORT).show();

                    Log.d("rqFriends", "Problem retrieving friends");
                }

                else {
                    tempFriends = (ArrayList<String>) parseObject.get("friend_list");

                    if (tempFriends != null) {
                        for (int i = 0; i < tempFriends.size(); i++) {
                            friends.add(new Friend(tempFriends.get(i)));
                        }
                    }
                }
            }
        });
    }
}
