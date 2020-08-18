package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.withdrwalsGetSet;
import com.img.mysure11.R;

import java.util.ArrayList;

public class MyWithdwralsActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ExpandableListView transactionList;
    LinearLayout noTransactionLL;
    ArrayList<withdrwalsGetSet> Translist;
    String TAG="Wallet";
    private int lastExpandedPosition = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_withdwrals);

        TextView title= (TextView)findViewById(R.id.title);
        title.setText("My Withdrawals");

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        transactionList =  findViewById(R.id.transactionList);
        noTransactionLL=(LinearLayout)findViewById(R.id.noTransactionLL);

        transactionList.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {

            @Override
            public void onGroupExpand(int groupPosition) {
                if (lastExpandedPosition != -1
                        && groupPosition != lastExpandedPosition) {
                    transactionList.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;
            }
        });

        if(cd.isConnectingToInternet())
            MyTransaction();
        else {
            new AppUtils().NoInternet(this);
            finish();
        }
    }

    public void MyTransaction(){
        progressDialog.show();

        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);
        Call<ArrayList<withdrwalsGetSet>> call = apiSeitemViewice.mywithdrawlist(session.getUserId());
        call.enqueue(new Callback<ArrayList<withdrwalsGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<withdrwalsGetSet>> call, Response<ArrayList<withdrwalsGetSet>> response) {

                Log.i(TAG, "Number of movies received: complete");
                Log.i(TAG, "Number of movies received: " + response.toString());
                if(response.code() == 200) {
                    Log.i(TAG, "Number of movies received: " + String.valueOf(response.body().size()));

                    Translist = response.body();
                    if (Translist.size() == 0) {
                        transactionList.setVisibility(View.GONE);
                        noTransactionLL.setVisibility(View.VISIBLE);
                    } else {
                        transactionList.setAdapter(new TransactionAdapter(MyWithdwralsActivity.this, Translist));
                    }
                    progressDialog.dismiss();
                } else if(response.code() == 401) {
                    new AppUtils().Toast(MyWithdwralsActivity.this,"session Timeout");
                    if(progressDialog != null && progressDialog.isShowing())
                        progressDialog.dismiss();
                    session.logoutUser();
                    finishAffinity();
                } else {
                    Log.i(TAG, "Responce code " + response.code());

                    AlertDialog.Builder d = new AlertDialog.Builder(MyWithdwralsActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            MyTransaction();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            progressDialog.dismiss();
                        }
                    });
                }
            }

            @Override
            public void onFailure(Call<ArrayList<withdrwalsGetSet>>call, Throwable t) {
                // Log error here since request failed
                Log.i(TAG, t.toString());
                progressDialog.dismiss();
            }
        });
    }

    public class TransactionAdapter extends BaseExpandableListAdapter {

        Context context;
        ArrayList<withdrwalsGetSet> list;

        public TransactionAdapter(Context context, ArrayList<withdrwalsGetSet> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getGroupCount() {
            return list.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return 1;
        }

        @Override
        public Object getGroup(int groupPosition) {
            return null;
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return null;
        }

        @Override
        public long getGroupId(int groupPosition) {
            return 0;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return 0;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
            View v;

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.transaction_list_parent,null);

            TextView date,type,amount;
            ImageView expand;

            date = v.findViewById(R.id.date);
            type = v.findViewById(R.id.type);
            amount = v.findViewById(R.id.amount);
            expand = v.findViewById(R.id.expand);

            date.setText(list.get(groupPosition).getWithdrawl_date());
            type.setText(list.get(groupPosition).getStatus());
            amount.setText("₹"+list.get(groupPosition).getAmount());

            return v;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
            View v;

            LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.transaction_list_child,null);

            TextView transactionID = v.findViewById(R.id.transactionID);
            TextView teamName = v.findViewById(R.id.teamName);

            transactionID.setText("Transaction id : "+list.get(groupPosition).getWithdrawtxnid());
            teamName.setText("Payment Status : "+list.get(groupPosition).getStatus());

            return v;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return false;
        }
    }
}
