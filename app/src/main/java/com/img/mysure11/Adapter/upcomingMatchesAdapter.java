package com.img.mysure11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.img.mysure11.Activity.ChallengesActivity;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.upcomingMatchesGetSet;
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

public class upcomingMatchesAdapter extends RecyclerView.Adapter<upcomingMatchesAdapter.MyViewHolder>{

    Context context;
    ArrayList<upcomingMatchesGetSet> list;
    String type;

    GlobalVariables gv;

    public upcomingMatchesAdapter(Context context, ArrayList<upcomingMatchesGetSet> list,String type) {
        this.context = context;
        this.list = list;
        this.type = type;

        gv = (GlobalVariables)context.getApplicationContext();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.upcoming_matches,parent,false));
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, final int i) {

        holder.seriesName.setText(list.get(i).getSeriesname());
        holder.team1Name.setText(list.get(i).getTeam1name());
        holder.team2Name.setText(list.get(i).getTeam2name());

        if(list.get(i).getPlaying11_status().equals("1")){
            holder.playing11.setVisibility(View.VISIBLE);
        } else{
            holder.playing11.setVisibility(View.GONE);
        }

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
        eDate = list.get(i).getTime_start();
        Log.i("matchtime", list.get(i).getTime_start());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long diffInMs = endDate.getTime() - startDate.getTime();

        final long hours1 = 1 * 60 * 60 * 1000;
        final long hours4 = 4 * 60 * 60 * 1000;
        final long hours48 = 48 * 60 * 60 * 1000;


        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {

            public void onTick(long millisUntilFinished) {

                holder.matchTime.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + " : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + " : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + ""));


            }

            public void onFinish()
            {
                holder.matchTime.setText("Time Over");
            }
        };
        cT.start();

        Picasso.with(context).load(list.get(i).getTeam1logo()).into(holder.teamLogo1);
        Picasso.with(context).load(list.get(i).getTeam2logo()).into(holder.teamLogo2);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(list.get(i).getLaunch_status().equals("launched")) {
                    gv.setSportType(type);
                    gv.setMatchKey(list.get(i).getMatchkey());
                    gv.setTeam1(list.get(i).getTeam1name().toUpperCase());
                    gv.setTeam2(list.get(i).getTeam2name().toUpperCase());
                    gv.setMatchTime(list.get(i).getTime_start());
                    gv.setSeries(list.get(i).getSeries());
                    gv.setTeam1Image(list.get(i).getTeam1logo());
                    gv.setTeam2image(list.get(i).getTeam2logo());
                    gv.setPlaying11_status(list.get(i).getPlaying11_status());

                    Intent ii = new Intent(context, ChallengesActivity.class);
                    context.startActivity(ii);
                } else
                    new AppUtils().showError(context,"To be launched soon");
            }
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    class MyViewHolder extends RecyclerView.ViewHolder {

        TextView seriesName,team1Name,matchTime,team2Name;
        CircleImageView teamLogo1,teamLogo2;
        ImageView playing11;

        public MyViewHolder(View v){
            super (v);

            seriesName = v.findViewById(R.id.seriesName);
            team1Name = v.findViewById(R.id.team1Name);
            team2Name = v.findViewById(R.id.team2Name);
            matchTime = v.findViewById(R.id.matchTime);
            teamLogo1 = v.findViewById(R.id.teamLogo1);
            teamLogo2 = v.findViewById(R.id.teamLogo2);
            playing11 = v.findViewById(R.id.playing11);
        }
    }
}
