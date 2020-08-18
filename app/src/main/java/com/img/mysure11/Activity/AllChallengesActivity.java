package com.img.mysure11.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.facebook.all.All;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.img.mysure11.Adapter.FilterGridAdapter;
import com.img.mysure11.Adapter.contestsAdapter;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.ExpandableHeightGridView;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.contestGetSet;
import com.img.mysure11.GetSet.filterGetSet;
import com.img.mysure11.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

public class AllChallengesActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView matchName,matchTime;

    RecyclerView ll;
    SwipeRefreshLayout swipeRefreshLayout;
    ArrayList<contestGetSet> list,list1, list2;

    String catid;

    String entryAr[] = {"₹1 to ₹50","₹51 to ₹100","₹101 to 1000","₹1,001 & above"};
    String prizePoolAr[] = {"₹1 - ₹10,000","₹10,001 - ₹1 Lakh","₹1 Lakh - ₹10 Lakh","₹10 Lakh - ₹25 Lakh","₹25 Lakh & above"};
    String conTypeAr[] = {"Single Entry","Multi Entry","Single Winner","Multi Winner","Confirmed"};
    String numofTeamsAr [] = {"2","3 - 10","11 - 100","101 - 1,000","1,001 & above"};

    private BottomSheetBehavior mBottomSheetBehavior1;
    String type ="";
    String winning_sort = "asc", teams_sort = "asc", winners_sort = "asc", entry_sort = "asc";
    ExpandableHeightGridView entryGrid,numberOfTeamsGrid,prizePoolGrid,contestTypeGrid;
    TextView closeFilter,resetFilter;
    Button btnApply;
    ArrayList<filterGetSet> entryList,numofTeamsList,prizePoolList,contestTypeList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_challenges);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        matchName = findViewById(R.id.matchName);
        matchName.setText(gv.getTeam1()+" vs " + gv.getTeam2());

        matchTime = findViewById(R.id.matchTime);
        String sDate = "2017-09-08 10:05:00";
        String eDate = "2017-09-10 12:05:00";
        Date startDate=null,endDate = null;

        Calendar c = Calendar.getInstance();
        c.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        int hour = c.get(Calendar.HOUR_OF_DAY);
        final int minute = c.get(Calendar.MINUTE);
        int sec = c.get(Calendar.SECOND);
        int mYear1 = c.get(Calendar.YEAR);
        int mMonth1 = c.get(Calendar.MONTH);
        int mDay1 = c.get(Calendar.DAY_OF_MONTH);

        sDate = mYear1 + "-" + (mMonth1 + 1) + "-" + mDay1 + " " + hour + ":" + minute + ":" + sec;
        eDate = gv.getMatchTime();
        Log.i("matchtime", gv.getMatchTime());

        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            startDate = dateFormat.parse(sDate);
            endDate = dateFormat.parse(eDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        final long diffInMs = endDate.getTime() - startDate.getTime();

        CountDownTimer cT = new CountDownTimer(diffInMs, 1000) {
            public void onTick(long millisUntilFinished) {
                matchTime.setText(String.format(String.format("%02d", TimeUnit.MILLISECONDS.toHours(millisUntilFinished)) + " : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished) - TimeUnit.HOURS.toMinutes(TimeUnit.MILLISECONDS.toHours(millisUntilFinished))) + " : "
                        + String.format("%02d", TimeUnit.MILLISECONDS.toSeconds(millisUntilFinished) - TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes(millisUntilFinished))) + ""));
            }
            public void onFinish()
            {
                if(progressDialog!=null)
                    progressDialog.dismiss();


                requestQueue.stop();

                finish();
            }
        };
        cT.start();

        findViewById(R.id.wallet).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(AllChallengesActivity.this,WalletActivity.class));
            }
        });

        catid = getIntent().getExtras().getString("catid");

        ll = findViewById(R.id.ll);
        ll.setLayoutManager(new LinearLayoutManager(this));

        swipeRefreshLayout = findViewById(R.id.swipeRefreshLayout);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if(cd.isConnectingToInternet()) {
                    progressDialog.show();
                    getChallenges();
                } else
                    new AppUtils().NoInternet(AllChallengesActivity.this);

                swipeRefreshLayout.setRefreshing(false);
            }
        });


        // filter

        View bottomSheet = findViewById(R.id.bottom_sheet1);
        mBottomSheetBehavior1 = BottomSheetBehavior.from(bottomSheet);
        mBottomSheetBehavior1.setHideable(true);
        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        entryGrid=(ExpandableHeightGridView)findViewById(R.id.entryGrid);
        numberOfTeamsGrid=(ExpandableHeightGridView)findViewById(R.id.numberOfTeamsGrid);
        prizePoolGrid=(ExpandableHeightGridView)findViewById(R.id.prizePoolGrid);
        contestTypeGrid=(ExpandableHeightGridView)findViewById(R.id.contestTypeGrid);

        entryGrid.setExpanded(true);
        numberOfTeamsGrid.setExpanded(true);
        contestTypeGrid.setExpanded(true);
        prizePoolGrid.setExpanded(true);

        entryList = new ArrayList<>();
        numofTeamsList =new ArrayList<>();
        contestTypeList =new ArrayList<>();
        prizePoolList = new ArrayList<>();

        for(int i =0; i< entryAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(entryAr[i]);

            entryList.add(ob);
        }

        for(int i =0; i< numofTeamsAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(numofTeamsAr[i]);

            numofTeamsList.add(ob);
        }

        for(int i =0; i< conTypeAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(conTypeAr[i]);

            contestTypeList.add(ob);
        }

        for(int i =0; i< prizePoolAr.length; i++){
            filterGetSet ob = new filterGetSet();
            ob.setSelected(false);
            ob.setValue(prizePoolAr[i]);

            prizePoolList.add(ob);
        }

        btnApply=(Button)findViewById(R.id.btnApply);
        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ApplyFilter();
            }
        });

        closeFilter=(TextView)findViewById(R.id.closeFilter);
        closeFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
                finish();
            }
        });

        resetFilter = (TextView)findViewById(R.id.resetFilter);
        resetFilter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                for(int i =0; i< entryList.size(); i++){
                    entryList.get(i).setSelected(false);
                }

                for(int i =0; i< numofTeamsList.size(); i++){
                    numofTeamsList.get(i).setSelected(false);
                }

                for(int i =0; i< contestTypeList.size(); i++){
                    contestTypeList.get(i).setSelected(false);
                }

                for(int i =0; i< prizePoolList.size(); i++){
                    prizePoolList.get(i).setSelected(false);
                }

                entryGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,entryList));
                numberOfTeamsGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,numofTeamsList));
                contestTypeGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,contestTypeList));
                prizePoolGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,prizePoolList));

                ApplyFilter();
            }
        });

        mBottomSheetBehavior1.setBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(View view, int i) {
                if(mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_COLLAPSED)
                    mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }

            @Override
            public void onSlide(View view, float v) {

//                mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        if(cd.isConnectingToInternet()) {
            progressDialog.show();
            getChallenges();
        } else {
            new AppUtils().NoInternet(AllChallengesActivity.this);
            finish();
        }
    }

    public void getChallenges(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<contestGetSet>> call = apiSeitemViewice.getAllContests(session.getUserId(),gv.getMatchKey());
        call.enqueue(new Callback<ArrayList<contestGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<contestGetSet>> call, Response<ArrayList<contestGetSet>> response) {

                Log.i("Contests",response.toString());
                Log.i("Contests",response.message().toString());

                if(response.code() == 200) {

                    list = response.body();
                    list1 = new ArrayList<>();

                    if(catid.equals("0"))
                        list1 = list;
                    else {
                        for (contestGetSet zz : list) {
                            if (zz.getCatid().equals(catid))
                                list1.add(zz);
                        }
                    }
                    ll.setAdapter(new contestsAdapter(AllChallengesActivity.this,list1));

                    if(catid.equals("0")){
                        entryGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,entryList));
                        numberOfTeamsGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,numofTeamsList));
                        contestTypeGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,contestTypeList));
                        prizePoolGrid.setAdapter(new FilterGridAdapter(AllChallengesActivity.this,prizePoolList));

                        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_EXPANDED);
                    }

                    if(progressDialog!=null)
                        progressDialog.dismiss();

                } else if(response.code() == 401) {
                    new AppUtils().Toast(AllChallengesActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    AlertDialog.Builder d = new AlertDialog.Builder(AllChallengesActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getChallenges();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<contestGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i("Contests",t.getMessage());
                Log.i("Contests",t.toString());
            }
        });
    }

    public void  ApplyFilter(){
        ArrayList<contestGetSet> list_1,list_2,list_3,list_4;

        list_1 = new ArrayList<>();
        list_2 = new ArrayList<>();
        list_3 = new ArrayList<>();
        list_4 = new ArrayList<>();

        list2 = new ArrayList<>();

        // entry fee
        if(entryList.get(0).isSelected()) {
            for (contestGetSet zz : list1) {
                if(Double.parseDouble(zz.getEntryfee()) <= 50)
                    list_1.add(zz);
            }
        }
        if(entryList.get(1).isSelected()) {
            for (contestGetSet zz : list1) {
                if(Double.parseDouble(zz.getEntryfee()) >= 51 && Double.parseDouble(zz.getEntryfee()) <= 100)
                    list_1.add(zz);
            }
        }
        if(entryList.get(2).isSelected()) {
            for (contestGetSet zz : list1) {
                if(Double.parseDouble(zz.getEntryfee()) >= 1001 && Double.parseDouble(zz.getEntryfee()) <= 1000)
                    list_1.add(zz);
            }
        }
        if(entryList.get(3).isSelected()) {
            for (contestGetSet zz : list1) {
                if(Double.parseDouble(zz.getEntryfee()) >1000)
                    list_1.add(zz);
            }
        }
        if(entryList.get(0).isSelected() == false && entryList.get(1).isSelected() == false && entryList.get(2).isSelected() == false && entryList.get(3).isSelected() == false)
            list_1 = list1;


        //num of teams
        if(numofTeamsList.get(0).isSelected()){
            for (contestGetSet zz : list_1) {
                if(zz.getMaximum_user() ==2)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(1).isSelected()){
            for (contestGetSet zz : list_1) {
                if(zz.getMaximum_user() >= 3 && zz.getMaximum_user() <= 10)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(2).isSelected()){
            for (contestGetSet zz : list_1) {
                if(zz.getMaximum_user() >= 11 && zz.getMaximum_user() <= 100)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(3).isSelected()){
            for (contestGetSet zz : list_1) {
                if(zz.getMaximum_user() >= 101 && zz.getMaximum_user() <= 1000)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(4).isSelected()){
            for (contestGetSet zz : list_1) {
                if(zz.getMaximum_user() > 1000)
                    list_2.add(zz);
            }
        }

        if(numofTeamsList.get(0).isSelected() == false
                && numofTeamsList.get(1).isSelected() == false
                && numofTeamsList.get(2).isSelected() == false
                && numofTeamsList.get(3).isSelected() == false
                && numofTeamsList.get(4).isSelected() == false)
            list_2 = list_1;


        //Prize Pool
        if(prizePoolList.get(0).isSelected()){
            for (contestGetSet zz : list_2) {
                if(zz.getWin_amount() >=1 && zz.getWin_amount() <= 10000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(1).isSelected()){
            for (contestGetSet zz : list_2) {
                if(zz.getWin_amount() >= 10001 && zz.getWin_amount() <= 100000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(2).isSelected()){
            for (contestGetSet zz : list_2) {
                if(zz.getWin_amount() >= 100001 && zz.getWin_amount() <= 1000000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(3).isSelected()){
            for (contestGetSet zz : list_2) {
                if(zz.getWin_amount() >= 1000001 && zz.getWin_amount() <= 2500000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(4).isSelected()){
            for (contestGetSet zz : list_2) {
                if(zz.getWin_amount() > 2500000)
                    list_3.add(zz);
            }
        }

        if(prizePoolList.get(0).isSelected() == false
                && prizePoolList.get(1).isSelected() == false
                && prizePoolList.get(2).isSelected() == false
                && prizePoolList.get(3).isSelected() == false
                && prizePoolList.get(4).isSelected() == false)
            list_3 = list_2;


        //Contest Type
        if(prizePoolList.get(0).isSelected()){
            for (contestGetSet zz : list_3) {
                if(zz.getMulti_entry() == 0)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(1).isSelected()){
            for (contestGetSet zz : list_3) {
                if(zz.getMulti_entry() == 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(2).isSelected()){
            for (contestGetSet zz : list_3) {
                if(zz.getTotalwinners() == 0 || zz.getTotalwinners() == 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(3).isSelected()){
            for (contestGetSet zz : list_3) {
                if(zz.getTotalwinners() > 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(4).isSelected()){
            for (contestGetSet zz : list_3) {
                if(zz.getConfirmed_challenge() == 1)
                    list_4.add(zz);
            }
        }

        if(prizePoolList.get(0).isSelected() == false
                && prizePoolList.get(1).isSelected() == false
                && prizePoolList.get(2).isSelected() == false
                && prizePoolList.get(3).isSelected() == false
                && prizePoolList.get(4).isSelected() == false)
            list_4 = list_3;

        mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);

        list2 = list_4;
        ll.setAdapter(new contestsAdapter(AllChallengesActivity.this,list2));
    }

    @Override
    public void onBackPressed() {
        if( mBottomSheetBehavior1.getState() == BottomSheetBehavior.STATE_EXPANDED)
            mBottomSheetBehavior1.setState(BottomSheetBehavior.STATE_HIDDEN);
        else
            finish();
    }

}
