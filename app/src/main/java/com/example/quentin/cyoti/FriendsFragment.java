package com.example.quentin.cyoti;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;

import java.util.ArrayList;


public class FriendsFragment extends Fragment {
    private View rootView;
    private ArrayList<Friend> friends;

    public FriendsFragment() {
        friends = new ArrayList<Friend>();
        Friend friend1 = new Friend();
        Friend friend2 = new Friend("Toto", "Tutu", "TotoTutu");
        Friend friend3 = new Friend("James", "Morrison", "Jim");
        Friend friend4 = new Friend("Vincent", "Aunai", "Lodoss");
        Friend friend5 = new Friend("Truc", "Muche", "Osef");
        Friend friend6 = new Friend("Machin", "Bidule", "TrucBidule");
        Friend friend7 = new Friend("Test", "7", "test7");
        friends.add(friend1);
        friends.add(friend2);
        friends.add(friend3);
        friends.add(friend4);
        friends.add(friend5);
        friends.add(friend6);
        friends.add(friend7);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_friends, container, false);

        ListView listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
        listFriends.setClickable(true);

        FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(),
                R.layout.listitem_friend,
                friends);

        listFriends.setAdapter(friendAdapter);

        EditText addFriend = (EditText)rootView.findViewById(R.id.et_addFriend);
        addFriend.clearFocus();

        return rootView;
    }

}
