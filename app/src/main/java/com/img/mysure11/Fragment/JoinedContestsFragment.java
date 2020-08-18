package com.img.mysure11.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Activity.AllChallengesActivity;
import com.img.mysure11.Activity.LeaderboardActivity;
import com.img.mysure11.Adapter.joinedContestsAdapter;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.contestGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class JoinedContestsFragment extends Fragment {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    Context context;
    TabLayout tabLayout;

    RecyclerView ll;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<contestGetSet> list;
    LinearLayout noContestLL;

    public JoinedContestsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_joined_contests, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        tabLayout = (TabLayout)((Activity)context).findViewById(R.id.tab);

        ll = v.findViewById(R.id.ll);
        ll.setLayoutManager(new LinearLayoutManager(context));

        noContestLL = v.findViewById(R.id.noContestLL);

        swipeRefreshLayout = v.findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet()) {
                    progressDialog.show();
                    getChallenges();
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
        } else {
            new AppUtils().NoInternet(context);
        }
    }

    public void getChallenges(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<contestGetSet>> call = apiSeitemViewice.myjoinedleauges(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<contestGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<contestGetSet>> call, Response<ArrayList<contestGetSet>> response) {

                Log.i("Contests",response.toString());
                Log.i("Contests",response.message().toString());

                if(response.code() == 200) {

                    list = response.body();

                    if(list.size() == 0){
                        noContestLL.setVisibility(View.VISIBLE);
                    } else {
                        noContestLL.setVisibility(View.GONE);
                        ll.setAdapter(new joinedContestsAdapter(context, list));
                    }
                    tabLayout.getTabAt(1).setText("My Contests("+list.size()+")");

                                    if(progressDialog!=null)
                    progressDialog.dismiss();

                } else if(response.code() == 401) {
                    new AppUtils().Toast(context,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    getActivity().finishAffinity();
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
            public void onFailure(Call<ArrayList<contestGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i("Contests",t.getMessage());
                Log.i("Contests",t.toString());
            }
        });
    }

}
