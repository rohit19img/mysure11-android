package com.img.mysure11.Activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.provider.Settings;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.Profile;
import com.facebook.ProfileTracker;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseApp;
import com.img.mysure11.Extras.AppUtils;
import com.img.mysure11.Extras.ConnectionDetector;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.R;
import com.img.mysure11.Static.TermsActivity;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    Button btnRegister;
    TextInputLayout email,mobile,password,inviteCode,fullname;
    ImageView seePassword;
    boolean seePass = false;

    LinearLayout fbLogin,btnGoogle;
    GoogleApiClient mGoogleApiClient;
    private CallbackManager callbackManager;
    String FGname="",FGemail="",FGimage="",dob="";
    private AccessTokenTracker accessTokenTracker;
    private ProfileTracker profileTracker;
    String TokenId="",email1="";
    TextView tc;
    String android_id = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        FirebaseApp.initializeApp(getApplicationContext());
        setContentView(R.layout.activity_register);

        cd= new ConnectionDetector(getApplicationContext());
        gv= (GlobalVariables)getApplicationContext();
        requestQueue = Volley.newRequestQueue(getApplicationContext());
        session= new UserSessionManager(getApplicationContext());
        progressDialog = new AppUtils().getProgressDialog(this);

        ImageView back = (ImageView)findViewById(R.id.back);
        back.setVisibility(View.GONE);

        TextView title = findViewById(R.id.title);
        title.setText("Register");

        tc=(TextView) findViewById(R.id.tc);
        btnRegister=(Button)findViewById(R.id.btnRegister);
        mobile=(TextInputLayout) findViewById(R.id.mobile);
        email=(TextInputLayout) findViewById(R.id.email);
        password=(TextInputLayout) findViewById(R.id.password);
        inviteCode=(TextInputLayout) findViewById(R.id.inviteCode);
        fullname=(TextInputLayout) findViewById(R.id.fullname);
        seePassword=(ImageView) findViewById(R.id.seePassword);

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(fullname.getEditText().getText().toString().length() < 4)
                    fullname.setError("Please enter valid  full name with Min 4 character");
                else if(!new AppUtils().isValidNumber(mobile.getEditText().getText().toString()))
                    mobile.setError("Please enter mobile number");
                else if(!new AppUtils().isValidEmail(email.getEditText().getText().toString()))
                    email.setError("Please enter email");
                else if(password.getEditText().getText().toString().length() < 4)
                    password.setError("Please enter valid password with Min 4 character");
                else {
                    if(cd.isConnectingToInternet()) {
                        Signup();
                    }
                }
            }
        });

        seePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(seePass){
                    password.getEditText().setTransformationMethod(PasswordTransformationMethod.getInstance());
                    seePass = false;
                    seePassword.setImageResource(R.drawable.ic_eye_accent);
                }else{
                    password.getEditText().setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                    seePass = true;
                    seePassword.setImageResource(R.drawable.ic_eye_font);
                }
            }
        });

        callbackManager = CallbackManager.Factory.create();

        profileTracker = new ProfileTracker() {
            @Override
            protected void onCurrentProfileChanged(Profile oldProfile, Profile newProfile) {

            }
        };

        accessTokenTracker= new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(AccessToken oldToken, AccessToken newToken) {
            }
        };

        FacebookSdk.sdkInitialize(getApplicationContext());
        LoginManager.getInstance().logOut();

        accessTokenTracker.startTracking();
        profileTracker.startTracking();

        fbLogin=(LinearLayout) findViewById(R.id.login_button);
        fbLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(RegisterActivity.this, Arrays.asList("public_profile","email"));
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                AccessToken accessToken = loginResult.getAccessToken();

                System.out.println("  accesstoken=  " + accessToken);
                final Profile profile = Profile.getCurrentProfile();

                GraphRequest request = GraphRequest.newMeRequest(
                        loginResult.getAccessToken(),
                        new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject object, GraphResponse response) {
                                Log.v("FBRegisterActivity", response.toString());

                                JSONObject json = response.getJSONObject();
                                try {
                                    email1 = json.getString("email");
                                    displayMessage(profile);

                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        });

                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,email");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(RegisterActivity.this, "Login Canceled.", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onError(FacebookException e) {
                Toast.makeText(getApplicationContext(),"Cannot connect facebook error.",Toast.LENGTH_LONG);
            }
        });

        btnGoogle=(LinearLayout) findViewById(R.id.btnGoogle);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestScopes(new Scope(Scopes.PLUS_LOGIN))
                .build();


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */,  this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .addApi(Plus.API)
                .build();


        btnGoogle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
        tc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterActivity.this, TermsActivity.class));
            }
        });


        android_id = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);

        Log.i("androididss", android_id);


    }

    public void LOGIN(View v){
        finish();
    }

    public void REFER(View v){
//        startActivity(new Intent(RegisterActivity.this,ReferCodeActivity.class));
    }

    private void displayMessage(Profile profile){
        if(profile != null) {

            email.getEditText().setText(email1);
            FGname = profile.getName();
            FGimage = String.valueOf(profile.getProfilePictureUri(100, 100));
            FGemail = email1;


            FbGoogleLogin("facebook");
        }
        else
        {

        }
    }

    public void LOGINR(View v){
        startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
    }
    @Override
    public void onStop() {
        super.onStop();
        accessTokenTracker.stopTracking();
        profileTracker.stopTracking();
    }

    // google
    private void signIn() {
        Auth.GoogleSignInApi.signOut(mGoogleApiClient);
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, 1);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        callbackManager.onActivityResult(requestCode, resultCode, data);

        if (resultCode == -1) {

            if (requestCode == 1) {
                GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
                handleSignInResult(result);
            }
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d("log", "handleSignInResult:" + result);

        if (result.isSuccess() == true) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();

            if(acct.getEmail().equals(""))
            {
                new AppUtils().showError(RegisterActivity.this,"Email id not found,please try manually.");
            } else {
                FGemail=acct.getEmail();
                FGname=acct.getDisplayName();
                FGimage=String.valueOf(acct.getPhotoUrl());

                Log.i("Email",FGemail);
                Log.i("name",FGname);
                Log.i("image",FGimage);

                dob = "";
                progressDialog.show();
                FbGoogleLogin("google");
            }
        } else {

            Log.i("where","else");
        }
    }



    public void FbGoogleLogin(final String type){
        progressDialog.show();

        try {

            String url = getResources().getString(R.string.app_url)+"socialauthentication";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>(){
                        @Override
                        public void onResponse(String response)
                        {
                            try {
                                Log.i("Response is",response.toString());
                                final JSONObject jsonObject = new JSONObject(response.toString());

                                if(jsonObject.getBoolean("status")){
                                    if(progressDialog!=null)
                                        progressDialog.dismiss();

                                    /*if(jsonObject.getInt("mobile_status") == 0){
                                        Intent ii = new Intent(RegisterActivity.this,MobileVerificationActivity.class);
                                        ii.putExtra("Auth","bearer "+jsonObject.getString("userid"));
                                        startActivity(ii);
                                    }else */{
                                        session.createUserLoginSession(true, "bearer " + jsonObject.getString("userid"),
                                                FGemail,jsonObject.getString("type"));
                                        Details();
                                    }
                                }else{
                                                    if(progressDialog!=null)
                    progressDialog.dismiss();

                                    new AppUtils().showError(RegisterActivity.this, jsonObject.getString("msg"));
                                }

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
                            if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(RegisterActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FbGoogleLogin(type);
                                    }
                                });
                                d.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        finish();
                                    }
                                });
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(RegisterActivity.this);
                                d.setTitle("Something went wrong");
                                d.setCancelable(false);
                                d.setMessage("Something went wrong, Please try again");
                                d.setPositiveButton("Retry", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FbGoogleLogin(type);
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
                    params.put("email", FGemail);
                    params.put("name", FGname);
                    params.put("image", FGimage);
                    params.put("appid", session.getNotificationToken());
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

    public void Signup(){
        progressDialog.show();

        try {

            String url = getResources().getString(R.string.app_url)+"tempregisteruser";
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
                                    Intent ii = new Intent(RegisterActivity.this,OTPActivity.class);
                                    session.setReferalCode("");
                                    ii.putExtra("email",email.getEditText().getText().toString());
                                    ii.putExtra("mobile",mobile.getEditText().getText().toString());
                                    ii.putExtra("tempuser",jsonObject.getString("tempuser"));
                                    ii.putExtra("password",password.getEditText().getText().toString());
                                    ii.putExtra("referCode",inviteCode.getEditText().getText().toString());
                                    ii.putExtra("from","register");
                                    startActivity(ii);
                                }else {
                                    new AppUtils().showError(RegisterActivity.this, jsonObject.getString("msg"));
                                }
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
                            if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(RegisterActivity.this);
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
                            }else{
                                AlertDialog.Builder d = new AlertDialog.Builder(RegisterActivity.this);
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
                    params.put("fullname", fullname.getEditText().getText().toString());
                    params.put("email", email.getEditText().getText().toString());
                    params.put("mobile", mobile.getEditText().getText().toString());
                    params.put("password", password.getEditText().getText().toString());
                    params.put("refercode",inviteCode.getEditText().getText().toString());
                    params.put("appid", session.getNotificationToken());
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

                                progressDialog.dismiss();

                                startActivity(new Intent(RegisterActivity.this,HomeActivity.class));

                                if (jsonObject.getString("activation_status").equals("deactivated")){
                                    startActivity(new Intent(RegisterActivity.this,LoginActivity.class));
                                    finishAffinity();
                                }

                                finishAffinity();
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
                                new AppUtils().Toast(RegisterActivity.this,"Session Timeout");

                                session.logoutUser();
                                finishAffinity();
                            }else if (error instanceof TimeoutError) {
                                android.app.AlertDialog.Builder d = new android.app.AlertDialog.Builder(RegisterActivity.this);
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
                            }else {
                                AlertDialog.Builder d = new AlertDialog.Builder(RegisterActivity.this);
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

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        // An unresolvable error has occurred and Google APIs (including Sign-In) will not
        // be available.
        Log.d("Error", "onConnectionFailed:" + connectionResult);
    }

}
