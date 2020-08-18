package com.img.mysure11.Static;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
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
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.referFriendsGetSet;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class InviteFriendsActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextView code,more,totalJoined;
    String inviteText="";
    ArrayList<referFriendsGetSet> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invite_friends);

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
        title.setText("Refer & Earn");

        code = (TextView)findViewById(R.id.code);
        code.setText(session.getReferalCode());

        more = (TextView)findViewById(R.id.more);
        more.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String shareBody = "Think you can challenge me on "+getString(R.string.app_name)+"? Tap \n" +
                        "to download the app & use my invite code "+session.getReferalCode()+" to get a Cash Bonus of Rs.100! Let the games begin" +
                        "\nDownload Application from "+ getResources().getString(R.string.apk_url);


                Intent sharingIntent = new Intent(Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");

                sharingIntent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Share via"));
            }
        });

        totalJoined = findViewById(R.id.totalJoined);

        if(cd.isConnectingToInternet())
            FindJoined();

    }

    public void FindJoined(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"getreferuser";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.GET, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                JSONArray jsonArray = new JSONArray(response.toString());

                                totalJoined.setText(jsonArray.length()+" Friends Joined");
                                if(jsonArray.length() > 0){

                                    list = new ArrayList<>();

                                    for(int i = 0; i< jsonArray.length();i++){
                                        referFriendsGetSet ob = new referFriendsGetSet();
                                        JSONObject job = jsonArray.getJSONObject(i);

                                        ob.setId(job.getString("id"));
                                        ob.setUsername(job.getString("username"));
                                        ob.setEmail(job.getString("email"));
                                        ob.setAmount(job.getString("amount"));
                                        ob.setImage(job.getString("image"));
                                        ob.setCreated_at(job.getString("created_at"));
                                        ob.setSuccess(job.getBoolean("success"));

                                        list.add(ob);
                                    }

                                    findViewById(R.id.joinedLL).setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {

                                        }
                                    });
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
                                new AppUtils().Toast(InviteFriendsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(InviteFriendsActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FindJoined();
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
