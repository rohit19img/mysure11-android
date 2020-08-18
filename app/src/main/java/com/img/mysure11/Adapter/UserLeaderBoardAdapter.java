package com.img.mysure11.Adapter;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mysure11.Activity.LeaderboardByUserActivity;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.LeaderboardUserGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class UserLeaderBoardAdapter extends RecyclerView.Adapter<UserLeaderBoardAdapter.MyViewHolder> {

    Context context;
    ArrayList<LeaderboardUserGetSet> list;

    UserSessionManager session;
    GlobalVariables gv;
    Dialog progressDialog;

    public UserLeaderBoardAdapter(Context context, ArrayList<LeaderboardUserGetSet> list){
        this.context = context;
        this.list = list;

        session = new UserSessionManager(context);
        gv= (GlobalVariables)context.getApplicationContext();
        progressDialog = new AppUtils().getProgressDialog(context);
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        TextView matchName,points;

        public MyViewHolder(View v) {
            super(v);
            points = v.findViewById(R.id.points);
            matchName = v.findViewById(R.id.indicatorIcon);
        }
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.user_leaderboard, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int i) {

        holder.matchName.setText(list.get(i).getShort_name());
        holder.points.setText(list.get(i).getPoints()+" Points");
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

}
