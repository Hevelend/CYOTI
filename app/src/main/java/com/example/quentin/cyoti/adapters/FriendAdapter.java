package com.example.quentin.cyoti.adapters;


import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.R;

import java.util.ArrayList;

/**
 * Created by Vincent on 13/05/2015.
 */
public class FriendAdapter extends ArrayAdapter<Friend>{
    public FriendAdapter(Context context, int resource, ArrayList<Friend> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Friend f = this.getItem(position);

        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(R.layout.listitem_friend, parent, false);

        TextView tvFriend = (TextView)v.findViewById(R.id.tv_friend);
        tvFriend.setText(f.getFirstName() + " " + f.getLastName());

        //ImageView imgFriend = (ImageView)v.findViewById(R.id.imageFriend);
        //imgFriend.setImageURI(Uri.parse(f.getImgPath()));
        return v;
    }
}
