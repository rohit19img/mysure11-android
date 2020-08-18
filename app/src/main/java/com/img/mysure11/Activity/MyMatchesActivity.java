package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.Fragment.CricketMyMatchesFragment;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class MyMatchesActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TabLayout tab;
    ViewPager vp;
    int tab_icons[] = {R.drawable.ic_cricket,R.drawable.ic_football};
    int tab_icons_selected[] = {R.drawable.ic_cricket_selected,R.drawable.ic_football_selected};
    ArrayList<joinedMatchesGetSet> joinedMatchList,joinedlistCricket,joinedlistFootbal;

    SwipeRefreshLayout swipeRefreshLayout;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_matches);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);

        findViewById(R.id.notification).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMatchesActivity.this,NotificationActivity.class));
            }
        });

        findViewById(R.id.wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMatchesActivity.this,WalletActivity.class));
            }
        });

        findViewById(R.id.profileImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMatchesActivity.this,ProfileActivity.class));
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pos = vp.getCurrentItem();
                if(cd.isConnectingToInternet()) {
                    progressDialog.show();
                    joinedmatches();
                } else
                    new AppUtils().NoInternet(MyMatchesActivity.this);
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        vp.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                swipeRefreshLayout.setEnabled(false);
                switch (event.getAction()) {
                    case MotionEvent.ACTION_UP:
                        swipeRefreshLayout.setEnabled(true);
                        break;
                }
                return false;
            }
        });

        ImageView homeIcon = findViewById(R.id.matchesIcon);
        homeIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        TextView homeText = findViewById(R.id.matchesText);
        homeText.setTextColor(getResources().getColor(R.color.colorPrimary));

        findViewById(R.id.homeLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMatchesActivity.this,HomeActivity.class));
                finishAffinity();
            }
        });

        findViewById(R.id.profileLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMatchesActivity.this,ProfileActivity.class));
            }
        });

        findViewById(R.id.moreLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MyMatchesActivity.this,MoreActivity.class));
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(session.getTeamName().equals("") || session.getState().equals("")){
            startActivity(new Intent(MyMatchesActivity.this,UserFirstDetailsActivity.class));
            finishAffinity();
        } else{
            if(cd.isConnectingToInternet()) {
                progressDialog.show();
                joinedmatches();
            } else {
                new AppUtils().NoInternet(MyMatchesActivity.this);
                finish();
            }
        }
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CricketMyMatchesFragment(joinedlistCricket,"Cricket");
                default:
                    return new CricketMyMatchesFragment(joinedlistFootbal,"Football");
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
                    return "CRICKET";
                default:
                    return "FOOTBALL";
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pos = vp.getCurrentItem();
    }

    public void joinedmatches(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<joinedMatchesGetSet>> call = apiSeitemViewice.joinedmatches(session.getUserId());
        call.enqueue(new Callback<ArrayList<joinedMatchesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<joinedMatchesGetSet>> call, Response<ArrayList<joinedMatchesGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    joinedMatchList = response.body();

                    joinedlistCricket = new ArrayList<>();
                    joinedlistFootbal = new ArrayList<>();

                    for(joinedMatchesGetSet zz:joinedMatchList){
                        if(zz.getType().equals("Cricket"))
                            joinedlistCricket.add(zz);
                        else
                            joinedlistFootbal.add(zz);
                    }

                    progressDialog.dismiss();

                    tab.setupWithViewPager(vp);
                    vp.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));
                    vp.setCurrentItem(pos);

                    for (int i = 0; i < tab.getTabCount(); i++) {
                        if(i != 0)
                            tab.getTabAt(i).setIcon(tab_icons[i]);
                        else
                            tab.getTabAt(i).setIcon(tab_icons_selected[i]);
                    }

                    vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            for (int i = 0; i < tab.getTabCount(); i++) {
                                if(i != position)
                                    tab.getTabAt(i).setIcon(tab_icons[i]);
                                else
                                    tab.getTabAt(i).setIcon(tab_icons_selected[i]);
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {
                            for (int i = 0; i < tab.getTabCount(); i++) {
                                if(i != position)
                                    tab.getTabAt(i).setIcon(tab_icons[i]);
                                else
                                    tab.getTabAt(i).setIcon(tab_icons_selected[i]);
                            }

                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                } else if(response.code() == 401) {
                    new AppUtils().Toast(MyMatchesActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(MyMatchesActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            joinedmatches();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<joinedMatchesGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

}
