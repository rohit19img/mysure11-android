package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
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
import com.img.mysure11.Api.ApiClient;
import com.img.mysure11.Api.ApiInterface;
import com.img.mysure11.CashFree.PaymentActivity;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.offersGetSet;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class AddBalanceActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView currentBalance,applied,referCode;
    TextInputLayout addMoney;
    TextView add10,add50,add100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_balance);

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
        title.setText("Add Cash");

        currentBalance=(TextView) findViewById(R.id.currentBalance);
        addMoney=(TextInputLayout)findViewById(R.id.addMoney);
        referCode = findViewById(R.id.referCode);
        applied = findViewById(R.id.applied);

        add10=(TextView)findViewById(R.id.add10);
        add50=(TextView)findViewById(R.id.add50);
        add100=(TextView)findViewById(R.id.add100);

        currentBalance.setText("₹"+getIntent().getExtras().getString("balance"));

        referCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(AddBalanceActivity.this,OffersActivity.class)
                        .putExtra("balance",getIntent().getExtras().getString("balance"))
                );
                finish();
            }
        });

        if(getIntent().hasExtra("offerCode")){
            if (!getIntent().getExtras().getString("offerCode").equals("")){
                referCode.setText(getIntent().getExtras().getString("offerCode"));
                referCode.setEnabled(false);
            }
        }

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

        findViewById(R.id.btnUpi).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    if(cd.isConnectingToInternet()){
                        AddAmount(addMoney.getEditText().getText().toString(), "cashfree",true);
                    }
                }
            }
        });

        findViewById(R.id.btnOther).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!addMoney.getEditText().getText().toString().equals("")){
                    if(cd.isConnectingToInternet()){
                        AddAmount(addMoney.getEditText().getText().toString(), "cashfree",false);
                    }
                }
            }
        });
    }

    public void AddAmount(final String price, final String from, final boolean doUPI){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"requestaddcash";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONObject(response.toString());

                                if(jsonObject.getBoolean("success")) {
                                    String txnId = jsonObject.getString("txnid");
                                    Intent i = new Intent(AddBalanceActivity.this, PaymentActivity.class);
                                    i.putExtra("price", price);
                                    i.putExtra("doUPI",doUPI);
                                    i.putExtra("orderid", txnId);
                                    startActivity(i);
                                    finish();
                                }else{
                                    new AppUtils().showError(AddBalanceActivity.this,jsonObject.getString("message"));
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
                                new AppUtils().Toast(AddBalanceActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(AddBalanceActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddAmount(price,from,doUPI);
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
                    map.put("paymentby",from);
                    map.put("offerid",referCode.getText().toString().toUpperCase());
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
