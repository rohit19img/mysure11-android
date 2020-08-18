package com.img.mysure11.Activity;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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

public class OffersActivity extends AppCompatActivity {

    ListView offersList;
    ArrayList<offersGetSet> list;

    EditText offerCode;
    TextView btnApply;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_offers);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(
                        new Intent(OffersActivity.this,AddBalanceActivity.class)
                                .putExtra("balance",getIntent().getExtras().getString("balance"))
                                .putExtra("offerCode","")
                );
                finish();
            }
        });

        TextView title = findViewById(R.id.title);
        title.setText("Choose your offer");

        offersList = findViewById(R.id.offersList);

        offerCode = findViewById(R.id.offerCode);
        btnApply = findViewById(R.id.btnApply);

        btnApply.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(offerCode.getText().toString().equals("")){
                    new AppUtils().showError(OffersActivity.this,"Please enter your offer code here");
                } else {
                    checkpromocode();
                }
            }
        });

        if(cd.isConnectingToInternet())
            getOffers();
        else{
            new AppUtils().NoInternet(OffersActivity.this);
            finish();
        }
    }

    @Override
    public void onBackPressed() {
        startActivity(
                new Intent(OffersActivity.this,AddBalanceActivity.class)
                .putExtra("balance",getIntent().getExtras().getString("balance"))
                .putExtra("offerCode","")
        );
        finish();
    }

    public void getOffers(){
        ApiInterface apiSeitemViewice = ApiClient.getClient().create(ApiInterface.class);

        Call<ArrayList<offersGetSet>> call = apiSeitemViewice.offers(session.getUserId());
        call.enqueue(new Callback<ArrayList<offersGetSet>>() {

            @Override
            public void onResponse(Call<ArrayList<offersGetSet>> call, Response<ArrayList<offersGetSet>> response) {

                Log.i("Match",response.toString());
                Log.i("Match",response.message());

                if(response.code() == 200) {

                    list = response.body();

                    offersList.setAdapter(new adapter(OffersActivity.this,list));

                }else {
                    AlertDialog.Builder d = new AlertDialog.Builder(OffersActivity.this);
                    d.setTitle("Something went wrong");
                    d.setCancelable(false);
                    d.setMessage("Something went wrong, Please try again");
                    d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getOffers();
                        }
                    });
                    d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                }
            }
            @Override
            public void onFailure(Call<ArrayList<offersGetSet>>call, Throwable t) {
                // Log error here since request failed
            }
        });
    }

    class adapter extends BaseAdapter{

        Context context;
        ArrayList<offersGetSet> list;

        public adapter(Context context, ArrayList<offersGetSet> list) {
            this.context = context;
            this.list = list;
        }

        @Override
        public int getCount() {
            return list.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int i, View convertView, ViewGroup parent) {

            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View v = inflater.inflate(R.layout.offer_list_single,null);

            TextView offerCode,btnApply,offerTitle,offerDescription;

            offerCode = v.findViewById(R.id.offerCode);
            btnApply = v.findViewById(R.id.btnApply);
            offerTitle = v.findViewById(R.id.offerTitle);
            offerDescription = v.findViewById(R.id.offerDescription);

            offerCode.setText(list.get(i).getOffercode());
            if(list.get(i).getBonus_type().equals("rs"))
                offerTitle.setText("Earn ₹"+list.get(i).getBonus()+" Bonus");
            else
                offerTitle.setText("Earn "+list.get(i).getBonus()+"% Bonus");
            offerDescription.setText(list.get(i).getDescription());

            btnApply.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(
                            new Intent(OffersActivity.this,AddBalanceActivity.class)
                                    .putExtra("balance",getIntent().getExtras().getString("balance"))
                                    .putExtra("offerCode",list.get(i).getOffercode())
                    );
                    ((Activity)context).finish();

                }
            });

            return v;
        }
    }

    public void checkpromocode(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"checkpromocode";
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

                                if(jsonObject.getInt("status") == 1) {
                                    startActivity(
                                            new Intent(OffersActivity.this,AddBalanceActivity.class)
                                                    .putExtra("balance",getIntent().getExtras().getString("balance"))
                                                    .putExtra("offerCode",offerCode.getText().toString())
                                    );
                                    finish();
                                }else{
                                    new AppUtils().showError(OffersActivity.this,jsonObject.getString("msg"));
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
                                new AppUtils().Toast(OffersActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(OffersActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        checkpromocode();
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
                    map.put("promocode",offerCode.getText().toString().toUpperCase());
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
