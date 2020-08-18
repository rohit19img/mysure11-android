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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Activity.PlayerStatsActivity;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.PlayerListGetSet;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class PlayerListFootballAdapter extends BaseAdapter {

    Context context;
    ArrayList<PlayerListGetSet>list,list1;
    TextView leftPlayers,team1_size,team2_size,leftAmount;
    Button btnContinue;
    GlobalVariables gv;
    ArrayList<captainListGetSet> Clist;
    boolean edit;
    TabLayout playersTab;

    public PlayerListFootballAdapter(Context context, ArrayList<PlayerListGetSet>list, ArrayList<PlayerListGetSet>list1, boolean edit){
        this.context=context;
        this.list=list;
        this.list1=list1;
        this.edit=edit;
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
    public View getView(final int i, View view, ViewGroup viewGroup) {

        final View v;
        final TextView name,role,point,credit,enabledBG,teamName;
        CircleImageView img,teamImage;
        ImageView select;
        final double[] left = {0.0};
        LinearLayout counterll,deselectedLL,selectedLL;

        if(view==null){
            LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v= inflater.inflate(R.layout.player_list,null);
        }
        else
            v= (View)view;

        gv= (GlobalVariables)context.getApplicationContext();

        select= (ImageView) v.findViewById(R.id.select);

        enabledBG= (TextView)v.findViewById(R.id.enabledBG);
        name= (TextView)v.findViewById(R.id.playerName);
        point= (TextView)v.findViewById(R.id.point);
        credit= (TextView)v.findViewById(R.id.credit);
        role= (TextView)v.findViewById(R.id.role);
        teamName = (TextView)v.findViewById(R.id.teamName);
        img=(CircleImageView) v.findViewById(R.id.img);
        teamImage=(CircleImageView) v.findViewById(R.id.teamImage);

        name.setText(list.get(i).getName());
        point.setText(list.get(i).getTotalpoints());
        credit.setText(list.get(i).getCredit());
        role.setText("Sel by "+list.get(i).getPlayer_selection_percentage()+"%");

        if(list.get(i).getTeam().equals("team1")) {
            Picasso.with(context).load(gv.getTeam1Image()).placeholder(R.drawable.football_default).into(teamImage);
            teamName.setText(gv.getTeam1().toUpperCase());
        } else {
            Picasso.with(context).load(gv.getTeam2image()).placeholder(R.drawable.football_default).into(teamImage);
            teamName.setText(gv.getTeam2().toUpperCase());
        }


        leftPlayers=(TextView) ((Activity)context).findViewById(R.id.leftPlayers);
        leftAmount=(TextView) ((Activity)context).findViewById(R.id.leftAmount);
        team1_size=(TextView) ((Activity)context).findViewById(R.id.team1);
        team2_size=(TextView) ((Activity)context).findViewById(R.id.team2);
        btnContinue=(Button) ((Activity)context).findViewById(R.id.btnContinue);
        counterll=(LinearLayout) ((Activity)context).findViewById(R.id.counterll);
        playersTab=(TabLayout)((Activity)context).findViewById(R.id.playersTab);

        deselectedLL =(LinearLayout)v.findViewById(R.id.deselectedLL);
        selectedLL =(LinearLayout)v.findViewById(R.id.selectedLL);

        if(list.get(i).getPlayingstatus().equals("-1")){
            deselectedLL.setVisibility(View.GONE);
            selectedLL.setVisibility(View.GONE);
        }else if(list.get(i).getPlayingstatus().equals("1")){
            deselectedLL.setVisibility(View.GONE);
            selectedLL.setVisibility(View.VISIBLE);
        }else if(list.get(i).getPlayingstatus().equals("0")){
            deselectedLL.setVisibility(View.VISIBLE);
            selectedLL.setVisibility(View.GONE);
        }

        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent ii= new Intent(context, PlayerStatsActivity.class);
                ii.putExtra("key",list.get(i).getId());
                ii.putExtra("PlayerName",list.get(i).getName());
                context.startActivity(ii);
            }
        });

        if(!list.get(i).getImage().equals("")){
            Picasso.with(context).load(list.get(i).getImage()).into(img);
        }

        if(edit){
            Clist= new ArrayList<>();
            Clist= gv.getCaptainList();

            for(captainListGetSet zz:Clist){
                int id=0;
                for(int j=0; j<list1.size(); j++){
                    if(list1.get(j).getId().equals(zz.getId()))
                        id=j;
                }
                list1.get(id).setSelected(true);
            }

            int team1=0,team2=0;
            for(PlayerListGetSet zz:list1){
                if(zz.isSelected()){

                    if(zz.getTeam().equals("team1")){
                        team1++;
                    }
                    if(zz.getTeam().equals("team2")){
                        team2++;
                    }
                }
            }

            team1_size.setText(team1+"");
            team2_size.setText(team2+"");

            boolean d= false;
            float cr= 0.0f;

            for(PlayerListGetSet zz: list1){
                if(zz.isSelected()){

                    cr= cr+ Float.parseFloat(zz.getCredit());

                    d= true;
                    left[0] = left[0] +Double.parseDouble(list.get(i).getCredit());
                    leftAmount.setText(String.format("%.1f", (100.0f-cr)));
                }

                if(!d) {
                    leftAmount.setText("100");
                }
            }

            Clist= new ArrayList<>();
            gv.setCaptainList(Clist);
        }

        if(list.get(i).isSelected()) {
            list.get(i).setEnabled(true);
            v.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#fff7ec")));
            select.setImageDrawable(context.getDrawable(R.drawable.ic_plus_circle));
        }else{
            list.get(i).setEnabled(false);
            v.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#ffffff")));
            select.setImageDrawable(context.getDrawable(R.drawable.ic_minus_circle));
        }

        int count=0;
        int countGK1 =0,countDEF1=0,countMF1=0,countFOW1=0;
        int team1=0,team2=0;
        float cr = 100.0f;

        for(PlayerListGetSet zz:list1){
            if(zz.isSelected()){
                count++;
                cr= cr - Float.parseFloat(zz.getCredit());
                if(zz.getTeam().equals("team1")){
                    team1++;
                }
                if(zz.getTeam().equals("team2")){
                    team2++;
                }
                if(zz.getRole().equals("goalkeeper"))
                    countGK1++;
                if(zz.getRole().equals("defender"))
                    countDEF1++;
                if(zz.getRole().equals("midfielder"))
                    countMF1++;
                if(zz.getRole().equals("striker"))
                    countFOW1++;
            }
        }

        for(PlayerListGetSet xx:list1){
            if(xx.isSelected() == false)
                xx.setEnabled(true);
        }

        if(countGK1 == 1){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("goalkeeper") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }else{
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("goalkeeper") && xx.isSelected() == false)
                    xx.setEnabled(true);
            }
        }

        if(countDEF1 == 5){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }
        if(countDEF1 == 4){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(true);
            }
        }

        if(countMF1 == 5){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }
        if(countMF1 == 4){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(true);
            }
        }

        if(countFOW1 == 3){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }
        if(countFOW1 == 2 && (countMF1 == 5 || countDEF1 == 5)){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 5 && countMF1 ==4){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 4 && countMF1 == 5){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 5 && countMF1 ==4 && countFOW1 ==1){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 4 && countMF1 == 5 && countFOW1 ==1){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 4 && countMF1 == 4 && countFOW1 ==2 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 5 && countMF1 == 3 && countFOW1 ==2 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 3 && countMF1 == 5 && countFOW1 ==2 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 4 && countMF1 == 3 && countFOW1 == 3 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 3 && countMF1 == 4 && countFOW1 == 3 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 4 && countFOW1 == 3 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countDEF1 == 5 && countFOW1 == 2 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countMF1 == 4 && countFOW1 == 3 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(countMF1 == 5 && countFOW1 == 2 ){
            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }

            for(PlayerListGetSet xx:list1){
                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                    xx.setEnabled(false);
            }
        }

        if(team1 ==7){
            for(PlayerListGetSet xx:list1){
                if(xx.getTeam().equals("team1") && xx.isSelected() == false) {
                    xx.setEnabled(false);
                }
            }
        }

        if(team2 ==7){
            for(PlayerListGetSet xx:list1){
                if(xx.getTeam().equals("team2") && xx.isSelected() == false) {
                    xx.setEnabled(false);
                }
            }
        }

        for(PlayerListGetSet xx:list1){
            if(Float.parseFloat(xx.getCredit()) > cr){
                xx.setEnabled(false);
            }
        }

        if(count ==11){
            for(PlayerListGetSet xx:list1){
                if(xx.isSelected() == false)
                    xx.setEnabled(false);
                else
                    xx.setEnabled(true);
            }
        }

        playersTab.getTabAt(0).setText("GK (" + countGK1 + ")");
        playersTab.getTabAt(1).setText("DEF (" + countDEF1 + ")");
        playersTab.getTabAt(2).setText("MID (" + countMF1 + ")");
        playersTab.getTabAt(3).setText("ST (" + countFOW1 + ")");

        leftPlayers.setText(String.valueOf(count));

        if(!list.get(i).isEnabled()) {
            v.setEnabled(false);
            enabledBG.setVisibility(View.VISIBLE);
        }else {
            v.setEnabled(true);
            enabledBG.setVisibility(View.GONE);
        }

        int left_count = 11-count;
        counterll.removeAllViews();
        for (int k = 0; k < count; k++) {
            RelativeLayout ll = new RelativeLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(pxFromDp(2f, context), pxFromDp(2f, context), pxFromDp(2f, context), pxFromDp(2f, context));
            ll.setLayoutParams(lp);
            ImageView im = new ImageView(context);
            im.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_deselected));
            ll.addView(im);

