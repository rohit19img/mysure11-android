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
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
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

public class MakeChallengeActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchTime,entryFee;
    TextInputLayout name,amount,numWinners;
    Switch switchMultiple;
    Button btnCreate;
    int multi_entry = 0,challengeId;
    String joinnigB = "";

    String TAG = "myTeams";
    ArrayList<MyTeamsGetSet> selectedteamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_challenge);

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

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        name=(TextInputLayout)findViewById(R.id.name);
        amount=(TextInputLayout)findViewById(R.id.amount);
        numWinners=(TextInputLayout)findViewById(R.id.numWinners);

        entryFee=(TextView)findViewById(R.id.entryFee);

        switchMultiple=(Switch)findViewById(R.id.switchMultiple);
        btnCreate=(Button)findViewById(R.id.btnCreate);

        amount.getEditText().addTextChangedListener(watch1);
        numWinners.getEditText().addTextChangedListener(watch2);

        switchMultiple.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked)
                    multi_entry=1;
                else
                    multi_entry =0;
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        findViewById(R.id.wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MakeChallengeActivity.this,WalletActivity.class));
            }
        });
    }

    TextWatcher watch1= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!amount.getEditText().getText().toString().equals("") && !amount.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("")){
                Double total= (Double.parseDouble(amount.getEditText().getText().toString())*1.15) / Double.parseDouble(numWinners.getEditText().getText().toString()) ;
                entryFee.setText("₹ "+String.format("%.2f", total));
            } else {
                entryFee.setText("₹ 0.0");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    TextWatcher watch2= new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            if(!amount.getEditText().getText().toString().equals("") && !amount.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("") && !numWinners.getEditText().getText().toString().equals("")){
                Double total= (Double.parseDouble(amount.getEditText().getText().toString())*1.15) / Double.parseDouble(numWinners.getEditText().getText().toString()) ;
                entryFee.setText("₹ "+String.format("%.2f", total));
            } else {
                entryFee.setText("₹ 0.0");
            }
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    public void validate(){

        final String name1,amount1,numWinners1;

        name1=name.getEditText().getText().toString();
        amount1=amount.getEditText().getText().toString();
        numWinners1= numWinners.getEditText().getText().toString();

        if(name1.equals(""))
            name.setError("Please enter a name for your contest");
        else if(amount1.equals("") || Integer.parseInt(amount1)>10000)
            amount.setError("Please enter a valid amount");
        else if(numWinners1.equals("") || Integer.parseInt(numWinners1)>100 || Integer.parseInt(numWinners1) < 2 )
            numWinners.setError("Please enter valid contest size");
        else{
            joinnigB = entryFee.getText().toString().replace("₹ ","");
            if(cd.isConnectingToInternet()) {
                CreateChallenge();
            }
        }
    }

    public void CreateChallenge(){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"create_private_contest";
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

                                new AppUtils().showSuccess(MakeChallengeActivity.this,jsonObject.getString("msg"));
                                if(jsonObject.getBoolean("success")){
                                    gv.setIsprivate(true);
                                    gv.setMulti_entry(String.valueOf(multi_entry));
                                    challengeId = jsonObject.getInt("challengeid");
                                    MyTeams(0,challengeId);
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
                                new AppUtils().Toast(MakeChallengeActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MakeChallengeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateChallenge();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(MakeChallengeActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        CreateChallenge();
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

                @Override
                public Map<String, String> getParams(){
                    HashMap<String, String> params = new HashMap<String, String>();
                    params.put("matchkey",gv.getMatchKey());
                    params.put("maximum_user",numWinners.getEditText().getText().toString());
                    params.put("win_amount",amount.getEditText().getText().toString());
                    params.put("entryfee",entryFee.getText().toString().replaceAll("₹ ",""));
                    params.put("name",name.getEditText().getText().toString());
                    params.put("multi_entry",String.valueOf(multi_entry));
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
                            Intent ii = new Intent(MakeChallengeActivity.this, CreateTeamActivity.class);

                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        } else {
                            Intent ii = new Intent(MakeChallengeActivity.this, CreateTeamFootballActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        }
                    }else if (result == 1) {
                        Intent intent = new Intent(MakeChallengeActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id", challengeId);
                        intent.putExtra("team", String.valueOf(teamid));
                        gv.setMulti_entry(String.valueOf(multi_entry));
                        startActivity(intent);
                    } else {
                        gv.setSelectedteamList(selectedteamList);
                        Intent intent = new Intent(MakeChallengeActivity.this, ChooseTeamActivity.class);
                        intent.putExtra("type", "join");
                        intent.putExtra("challengeId", challengeId);
                        startActivity(intent);
                        gv.setMulti_entry(String.valueOf(multi_entry));
                    }
                } else if(response.code() == 401) {
                    new AppUtils().Toast(MakeChallengeActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(MakeChallengeActivity.this);
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
