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
import android.widget.ImageView;
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
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.R;
import com.img.mysure11.Static.TermsActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
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

public class JoinContestActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView availableBalance,entryFee,bonus,toPay,btnJoinContest;
    int challenge_id,count = 1;
    String teamid ="";
    double usableB,joiningB;

    CountDownTimer ct1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_contest);

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

        final CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

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

        availableBalance= findViewById(R.id.availableBalance);
        entryFee= findViewById(R.id.entryFee);
        bonus= findViewById(R.id.bonus);
        toPay= findViewById(R.id.toPay);
        btnJoinContest= findViewById(R.id.btnJoinContest);

        challenge_id = getIntent().getExtras().getInt("challenge_id");
        teamid=getIntent().getExtras().getString("team");

        findViewById(R.id.terms).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(JoinContestActivity.this, TermsActivity.class));
            }
        });

        if(getIntent().hasExtra("count"))
            count = getIntent().getExtras().getInt("count");

        if (count == 0) {
            count = 1;
        }

        ct1 = new CountDownTimer(10000,1000) {
            @Override
            public void onTick(long millisUntilFinished) {

            }

            @Override
            public void onFinish() {
                AlertDialog.Builder d = new AlertDialog.Builder(JoinContestActivity.this);
                d.setMessage("Something went wrong...");
                d.setNegativeButton("Ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestQueue.stop();
                        if(progressDialog!=null)
                            progressDialog.dismiss();
                        dialog.dismiss();
                        hellohello();
                        finish();

                    }
                });
                d.show();
            }
        };


        btnJoinContest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(cd.isConnectingToInternet())
                    joinChallenge(teamid);

                ct1.start();
            }
        });

        if(cd.isConnectingToInternet()) {
            CheckBalance();
        }

    }

    public void CheckBalance(){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"getUsableBalance?challengeid="+challenge_id;
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

                                availableBalance.setText("Unutilized Balance + Winnings = ₹" + jsonObject.getString("usablebalance"));
                                bonus.setText("-₹" + jsonObject.getDouble("bonus")*count);
                                entryFee.setText("₹" + (jsonObject.getDouble("entryfee")*count));
                                double pay = jsonObject.getDouble("entryfee")*count - jsonObject.getDouble("bonus")*count;
                                toPay.setText("₹" + String.format("%.1f", pay));

                                usableB = jsonObject.getDouble("usablebalance");
                                joiningB = jsonObject.getDouble("entryfee")*count;

                                if (pay > usableB) {
                                    new AppUtils().Toast(JoinContestActivity.this, "Insufficient Balance");
                                    Intent ii = new Intent(new Intent(JoinContestActivity.this, AddBalanceActivity.class));
                                    ii.putExtra("balance",jsonObject.getString("usertotalbalance"));
                                    startActivity(ii);
                                    gv.setPaytype("contest");
                                    finish();
                                }

                                if(progressDialog!=null)
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
                            if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(JoinContestActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CheckBalance();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinContestActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CheckBalance();
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

    public void joinChallenge(final String id){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"joinleauge";
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

                                ct1.cancel();

                                if(progressDialog!=null)
                                    progressDialog.dismiss();

                                Log.i("message",jsonObject.getString("message"));
                                if(jsonObject.getBoolean("success")){
//                                    session.setWallet_amount(jsonObject.getString("totalbalance"));

                                    if(gv.isIsprivate()){
                                        gv.setIsprivate(false);

                                        String shareBody ="You’ve been challenged! \n" +
                                                "\n" +
                                                "Think you can beat me? Join the contest on "+getResources().getString(R.string.app_name)+" for the "+gv.getTeam1()+" vs "+gv.getTeam2()+" match and prove it!\n" +
                                                "\n" +
                                                "Use Contest Code "+jsonObject.getString("refercode")+" & join the action NOW!"+
                                                "\nDownload Application from "+ getResources().getString(R.string.apk_url);
                                        Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                                        sharingIntent.setType("text/plain");

                                        sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                                        startActivity(Intent.createChooser(sharingIntent, "Share via"));
                                    }
                                    finish();

                                }else {
                                    new AppUtils().showError(JoinContestActivity.this, jsonObject.getString("message"));
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
                                new AppUtils().Toast(JoinContestActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinContestActivity.this);
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
                    params.put("challengeid",String.valueOf(challenge_id));
                    params.put("teamid",id);
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

    public void hellohello(){
        progressDialog.show();
        try {
            String url = "http://mysure11.com/Mysure11/hellohello";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            finish();
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            finish();
                        }
                    })
            {

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
