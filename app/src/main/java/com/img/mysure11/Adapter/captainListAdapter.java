package com.img.mysure11.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.img.mysure11.Activity.CaptainViceCaptainActivity;
import com.img.mysure11.Activity.PlayerStatsActivity;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class captainListAdapter extends BaseAdapter{

    Context context;
    ArrayList<captainListGetSet> list;
    Button btnContinue;
    GlobalVariables gv;

    public captainListAdapter(Context context, ArrayList<captainListGetSet> list){
        this.context= context;
        this.list=list;

        gv = (GlobalVariables)context.getApplicationContext();
    }

    @Override
    public int getCount() {
        return list.size();
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
    public View getView(final int i, View view, final ViewGroup viewGroup) {

        View v;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.captain_list,null);

        final String[] captain = {""};
        final String vicecaptain[] = {""};
        final TextView c,vc,playerName,points;
        CircleImageView img,teamImage;
        RelativeLayout rl;

        c= (TextView)v.findViewById(R.id.captain);
        vc= (TextView)v.findViewById(R.id.vicecaptain);
        img=(CircleImageView) v.findViewById(R.id.img);
        teamImage=(CircleImageView) v.findViewById(R.id.teamImage);

        playerName=(TextView)v.findViewById(R.id.playerName);
        points=(TextView)v.findViewById(R.id.points);
        rl = (RelativeLayout)v.findViewById(R.id.rl);

        btnContinue=(Button)((Activity)context).findViewById(R.id.btnContinue);

        if(!list.get(i).getId().equals("0")) {
            playerName.setText(list.get(i).getName());
            points.setText(list.get(i).getPoints() + " pts");

            if (!list.get(i).getImage().equals(""))
                Picasso.with(context).load(list.get(i).getImage()).placeholder(R.drawable.football_default).into(img);

            if(list.get(i).getTeam().equals("team1"))
                Picasso.with(context).load(gv.getTeam1Image()).placeholder(R.drawable.football_default).into(teamImage);
            else
                Picasso.with(context).load(gv.getTeam2image()).placeholder(R.drawable.football_default).into(teamImage);

            img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent ii = new Intent(context, PlayerStatsActivity.class);
                    ii.putExtra("key", list.get(i).getId());
                    ii.putExtra("PlayerName", list.get(i).getName());
                    context.startActivity(ii);
                }
            });
        }else{
            playerName.setVisibility(View.GONE);
            points.setVisibility(View.GONE);
            img.setVisibility(View.GONE);
            teamImage.setVisibility(View.GONE);
            c.setVisibility(View.GONE);
            vc.setVisibility(View.GONE);

            rl.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT,15));
            v.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f0f0f0")));
        }

        if(list.get(i).getVc().equals("Y")){
            vc.setText("1.5x");
            vc.setBackgroundResource(R.drawable.captain_selected);
            vc.setTextColor(context.getResources().getColor(R.color.white));
            c.setBackgroundResource(R.drawable.captain_deselected);
            c.setTextColor(context.getResources().getColor(R.color.font_color));
        }

        if(list.get(i).getCaptain().equals("Y")){
            c.setText("2x");
            c.setBackgroundResource(R.drawable.captain_selected);
            c.setTextColor(context.getResources().getColor(R.color.white));
            vc.setBackgroundResource(R.drawable.captain_deselected);
            vc.setTextColor(context.getResources().getColor(R.color.font_color));
        }

        for(captainListGetSet zz:list){
            if(zz.getCaptain().equals("Y"))
                captain[0] = zz.getId();
            else if(zz.getVc().equals("Y"))
                vicecaptain[0] = zz.getId();
        }

        c.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int ii=0; ii<list.size(); ii++){
                    if(i != ii){
                        list.get(ii).setCaptain("N");
                    }
                    else{
                        list.get(ii).setCaptain("Y");
                    }
                }
                notifyDataSetChanged();
            }
        });

        vc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                for(int ii=0; ii<list.size(); ii++){
                    if(i != ii){
                        list.get(ii).setVc("N");
                    }
                    else{
                        list.get(ii).setVc("Y");
                    }
                }
                notifyDataSetChanged();
            }
        });

        if(!captain[0].equals("") && !vicecaptain[0].equals("") && !captain[0].equals(vicecaptain[0])) {
            btnContinue.setEnabled(true);
            btnContinue.setTextColor(context.getResources().getColor(R.color.white));
            btnContinue.setBackgroundDrawable(context.getDrawable(R.drawable.btn_primary));
        }else {
            btnContinue.setEnabled(false);
            btnContinue.setTextColor(context.getResources().getColor(R.color.font_color));
            btnContinue.setBackgroundDrawable(context.getDrawable(R.drawable.btn_gray));
        }

        Log.i("captain",captain[0]);
        Log.i("vicecaptain",vicecaptain[0]);

        return v;
    }
}
