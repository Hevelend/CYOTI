package com.example.quentin.cyoti;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.GetDataCallback;
import com.parse.ParseException;
import com.parse.ParseFile;
import com.parse.ParseObject;
import com.parse.ParseQuery;

/**
 * Created by Gabriel on 05/06/2015.
 */
public class DescriptionChallengeActivity extends AppCompatActivity {
    // TODO Changer ces valeurs qui sont en dur pour l'instant
    private String description = "Gabriel challenges admin to se deguiser en clown";
    private String challengeID = "Rs5JmZXtjn";
    private Bitmap proof;

    /*public DescriptionChallengeActivity (String desc, String id) {
        this.description = desc;
        this.challengeID = id;
    }*/

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_description_challenge);

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
                ParseFile imageFile = (ParseFile)tempEvidence.get("evidence");
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
            }
        }
    }

}
