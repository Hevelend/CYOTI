package com.example.quentin.cyoti;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.quentin.cyoti.utilities.Mail;
import com.example.quentin.cyoti.utilities.PushNotification;
import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import org.w3c.dom.Text;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Gabriel on 05/06/2015.
 */
public class DescriptionChallengeActivity extends AppCompatActivity {
    private String description;
    private String challengeID;
    private String friendSelected;
    private boolean isCurrentUserChallenged;
    private boolean isCurrentUserChallenger;
    private Double percentVote = 0.0;
    private ProgressBar vote;
    private TextView tvPercent;
    private Bitmap proof;

    private View rootView;
    private ParseUser currentUser;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_FILE_ACTIVITY_REQUEST_CODE = 200;

    public DescriptionChallengeActivity() {
        currentUser = ParseUser.getCurrentUser();
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_challenge);
        rootView = findViewById(R.id.activity_description_challenge);

        Intent intent = getIntent();
        this.description = intent.getExtras().getString("description");
        this.challengeID = intent.getExtras().getString("challengeID");
        this.isCurrentUserChallenged = Boolean.parseBoolean(intent.getExtras().getString("isCurrentUserChallenged"));
        this.isCurrentUserChallenger = Boolean.parseBoolean(intent.getExtras().getString("isCurrentUserChallenger"));

        TextView tvDescription = (TextView) this.findViewById(R.id.tv_description);
        tvDescription.setText(description);

        TextView tvNoProof = (TextView) this.findViewById(R.id.tv_no_proof);
        final Button bt_select_file = (Button) this.findViewById(R.id.bt_select_file);
        final Button bt_take_picture = (Button) this.findViewById(R.id.bt_take_picture);

        final ImageView imProof = (ImageView) this.findViewById(R.id.im_proof);

        ParseQuery<ParseObject> queryProof = ParseQuery.getQuery("Evidence");
        queryProof.whereEqualTo("attributed_challenge_id", challengeID);
        int count = 0;
        ParseObject tempEvidence = null;
        try {
            count = queryProof.count();
        } catch (ParseException e) {
            Log.d("queryFail", "Query has failed : " + e.toString());
        }


