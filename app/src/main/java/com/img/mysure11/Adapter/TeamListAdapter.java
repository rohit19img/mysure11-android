package com.img.mysure11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.img.mysure11.Activity.CreateTeamActivity;
import com.img.mysure11.Activity.CreateTeamFootballActivity;
import com.img.mysure11.Activity.TeamPreviewActivity;
import com.img.mysure11.Activity.TeamPreviewFootballActivity;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.PlayerListGetSet;
import com.img.mysure11.GetSet.SelectedPlayersGetSet;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;


public class TeamListAdapter extends BaseAdapter{

    Context context;
    GlobalVariables gv;
    ArrayList<MyTeamsGetSet> list;
    ArrayList<SelectedPlayersGetSet> playerList;
    ArrayList<captainListGetSet> captainList;
    UserSessionManager session;

    public TeamListAdapter(Context context, ArrayList<MyTeamsGetSet> list){
        this.context= context;
        this.list=list;

        session = new UserSessionManager(context);
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
    public View getView(final int i, View view, ViewGroup viewGroup) {
        View v;

        TextView teamName,team1_name,team1,team2_name,team2,captainName,vicecaptainName,countWK,countBAT,countALL,countBOWL;
        ImageView captain,vicecaptain;
        LinearLayout editLL,cloneLL;
        ImageView status1,status;

        TextView keeperText,batText,allText,bowlText;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.team_list,null);

        gv= (GlobalVariables)context.getApplicationContext();

        teamName=(TextView)v.findViewById(R.id.teamName);
        captainName=(TextView)v.findViewById(R.id.captainName);
        vicecaptainName=(TextView)v.findViewById(R.id.vicecaptainName);
        team1 =(TextView)v.findViewById(R.id.team1);
        team1_name =(TextView)v.findViewById(R.id.team1_name);
        team2 =(TextView)v.findViewById(R.id.team2);
        team2_name =(TextView)v.findViewById(R.id.team2_name);

        countWK=(TextView)v.findViewById(R.id.countWK);
        countBAT=(TextView)v.findViewById(R.id.countBAT);
        countALL=(TextView)v.findViewById(R.id.countALL);
        countBOWL=(TextView)v.findViewById(R.id.countBOWL);

        status =(ImageView)v.findViewById(R.id.status);
        status1 =(ImageView)v.findViewById(R.id.status1);
        captain =(ImageView)v.findViewById(R.id.captain);
        vicecaptain =(ImageView)v.findViewById(R.id.vicecaptain);

        keeperText = v.findViewById(R.id.keeperText);
        batText = v.findViewById(R.id.batText);
        allText = v.findViewById(R.id.allText);
        bowlText = v.findViewById(R.id.bowlText);

        if(gv.getSportType().equals("Cricket")){
            keeperText.setText("WK");
            batText.setText("BAT");
            allText.setText("AR");
            bowlText.setText("BOWL");
        } else{
            keeperText.setText("GK");
            batText.setText("DEF");
            allText.setText("ST");
            bowlText.setText("MID");
        }

        playerList = list.get(i).getPlayer();

        int wk=0,bat=0,ball=0,all=0;
        int t1=0,t2=0;

        for(SelectedPlayersGetSet zz:playerList){
            if(zz.getCaptain().equals("1")) {
                captainName.setText(zz.getName());
                Picasso.with(context).load(zz.getImage()).into(captain);

                if(zz.getPlayingstatus().equals("1")) {
                    status.setVisibility(View.VISIBLE);
                    status.setImageResource(R.drawable.player_selected);
                } else if(zz.getPlayingstatus().equals("0")) {
                    status.setVisibility(View.VISIBLE);
                    status.setImageResource(R.drawable.player_deselected);
                }

                if(zz.getTeam().equals("team1"))
                    captainName.setBackground(context.getResources().getDrawable(R.drawable.blue_name_bg));
                else
                    captainName.setBackground(context.getResources().getDrawable(R.drawable.red_name_bg));

            }else if(zz.getVicecaptain().equals("1")) {
                vicecaptainName.setText(zz.getName());
                Picasso.with(context).load(zz.getImage()).into(vicecaptain);

                if(zz.getPlayingstatus().equals("1")) {
                    status1.setVisibility(View.VISIBLE);
                    status1.setImageResource(R.drawable.player_selected);
                } else if(zz.getPlayingstatus().equals("0")) {
                    status1.setVisibility(View.VISIBLE);
                    status1.setImageResource(R.drawable.player_deselected);
                }
                if(zz.getTeam().equals("team1"))
                    vicecaptainName.setBackground(context.getResources().getDrawable(R.drawable.blue_name_bg));
                else
                    vicecaptainName.setBackground(context.getResources().getDrawable(R.drawable.red_name_bg));
            }

            if(gv.getSportType().equals("Cricket")) {

                if (zz.getRole().equals("keeper"))
                    wk++;
                else if (zz.getRole().equals("batsman"))
                    bat++;
                else if (zz.getRole().equals("allrounder"))
                    all++;
                else if (zz.getRole().equals("bowler"))
                    ball++;

                if (zz.getTeam().equals("team1"))
                    t1++;
                else
                    t2++;
            } else{
                if (zz.getRole().equals("goalkeeper"))
                    wk++;
                else if (zz.getRole().equals("defender"))
                    bat++;
                else if (zz.getRole().equals("striker"))
                    all++;
                else if (zz.getRole().equals("midfielder"))
                    ball++;

                if (zz.getTeam().equals("team1"))
                    t1++;
                else
                    t2++;
            }
        }

        team1_name.setText(gv.getTeam1());
        team2_name.setText(gv.getTeam2());

        countWK.setText(String.valueOf(wk));
        countBAT.setText(String.valueOf(bat));
        countALL.setText(String.valueOf(all));
        countBOWL.setText(String.valueOf(ball));

        team1.setText(String.valueOf(t1));
        team2.setText(String.valueOf(t2));

        teamName.setText(session.getTeamName()+" (T"+list.get(i).getTeamnumber()+")");

        editLL=(LinearLayout)v.findViewById(R.id.editLL);
        editLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerList= new ArrayList<>();
                playerList= list.get(i).getPlayer();

                captainList= new ArrayList<>();
                for(SelectedPlayersGetSet zz:playerList){

                    captainListGetSet ob = new captainListGetSet();
                    ob.setTeam(zz.getTeam());
                    ob.setName(zz.getName());
                    ob.setType(zz.getRole());
                    ob.setId(zz.getId());
                    ob.setCaptain(String.valueOf(zz.getCaptain()));
                    ob.setVc(String.valueOf(zz.getVicecaptain()));

                    captainList.add(ob);
                }

                if(gv.getSportType().equals("Cricket")) {
                    Intent ii = new Intent(context, CreateTeamActivity.class);
                    gv.setCaptainList(captainList);
                    ii.putExtra("from", "EditTeam");
                    ii.putExtra("edit", true);
                    ii.putExtra("type", list.get(i).getPlayer_type());
                    ii.putExtra("teamNumber", list.get(i).getTeamnumber());
                    context.startActivity(ii);
                } else{
                    Intent ii = new Intent(context, CreateTeamFootballActivity.class);
                    gv.setCaptainList(captainList);
                    ii.putExtra("from", "EditTeam");
                    ii.putExtra("edit", true);
                    ii.putExtra("type", list.get(i).getPlayer_type());
                    ii.putExtra("teamNumber", list.get(i).getTeamnumber());
                    context.startActivity(ii);
                }
            }
        });

        cloneLL=(LinearLayout)v.findViewById(R.id.cloneLL);

        if(list.size() >= gv.getMax_teams()) {
            cloneLL.setVisibility(View.GONE);
        }
        cloneLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                    playerList= new ArrayList<>();
                    playerList= list.get(i).getPlayer();

                    captainList= new ArrayList<>();
                    for(SelectedPlayersGetSet zz:playerList){

                        captainListGetSet ob = new captainListGetSet();
                        ob.setTeam(zz.getTeam());
                        ob.setName(zz.getName());
                        ob.setType(zz.getRole());
                        ob.setId(zz.getId());
                        ob.setCaptain(String.valueOf(zz.getCaptain()));
                        ob.setVc(String.valueOf(zz.getVicecaptain()));

                        captainList.add(ob);
                    }

                if(gv.getSportType().equals("Cricket")) {
                    if (list.size() < gv.getMax_teams()) {
                        Intent ii = new Intent(context, CreateTeamActivity.class);
                        gv.setCaptainList(captainList);
                        ii.putExtra("type", list.get(i).getPlayer_type());
                        ii.putExtra("from", "CloneTeam");
                        ii.putExtra("edit", true);
                        ii.putExtra("teamNumber", list.size() + 1);
                        context.startActivity(ii);
                    }
                } else{
                    if (list.size() < gv.getMax_teams()) {
                        Intent ii = new Intent(context, CreateTeamFootballActivity.class);
                        gv.setCaptainList(captainList);
                        ii.putExtra("type", list.get(i).getPlayer_type());
                        ii.putExtra("from", "CloneTeam");
                        ii.putExtra("edit", true);
                        ii.putExtra("teamNumber", list.size() + 1);
                        context.startActivity(ii);
                    }
                }
            }
        });

        v.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerList= new ArrayList<>();
                playerList= list.get(i).getPlayer();

                captainList= new ArrayList<>();

                if(gv.getSportType().equals("Cricket")) {

                    for(SelectedPlayersGetSet zz:playerList){

                        Log.i("Selected team ",zz.getName());
                        Log.i("captain",zz.getCaptain());
                        Log.i("Vice captain",zz.getVicecaptain());

                        captainListGetSet ob = new captainListGetSet();
                        ob.setTeam(zz.getTeam());
                        ob.setName(zz.getName());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        if(zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }if(zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }if(zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }if(zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setId(zz.getId());
                        ob.setCaptain(String.valueOf(zz.getCaptain()));
                        ob.setVc(String.valueOf(zz.getVicecaptain()));
                        ob.setPlayingstatus(zz.getPlayingstatus());

                        captainList.add(ob);
                    }

                    Intent ii = new Intent(context, TeamPreviewActivity.class);
                    ii.putExtra("team_name", "Team " + list.get(i).getTeamnumber());
                    gv.setCaptainList(captainList);
                    context.startActivity(ii);
                } else {
                    for(SelectedPlayersGetSet zz:playerList){

                        Log.i("Selected team ",zz.getName());
                        Log.i("captain",zz.getCaptain());
                        Log.i("Vice captain",zz.getVicecaptain());

                        captainListGetSet ob = new captainListGetSet();
                        ob.setTeam(zz.getTeam());
                        ob.setName(zz.getName());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        if(zz.getRole().equals("goalkeeper")) {
                            ob.setRole("GK");
                        }if(zz.getRole().equals("defender")) {
                            ob.setRole("DEF");
                        }if(zz.getRole().equals("midfielder")) {
                            ob.setRole("MF");
                        }if(zz.getRole().equals("striker")) {
                            ob.setRole("ST");
                        }
                        ob.setId(zz.getId());
                        ob.setCaptain(String.valueOf(zz.getCaptain()));
                        ob.setVc(String.valueOf(zz.getVicecaptain()));
                        ob.setPlayingstatus(zz.getPlayingstatus());

                        captainList.add(ob);
                    }

                    Intent ii = new Intent(context, TeamPreviewFootballActivity.class);
                    ii.putExtra("team_name", "Team " + list.get(i).getTeamnumber());
                    gv.setCaptainList(captainList);
                    context.startActivity(ii);
                }
            }
        });

        return v;
    }
}
