package com.img.mysure11.Fragment;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;
import android.widget.TextView;

import com.img.mysure11.Adapter.ScorecardAdapter;
import com.img.mysure11.Extras.ExpandableHeightExpendableListView;
import com.img.mysure11.GetSet.FantasyScoreGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

import androidx.fragment.app.Fragment;

/**
 * A simple {@link Fragment} subclass.
 */
public class FantasyT10Fragment extends Fragment {

    Context context;
    ArrayList<FantasyScoreGetSet> list;
    int lastExpandedPosition = -1;
    ExpandableHeightExpendableListView scoreList;
    TextView text;

    String [] points = {"+1","+1","+2","+8","+16","-2"
            ,"+25","+8","+16","+16"
            ,"+8","+12","+8","+4"
            ,"2x","1.5x","+4"
            ,"+6","+4","+2","-2","-4","-6"
            ,"-2","-4","-6"};
    String [] type = {"Run","Boundary Bonus","Six Bonus","30 Run Bonus","50 Run Bonus","Dismissal for a duck"
            ,"Wicket","2 Wicket haul Bonus","3 Wicket haul Bonus","Maiden Over"
            ,"Catch","Stumping/Run-out","Run Out(Thrower)", "Run Out(Catcher)"
            ,"Captain","Vice-Captain","Starting"
            ,"Below 6 runs per over","Between 6 - 6.99 runs per over","Between 7 - 8 runs per over","Between 11 - 12 runs per over","Between 12.01 - 13 runs per over","Above 13 runs per over"
            ,"Between 90 - 99.99 runs per 100 balls","Between 80 - 89.99 runs per 100 balls","Below 80 runs per 100 balls"};
    String[] type_sub_text = {"", "", "", "", "", "Batsman, Wicket-Keeper & All-Rounder",
            "Excluding Run Out", "", "", "",
            "", "", "","",
            "", "","", "",
            "", "", "", "", "", "",
            "", "", ""};
    String [] role = {"bat","bat","bat","bat","bat","bat"
            ,"ball","ball","ball","ball"
            ,"fielding","fielding","fielding","fielding"
            ,"others","others","others"
            ,"economy","economy","economy","economy","economy","economy"
            ,"strike","strike","strike"};

    String groupAr[]= {"Batting","Bowling","Fielding","Others","Economy Rate","Strike Rate(Except Bowlers)"};
    String groupSubTitle[]={"","","","","Min 1 Over","Min 5 Balls"};
    int icons[] = {R.drawable.mw11_bat,R.drawable.mw11_ball,R.drawable.mw11_fielding,R.drawable.mw11_others,R.drawable.mw11_economy,R.drawable.mw11_strike};

    public FantasyT10Fragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v= inflater.inflate(R.layout.fragment_fantasy_t20, container, false);
        context = getActivity();

        text = (TextView)v.findViewById(R.id.text);
        scoreList = (ExpandableHeightExpendableListView)v.findViewById(R.id.scoreList);
        scoreList.setExpanded(true);

        text.setText(Html.fromHtml("<h5>Key things to remember</h5>\n" +
                "<ol><li><p>The cricketer you choose to be your Fantasy Cricket Team's Captain will receive 2 times the points.</p></li>\n" +
                "<li><p>The Vice-Captain will receive 1.5 times the points for his/her performance.</p></li>\n" +
                "<li><p>Starting points are assigned to any player on the basis of announcement of his/her inclusion in the team. However, in case the player in unable to start the match after being included in the team sheet, he/she will not score any points. Points shall however, be applicable (including starting points) to any player who plays as a replacement of such player to whom starting points were initially assigned.</p></li>\n" +
                "<li><p>11 players from each side play the game.</p></li>\n" +
                "<li><p>Strike rate scoring is applicable only for strike rate below 60 runs per 100 balls.</p></li>\n" +
                "<li><p>None of the events occurring during a Super Over, if any, will be considered for Points to be assigned/applied to a player.</p></li>\n" +
                "<li><p>In case of run-outs involving 3 or more players from the fielding side, points will be awarded only to the last 2 players involved in the run-out.</p></li>\n" +
                "<li><p>Any player scoring a century will only get points for the century and no points will be awarded for half-century. Any score over and above century will be eligible for bonus points only for the century.</p></li>\n" +
                "<li><p>Substitutes on the field will not be awarded points for any contribution they make. However, 'Concussion Substitutes' will be awarded four (4) points for making an appearance in a match and will be awarded points any contribution they make as per the Fantasy Point System.</p></li>\n" +
                "<li><p>In case a player is transferred/reassigned to a different team between two scheduled updates, for any reason whatsoever, such transfer/reassignment (by whatever name called) shall not be reflected in the roster of players until next scheduled update. It is clarified that during the intervening period of two scheduled updates,while such player will be available for selection in the team to which the player originally belong, no points will be attributable to such player during the course of such contest.</p></li>\n" +
                "<li><p>Data is provided by reliable sources and once the points have been marked as completed i.e. winners have been declared, no further adjustments will be made. Points awarded live in-game are subject to change as long as the status is 'In progress' or 'Waiting for review'.</p></li></ol>\n"));

        list = new ArrayList<>();
        for(int i =0; i < points.length; i++){
            FantasyScoreGetSet ob = new FantasyScoreGetSet();
            ob.setPoints(points[i]);
            ob.setRole(role[i]);
            ob.setType(type[i]);
            ob.setTypeSubText(type_sub_text[i]);

            list.add(ob);
        }

        scoreList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    scoreList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        scoreList.setAdapter(new ScorecardAdapter(context,list,groupAr,icons,groupSubTitle));

        return v;
    }

}
