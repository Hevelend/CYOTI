package com.example.quentin.cyoti;

import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class UserProfileActivity extends ActionBarActivity {
    private ImageButton btEditPhoto;
    private EditText etUserName;
    private EditText etUserEmail;
    private ParseUser currentUser;

    public UserProfileActivity() {
        currentUser = ParseUser.getCurrentUser();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ActionBar ac = getSupportActionBar();
        ac.setTitle(R.string.title_activity_user_profile);
        ac.setIcon(R.drawable.ic_app);

        ImageView imageUser = (ImageView) this.findViewById(R.id.imageUser);
        btEditPhoto = (ImageButton) this.findViewById(R.id.bt_editPhoto);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btEditPhoto.getVisibility() == View.INVISIBLE)
                    btEditPhoto.setVisibility(View.VISIBLE);
                else btEditPhoto.setVisibility(View.INVISIBLE);
            }
        });

        etUserName = (EditText) this.findViewById(R.id.et_userName);
        etUserName.setHint(currentUser.getUsername().toString());

        etUserEmail = (EditText) this.findViewById(R.id.et_userEmail);
        etUserEmail.setHint(currentUser.getEmail().toString());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_user_profile, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStop() {
        super.onStop();
        ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("_User");

        try {
            ParseObject newUser = queryUser.get(currentUser.getObjectId());

            newUser.put("username", etUserName.getText().toString());
            newUser.put("email", etUserEmail.getText().toString());
            newUser.save();
        } catch (ParseException e) {
            e.printStackTrace();
            Log.d("majU", "Mise à jour du user échouée");
        }
    }
}
