package com.img.mysure11.Fragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.img.mysure11.Adapter.PlayerListFootballAdapter;
import com.img.mysure11.GetSet.PlayerListGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import androidx.fragment.app.Fragment;

public class PlayersFootballFragment extends Fragment {


    Context context;
    ArrayList<PlayerListGetSet> list,list1;
    TextView selectionText;
    LinearLayout players,points,credits;
    TextView playerIcon,pointsIcon,creditsIcon;
    ListView playersList;
    String Text;
    boolean edit;

    String sortByCredit="asc";
    String sortByPoint="asc";
    String sortByName="z";

    @SuppressLint("ValidFragment")
    public PlayersFootballFragment(ArrayList<PlayerListGetSet> list1, ArrayList<PlayerListGetSet> list, String Text, boolean edit){
        this.list = list;
        this.list1 = list1;
        this.Text = Text;
        this.edit = edit;
    }

    public PlayersFootballFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v=  inflater.inflate(R.layout.fragment_players, container, false);

        context = getActivity();
        players=(LinearLayout)v.findViewById(R.id.players);
        points=(LinearLayout)v.findViewById(R.id.points);
        credits=(LinearLayout)v.findViewById(R.id.credits);

        playerIcon=(TextView)v.findViewById(R.id.playerIcon);
        pointsIcon=(TextView)v.findViewById(R.id.pointsIcon);
        creditsIcon=(TextView)v.findViewById(R.id.creditsIcon);

        selectionText =(TextView)v.findViewById(R.id.selectionText);
        selectionText.setText(Text);

        playersList = (ListView)v.findViewById(R.id.playersList);

//        Collections.sort(list, new Comparator<PlayerListGetSet>() {
//            @Override
//            public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
//                return (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? -1: (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? 1:0 ;
//            }
//        });
//        sortByCredit="desc";
//        creditsIcon.setText(Html.fromHtml("&#x2191;"));

        playersList.setAdapter(new PlayerListFootballAdapter(context,list,list1,edit));

        points.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerIcon.setText("");
                creditsIcon.setText("");

                if(sortByPoint.equals("asc")){
                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            return (Double.parseDouble(ob1.getTotalpoints()) > Double.parseDouble(ob2.getTotalpoints())) ? -1: (Double.parseDouble(ob1.getTotalpoints()) > Double.parseDouble(ob2.getTotalpoints())) ? 1:0 ;
                        }
                    });
                    sortByPoint="desc";
                    pointsIcon.setText(Html.fromHtml("&#x2191;"));
                }else{
                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            return (Double.parseDouble(ob1.getTotalpoints()) < Double.parseDouble(ob2.getTotalpoints())) ? -1: (Double.parseDouble(ob1.getTotalpoints()) > Double.parseDouble(ob2.getTotalpoints())) ? 1:0 ;
                        }
                    });
                    sortByPoint="asc";
                    pointsIcon.setText(Html.fromHtml("&#x2193;"));
                }
                playersList.setAdapter(new PlayerListFootballAdapter(context,list,list1,edit));
            }
        });

        credits.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                playerIcon.setText("");
                pointsIcon.setText("");

                if(sortByCredit.equals("asc")){
                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            return (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? -1: (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? 1:0 ;
                        }
                    });
                    sortByCredit="desc";
                    creditsIcon.setText(Html.fromHtml("&#x2191;"));
                }else{
                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            return (Double.parseDouble(ob1.getCredit()) < Double.parseDouble(ob2.getCredit())) ? -1: (Double.parseDouble(ob1.getCredit()) > Double.parseDouble(ob2.getCredit())) ? 1:0 ;
                        }
                    });
                    sortByCredit="asc";
                    creditsIcon.setText(Html.fromHtml("&#x2193;"));
                }
                playersList.setAdapter(new PlayerListFootballAdapter(context,list,list1,edit));
            }
        });

        players.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pointsIcon.setText("");
                creditsIcon.setText("");

                if(sortByName.equals("a")){
                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            char l = Character.toUpperCase(ob1.getName().charAt(0));
                            if (l < 'A' || l > 'Z')
                                l += 'Z';
                            char r = Character.toUpperCase(ob2.getName().charAt(0));
                            if (r < 'A' || r > 'Z')
                                r += 'Z';
                            String s1 = l + ob1.getName().substring(1);
                            String s2 = r + ob2.getName().substring(1);
                            return s1.compareTo(s2);
                        }
                    });
                    sortByName="z";
                    playerIcon.setText(Html.fromHtml("&#x2191;"));
                }
                else {
                    Collections.sort(list, new Comparator<PlayerListGetSet>() {
                        @Override
                        public int compare(PlayerListGetSet ob1, PlayerListGetSet ob2) {
                            char l = Character.toUpperCase(ob1.getName().charAt(0));
                            if (l < 'A' || l > 'Z')
                                l += 'Z';
                            char r = Character.toUpperCase(ob2.getName().charAt(0));
                            if (r < 'A' || r > 'Z')
                                r += 'Z';
                            String s1 = l + ob1.getName().substring(1);
                            String s2 = r + ob2.getName().substring(1);
                            return s2.compareTo(s1);
                        }
                    });
                    sortByName="a";
                    playerIcon.setText(Html.fromHtml("&#x2193;"));
                }
                playersList.setAdapter(new PlayerListFootballAdapter(context,list,list1,edit));
            }
        });

        return v;
    }

    @Override
    public boolean getUserVisibleHint() {
        if(isResumed()){
            playersList.setAdapter(new PlayerListFootballAdapter(context,list,list1,edit));
        }
        return super.getUserVisibleHint();
    }

    @Override
    public void onResume() {
        super.onResume();
        playersList.setAdapter(new PlayerListFootballAdapter(context,list,list1,edit));
    }

}
