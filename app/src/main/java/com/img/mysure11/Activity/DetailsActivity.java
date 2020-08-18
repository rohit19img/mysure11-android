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
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.Fragment.LeaderboardFragment;
import com.img.mysure11.Fragment.PriceCardFragment;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.priceCardGetSet;
import com.img.mysure11.GetSet.teamsGetSet;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class DetailsActivity extends AppCompatActivity {

    int challenge_id;
    int tabPos = 1;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchTime;

    TabLayout tab;
    ViewPager vp;

    TextView prizeMoney,numWinners,jointxt,entryFee,teamsLeft,totalTeams,prize1,totalEntries;
    TextView m,confirm,b,bonusPer;
    LinearLayout bll;
    ProgressBar teamEnteredPB;

    String TAG = "myTeams";
    ArrayList<MyTeamsGetSet> selectedteamList;
    ArrayList<priceCardGetSet> priceCardList = new ArrayList<>();
    ArrayList<teamsGetSet>teams = new ArrayList<>();
    int multi_entry = 0;
    boolean isIsselected = false;
    String referCode = "";

    int tab_icons[] = {R.drawable.prize_breakup,R.drawable.leaderboard};
    int tab_icons_selected[] = {R.drawable.prize_selected,R.drawable.leaderboard_selected};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        challenge_id = getIntent().getExtras().getInt("challenge_id");
        if(getIntent().hasExtra("tabPos"))
            tabPos = getIntent().getExtras().getInt("tabPos");

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

        final long diffInMs = endDate.getTime() - startDate.getTime();

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

        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);

        prizeMoney = findViewById(R.id.prizeMoney);
        numWinners = findViewById(R.id.numWinners);
        jointxt = findViewById(R.id.jointxt);
        entryFee = findViewById(R.id.entryFee);
        teamsLeft = findViewById(R.id.teamsLeft);
        totalTeams = findViewById(R.id.totalTeams);
        prize1 = findViewById(R.id.prize1);
        totalEntries = findViewById(R.id.totalEntries);

        bll = findViewById(R.id.bll);
        bonusPer = findViewById(R.id.bonusPer);
        b = findViewById(R.id.b);
        confirm = findViewById(R.id.c);
        m = findViewById(R.id.m);

        teamEnteredPB = findViewById(R.id.teamEnteredPB);

        findViewById(R.id.btnJoin).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isIsselected){
                    String code = referCode;

                    String shareBody ="You’ve been challenged! \n" +
                            "\n" +
                            "Think you can beat me? Join the contest on "+getResources().getString(R.string.app_name)+" for the "+gv.getTeam1()+" vs "+gv.getTeam2()+" match and prove it!\n" +
                            "\n" +
                            "Use Contest Code "+code+" & join the action NOW!"+
                            "\nDownload Application from "+ getResources().getString(R.string.apk_url);

                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }else{
                    MyTeams(challenge_id);
                }
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            Details();
        } else {
            new AppUtils().NoInternet(DetailsActivity.this);
            finish();
        }

    }

    @Override
    protected void onPause() {
        super.onPause();
        tabPos = vp.getCurrentItem();
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new PriceCardFragment(priceCardList);
                default:
                    return new LeaderboardFragment(teams,challenge_id);
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
                    return "Prize Breakup";
                default:
                    return "Leaderboard";
            }
        }
    }

    public void Details(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"leaugesdetails?challengeid="+challenge_id;
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                if(jsonObject.getString("contest_type").equals("Amount")){
                                    if(jsonObject.getInt("totalwinners")==1)
                                        numWinners.setText("1");
                                    else
                                         numWinners.setText(jsonObject.getInt("totalwinners")+" ▼");

                                    int left= jsonObject.getInt("maximum_user") - jsonObject.getInt("joinedusers");
                                    if(left!=0) {
                                        if(left == 1)
                                            teamsLeft.setText("" + left + " team left");
                                        else
                                            teamsLeft.setText("" + left + " teams left");
                                    }else
                                         teamsLeft.setText("Contest Completed");
                                    totalTeams.setText(jsonObject.getInt("maximum_user")+ " Teams");

                                    teamEnteredPB.setMax(jsonObject.getInt("maximum_user"));
                                     teamEnteredPB.setProgress(jsonObject.getInt("joinedusers"));

                                }else {
                                     numWinners.setText(jsonObject.getInt("winning_percentage") + " % Winners");
                                    if(jsonObject.getInt("joinedusers") == 1)
                                         teamsLeft.setText(jsonObject.getInt("joinedusers")+" team joined");
                                    else
                                         teamsLeft.setText(jsonObject.getInt("joinedusers")+" teams joined");
                                    totalTeams.setText("∞ Teams");

                                    teamEnteredPB.setMax(jsonObject.getInt("joinedusers"));
                                    teamEnteredPB.setProgress(jsonObject.getInt("joinedusers"));
                                }
                                if(jsonObject.getBoolean("isselected")){
                                    jointxt.setText("Invite");
                                }else {
                                    jointxt.setText("Join");
                                }

                                isIsselected = jsonObject.getBoolean("isselected");
                                referCode = jsonObject.getString("refercode");

                                if(jsonObject.getInt("multi_entry") == 1)
                                    totalEntries.setText("Upto "+gv.getMax_teams()+" Entries");
                                else
                                    totalEntries.setText("Single Entry");

                                multi_entry = jsonObject.getInt("multi_entry");

                                if(jsonObject.getInt("multi_entry") == 1)
                                    m.setVisibility(View.VISIBLE);
                                else
                                    m.setVisibility(View.GONE);

                                if(jsonObject.getInt("is_bonus") == 1) {
                                    bll.setVisibility(View.VISIBLE);
                                    bonusPer.setText(jsonObject.getString("bonus_percentage")+"%");
                                }  else
                                    bll.setVisibility(View.GONE);

                                if(jsonObject.getInt("confirmed_challenge") == 1)
                                    confirm.setVisibility(View.VISIBLE);
                                else
                                    confirm.setVisibility(View.GONE);

                                if(jsonObject.has("jointeams")){
                                    teams = new ArrayList<>();
                                    JSONArray jointeams = jsonObject.getJSONArray("jointeams");

                                    for(int i =0; i < jointeams.length(); i++){
                                        JSONObject job = jointeams.getJSONObject(i);

                                        teamsGetSet ob = new teamsGetSet();
                                        ob.setImage(job.getString("image"));
                                        ob.setJid(job.getInt("jid"));
                                        ob.setTeam(job.getString("team"));
                                        ob.setTeamid(job.getInt("teamid"));
                                        ob.setTeamnumber(job.getInt("teamnumber"));

                                        teams.add(ob);
                                    }
                                } else
                                    teams = new ArrayList<>();

                                Log.i("teamSize",String.valueOf(teams.size()));

                                if(jsonObject.has("price_card")){
                                    priceCardList = new ArrayList<>();
                                    JSONArray price_card = jsonObject.getJSONArray("price_card");

                                    for(int i =0; i < price_card.length(); i++){
                                        JSONObject job = price_card.getJSONObject(i);

                                        priceCardGetSet ob = new priceCardGetSet();
                                        ob.setPrice(job.getString("price"));
                                        ob.setStart_position(job.getString("start_position"));

                                        priceCardList.add(ob);
                                    }

                                    if(priceCardList.size() == 0){
                                        prize1.setText("₹ "+jsonObject.getInt("win_amount")+"");
                                        priceCardList = new ArrayList<>();

                                        priceCardGetSet ob1 = new priceCardGetSet();
                                        ob1.setStart_position("1");
                                        ob1.setPrice(String.valueOf((int) Math.round(jsonObject.getDouble("win_amount"))));
                                        priceCardList.add(ob1);

                                        gv.setPriceCard(priceCardList);

                                    } else {
                                        prize1.setText("₹ " + priceCardList.get(0).getPrice());
                                        gv.setPriceCard(priceCardList);
                                    }

                                } else{
                                    priceCardList = new ArrayList<>();
                                    prize1.setText("₹ "+jsonObject.getInt("win_amount")+"");

                                    priceCardGetSet ob = new priceCardGetSet();
                                    ob.setStart_position("1");
                                    ob.setPrice(String.valueOf((int) Math.round(jsonObject.getDouble("win_amount"))));
                                    priceCardList.add(ob);

                                    gv.setPriceCard(priceCardList);

                                }

                                entryFee.setText("Entry Fee : ₹"+jsonObject.getInt("entryfee"));

                                if(jsonObject.getInt("win_amount")!=0)
                                    prizeMoney.setText("₹ "+jsonObject.getInt("win_amount")+"");
                                else {
                                    prizeMoney.setText("Net Practice");
                                    prizeMoney.setTextSize(12);

                                    jointxt.setVisibility(View.GONE);
                                }

                                tab.setupWithViewPager(vp);
                                vp.setAdapter(new SectionPagerAdapter(getSupportFragmentManager()));

                                vp.setCurrentItem(tabPos);

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
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

                            progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                new AppUtils().Toast(DetailsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(DetailsActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Details();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                        }
                    })
            {

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }
            };
            strRequest.setShouldCache(false);
            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

    public void MyTeams(final int challengeid){
        progressDialog.show();
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.MyTeams(session.getUserId(),gv.getMatchKey(), String.valueOf(challengeid));
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {
                    progressDialog.dismiss();
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    selectedteamList = new ArrayList<>();
                    selectedteamList = response.body();
                    int total = selectedteamList.size();
                    int count =0;
                    int teamid =0;
                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isSelected())
                            count++;
                        else
                            teamid = zz.getTeamid();
                    }
                    int result = total-count;

                    if(result == 0){
                        if(gv.getSportType().equals("Cricket")) {
                            Intent ii = new Intent(DetailsActivity.this, CreateTeamActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        } else {
                            Intent ii = new Intent(DetailsActivity.this, CreateTeamFootballActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        }
                    }else if (result == 1) {
                        Intent intent = new Intent(DetailsActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id", challengeid);
                        intent.putExtra("team", String.valueOf(teamid));
                        gv.setMulti_entry(String.valueOf(multi_entry));
                        startActivity(intent);
                    } else {
                        gv.setSelectedteamList(selectedteamList);
                        Intent intent = new Intent(DetailsActivity.this, ChooseTeamActivity.class);
                        intent.putExtra("type", "join");
                        intent.putExtra("challengeId", challengeid);
                        startActivity(intent);
                        gv.setMulti_entry(String.valueOf(multi_entry));
                    }
                } else if(response.code() == 401) {
                    new AppUtils().Toast(DetailsActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(DetailsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeams(challengeid);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MyTeamsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
            }
        });
    }

}
