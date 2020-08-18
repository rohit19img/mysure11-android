package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.app.Dialog;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.Fragment.LiveContestFragment;
import com.img.mysure11.Fragment.PlayerStatsFragment;
import com.img.mysure11.R;

public class LiveChallengesActivity extends AppCompatActivity {

    TabLayout tab;
    ViewPager vp;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_challenges);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        matchName = findViewById(R.id.matchName);
        matchStatus = findViewById(R.id.matchStatus);

        matchName.setText(gv.getTeam1()+" vs " + gv.getTeam2());
        matchStatus.setText(gv.getStatus());

        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);

        tab.setupWithViewPager(vp);
        vp.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new LiveContestFragment();
                default:
                    return new PlayerStatsFragment();
            }
        }

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Contests";
                default:
                    return "Player Stats";
            }
        }
    }

}
