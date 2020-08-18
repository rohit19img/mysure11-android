package com.img.mysure11.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.material.textfield.TextInputLayout;
import com.img.mysure11.Activity.OTPActivity;
import com.img.mysure11.Activity.VerificationActivity;
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

public class mobileVarificationFragment extends Fragment {

    Context context;

    CardView mobileVerify,mobileVerified,emailVerify,emailVerified;
    TextInputLayout mobileNumber,email;
    TextView verifyMobile,mobileText,verifyEmail,emailText;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    public mobileVarificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_mobile_varification, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        mobileVerify = v.findViewById(R.id.mobileVerify);
        mobileVerified = v.findViewById(R.id.mobileVerified);
        emailVerify = v.findViewById(R.id.emailVerify);
        emailVerified = v.findViewById(R.id.emailVerified);

        mobileNumber = v.findViewById(R.id.mobileNumber);
        email = v.findViewById(R.id.email);

        verifyMobile = v.findViewById(R.id.verifyMobile);
        mobileText = v.findViewById(R.id.mobileText);
        verifyEmail = v.findViewById(R.id.verifyEmail);
        emailText = v.findViewById(R.id.emailText);

        verifyMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new AppUtils().isValidNumber(mobileNumber.getEditText().getText().toString())){
                    mobileNumber.setError("Please enter valid mobile number");
                } else{
                    progressDialog.show();
                    verifyMobile();
                }
            }
        });

        verifyEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!new AppUtils().isValidEmail(email.getEditText().getText().toString())){
                    email.setError("Please enter valid email address");
                } else{
                    progressDialog.show();
                    verifyEmail();
                }
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if(session.isMobileVerified()){
            mobileVerified.setVisibility(View.VISIBLE);
            mobileVerify.setVisibility(View.GONE);

            mobileText.setText(session.getMobile());
        } else {
            mobileVerified.setVisibility(View.GONE);
            mobileVerify.setVisibility(View.VISIBLE);
        }

        if(session.isEmailVerified()){
            emailVerified.setVisibility(View.VISIBLE);
            emailVerify.setVisibility(View.GONE);

            emailText.setText(session.getEmail());
        } else {
            emailVerified.setVisibility(View.GONE);
            emailVerify.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            if(session.isMobileVerified()){
                mobileVerified.setVisibility(View.VISIBLE);
                mobileVerify.setVisibility(View.GONE);

                mobileText.setText(session.getMobile());
            } else {
                mobileVerified.setVisibility(View.GONE);
                mobileVerify.setVisibility(View.VISIBLE);
            }

            if(session.isEmailVerified()){
                emailVerified.setVisibility(View.VISIBLE);
                emailVerify.setVisibility(View.GONE);

                emailText.setText(session.getEmail());
            } else {
                emailVerified.setVisibility(View.GONE);
                emailVerify.setVisibility(View.VISIBLE);
            }
        }
    }

    public void verifyEmail(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"verifyEmail";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                                            if(progressDialog!=null)
                    progressDialog.dismiss();

                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                startActivity(
                                        new Intent(context, OTPActivity.class)
                                                .putExtra("from","email_verify")
                                                .putExtra("mobile",email.getEditText().getText().toString())
                                                .putExtra("Auth",session.getUserId())
                                );
                            }
                            catch (JSONException je)
                            {
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
                                new AppUtils().Toast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        verifyEmail();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity)context).finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(context);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        verifyEmail();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ((Activity)context).finish();
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

                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("email", email.getEditText().getText().toString());
                    Log.i("params",params.toString());

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

    public void verifyMobile(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"verifyMobileNumber";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                                            if(progressDialog!=null)
                    progressDialog.dismiss();

                            try {
                                Log.i("Response is",response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                startActivity(
                                        new Intent(context, OTPActivity.class)
                                        .putExtra("from","mobile_verify")
                                        .putExtra("mobile",mobileNumber.getEditText().getText().toString())
                                        .putExtra("Auth",session.getUserId())
                                );
                            }
                            catch (JSONException je)
                            {
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
                                new AppUtils().Toast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(context);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        verifyEmail();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ((Activity)context).finish();
                                    }
                                });
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(context);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        verifyEmail();
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        ((Activity)context).finish();
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

                @Override
                public Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
//                        params.put("Content-Type", "application/json; charset=UTF-8");
                    params.put("mobile", mobileNumber.getEditText().getText().toString());
                    Log.i("params",params.toString());

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
