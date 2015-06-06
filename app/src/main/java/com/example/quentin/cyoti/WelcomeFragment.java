package com.example.quentin.cyoti;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.List;

public class WelcomeFragment extends Fragment {


    private final ParseUser currentUser;
    public int XPuser;
    public int levelNumber;
    public String titleLevel;
    public ImageView iv_user;

    public WelcomeFragment(){
        currentUser = ParseUser.getCurrentUser();
    }

    private View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_welcome, container, false);

        // Convert currentUser into String
        String struser = currentUser.getUsername().toString();

        // Locate TextView in welcome.xml
        TextView txtuser = (TextView) rootView.findViewById(R.id.txtuser);

        // Set the currentUser String into TextView
        txtuser.setText(struser);

        String txtUserID = currentUser.getObjectId().toString();

        // récupération des points d'xp du user
        ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
        query.getInBackground(txtUserID, new GetCallback<ParseObject>() {
            public void done(ParseObject object, ParseException e) {
                if (e == null) {
                    XPuser = object.getInt("experience");

                    //on crée les contraintes pour récupérer le level du user
                    ParseQuery<ParseObject> levelQuery = ParseQuery.getQuery("Level");
                    levelQuery.whereLessThanOrEqualTo("scoreMin", XPuser);
                    levelQuery.whereGreaterThanOrEqualTo("scoreMax", XPuser);
                    levelQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                        public void done(ParseObject object, ParseException e) {
                            if (e == null) {
                                levelNumber = object.getInt("level");

                                //on récupère le level et on l'affiche
                                TextView txtLevel = (TextView) rootView.findViewById(R.id.txtLevel);
                                txtLevel.setText(""+levelNumber);


                                //on crée les contraintes pour récupérer le titre du user
                                ParseQuery<ParseObject> titleQuery = ParseQuery.getQuery("titleByLevel");
                                titleQuery.whereLessThanOrEqualTo("levelMin", levelNumber);
                                titleQuery.whereGreaterThanOrEqualTo("levelMax", levelNumber);
                                titleQuery.getFirstInBackground(new GetCallback<ParseObject>() {
                                    public void done(ParseObject object, ParseException e) {
                                        if (e == null) {
                                            titleLevel = object.getString("title");

                                            //on affiche le titre du user
                                            TextView txtLevel = (TextView) rootView.findViewById(R.id.txtTitle);
                                            txtLevel.setText("You are a great " + titleLevel);
                                        } else {
                                            Log.d("score", "Retrieved the object.");
                                        }
                                    }
                                });

                            } else {
                                Log.d("score", "Retrieved the object.");
                            }
                        }
                    });

                } else {
                    Log.d("score", "Error: " + e.getMessage());
                }
            }
        });
        return rootView;
    }
}
