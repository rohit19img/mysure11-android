package com.img.mysure11.CashFree;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
//import com.gocashfree.cashfreesdk.CFClientInterface;
import com.gocashfree.cashfreesdk.CFPaymentService;
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

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_APP_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_EMAIL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_NAME;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_CUSTOMER_PHONE;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_NOTIFY_URL;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_AMOUNT;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_ID;
import static com.gocashfree.cashfreesdk.CFPaymentService.PARAM_ORDER_NOTE;


public class PaymentActivity extends AppCompatActivity /*implements CFClientInterface*/ {

    String appid,secret,orderid,amount,phone,email,name,checksum;
    String orderNote = "Recharge Wallet";

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String type = "PROD";  // TEST or PROD
    boolean doUPI;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_cash_free);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        amount = getIntent().getExtras().getString("price");
        orderid = getIntent().getExtras().getString("orderid");
        doUPI = getIntent().getExtras().getBoolean("doUPI");
        phone = session.getMobile();
        email = session.getEmail();
        name = session.getTeamName();

        if(type.equals("TEST")) {
            appid = "16756af667d5ab61fb8d0456665761" ; //test
            secret = "9b8b3f256a8808a529bdccc8a879f47ddfaea375" ; // test

            CallVolley("https://test.cashfree.com/api/v2/cftoken/order");  // test
        } else if(type.equals("PROD")) {
            appid = "545538a5bbb45ef313ff51e7c35545"; //Live
            secret = "52bdbebf598434f351fcda9f973334ef053aa0c1"; // Live

            CallVolley("https://api.cashfree.com/api/v2/cftoken/order");  // Live
        }
    }

    JSONObject jsonObject = new JSONObject();

    public void CallVolley(final String a)
    {

        try {
            jsonObject.put("orderId",orderid);
            jsonObject.put("orderAmount",amount);
            jsonObject.put("orderCurrency","INR");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        Log.i("params",jsonObject.toString());

        progressDialog.show();
        try {

            JsonObjectRequest strRequest = new JsonObjectRequest(Request.Method.POST, a, jsonObject, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    try {
                        Log.i("Response is",response.toString());
                        progressDialog.show();

                        JSONObject jsonObject = new JSONObject(response.toString());

//                                orderid = jsonObject.getString("orderId");
                        checksum = jsonObject.getString("cftoken");

                        progressDialog.dismiss();
                        triggerPayment(doUPI);
                    }
                    catch (JSONException je)
                    {
                        je.printStackTrace();
                    }

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    NetworkResponse networkResponse = error.networkResponse;
                    if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                        // HTTP Status Code: 401 Unauthorized
                        new AppUtils().Toast(PaymentActivity.this,"Session Timeout");

                        session.logoutUser();
                        finishAffinity();
                    } else {
                        AlertDialog.Builder d = new AlertDialog.Builder(PaymentActivity.this);
                        d.setTitle("Something went wrong");
                        d.setCancelable(false);
                        d.setMessage("Something went wrong, Please try again");
                        d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                CallVolley(a);
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String,String> map = new HashMap<String, String>();
                    map.put("Content-Type","application/json");
                    map.put("x-client-id", appid);
                    map.put("x-client-secret", secret);

                    Log.e("MAP", map.toString());

                    return map;
                }
            };
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Toast.makeText(this, "--"+e, Toast.LENGTH_SHORT).show();
        }

    }


    private void triggerPayment(boolean isUpiIntent) {
        /*
         * token can be generated from your backend by calling cashfree servers. Please
         * check the documentation for details on generating the token.
         * READ THIS TO GENERATE TOKEN: https://bit.ly/2RGV3Pp
         */
        String token = checksum;


        /*
         * stage allows you to switch between sandboxed and production servers
         * for CashFree Payment Gateway. The possible values are
         *
         * 1. TEST: Use the Test server. You can use this service while integrating
         *      and testing the CashFree PG. No real money will be deducted from the
         *      cards and bank accounts you use this stage. This mode is thus ideal
         *      for use during the development. You can use the cards provided here
         *      while in this stage: https://docs.cashfree.com/docs/resources/#test-data
         *
         * 2. PROD: Once you have completed the testing and integration and successfully
         *      integrated the CashFree PG, use this value for stage variable. This will
         *      enable live transactions
         */
        String stage = type;

        /*
         * appId will be available to you at CashFree Dashboard. This is a unique
         * identifier for your app. Please replace this appId with your appId.
         * Also, as explained below you will need to change your appId to prod
         * credentials before publishing your app.
         */

        Map<String, String> params = new HashMap<>();

        params.put(PARAM_APP_ID, appid);
        params.put(PARAM_ORDER_ID, orderid);
        params.put(PARAM_ORDER_AMOUNT, amount);
        params.put(PARAM_ORDER_NOTE, orderNote);
        params.put(PARAM_CUSTOMER_NAME, name);
        params.put(PARAM_CUSTOMER_PHONE, phone);
        params.put(PARAM_CUSTOMER_EMAIL,email);
        params.put(PARAM_NOTIFY_URL,"https://mysure11.com/Mysure11/api/webhook_detail");

        Log.i("params",params.toString());

        for(Map.Entry entry : params.entrySet()) {
            Log.d("CFSKDSample", entry.getKey() + " " + entry.getValue());
        }

        CFPaymentService cfPaymentService = CFPaymentService.getCFPaymentServiceInstance();
        cfPaymentService.setOrientation(0);

        if (isUpiIntent) {
            // Use the following method for initiating UPI Intent Payments
//            cfPaymentService.upiPayment(this, params, token, this, stage);
            cfPaymentService.upiPayment(this, params, token,  stage);
        }
        else {
            // Use the following method for initiating regular Payments
//            cfPaymentService.doPayment(this, params, token, this, stage);
            cfPaymentService.doPayment(this, params, token,  stage);
        }
    }

    public void doPayment(View view) {
        triggerPayment(false);
    }

    public void upiPayment(View view) {
        triggerPayment(true);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("requestCode",String.valueOf(requestCode));
        Log.i("resultCode",String.valueOf(resultCode));
        Log.i("data",String.valueOf(data.toString()));

        if (data != null) {
            final Bundle bundle = data.getExtras();

            if(bundle != null) {

                for (String key : bundle.keySet()) {
                    if (bundle.getString(key) != null) {
                        Log.d("key value", key + " : " + bundle.getString(key));
                    }
                }

                if (bundle.getString("txStatus").equals("SUCCESS")) {
                    if (cd.isConnectingToInternet())
                        AddAmount(amount, bundle.getString("referenceId"), orderid, bundle.toString());
                    else {
                        AlertDialog.Builder d = new AlertDialog.Builder(PaymentActivity.this);
                        d.setMessage("Internet Connection lost");
                        d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                AddAmount(amount, bundle.getString("referenceId"), orderid, bundle.toString());
                            }
                        });
                        d.show();
                    }
                } else if(bundle.getString("txStatus").equals("FAILED")){
                    new AppUtils().Toast(PaymentActivity.this,bundle.getString("txMsg"));
                    finish();
                } else if(bundle.getString("txStatus").equals("CANCELLED")){
                    new AppUtils().Toast(PaymentActivity.this,"Transaction Canceled");
                    finish();
                } else {
                    if(bundle.getString("txMsg") != null) {
                        new AppUtils().showError(PaymentActivity.this, bundle.getString("txMsg"));
                        progressDialog.dismiss();
                    } else {
                        new AppUtils().showError(PaymentActivity.this, "Transaction Canceled");
                        finish();
                    }
                }
            } else
                finish();
        }

    }

