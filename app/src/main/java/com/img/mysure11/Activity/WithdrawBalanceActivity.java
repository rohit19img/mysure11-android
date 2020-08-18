package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class WithdrawBalanceActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView winningBalance;
    TextInputLayout addMoney;
    TextView add10,add50,add100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_withdraw_balance);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("Withdraw Cash");

        winningBalance = findViewById(R.id.winningBalance);
        addMoney=(TextInputLayout)findViewById(R.id.addMoney);

        winningBalance.setText("₹"+getIntent().getExtras().getString("balance"));

        add10=(TextView)findViewById(R.id.add10);
        add50=(TextView)findViewById(R.id.add50);
        add100=(TextView)findViewById(R.id.add100);

        add10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    addMoney.getEditText().setText(String.valueOf((Integer.parseInt(addMoney.getEditText().getText().toString())+100)));
                }else{
                    addMoney.getEditText().setText("100");
                }
            }
        });

        add50.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    addMoney.getEditText().setText(String.valueOf((Integer.parseInt(addMoney.getEditText().getText().toString())+200)));
                }else{
                    addMoney.getEditText().setText("200");
                }
            }
        });

        add100.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    addMoney.getEditText().setText(String.valueOf((Integer.parseInt(addMoney.getEditText().getText().toString())+500)));
                }else{
                    addMoney.getEditText().setText("500");
                }
            }
        });

        findViewById(R.id.btnWithdraw).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    if(cd.isConnectingToInternet()){
                        if(Double.parseDouble(addMoney.getEditText().getText().toString()) >= 300)
                            AddAmount(addMoney.getEditText().getText().toString());
                        else
                            new AppUtils().showError(WithdrawBalanceActivity.this,"Minimum amount to withdraw ₹300");
                    }
                }else
                    new AppUtils().showError(WithdrawBalanceActivity.this,"Minimum amount to withdraw ₹300");
            }
        });

    }

    public void AddAmount(final String price){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"request_withdrow";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                if(jsonObject.getBoolean("status")) {
                                    new AppUtils().showSuccess(WithdrawBalanceActivity.this,jsonObject.getString("msg"));
                                }else{
                                    new AppUtils().showError(WithdrawBalanceActivity.this,jsonObject.getString("msg"));
                                }
                                 if(progressDialog!=null)
                                     progressDialog.dismiss();

                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

                            if(progressDialog!=null)
                                progressDialog.dismiss();
                        }
                    },
                    new com.android.volley.Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error)
                        {
                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                new AppUtils().Toast(WithdrawBalanceActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(WithdrawBalanceActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddAmount(price);
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        finish();
                                    }
                                });
                            }
                        }
                    })
            {

                @Override
                protected Map<String, String> getParams()
                {
                    HashMap<String,String> map=new HashMap<>();
                    map.put("amount",price);
                    map.put("withdrawFrom","cashfree");
                    Log.i("Params",map.toString());

                    return map;
                }

                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }
            };
            strRequest.setShouldCache(false);
            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

}
