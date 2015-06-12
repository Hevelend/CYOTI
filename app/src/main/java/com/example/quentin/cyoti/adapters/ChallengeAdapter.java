package com.example.quentin.cyoti.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.quentin.cyoti.R;
import com.example.quentin.cyoti.UserProfileActivity;
import com.example.quentin.cyoti.metier.Challenge;
import com.example.quentin.cyoti.metier.User;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * Created by Gabriel on 31/05/2015.
 */
public class ChallengeAdapter extends ArrayAdapter<Challenge> {
    private Context context;
    private int listItemId;
    private int idTextView2;
    private int idTextView;
    private ArrayList<String> listD;

    public ChallengeAdapter(Context context, int resource, ArrayList<Challenge> challenges, ArrayList<String> listDate, int idTV, int idTVD) {
        super(context, resource, challenges);
        this.context = context;
        this.listItemId = resource;
        this.idTextView = idTV;
        this.idTextView2 = idTVD;
        this.listD = listDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Challenge c = this.getItem(position);

        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(listItemId, parent, false);

        TextView textView = (TextView)v.findViewById(idTextView);
        textView.setText(c.getDescription());

        TextView textView2 = (TextView) v.findViewById(idTextView2);
        textView2.setText(listD.get(position).toString());

        RelativeLayout rl_head = (RelativeLayout) v.findViewById(R.id.header);
        if (c.isCurrentUserChallenged()) {
            rl_head.setBackgroundColor(context.getResources().getColor(R.color.bluemerica2));
        }
        if (c.isCurrentUserChallenger()) {
            rl_head.setBackgroundColor(context.getResources().getColor(R.color.bluemerica3));
        }

        ImageView imageFriend = (ImageView) v.findViewById(R.id.imageFriend);
        imageFriend.setImageBitmap(c.getUserChallenger().getImgBmp());

        return v;
    }
}
