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

        return v;
    }
}
