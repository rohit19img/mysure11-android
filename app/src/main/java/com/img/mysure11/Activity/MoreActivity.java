package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.R;
import com.img.mysure11.Static.AboutUsActivity;
import com.img.mysure11.Static.FairPlayActivity;
import com.img.mysure11.Static.FantasyPointSystemActivity;
import com.img.mysure11.Static.FantasyPointSystemFootballActivity;
import com.img.mysure11.Static.HelpDeskActivity;
import com.img.mysure11.Static.HowtoPlayActivity;
import com.img.mysure11.Static.HowtoPlayFootballActivity;
import com.img.mysure11.Static.InviteFriendsActivity;
import com.img.mysure11.Static.JoinByCodeMoreActivity;
import com.img.mysure11.Static.LegalityActivity;
import com.img.mysure11.Static.MyReferalsActivity;
import com.img.mysure11.Static.TermsActivity;

public class MoreActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ListView moreList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_more);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        moreList = findViewById(R.id.moreList);

        String Ar[];
        if(session.getUserType().equals("normal user")) {
            Ar = new String[] {"Refer & Earn", "Contest Invite Code",/*"Whatsapp Updates",*/"Fantasy Points System - Cricket", "Fantasy Points System - Football"
                    , "How to Play - Cricket", "How to Play - Football", "Helpdesk", "FairPlay", "About Us", "Legality", "Terms & Conditions"};
        } else {
            Ar = new String[]{"Refer & Earn", "Contest Invite Code",/*"Whatsapp Updates",*/"Fantasy Points System - Cricket", "Fantasy Points System - Football"
                    , "How to Play - Cricket", "How to Play - Football", "Helpdesk", "FairPlay", "About Us", "Legality", "Terms & Conditions","My Referal"};
        }

        moreList.setAdapter(new adapter(this,Ar));

        moreList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        startActivity(new Intent(MoreActivity.this, InviteFriendsActivity.class));
                        break;
                    case 1:
                        startActivity(new Intent(MoreActivity.this, JoinByCodeMoreActivity.class));
                        break;
                    case 2:
                        startActivity(new Intent(MoreActivity.this, FantasyPointSystemActivity.class));
                        break;
                    case 3:
                        startActivity(new Intent(MoreActivity.this, FantasyPointSystemFootballActivity.class));
                        break;
                    case 4:
                        startActivity(new Intent(MoreActivity.this, HowtoPlayActivity.class));
                        break;
                    case 5:
                        startActivity(new Intent(MoreActivity.this, HowtoPlayFootballActivity.class));
                        break;
                    case 6:
                        startActivity(new Intent(MoreActivity.this, HelpDeskActivity.class));
                        break;
                    case 7:
                        startActivity(new Intent(MoreActivity.this, FairPlayActivity.class));
                        break;
                    case 8:
                        startActivity(new Intent(MoreActivity.this, AboutUsActivity.class));
                        break;
                    case 9:
                        startActivity(new Intent(MoreActivity.this, LegalityActivity.class));
                        break;
                    case 10:
                        startActivity(new Intent(MoreActivity.this, TermsActivity.class));
                        break;
                    case 11:
                        startActivity(new Intent(MoreActivity.this, MyReferalsActivity.class));
                        break;
                }
            }
        });

        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,NotificationActivity.class));
            }
        });

        findViewById(R.id.wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,WalletActivity.class));
            }
        });

        findViewById(R.id.profileImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,ProfileActivity.class));
            }
        });

        ImageView homeIcon = findViewById(R.id.moreIcon);
        homeIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        TextView homeText = findViewById(R.id.moreText);
        homeText.setTextColor(getResources().getColor(R.color.colorPrimary));

        findViewById(R.id.myMatchesLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,MyMatchesActivity.class));
            }
        });

        findViewById(R.id.profileLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,ProfileActivity.class));
            }
        });

        findViewById(R.id.homeLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MoreActivity.this,HomeActivity.class));
                finishAffinity();
            }
        });

    }

    class adapter extends BaseAdapter{

        Context context;
        String Ar[];

        int Icons[] = {R.drawable.refer_icon_min,R.drawable.ic_envelope/*,R.drawable.ic_whatsapp*/,R.drawable.ic_bars_more,R.drawable.ic_bars_more
                ,R.drawable.ic_howtoplay,R.drawable.ic_howtoplay,R.drawable.ic_helpdesk,R.drawable.ic_fairplay,R.drawable.ic_about_us
                ,R.drawable.ic_legality,R.drawable.ic_terms,R.drawable.ic_terms};

        public adapter(Context context,String Ar[]) {
            this.context = context;
            this.Ar = Ar;
        }

        @Override
        public int getCount() {
            return Ar.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View v;

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v =inflater.inflate(R.layout.more_list,null);

            TextView title = v.findViewById(R.id.title);
            title.setText(Ar[i]);

            ImageView icon = v.findViewById(R.id.icon);
            icon.setImageResource(Icons[i]);

            return v;
        }
    }
}
