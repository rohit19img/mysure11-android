package com.img.mysure11.Fragment;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.fantasyScorecardGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayerStatsFragment extends Fragment {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    Context context;
    ListView scoreCard;
    ArrayList<fantasyScorecardGetSet> list;
    String TAG = "";

    public PlayerStatsFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_player_stats, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        scoreCard= (ListView) v.findViewById(R.id.scoreCard);

        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            PlayerStats();
        }

        return v;
    }

    public void PlayerStats(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<fantasyScorecardGetSet>> call = apiSeitemViewice.fantasyscorecards(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<fantasyScorecardGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<fantasyScorecardGetSet>> call, Response<ArrayList<fantasyScorecardGetSet>> response) {

                Log.i("Response is","Received");

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();
                    scoreCard.setAdapter(new fantasyScorecardAdapter(context, list));
                } else if(response.code() == 401) {
                    new AppUtils().Toast(context,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    ((Activity)context).finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            PlayerStats();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                        }
                    });
                }
                progressDialog.dismiss();
            }
            @Override
            public void onFailure(Call<ArrayList<fantasyScorecardGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                progressDialog.dismiss();
            }
        });
    }

    public class fantasyScorecardAdapter extends BaseAdapter {

        Context context;
        ArrayList<fantasyScorecardGetSet> list;

        public  fantasyScorecardAdapter(Context context, ArrayList<fantasyScorecardGetSet> list){
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int i, View convertView, ViewGroup parent) {
            View v;

            LayoutInflater inflater=(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.scorecard_list,null);

            TextView playerName,playerPoints,selectper;
            ImageView playerImage;

            playerImage=(ImageView)v.findViewById(R.id.playerImage);
            playerName=(TextView)v.findViewById(R.id.playerName);
            playerPoints=(TextView)v.findViewById(R.id.playerPoints);
            selectper=(TextView)v.findViewById(R.id.selectper);

            playerName.setText(list.get(i).getPlayername());
            playerPoints.setText(list.get(i).getTotal());
            selectper.setText(list.get(i).getSelectper());
            Picasso.with(context).load(list.get(i).getPlayerimage()).placeholder(R.drawable.avtar).into(playerImage);

            return v;
        }
    }

}
