package com.example.quentin.cyoti;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Parcel;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import com.parse.ParseFile;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import android.net.Uri;
import java.net.URL;
import java.util.List;


public class UserProfileActivity extends ActionBarActivity {
    private ImageButton btEditPhoto;
    private EditText etUserName;
    private EditText etUserEmail;
    private EditText etUserPassword;
    private Switch swNotificationsPush;
    private Switch swMailingNotifications;
    private static ImageView imageUser;

    private static ParseUser currentUser;

    private boolean saveChanges = false;
    private boolean deleteProfile = false;
    private boolean pushNotifications;
    private boolean mailingNotifications;
    private String imagePath = "";
    private Bitmap imageBMP;

    private final static int ACTION_SAVE_CHANGES   = 1;
    private final static int ACTION_DELETE_PROFILE = 2;

    public UserProfileActivity() {
        currentUser = ParseUser.getCurrentUser();
        pushNotifications = currentUser.getBoolean("notification");
        mailingNotifications = currentUser.getBoolean("newsletter");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        ActionBar ac = getSupportActionBar();
        ac.setTitle(R.string.title_activity_user_profile);
        ac.setIcon(R.drawable.ic_app);

        imageUser = (ImageView) this.findViewById(R.id.imageUser);

        Bitmap bmpUser = getImageProfile(currentUser.getObjectId());

        if (bmpUser == null) imageUser.setImageResource(R.drawable.default_avatar);
        else imageUser.setImageBitmap(bmpUser);

        btEditPhoto = (ImageButton) this.findViewById(R.id.bt_editPhoto);

        imageUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (btEditPhoto.getVisibility() == View.INVISIBLE)
                    btEditPhoto.setVisibility(View.VISIBLE);
                else btEditPhoto.setVisibility(View.INVISIBLE);
            }
        });

        btEditPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Open Gallery app
                Intent i = new Intent(
                        Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                startActivityForResult(i, 1); // 1 is for selection mode
            }
        });

        etUserName = (EditText) this.findViewById(R.id.et_userName);
        etUserName.setText(currentUser.getUsername());

        etUserEmail = (EditText) this.findViewById(R.id.et_userEmail);
        etUserEmail.setText(currentUser.getEmail());

        etUserPassword = (EditText) this.findViewById(R.id.et_userPwd);
        etUserPassword.setHint("Your password");


        /* TODO : gérer les notifications */
        swNotificationsPush = (Switch) this.findViewById(R.id.sw_notification);
        swNotificationsPush.setChecked(pushNotifications);

        swNotificationsPush.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swNotificationsPush.isChecked()) pushNotifications = true;
                else pushNotifications = false;
            }
        });

        swMailingNotifications = (Switch) this.findViewById(R.id.sw_mailNotification);
        swMailingNotifications.setChecked(mailingNotifications);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1 && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();

            imagePath = selectedImage.getPath();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            try {
                imageBMP = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                imageUser.setImageBitmap(imageBMP);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onBackPressed() {
        showAlertDialog(ACTION_SAVE_CHANGES);
    }

    @Override
    public void onStop() {
        super.onStop();

        if (saveChanges) {
            try {
                currentUser.put("username", etUserName.getText().toString());
                currentUser.put("email", etUserEmail.getText().toString());

                String password = etUserPassword.getText().toString();

                if(!password.equals("")) currentUser.put("password", password);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                imageBMP.compress(Bitmap.CompressFormat.PNG, 100, stream);

                byte[] imageBytes = stream.toByteArray();

                ParseFile imageFile = new ParseFile(currentUser.getObjectId() + "_avatar.png", imageBytes);

                currentUser.put("avatar", imageFile);
                currentUser.put("newsletter", mailingNotifications);
                currentUser.put("notification", pushNotifications);

                currentUser.save();
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

                break;

            default: break;
        }
    }


    public static Bitmap getImageProfile(String userId) {
        ParseQuery<ParseObject> queryImage = ParseQuery.getQuery("_User");
        queryImage.whereEqualTo("objectId", userId);

        try {
            ParseObject tmpUser = queryImage.getFirst();

            ParseFile imageFile = tmpUser.getParseFile("avatar");

            if (imageFile == null) {
                //imageUser.setImageResource(R.drawable.default_avatar);
                return null;
            }

            else {
                byte[] imageBytes = imageFile.getData();

                Bitmap imageBMP = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.length);

                //imageUser.setImageBitmap(imageBMP);
                return imageBMP;
            }
        }
        catch (ParseException e) {
            Log.d("imgquery", e.getMessage() + e.getCause().toString());
            return null;
        }
    }

}
