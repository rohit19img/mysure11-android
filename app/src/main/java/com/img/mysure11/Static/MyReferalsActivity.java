package com.img.mysure11.Static;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import de.hdodenhof.circleimageview.CircleImageView;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
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
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.GetSet.referFriendsGetSet;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MyReferalsActivity extends AppCompatActivity {

    RecyclerView usersRV;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    ArrayList<referFriendsGetSet> list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_referals);

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
        title.setText("My Referrals");

        usersRV = findViewById(R.id.usersRV);
        usersRV.setLayoutManager(new LinearLayoutManager(this));

        if(cd.isConnectingToInternet())
            FindJoined();
    }

    public void FindJoined(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"youtuber_bonus";
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
                                        ob.setMobile(job.getString("mobile"));
                                        ob.setSuccess(job.getBoolean("success"));

                                        list.add(ob);
                                    }

                                    usersRV.setAdapter(new adapter(MyReferalsActivity.this,list));
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
                                new AppUtils().Toast(MyReferalsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(MyReferalsActivity.this);
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

    class adapter extends RecyclerView.Adapter<adapter.MyViewHolder>{

        Context context;
        ArrayList<referFriendsGetSet> list;

        public adapter(Context context, ArrayList<referFriendsGetSet> list) {
            this.context = context;
            this.list = list;
        }

        @NonNull
        @Override
        public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            return new MyViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.refer_users_list,parent,false));
        }

        @Override
        public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

            holder.UserName.setText(list.get(position).getUsername());
            holder.mobileNumber.setText(list.get(position).getMobile());
            holder.amountEarned.setText("₹"+list.get(position).getAmount());

            if(!list.get(position).getImage().equals(""))
                Picasso.with(context).load(list.get(position).getImage()).placeholder(R.drawable.avtar).resize(100,100).into(holder.userImage);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        class MyViewHolder extends RecyclerView.ViewHolder{

            CircleImageView userImage;
            TextView UserName,mobileNumber,amountEarned;

            public MyViewHolder(View v){
                super(v);

                userImage = v.findViewById(R.id.userImage);
                UserName= v.findViewById(R.id.UserName);
                mobileNumber = v.findViewById(R.id.mobileNumber);
                amountEarned = v.findViewById(R.id.amountEarned);
            }
        }
    }
}