        if (count < 1) {
            tvNoProof.setVisibility(View.VISIBLE);

            if (isCurrentUserChallenged) {
                bt_select_file.setVisibility(View.VISIBLE);
                bt_select_file.setClickable(true);
                bt_take_picture.setVisibility(View.VISIBLE);
                bt_take_picture.setClickable(true);

                bt_select_file.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt_select_file.setVisibility(View.INVISIBLE);
                        bt_select_file.setClickable(false);
                        bt_take_picture.setVisibility(View.INVISIBLE);
                        bt_take_picture.setClickable(false);
                        // Open Gallery app
                        Intent i = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);

                        startActivityForResult(i, SELECT_FILE_ACTIVITY_REQUEST_CODE);
                    }
                });

                bt_take_picture.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        bt_select_file.setVisibility(View.INVISIBLE);
                        bt_select_file.setClickable(false);
                        bt_take_picture.setVisibility(View.INVISIBLE);
                        bt_take_picture.setClickable(false);

                        // create Intent to take a picture and return control to the calling application
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toURI());
                        //intent.putExtra(MediaStore.EXTRA_OUTPUT,  Environment.getExternalStorageDirectory() + "/DCIM/Camera/" + challengeID + "_proof.jpg");
                        // start the image capture Intent
                        startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
                    }
                });
            }
        } else {
            try {
                tempEvidence = queryProof.getFirst();
            } catch (ParseException e) {
                Log.d("queryFail", "Query has failed : " + e.toString());
            }
            if (tempEvidence != null) {
                ParseFile imageFile = tempEvidence.getParseFile("evidence");
                imageFile.getDataInBackground(new GetDataCallback() {
                    public void done(byte[] data, ParseException e) {
                        if (e == null) {
                            BitmapFactory.Options options = new BitmapFactory.Options();
                            proof = BitmapFactory.decodeByteArray(data, 0, data.length, options);
                            imProof.setImageBitmap(proof);
                        } else {
                            Log.d("queryFail", "Query has failed : " + e.toString());
                        }
                    }
                });
                if (isCurrentUserChallenger) {
                    final ImageButton ib_unlike = (ImageButton) this.findViewById(R.id.ib_unlike);
                    final ImageButton ib_like = (ImageButton) this.findViewById(R.id.ib_like);

                    final ParseQuery<ParseObject> queryChall = ParseQuery.getQuery("Attributed_challenge");
                    queryChall.whereEqualTo("objectId", challengeID);
                    ParseObject tempChall = null;
                    try {
                        tempChall = queryChall.getFirst();
                    } catch (ParseException e) {
                        Log.d("queryFail", "Query has failed : " + e.toString());
                    }

                    if (tempChall != null) {
                        ib_like.setVisibility(View.VISIBLE);
                        ib_like.setClickable(true);
                        ib_unlike.setVisibility(View.VISIBLE);
                        ib_unlike.setClickable(true);
                        tempChall.put("finish_date", new Date());
                        final ParseObject tempChallPos = tempChall;
                        tempChallPos.put("success", true);
                        final ParseObject tempChallNeg = tempChall;
                        tempChallNeg.put("success", false);


                        ib_like.setOnClickListener(new View.OnClickListener() {
                                                       @Override
                                                       public void onClick(View view) {
                                                           tempChallPos.saveInBackground();
                                                           ib_like.setVisibility(View.INVISIBLE);
                                                           ib_like.setClickable(false);
                                                           ib_unlike.setVisibility(View.INVISIBLE);
                                                           ib_unlike.setClickable(false);

                                                           //get objectId du challenge queryCahll.whereEqualTo un peu plus haut
                                                           queryChall.getFirstInBackground(new GetCallback<ParseObject>() {
                                                               public void done(ParseObject object, ParseException e) {
                                                                   if (e == null) {
                                                                       object.put("success", true);
                                                                       object.saveInBackground();
                                                                       String user_id = object.getString("user_id");

                                                                       ParseQuery<ParseObject> query = ParseQuery.getQuery("Experience");
                                                                       query.whereEqualTo("user_id", user_id);
                                                                       query.getFirstInBackground(new GetCallback<ParseObject>() {
                                                                           public void done(ParseObject object, ParseException e) {
                                                                               if (e == null) {
                                                                                   try {
                                                                                       int XPuser = object.getInt("experience");
                                                                                       int newXP = XPuser + 10;
                                                                                       Log.d("testXp", "Ancien XP" + XPuser + "Nouveau XP:" + newXP);
                                                                                       object.put("experience", newXP);
                                                                                       object.save();
                                                                                   } catch (ParseException error) {
                                                                                       error.printStackTrace();

                                                                                       Toast.makeText(getApplicationContext(),
                                                                                               "Error ! Please try again later.",
                                                                                               Toast.LENGTH_SHORT).show();

                                                                                       Log.d("majU", "Mise a jour du user echouee");
                                                                                   }

                                                                               } else {
                                                                                   Log.d("score", "Error: " + e.getMessage());
                                                                               }
                                                                           }
                                                                       });


                                                                   } else {
                                                                       Log.d("score", "Retrieved the object.");
                                                                   }
                                                               }
                                                           });

                                                       }
                                                   }

                        );

                        ib_unlike.setOnClickListener(new View.OnClickListener()

                                                     {
                                                         @Override
                                                         public void onClick(View view) {
                                                             tempChallNeg.saveInBackground();
                                                             ib_like.setVisibility(View.INVISIBLE);
                                                             ib_like.setClickable(false);
                                                             ib_unlike.setVisibility(View.INVISIBLE);
                                                             ib_unlike.setClickable(false);
                                                         }
                                                     }

                        );
                    }
                }

            }
        }

        // Vote for the challenge
        vote = (ProgressBar) rootView.findViewById(R.id.pb_vote);
        tvPercent = (TextView) rootView.findViewById(R.id.tv_percent);
        ImageButton btLike = (ImageButton) rootView.findViewById(R.id.bt_like);
        ImageButton btUnlike = (ImageButton) rootView.findViewById(R.id.bt_unlike);
        if (!isCurrentUserChallenged) {
            getPercentageVote();
            vote = (ProgressBar) rootView.findViewById(R.id.pb_vote);
            vote.setRotation(90f);
            if (percentVote.isNaN()) {
                vote.setProgress(100);
                tvPercent.setText("0 vote");
            } else {
                vote.setProgress(percentVote.intValue());
                tvPercent.setText(String.format("%.1f", percentVote) + "%");
            }

            btLike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!hasVoted()) {
                        ParseObject vote = new ParseObject("Vote");
                        vote.put("attributed_challenge_id", challengeID);
                        vote.put("vote_yes", true);
                        vote.put("vote_no", false);
                        vote.put("user_id", currentUser.getObjectId());

                        try {
                            vote.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        updateProgressBar();

                        Toast.makeText(getApplicationContext(),
                                "Vote saved - Victory", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "You have already voted for this challenge !",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });

            btUnlike.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!hasVoted()) {
                        ParseObject vote = new ParseObject("Vote");
                        vote.put("attributed_challenge_id", challengeID);
                        vote.put("vote_yes", false);
                        vote.put("vote_no", true);
                        vote.put("user_id", currentUser.getObjectId());

                        try {
                            vote.save();
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        updateProgressBar();

                        Toast.makeText(getApplicationContext(),
                                "Vote saved - Defeat", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "You have already voted for this challenge !",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        } else {
            vote.setVisibility(View.INVISIBLE);
            vote.setClickable(false);
            tvPercent.setVisibility(View.INVISIBLE);
            tvPercent.setClickable(false);
            btLike.setVisibility(View.INVISIBLE);
            btLike.setClickable(false);
            btUnlike.setVisibility(View.INVISIBLE);
            btUnlike.setClickable(false);
        }

        // Propose challenge to other friends
        Button btnPropose = (Button) rootView.findViewById(R.id.bt_proposeChallenge);
        btnPropose.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                //initialisation alertdialog
                AlertDialog.Builder alertDialog = new AlertDialog.Builder(rootView.getContext());
                LayoutInflater inflater = getLayoutInflater();
                View convertView = inflater.inflate(R.layout.custom, null);
                alertDialog.setView(convertView);

                //setting a title for the dialog
                alertDialog.setTitle(R.string.dialog_title);


                final ListView lv = (ListView) convertView.findViewById(R.id.listView1);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(rootView.getContext(), android.R.layout.simple_list_item_1, (ArrayList<String>)currentUser.get("friend_list"));
                lv.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position,
                                            long id) {
                        friendSelected = (String) lv.getItemAtPosition(position);
                        Log.e("TAG", friendSelected.toString());
                    }
                });
                lv.setAdapter(adapter);
                Log.d("GET FRIENDS",((ArrayList<String>)currentUser.get("friend_list")).toString());



                // Setting Positive "Yes" Button
                alertDialog.setPositiveButton("YES",
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                if (proposeChallengeToFriendSelected(friendSelected)) {
                                    Toast.makeText(rootView.getContext().getApplicationContext(),
                                            R.string.yes_propose, Toast.LENGTH_SHORT).show();
                                }

                                else {
                                    Toast.makeText(rootView.getContext().getApplicationContext(),
                                            R.string.pb_propose, Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                );
                // Setting Negative "NO" Button
                alertDialog.setNegativeButton("NO",
                        new DialogInterface.OnClickListener()

                        {
                            public void onClick(DialogInterface dialog, int which) {
                                // Write your code here to execute after dialog
                                Toast.makeText(rootView.getContext().getApplicationContext(), R.string.no_propose, Toast.LENGTH_SHORT).show();
                                dialog.cancel();
                            }
                        }

                );

                // Showing Alert Message
                alertDialog.show();

            }
        });
                /* FIN ALERTDIALOG*/
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Bitmap bmp = null;
        byte[] byteArray;
        ByteArrayOutputStream stream = new ByteArrayOutputStream();

        switch (requestCode) {
            case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
                if (resultCode == Activity.RESULT_OK && null != data) {
                    bmp = (Bitmap) data.getExtras().get("data");
                } else if (resultCode == Activity.RESULT_CANCELED) {
                    // User cancelled the image capture
                } else {
                    // Image capture failed, advise user
                }
                break;

            case SELECT_FILE_ACTIVITY_REQUEST_CODE:
                if (resultCode == RESULT_OK && null != data) {
                    try {
                        bmp = BitmapFactory.decodeStream(this.getContentResolver().openInputStream(data.getData()));
                    } catch (IOException e) {
                        Log.d("getBitmap", "Error : " + e.toString());
                    }
                }
                break;

            default:
                bmp = null;
        }
        // Convert bitmap to ParseFile then save in database
        if (bmp != null) {
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, stream);
            byteArray = stream.toByteArray();
            if (byteArray != null) {
                ParseFile tempFile = new ParseFile(challengeID + ".jpg", byteArray);
                tempFile.saveInBackground();
                ParseObject evidence = new ParseObject("Evidence");
                evidence.put("attributed_challenge_id", challengeID);
                evidence.put("evidence", tempFile);
                evidence.saveInBackground();
            }
        }
    }

    public void getPercentageVote() {
        double nbVotes = 0;
        double nbVotesYes = 0;

        ParseQuery<ParseObject> queryVote = ParseQuery.getQuery("Vote");
        queryVote.whereEqualTo("attributed_challenge_id", challengeID);

        try {
            nbVotes = queryVote.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        ParseQuery<ParseObject> queryVoteYes = ParseQuery.getQuery("Vote");
        queryVoteYes.whereEqualTo("attributed_challenge_id", challengeID);
        queryVoteYes.whereEqualTo("vote_yes", true);

        try {
            nbVotesYes = queryVoteYes.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        percentVote = (nbVotesYes / nbVotes) * 100.0;
    }

    public boolean hasVoted() {
        ParseQuery<ParseObject> queryUser = ParseQuery.getQuery("Vote");
        queryUser.whereEqualTo("attributed_challenge_id", challengeID);
        queryUser.whereEqualTo("user_id", currentUser.getObjectId());

        int count = 0;
        try {
            count = queryUser.count();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return (count > 0);
    }

    public void updateProgressBar() {
        getPercentageVote();
        vote.setProgress(percentVote.intValue());
        tvPercent.setText(String.format("%.1f", percentVote) + "%");
    }

    public boolean proposeChallengeToFriendSelected(String friend) {
        ParseACL accl = new ParseACL();
        accl.setPublicWriteAccess(true);
        accl.setPublicReadAccess(true);

        ParseObject myattributed = new ParseObject("Attributed_challenge");

        ParseQuery<ParseObject> queryAttributed = ParseQuery.getQuery("Attributed_challenge");
        queryAttributed.whereEqualTo("objectId", challengeID);

        ParseQuery<ParseObject> queryFriend = ParseQuery.getQuery("_User");
        queryFriend.whereEqualTo("username", friend);

        ParseObject parseFriend;
        ParseObject parseAttributed;

        try {
            parseFriend = queryFriend.getFirst();
            parseAttributed = queryAttributed.getFirst();

            myattributed.put("user_id", parseFriend.getObjectId());
            myattributed.put("challenge_id", parseAttributed.get("challenge_id"));
            myattributed.put("user_id_applicant", currentUser.getObjectId());
            myattributed.put("sending_date", new Date());
            myattributed.setACL(accl);

            myattributed.save();

            if (PushNotification.isNotificationsAllowed(parseFriend)) {
                PushNotification.sendNotification(currentUser, parseFriend,
                        PushNotification.NEW_CHALLENGE_NOTIFICATION);
            }

            Mail m = new Mail();

            if (m.isMailingAllowed(parseFriend)) {
                m.sendNewChallengeAsyncMail(m, currentUser, parseFriend, description);
            }

            return true;
        } catch (ParseException e) {
            e.printStackTrace();
            return false;
        }
    }

}
