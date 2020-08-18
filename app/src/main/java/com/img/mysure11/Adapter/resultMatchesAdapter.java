package com.img.mysure11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.img.mysure11.Activity.ChallengesActivity;
import com.img.mysure11.Activity.LiveChallengesActivity;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

public class resultMatchesAdapter extends RecyclerView.Adapter<resultMatchesAdapter.MyViewHolder>{

    Context context;
    ArrayList<joinedMatchesGetSet> list;
    String type;

    GlobalVariables gv;

    public resultMatchesAdapter(Context context, ArrayList<joinedMatchesGetSet> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;

        gv = (GlobalVariables)context.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.result_matches,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {

        holder.seriesName.setText(list.get(i).getSeries_name());
        holder.matchName.setText(list.get(i).getTeam1display().toUpperCase() + "  vs  "+list.get(i).getTeam2display().toUpperCase());

        holder.totalContestJoined.setText(list.get(i).getJoinedcontest()+" Contests Joined");

        Picasso.with(context).load(list.get(i).getTeam1logo()).into(holder.teamLogo1);
        Picasso.with(context).load(list.get(i).getTeam2logo()).into(holder.teamLogo2);

        if(list.get(i).getStatus().equals("closed")) {

            holder.status.setVisibility(View.VISIBLE);
            holder.matchTime.setVisibility(View.GONE);

            if(list.get(i).getFinal_status().equals("pending"))
                holder.status.setText("In Progress");
            else if(list.get(i).getFinal_status().equals("IsReviewed"))
                holder.status.setText("Under Review");
            else if(list.get(i).getFinal_status().equals("winnerdeclared")) {
                holder.status.setText("Completed");
            }else if(list.get(i).getFinal_status().equals("IsAbandoned")) {
                holder.status.setText("Abandoned");
            }else if(list.get(i).getFinal_status().equals("IsCanceled")) {
                holder.status.setText("Cancelled");
            } else {
                holder.status.setText(list.get(i).getFinal_status());
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    gv.setSportType(type);
                    gv.setMatchKey(list.get(i).getMatchkey());
                    gv.setTeam1(list.get(i).getTeam1display().toUpperCase());
                    gv.setTeam2(list.get(i).getTeam2display().toUpperCase());
                    gv.setMatchTime(list.get(i).getStart_date());
                    gv.setSeries(list.get(i).getSeries_id());
                    gv.setTeam1Image(list.get(i).getTeam1logo());
                    gv.setTeam2image(list.get(i).getTeam2logo());
                    gv.setStatus(holder.status.getText().toString());
                    gv.setSeriesName(list.get(i).getSeries_name());

                    context.startActivity(
                            new Intent(context, LiveChallengesActivity.class)
                    );
                }
            });

        } else {
            holder.status.setVisibility(View.GONE);
            holder.matchTime.setVisibility(View.VISIBLE);

            String sDate = "2017-09-08 10:05:00";
            String eDate = "2017-09-10 12:05:00";
            Date startDate=null,endDate = null;

            Calendar c = Calendar.getInstance();
            c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
            int hour = c.get(Calendar.HOUR_OF_DAY);
            final int minute = c.get(Calendar.MINUTE);
            int sec = c.get(Calendar.SECOND);
            int mYear1 = c.get(Calendar.YEAR);
            int mMonth1 = c.get(Calendar.MONTH);
            int mDay1 = c.get(Calendar.DAY_OF_MONTH);

            sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
            eDate = list.get(i).getStart_date();
            Log.i("matchtime", list.get(i).getStart_date());

            SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                startDate = dateFormat.parse(sDate);
                endDate = dateFormat.parse(eDate);
            } catch (ParseException e) {
                e.printStackTrace();
            }

            final long diffInMs = endDate.getTime() - startDate.getTime();

            CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

                public void onTick(long millisUntilFinished) {
                    holder.matchTime.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + " : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + " : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " left"));
                }

                public void onFinish()
                {
                    holder.matchTime.setText("Time Over");
                }
            };
            cT.start();

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    gv.setSportType(type);
                    gv.setMatchKey(list.get(i).getMatchkey());
                    gv.setTeam1(list.get(i).getTeam1display().toUpperCase());
                    gv.setTeam2(list.get(i).getTeam2display().toUpperCase());
                    gv.setMatchTime(list.get(i).getStart_date());
                    gv.setSeries(list.get(i).getSeries_id());
                    gv.setTeam1Image(list.get(i).getTeam1logo());
                    gv.setTeam2image(list.get(i).getTeam2logo());
                    gv.setSeriesName(list.get(i).getSeries_name());

                    Intent ii = new Intent(context, ChallengesActivity.class);
                    ii.putExtra("tabPos",1);
                    context.startActivity(ii);
                }
            });

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView seriesName,matchName,status,matchTime,totalContestJoined;
        CircleImageView teamLogo1,teamLogo2;

        public MyViewHolder(View v){
            super (v);

            seriesName = v.findViewById(R.id.seriesName);
            matchName = v.findViewById(R.id.matchName);
            status = v.findViewById(R.id.status);
            matchTime = v.findViewById(R.id.matchTime);
            totalContestJoined = v.findViewById(R.id.totalContestJoined);
            teamLogo1 = v.findViewById(R.id.teamLogo1);
            teamLogo2 = v.findViewById(R.id.teamLogo2);
        }
    }
}
