package com.example.quentin.cyoti;

import android.content.Intent;
import android.content.res.Resources;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.CustomFragmentPagerAdapter;
import com.example.quentin.cyoti.metier.User;
import com.example.quentin.cyoti.utilities.FontsOverride;
import com.facebook.appevents.AppEventsLogger;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;
import com.readystatesoftware.viewbadger.BadgeView;

import java.util.ArrayList;


public class ChallengeActivity extends AppCompatActivity
                                implements UserFragment.OnUserListener {

    private ViewPager viewPager;
    private CustomFragmentPagerAdapter mAdapter;
    private ImageButton imageButtonProfile;
    private ImageButton imageButtonHistorique;
    private ImageButton imageButtonPendingChallenges;
    private ImageButton imageButtonRefresh;
    private BadgeView badge;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        ActionBar ac = getSupportActionBar();
        ac.setIcon(R.drawable.ic_app);
        ac.setTitle(R.string.title_fragment_propose_challenge);
        ac.setDisplayUseLogoEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);

        addListenerOnBottomBar();

        FontsOverride.setDefaultFont(this, "MONOSPACE", "MAW.ttf");

        View target = findViewById(R.id.action_cup);
        badge = new BadgeView(this, target);

        getNBPending();
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
            this.finish();
        }

        return super.onOptionsItemSelected(item);
    }

    public void addListenerOnBottomBar() {

        imageButtonProfile = (ImageButton) findViewById(R.id.action_profile);
        imageButtonHistorique = (ImageButton) findViewById(R.id.action_diploma);
        imageButtonPendingChallenges = (ImageButton) findViewById(R.id.action_cup);
        imageButtonRefresh = (ImageButton) findViewById(R.id.action_refresh);

        imageButtonProfile.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), UserProfileActivity.class);
                startActivity(i);
            }

        });

        imageButtonHistorique.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent i = new Intent(getApplicationContext(), HistoryActivity.class);
                startActivity(i);
            }

        });

        imageButtonPendingChallenges.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(getApplicationContext(), PendingChallengesActivity.class);
                startActivity(i);
            }
        });

        imageButtonRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });

    }

    @Override
    public void onUserConnected(User user) {
//        ProposeChallengeFragment frag = (ProposeChallengeFragment)
//                                        getSupportFragmentManager().findFragmentById(R.id.fragment_propose_challenge);
//
//
//        Bundle args = new Bundle();
//        ArrayList<String> friends = new ArrayList<String>();
//
//        if (user.getFriends().size() != 0) {
//            for (int i=0; i<user.getFriends().size();i++) {
//                friends.add(user.getFriends().get(i).getFirstName());
//            }
//
//            args.putStringArrayList("friends", friends);
//
//            if (frag != null) {
//                Log.d("fragNNull", "Fragment ProposeChallengeFragment déjà existant");
//                frag.updateFriendsList(friends);
//            }
//
//            else {
//                Log.d("fragNull", "Fragment ProposeChallengeFragment non existant");
//                ProposeChallengeFragment newFrag = new ProposeChallengeFragment();
//
//                newFrag.setArguments(args);
//
//                FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
//                fragTransaction.replace(R.id.fragment_propose_challenge, newFrag);
//                fragTransaction.addToBackStack(null);
//                fragTransaction.commit();
//            }
//        }
    }

    public void getNBPending() {
        String tempUser = ParseUser.getCurrentUser().getObjectId();
        ParseQuery<ParseObject> queryCount = ParseQuery.getQuery("Attributed_challenge");
        queryCount.whereEqualTo("user_id", tempUser);
        queryCount.whereEqualTo("accepting_date", null);

        int tempCount = 0;

        try {
            tempCount = queryCount.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        if(tempCount > 0) {
            if(!badge.isShown()) {
                badge.setText(Integer.toString(tempCount));
                badge.show();
            } else {
                badge.setText(Integer.toString(tempCount));
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        getNBPending();
    }
}
