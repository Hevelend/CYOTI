package com.example.quentin.cyoti;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;


public class VoteChallengeFragment extends Fragment {
    private Friend friendChallenger = new Friend();
    private Friend friendChallenged = new Friend("Vincent","Aunai", "TiLodoss");
    private Challenge challenge = new Challenge();

    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        challenge.setDescription("dance in McDonald's");
        rootView = inflater.inflate(R.layout.fragment_vote_challenge, container, false);

        TextView descriptionChallenge = (TextView) rootView.findViewById(R.id.tv_descriptionChallenge);
        descriptionChallenge.setText(friendChallenger.getNickName()
                + " challenged "
                + friendChallenged.getNickName()
                + " to "
                + challenge.getDescription());

        ProgressBar vote = (ProgressBar) rootView.findViewById(R.id.pb_vote);
        vote.setRotation(90f);
        vote.setProgress(75);



        return rootView;
    }
}
