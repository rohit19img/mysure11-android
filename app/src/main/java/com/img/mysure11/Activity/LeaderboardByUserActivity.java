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
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Adapter.UserLeaderBoardAdapter;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.LeaderboardUserGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class LeaderboardByUserActivity extends AppCompatActivity {

    RecyclerView leaderboardRecycler;
    UserLeaderBoardAdapter adapter;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String userid,seriesid;
    ArrayList<LeaderboardUserGetSet> teams,teams1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard_by_user);

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

        userid = getIntent().getExtras().getString("userid");
        seriesid = getIntent().getExtras().getString("seriesid");

        leaderboardRecycler = findViewById(R.id.leaderboardRecycler);
        leaderboardRecycler.setLayoutManager(new LinearLayoutManager(this));

        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            getLeaderboard();
        } else {
            new AppUtils().NoInternet(this);
            finish();
        }
    }

    public void getLeaderboard(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<LeaderboardUserGetSet>> call = apiSeitemViewice.getleaderboardbyuser(session.getUserId(),seriesid,userid);
        call.enqueue(new Callback<ArrayList<LeaderboardUserGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<LeaderboardUserGetSet>> call, Response<ArrayList<LeaderboardUserGetSet>> response) {

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

                        adapter = new UserLeaderBoardAdapter(LeaderboardByUserActivity.this, teams1);
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
                    new AppUtils().Toast(LeaderboardByUserActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(LeaderboardByUserActivity.this);
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

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<LeaderboardUserGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

}
