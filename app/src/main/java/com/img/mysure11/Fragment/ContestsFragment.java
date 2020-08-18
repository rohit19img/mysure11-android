package com.img.mysure11.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.img.mysure11.Activity.AllChallengesActivity;
import com.img.mysure11.Activity.CreateTeamActivity;
import com.img.mysure11.Activity.CreateTeamFootballActivity;
import com.img.mysure11.Activity.JoinByCodeActivity;
import com.img.mysure11.Activity.LeaderboardActivity;
import com.img.mysure11.Activity.MakeChallengeActivity;
import com.img.mysure11.Adapter.ChallengeCategoryListAdapter;
import com.img.mysure11.Adapter.TeamListAdapter;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.contestCategoriesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class ContestsFragment extends Fragment {

    Context context;
    RecyclerView ll;
    SwipeRefreshLayout swipeRefreshLayout;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ArrayList <contestCategoriesGetSet> catList,catList1;
    ArrayList<MyTeamsGetSet> teamList = new ArrayList<>();
    View v;

    public ContestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_contests, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        ll = v.findViewById(R.id.ll);
        ll.setLayoutManager(new LinearLayoutManager(context));

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);

        v.findViewById(R.id.joinContest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, JoinByCodeActivity.class));
            }
        });

        v.findViewById(R.id.addContest).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, MakeChallengeActivity.class));
            }
        });

        v.findViewById(R.id.filter).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent ii = new Intent(context, AllChallengesActivity.class);
                ii.putExtra("catid",String.valueOf(0));
                context.startActivity(ii);

            }
        });

        v.findViewById(R.id.createTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gv.getSportType().equals("Cricket")) {
                    startActivity(
                            new Intent(context, CreateTeamActivity.class)
                                    .putExtra("teamNumber", (teamList.size() + 1))
                    );
                } else {
                    startActivity(
                            new Intent(context, CreateTeamFootballActivity.class)
                                    .putExtra("teamNumber", (teamList.size() + 1))
                    );
                }
            }
        });

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet()) {
                    progressDialog.show();
                    getChallenges();
                    MyTeam(v);
                } else
                    new AppUtils().NoInternet(context);

                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            getChallenges();
            MyTeam(v);
        } else {
            new AppUtils().NoInternet(context);
            ((Activity)context).finish();
        }
    }

    public void getChallenges(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<contestCategoriesGetSet>> call = apiSeitemViewice.getContests(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<contestCategoriesGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<contestCategoriesGetSet>> call, Response<ArrayList<contestCategoriesGetSet>> response) {

                Log.i("Contests",response.toString());
                Log.i("Contests",response.message().toString());

                if(response.code() == 200) {

                    catList = response.body();
                    catList1 = new ArrayList<>();

                    for(contestCategoriesGetSet zz:catList){
                        if(zz.getContest().size() > 0)
                            catList1.add(zz);
                    }

                    ll.setAdapter(new ChallengeCategoryListAdapter(context,catList1));

                    if(progressDialog!=null)
                        progressDialog.dismiss();

                } else if(response.code() == 401) {
                    new AppUtils().Toast(context,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    (getActivity()).finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getChallenges();
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
            public void onFailure(Call<ArrayList<contestCategoriesGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i("Contests",t.getMessage());
                Log.i("Contests",t.toString());
            }
        });
    }

    public void MyTeam(final View v){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.MyTeams(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i("Teams", "Number of movies received: complete");

                Log.i("Teams", "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i("Teams", "Number of movies received: " + String.valueOf(response.body().size()));

                    teamList = response.body();

                    if(teamList.size() == gv.getMax_teams())
                        v.findViewById(R.id.createTeam).setVisibility(View.GONE);
                }else {
                    Log.i("Teams", "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeam(v);
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    d.show();
                }
            }
            @Override
            public void onFailure(Call<ArrayList<MyTeamsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i("Teams", t.toString());
            }
        });
    }

}
