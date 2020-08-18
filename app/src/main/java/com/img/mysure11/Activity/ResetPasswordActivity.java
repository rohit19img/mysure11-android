package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;
import cn.pedant.SweetAlert.SweetAlertDialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ResetPasswordActivity extends AppCompatActivity {

    Button submit;
    EditText password,cpassword;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_password);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setVisibility(View.GONE);

        TextView title = (TextView)findViewById(R.id.title);
        title.setText("Reset password");

        cpassword=(EditText)findViewById(R.id.cpassword);
        password=(EditText)findViewById(R.id.password);

        submit= (Button)findViewById(R.id.submit);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                register();
            }
        });
    }

    public void register(){
        String cpass,pass;

        cpass= cpassword.getText().toString();
        pass= password.getText().toString();

        if(!validPassword())
            password.setError("Please enter valid password with Min 4 character");
        else if(!pass.equals(cpass))
            cpassword.setError("Password & Confirm password not matched");
        else{
            if (cd.isConnectingToInternet()) {
                Signup();

                Intent ii = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                startActivity(ii);
            } else {
                new SweetAlertDialog(ResetPasswordActivity.this, SweetAlertDialog.ERROR_TYPE)
                        .setTitleText("No Internet Connection")
                        .setContentText("You don't have internet connection")
                        .show();
            }
        }
    }

    public boolean validPassword(){
//        String pattern = "(?=^.{8,}$)((?=.*\\d)|(?=.*\\W+))(?![.\\n])(?=.*[A-Za-z]).*$";
        if(password.getText().length() > 4)
            return true;
        else
            return false;
    }


    public void Signup(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"resetpassword";
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
                                    Intent ii = new Intent(ResetPasswordActivity.this,LoginActivity.class);
                                    startActivity(ii);
                                    finishAffinity();
                                }

//                                ma.showToast(ResetPasswordActivity.this,jsonObject.getString("msg"));

                                                if(progressDialog!=null)
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
                                new AppUtils().Toast(ResetPasswordActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(ResetPasswordActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Signup();
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
                    params.put("token", getIntent().getExtras().getString("token"));
                    params.put("password", password.getText().toString());
                    params.put("code",getIntent().getExtras().getString("code"));
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
