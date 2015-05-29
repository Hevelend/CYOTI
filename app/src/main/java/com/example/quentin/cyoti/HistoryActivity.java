package com.example.quentin.cyoti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

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


public class HistoryActivity extends AppCompatActivity {
    private View myView;
    private ParseUser currentUser;
    private ImageButton imageButtonProfile;
    private ArrayList<String> challenges;

    public HistoryActivity() {
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
        setContentView(R.layout.activity_history);

        addListenerOnBottomBar();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "MAW.ttf");

        ListView listChallenges = (ListView) myView.findViewById(R.id.lv_history);
        listChallenges.setClickable(true);

        StringAdapter stringAdapter = new StringAdapter(myView.getContext(),
                R.layout.listitem_history_challenge,
                challenges,
                R.id.tv_challenge);

        listChallenges.setAdapter(stringAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
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
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnBottomBar() {
        imageButtonProfile = (ImageButton) findViewById(R.id.action_profile);

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(i);
            }

        });
    }

}
