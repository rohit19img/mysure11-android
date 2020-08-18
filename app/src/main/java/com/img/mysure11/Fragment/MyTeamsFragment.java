package com.img.mysure11.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Activity.CreateTeamActivity;
import com.img.mysure11.Activity.CreateTeamFootballActivity;
import com.img.mysure11.Adapter.TeamListAdapter;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class MyTeamsFragment extends Fragment {

    ListView teamList;
    LinearLayout noTeamLL;
    ArrayList<MyTeamsGetSet> list;
    String TAG="MyTeams";

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    Context context;
    TabLayout tabLayout;
    View v;

    public MyTeamsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v =  inflater.inflate(R.layout.fragment_my_teams, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        teamList = v.findViewById(R.id.teamList);
        noTeamLL = v.findViewById(R.id.noTeamLL);

        tabLayout = (TabLayout)((Activity)context).findViewById(R.id.tab);

        v.findViewById(R.id.createTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        v.findViewById(R.id.createTeam).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(gv.getSportType().equals("Cricket")) {
                    if(list.size() != gv.getMax_teams()) {
                        Intent ii = new Intent(context, CreateTeamActivity.class);
                        ii.putExtra("teamNumber",list.size()+1);
                        startActivity(ii);
                    }else
                    {
                        new AppUtils().showError(context,"Cannot create more team.");
                    }
                } else {
                    if(list.size() != gv.getMax_teams()) {
                        Intent ii = new Intent(context, CreateTeamFootballActivity.class);
                        ii.putExtra("teamNumber",list.size()+1);
                        startActivity(ii);
                    }else
                    {
                        new AppUtils().showError(context,"Cannot create more team.");
                    }                }
            }
        });


        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()){
            MyTeam();
        }
    }

    public void MyTeam(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<MyTeamsGetSet>> call = apiSeitemViewice.MyTeams(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<MyTeamsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<MyTeamsGetSet>> call, Response<ArrayList<MyTeamsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    if (list.size() > 0) {
                        teamList.setVisibility(View.VISIBLE);
                        noTeamLL.setVisibility(View.GONE);
                        teamList.setAdapter(new TeamListAdapter(context, list));
                    } else {
                        teamList.setVisibility(View.GONE);
                        noTeamLL.setVisibility(View.VISIBLE);
                    }

                    tabLayout.getTabAt(2).setText("My Teams("+list.size()+")");

                    if (list.size() == gv.getMax_teams()) {
                        v.findViewById(R.id.createTeam).setVisibility(View.GONE);
                    }else {
                        v.findViewById(R.id.createTeam).setVisibility(View.VISIBLE);
                    }

                }else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTeam();
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
                Log.i(TAG, t.toString());
            }
        });
    }

}
