package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
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

public class ChangePassowrdActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    Button submit;
    TextInputLayout password,cpassword,oldpassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_passowrd);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Change password");

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        cpassword=(TextInputLayout)findViewById(R.id.cpassword);
        password=(TextInputLayout)findViewById(R.id.password);
        oldpassword=(TextInputLayout)findViewById(R.id.oldpassword);

        submit= (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Verify();
            }
        });

    }

    public void Verify(){
        String cpass,pass,opass;

        opass= oldpassword.getEditText().getText().toString();
        cpass= cpassword.getEditText().getText().toString();
        pass= password.getEditText().getText().toString();

        if(opass.length()<1)
            oldpassword.setError("please enter your old password");
        if(pass.length()<8)
            password.setError("Password should be more than 8 char long");
        else if(!new AppUtils().isValidPassword(password.getEditText().getText().toString()))
            password.setError("Please enter valid password with Min 8 character & 1 number/symbol");
        else if(!pass.equals(cpass))
            cpassword.setError("Password & Confirm password not matched");
        else{
            if (cd.isConnectingToInternet()) {
                ChangePassword();
            } else {
                new SweetAlertDialog(ChangePassowrdActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("No Internet Connection")
                        .setContentText("You don't have internet connection")
                        .show();
            }
        }
    }

    public void ChangePassword(){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"changepassword";
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
                                    session.setUserID("bearer " + jsonObject.getString("auth_key"));
                                    finish();
                                }
                                new AppUtils().showError(ChangePassowrdActivity.this,jsonObject.getString("msg"));
                                progressDialog.dismiss();
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
                                new AppUtils().Toast(ChangePassowrdActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(ChangePassowrdActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        ChangePassword();
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
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("oldpassword", oldpassword.getEditText().getText().toString());
                    params.put("newpassword", password.getEditText().getText().toString());
                    params.put("confirmpassword", cpassword.getEditText().getText().toString());
                    Log.i("params",params.toString());

                    return params;
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
