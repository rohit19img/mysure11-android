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
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
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

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class UserFirstDetailsActivity extends AppCompatActivity {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    TextInputLayout teamName;
    TextView dob;
    Spinner state;
    Button btnCreate;

    String dob1 = "", state1 = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_first_details);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                session.logoutUser();
                finishAffinity();
            }
        });

        TextView title =(TextView)findViewById(R.id.title);
        title.setText("Create Profile");

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        teamName =  findViewById(R.id.teamName);
        dob = (TextView) findViewById(R.id.dob);
        state = (Spinner) findViewById(R.id.state);
        btnCreate = (Button) findViewById(R.id.btnCreateProfile);

        if (!session.getTeamName().equals("")){
            teamName.setEnabled(false);
            teamName.getEditText().setText(session.getTeamName());
        }

        final String[] stateAr = getResources().getStringArray(R.array.india_states);
        state.setAdapter(new SpinnerAdapter(this, stateAr));
        state.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0)
                    state1 = "";
                else
                    state1 = stateAr[position];
//                ((TextView) state.getSelectedView()).setTextColor(Color.WHITE);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickDate(dob);
            }
        });

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (teamName.getEditText().getText().toString().length() < 2) {
                    teamName.setError("Please enter valid team name");
                } else if (dob1.equals(""))
                    dob.setError("Please enter your date of birth");
                else if (state1.equals(""))
                    new AppUtils().showError(UserFirstDetailsActivity.this,"Please select your state");
                else {
                    EditProfile();
                }
            }
        });
    }

    public void pickDate(final TextView t) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(UserFirstDetailsActivity.this, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                dob1 = (selectedmonth + 1) + "/" + selectedday + "/" + selectedyear;

                Date date1;
                String date2 = null;
                try {
                    DateFormat d1 = new SimpleDateFormat("MM/dd/yyyy");
                    date1 = d1.parse(dob1);
                    DateFormat d2 = new SimpleDateFormat("MMMM dd,yyyy");
                    date2 = d2.format(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                t.setText(date2);
            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - (5.681e+11)));
        mDatePicker.setTitle("Select Birth Date");
        mDatePicker.setCancelable(false);
        mDatePicker.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialogInterface) {

            }
        });
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDatePicker.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        }
        mDatePicker.show();
    }

    @Override
    public void onBackPressed() {
        session.logoutUser();
        finishAffinity();
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

                                if (jsonObject.getInt("status") == 1) {
                                    Intent ii = new Intent(UserFirstDetailsActivity.this, HomeActivity.class);
                                    session.setTeamName(teamName.getEditText().getText().toString());
                                    session.setState(state1);
                                    startActivity(ii);
                                    finishAffinity();
                                }

                                new AppUtils().showError(UserFirstDetailsActivity.this, jsonObject.getString("msg"));

                                progressDialog.dismiss();
                            } catch (JSONException je) {
                                je.printStackTrace();
                            }
                        }
                    },
                    new com.android.volley.Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            Log.i("ErrorResponce", error.toString());
//                            Log.i("Responce", String.valueOf(error.networkResponse.statusCode));

                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                new AppUtils().Toast(UserFirstDetailsActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            } else {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(UserFirstDetailsActivity.this);
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
                    params.put("team", teamName.getEditText().getText().toString());
                    params.put("state", state1);
                    params.put("dob", dob1);
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
