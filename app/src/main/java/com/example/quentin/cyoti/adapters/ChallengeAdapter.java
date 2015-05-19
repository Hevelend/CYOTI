package com.example.quentin.cyoti.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.quentin.cyoti.R;

import java.util.ArrayList;

/**
 * Created by Gabriel on 18/05/2015.
 */
public class ChallengeAdapter extends ArrayAdapter<String> {
    public ChallengeAdapter(Context context, int resource, ArrayList<String> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String f = this.getItem(position);

        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(R.layout.listitem_pending_challenge, parent, false);

        TextView tvChallenge = (TextView)v.findViewById(R.id.tv_challenge);
        tvChallenge.setText(f);

        return v;
    }
}
