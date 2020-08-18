package com.img.mysure11.Fragment;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Adapter.resultMatchesAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.joinedMatchesGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class UpcomingFragment extends Fragment {

    Context context;

    ArrayList<joinedMatchesGetSet> joinedMatchList;
    RecyclerView matchesRecycler;
    LinearLayout noMatchesLL;
    TextView noMatchText;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String sportType;
    int matchType;

    public UpcomingFragment() {
        // Required empty public constructor
    }

    public UpcomingFragment(ArrayList<joinedMatchesGetSet> joinedMatchList,String sportType,int matchType) {
        this.joinedMatchList = joinedMatchList;
        this.sportType = sportType;
        this.matchType = matchType;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_upcoming, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        matchesRecycler = v.findViewById(R.id.matchesRecycler);
        matchesRecycler.setLayoutManager(new LinearLayoutManager(context));

        noMatchesLL = v.findViewById(R.id.noMatchesLL);
        noMatchText = v.findViewById(R.id.noMatchText);

        if(joinedMatchList.size() > 0) {
            matchesRecycler.setAdapter(new resultMatchesAdapter(context, joinedMatchList, sportType));
            matchesRecycler.setVisibility(View.VISIBLE);
            noMatchesLL.setVisibility(View.GONE);
        } else {
            matchesRecycler.setVisibility(View.GONE);
            noMatchesLL.setVisibility(View.VISIBLE);

            if(matchType == 0)
                noMatchText.setText("No upcoming match joined yet");
            else if(matchType == 1)
                noMatchText.setText("No ongoing match joined yet");
            else
                noMatchText.setText("No completed match joined yet");
        }

        Log.i("Auth",session.getUserId());

        return v;
    }
}
