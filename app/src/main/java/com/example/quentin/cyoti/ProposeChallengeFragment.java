package com.example.quentin.cyoti;

import android.support.v4.app.Fragment;
import android.os.Bundle;
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


/**
 * A placeholder fragment containing a simple view.
 */
public class ProposeChallengeFragment extends Fragment {
    private View rootView;
    private ArrayList<Friend> friends;

    public ProposeChallengeFragment() {
        friends = new ArrayList<Friend>();
        Friend friend1 = new Friend();
        Friend friend2 = new Friend("Toto", "Tutu", "TotoTutu","mipmap/cup");
        friends.add(friend1);
        friends.add(friend2);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_propose_challenge, container, false);

        EditText writeChallenge = (EditText)rootView.findViewById(R.id.et_challenge);
        writeChallenge.clearFocus();

        Spinner spinner = (Spinner)rootView.findViewById(R.id.sp_challenge_themes);

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(rootView.getContext(),
                R.array.challenge_themes, android.R.layout.simple_spinner_item);

        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        spinner.setAdapter(adapter);

        ListView listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
        listFriends.setClickable(true);

        FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(),
                                                        R.layout.listitem_friend,
                                                        friends);

        listFriends.setAdapter(friendAdapter);

        return rootView;
    }
}
