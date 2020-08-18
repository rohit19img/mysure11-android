package com.img.mysure11.Fragment;

import android.app.Dialog;
import android.content.Context;
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
import com.img.mysure11.Activity.MyMatchesActivity;
import com.img.mysure11.Adapter.ResultMatchesVPAdapter;
import com.img.mysure11.Adapter.SlideBannerAdapter;
import com.img.mysure11.Adapter.upcomingMatchesAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.bannersGetSet;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.GetSet.upcomingMatchesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;

public class CricketMyMatchesFragment extends Fragment {

    Context context;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String type;
    ArrayList<joinedMatchesGetSet> joinedMatchList,listUpcoming,listLive,listResult;

    TabLayout tab;
    ViewPager vp;

    public CricketMyMatchesFragment() {
        // Required empty public constructor
    }

    public CricketMyMatchesFragment(ArrayList<joinedMatchesGetSet> joinedMatchList, String type) {
        this.joinedMatchList = joinedMatchList;
        this.type = type;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_cricket_my_matches, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        tab = v.findViewById(R.id.tab);
        vp = v.findViewById(R.id.vp);

        listUpcoming = new ArrayList<>();
        listLive = new ArrayList<>();
        listResult = new ArrayList<>();

        for (joinedMatchesGetSet zz : joinedMatchList) {
            if (zz.getStatus().equals("opened"))
                listUpcoming.add(zz);
            else if (zz.getStatus().equals("closed") && (zz.getFinal_status().equals("pending") || zz.getFinal_status().equals("IsReviewed")))
                listLive.add(zz);
            else if (zz.getStatus().equals("closed") && (zz.getFinal_status().equals("winnerdeclared") || zz.getFinal_status().equals("IsAbandoned") || zz.getFinal_status().equals("IsCanceled")))
                listResult.add(zz);
        }

        tab.setupWithViewPager(vp);
        vp.setAdapter(new SectionPagerAdapter(getChildFragmentManager()));

        return v;
    }

    public class SectionPagerAdapter extends FragmentStatePagerAdapter {
        public SectionPagerAdapter(FragmentManager fm) {
            super(fm);
        }
        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    return new UpcomingFragment(listUpcoming,type,0);
                case 1:
                    return new UpcomingFragment(listLive,type,1);
                default:
                    return new UpcomingFragment(listResult,type,2);
            }
        }

        @Override
        public int getCount() {
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            switch (position) {
                case 0:
                    return "Upcoming";
                case 1:
                    return "Live";
                default:
                    return "Completed";
            }
        }
    }

}
