package com.img.mysure11.Adapter;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Activity.ChooseTeamActivity;
import com.img.mysure11.Activity.CreateTeamActivity;
import com.img.mysure11.Activity.CreateTeamFootballActivity;
import com.img.mysure11.Activity.DetailsActivity;
import com.img.mysure11.Activity.JoinContestActivity;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.contestGetSet;
import com.img.mysure11.R;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Locale;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class contests3Adapter extends RecyclerView.Adapter<contests3Adapter.MyViewHolder>{

    Context context;
    ArrayList<contestGetSet> list;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String TAG = "myTeams";
    ArrayList<MyTeamsGetSet> selectedteamList;

    public contests3Adapter(Context context, ArrayList<contestGetSet> list) {
        this.context = context;
        this.list = list;

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_list, parent, false));
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder,final int i) {

        final NumberFormat nf2 = NumberFormat.getInstance(Locale.ENGLISH);
        ((DecimalFormat)nf2).applyPattern("##,##,###.##");

        if(list.get(i).getContest_type().equals("Amount")){
            if(list.get(i).getTotalwinners()==1)
                holder.numWinners.setText("1");
            else
                holder. numWinners.setText(nf2.format(list.get(i).getTotalwinners())+" ▼");

            int left= (list.get(i).getMaximum_user()) - (list.get(i).getJoinedusers());
            if(left!=0) {
                if(left == 1)
                    holder.teamsLeft.setText("" + String.valueOf(nf2.format(left)) + " spot left");
                else
                    holder.teamsLeft.setText("" + String.valueOf(nf2.format(left)) + " spots left");
            }else
                holder. teamsLeft.setText("Contest Completed");
            holder.totalTeams.setText(nf2.format(list.get(i).getMaximum_user())+ " Spots");

            holder.teamEnteredPB.setMax(list.get(i).getMaximum_user());
            holder. teamEnteredPB.setProgress(list.get(i).getJoinedusers());

        }else {
            holder. numWinners.setText(nf2.format(list.get(i).getWinning_percentage()) + " % Winners");
            if(list.get(i).getJoinedusers() == 1)
                holder. teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" spot joined");
            else
                holder. teamsLeft.setText(nf2.format(list.get(i).getJoinedusers())+" spots joined");
            holder.totalTeams.setText("∞ Spots");

            holder.teamEnteredPB.setMax(list.get(i).getJoinedusers());
            holder.teamEnteredPB.setProgress(list.get(i).getJoinedusers());
        }
        if(list.get(i).isIsselected()){
            holder.jointxt.setText("Invite");
        }else {
            holder.jointxt.setText("Join");
        }

        if(list.get(i).getPrice_card().size() == 0){
            holder.prize1.setText("₹ "+nf2.format(list.get(i).getWin_amount())+"");
        } else
            holder.prize1.setText("₹ "+list.get(i).getPrice_card().get(0).getPrice());

        holder.entryFee.setText("Entry Fee : ₹"+list.get(i).getEntryfee());

        if(list.get(i).getMulti_entry() == 1)
            holder.totalEntries.setText("Upto 11 Entries");
        else
            holder.totalEntries.setText("Single Entry");

        if(list.get(i).getMulti_entry() == 1)
            holder.m.setVisibility(View.VISIBLE);
        else
            holder.m.setVisibility(View.GONE);

        if(list.get(i).getIs_bonus() == 1) {
            holder.bll.setVisibility(View.VISIBLE);
            holder.bonusPer.setText(list.get(i).getBonus_percentage()+"%");
        } else
            holder.bll.setVisibility(View.GONE);

        if(list.get(i).getConfirmed_challenge() == 1)
            holder.c.setVisibility(View.VISIBLE);
        else
            holder.c.setVisibility(View.GONE);

        holder.btnJoin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(list.get(i).isIsselected()){
                    String code = list.get(i).getRefercode();

                    String shareBody ="You’ve been challenged! \n" +
                            "\n" +
                            "Think you can beat me? Join the contest on "+context.getResources().getString(R.string.app_name)+" for the "+gv.getTeam1()+" vs "+gv.getTeam2()+" match and prove it!\n" +
                            "\n" +
                            "Use Contest Code "+list.get(i).getRefercode()+" & join the action NOW!"+
                            "\nDownload Application from "+ context.getResources().getString(R.string.apk_url);



                    Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                    sharingIntent.setType("text/plain");

                    sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                    context.startActivity(Intent.createChooser(sharingIntent, "Share via"));
                }else{
                    MyTeams(i,list.get(i).getId());
                }
            }
        });

        if(list.get(i).getWin_amount()!=0)
            holder.prizeMoney.setText("₹ "+nf2.format(list.get(i).getWin_amount())+"");
        else {
            holder.prizeMoney.setText("Net Practice");
            holder.prizeMoney.setTextSize(12);

            holder.jointxt.setVisibility(View.GONE);
        }

        holder.numWinners.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getId());
                Intent ii= new Intent(context, DetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getId());
                ii.putExtra("tabPos",0);
                context.startActivity(ii);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getId());
                Intent ii= new Intent(context, DetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getId());
                context.startActivity(ii);
            }
        });

    }

    @Override
    public int getItemCount() {
        if(list.size() < 3)
            return list.size();
        else
            return 3;
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ll,winnerLL,btnJoin,bll;
        TextView prizeMoney,numWinners,jointxt,entryFee,teamsLeft,totalTeams,prize1,totalEntries,b,c,m,bonusPer;
        ProgressBar teamEnteredPB;

        public MyViewHolder(View v){
            super(v);

            ll = v.findViewById(R.id.ll);
            winnerLL = v.findViewById(R.id.winnerLL);
            btnJoin = v.findViewById(R.id.btnJoin);

            prizeMoney = v.findViewById(R.id.prizeMoney);
            numWinners = v.findViewById(R.id.numWinners);
            jointxt = v.findViewById(R.id.jointxt);
            entryFee = v.findViewById(R.id.entryFee);
            teamsLeft = v.findViewById(R.id.teamsLeft);
            totalTeams = v.findViewById(R.id.totalTeams);
            prize1 = v.findViewById(R.id.prize1);
            totalEntries = v.findViewById(R.id.totalEntries);
            bll = v.findViewById(R.id.bll);
            bonusPer = v.findViewById(R.id.bonusPer);
            b = v.findViewById(R.id.b);
            c = v.findViewById(R.id.c);
            m = v.findViewById(R.id.m);

            teamEnteredPB = v.findViewById(R.id.teamEnteredPB);
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
                    if(progressDialog!=null)
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
                            Intent ii = new Intent(context, CreateTeamActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", list.get(pos).getId());
                            gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
                            context.startActivity(ii);
                        } else {
                            Intent ii = new Intent(context, CreateTeamFootballActivity.class);
                            ii.putExtra("teamNumber", selectedteamList.size() + 1);
                            ii.putExtra("challengeId", list.get(pos).getId());
                            gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
                            context.startActivity(ii);
                        }
                    }else if (result == 1) {
                        Intent intent = new Intent(context, JoinContestActivity.class);
                        intent.putExtra("challenge_id", list.get(pos).getId());
                        intent.putExtra("team", String.valueOf(teamid));
                        gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
                        context.startActivity(intent);
                    } else {
                        gv.setSelectedteamList(selectedteamList);
                        Intent intent = new Intent(context, ChooseTeamActivity.class);
                        intent.putExtra("type", "join");
                        intent.putExtra("challengeId", list.get(pos).getId());
                        context.startActivity(intent);
                        gv.setMulti_entry(String.valueOf(list.get(pos).getMulti_entry()));
                    }
                } else if(response.code() == 401) {
                    new AppUtils().Toast(context,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    ((Activity)context).finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(context);
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
                            if(progressDialog!=null)
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
