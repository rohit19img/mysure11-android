package com.img.mysure11.Static;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Adapter.ScorecardAdapter;
import com.img.mysure11.Extras.ExpandableHeightExpendableListView;
import com.img.mysure11.Fragment.FantasyODIFragment;
import com.img.mysure11.Fragment.FantasyT10Fragment;
import com.img.mysure11.Fragment.FantasyT20Fragment;
import com.img.mysure11.Fragment.FantasyTESTFragment;
import com.img.mysure11.GetSet.FantasyScoreGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class FantasyPointSystemFootballActivity extends AppCompatActivity {

    ArrayList<FantasyScoreGetSet> list;
    int lastExpandedPosition = -1;
    ExpandableHeightExpendableListView scoreList;
    TextView text;

    String [] points = {"+2","+1"
            ,"+10","+9","+8","+5","+0.5","+1"
            ,"+1","+5","+2","+9","+1"
            ,"-1","-3","-2","-1","-2"};
    String [] type = {"Played 55 minutes or more","Played less than 55 minutes"
            ,"For every goal scored","For every goal scored","For every goal scored","For every assist","For every 10 passes completed","For every 2 shots on target"
            ,"Clean sheet","Clean sheet","For every 3 shots saved", "For every penalty saved","For every 3 successful tackles made"
            ,"Yellow card","Red card","For every own goal","For every 2 goals conceded","For every penalty missed"     };
    String[] type_sub_text = {"", "",
            "GK/Defender", "Midfielder", "Forward", "", "", "",
            "Midfielder", "GK/Defender", "GK","GK","",
            "", "","", "GK/Defender",""};
    String [] role = {"bat","bat"
            ,"ball","ball","ball","ball","ball","ball"
            ,"fielding","fielding","fielding","fielding","fielding"
            ,"others","others","others","others","others"};

    String groupAr[]= {"Playing Time","Attack","Defense","Card & Other penalties"};
    String groupSubTitle[]={"","","",""};
    int icons[] = {R.drawable.ms_playing_time,R.drawable.ms_attack,R.drawable.ms_defense,R.drawable.ms_cards};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fantasy_point_system_football);

        ImageView back=(ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("Fantasy Point System");

        text = (TextView)findViewById(R.id.text);
        scoreList = (ExpandableHeightExpendableListView)findViewById(R.id.scoreList);
        scoreList.setExpanded(true);

        text.setText(Html.fromHtml("<h5>Key things to remember</h5>\n" +
                "<ul class=\"sub_list\" style=\"margin: 30px 0;\">\n" +
                "<li>No points will be awarded for assists, passes, saves (including penalty saves), shots, penalties missed and tackles in the Nicaragua Premier League</li>\n" +
                "<li>The player you choose to be your team’s Captain will receive <strong>2 times</strong> the points for his/her performance</li>\n" +
                "<li>The player you choose to be your team’s Vice-captain will receive <strong>1.5 times</strong> the points for his/her performance</li>\n" +
                "<li>Starting points are assigned to any player on the basis of announcement of his/her inclusion in the team. However, in case the player is unable to start the match after being included in the team sheet, he/she will not score any points. Points shall however, be applicable (including starting points) to any player who plays as a replacement of such player to whom starting points were initially assigned.</li>\n" +
                "<li><strong>Fantasy Points for the ISL will be awarded based on the information and stats provided by the official ISL score providers. The information on the official scorecard will override all conditions mentioned for assists, passes, etc. THE OFFICIAL ISL SCORECARD IS THE FINAL POINT OF REFERENCE FOR AWARDING POINTS IN FANTASY ISL.</strong></li>\n" +
                "<li>If a player has been substituted before a goal is conceded, the player will still get the clean sheet bonus (provided he has played at least 55 minutes).</li>\n" +
                "<li>Assists are awarded to the player from the goal scoring team, who makes the final pass before a goal is scored.\n" +
                "<div>An assist is awarded when:</div>\n" +
                "<ul class=\"pdBtm5\">\n" +
                "<li style=\"list-style: none!important;\">a) The pass was intentional (that it actually creates the goal).</li>\n" +
                "<li style=\"list-style: none!important;\">b) The pass was unintentional (that the player had to dribble the ball or an inadvertent touch or shot created the goal).</li>\n" +
                "</ul>\n" +
                "<div>An assist is NOT awarded when:</div>\n" +
                "<ul class=\"pdBtm5\">\n" +
                "<li style=\"list-style: none!important;\">a) An opposing player touches the ball after the final pass before a goal is scored, significantly altering the intended destination of the ball.</li>\n" +
                "<li style=\"list-style: none!important;\">b) Two or more opposing players touch the ball after the final pass before a goal is scored.</li>\n" +
                "<li style=\"list-style: none!important;\">c) The goal scorer loses possession to a player from the opposition team and then regains control of the ball</li>\n" +
                "</ul>\n" +
                "</li>\n" +
                "<li>Points for assists will be awarded to the player:\n" +
                "<ul class=\"pdBtm5\">\n" +
                "<li style=\"list-style: none!important;\">a) For winning a Penalty</li>\n" +
                "<li style=\"list-style: none!important;\">b) For winning a Free kick</li>\n" +
                "<li style=\"list-style: none!important;\">c) For having his/her shot saved or blocked by a defender, goalkeeper or goalpost if his/her teammate scores on the rebound</li>\n" +
                "<li style=\"list-style: none!important;\">d) For a cross or pass that results in an own goal</li>\n" +
                "<li style=\"list-style: none!important;\">e) For having his/her shot saved/blocked/rebounded and the next touch (by opposition player) results in an own goal</li>\n" +
                "</ul>\n" +
                "All of the above must result in a successful goal for points to be awarded to the player providing the assist.</li>\n" +
                "<li>In cases where a player misses a penalty kick and scores on the rebound, he will get points for the penalty but will get -2 points for the miss. He will NOT get any points for the assist.</li>\n" +
                "<li>In cases where a player misses a penalty kick but the goalkeeper hasn’t touched the ball, the GK will NOT get any points for saving the penalty, while the player will get -2 points for the miss.</li>\n" +
                "<li>Goals conceded will be counted for the players playing on the field at the time of the goal irrespective of player's total playing time</li>\n" +
                "<li>If a player receives a red card, he will continue to be penalised for goals conceded by his/her team. i.e for the goals conceded after he leaves the field</li>\n" +
                "<li>Point deductions for a red card include any points deducted for yellow cards</li>\n" +
                "<li>Data is provided by reliable sources and once the points have been marked as completed i.e. winners have been declared, no further adjustments will be made. Points awarded live in-game are subject to change as long as the status is 'In progress' or 'Waiting for review'.</li>\n" +
                "<li>Any event during extra time will be considered for awarding points</li>\n" +
                "<li>Any event during penalty shootouts will not be considered for awarding points</li>\n" +
                "<li>A player who has not participated in the game as part of the starting 11 or as a substitute, will not be awarded negative points for receiving a yellow or red card for off field activity</li>\n" +
                "<li>54 minutes and 1 second onwards (54’1”) will be considered as 55 minutes for the purpose of point calculation.</li>\n" +
                "<li>In case a player is transferred/reassigned to a different team between two scheduled updates, for any reason whatsoever, such transfer/reassignment (by whatever name called) shall not be reflected in the roster of players until next scheduled update. It is clarified that during the intervening period of two scheduled updates, while such player will be available for selection in the team to which the player originally belong, no points will be attributable to such player during the course of such contest.</li>\n" +
                "\n" +
                "<li>If a player received a yellow or red card for an off-field activity, the Fantasy Points will come into effect if\n" +
                "<ul class=\"pdBtm5\">\n" +
                "<li style=\"list-style: none!important;\">a) He already played the match and was subbed off</li>\n" +
                "<li style=\"list-style: none!important;\">b) He comes on the field as substitute after receiving a yellow card and plays the match</li>\n" +
                "</ul>\n" +
                "<strong>In case the player does not play the match at all, no points will be given.</strong>\n" +
                "</li>\n" +
                "</ul>"));

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

        scoreList.setAdapter(new ScorecardAdapter(this,list,groupAr,icons,groupSubTitle));

    }

}
