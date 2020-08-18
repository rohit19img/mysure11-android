package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
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
import com.google.android.material.textfield.TextInputLayout;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
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

public class JoinByCodeActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchTime;

    TextInputLayout promoCode;
    TextView btnJoin;

    int challengeId,multi_entry = 0;
    String TAG = "myTeams";
    ArrayList<MyTeamsGetSet> selectedteamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_by_code);

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

        promoCode=(TextInputLayout)findViewById(R.id.promoCode);
        btnJoin=(TextView)findViewById(R.id.btnJoin);

        btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(promoCode.getEditText().getText().toString().equals(""))
                    promoCode.setError("Please enter challenge code");
                else
                {
                    JoinByCode(promoCode.getEditText().getText().toString());
                }
            }
        });

    }

    public void JoinByCode(final String code){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"joinbycode";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                progressDialog.dismiss();
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                new AppUtils().showSuccess(JoinByCodeActivity.this,jsonObject.getString("message"));
                                if(jsonObject.getBoolean("success")){
                                    challengeId = jsonObject.getInt("challengeid");
                                    multi_entry = jsonObject.getInt("multi_entry");
                                    gv.setMulti_entry(String.valueOf(jsonObject.getInt("multi_entry")));
                                    MyTeams(0,challengeId);
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
                                new AppUtils().Toast(JoinByCodeActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JoinByCode(code);
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        JoinByCode(code);
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
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getParams(){
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("matchkey",gv.getMatchKey());
                    params.put("getcode",code);
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

    public void MyTeams(final int pos, final int challengeid){
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
                            Intent ii = new Intent(JoinByCodeActivity.this, CreateTeamActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        } else {
                            Intent ii = new Intent(JoinByCodeActivity.this, CreateTeamFootballActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        }
                    }else if (result == 1) {
                        Intent intent = new Intent(JoinByCodeActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id", challengeId);
                        intent.putExtra("team", String.valueOf(teamid));
                        gv.setMulti_entry(String.valueOf(multi_entry));
                        startActivity(intent);
                    } else {
                        gv.setSelectedteamList(selectedteamList);
                        Intent intent = new Intent(JoinByCodeActivity.this, ChooseTeamActivity.class);
                        intent.putExtra("type", "join");
                        intent.putExtra("challengeId", challengeId);
                        startActivity(intent);
                        gv.setMulti_entry(String.valueOf(multi_entry));
                    }
                } else if(response.code() == 401) {
                    new AppUtils().Toast(JoinByCodeActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeams(pos,challengeid);
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

