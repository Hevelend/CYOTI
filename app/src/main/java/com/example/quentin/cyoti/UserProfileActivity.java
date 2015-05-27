package com.example.quentin.cyoti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import android.widget.Switch;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;


public class UserProfileActivity extends ActionBarActivity {
    private ImageButton btEditPhoto;
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etUserPassword;
    private Switch swNotificationsPush;
    private Switch swMailingNotifications;

    private ParseUser currentUser;

    private boolean saveChanges = false;
    private boolean deleteProfile = false;
    private boolean notificationsPush;
    private boolean mailingNotifications;

    private final static int ACTION_SAVE_CHANGES   = 1;
    private final static int ACTION_DELETE_PROFILE = 2;

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
        etUserName.setText(currentUser.getUsername());

        etUserEmail = (EditText) this.findViewById(R.id.et_userEmail);
        etUserEmail.setText(currentUser.getEmail());

        etUserPassword = (EditText) this.findViewById(R.id.et_userPwd);
        etUserPassword.setText(currentUser.getString("password"));

        swNotificationsPush = (Switch) this.findViewById(R.id.sw_notification);
        swNotificationsPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swNotificationsPush.isChecked()) notificationsPush = true;
                else notificationsPush = false;
            }
        });

        swMailingNotifications = (Switch) this.findViewById(R.id.sw_mailNotification);
        swMailingNotifications.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swMailingNotifications.isChecked()) mailingNotifications = true;
                else mailingNotifications = false;
            }
        });

        ImageButton btDeleteProfile = (ImageButton) this.findViewById(R.id.bt_deleteProfile);
        btDeleteProfile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showAlertDialog(ACTION_DELETE_PROFILE);
            }
        });

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

        if (saveChanges) {
            ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("_User");

            try {
                ParseObject newUser = queryUser.get(currentUser.getObjectId());

                newUser.put("username", etUserName.getText().toString());
                newUser.put("email", etUserEmail.getText().toString());
                newUser.put("password", etUserPassword.getText().toString());
                newUser.put("newsletter", mailingNotifications);

                /* TODO : ajouter champ notificationPush dans BDD */

                newUser.save();
            } catch (ParseException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),
                                "Error ! Please try again later.",
                                Toast.LENGTH_SHORT).show();

                Log.d("majU", "Mise à jour du user échouée");
            }
        }

        else if(deleteProfile) {
            ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("_User");

            try {

                // Removing informations about the user
                ParseObject newUser = queryUser.get(currentUser.getObjectId());
                newUser.remove("email");
                newUser.remove("experience");
                newUser.remove("facebook_account");
                newUser.remove("friend_list");
                newUser.remove("level");
                newUser.remove("newsletter");
                newUser.remove("twitter_account");

                // Freezing the account
                newUser.put("freeze_account", true);

                // Updating account
                newUser.save();


                // Show connection activity
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
            }

            catch (ParseException e) {
                e.printStackTrace();

                Toast.makeText(getApplicationContext(),
                        "Error ! Please try again later.",
                        Toast.LENGTH_SHORT).show();

                Log.d("delU", "Suppression du user échouée");
            }
        }
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ACTION_SAVE_CHANGES);
    }

    public void showAlertDialog(int action) {
        switch (action) {
            case ACTION_SAVE_CHANGES:
                AlertDialog.Builder alertSave = new AlertDialog.Builder(this);
                alertSave.setTitle("Save changes ?");
                alertSave.setMessage(R.string.tv_saveChanges);

                alertSave.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        saveChanges = true;
                        finish();
                    }
                });

                alertSave.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertSave.show();

                break;

            case ACTION_DELETE_PROFILE:
                AlertDialog.Builder alertDelete = new AlertDialog.Builder(this);
                alertDelete.setTitle("Delete profile ?");
                alertDelete.setMessage(R.string.tv_deleteProfile);

                alertDelete.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deleteProfile = true;
                        finish();
                    }
                });

                alertDelete.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                    }
                });

                alertDelete.show();
        }
    }
}
