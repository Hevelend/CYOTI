package com.example.quentin.cyoti;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Friend;

import java.util.ArrayList;


/**
 * A placeholder fragment containing a simple view.
 */
public class ProposeChallengeFragment extends Fragment {
    private View rootView;
    private ArrayList<String> friends;
    private ListView listFriends;

    public ProposeChallengeFragment() {
        friends = new ArrayList<String>();
//        Friend friend1 = new Friend();
//        Friend friend2 = new Friend("Toto", "Tutu", "TotoTutu");
//        Friend friend3 = new Friend("James", "Morrison", "Jim");
//        Friend friend4 = new Friend("Vincent","Aunai","Lodoss");
//        Friend friend5 = new Friend("Truc", "Muche", "Osef");
//        Friend friend6 = new Friend("Machin", "Bidule", "TrucBidule");
//        Friend friend7 = new Friend("Test", "7", "test7");
//        friends.add(friend1);
//        friends.add(friend2);
//        friends.add(friend3);
//        friends.add(friend4);
//        friends.add(friend5);
//        friends.add(friend6);
//        friends.add(friend7);
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

        Bundle args = getArguments();

        if (args != null) {
            this.friends = args.getStringArrayList("friends");
        }

        listFriends = (ListView)rootView.findViewById(R.id.lv_friends);
        listFriends.setClickable(true);

        FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(),
                                                        R.layout.listitem_friend,
                                                        friends);

        listFriends.setAdapter(friendAdapter);

        return rootView;
    }

    public void updateFriendsList(ArrayList<String> friends) {
        this.friends = friends;

       FriendAdapter friendAdapter = new FriendAdapter(rootView.getContext(),
                R.layout.listitem_friend,
                this.friends);

        listFriends.setAdapter(friendAdapter);
    }

    public void updateFriendsList() {
        Bundle args = getArguments();
//        this.friends = args.getStringArrayList("friends");
//
//        if (this.friends.size() == 0) {
//            Toast.makeText(getActivity().getApplicationContext(),
//                    "liste friends vide",
//                    Toast.LENGTH_SHORT).show();
//
//        }
//
//        else updateFriendsList(this.friends);

        TextView tvWho = (TextView)rootView.findViewById(R.id.tv_who);
        tvWho.setText(args.get("test").toString());
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }
}
