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
import com.img.mysure11.Fragment.CricketHomeFragment;
import com.img.mysure11.GetSet.bannersGetSet;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.GetSet.upcomingMatchesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TabLayout tab;
    ViewPager vp;
    int tab_icons[] = {R.drawable.ic_cricket,R.drawable.ic_football};
    int tab_icons_selected[] = {R.drawable.ic_cricket_selected,R.drawable.ic_football_selected};

    ArrayList<upcomingMatchesGetSet> matchList,listCricket,listFootbal;
    ArrayList<joinedMatchesGetSet> joinedMatchList,joinedlistCricket,joinedlistFootbal;
    ArrayList<bannersGetSet> imageList = new ArrayList();

    SwipeRefreshLayout swipeRefreshLayout;
    int pos = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

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
                startActivity(new Intent(HomeActivity.this,NotificationActivity.class));
            }
        });

        findViewById(R.id.wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,WalletActivity.class));
            }
        });

        findViewById(R.id.profileImage).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pos = vp.getCurrentItem();
                if(cd.isConnectingToInternet()) {
                    progressDialog.show();
                    getMatches();
                } else
                    new AppUtils().NoInternet(HomeActivity.this);
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

        ImageView homeIcon = findViewById(R.id.homeIcon);
        homeIcon.setImageTintList(ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary)));
        TextView homeText = findViewById(R.id.homeText);
        homeText.setTextColor(getResources().getColor(R.color.colorPrimary));

        findViewById(R.id.myMatchesLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MyMatchesActivity.class));
            }
        });

        findViewById(R.id.profileLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,ProfileActivity.class));
            }
        });

        findViewById(R.id.moreLL).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(HomeActivity.this,MoreActivity.class));
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        gv.setPlaying11_status("0");
        if(session.getTeamName().equals("") || session.getState().equals("")){
            startActivity(new Intent(HomeActivity.this,UserFirstDetailsActivity.class));
            finishAffinity();
        } else{
            if(cd.isConnectingToInternet()) {
                progressDialog.show();
                getBanners();
            } else
                new AppUtils().NoInternet(HomeActivity.this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        pos = vp.getCurrentItem();
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CricketHomeFragment(listCricket,imageList,joinedlistCricket,"Cricket");
                default:
                    return new CricketHomeFragment(listFootbal,imageList,joinedlistFootbal,"Football");
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

    public void getBanners(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<bannersGetSet>> call = apiSeitemViewice.getmainbanner(session.getUserId());
        call.enqueue(new Callback<ArrayList<bannersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<bannersGetSet>> call, Response<ArrayList<bannersGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    imageList = response.body();

                    getMatches();
                } else if(response.code() == 401) {
                    new AppUtils().Toast(HomeActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(HomeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getBanners();
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
            public void onFailure(Call<ArrayList<bannersGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    public void getMatches(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<upcomingMatchesGetSet>> call = apiSeitemViewice.getmatchlist(session.getUserId());
        call.enqueue(new Callback<ArrayList<upcomingMatchesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<upcomingMatchesGetSet>> call, Response<ArrayList<upcomingMatchesGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    matchList = response.body();

                    listCricket = new ArrayList<>();
                    listFootbal = new ArrayList<>();

                    for(upcomingMatchesGetSet zz:matchList){
                        if(zz.getType().equals("Cricket"))
                            listCricket.add(zz);
                        else
                            listFootbal.add(zz);
                    }

                    joinedmatches();

                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(HomeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getMatches();
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
            public void onFailure(Call<ArrayList<upcomingMatchesGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
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

                    if(progressDialog!=null)
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


                } else if(response.code() == 401){
                    new AppUtils().Toast(HomeActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                }

                else {
                    AlertDialog.Builder d = new AlertDialog.Builder(HomeActivity.this);
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
