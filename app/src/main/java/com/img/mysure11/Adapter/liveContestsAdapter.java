package com.img.mysure11.Adapter;

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
import com.img.mysure11.Activity.LiveDetailsActivity;
import com.img.mysure11.Activity.JoinContestActivity;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.live_contestGetSet;
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

public class liveContestsAdapter extends RecyclerView.Adapter<liveContestsAdapter.MyViewHolder>{

    Context context;
    ArrayList<live_contestGetSet> list;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String TAG = "myTeams";
    ArrayList<MyTeamsGetSet> selectedteamList;

    public liveContestsAdapter(Context context, ArrayList<live_contestGetSet> list) {
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
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.challenge_list_live, parent, false));
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

        }else {
            holder. numWinners.setText(nf2.format(list.get(i).getWinning_percentage()) + " % Winners");
        }

        holder.joinedWith.setText("Team "+list.get(i).getUserteamnumber());
        holder.pointsGained.setText(""+list.get(i).getUserpoints());
        holder.rank.setText("#"+list.get(i).getUserrank());

        holder.teamName.setText(session.getTeamName());

        holder.entryFee.setText("₹"+list.get(i).getEntryfee());
        if(gv.getStatus().equals("In Progress") || gv.getStatus().equals("Under Review")){
            if(Integer.parseInt(list.get(i).getUserrank()) > list.get(i).getTotalwinners())
                holder.wonAmount.setText("Not in the winning zone");
            else
                holder.wonAmount.setText("In the winning zone");
        } else {
            holder.wonAmount.setText("Winning Amount ₹" + list.get(i).getTotalwinning());
        }
        if(list.get(i).getWinamount()!=0)
            holder.prizeMoney.setText("₹ "+nf2.format(list.get(i).getWinamount())+"");
        else {
            holder.prizeMoney.setText("Net Practice");
            holder.prizeMoney.setTextSize(12);
        }

        holder.winnerLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getChallenge_id());
                Intent ii= new Intent(context, LiveDetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getChallenge_id());
                ii.putExtra("tabPos",0);
                context.startActivity(ii);
            }
        });

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                gv.setChallengeId(list.get(i).getChallenge_id());
                Intent ii= new Intent(context, LiveDetailsActivity.class);
                ii.putExtra("challenge_id",list.get(i).getChallenge_id());
                context.startActivity(ii);
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder{

        LinearLayout ll,winnerLL;
        TextView prizeMoney,numWinners,entryFee,joinedWith,pointsGained,rank,wonAmount,teamName;

        public MyViewHolder(View v){
            super(v);

            ll = v.findViewById(R.id.ll);
            winnerLL = v.findViewById(R.id.winnerLL);

            prizeMoney = v.findViewById(R.id.prizeMoney);
            numWinners = v.findViewById(R.id.numWinners);
            entryFee = v.findViewById(R.id.entryFee);
            joinedWith = v.findViewById(R.id.joinedWith);
            pointsGained = v.findViewById(R.id.pointsGained);
            rank = v.findViewById(R.id.rank);
            wonAmount = v.findViewById(R.id.wonAmount);
            teamName = v.findViewById(R.id.teamName);
        }
    }
}
