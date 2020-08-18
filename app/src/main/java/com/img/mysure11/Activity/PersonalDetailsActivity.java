package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
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
import com.img.mysure11.Adapter.SpinnerAdapter;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.R;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

public class PersonalDetailsActivity extends AppCompatActivity {

    Spinner stateSpinner;
    TextView title,btnSave,dob;
    String [] stateAr;
    ImageView back;

    TextInputLayout name,email,mobile,address,pincode;
    RadioButton male,female;
    RadioGroup gender;
    String TAG="Profile";
    String name1,email1,gender1,state1;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_details);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        title=(TextView)findViewById(R.id.title);
        title.setText("Edit Details");

        back=(ImageView) findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        stateAr= new String[getResources().getStringArray(R.array.india_states).length];
        stateAr= getResources().getStringArray(R.array.india_states);

        stateSpinner=(Spinner)findViewById(R.id.stateSpinner);
        stateSpinner.setAdapter(new SpinnerAdapter(this,stateAr));

        gender=(RadioGroup)findViewById(R.id.genderRg);
        male=(RadioButton)findViewById(R.id.maleRb);
        female=(RadioButton)findViewById(R.id.femaleRb);

        name=(TextInputLayout)findViewById(R.id.name);
        email=(TextInputLayout)findViewById(R.id.email);
        mobile=(TextInputLayout)findViewById(R.id.mobile);
        pincode=(TextInputLayout)findViewById(R.id.pincode);
        address=(TextInputLayout)findViewById(R.id.address);
        dob=(TextView)findViewById(R.id.dob);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(dob);
            }
        });

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                state1=stateAr[i];
//                ((TextView) stateSpinner.getSelectedView()).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        mobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!session.isMobileVerified()) {
                    startActivity(new Intent(PersonalDetailsActivity.this,VerificationActivity.class));
                }else{
                    new AppUtils().showSuccess(PersonalDetailsActivity.this,"You cannot change this number as it already been verified");
                }
            }
        });

        btnSave=(TextView)findViewById(R.id.btnSave);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                name1= name.getEditText().getText().toString();
                email1= email.getEditText().getText().toString();

                if(male.isChecked())
                    gender1="male";
                else
                    gender1= "female";

                if(name1.length()<4)
                    name.setError("Please enter valid name");
                else if(email1.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email1).matches())
                    email.setError("Please enter valid email address");
                else if(state1.equals("Select State"))
                {
                    new AppUtils().showError(PersonalDetailsActivity.this,"Please select your state");
                }
                else{
                    EditProfile();
                }
            }
        });

        if(cd.isConnectingToInternet()) {
            Details();
        } else
            new AppUtils().NoInternet(this);

    }

    public void pickDate(final TextView dialog) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(PersonalDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                String  d=(selectedmonth+1)+"/"+selectedday+"/"+selectedyear;
                dialog.setText(d);
            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - (5.681e+11)));
        mDatePicker.setTitle("Select Birth Date");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDatePicker.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        }
        mDatePicker.show();
    }

    public void Details(){
       progressDialog.show();
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

                                                if(progressDialog!=null)
                    progressDialog.dismiss();


                                session.setEmail(jsonObject.getString("email"));
                                session.setName(jsonObject.getString("username"));
                                session.setMobile(jsonObject.getString("mobile"));
                                session.setDob(jsonObject.getString("dob"));
                                session.setImage(jsonObject.getString("image"));
                                session.setWallet_amount(jsonObject.getString("walletamaount"));
                                session.setState(jsonObject.getString("state"));
                                session.setTeamName(jsonObject.getString("team"));
//                                session.setReferalCode(jsonObject.getString("refer_code"));
                                if(jsonObject.getInt("verified") ==1)
                                    session.setVerified(true);
                                else
                                    session.setVerified(false);

                                name.getEditText().setText(jsonObject.getString("username"));
                                email.getEditText().setText(jsonObject.getString("email"));
                                mobile.getEditText().setText(jsonObject.getString("mobile"));
                                dob.setText(jsonObject.getString("dob"));
                                address.getEditText().setText(jsonObject.getString("address"));
                                pincode.getEditText().setText(jsonObject.getString("pincode"));

                                if(jsonObject.getString("gender").equals("male"))
                                    male.setChecked(true);
                                else if(jsonObject.getString("gender").equals("female"))
                                    female.setChecked(true);

                                for (int i = 0; i < stateAr.length; i++) {
                                    if (stateAr[i].equalsIgnoreCase(jsonObject.getString("state")))
                                        stateSpinner.setSelection(i);
                                }

                                if(jsonObject.getInt("emailfreeze") ==1)
                                    email.setEnabled(false);

                                if(jsonObject.getInt("mobilefreeze") ==1)
                                    mobile.setEnabled(false);

                                if(jsonObject.getInt("statefreeze") ==1)
                                    stateSpinner.setEnabled(false);

                                if(jsonObject.getInt("dobfreeze") ==1)
                                    dob.setEnabled(false);

                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(PersonalDetailsActivity.this,LoginActivity.class));
                                    finishAffinity();
                                }
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
                                new AppUtils().Toast(PersonalDetailsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(PersonalDetailsActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        Details();
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

    public void EditProfile() {
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url) + "editprofile";
            Log.i("url", url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            try {
                                Log.i("Response is", response.toString());
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);

                                progressDialog.dismiss();
                                if (jsonObject.getInt("status") == 1) {
//                                    Details();
                                    finish();
                                }

                                new AppUtils().showSuccess(PersonalDetailsActivity.this, jsonObject.getString("msg"));

                            } catch (JSONException je) {
                                je.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("ErrorResponce", error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                new AppUtils().Toast(PersonalDetailsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            } else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(PersonalDetailsActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        EditProfile();
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
                    }) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", session.getUserId());
                    Log.i("Header", params.toString());

                    return params;
                }

                @Override
                public Map<String, String> getParams() {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("username", name.getEditText().getText().toString());
                    params.put("state", state1);
                    params.put("dob", dob.getText().toString());
                    params.put("gender", gender1);
                    params.put("address", address.getEditText().getText().toString());
                    params.put("pincode", pincode.getEditText().getText().toString());
                    params.put("team", session.getTeamName());
                    Log.i("Header", params.toString());

                    return params;
                }
            };
            strRequest.setShouldCache(false);
            strRequest.setRetryPolicy(new DefaultRetryPolicy(
                    0,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            requestQueue.add(strRequest);
        } catch (Exception e) {
            Log.i("Exception", e.getMessage());
        }

    }

}
