package com.example.quentin.cyoti;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class VoteChallengeFragment extends Fragment {
    private Friend friendChallenger;
    private Friend friendChallenged;
    private Challenge challenge = new Challenge();
    private Double purcentVote = 0.0;
    private View rootView;
    private ProgressBar vote;
    private TextView tvPurcent;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getChallenge();
        getAttributedChallenge();
        getPurcentageVote();

        rootView = inflater.inflate(R.layout.fragment_vote_challenge, container, false);

        TextView descriptionChallenge = (TextView) rootView.findViewById(R.id.tv_descriptionChallenge);
        descriptionChallenge.setText(friendChallenger.getFirstName()
                + " challenged "
                + friendChallenged.getFirstName()
                + " to "
                + challenge.getDescription());

        vote = (ProgressBar) rootView.findViewById(R.id.pb_vote);
        vote.setRotation(90f);
        vote.setProgress(purcentVote.intValue());

        tvPurcent = (TextView) rootView.findViewById(R.id.tv_purcent);
        tvPurcent.setText(String.format("%.1f", purcentVote) + "%");

        ImageButton btLike = (ImageButton) rootView.findViewById(R.id.bt_like);
        ImageButton btUnlike = (ImageButton) rootView.findViewById(R.id.bt_unlike);

        btLike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasVoted()) {
                    ParseObject vote = new ParseObject("Vote");
                    vote.put("attributed_challenge_id", "idTest");
                    vote.put("vote_yes", true);
                    vote.put("vote_no", false);
                    vote.put("user_id", ParseUser.getCurrentUser().getObjectId());

                    try {
                        vote.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    updateProgressBar();

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Vote saved - Victory", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btUnlike.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!hasVoted()) {
                    ParseObject vote = new ParseObject("Vote");
                    vote.put("attributed_challenge_id", "idTest");
                    vote.put("vote_yes", false);
                    vote.put("vote_no", true);
                    vote.put("user_id", ParseUser.getCurrentUser().getObjectId());

                    try {
                        vote.save();
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }

                    updateProgressBar();

                    Toast.makeText(getActivity().getApplicationContext(),
                            "Vote saved - Defeat", Toast.LENGTH_SHORT).show();
                }
            }
        });

        return rootView;
    }


    public void getChallenge() {
        ParseObject parseChallenge;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
        query.whereEqualTo("objectId", "bEAxCfTnKK");

        try {
            parseChallenge = query.getFirst();
            challenge.setDescription(parseChallenge.get("challenge").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getAttributedChallenge() {
        ParseObject parseAttributedChallenge;
        ParseObject parseUser;
        String userChallengerID = null;

        ParseQuery<ParseObject> queryAttributed = ParseQuery.getQuery("Attributed_challenge");
        queryAttributed.whereEqualTo("challenge_id", "idTest");

        try {
            parseAttributedChallenge = queryAttributed.getFirst();
            userChallengerID = parseAttributedChallenge.get("user_id").toString();
            friendChallenged = new Friend(parseAttributedChallenge.get("user_id_applicant").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("_User");
        queryUser.whereEqualTo("objectId", userChallengerID);

        try {
            parseUser = queryUser.getFirst();
            friendChallenger = new Friend(parseUser.get("username").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getPurcentageVote() {
        double nbVotes = 0;
        double nbVotesYes = 0;

        ParseQuery<ParseObject> queryVote = ParseQuery.getQuery("Vote");
        queryVote.whereEqualTo("attributed_challenge_id", "idTest");

        try {
            nbVotes = queryVote.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> queryVoteYes = ParseQuery.getQuery("Vote");
        queryVoteYes.whereEqualTo("attributed_challenge_id", "idTest");
        queryVoteYes.whereEqualTo("vote_yes", true);

        try {
            nbVotesYes = queryVoteYes.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        purcentVote = (nbVotesYes / nbVotes) * 100.0;
    }

    public void updateProgressBar() {
        getPurcentageVote();
        vote.setProgress(purcentVote.intValue());
        tvPurcent.setText(String.format("%.1f", purcentVote) + "%");
    }

    public boolean hasVoted() {
        boolean hasVoted;
        ParseQuery<ParseObject> queryUser= ParseQuery.getQuery("Vote");
        queryUser.whereEqualTo("attributed_challenge_id", "idTest");
        queryUser.whereEqualTo("user_id", ParseUser.getCurrentUser().getObjectId());

        try {
            ParseObject result = queryUser.getFirst();
            String userID = result.get("user_id").toString();

            if (userID.equals(ParseUser.getCurrentUser().getObjectId())) {
                Toast.makeText(getActivity().getApplicationContext(),
                                "You have already voted for this challenge !",
                                Toast.LENGTH_SHORT).show();

                hasVoted = true;
            }

            else hasVoted =  false;
        } catch (ParseException e) {
            e.printStackTrace();
            hasVoted = false;
        }

        return hasVoted;
    }
}
