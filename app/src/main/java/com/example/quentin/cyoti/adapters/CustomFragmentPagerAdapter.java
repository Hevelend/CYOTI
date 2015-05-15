package com.example.quentin.cyoti.adapters;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.quentin.cyoti.ProposeChallengeFragment;
import com.example.quentin.cyoti.VoteChallengeFragment;

/**
 * Created by Vincent on 14/05/2015.
 */
public class CustomFragmentPagerAdapter extends FragmentPagerAdapter {
    public CustomFragmentPagerAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int frag) {
        switch(frag) {
            case 0:
                return new ProposeChallengeFragment();

            case 1:
                return new VoteChallengeFragment();

            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}
