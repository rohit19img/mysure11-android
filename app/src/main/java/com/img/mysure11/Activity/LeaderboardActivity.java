package com.img.mysure11.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Adapter.MainLeaderBoardAdapter;
import com.img.mysure11.Adapter.SpinnerAdapter;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.mainLeaderboardGetSet;
import com.img.mysure11.GetSet.seriesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class LeaderboardActivity extends AppCompatActivity {

    Spinner seriesSpinner;
    RecyclerView leaderboardRecycler;
    MainLeaderBoardAdapter adapter;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ArrayList<seriesGetSet> seriesList;
    ArrayList<mainLeaderboardGetSet> teams,teams1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("Tournament Leaderboard");

        seriesSpinner = findViewById(R.id.seriesSpinner);
        leaderboardRecycler = findViewById(R.id.leaderboardRecycler);
        leaderboardRecycler.setLayoutManager(new LinearLayoutManager(this));

        seriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position != 0)
                    getLeaderboard(seriesList.get(position-1).getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            getSeries();
        } else {
            new AppUtils().NoInternet(this);
            finish();
        }
    }

    public void getSeries(){
        progressDialog.show();
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<seriesGetSet>> call = apiSeitemViewice.getallseries(session.getUserId());
        call.enqueue(new Callback<ArrayList<seriesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<seriesGetSet>> call, Response<ArrayList<seriesGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    seriesList = response.body();
                    String Ar [] = new String[seriesList.size()+1];

                    Ar[0] = "Select Tournament";
                    for(int i =0; i< seriesList.size(); i++){
                        Ar[i+1] = seriesList.get(i).getName();
                    }
                    seriesSpinner.setAdapter(new SpinnerAdapter(LeaderboardActivity.this,Ar));

                    getLeaderboard(seriesList.get(0).getId());
                } else if(response.code() == 401) {
                    new AppUtils().Toast(LeaderboardActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(LeaderboardActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getSeries();
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
            public void onFailure(Call<ArrayList<seriesGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    public void getLeaderboard(final String series_id){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<mainLeaderboardGetSet>> call = apiSeitemViewice.getleaderboard(session.getUserId(),series_id);
        call.enqueue(new Callback<ArrayList<mainLeaderboardGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<mainLeaderboardGetSet>> call, Response<ArrayList<mainLeaderboardGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    teams = response.body();
                    teams1 = new ArrayList<>();

                    if (teams != null) {
                        if (teams.size() > 50)
                            teams1 = new ArrayList<>(teams.subList(0, 50));
                        else
                            teams1 = teams;

                        adapter = new MainLeaderBoardAdapter(LeaderboardActivity.this, teams1,series_id);
                        leaderboardRecycler.setAdapter(adapter);

                        leaderboardRecycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
                            @Override
                            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                                super.onScrolled(recyclerView, dx, dy);
                                if (!recyclerView.canScrollVertically(1)) {
                                    if (teams.size() > teams1.size()) {
                                        int x, y;
                                        if ((teams.size() - teams1.size()) >= 50) {
                                            x = teams1.size();
                                            y = x + 50;
                                        } else {
                                            x = teams1.size();
                                            y = x + teams.size() - teams1.size();
                                        }
                                        for (int i = x; i < y; i++) {
                                            teams1.add(teams.get(i));
                                        }
                                        adapter.notifyDataSetChanged();
                                    }

                                }
                            }
                        });
                    }

                    progressDialog.dismiss();
                } else if(response.code() == 401) {
                    new AppUtils().Toast(LeaderboardActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(LeaderboardActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getLeaderboard(series_id);
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
            public void onFailure(Call<ArrayList<mainLeaderboardGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

}
