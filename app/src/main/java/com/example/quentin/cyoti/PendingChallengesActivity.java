package com.example.quentin.cyoti;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
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
    private ArrayList<String> challenges;

    public PendingChallengesActivity() {
        currentUser = ParseUser.getCurrentUser();
        challenges = new ArrayList<String>();
        Challenge challenge1 = new Challenge("Dance in McDonald's", new Date(), new Date(), new Friend("James", "Morrison", "Jim"), new Friend("Vincent", "Aunai", "Lodoss"));
        Challenge challenge2 = new Challenge("make a cookie in 5 minutes", new Date(), new Date(), new Friend("Toto", "Tutu", "TotoTutu"), new Friend("Martin", "Dupont", "mDupont"));
        challenges.add(challenge1.getUserChallenger().getNickName() + " challenge you to " + challenge1.getDescription());
        challenges.add(challenge2.getUserChallenger().getNickName() + " challenge you to " + challenge2.getDescription());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_challenges);

        addListenerOnBottomBar();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "MAW.ttf");

        //Liste des défis en attente
        ListView listChallenges = (ListView) this.findViewById(R.id.lv_pending_challenges);

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

}
