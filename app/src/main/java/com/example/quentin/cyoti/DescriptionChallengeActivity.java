package com.example.quentin.cyoti;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

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

    public DescriptionChallengeActivity () {
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
        }
        else {
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
                    final ImageButton bt_unlike = (ImageButton) this.findViewById(R.id.bt_unlike);
                    final ImageButton bt_like = (ImageButton) this.findViewById(R.id.bt_like);

                    ParseQuery<ParseObject> queryChall = ParseQuery.getQuery("Attributed_challenge");
                    queryChall.whereEqualTo("objectId", challengeID);
                    ParseObject tempChall = null;
                    try {
                        tempChall = queryChall.getFirst();
                    } catch (ParseException e) {
                        Log.d("queryFail", "Query has failed : " + e.toString());
                    }

                    if (tempChall != null) {
                        bt_like.setVisibility(View.VISIBLE);
                        bt_like.setClickable(true);
                        bt_unlike.setVisibility(View.VISIBLE);
                        bt_unlike.setClickable(true);
                        tempChall.put("finish_date", new Date());
                        final ParseObject tempChallPos = tempChall;
                        tempChallPos.put("success", true);
                        final ParseObject tempChallNeg = tempChall;
                        tempChallNeg.put("success", false);


                        bt_like.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tempChallPos.saveInBackground();
                                bt_like.setVisibility(View.INVISIBLE);
                                bt_like.setClickable(false);
                                bt_unlike.setVisibility(View.INVISIBLE);
                                bt_unlike.setClickable(false);
                            }
                        });

                        bt_unlike.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View view) {
                                tempChallNeg.saveInBackground();
                                bt_like.setVisibility(View.INVISIBLE);
                                bt_like.setClickable(false);
                                bt_unlike.setVisibility(View.INVISIBLE);
                                bt_unlike.setClickable(false);
                            }
                        });
                    }
                }

            }
        }
    }

}
