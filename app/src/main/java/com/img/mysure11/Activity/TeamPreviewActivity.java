package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TeamPreviewActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView team1size,team2size;
    ArrayList<captainListGetSet> list,listWK,listBAT,listAR,listBALL;
    LinearLayout wklayout,batlayout,allayout,ballayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_team_preview);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        list = gv.getCaptainList();

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title=(TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("team_name"));

        wklayout=(LinearLayout)findViewById(R.id.wklayout);
        batlayout=(LinearLayout)findViewById(R.id.batlayout);
        allayout=(LinearLayout)findViewById(R.id.allayout);
        ballayout=(LinearLayout)findViewById(R.id.ballayout);

        team1size = findViewById(R.id.team1size);
        team2size = findViewById(R.id.team2size);
        listWK = new ArrayList<>();
        listBAT = new ArrayList<>();
        listAR = new ArrayList<>();
        listBALL = new ArrayList<>();

        int team1 = 0;
        int team2 = 0;

        for(captainListGetSet zz:list){
            Log.i("Role",zz.getRole());
            if(zz.getRole().equals("Wk"))
                listWK.add(zz);
            else if(zz.getRole().equals("Bat"))
                listBAT.add(zz);
            else if(zz.getRole().equals("AR"))
                listAR.add(zz);
            else if(zz.getRole().equals("Bow"))
                listBALL.add(zz);


            if(zz.getTeam().equals("team1"))
                team1++;
            else
                team2++;
        }

        Player(wklayout,listWK);
        Player(batlayout,listBAT);
        Player(allayout,listAR);
        Player(ballayout,listBALL);

        team1size.setText(gv.getTeam1()+" "+ team1);
        team2size.setText(gv.getTeam2()+" "+ team2);

    }

    public void Player(LinearLayout ll, final ArrayList<captainListGetSet> list){
        ll.removeAllViews();
        for (int f = 0 ;f < list.size();f++){

            View v;
            LayoutInflater inflater =(LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v= inflater.inflate(R.layout.preview_layout,null);

            ImageView image,status;
            TextView name,credit,cap,vc;

            image = v.findViewById(R.id.image);

            name = v.findViewById(R.id.name);
            credit = v.findViewById(R.id.credit);
            cap = v.findViewById(R.id.cap);
            vc = v.findViewById(R.id.vc);
            status = v.findViewById(R.id.status);

            Picasso.with(TeamPreviewActivity.this).load(list.get(f).getImage()).placeholder(R.drawable.avtar).into(image);
            name.setText(list.get(f).getName());
            credit.setText(list.get(f).getCredit()+" CR");


            if(list.get(f).getPlayingstatus().equals("1")) {
                status.setVisibility(View.VISIBLE);
                status.setImageResource(R.drawable.player_selected);
            } else if(list.get(f).getPlayingstatus().equals("0")) {
                status.setVisibility(View.VISIBLE);
                status.setImageResource(R.drawable.player_deselected);
            }

            if (list.get(f).getCaptain().equals("1")){
                cap.setVisibility(View.VISIBLE);
            }else
                cap.setVisibility(View.GONE);

            if (list.get(f).getVc().equals("1")) {
                vc.setVisibility(View.VISIBLE);
            }else
                vc.setVisibility(View.GONE);

            if(list.get(f).getTeam().equals("team1"))
                name.setBackground(getResources().getDrawable(R.drawable.blue_name_bg));
            else
                name.setBackground(getResources().getDrawable(R.drawable.red_name_bg));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
            params.weight = 1.0f;
            params.gravity = Gravity.CENTER;
            v.setLayoutParams(params);


            ll.addView(v);
        }
    }

}
