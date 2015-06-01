package com.example.quentin.cyoti.adapters;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.quentin.cyoti.R;

import java.util.ArrayList;

/**
 * Created by Gabriel on 31/05/2015.
 */
public class NewsAdapter extends ArrayAdapter<String> {
    private int listItemId;
    private int idTextView2;
    private int idTextView;
    private int idImgView;
    private ArrayList<Boolean> listS;
    private ArrayList<String> listD;

    public NewsAdapter(Context context, int resource, ArrayList<String> objects, ArrayList<Boolean> listSuccess, ArrayList<String> listDate,int idTV, int imgSV, int idTVD) {
        super(context, resource, objects);
        this.listItemId = resource;
        this.idTextView = idTV;
        this.idImgView = imgSV;
        this.idTextView2 = idTVD;
        this.listS = listSuccess;
        this.listD = listDate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String s = this.getItem(position);

        Activity act = (Activity)getContext();
        LayoutInflater inflater = (act).getLayoutInflater();

        View v = inflater.inflate(listItemId, parent, false);

        TextView textView = (TextView)v.findViewById(idTextView);
        textView.setText(s);

        TextView textView2 = (TextView) v.findViewById(idTextView2);
        textView2.setText(listD.get(position).toString());

        ImageView imgView = (ImageView) v.findViewById(idImgView);

        if(listS.get(position).booleanValue() == true) {
            imgView.setImageResource(R.mipmap.ic_victory);
        } else {
            imgView.setImageResource(R.mipmap.ic_fail);
        }

        return v;
    }
}
