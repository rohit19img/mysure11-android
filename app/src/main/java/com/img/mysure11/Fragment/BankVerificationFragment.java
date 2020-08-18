package com.img.mysure11.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
import com.img.mysure11.Extras.FileUtils;
import com.img.mysure11.Extras.GlobalVariables;
import com.img.mysure11.Extras.UserSessionManager;
import com.img.mysure11.Extras.VolleyMultipartRequest;
import com.img.mysure11.R;
import com.squareup.picasso.Picasso;

import org.apache.http.HttpStatus;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 */
public class BankVerificationFragment extends Fragment {

    Context context;

    CardView bankVerified,bankDetails,bankNotVerified,invalidBank;
    TextView holderName,accNo,number,bnkName,branch,state,comment,bankText;
    ImageView passbook,img;
    TextInputLayout name,accountNumber,VaccountNumber,bankName,branchName,ifscCode;
    Spinner stateSpinner;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String state_name = "", image_path = "",Simage = "";

    public BankVerificationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_bank_verification, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        bankVerified = v.findViewById(R.id.bankVerified);
        bankDetails = v.findViewById(R.id.bankDetails);
        bankNotVerified = v.findViewById(R.id.bankNotVerified);
        invalidBank = v.findViewById(R.id.invalidBank);
        bankText = v.findViewById(R.id.bankText);

        holderName = v.findViewById(R.id.holderName);
        accNo = v.findViewById(R.id.accNo);
        number = v.findViewById(R.id.number);
        bnkName = v.findViewById(R.id.bnkName);
        branch = v.findViewById(R.id.branch);
        state = v.findViewById(R.id.state);
        comment = v.findViewById(R.id.comment);
        passbook = v.findViewById(R.id.passbook);

        img = v.findViewById(R.id.img);
        name = v.findViewById(R.id.name);
        accountNumber = v.findViewById(R.id.accountNumber);
        VaccountNumber = v.findViewById(R.id.VaccountNumber);
        bankName = v.findViewById(R.id.bankName);
        branchName = v.findViewById(R.id.branchName);
        ifscCode = v.findViewById(R.id.ifscCode);

        stateSpinner = v.findViewById(R.id.stateSpinner);
        final String Ar[] = getResources().getStringArray(R.array.india_states);
        stateSpinner.setAdapter(new SpinnerAdapter(context,Ar));

        stateSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position == 0)
                    state_name = "";
                else
                    state_name = Ar[position];
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        v.findViewById(R.id.btnUpload).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }
        });

        v.findViewById(R.id.btnSubmit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                validate();
            }
        });

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

        if(!session.getPANVerified().equals("-1")){

            if(session.getBankVerified().equals("-1")){
                bankVerified.setVisibility(View.GONE);
                bankDetails.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.VISIBLE);
                invalidBank.setVisibility(View.GONE);
            } else if(session.getBankVerified().equals("0")){
                bankVerified.setVisibility(View.VISIBLE);
                bankDetails.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.GONE);
                invalidBank.setVisibility(View.GONE);

                details();
            } else if(session.getBankVerified().equals("1") || session.getBankVerified().equals("2")){
                bankVerified.setVisibility(View.GONE);
                bankDetails.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.GONE);
                invalidBank.setVisibility(View.GONE);

                if(session.getBankVerified().equals("2")) {
                    bankVerified.setVisibility(View.VISIBLE);
                    bankNotVerified.setVisibility(View.VISIBLE);
                    bankText.setText("Your Bank details are rejected.");
                    bankText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }

                details();
            }
        } else{
            bankVerified.setVisibility(View.GONE);
            bankDetails.setVisibility(View.GONE);
            bankNotVerified.setVisibility(View.GONE);
            invalidBank.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            if(!session.getPANVerified().equals("-1")){

                if(session.getBankVerified().equals("-1")){
                    bankVerified.setVisibility(View.GONE);
                    bankDetails.setVisibility(View.GONE);
                    bankNotVerified.setVisibility(View.VISIBLE);
                    invalidBank.setVisibility(View.GONE);
                } else if(session.getBankVerified().equals("0")){
                    bankVerified.setVisibility(View.VISIBLE);
                    bankDetails.setVisibility(View.GONE);
                    bankNotVerified.setVisibility(View.GONE);
                    invalidBank.setVisibility(View.GONE);

                    details();
                } else if(session.getBankVerified().equals("1") || session.getBankVerified().equals("2")){
                    bankVerified.setVisibility(View.GONE);
                    bankDetails.setVisibility(View.GONE);
                    bankNotVerified.setVisibility(View.GONE);
                    invalidBank.setVisibility(View.GONE);

                    if(session.getBankVerified().equals("2")) {
                        bankVerified.setVisibility(View.VISIBLE);
                        bankNotVerified.setVisibility(View.VISIBLE);
                        bankText.setText("Your Bank details are rejected.");
                        bankText.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    }

                    details();
                }

            } else{
                bankVerified.setVisibility(View.GONE);
                bankDetails.setVisibility(View.GONE);
                bankNotVerified.setVisibility(View.GONE);
                invalidBank.setVisibility(View.VISIBLE);
            }

        }
    }

    public void validate(){
        if(VaccountNumber.getEditText().getText().toString().length()<1)
            VaccountNumber.setError("Please enter valid account number");
        else if(accountNumber.getEditText().getText().toString().length()<1)
            accountNumber.setError("Please enter valid account number");
        else if(!VaccountNumber.getEditText().getText().toString().equals(accountNumber.getEditText().getText().toString()))
            VaccountNumber.setError("Your account number and verify account number not matched");
        else if(!validIfsc(ifscCode.getEditText().getText().toString()))
            ifscCode.setError("Please enter valid IFSC Code");
        else if(branchName.getEditText().getText().toString().length()<1)
            branchName.setError("Please enter valid branch name");
        else if(bankName.getEditText().getText().toString().length()<1)
            bankName.setError("Please enter valid bank name");
        else if(state_name.equals(""))
            new AppUtils().showError(context, "Please select your state");
        else if(Simage.equals("")) {
            new AppUtils().showError(context,"Please click a image of passbook first");
        }else{
            if(cd.isConnectingToInternet()) {
                VerifyBankDetails();
            } else
                new AppUtils().NoInternet(context);
        }
    }

    public boolean validIfsc(String text){
        // The IFSC is an 11-character code with the
        // first four alphabetic characters representing the bank name,
        // and the last six characters (usually numeric, but can be alphabetic) representing the branch.
        // The fifth character is 0 (zero) and reserved for future use.

        String pattern = "^[A-Za-z]{4}[0][a-zA-Z0-9]{6}$";

        if(text.matches(pattern))
            return true;
        return false;
    }

