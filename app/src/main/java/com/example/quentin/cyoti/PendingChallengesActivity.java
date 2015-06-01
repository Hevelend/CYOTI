package com.example.quentin.cyoti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;

import com.example.quentin.cyoti.adapters.StringAdapter;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.utilities.FontsOverride;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriel on 30/05/2015.
 */
public class PendingChallengesActivity extends AppCompatActivity {
    private View myView;
    private ParseUser currentUser;
    private ImageButton imageButtonProfile;
    private ImageButton imageButtonHistory;
    private ImageButton imageButtonAccept;
    private ImageButton imageButtonCancel;
    private ArrayList<String> challenges;
    private ArrayList<String> idChallenges;
    private int itemSelect;

    public PendingChallengesActivity() {
        currentUser = ParseUser.getCurrentUser();
        challenges = new ArrayList<String>();
        idChallenges = new ArrayList<String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_challenges);

        addListenerOnBottomBar();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "MAW.ttf");

        getPendingChallenge();

        //Liste des défis en attente
        ListView listChallenges = (ListView) this.findViewById(R.id.lv_pending_challenges);

        listChallenges.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                Log.d("listen", "Item click list challenge");

                imageButtonAccept = (ImageButton) view.findViewById(R.id.ib_acceptChallenge);
                imageButtonCancel = (ImageButton) view.findViewById(R.id.ib_refuseChallenge);

                imageButtonAccept.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {
                        ParseQuery<ParseObject> queryChallenge = ParseQuery.getQuery("Attributed_challenge");
                        ParseObject myChallenge = null;

                        try {
                            myChallenge = queryChallenge.get(idChallenges.get(itemSelect));
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d("GET Attributed_CHG", "GET attributed_challenge");
                        }
                        myChallenge.put("accepting_date", new Date());

                        try {
                            myChallenge.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                            Log.d("SAVE Challenge", "Mise à jour du attributed_challenge");
                        }

                        finish();
                        startActivity(getIntent());
                    }

                });

                imageButtonCancel.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View arg0) {

                    }

                });
            }
        });

        StringAdapter stringAdapter = new StringAdapter(this,
                R.layout.listitem_pending_challenge,
                challenges,
                R.id.tv_challenge);

        listChallenges.setAdapter(stringAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_challenge, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if(id == R.id.action_logout) {
            ParseUser.logOut();
            finish();
            System.exit(0);
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnBottomBar() {
        imageButtonProfile = (ImageButton) findViewById(R.id.action_profile);
        imageButtonHistory = (ImageButton) findViewById(R.id.action_diploma);

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(PendingChallengesActivity.this, UserProfileActivity.class);
                startActivity(i);
            }

        });

        imageButtonHistory.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(PendingChallengesActivity.this, HistoryActivity.class);
                startActivity(i);
            }

        });
    }

    public void getPendingChallenge() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Attributed_challenge");
        query.whereEqualTo("user_id", currentUser.getObjectId());
        query.whereEqualTo("accepting_date", null);

        List<ParseObject> tempObject = null;

        try {
            tempObject = query.find();
        } catch (ParseException e) {
            Log.d("queryFail", "Query Object has failed : " + e.toString());
        }

        if (tempObject != null) {
            ParseObject tempObject2 = null;
            ParseObject tempObject3 = null;

            for(int j = 0; j < tempObject.size(); j++) {
                String tempApplicant = (String) tempObject.get(j).get("user_id_applicant");
                String idAttributedChallenge = tempObject.get(j).getObjectId().toString();

                ParseQuery<ParseObject> query2 = ParseQuery.getQuery("Challenge");
                query2.whereEqualTo("objectId", tempObject.get(j).get("challenge_id"));

                try {
                    tempObject2 = query2.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Challenge has failed : " + e.toString());
                }

                ParseQuery<ParseObject> query3 = ParseQuery.getQuery("_User");
                query3.whereEqualTo("objectId", tempApplicant);

                try {
                    tempObject3 = query3.getFirst();
                } catch (ParseException e) {
                    Log.d("queryFail", "Query Applicant has failed : " + e.toString());
                }

                challenges.add(tempObject3.get("username").toString() + " challenge you to " + tempObject2.get("challenge"));
                idChallenges.add(idAttributedChallenge);

            }

        }
    }

}
