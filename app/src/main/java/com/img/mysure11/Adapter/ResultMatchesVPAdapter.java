package com.img.mysure11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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

import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;
import de.hdodenhof.circleimageview.CircleImageView;

public class ResultMatchesVPAdapter extends PagerAdapter {

    Context context;
    ArrayList<joinedMatchesGetSet> list;
    GlobalVariables gv;
    String type;

    public ResultMatchesVPAdapter(Context context, ArrayList<joinedMatchesGetSet> list, String type) {
        this.context = context;
        this.list = list;
        this.type = type;

        gv = (GlobalVariables)context.getApplicationContext();
    }

    @Override
    public int getCount() {
        if(list.size() < 5)
            return list.size();
        else
            return 5;
    }

    @Override
    public boolean isViewFromObject(View view, Object object) {

        return view == ((LinearLayout) object);
    }

    @Override
    public Object instantiateItem(ViewGroup container, final int i) {

        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View v = inflater.inflate(R.layout.result_matches, null);;

        final TextView seriesName,matchName,status,matchTime,totalContestJoined;
        CircleImageView teamLogo1,teamLogo2;

        seriesName = v.findViewById(R.id.seriesName);
        matchName = v.findViewById(R.id.matchName);
        status = v.findViewById(R.id.status);
        matchTime = v.findViewById(R.id.matchTime);
        totalContestJoined = v.findViewById(R.id.totalContestJoined);
        teamLogo1 = v.findViewById(R.id.teamLogo1);
        teamLogo2 = v.findViewById(R.id.teamLogo2);

        seriesName.setText(list.get(i).getSeries_name());
        matchName.setText(list.get(i).getTeam1display().toUpperCase() + "  vs  "+list.get(i).getTeam2display().toUpperCase());

        totalContestJoined.setText(list.get(i).getJoinedcontest()+" Contests Joined");

        Picasso.with(context).load(list.get(i).getTeam1logo()).into(teamLogo1);
        Picasso.with(context).load(list.get(i).getTeam2logo()).into(teamLogo2);

        if(list.get(i).getStatus().equals("closed")) {

            status.setVisibility(View.VISIBLE);
            matchTime.setVisibility(View.GONE);

            if(list.get(i).getFinal_status().equals("pending"))
                status.setText("In Progress");
            else if(list.get(i).getFinal_status().equals("IsReviewed"))
                status.setText("Under Review");
            else if(list.get(i).getFinal_status().equals("winnerdeclared")) {
                status.setText("Completed");
            }else if(list.get(i).getFinal_status().equals("IsAbandoned")) {
                status.setText("Abandoned");
            }else if(list.get(i).getFinal_status().equals("IsCanceled")) {
                status.setText("Cancelled");
            } else {
                status.setText(list.get(i).getFinal_status());
            }

            v.setOnClickListener(new View.OnClickListener() {
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
                    gv.setStatus(status.getText().toString());
                    gv.setSeriesName(list.get(i).getSeries_name());

                    context.startActivity(
                            new Intent(context, LiveChallengesActivity.class)
                    );
                }
            });

        } else {
            status.setVisibility(View.GONE);
            matchTime.setVisibility(View.VISIBLE);

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
                    matchTime.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + " : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + " : "
                            + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + " left"));
                }

                public void onFinish()
                {
                    matchTime.setText("Time Over");
                }
            };
            cT.start();

            v.setOnClickListener(new View.OnClickListener() {
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
        container.addView(v);
        return v;
    }

    @Override
    public void destroyItem(ViewGroup container, int position, Object object) {
        // Remove viewpager_item.xml from ViewPager
        ((ViewPager) container).removeView((LinearLayout) object);
    }
}
