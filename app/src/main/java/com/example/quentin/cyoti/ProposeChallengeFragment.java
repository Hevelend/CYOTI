package com.example.quentin.cyoti;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ProposeChallengeFragment extends Fragment {
    private View rootView;
    private ArrayList<String> tempFriends;
    private ArrayList<Friend> friends;
    private ListView listFriends;
    private ParseObject tempObject;

    public ProposeChallengeFragment() {
        tempFriends = new ArrayList<String>();
        friends = new ArrayList<Friend>();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_propose_challenge, container, false);

        EditText writeChallenge = (EditText)rootView.findViewById(R.id.et_challenge);
        writeChallenge.setFocusable(false);

        Spinner spinner = (Spinner)rootView.findViewById(R.id.sp_challenge_themes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.challenge_themes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

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

        if (listFriends.isClickable()) Log.d("pcfList", "liste clickable");

        FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(),
                                                        R.layout.listitem_friend,
                                                        friends);

        listFriends.setAdapter(friendAdapter);

        listFriends.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d("listclick", "click sur item "+position);
                Friend f = (Friend)parent.getAdapter().getItem(position);

                CheckBox cbFriend = (CheckBox)rootView.findViewById(R.id.cb_friendCkeck);

                if (f.isChecked()) {
                    cbFriend.setChecked(false);
                    f.setChecked(false);
                }

                else {
                    cbFriend.setChecked(true);
                    f.setChecked(true);
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
        ParseUser currentUser = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");

        try {
            tempObject = query.get(currentUser.getObjectId());
            Log.d("queryOK", "Results found in query");
        } catch (ParseException e) {
            Log.d("queryFail", "Query has failed : " + e.toString());
        }

        if (tempObject != null) {
            Log.d("tpObj", "tempObject not null");

            tempFriends = (ArrayList<String>) tempObject.get("friend_list");

            for (int i=0;i<tempFriends.size();i++) {
                friends.add(new Friend(tempFriends.get(i)));
            }
        }
    }
}
