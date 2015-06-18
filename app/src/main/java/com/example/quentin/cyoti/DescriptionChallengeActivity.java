package com.example.quentin.cyoti;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.parse.GetCallback;
import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.Date;

/**
 * Created by Gabriel on 05/06/2015.
 */
public class DescriptionChallengeActivity extends AppCompatActivity {
    private String description;
    private String challengeID;
    private boolean isCurrentUserChallenged;
    private boolean isCurrentUserChallenger;
    private Bitmap proof;

    private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
    private static final int SELECT_FILE_ACTIVITY_REQUEST_CODE = 200;

    public DescriptionChallengeActivity() {

    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_challenge);

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
                        intent.putExtra(MediaStore.EXTRA_OUTPUT,  Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toURI());
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

                                           ParseQuery<ParseObject> query = ParseQuery.getQuery("_User");
                                           query.getInBackground(user_id, new GetCallback<ParseObject>() {
                                               public void done(ParseObject object, ParseException e) {
                                                   if (e == null) {
                                                       int XPuser = object.getInt("experience");
                                                       int newXP = XPuser + 10;
                                                       object.put("experience", newXP);
                                                       Log.d("testXp", "Ancien XP" + XPuser + "Nouveau XP:" + newXP);
                                                       object.saveInBackground();

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
                public void onClick (View view){
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

}
