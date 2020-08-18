package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
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
import com.img.mysure11.Fragment.PlayersFragment;
import com.img.mysure11.GetSet.PlayerListGetSet;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class CreateTeamActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchTime,marqueText;

    ImageView team1Image,team2Image;
    TextView team1_name,team2_name,leftAmount;
    TabLayout playersTab;
    ViewPager vp;
    Button btnPreview,btnContinue;

    String TAG="Players";
    ArrayList<PlayerListGetSet> list,listWK,listBAT,listAR,listBOWL;
    boolean edit = false;
    ArrayList<captainListGetSet> Clist;
    int teamNumber,challengeId;
    public static Activity fa;

    TextView maxPlayers,totalPlayers;
    String captain="",vc="",chooseType="",joinid="";

    int tab_icons[] = {R.drawable.wk_icon,R.drawable.bat_icon,R.drawable.ar_icon,R.drawable.bowl_icon};
    int tab_icons_selected[] = {R.drawable.wk_selected,R.drawable.bat_selected,R.drawable.ar_selected,R.drawable.bowl_selected};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_team);

        fa = this;

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
        matchName.setText(gv.getTeam1()+" vs " + gv.getTeam2());

        matchTime = findViewById(R.id.matchTime);
        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate = gv.getMatchTime();
        Log.i("matchtime", gv.getMatchTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        long diffInMs = endDate.getTime() - startDate.getTime();

        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {
                matchTime.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + " : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + " : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + ""));
            }

            public void onFinish()
            {
                if(progressDialog!=null)
                    progressDialog.dismiss();


                requestQueue.stop();

                finish();
            }
        };
        cT.start();

        if(getIntent().hasExtra("teamNumber"))
            teamNumber = getIntent().getExtras().getInt("teamNumber");
        else
            teamNumber = 1;

        if(getIntent().hasExtra("chooseType")) {
            chooseType = getIntent().getExtras().getString("chooseType");
            joinid = getIntent().getExtras().getString("joinid");
        }
        else
            chooseType = "";

        marqueText = findViewById(R.id.marqueText);
        if(gv.getPlaying11_status().equals("1")) {
            marqueText.setSelected(true);
            marqueText.setVisibility(View.VISIBLE);
        }

        if(teamNumber > gv.getMax_teams()){
            new AppUtils().showError(CreateTeamActivity.this,"Can't create more teams");
            finish();
        }

        maxPlayers=(TextView)findViewById(R.id.maxPlayers);
        totalPlayers=(TextView)findViewById(R.id.totalPlayers);
        leftAmount=(TextView)findViewById(R.id.leftAmount);

        if(getIntent().hasExtra("challengeId")){
            challengeId=getIntent().getExtras().getInt("challengeId");
        }
        else {
            challengeId = 0;
        }

        if(getIntent().hasExtra("edit")) {
            edit = getIntent().getExtras().getBoolean("edit");

            Clist = gv.getCaptainList();
            for(captainListGetSet zz:Clist){
                if(zz.getCaptain().equals("1"))
                    captain = zz.getId();
                if(zz.getVc().equals("1"))
                    vc = zz.getId();
            }
        }

        team1_name =(TextView)findViewById(R.id.team1_name);
        team2_name =(TextView)findViewById(R.id.team2_name);

        team1_name.setText(gv.getTeam1());
        team2_name.setText(gv.getTeam2());

        team1Image=(ImageView)findViewById(R.id.team1Image);
        team2Image=(ImageView)findViewById(R.id.team2Image);

        Picasso.with(CreateTeamActivity.this).load(gv.getTeam1Image()).into(team1Image);
        Picasso.with(CreateTeamActivity.this).load(gv.getTeam2image()).into(team2Image);

        playersTab=(TabLayout)findViewById(R.id.playersTab);
        vp = (ViewPager)findViewById(R.id.vp);

        btnContinue=(Button)findViewById(R.id.btnContinue);
        btnPreview=(Button)findViewById(R.id.btnPreview);

        if(cd.isConnectingToInternet())
            PlayersList();

        findViewById(R.id.ques).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clist= new ArrayList<>();
                for(PlayerListGetSet zz:list){
                    if(zz.isSelected()){
                        captainListGetSet ob= new captainListGetSet();

                        ob.setCaptain("N");
                        ob.setName(zz.getName());
                        ob.setId(zz.getId());
                        if(zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }if(zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }if(zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }if(zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setVc("N");
                        ob.setType(zz.getRole());
                        ob.setTeamname(zz.getTeamname());
                        ob.setTeam(zz.getTeam());
                        ob.setPoints(zz.getTotalpoints());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        ob.setPlayingstatus(zz.getPlayingstatus());
                        ob.setCaptain_selection_percentage(zz.getCaptain_selection_percentage());
                        ob.setVice_captain_selection_percentage(zz.getVice_captain_selection_percentage());

                        Log.i("team",zz.getTeam());

                        if(edit){
                            if(zz.getId().equals(captain))
                                ob.setCaptain("Y");
                            if(zz.getId().equals(vc))
                                ob.setVc("Y");
                        }

                        Clist.add(ob);
                    }
                }
                if(Clist.size() != 11){
                    new AppUtils().showError(CreateTeamActivity.this,"Complete your team first");
                }else{
                    String option;
                    if(edit)
                        option="Edit";
                    else
                        option="Create";

                    gv.setCaptainList(Clist);

                    Intent ii= new Intent(CreateTeamActivity.this,CaptainViceCaptainActivity.class);
                    ii.putExtra("teamNumber",teamNumber);
                    ii.putExtra("challengeId",challengeId);
                    ii.putExtra("option",option);
                    ii.putExtra("chooseType",chooseType);
                    ii.putExtra("joinid",joinid);
                    startActivity(ii);
                }
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Clist= new ArrayList<>();
                for(PlayerListGetSet zz:list){
                    if(zz.isSelected()){
                        captainListGetSet ob= new captainListGetSet();

                        ob.setCaptain("N");
                        ob.setName(zz.getName());
                        ob.setId(zz.getId());
                        if(zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }if(zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }if(zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }if(zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setVc("N");
                        ob.setType(zz.getRole());
                        ob.setTeamname(zz.getTeamname());
                        ob.setTeam(zz.getTeam());
                        ob.setImage(zz.getImage());
                        ob.setPoints(zz.getTotalpoints());
                        ob.setCredit(zz.getCredit());
                        ob.setPlayingstatus(zz.getPlayingstatus());

                        Clist.add(ob);
                    }
                }

                gv.setCaptainList(Clist);

                Intent ii= new Intent(CreateTeamActivity.this, TeamPreviewActivity.class);
                ii.putExtra("team_name","Team "+teamNumber);
                ii.putExtra("TeamID",0);
                startActivity(ii);
            }
        });
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {

        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PlayersFragment(list,listWK,"Select 1-4 Wicket-Keepers",edit);
                case 1:
                    return new PlayersFragment(list,listBAT,"Select 3-6 Batsmen",edit);
                case 2:
                    return new PlayersFragment(list,listAR,"Select 1-4 All-Rounders",edit);
                default:
                    return new PlayersFragment(list,listBOWL,"Select 3-6 Bowlers",edit);
            }
        }

        @Override
        public int getCount() {
            return 4;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Wk (0)";
                case 1:
                    return "Bat (0)";
                case 2:
                    return "Ar (0)";
                default:
                    return "Bowl (0)";
            }
        }

    }

    public void PlayersList(){
        progressDialog.show();
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<PlayerListGetSet>> call = apiSeitemViewice.PlayersList(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<PlayerListGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<PlayerListGetSet>> call, Response<ArrayList<PlayerListGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    listWK = new ArrayList<>();
                    listBAT = new ArrayList<>();
                    listAR = new ArrayList<>();
                    listBOWL = new ArrayList<>();

                    for (PlayerListGetSet zz : list) {
                        if (zz.getRole().equals("keeper"))
                            listWK.add(zz);
                        if (zz.getRole().equals("batsman"))
                            listBAT.add(zz);
                        if (zz.getRole().equals("allrounder"))
                            listAR.add(zz);
                        if (zz.getRole().equals("bowler"))
                            listBOWL.add(zz);
                    }

                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            return (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? -1: (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? 1:0 ;
                        }
                    });

                    playersTab.setupWithViewPager(vp);
                    vp.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

                    for (int i = 0; i < playersTab.getTabCount(); i++) {
                        if(i != 0)
                            playersTab.getTabAt(i).setIcon(tab_icons[i]);
                        else
                            playersTab.getTabAt(i).setIcon(tab_icons_selected[i]);
                    }

                    vp.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
                        @Override
                        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                            for (int i = 0; i < playersTab.getTabCount(); i++) {
                                if(i != position)
                                    playersTab.getTabAt(i).setIcon(tab_icons[i]);
                                else
                                    playersTab.getTabAt(i).setIcon(tab_icons_selected[i]);
                            }
                        }

                        @Override
                        public void onPageSelected(int position) {
                            for (int i = 0; i < playersTab.getTabCount(); i++) {
                                if(i != position)
                                    playersTab.getTabAt(i).setIcon(tab_icons[i]);
                                else
                                    playersTab.getTabAt(i).setIcon(tab_icons_selected[i]);
                            }
                        }

                        @Override
                        public void onPageScrollStateChanged(int state) {

                        }
                    });

                                    if(progressDialog!=null)
                    progressDialog.dismiss();

                } else if(response.code() == 401) {
                    new AppUtils().Toast(CreateTeamActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(CreateTeamActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayersList();
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
            public void onFailure(Call<ArrayList<PlayerListGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                                if(progressDialog!=null)
                    progressDialog.dismiss();

            }
        });
    }

}
