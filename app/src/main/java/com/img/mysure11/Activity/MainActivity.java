package com.img.mysure11.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.login.Login;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.messaging.FirebaseMessaging;
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
import java.util.concurrent.Executor;

public class MainActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    CoordinatorLayout coordinator;

    String[] perarr = new String[]{
            Manifest.permission.INTERNET,
            Manifest.permission.ACCESS_NETWORK_STATE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        coordinator = findViewById(R.id.coordinator);

        snack();
    }

    public void snack(){

        if(cd.isConnectingToInternet()){
            FirebaseMessaging.getInstance().subscribeToTopic("All");
            if (checkPermission()) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getAppVersionCheck();
                    }
                }, 2000);
            } else {
                ActivityCompat.requestPermissions(MainActivity.this, perarr, 1);
            }
        } else{
            Snackbar s = Snackbar.make(coordinator,"No Internet Connection!", Snackbar.LENGTH_INDEFINITE);
            s.setAction("Retry", new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    snack();
                }
            });
            s.show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(grantResults.length==perarr.length)
        {
//            snack();
            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    getAppVersionCheck();
                }
            }, 2);
        }
    }

    private boolean checkPermission() {
        String permission1 = "android.permission.INTERNET";
        int res1 = getApplicationContext().checkCallingOrSelfPermission(permission1);

        String permission2 = "android.permission.ACCESS_NETWORK_STATE";
        int res2 = getApplicationContext().checkCallingOrSelfPermission(permission2);

        String permission3 = "android.permission.READ_EXTERNAL_STORAGE";
        int res3 = getApplicationContext().checkCallingOrSelfPermission(permission3);

        String permission4 = "android.permission.WRITE_EXTERNAL_STORAGE";
        int res4 = getApplicationContext().checkCallingOrSelfPermission(permission4);

        if(
                res1 == PackageManager.PERMISSION_GRANTED
                        && res2== PackageManager.PERMISSION_GRANTED
                        && res3== PackageManager.PERMISSION_GRANTED
                        && res4== PackageManager.PERMISSION_GRANTED
        )
            return (true);
        else
            return false;
    }

    private void getAppVersionCheck() {
        try {
            String url = getResources().getString(R.string.app_url)+"getversion";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response).getJSONObject(0);

                                int version = jsonObject.getInt("status");
                                int versionCode = getPackageManager().getPackageInfo(getPackageName(), 0).versionCode;
                                if(version > versionCode) {
                                    Intent intent = new Intent(MainActivity.this, UpdateActivity.class);
                                    intent.putExtra("points", jsonObject.getString("point"));
                                    startActivity(intent);
                                    finishAffinity();
                                } else {
                                    if(session.isUserLoggedIn()) {
                                        UserDetails();
                                    } else {
                                        startActivity(new Intent(MainActivity.this, WelcomeActivity.class));
                                        finishAffinity();
                                    }
                                }

                            }
                            catch (JSONException je)
                            {
                                je.printStackTrace();
                            } catch (PackageManager.NameNotFoundException e) {
                                e.printStackTrace();
                            }
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
                                //logout
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MainActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again.");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        getAppVersionCheck();
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

    public void UserDetails(){
        try {

            String url = getResources().getString(R.string.app_url)+"userfulldetails";
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

                                session.setEmail(jsonObject.getString("email"));
                                session.setName(jsonObject.getString("username"));
                                session.setMobile(jsonObject.getString("mobile"));
                                session.setDob(jsonObject.getString("dob"));
                                session.setImage(jsonObject.getString("image"));
                                session.setWallet_amount(jsonObject.getString("walletamaount"));
                                session.setTeamName(jsonObject.getString("team"));
                                session.setReferalCode(jsonObject.getString("refer_code"));
                                session.setState(jsonObject.getString("state"));
                                if(jsonObject.getInt("verified") ==1)
                                    session.setVerified(true);
                                else
                                    session.setVerified(false);

                                startActivity(new Intent(MainActivity.this,HomeActivity.class));

                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(MainActivity.this,LoginActivity.class));
                                    finishAffinity();
                                }
                                finishAffinity();
                            }
                            catch (JSONException je)
                            {

                                try {
                                    JSONObject job = new JSONObject(response.toString());
                                    if(job.has("status")){
                                        if(job.getInt("status") == 401){
                                            new AppUtils().Toast(MainActivity.this,"Session Timeout");

                                            session.logoutUser();
                                            finishAffinity();
                                        }
                                    }

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }

                                je.printStackTrace();
                            }
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
                                new AppUtils().Toast(MainActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MainActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserDetails();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(MainActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        UserDetails();
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
