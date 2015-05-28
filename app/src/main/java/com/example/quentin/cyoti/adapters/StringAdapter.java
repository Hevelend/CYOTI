package com.example.quentin.cyoti.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by Gabriel on 18/05/2015.
 */
public class StringAdapter extends ArrayAdapter<String> {
    private int listItemId;
    private int idTextView;

    public StringAdapter(Context context, int resource, ArrayList<String> objects, int idTV) {
        super(context, resource, objects);
        this.listItemId = resource;
        this.idTextView = idTV;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = this.getItem(position);

        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(listItemId, parent, false);

        TextView textView = (TextView)v.findViewById(idTextView);
        textView.setText(s);

        return v;
    }
}
