package com.img.mysure11.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.img.mysure11.Adapter.LiveLeaderBoardAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.GetSet.liveLeaderboardGetSet;
import com.img.mysure11.GetSet.liveTeamsGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class LiveLeaderboardFragment extends Fragment {

    Context context;
    ArrayList<liveTeamsGetSet> teams,teams1;
    ArrayList<liveLeaderboardGetSet> list;
    int challenge_id;

    ImageView btnDownload;
    RecyclerView leardboard;
    LinearLayoutManager manager;

    LiveLeaderBoardAdapter adapter;

    GlobalVariables gv;

    public LiveLeaderboardFragment(ArrayList<liveLeaderboardGetSet> list, int challenge_id) {
        this.list = list;
        this.challenge_id = challenge_id;

    }

    public LiveLeaderboardFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_live_leaderboard, container, false);
        context = getActivity();

        gv = (GlobalVariables)context.getApplicationContext();

        btnDownload = v.findViewById(R.id.btnDownload);
        btnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String Pdflink = getResources().getString(R.string.app_url).replace("/api/", "/getPdfDownload?matchkey=") + gv.getMatchKey()+"&challengeid="+challenge_id;

                Log.i("url is", Pdflink);

                if (ActivityCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                    DownloadManager.Request request = new DownloadManager.Request(Uri.parse(Pdflink));

                    request.allowScanningByMediaScanner();
                    request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);//Notify client once download is completed!
                    request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, "MySure11-Contest-"+challenge_id+".pdf");
                    DownloadManager dm = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
                    dm.enqueue(request);
                    new AppUtils().showSuccess(context, "Downloading File"); //To notify the Client that the file is being downloaded
                } else {
                    String[] PERMISSIONS_STORAGE = new String[] {Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE};
                    ActivityCompat.requestPermissions( (Activity) context, PERMISSIONS_STORAGE, 1);
                }
            }
        });

        leardboard = v.findViewById(R.id.leardboard);
        manager = new LinearLayoutManager(context);
        leardboard.setLayoutManager(manager);

        teams = list.get(0).getJointeams();
        teams1 = new ArrayList<>();

        if (teams != null) {
            if (teams.size() > 50)
                teams1 = new ArrayList<>(teams.subList(0, 50));
            else
                teams1 = teams;

            adapter = new LiveLeaderBoardAdapter(context, teams1,challenge_id);
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