//            TextView tv = new TextView(context);
//            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            lp1.gravity = Gravity.CENTER;
//            tv.setLayoutParams(lp1);
//            tv.setText(String.valueOf(count));
//            tv.setTextSize(9);
//            tv.setGravity(Gravity.CENTER);
//            tv.setTextColor(context.getResources().getColor(R.color.white));
//            if (k == count - 1)
//                ll.addView(tv);

            counterll.addView(ll);
        }

        for (int k = 0; k < left_count; k++) {
            RelativeLayout ll = new RelativeLayout(context);
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            lp.setMargins(pxFromDp(2f, context), pxFromDp(2f, context), pxFromDp(2f, context), pxFromDp(2f, context));
            ll.setLayoutParams(lp);
            ImageView im = new ImageView(context);
            im.setImageDrawable(context.getResources().getDrawable(R.drawable.ic_star_selected));
            ll.addView(im);

//            TextView tv = new TextView(context);
//            LinearLayout.LayoutParams lp1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
//            lp1.gravity = Gravity.CENTER;
//            tv.setLayoutParams(lp1);
//            tv.setText(String.valueOf(11));
//            tv.setTextSize(9);
//            tv.setGravity(Gravity.CENTER);
//            tv.setTextColor(context.getResources().getColor(R.color.text_color));
//            if (k == left_count - 1)
//                ll.addView(tv);

            counterll.addView(ll);
        }

        if(count == 11) {
            btnContinue.setEnabled(true);
            btnContinue.setTextColor(context.getResources().getColor(R.color.white));
            btnContinue.setBackgroundDrawable(context.getDrawable(R.drawable.btn_primary));
        }else if(count == 10) {
            btnContinue.setEnabled(false);
            btnContinue.setTextColor(context.getResources().getColor(R.color.font_color));
            btnContinue.setBackgroundDrawable(context.getDrawable(R.drawable.btn_gray));
        }

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                boolean b = false;

                if(list.get(i).isSelected()){    //deselect player
                    list.get(i).setSelected(false);
                    b=true;

                }else {  //select player
                    list.get(i).setSelected(true);

                    int countGK=0,countDEF=0,countMF=0,countFOW=0;
                    int countGK1=0,countDEF1=0,countMF1=0,countFOW1=0;
                    int team1=0,team2=0;

                    for(PlayerListGetSet zz:list){
                        if(zz.isSelected()){
                            if(zz.getRole().equals("goalkeeper"))
                                countGK++;
                            if(zz.getRole().equals("defender"))
                                countDEF++;
                            if(zz.getRole().equals("midfielder"))
                                countMF++;
                            if(zz.getRole().equals("striker"))
                                countFOW++;
                        }
                    }

                    for(PlayerListGetSet zz:list1){
                        if(zz.isSelected()){
                            if(zz.getRole().equals("goalkeeper"))
                                countGK1++;
                            if(zz.getRole().equals("defender"))
                                countDEF1++;
                            if(zz.getRole().equals("midfielder"))
                                countMF1++;
                            if(zz.getRole().equals("striker"))
                                countFOW1++;
                        }
                    }

                    int c1=0;
                    for(PlayerListGetSet zz:list1){
                        if(zz.isSelected()){
                            c1++;
                            if(zz.getTeam().equals("team1"))
                                team1++;
                            if(zz.getTeam().equals("team2"))
                                team2++;
                        }
                    }

                    float cr= 0.0f;

                    for(PlayerListGetSet zz: list1){
                        if(zz.isSelected())
                            cr= cr+ Float.parseFloat(zz.getCredit());
                    }

                    if(c1>11){
                        list.get(i).setSelected(false);
                        new AppUtils().showError(context,"You can select maximum 11 players in your team");
                    }
                    else if(countGK >1  || countDEF >5 || countMF >5 || countFOW >3 ){
                        list.get(i).setSelected(false);
                        if(countGK >1){
                            new AppUtils().showError(context,"Choose exact 1 goal-keeper");
                        }
                        else if(countDEF >5){
                            new AppUtils().showError(context,"You can choose maximum 5 defenders");
                        }
                        if(countMF >5){
                            new AppUtils().showError(context,"You can choose maximum 5 mid-fielders");
                        }
                        if(countFOW >3){
                            new AppUtils().showError(context,"You can choose maximum 3 forwards");
                        }
                        b=false;
                    }
                    else if(team1>7 || team2 >7){
                        list.get(i).setSelected(false);
                        new AppUtils().showError(context,"You can select maximum of 7 players from a team");
                        b=false;
                    }
                    else if(c1>10  &&(countMF1<3  || countDEF1<3  || countGK1==0 || countFOW1==0)){
                        if(countMF1<3){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 3 mid-fielders");
                            b=false;
                        }
                        else if(countDEF1<3){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 3 defenders");
                            b=false;
                        }
                        else if(countGK1==0){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 1 goal-keeper");
                            b=false;
                        }
                        else if(countFOW1<1){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 1 forward");
                            b=false;
                        }
                        else {
                            b = true;
                        }
                    }
                    else if(c1>9  && ((countGK1 ==0)  && (countFOW1 ==0) )){
                        if(countGK1==0){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 1 goal-keeper");
                            b=false;
                        }
                        else{
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 1 forward");
                            b=false;
                        }
                    }
                    else if(c1>7 && ( (countDEF1==5 && countFOW1>2) || (countMF1==5 && countFOW1>2) )){
                        if(countDEF1==5 && countFOW1>2){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 3 mid-fielders");
                            b=false;
                        }
                        if(countMF1==5 && countFOW1>2){
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Choose atleast 3 defenders");
                            b=false;
                        }
                    }
                    else{
                        if(cr > 100.0f){
                            // Log.i("Credit after selection ",String.valueOf(cr));
                            list.get(i).setSelected(false);
                            new AppUtils().showError(context,"Not enough credit");
                            b=false;
                        }else{
                            b=true;
                        }
                    }
                }

                /*if(b)*/{
                    int countGK1 =0,countDEF1=0,countMF1=0,countFOW1=0;
                    int team1=0,team2=0;
                    int c1=0;
                    float cr = 100.0f;

                    for(PlayerListGetSet zz:list){
                        if(zz.isSelected())
                            c1++;
                    }

                    for(PlayerListGetSet zz:list1){
                        if(zz.isSelected()){
                            cr= cr - Float.parseFloat(zz.getCredit());

                            if(zz.getTeam().equals("team1"))
                                team1++;
                            if(zz.getTeam().equals("team2"))
                                team2++;


                            if(zz.getRole().equals("goalkeeper"))
                                countGK1++;
                            if(zz.getRole().equals("defender"))
                                countDEF1++;
                            if(zz.getRole().equals("midfielder"))
                                countMF1++;
                            if(zz.getRole().equals("striker"))
                                countFOW1++;
                        }
                    }

                    team1_size.setText(team1+"");
                    team2_size.setText(team2+"");

                    {
                        for(PlayerListGetSet xx:list1){
                            if(xx.isSelected() == false)
                                xx.setEnabled(true);
                        }
                        if(team1 ==7){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getTeam().equals("team1") && xx.isSelected() == false) {
                                    xx.setEnabled(false);
                                }
                            }
                        }
                        if(team1 ==6){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getTeam().equals("team1") && xx.isSelected() == false)
                                    xx.setEnabled(true);
                            }
                        }

                        if(team2 ==7){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getTeam().equals("team2") && xx.isSelected() == false) {
                                    xx.setEnabled(false);
                                }
                            }
                        }
                        if(team2 ==6){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getTeam().equals("team2") && xx.isSelected() == false)
                                    xx.setEnabled(true);
                            }
                        }

                        if(countGK1 == 1){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("goalkeeper") && xx.isSelected() == false)
                                    xx.setEnabled(false);
                            }
                        }else{
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("goalkeeper") && xx.isSelected() == false)
                                    xx.setEnabled(true);
                            }
                        }

                        if(countDEF1 == 5){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                                    xx.setEnabled(false);
                            }
                        }
                        if(countDEF1 == 4){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("defender") && xx.isSelected() == false)
                                    xx.setEnabled(true);
                            }
                        }

                        if(countMF1 == 5){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                                    xx.setEnabled(false);
                            }
                        }
                        if(countMF1 == 4){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("midfielder") && xx.isSelected() == false)
                                    xx.setEnabled(true);
                            }
                        }

                        if(countFOW1 == 3){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                                    xx.setEnabled(false);
                            }
                        }
                        if(countFOW1 == 2 && (countMF1 == 5 || countDEF1 == 5)){
                            for(PlayerListGetSet xx:list1){
                                if(xx.getRole().equals("striker") && xx.isSelected() == false)
                                    xx.setEnabled(false);
                            }
                        }

                        for(PlayerListGetSet xx:list1){
                            if(Float.parseFloat(xx.getCredit()) > cr){
                                xx.setEnabled(false);
                            }
                        }

                        if(c1 ==11){
                            for(PlayerListGetSet xx:list1){
                                if(xx.isSelected() == false)
                                    xx.setEnabled(false);
                                else
                                    xx.setEnabled(true);
                            }
                        }
                    }

                }

                notifyDataSetChanged();
                boolean d= false;
                float cr= 0.0f;

                for(PlayerListGetSet zz: list1){
                    if(zz.isSelected()){

                        cr= cr+ Float.parseFloat(zz.getCredit());

                        d= true;
                        left[0] = left[0] +Double.parseDouble(list.get(i).getCredit());
                        leftAmount.setText(String.format("%.2f", (100.0f-cr)));
                        // Log.i("Credit is",String.format("%.2f", left[0])+" cr");
                    }

                    if(!d)
                        leftAmount.setText("100");
                }

            }
        });

        return v;
    }

    public static int pxFromDp(final float dp,final Context context) {
        return (int) (dp * context.getResources().getDisplayMetrics().density);
    }
}
