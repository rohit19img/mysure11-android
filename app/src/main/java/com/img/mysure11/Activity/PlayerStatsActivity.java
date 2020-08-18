package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.ExpandableHeightListView;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MatchStatsGetSet;
import com.img.mysure11.GetSet.PlayerStatsGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class PlayerStatsActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ExpandableHeightListView statsList;
    ImageView back;
    TextView bats,bowls,nationality,birthday;
    CircleImageView img;
    String key;
    TextView title,credit,points;
    ArrayList<PlayerStatsGetSet> list;
    String TAG="Stats";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_player_stats);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        key= getIntent().getExtras().getString("key");

        title=(TextView)findViewById(R.id.title);
        title.setText(getIntent().getExtras().getString("PlayerName"));

        statsList=(ExpandableHeightListView)findViewById(R.id.statsList);
        statsList.setExpanded(true);

        img=(CircleImageView)findViewById(R.id.img);
        credit=(TextView)findViewById(R.id.credits);
        points=(TextView)findViewById(R.id.points);
        birthday=(TextView)findViewById(R.id.birthday);
        nationality=(TextView)findViewById(R.id.nationality);
        bowls=(TextView)findViewById(R.id.bowls);
        bats=(TextView)findViewById(R.id.bats);

        if(cd.isConnectingToInternet()) {
            PlayerStats();
        }
    }

    public void PlayerStats(){
        progressDialog.show();
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<PlayerStatsGetSet>> call = apiSeitemViewice.PlayerStats(session.getUserId(),key,gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<PlayerStatsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<PlayerStatsGetSet>> call, Response<ArrayList<PlayerStatsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");

                Log.i(TAG, "Number of movies received: " + response.toString());

                if(response.code() == 200) {

                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    list = response.body();

                    ArrayList<MatchStatsGetSet> matches;
                    matches = new ArrayList<>();

                    if (list.get(0).getMatches() != null)
                        matches = list.get(0).getMatches();

                    statsList.setAdapter(new PlayerStatsAdapter(PlayerStatsActivity.this, matches));

                    credit.setText(list.get(0).getPlayercredit());
                    title.setText(list.get(0).getPlayername());
                    Picasso.with(PlayerStatsActivity.this).load(list.get(0).getPlayerimage()).into(img);
                    points.setText(String.valueOf(list.get(0).getPlayerpoints()));
                    nationality.setText(list.get(0).getPlayercountry());
                    bats.setText(list.get(0).getBattingstat());
                    bowls.setText(list.get(0).getBowlerstat());

                    if(!list.get(0).getPlayerdob().equals("")) {
                        try {
                            SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd");
                            SimpleDateFormat outputFormat = new SimpleDateFormat("MMM dd,yyyy");

                            birthday.setText(outputFormat.format(inputFormat.parse(list.get(0).getPlayerdob())));
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }
                    }

                    progressDialog.dismiss();
                } else if(response.code() == 401) {
                    new AppUtils().Toast(PlayerStatsActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(PlayerStatsActivity.this);
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
            }
            @Override
            public void onFailure(Call<ArrayList<PlayerStatsGetSet>> call, Throwable t) {
                // Log error here since request failed
                progressDialog.dismiss();
                Log.i(TAG, t.toString());
            }
        });
    }

    class PlayerStatsAdapter extends BaseAdapter {

        Context context;
        ArrayList<MatchStatsGetSet> matches;

        public PlayerStatsAdapter(Context context, ArrayList<MatchStatsGetSet> matches){
            this.context=context;
            this.matches= matches;
        }

        @Override
        public int getCount() {
            return matches.size();
        }

        @Override
        public Object getItem(int i) {
            return null;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {

            View v;
            TextView matchName,date,point,selectedBy;

            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v= inflater.inflate(R.layout.player_stats_list,null);

            matchName=(TextView)v.findViewById(R.id.match_name);
            date=(TextView)v.findViewById(R.id.match_date);
            point=(TextView)v.findViewById(R.id.points);
            selectedBy=(TextView)v.findViewById(R.id.selectedBy);

            matchName.setText(matches.get(i).getShortname());
            date.setText(matches.get(i).getMatchdate());
            point.setText(String.valueOf(matches.get(i).getTotal_points()));
            selectedBy.setText(matches.get(i).getSelectper());

            return v;
        }
    }
}
