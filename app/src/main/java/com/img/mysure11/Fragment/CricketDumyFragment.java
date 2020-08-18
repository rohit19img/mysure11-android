package com.img.mysure11.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.google.android.material.tabs.TabLayout;
import com.img.mysure11.Adapter.SlideBannerAdapter;
import com.img.mysure11.Adapter.upcomingMatchesDumyAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.bannersGetSet;
import com.img.mysure11.GetSet.upcomingMatchesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class CricketDumyFragment extends Fragment {

    Context context;
    ViewPager vpAdvertisments;
    ArrayList<bannersGetSet> imageList = new ArrayList();

    ArrayList<upcomingMatchesGetSet> matchList = new ArrayList<>();
    RecyclerView matchesRecycler;
    LinearLayout myMatchesLL;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String type;

    public CricketDumyFragment() {
        // Required empty public constructor
    }

    public CricketDumyFragment(ArrayList<upcomingMatchesGetSet> matchList, ArrayList<bannersGetSet> imageList,  String type) {
        this.matchList = matchList;
        this.imageList = imageList;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cricket_home, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        vpAdvertisments = v.findViewById(R.id.vpAdvertisments);

        vpAdvertisments.setAdapter(new SlideBannerAdapter(context,imageList));
        final int[] currentPage = {0};
        Timer timer;
        final long DELAY_MS = 500;//delay in milliseconds before task is to be executed
        final long PERIOD_MS = 10000; // time in milliseconds between successive task executions.

        final Handler handler = new Handler();
        final Runnable Update = new Runnable() {
            public void run() {
                if (currentPage[0] == imageList.size()) {
                    currentPage[0] = 0;
                }
                vpAdvertisments.setCurrentItem(currentPage[0]++, true);
            }
        };

        timer = new Timer(); // This will create a new Thread
        timer .schedule(new TimerTask() { // task to be scheduled

            @Override
            public void run() {
                handler.post(Update);
            }
        }, DELAY_MS, PERIOD_MS);

        matchesRecycler = v.findViewById(R.id.matchesRecycler);
        matchesRecycler.setLayoutManager(new LinearLayoutManager(context));
        matchesRecycler.setAdapter(new upcomingMatchesDumyAdapter(context,matchList,type));

        myMatchesLL = v.findViewById(R.id.myMatchesLL);
        myMatchesLL.setVisibility(View.GONE);

        return v;
    }
}