//    CFPaymentService.REQ_CODE java

/*
    @Override
    public void onSuccess(final Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Success");
        Log.i("map",map.toString());

        if(map.get("txStatus").equals("SUCCESS")){
            if(cd.isConnectingToInternet())
                AddAmount(amount, map.get("referenceId"),orderid,map.toString());
            else{
                AlertDialog.Builder d= new AlertDialog.Builder(PaymentActivity.this);
                d.setMessage("Internet Connection lost");
                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        AddAmount(amount, map.get("referenceId"),orderid,map.toString());
                    }
                });
                d.show();
            }
        }else{
            new AppUtils().showError(PaymentActivity.this,map.get("txMsg"));
        }
    }

    // Success :  {txTime=, txMsg=Transaction Successful, referenceId=, paymentMode=, signature=, orderAmount=1.00, txStatus=SUCCESS, orderId=}

    @Override
    public void onFailure(Map<String, String> map) {
        Log.d("CFSDKSample", "Payment Failure");
        Log.i("map",map.toString());
//        finish();
        AddAmount(amount, "-",orderid,map.toString());
    }

    @Override
    public void onNavigateBack() {
        finish();
    }
*/

    @Override
    public void onBackPressed() {
        finish();
    }

    public void AddAmount(final String Amount, final String txnid,final String returnid, final String response){
       progressDialog.dismiss();
        try {

            String url = getResources().getString(R.string.app_url)+"addcash1?amount="+Amount+"&paymentby=cashFree&txnid="+txnid+"&returnid="+returnid+"&response="+response;
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

//                                startActivity(new Intent(PaymentActivity.this,AddBalanceActivity.class));

                                new AppUtils().showSuccess(PaymentActivity.this,jsonObject.getString("msg"));
                                progressDialog.dismiss();
                                finish();
                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            }

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
                                new AppUtils().Toast(PaymentActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(PaymentActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        AddAmount(Amount,txnid,returnid,response);
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
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("Authorization", session.getUserId());
                    Log.i("Header",params.toString());

                    return params;
                }
            };
            strRequest.setShouldCache(false);
            requestQueue.add(strRequest);
        }
        catch (Exception e) {
            Log.i("Exception",e.getMessage());
        }

    }

}

