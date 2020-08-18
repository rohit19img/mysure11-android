package com.img.mysure11.Adapter;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mysure11.Activity.LeaderboardByUserActivity;
import com.img.mysure11.Activity.TeamPreviewActivity;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.SelectedPlayersGetSet;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.GetSet.mainLeaderboardGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainLeaderBoardAdapter extends RecyclerView.Adapter<MainLeaderBoardAdapter.MyViewHolder> {

    Context context;
    ArrayList<mainLeaderboardGetSet> list;
    String series_id;

    UserSessionManager session;
    GlobalVariables gv;
    Dialog progressDialog;

    public MainLeaderBoardAdapter(Context context, ArrayList<mainLeaderboardGetSet> list,String series_id){
        this.context = context;
        this.list = list;
        this.series_id = series_id;

        session = new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();
        progressDialog = new AppUtils().getProgressDialog(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView teamName,rank,points,winAmount;
        ImageView indicatorIcon;
        CircleImageView userImage;

        public MyViewHolder(View v) {
            super(v);
            userImage=(CircleImageView)v.findViewById(R.id.userImage);
            teamName= (TextView)v.findViewById(R.id.teamName);
            rank= (TextView)v.findViewById(R.id.rank);
            winAmount = v.findViewById(R.id.winAmount);
            points = v.findViewById(R.id.points);
            indicatorIcon = v.findViewById(R.id.indicatorIcon);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.main_leaderboard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {
        if(!list.get(i).getImage().equals(""))
            Picasso.with(context).load(list.get(i).getImage()).resize(100,100).into(holder.userImage);

        holder.teamName.setText(list.get(i).getTeam());
        holder.rank.setText("#"+list.get(i).getRank());
        holder.points.setText(list.get(i).getPoints()+" Points");

        if(list.get(i).getArrowname().equals("up-arrow"))
            holder.indicatorIcon.setImageResource(R.drawable.ic_up);
        else if(list.get(i).getArrowname().equals("equal-arrow"))
            holder.indicatorIcon.setImageResource(R.drawable.ic_up_down1);
        else
            holder.indicatorIcon.setImageResource(R.drawable.ic_down);

        if(session.getTeamName().equalsIgnoreCase(list.get(i).getTeam())){
            holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fdf1f1")));
        }else{
            holder.itemView.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(
                        new Intent(context, LeaderboardByUserActivity.class)
                        .putExtra("userid",list.get(i).getUserid())
                        .putExtra("seriesid",series_id)
                );
            }
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
