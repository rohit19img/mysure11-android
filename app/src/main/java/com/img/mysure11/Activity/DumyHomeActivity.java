package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Fragment.CricketDumyFragment;
import com.img.mysure11.GetSet.bannersGetSet;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.GetSet.upcomingMatchesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class DumyHomeActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TabLayout tab;
    ViewPager vp;
    int tab_icons[] = {R.drawable.ic_cricket,R.drawable.ic_football};
    int tab_icons_selected[] = {R.drawable.ic_cricket_selected,R.drawable.ic_football_selected};

    ArrayList<upcomingMatchesGetSet> matchList,listCricket,listFootbal;
    ArrayList<bannersGetSet> imageList = new ArrayList();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dumy_home);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            getBanners();
        } else
            new AppUtils().NoInternet(DumyHomeActivity.this);
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new CricketDumyFragment(listCricket,imageList,"Cricket");
                default:
                    return new CricketDumyFragment(listFootbal,imageList,"Football");
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

        Call<ArrayList<bannersGetSet>> call = apiSeitemViewice.getmainbanner();
        call.enqueue(new Callback<ArrayList<bannersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<bannersGetSet>> call, Response<ArrayList<bannersGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    imageList = response.body();

                    getMatches();
                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(DumyHomeActivity.this);
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

        Call<ArrayList<upcomingMatchesGetSet>> call = apiSeitemViewice.getmatchlist();
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

                    tab.setupWithViewPager(vp);
                    vp.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

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

                    progressDialog.dismiss();
                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(DumyHomeActivity.this);
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
}
