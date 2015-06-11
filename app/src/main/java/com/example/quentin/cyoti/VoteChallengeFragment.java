package com.example.quentin.cyoti;

import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.FriendAdapter;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;
import java.util.List;


public class VoteChallengeFragment extends Fragment {
    private Friend friendChallenger;
    private Friend friendChallenged;
    private Challenge challenge = new Challenge();
    private Double percentVote = 0.0;
    private ProgressBar vote;
    private View rootView;

    //getfriends
    private TextView tvPercent;
    private ParseObject tempObject;
    private ParseUser currentUser;
    private String friends;

    List<String> listFriends;
    ArrayList<String> names;

    public VoteChallengeFragment(){
        currentUser = ParseUser.getCurrentUser();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        getChallenge();
        getAttributedChallenge();
        getPercentageVote();

        rootView = inflater.inflate(R.layout.fragment_vote_challenge, container, false);

        Button btnPropose = (Button) rootView.findViewById(R.id.bt_proposeChallenge);
        btnPropose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //initialisation alertdialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                LayoutInflater inflater = getActivity().getLayoutInflater();
                final View convertView = (View) inflater.inflate(R.layout.custom, null);
                alertDialog.setView(convertView);

                //setting a title for the dialog
                alertDialog.setTitle(R.string.dialog_title);

                ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                String userID = currentUser.getObjectId().toString();
                query.getInBackground(userID, new GetCallback<ParseObject>() {
                    public void done(ParseObject object, ParseException e) {
                        if (e == null) {// pas d'exceptions
                            listFriends = new ArrayList<String>();
                            listFriends = object.getList("friend_list");
                            if (listFriends != null) {
                                names = new ArrayList<String>();
                                for (int i = 0; i < listFriends.size(); i++) {
                                    String temp = listFriends.get(i);
                                    names.add(temp);
                                }

                                final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1, names);
                                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                                            long id) {
                                        friends = (String) lv.getItemAtPosition(position);
                                        Log.e("TAG", friends.toString());
                                    }
                                });
                                lv.setAdapter(adapter);
                                Log.d("GET FRIENDS", listFriends.toString());
                            }
                            else{
                                Toast.makeText(getActivity().getApplicationContext(),"you don't have friends yet", Toast.LENGTH_LONG);
                            }
                        } else {
                            Log.d("GET FRIENDS", "an error has occured");
                        }
                    }
                });



                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getActivity().getApplicationContext(), R.string.yes_propose, Toast.LENGTH_SHORT).show();
                            }
                        }

                );
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(getActivity().getApplicationContext(), R.string.no_propose, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }

                );

                // Showing Alert Message
                alertDialog.show();

            }
        });
                /* FIN ALERTDIALOG*/
        if (challenge.getCreatedDate() != null) {

            TextView descriptionChallenge = (TextView) rootView.findViewById(R.id.tv_descriptionChallenge);
            descriptionChallenge.setText(friendChallenger.getFirstName()
                    + " challenged "
                    + friendChallenged.getFirstName()
                    + " to "
                    + challenge.getDescription());

            vote = (ProgressBar) rootView.findViewById(R.id.pb_vote);
            vote.setRotation(90f);
            vote.setProgress(percentVote.intValue());

            tvPercent = (TextView) rootView.findViewById(R.id.tv_percent);
            tvPercent.setText(String.format("%.1f", percentVote) + "%");

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

        }

        return rootView;
    }

    public void getChallenge() {
        ParseObject parseChallenge;

        ParseQuery<ParseObject> query = ParseQuery.getQuery("Challenge");
        query.whereEqualTo("objectId", "w0FzgnFaJp");

        try {
            parseChallenge = query.getFirst();
            challenge.setDescription(parseChallenge.get("challenge").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getAttributedChallenge() {
        ParseObject parseAttributedChallenge;
        ParseObject parseUserApplicant;
        ParseObject parseUser;
        String userChallengerID = null;
        String userChallengedID = null;

        ParseQuery<ParseObject> queryAttributed = ParseQuery.getQuery("Attributed_challenge");
        queryAttributed.whereEqualTo("challenge_id", "w0FzgnFaJp");

        try {
            parseAttributedChallenge = queryAttributed.getFirst();
            userChallengerID = parseAttributedChallenge.get("user_id_applicant").toString();
            userChallengedID = parseAttributedChallenge.get("user_id").toString();
            //friendChallenged = new Friend(parseAttributedChallenge.get("user_id_applicant").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> queryUserApplicant = ParseQuery.getQuery("_User");
        queryUserApplicant.whereEqualTo("objectId", userChallengerID);

        ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("_User");
        ;
        queryUser.whereEqualTo("objectId", userChallengedID);

        try {
            parseUserApplicant = queryUserApplicant.getFirst();
            parseUser = queryUser.getFirst();
            friendChallenger = new Friend(parseUserApplicant.get("username").toString());
            friendChallenged = new Friend(parseUser.get("username").toString());
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    public void getPercentageVote() {
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

        percentVote = (nbVotesYes / nbVotes) * 100.0;
    }

    public void updateProgressBar() {
        getPercentageVote();
        vote.setProgress(percentVote.intValue());
        tvPercent.setText(String.format("%.1f", percentVote) + "%");
    }

    public boolean hasVoted() {
        boolean hasVoted;
        ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("Vote");
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
            } else hasVoted = false;
        } catch (ParseException e) {
            e.printStackTrace();
            hasVoted = false;
        }

        return hasVoted;
    }
}
