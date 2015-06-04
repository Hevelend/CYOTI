package com.example.quentin.cyoti.adapters;

import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quentin.cyoti.R;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.ArrayList;
import java.util.Date;

public class PendingChallengeAdapter extends ArrayAdapter<String> {
    private int listItemId;
    private int textView;
    private int imageView;
    private int acceptButton;
    private int refuseButton;
    private ArrayList<String> listIDC;
    private static int pos;

    public PendingChallengeAdapter(Context context, int resource, ArrayList<String> objects, ArrayList<String> idChallenges, int idTV, int idIMG, int aBTN, int rBTN) {
        super(context, resource, objects);
        this.listItemId = resource;
        this.textView = idTV;
        this.imageView = idIMG;
        this.acceptButton = aBTN;
        this.refuseButton = rBTN;
        this.listIDC = idChallenges;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(listItemId, parent, false);

        TextView tv = (TextView)v.findViewById(textView);
        tv.setText(this.getItem(position));

        pos = position;

        ImageButton imgbA = (ImageButton) v.findViewById(acceptButton);
        imgbA.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {

                ParseQuery<ParseObject> queryChallenge = ParseQuery.getQuery("Attributed_challenge");
                ParseObject myChallenge = null;

                try {
                    myChallenge = queryChallenge.get(listIDC.get(pos));
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

            }
        });

        return v;
    }
}
