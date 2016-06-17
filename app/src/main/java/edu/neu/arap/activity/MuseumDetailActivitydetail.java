package edu.neu.arap.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import edu.neu.arap.R;

public class MuseumDetailActivitydetail extends AppCompatActivity {

    private int RPosition;
    private int[] resID;
    private String[] resName;
    private String[] resIntro;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_museum_detail_activitydetail);
        Intent intent=getIntent();
        RPosition=intent.getIntExtra("RPosition",0);
        resID=intent.getIntArrayExtra("resID");
        resName=intent.getStringArrayExtra("resName");

        findViewById(R.id.museum_detail_back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MuseumDetailActivitydetail.this,MuseumMainActivity.class));
                finish();
            }
        });
        ((TextView)findViewById(R.id.museum_detail_name)).setText(resName[RPosition]);
        ((TextView)findViewById(R.id.museum_detail_intro)).setText("ddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddddd");
        ((ImageView)findViewById(R.id.museum_detail_image)).setImageResource(resID[RPosition]);
    }
}
