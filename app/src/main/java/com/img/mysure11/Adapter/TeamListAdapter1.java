package com.img.mysure11.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;

import com.img.mysure11.Activity.TeamPreviewActivity;
import com.img.mysure11.Activity.TeamPreviewFootballActivity;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.MyTeamsGetSet;
import com.img.mysure11.GetSet.SelectedPlayersGetSet;
import com.img.mysure11.GetSet.captainListGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;


public class TeamListAdapter1 extends BaseAdapter{

    GlobalVariables gv;
    Context context;
    ArrayList<MyTeamsGetSet> list;
    String type;
    String multi;

    public TeamListAdapter1(Context context, ArrayList<MyTeamsGetSet> list, String type, String multi){
        this.context= context;
        this.list=list;
        this.type = type;
        this.multi = multi;

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
        RadioButton teamName;
        CheckBox teamNameCC;
        TextView captainName,viceCaptainName,btnPreview,allreadyselected;
        LinearLayout ll;

        LayoutInflater inflater= (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        v= inflater.inflate(R.layout.team_list1,null);

        teamName=(RadioButton) v.findViewById(R.id.teamName);
        teamNameCC=(CheckBox) v.findViewById(R.id.teamNameCC);
        captainName=(TextView)v.findViewById(R.id.captainName);
        viceCaptainName=(TextView)v.findViewById(R.id.viceCaptainName);
        btnPreview=(TextView)v.findViewById(R.id.btnPreview);
        allreadyselected=(TextView)v.findViewById(R.id.allreadyselected);

        ll = (LinearLayout)v.findViewById(R.id.ll);

        if(multi.equals("1")){
            teamNameCC.setVisibility(View.VISIBLE);
            teamName.setVisibility(View.GONE);
        }else{
            teamNameCC.setVisibility(View.GONE);
            teamName.setVisibility(View.VISIBLE);
        }
        captainName.setText(list.get(i).getCaptain());
        viceCaptainName.setText(list.get(i).getVicecaptain());

        teamName.setText("Team "+list.get(i).getTeamnumber());
        teamNameCC.setText("Team "+list.get(i).getTeamnumber());

        if(list.get(i).isSelected()) {
            teamName.setText("Team " + list.get(i).getTeamnumber() + " (ALREADY JOINED)");
            ll.setBackground(context.getResources().getDrawable(R.drawable.green_border));
            allreadyselected.setVisibility(View.VISIBLE);
            ll.setEnabled(false);
            teamName.setEnabled(false);
            teamNameCC.setEnabled(false);
        }else {
            ll.setEnabled(true);
            teamName.setEnabled(true);
            teamNameCC.setEnabled(true);
            allreadyselected.setVisibility(View.GONE);
        }

        btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<captainListGetSet> captainList;
                ArrayList<SelectedPlayersGetSet> playerList;

                playerList= list.get(i).getPlayer();

                captainList= new ArrayList<>();

                if(gv.getSportType().equals("Cricket")) {

                    for (SelectedPlayersGetSet zz : playerList) {

                        Log.i("Selected team ", zz.getName());
                        Log.i("captain", zz.getCaptain());
                        Log.i("Vice captain", zz.getVicecaptain());

                        captainListGetSet ob = new captainListGetSet();
                        ob.setTeam(zz.getTeam());
                        ob.setName(zz.getName());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        ob.setPlayingstatus(zz.getPlayingstatus());
                        if (zz.getRole().equals("keeper")) {
                            ob.setRole("Wk");
                        }
                        if (zz.getRole().equals("batsman")) {
                            ob.setRole("Bat");
                        }
                        if (zz.getRole().equals("bowler")) {
                            ob.setRole("Bow");
                        }
                        if (zz.getRole().equals("allrounder")) {
                            ob.setRole("AR");
                        }
                        ob.setId(zz.getId());
                        ob.setCaptain(String.valueOf(zz.getCaptain()));
                        ob.setVc(String.valueOf(zz.getVicecaptain()));

                        captainList.add(ob);
                    }

                    Intent ii = new Intent(context, TeamPreviewActivity.class);
                    ii.putExtra("team_name", "Team " + list.get(i).getTeamnumber());
                    gv.setCaptainList(captainList);
                    context.startActivity(ii);
                } else {

                    for (SelectedPlayersGetSet zz : playerList) {

                        Log.i("Selected team ", zz.getName());
                        Log.i("captain", zz.getCaptain());
                        Log.i("Vice captain", zz.getVicecaptain());

                        captainListGetSet ob = new captainListGetSet();
                        ob.setTeam(zz.getTeam());
                        ob.setName(zz.getName());
                        ob.setCredit(zz.getCredit());
                        ob.setImage(zz.getImage());
                        ob.setPlayingstatus(zz.getPlayingstatus());
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

                        captainList.add(ob);
                    }

                    Intent ii = new Intent(context, TeamPreviewFootballActivity.class);
                    ii.putExtra("team_name", "Team " + list.get(i).getTeamnumber());
                    gv.setCaptainList(captainList);
                    context.startActivity(ii);
                }
            }
        });

        if(list.get(i).isPicked()){
            teamName.setChecked(true);
            ll.setBackground(context.getResources().getDrawable(R.drawable.green_border));
            teamNameCC.setChecked(true);
        }else {
            teamName.setChecked(false);
            teamNameCC.setChecked(false);
        }

        ll.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multi.equals("0")) {
                    for (MyTeamsGetSet zz : list) {
                        zz.setPicked(false);
                    }
                    list.get(i).setPicked(true);
                }else {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }
                notifyDataSetChanged();
            }
        });

        teamName.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multi.equals("0")) {
                    for (MyTeamsGetSet zz : list) {
                        zz.setPicked(false);
                    }
                    list.get(i).setPicked(true);
                }else {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }
                notifyDataSetChanged();
            }
        });

        teamNameCC.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(multi.equals("0")){
                    for (MyTeamsGetSet zz : list) {
                        zz.setPicked(false);
                    }
                    list.get(i).setPicked(true);
                }else {
                    if (list.get(i).isPicked())
                        list.get(i).setPicked(false);
                    else
                        list.get(i).setPicked(true);
                }
                notifyDataSetChanged();
            }
        });

        return v;
    }
}
