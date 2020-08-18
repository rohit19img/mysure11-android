package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Adapter.TeamListAdapter1;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class ChooseTeamActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ArrayList<MyTeamsGetSet> selectedteamList;

    String selectedteam;
    ListView teamList;
    TextView btnCreateTeam,join,text;
    String type;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose_team);

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

        TextView title = findViewById(R.id.title);
        title.setText("Choose Team");

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


        type = getIntent().getExtras().getString("type");

        selectedteamList = gv.getSelectedteamList();

        Log.i("challenge_id",String.valueOf(getIntent().getExtras().getInt("challengeId")));

        teamList= (ListView)findViewById(R.id.teamList);
        if(type.equals("join")) {
            teamList.setAdapter(new TeamListAdapter1(ChooseTeamActivity.this, selectedteamList, type, gv.getMulti_entry()));
        } else {
            teamList.setAdapter(new TeamListAdapter1(ChooseTeamActivity.this, selectedteamList, type, "0"));
        }

        btnCreateTeam = (TextView)findViewById(R.id.btnCreateTeam);
        join = (TextView)findViewById(R.id.btnJoin);
        text = (TextView)findViewById(R.id.text);

        if(selectedteamList.size() >= gv.getMax_teams()){
            btnCreateTeam.setVisibility(View.GONE);
        }

        if(type.equals("join")){
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    selectedteam = "";
                    int count=0;
                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isPicked()) {
                            selectedteam += "," + zz.getTeamid();
                            count++;
                        }
                    }

                    if(count > 0) {
                        selectedteam = selectedteam.substring(1);

                        Intent intent = new Intent(ChooseTeamActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id", getIntent().getExtras().getInt("challengeId"));
                        intent.putExtra("team",selectedteam);
                        intent.putExtra("count",count);
                        startActivity(intent);
                        finish();
                    }else{
                        new AppUtils().showError(ChooseTeamActivity.this,"Please select your team to join this contest");
                    }
                }
            });

            btnCreateTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedteamList.size()!= gv.getMax_teams()) {
                        if(gv.getSportType().equals("Cricket")) {
                            Intent ii = new Intent(new Intent(ChooseTeamActivity.this, CreateTeamActivity.class));
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", getIntent().getExtras().getString("challengeId"));
                            startActivity(ii);
                            finish();
                        } else {
                            Intent ii = new Intent(new Intent(ChooseTeamActivity.this, CreateTeamFootballActivity.class));
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", getIntent().getExtras().getString("challengeId"));
                            startActivity(ii);
                            finish();
                        }
                    }else{
                        new AppUtils().showError(ChooseTeamActivity.this,"You can only create maximum of "+gv.getMax_teams()+" teams");
                    }
                }
            });

        }else if(type.equals("switch")){
            join.setText("Switch");
            text.setText("Choose team to switch");
            title.setText("Switch Teams");
            join.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    int count =0;
                    selectedteam = "";

                    String teamid = getIntent().getExtras().getString("teamid");

                    for(MyTeamsGetSet zz:selectedteamList){
                        if(zz.isPicked()) {
                            selectedteam = String.valueOf(zz.getTeamid());
                            count++;
                        }
                    }
//                    selectedteam = selectedteam.replaceFirst(",","");
                    Log.i("Selected team",selectedteam);
                    if(!selectedteam.equals("")){
                        SwitchTeam(selectedteam,getIntent().getExtras().getString("joinid"));
                    }else{
                        new AppUtils().showError(ChooseTeamActivity.this,"Please select your team first to switch");
                    }
                }
            });

            btnCreateTeam.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(selectedteamList.size()!= gv.getMax_teams()) {
                        if(gv.getSportType().equals("Cricket")) {
                            Intent ii = new Intent(new Intent(ChooseTeamActivity.this, CreateTeamActivity.class));
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            startActivity(ii);
                            finish();
                        } else {
                            Intent ii = new Intent(new Intent(ChooseTeamActivity.this, CreateTeamFootballActivity.class));
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            startActivity(ii);
                            finish();
                        }
                    }else{
                        new AppUtils().showError(ChooseTeamActivity.this,"You can only create maximum of "+gv.getMax_teams()+" teams");
                    }
                }
            });

        }
    }

    public void SwitchTeam(final String teamid, final String joinid){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"switchteams";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                final JSONObject jsonObject = new JSONObject(response.toString());

                                                if(progressDialog!=null)
                    progressDialog.dismiss();

                                if(jsonObject.getBoolean("success")){
                                    new AppUtils().showSuccess(ChooseTeamActivity.this, jsonObject.getString("msg"));

                                    Intent ii= new Intent(ChooseTeamActivity.this, DetailsActivity.class);
                                    ii.putExtra("challenge_id",getIntent().getExtras().getInt("challengeId"));
                                    startActivity(ii);
                                    finish();
                                }else {
                                    new AppUtils().showError(ChooseTeamActivity.this, jsonObject.getString("msg"));
                                }
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
                                new AppUtils().Toast(ChooseTeamActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(ChooseTeamActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

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

                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("matchkey",gv.getMatchKey());
                    params.put("challengeid",String.valueOf(getIntent().getExtras().getInt("challengeId")));
                    params.put("teamid", teamid);
                    params.put("joinid",joinid);
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