/*
    public void selectImage()
    {
        String[] perarr = new String[]{
                Manifest.permission.INTERNET,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
        };
        ActivityCompat.requestPermissions(getActivity(), perarr, 1);

        final AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("Select a image");

        final String [] items = {"Take Photo..","Choose From Library.."};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position){
                    case 0:
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(context,
                                context.getApplicationContext().getPackageName() + ".provider", f));
                        startActivityForResult(intent, 1);
                        break;
                    case 1:
                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallaryIntent, 2);
                        break;
                }
            }
        });
        final AlertDialog alert = builder.create();
        alert.setButton(AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {   // image camera
            if (resultCode == Activity.RESULT_OK) {
                File f = new File(Environment.getExternalStorageDirectory().toString());
                for (File temp : f.listFiles()) {
                    if (temp.getName().equals("temp.jpg")) {
                        f = temp;
                        break;
                    }
                }
                try {
                    Bitmap bitmap;
                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
                            bitmapOptions);

                    String path = android.os.Environment
                            .getExternalStorageDirectory()
                            + File.separator
                            + "" ;

                    OutputStream outFile = null;
                    File Simage = new File(path, "temp.jpg");

                    image_path = Simage.getAbsolutePath();
                    passbook.setImageBitmap(bitmap);
                    img.setImageBitmap(bitmap);

                    Log.d("filePath", image_path);

                    try {
                        outFile = new FileOutputStream(Simage);
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outFile);
                        outFile.flush();
                        outFile.close();
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }

        }

        if (requestCode == 2) {   // image gallery
            if (resultCode == Activity.RESULT_OK) {

                Uri image = data.getData();
                String[] filePath = { MediaStore.Images.Media.DATA };
                Cursor cursor = context.getContentResolver().query(image, filePath, null, null, null);

                Log.d("Image", DatabaseUtils.dumpCursorToString(cursor));

                image_path = new FileUtils().getRealPath(context,image);

//                cursor.close();
                Log.i("image is",image_path);

                File Simage= new File(image_path);
                if(Simage.exists()){
                    Bitmap myBitmap = BitmapFactory.decodeFile(Simage.getAbsolutePath());
                    passbook.setImageBitmap(myBitmap);
                    img.setImageBitmap(myBitmap);
                }

                Log.d("picUri", image.toString());
                Log.d("filePath", image_path);

            }
            else {

            }
        }

    }
*/

    public void selectImage()
    {
        final androidx.appcompat.app.AlertDialog.Builder builder = new androidx.appcompat.app.AlertDialog.Builder(context);
        builder.setTitle("Select a image");

        final String [] items = {"Take Photo..","Choose Image From Library"};
        builder.setItems(items, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int position) {
                switch (position){
                    case 0:
//                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        File f = new File(android.os.Environment.getExternalStorageDirectory(), "temp.jpg");
//                        intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(CreateEventActivity.this,
//                                getApplicationContext().getPackageName() + ".provider", f));
//                        startActivityForResult(intent, 4);

                        Intent cameraIntent = new Intent("android.media.action.IMAGE_CAPTURE");
                        startActivityForResult(cameraIntent, 4);
                        break;
                    case 1:
                        Intent gallaryIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                        startActivityForResult(gallaryIntent, 1);
                        break;
                }
            }
        });
        final androidx.appcompat.app.AlertDialog alert = builder.create();
        alert.setButton(androidx.appcompat.app.AlertDialog.BUTTON_NEGATIVE, "Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                alert.dismiss();
            }
        });
        alert.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                Uri image = data.getData();

                Bitmap bitmap=null;
                try {
                    bitmap = MediaStore.Images.Media.getBitmap(context.getContentResolver(),image);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                bitmap = getResizedBitmap(bitmap, 2000);// 400 is for example, replace with desired size
                passbook.setImageBitmap(bitmap);
                img.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) passbook.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            }
            else {

            }
        }
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
                Bitmap image = (Bitmap) data.getExtras().get("data");
                passbook.setImageBitmap(image);
                img.setImageBitmap(image);
                img.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) passbook.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
            }
        }
    }

    public Bitmap getResizedBitmap(Bitmap image, int maxSize) {
        int width = image.getWidth();
        int height = image.getHeight();

        float bitmapRatio = (float)width / (float) height;
        if (bitmapRatio > 1) {
            width = maxSize;
            height = (int) (width / bitmapRatio);
        } else {
            height = maxSize;
            width = (int) (height * bitmapRatio);
        }
        return Bitmap.createScaledBitmap(image, width, height, true);
    }

    public void details(){
        progressDialog.show();
        try {

            String url = getResources().getString(R.string.app_url)+"seebankdetails";
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

                                accNo.setText(jsonObject.getString("accno"));
                                number.setText(jsonObject.getString("ifsc"));
                                bnkName.setText(jsonObject.getString("bankname"));
                                branch.setText(jsonObject.getString("bankbranch"));
                                state.setText(jsonObject.getString("state"));
                                holderName.setText(jsonObject.getString("accountholdername"));

                                String bankImage = jsonObject.getString("image");
                                if (!bankImage.equals("")) {
                                    Picasso.with(context).load(bankImage).into(passbook);
                                }
                                if(jsonObject.has("comment")) {
                                    comment.setText(jsonObject.getString("comment"));
                                    comment.setVisibility(View.VISIBLE);
                                }
                                bankDetails.setVisibility(View.VISIBLE);

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
                                            if(progressDialog!=null)
                    progressDialog.dismiss();

                            Log.i("ErrorResponce",error.toString());
                            NetworkResponse networkResponse = error.networkResponse;
                            if (networkResponse != null && networkResponse.statusCode == HttpStatus.SC_UNAUTHORIZED) {
                                // HTTP Status Code: 401 Unauthorized
                                new AppUtils().Toast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
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


    public void VerifyBankDetails(){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"bankrequest";
            Log.i("url",url);
            StringRequest strRequest = new StringRequest(Request.Method.POST, url,
                    new com.android.volley.Response.Listener<String>()
                    {
                        @Override
                        public void onResponse(String response)
                        {

                            if(progressDialog!=null)
                                progressDialog.dismiss();

                            try{
                                if(progressDialog!=null)
                                    progressDialog.dismiss();
                                JSONObject jsonObject = new JSONArray(response).getJSONObject(0);
                                if(jsonObject.getBoolean("status")){
                                    session.setBankVerified("0");

                                    bankVerified.setVisibility(View.VISIBLE);
                                    bankDetails.setVisibility(View.GONE);
                                    bankNotVerified.setVisibility(View.GONE);
                                    invalidBank.setVisibility(View.GONE);

                                    bankText.setText("Your Bank details are sent for verification.");
                                    bankText.setTextColor(context.getResources().getColor(R.color.colorAccent));

                                    new AppUtils().showSuccess(context,jsonObject.getString("msg"));
                                    details();
                                } else
                                    new AppUtils().showError(context,jsonObject.getString("msg"));
                            } catch (JSONException e) {
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
                                new AppUtils().Toast(context,"Session Timeout");

                                session.logoutUser();
                                ((Activity)context).finishAffinity();
                            }
                        }
                    })
            {

                @Override
                protected Map<String, String> getParams(){
                    HashMap<String,String> map = new HashMap<>();
                    map.put("accountholder",name.getEditText().getText().toString());
                    map.put("accno",accountNumber.getEditText().getText().toString());
                    map.put("ifsc",ifscCode.getEditText().getText().toString());
                    map.put("bankname",bankName.getEditText().getText().toString());
                    map.put("bankbranch",branchName.getEditText().getText().toString());
                    map.put("state",state_name);
                    map.put("image",Simage);

                    Log.i("params",map.toString());

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

    public byte[] convertToByte(String path){

        Log.i("path",path);

        File file = new File(path);
        Log.i("length", String.valueOf(file.length()));

        if(file.exists()){
            Log.i("file","exists");
        } else
            Log.i("file"," not exists");

        byte[] b = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(b);
//            for (int i = 0; i < b.length; i++) {
//                System.out.print((char)b[i]);
//            }
        } catch (FileNotFoundException e) {
            System.out.println("File Not Found.");
            e.printStackTrace();
        }
        catch (IOException e1) {
            System.out.println("Error Reading The File.");
            e1.printStackTrace();
        }

        return b;
    }

}
