package com.example.quentin.cyoti;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.quentin.cyoti.adapters.CustomFragmentPagerAdapter;
import com.example.quentin.cyoti.metier.User;
import com.example.quentin.cyoti.utilities.FontsOverride;

import java.util.ArrayList;


public class ChallengeActivity extends ActionBarActivity
                                implements UserFragment.OnUserListener {

    private ViewPager viewPager;
    private CustomFragmentPagerAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_challenge);

        ActionBar ac = getSupportActionBar();
        ac.setTitle(R.string.title_fragment_propose_challenge);
        ac.setDisplayShowHomeEnabled(true);

        viewPager = (ViewPager) findViewById(R.id.pager);
        mAdapter = new CustomFragmentPagerAdapter(getSupportFragmentManager());
        viewPager.setAdapter(mAdapter);


        FontsOverride.setDefaultFont(this, "MONOSPACE", "MAW.ttf");
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onUserConnected(User user) {
        ProposeChallengeFragment frag = (ProposeChallengeFragment)
                                        getSupportFragmentManager().findFragmentById(R.id.fragment_propose_challenge);

        Bundle args = new Bundle();
        ArrayList<String> friends = new ArrayList<String>();

        for (int i=0; i<user.getFriends().size();i++) {
            friends.add(user.getFriends().get(i).getFirstName());
        }

        args.putStringArrayList("friends", friends);



        if (frag != null) {
            frag.updateFriendsList(friends);
        }

        else {
            ProposeChallengeFragment newFrag = new ProposeChallengeFragment();

            newFrag.setArguments(args);

            FragmentTransaction fragTransaction = getSupportFragmentManager().beginTransaction();
            fragTransaction.replace(R.id.fragment_propose_challenge, newFrag);
            fragTransaction.addToBackStack(null);
            fragTransaction.commit();
        }
    }
}
