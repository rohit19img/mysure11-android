package com.img.mysure11.Fragment;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
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
import android.widget.DatePicker;
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
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class PanValidationFragment extends Fragment {

    Context context;

    CardView cardVerified,cardDetails,cardNotVerified,invalidRequest;
    TextView panname,number,pandob,comment,btnUpload,dob,btnSubmit,panVerified;
    ImageView pancard,img,img1;
    TextInputLayout name,panNumber;

    ConnectionDetector cd;
    GlobalVariables gv;
    UserSessionManager session;
    RequestQueue requestQueue;
    Dialog progressDialog;

    String image_path = "",date = "",Simage = "";

    public PanValidationFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_pan_validation, container, false);
        context = getActivity();

        cd= new ConnectionDetector(context);
        gv= (GlobalVariables)context.getApplicationContext();
        requestQueue = Volley.newRequestQueue(context);
        session= new UserSessionManager(context);
        progressDialog = new AppUtils().getProgressDialog(context);

        name = (TextInputLayout) v.findViewById(R.id.name);
        panNumber = (TextInputLayout) v.findViewById(R.id.panNumber);
        btnUpload = (TextView) v.findViewById(R.id.btnUpload);
        btnSubmit = (TextView) v.findViewById(R.id.btnSubmit);
        dob = (TextView) v.findViewById(R.id.dob);
        comment = (TextView) v.findViewById(R.id.comment);
        panVerified = (TextView) v.findViewById(R.id.panVerified);

        dob.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pickDate(dob);
            }
        });

        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                validate();
            }
        });

        btnUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                selectImage();
            }
        });

        invalidRequest = (CardView) v.findViewById(R.id.invalidRequest);
        cardNotVerified = (CardView) v.findViewById(R.id.cardNotVerified);
        cardVerified = (CardView) v.findViewById(R.id.cardVerified);
        cardDetails = (CardView) v.findViewById(R.id.cardDetails);

        pancard = (ImageView) v.findViewById(R.id.pancard);
        img = (ImageView) v.findViewById(R.id.img);
        img1 = (ImageView) v.findViewById(R.id.img1);
        pancard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

        panname = (TextView) v.findViewById(R.id.panname);
        number = (TextView) v.findViewById(R.id.number);
        pandob = (TextView) v.findViewById(R.id.pandob);

        return v;
    }

    public void pickDate(final TextView dialog) {
        Calendar mcurrentDate = Calendar.getInstance();
        int mYear = mcurrentDate.get(Calendar.YEAR);
        int mMonth = mcurrentDate.get(Calendar.MONTH);
        int mDay = mcurrentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog mDatePicker = new DatePickerDialog(getActivity(), new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker datepicker, int selectedyear, int selectedmonth, int selectedday) {
                selectedmonth = selectedmonth + 1;
                String d = selectedyear + "-" + (selectedmonth) + "-" + selectedday;

                Date date1;
                String date2 = null;
                try {
                    DateFormat d1 = new SimpleDateFormat("yyyy-MM-dd");
                    date1 = d1.parse(d);
                    DateFormat d2 = new SimpleDateFormat("dd-MMM-yyyy");
                    date2 = d2.format(date1);
                } catch (ParseException e) {
                    e.printStackTrace();
                }

                dialog.setText("Date : " + date2);
                date = d;

            }
        }, mYear, mMonth, mDay);

        mDatePicker.getDatePicker().setMaxDate((long) (System.currentTimeMillis() - (5.681e+11)));
        mDatePicker.setTitle("Select Birth Date");
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            mDatePicker.getDatePicker().setFirstDayOfWeek(Calendar.MONDAY);
        }
        mDatePicker.show();
    }

    @Override
    public void onResume() {
        super.onResume();

        if(session.isEmailVerified() && session.isMobileVerified()){

            if(session.getPANVerified().equals("-1")){
                cardVerified.setVisibility(View.GONE);
                cardDetails.setVisibility(View.GONE);
                cardNotVerified.setVisibility(View.VISIBLE);
                invalidRequest.setVisibility(View.GONE);
            } else if(session.getPANVerified().equals("0")){
                cardVerified.setVisibility(View.VISIBLE);
                cardDetails.setVisibility(View.GONE);
                cardNotVerified.setVisibility(View.GONE);
                invalidRequest.setVisibility(View.GONE);

                details();
            } else if(session.getPANVerified().equals("1") || session.getPANVerified().equals("2")){
                cardVerified.setVisibility(View.GONE);
                cardDetails.setVisibility(View.GONE);
                cardNotVerified.setVisibility(View.GONE);
                invalidRequest.setVisibility(View.GONE);

                if(session.getPANVerified().equals("2")) {
                    cardVerified.setVisibility(View.VISIBLE);
                    cardNotVerified.setVisibility(View.VISIBLE);
                    panVerified.setText("Your PAN Card details are rejected");
                    panVerified.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                }
                details();
            }

        } else{
            cardVerified.setVisibility(View.GONE);
            cardDetails.setVisibility(View.GONE);
            cardNotVerified.setVisibility(View.GONE);
            invalidRequest.setVisibility(View.VISIBLE);
        }

    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if(isResumed()){
            if(session.isEmailVerified() && session.isMobileVerified()){

                if(session.getPANVerified().equals("-1")){
                    cardVerified.setVisibility(View.GONE);
                    cardDetails.setVisibility(View.GONE);
                    cardNotVerified.setVisibility(View.VISIBLE);
                    invalidRequest.setVisibility(View.GONE);
                } else if(session.getPANVerified().equals("0")){
                    cardVerified.setVisibility(View.VISIBLE);
                    cardDetails.setVisibility(View.GONE);
                    cardNotVerified.setVisibility(View.GONE);
                    invalidRequest.setVisibility(View.GONE);

                    details();
                } else if(session.getPANVerified().equals("1") || session.getPANVerified().equals("2")){
                    cardVerified.setVisibility(View.GONE);
                    cardDetails.setVisibility(View.GONE);
                    cardNotVerified.setVisibility(View.GONE);
                    invalidRequest.setVisibility(View.GONE);

                    if(session.getPANVerified().equals("2")) {
                        cardVerified.setVisibility(View.VISIBLE);
                        cardNotVerified.setVisibility(View.VISIBLE);
                        panVerified.setText("Your PAN Card details are rejected");
                        panVerified.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                    }
                    details();
                }

            } else{
                cardVerified.setVisibility(View.GONE);
                cardDetails.setVisibility(View.GONE);
                cardNotVerified.setVisibility(View.GONE);
                invalidRequest.setVisibility(View.VISIBLE);
            }
        }
    }

    public boolean validPan() {
        String text = panNumber.getEditText().getText().toString();
        if (text.length() != 10) {
            return false;
        } else if (!text.matches("(([A-Za-z]{5})([0-9]{4})([a-zA-Z]))")) {
            return false;
        } else
            return true;
    }

    public void validate() {
        if (name.getEditText().getText().toString().length() < 4)
            name.setError("Please enter a valid name");
        else if (!validPan())
            panNumber.setError("Please enter a valid PAN Number");
        else if (date.equals(""))
            dob.setError("Please enter your Date of birth");
        else if (Simage.equals("")) {
            new AppUtils().showError(context,"Upload PAN Card Image");
        } else {
            if(cd.isConnectingToInternet())
                VerifyPanDetails();
            else
                new AppUtils().NoInternet(context);
        }

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
                    pancard.setImageBitmap(bitmap);
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
                    pancard.setImageBitmap(myBitmap);
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
                pancard.setImageBitmap(bitmap);
                img.setImageBitmap(bitmap);
                img1.setImageBitmap(bitmap);
                img.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) img1.getDrawable()).getBitmap();
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                imageB.compress(Bitmap.CompressFormat.JPEG,100,byteArrayOutputStream);
                Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);

            }
            else {

            }
        }
        if (requestCode == 4) {
            if (resultCode == Activity.RESULT_OK) {
//                File f = new File(Environment.getExternalStorageDirectory().toString());
//                for (File temp : f.listFiles()) {
//                    if (temp.getName().equals("temp.jpg")) {
//                        f = temp;
//                        break;
//                    }
//                }
//                try {
//                    Bitmap bitmap;
//                    BitmapFactory.Options bitmapOptions = new BitmapFactory.Options();
//                    bitmap = BitmapFactory.decodeFile(f.getAbsolutePath(),
//                            bitmapOptions);
//
//                    Log.i("path",f.getAbsolutePath());
//                    Log.i("path",f.getPath());
//
//                    img.setImageBitmap(bitmap);
//
//                    Bitmap imageB = ((BitmapDrawable) img.getDrawable()).getBitmap();
//                    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
//                    imageB.compress(Bitmap.CompressFormat.JPEG,90,byteArrayOutputStream);
//                    Simage = Base64.encodeToString(byteArrayOutputStream.toByteArray(), Base64.DEFAULT);
//
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }

                Bitmap image = (Bitmap) data.getExtras().get("data");
                pancard.setImageBitmap(image);
                img.setImageBitmap(image);
                img1.setImageBitmap(image);
                img.setVisibility(View.VISIBLE);

                Bitmap imageB = ((BitmapDrawable) img1.getDrawable()).getBitmap();
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
            String url = getResources().getString(R.string.app_url)+"getpandetails";
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

                                if(jsonObject.getBoolean("status")){
                                    panname.setText(jsonObject.getString("panname"));
                                    number.setText(jsonObject.getString("pannumber"));
                                    pandob.setText(jsonObject.getString("pandob"));

                                    String panImage = jsonObject.getString("image");
                                    if (!panImage.equals("")) {
                                        Picasso.with(context).load(panImage).into(pancard);
                                    }

                                    if(jsonObject.has("comment")) {
                                        comment.setText(jsonObject.getString("comment"));
                                        comment.setVisibility(View.VISIBLE);
                                    }

                                    cardDetails.setVisibility(View.VISIBLE);
                                } else{
                                    new AppUtils().showError(context,"This pan card number is already exist.");
                                    session.setPANVerified("-1");

                                    cardVerified.setVisibility(View.GONE);
                                    cardDetails.setVisibility(View.GONE);
                                    cardNotVerified.setVisibility(View.GONE);
                                    invalidRequest.setVisibility(View.VISIBLE);
                                }
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

    public void VerifyPanDetails(){
        progressDialog.show();
        try {
            String url = getResources().getString(R.string.app_url)+"panrequest";
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
                                JSONObject jsonObject = new JSONArray(response.toString()).getJSONObject(0);
                                if(jsonObject.getBoolean("status")){
                                    session.setPANVerified("0");
                                    cardVerified.setVisibility(View.VISIBLE);
                                    cardDetails.setVisibility(View.GONE);
                                    cardNotVerified.setVisibility(View.GONE);
                                    invalidRequest.setVisibility(View.GONE);

                                    panVerified.setText("Your PAN Card details are sent for verification.");
                                    panVerified.setTextColor(context.getResources().getColor(R.color.colorAccent));

                                    details();

                                    new AppUtils().showSuccess(context,jsonObject.getString("msg"));
                                } else{
                                    new AppUtils().showError(context,jsonObject.getString("msg"));
                                }
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
                    map.put("panname",name.getEditText().getText().toString());
                    map.put("pannumber",panNumber.getEditText().getText().toString());
                    map.put("dob",date);
                    map.put("image",Simage);

                    Log.i("params",map.toString());

                    return map;
                }

//                protected Map<String, DataPart> getByteData() {
//                    Map<String, DataPart> params = new HashMap<>();
//                    params.put("file", new DataPart("gallery_image.jpg", convertToByte(image_path), "image/jpg"));
//
//                    Log.i("data",params.toString());
//                    return params;
//                }

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
