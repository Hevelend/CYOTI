package com.example.quentin.cyoti;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
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
import java.util.List;


public class FriendsFragment extends Fragment {
    private View rootView;
    private ArrayList<Friend> friends;
    private boolean friendsCollected = false;
    private ArrayList<Friend> friendsSelected;
    private ParseUser currentUser;

    public FriendsFragment() {
        currentUser = ParseUser.getCurrentUser();

        friendsSelected = new ArrayList<Friend>();
        friends = new ArrayList<Friend>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ListView listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
        listFriends.setClickable(true);

        if (!friendsCollected) getFriends();

        FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(), R.layout.listitem_friend, friends);

        listFriends.setAdapter(friendAdapter);

        listFriends.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Friend f = (Friend) parent.getAdapter().getItem(position);

                TextView tvFriendSelected = (TextView) view.findViewById(R.id.tv_friendSelected);

                if (f.isSelected()) {
                    friendsSelected.remove(f);
                    f.setSelected(false);
                    tvFriendSelected.setText("");
                } else {
                    friendsSelected.add(f);
                    f.setSelected(true);
                    tvFriendSelected.setText("Selected !");
                }
            }
        });

        Button btRemoveFriend = (Button) rootView.findViewById(R.id.bt_removeFriend);
        btRemoveFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> newFriends = new ArrayList<String>();

                if (!friendsSelected.isEmpty()) {
                    // remove friends in database

                    friends.removeAll(friendsSelected);

                    newFriends = myStringListMaker(friends);

                    currentUser.put("friend_list", newFriends);

                    currentUser.saveInBackground(new SaveCallback() {
                        public void done(ParseException e) {
                            if (e == null) {
                                Log.d("chg", "Query object ok");
                                Toast.makeText(getActivity().getApplicationContext(), "Friends removed", Toast.LENGTH_LONG).show();
                            } else {
                                Log.d("chg", "The getFirst request failed.");
                            }
                        }
                    });

                    currentUser.saveInBackground();
                }
            }
        });

        // Partie ajout d'amis
        final EditText addFriend = (EditText)rootView.findViewById(R.id.et_addFriend);
        addFriend.clearFocus();

        ImageButton ibAddFriend = (ImageButton) rootView.findViewById(R.id.ib_addFriend);
        ibAddFriend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ArrayList<String> newFriends = new ArrayList<String>();
                String textAddFriend = addFriend.getText().toString();
                if (textAddFriend.isEmpty()) {
                    Toast.makeText(getActivity().getApplicationContext(),
                            "You have to enter the pseudo of a friend !", Toast.LENGTH_SHORT).show();
                }
                else {

                    ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                    query.whereEqualTo("username", textAddFriend);
                    int count = 0;
                    try {
                        count = query.count();
                    } catch (ParseException e) {
                        Log.d("queryFail", "Query has failed : " + e.toString());
                    }


                    if (count < 1) {
                        Toast.makeText(getActivity().getApplicationContext(),
                                "This user does not exist", Toast.LENGTH_SHORT).show();
                    }
                    else {
                        friends.add(new Friend(textAddFriend));
                        newFriends = myStringListMaker(friends);

                        currentUser.put("friend_list", newFriends);

                        currentUser.saveInBackground(new SaveCallback() {
                            public void done(ParseException e) {
                                if (e == null) {
                                    Log.d("chg", "Query object ok");
                                    Toast.makeText(getActivity().getApplicationContext(), "Friend added", Toast.LENGTH_LONG).show();
                                } else {
                                    Log.d("chg", "The getFirst request failed.");
                                }
                            }
                        });

                        currentUser.saveInBackground();

                    }
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

            for (int i=0;i<tempFriends.size();i++) {
                friends.add(new Friend(tempFriends.get(i)));
            }
        }

        friendsCollected = true;
    }

    private ArrayList<String> myStringListMaker(ArrayList<Friend> friendList) {
        ArrayList<String> result = new ArrayList<String>();
        for (Friend f : friendList) {
            result.add(f.getFirstName());
        }
        return result;
    }

}
