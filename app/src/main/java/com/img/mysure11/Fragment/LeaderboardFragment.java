package com.img.mysure11.Fragment;


import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.img.mysure11.Adapter.LeaderBoardAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.GetSet.teamsGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;


/**
 * A simple {@link Fragment} subclass.
 */
public class LeaderboardFragment extends Fragment {

    Context context;

    TextView totalTeamslb;
    RecyclerView leardboard;
    RecyclerView.LayoutManager mLayoutManager;
    ArrayList<teamsGetSet> teams,teams1;
    LeaderBoardAdapter adapter;

    int challenge_id;

    public LeaderboardFragment() {
        // Required empty public constructor
    }

    public LeaderboardFragment(ArrayList<teamsGetSet>teams,  int challenge_id) {
        // Required empty public constructor
        this.teams = teams;
        this.challenge_id = challenge_id;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_leaderboard, container, false);
        context = getActivity();

        totalTeamslb=(TextView)v.findViewById(R.id.totalteamsLB);
        totalTeamslb.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AppUtils().showError(context,"Hang on!You'll be able to download teams only after deadline");
            }
        });

        leardboard= (RecyclerView) v.findViewById(R.id.leardboard);
        leardboard.setNestedScrollingEnabled(false);
        mLayoutManager = new LinearLayoutManager(context);
        mLayoutManager.setAutoMeasureEnabled(false);
        leardboard.setLayoutManager(mLayoutManager);

        if (teams != null) {
            if (teams.size() > 50)
                teams1 = new ArrayList<>(teams.subList(0, 50));
            else
                teams1 = teams;

            adapter = new LeaderBoardAdapter(context, teams1,challenge_id);
            leardboard.setAdapter(adapter);

            leardboard.addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                    if (!recyclerView.canScrollVertically(1)) {
                        if (teams.size() > teams1.size()) {
                            int x, y;
                            if ((teams.size() - teams1.size()) >= 50) {
                                x = teams1.size();
                                y = x + 50;
                            } else {
                                x = teams1.size();
                                y = x + teams.size() - teams1.size();
                            }
                            for (int i = x; i < y; i++) {
                                teams1.add(teams.get(i));
                            }
                            adapter.notifyDataSetChanged();
                        }

                    }
                }
            });
        }
        return v;
    }
}
