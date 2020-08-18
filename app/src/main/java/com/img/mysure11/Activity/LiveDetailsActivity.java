package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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
import com.img.mysure11.Fragment.LiveLeaderboardFragment;
import com.img.mysure11.Fragment.PriceCardFragment;
import com.img.mysure11.GetSet.liveLeaderboardGetSet;
import com.img.mysure11.GetSet.priceCardGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class LiveDetailsActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchStatus;

    int challenge_id;
    int tabPos = 1;

    TabLayout tab;
    ViewPager vp;

    TextView prizeMoney,numWinners,entryFee;
    ArrayList<priceCardGetSet> priceCardList = new ArrayList<>();
    ArrayList<liveLeaderboardGetSet> teams;
    String TAG = "Leaderboard";

    int tab_icons[] = {R.drawable.prize_breakup,R.drawable.leaderboard};
    int tab_icons_selected[] = {R.drawable.prize_selected,R.drawable.leaderboard_selected};

    TextView seriesName,team1Name,team1Score,team2Name,team2Score,status,matchResult,team1Overs,team2Overs;
    CircleImageView teamLogo1,teamLogo2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_live_details);

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
        matchStatus = findViewById(R.id.matchStatus);

        matchName.setText(gv.getTeam1()+" vs " + gv.getTeam2());
        matchStatus.setText(gv.getStatus());

        seriesName = findViewById(R.id.seriesName);
        team1Name = findViewById(R.id.team1Name);
        team1Score = findViewById(R.id.team1Score);
        team2Name = findViewById(R.id.team2Name);
        team2Score = findViewById(R.id.team2Score);
        matchResult = findViewById(R.id.matchResult);
        team1Overs = findViewById(R.id.team1Overs);
        team2Overs = findViewById(R.id.team2Overs);
        status = findViewById(R.id.status);

        teamLogo1 = findViewById(R.id.teamLogo1);
        teamLogo2 = findViewById(R.id.teamLogo2);

        seriesName.setText(gv.getSeriesName());
        team1Name.setText(gv.getTeam1());
        team2Name.setText(gv.getTeam2());
        team1Score.setText("-");
        team2Score.setText("-");
        team1Overs.setText("-");
        team2Overs.setText("-");
        status.setText(gv.getStatus());

        Picasso.with(LiveDetailsActivity.this).load(gv.getTeam1Image()).into(teamLogo1);
        Picasso.with(LiveDetailsActivity.this).load(gv.getTeam2image()).into(teamLogo2);

        tab = findViewById(R.id.tab);
        vp = findViewById(R.id.vp);

        prizeMoney = findViewById(R.id.prizeMoney);
        numWinners = findViewById(R.id.numWinners);
        entryFee = findViewById(R.id.entryFee);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            Details();
        } else {
            new AppUtils().NoInternet(LiveDetailsActivity.this);
            finish();
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
                    return new PriceCardFragment(priceCardList);
                default:
                    return new LiveLeaderboardFragment(teams,challenge_id);
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
                                }else {
                                    numWinners.setText(jsonObject.getInt("winning_percentage") + " % Winners");
                                }

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
                                        priceCardList = new ArrayList<>();

                                        priceCardGetSet ob1 = new priceCardGetSet();
                                        ob1.setStart_position("1");
                                        ob1.setPrice(String.valueOf((int) Math.round(jsonObject.getDouble("win_amount"))));
                                        priceCardList.add(ob1);

                                        gv.setPriceCard(priceCardList);

                                    } else {
                                        gv.setPriceCard(priceCardList);
                                    }

                                } else{
                                    priceCardList = new ArrayList<>();

                                    priceCardGetSet ob = new priceCardGetSet();
                                    ob.setStart_position("1");
                                    ob.setPrice(String.valueOf((int) Math.round(jsonObject.getDouble("win_amount"))));
                                    priceCardList.add(ob);

                                    gv.setPriceCard(priceCardList);

                                }

                                entryFee.setText("₹"+jsonObject.getInt("entryfee"));

                                if(jsonObject.getInt("win_amount")!=0)
                                    prizeMoney.setText("₹ "+jsonObject.getInt("win_amount")+"");
                                else {
                                    prizeMoney.setText("Net Practice");
                                    prizeMoney.setTextSize(12);

                                }

                                getLeaderboard();

//                                progressDialog.dismiss();
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

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
                                new AppUtils().Toast(LiveDetailsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(LiveDetailsActivity.this);
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

    public void getLeaderboard(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<liveLeaderboardGetSet>> call = apiSeitemViewice.getleaderboard_challenge(session.getUserId(),challenge_id,gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<liveLeaderboardGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<liveLeaderboardGetSet>> call, Response<ArrayList<liveLeaderboardGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    teams = response.body();

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

                    Scores();

                } else if(response.code() == 401) {
                    new AppUtils().Toast(LiveDetailsActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(LiveDetailsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getLeaderboard();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ArrayList<liveLeaderboardGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                progressDialog.dismiss();
            }
        });
    }

    public void Scores(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"getlivescores?matchkey="+gv.getMatchKey();
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                team1Score.setText(jsonObject.getString("Team1_Totalruns1")+"/"+jsonObject.getString("Team1_Totalwickets1"));
                                team1Overs.setText("("+jsonObject.getString("Team1_Totalovers1")+")");
                                if(!jsonObject.getString("Team1_Totalovers2").equals("0")) {
                                    team1Score.append(", " + jsonObject.getString("Team1_Totalruns2") + "/" + jsonObject.getString("Team1_Totalwickets2"));
                                    team1Overs.setText("("+jsonObject.getString("Team1_Totalovers1")+", " + jsonObject.getString("Team1_Totalovers2")+")");
                                }

                                team2Score.setText(jsonObject.getString("Team2_Totalruns1")+"/"+jsonObject.getString("Team2_Totalwickets1"));
                                team2Overs.setText("("+jsonObject.getString("Team2_Totalovers1")+")");
                                if(!jsonObject.getString("Team2_Totalovers2").equals("0")) {
                                    team1Score.append(", " + jsonObject.getString("Team2_Totalruns2") + "/" + jsonObject.getString("Team2_Totalwickets2"));
                                    team2Overs.setText("("+jsonObject.getString("Team2_Totalovers1")+", " + jsonObject.getString("Team2_Totalovers2")+")");
                                }

                                matchResult.setText(jsonObject.getString("Winning_Status"));


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
                                new AppUtils().Toast(LiveDetailsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(LiveDetailsActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Scores();
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

}
