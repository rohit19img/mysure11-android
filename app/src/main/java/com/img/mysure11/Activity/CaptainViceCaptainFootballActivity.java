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
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Adapter.captainListAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.captainListGetSet;
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

public class CaptainViceCaptainFootballActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchTime;

    int teamNumber,challengeId=0;
    ListView playerList;
    Button btnPreview,btnContinue;
    ArrayList<captainListGetSet> list,list1;
    String selectedPlayers="",captain_id="",viceCaptain_id="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_captain_vice_captain_football);

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
        matchName.setText(gv.getTeam1()+" vs "+ gv.getTeam2());

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

        findViewById(R.id.wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(CaptainViceCaptainFootballActivity.this,WalletActivity.class));
            }
        });

        list= new ArrayList<>();
        list= gv.getCaptainList();

        teamNumber = getIntent().getExtras().getInt("teamNumber");
        challengeId = getIntent().getExtras().getInt("challengeId");

        Log.i("teamNumber",String.valueOf(teamNumber));
        Log.i("challengeId",String.valueOf(challengeId));

        btnContinue=(Button)findViewById(R.id.btnContinue);
        btnPreview=(Button)findViewById(R.id.btnPreview);

        playerList=(ListView)findViewById(R.id.playerList);
        final captainListGetSet ob = new captainListGetSet();
        ob.setPoints("");
        ob.setCredit("");
        ob.setTeam("");
        ob.setRole("");
        ob.setCaptain("N");
        ob.setVc("N");
        ob.setId("0");
        ob.setImage("");
        ob.setName("");

        list1= new ArrayList<>();
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("GK"))
                list1.add(zz);
        }

        list1.add(ob);
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("DEF"))
                list1.add(zz);
        }

        list1.add(ob);
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("MF"))
                list1.add(zz);
        }

        list1.add(ob);
        for(captainListGetSet zz:list){
            if(zz.getRole().equals("ST"))
                list1.add(zz);
        }

        playerList.setAdapter(new captainListAdapter(CaptainViceCaptainFootballActivity.this,list1));

        btnContinue.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedPlayers="";
                for(captainListGetSet zz:list){
                    if(selectedPlayers.equals(""))
                        selectedPlayers=zz.getId();
                    else
                        selectedPlayers= selectedPlayers+","+zz.getId();

                    Log.i("Selected players sent",zz.getId()+",");

                    if(zz.getCaptain().equals("Y"))
                        captain_id= zz.getId();
                    if(zz.getVc().equals("Y"))
                        viceCaptain_id= zz.getId();
                }

                Log.i("Selected players sent",selectedPlayers);

                if(viceCaptain_id.equals("")  || captain_id.equals("") || captain_id.equals(viceCaptain_id)) {
                    new AppUtils().showError(CaptainViceCaptainFootballActivity.this,"Please select captain & vice-captain");
                }else{
                    if(cd.isConnectingToInternet()){
                        CreateTeam();
                    }
                }
            }
        });

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii= new Intent(CaptainViceCaptainFootballActivity.this, TeamPreviewFootballActivity.class);
                ii.putExtra("team_name","Team "+teamNumber);
                ii.putExtra("TeamID",0);
                startActivity(ii);
            }
        });

    }

    public void CreateTeam(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"createmyteam";
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


                                if(jsonObject.getBoolean("success")){
                                    if(challengeId !=0){
                                        new AppUtils().showError(CaptainViceCaptainFootballActivity.this,jsonObject.getString("msg"));
                                        Intent intent = new Intent(CaptainViceCaptainFootballActivity.this, JoinContestActivity.class);
                                        intent.putExtra("challenge_id",challengeId);
                                        intent.putExtra("team",jsonObject.getString("teamid"));
                                        startActivity(intent);
                                    }else {
                                        new AppUtils().showError(CaptainViceCaptainFootballActivity.this,jsonObject.getString("msg"));
                                    }
                                    finish();
                                    CreateTeamFootballActivity.fa.finish();
                                }else {
                                    new AppUtils().showError(CaptainViceCaptainFootballActivity.this,jsonObject.getString("msg"));
                                }
                                progressDialog.dismiss();
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
                                new AppUtils().Toast(CaptainViceCaptainFootballActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(CaptainViceCaptainFootballActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateTeam();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(CaptainViceCaptainFootballActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateTeam();
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
                public Map<String, String> getParams() {
                    Map<String, String> map = new HashMap<String, String>();
                    map.put("matchkey", gv.getMatchKey());
                    map.put("teamnumber", String.valueOf(getIntent().getExtras().getInt("teamNumber")));
                    map.put("players", selectedPlayers);
                    map.put("captain",captain_id);
                    map.put("vicecaptain",viceCaptain_id);
                    map.put("player_type","classic");

                    Log.i("map",map.toString());

                    return map;
                }

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
