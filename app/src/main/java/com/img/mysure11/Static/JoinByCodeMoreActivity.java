package com.img.mysure11.Static;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
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
import com.img.mysure11.Activity.ChooseTeamActivity;
import com.img.mysure11.Activity.CreateTeamActivity;
import com.img.mysure11.Activity.CreateTeamFootballActivity;
import com.img.mysure11.Activity.JoinContestActivity;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class JoinByCodeMoreActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextInputLayout promoCode;
    TextView btnJoin;

    int challengeId,multi_entry = 0;
    String TAG = "myTeams";
    String match_key = "";
    ArrayList<MyTeamsGetSet> selectedteamList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_by_code_more);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("Contest Invite Code");

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);


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
            String url = getResources().getString(R.string.app_url)+"joinbycode_without_matchkey";
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

                                new AppUtils().showSuccess(JoinByCodeMoreActivity.this,jsonObject.getString("message"));
                                if(jsonObject.getBoolean("success")){

                                    gv.setSportType(jsonObject.getString("type"));
                                    gv.setMatchKey(jsonObject.getString("match_key"));
                                    gv.setTeam1(jsonObject.getString("team1name").toUpperCase());
                                    gv.setTeam2(jsonObject.getString("team2name").toUpperCase());
                                    gv.setMatchTime(jsonObject.getString("time_start"));
                                    gv.setSeries(jsonObject.getString("series"));
                                    gv.setTeam1Image(jsonObject.getString("team1logo"));
                                    gv.setTeam2image(jsonObject.getString("team2logo"));

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
                                new AppUtils().Toast(JoinByCodeMoreActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeMoreActivity.this);
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
                                AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeMoreActivity.this);
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
                            Intent ii = new Intent(JoinByCodeMoreActivity.this, CreateTeamActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        } else {
                            Intent ii = new Intent(JoinByCodeMoreActivity.this, CreateTeamFootballActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", challengeid);
                            gv.setMulti_entry(String.valueOf(multi_entry));
                            startActivity(ii);
                        }
                    }else if (result == 1) {
                        Intent intent = new Intent(JoinByCodeMoreActivity.this, JoinContestActivity.class);
                        intent.putExtra("challenge_id", challengeId);
                        intent.putExtra("team", String.valueOf(teamid));
                        gv.setMulti_entry(String.valueOf(multi_entry));
                        startActivity(intent);
                    } else {
                        gv.setSelectedteamList(selectedteamList);
                        Intent intent = new Intent(JoinByCodeMoreActivity.this, ChooseTeamActivity.class);
                        intent.putExtra("type", "join");
                        intent.putExtra("challengeId", challengeId);
                        startActivity(intent);
                        gv.setMulti_entry(String.valueOf(multi_entry));
                    }
                } else if(response.code() == 401) {
                    new AppUtils().Toast(JoinByCodeMoreActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(JoinByCodeMoreActivity.this);
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
