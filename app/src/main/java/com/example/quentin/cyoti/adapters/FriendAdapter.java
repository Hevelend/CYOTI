package com.example.quentin.cyoti.adapters;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quentin.cyoti.UserProfileActivity;
import com.example.quentin.cyoti.metier.Friend;
import com.example.quentin.cyoti.R;
import com.example.quentin.cyoti.utilities.FriendViewHolder;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

        ParseQuery queryFriend = ParseQuery.getQuery("_User");
        queryFriend.whereEqualTo("username", f.getFirstName());

        ParseObject friend;

        try {
            friend = queryFriend.getFirst();
            f.setUserID(friend.getObjectId());
        } catch (ParseException e) {
            e.printStackTrace();
            f.setUserID("error");
        }

        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(R.layout.listitem_friend, parent, false);

        TextView tvFriend = (TextView)v.findViewById(R.id.tv_friend);
        tvFriend.setText(f.getFirstName());

        ImageView imageFriend = (ImageView) v.findViewById(R.id.imageFriend);

        if (!f.getUserID().equals("error")) {
            f.setImgBmp(UserProfileActivity.getImageProfile(f.getUserID()));

            if(f.getImgBmp() == null) imageFriend.setImageResource(R.drawable.default_avatar);
            else imageFriend.setImageBitmap(f.getImgBmp());
        }

        return v;
    }
}
