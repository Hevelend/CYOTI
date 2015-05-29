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
import java.util.List;


public class HistoryActivity extends AppCompatActivity {
    private View myView;
    private ParseUser currentUser;
    private ImageButton imageButtonProfile;
    private ArrayList<String> challenges;

    public HistoryActivity() {
        currentUser = ParseUser.getCurrentUser();
        challenges = new ArrayList<String>();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        addListenerOnBottomBar();
        FontsOverride.setDefaultFont(this, "MONOSPACE", "MAW.ttf");

        ListView listChallenges = (ListView) this.findViewById(R.id.Lv_history);
        listChallenges.setClickable(false);

        getHistory();

        StringAdapter stringAdapter = new StringAdapter(this,
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
                Intent i = new Intent(HistoryActivity.this, UserProfileActivity.class);
                startActivity(i);
            }

        });
    }

    public void getHistory() {
        ParseQuery<ParseObject> query = ParseQuery.getQuery("Attributed_challenge");
        query.whereEqualTo("user_id", currentUser.getObjectId());
        query.whereNotEqualTo("finish_date", null);
        /*query.whereEqualTo("success", true);
        query.whereEqualTo("success", false);*/

        List<ParseObject> tempObject = null;

        try {
            tempObject = query.find();
        } catch (ParseException e) {
            Log.d("queryFail", "Query Object has failed : " + e.toString());
        }

        if (tempObject != null) {
            ParseObject tempObject2 = null;
            ParseObject tempObject3 = null;
            Challenge tempChallengeMetier = null;

            for(int j = 0; j < tempObject.size(); j++) {
                Date finishDate = (Date) tempObject.get(j).get("finish_date");
                String tempApplicant = (String) tempObject.get(j).get("user_id_applicant");
                String tempChallengeID = (String) tempObject.get(j).get("challenge_id");

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

            }

        }
    }
}
