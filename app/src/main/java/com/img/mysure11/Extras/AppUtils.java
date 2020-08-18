package com.img.mysure11.Extras;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.andrognito.flashbar.Flashbar;
import com.andrognito.flashbar.anim.FlashAnim;
import com.img.mysure11.R;

import java.util.regex.Pattern;

public class AppUtils {

    public Dialog getProgressDialog(Context context){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage("Please Wait...");
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public Dialog getProgressDialog(Context context,String message){
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setMessage(message);
        progressDialog.setCancelable(false);
        return progressDialog;
    }

    public void Toast(Context context,String toast_text){
        LayoutInflater inflater1 =((Activity)context).getLayoutInflater();
        View layout = inflater1.inflate(R.layout.custom_toast1,
                (ViewGroup) ((Activity)context).findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText(toast_text);

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.BOTTOM, 20, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public void showSuccess(Context context,String toast_text){

        new Flashbar.Builder((Activity) context)
                .gravity(Flashbar.Gravity.TOP)
                .duration(5000)
//                .title("Error!!")
                .message(toast_text)
                .enableSwipeToDismiss()
                .dismissOnTapOutside()
                .backgroundDrawable(R.drawable.success_bg)
//                .icon(R.drawable.ic_app_logo)
                .titleColorRes(R.color.white)
                .messageColorRes(R.color.white)
                .enterAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(750)
                        .alpha()
                        .overshoot())
                .exitAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(400)
                        .accelerateDecelerate())
                .build()
                .show();

    }

    public void showError(Context context,String toast_text){
        new Flashbar.Builder((Activity) context)
                .gravity(Flashbar.Gravity.TOP)
                .duration(5000)
//                .title("Error!!")
                .message(toast_text)
                .enableSwipeToDismiss()
                .dismissOnTapOutside()
                .backgroundDrawable(R.drawable.error_bg)
//                .icon(R.drawable.ic_app_logo)
                .titleColorRes(R.color.white)
                .messageColorRes(R.color.white)
                .enterAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(750)
                        .alpha()
                        .overshoot())
                .exitAnimation(FlashAnim.with(context)
                        .animateBar()
                        .duration(400)
                        .accelerateDecelerate())
                .build()
                .show();

    }

    public void NoInternet(Context context){
        LayoutInflater inflater1 =((Activity)context).getLayoutInflater();
        View layout = inflater1.inflate(R.layout.custom_toast1,
                (ViewGroup) ((Activity)context).findViewById(R.id.toast_layout_root));

        TextView text = (TextView) layout.findViewById(R.id.text);
        text.setText("No Internet");

        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.TOP, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();

    }

    public boolean isValidEmail(String email){

        return Pattern.compile("^(([\\w-]+\\.)+[\\w-]+|([a-zA-Z]{1}|[\\w-]{2,}))@"
                + "((([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\."
                + "([0-1]?[0-9]{1,2}|25[0-5]|2[0-4][0-9])\\.([0-1]?"
                + "[0-9]{1,2}|25[0-5]|2[0-4][0-9])){1}|"
                + "([a-zA-Z]+[\\w-]+\\.)+[a-zA-Z]{2,4})$").matcher(email).matches();

    }

    public boolean isValidPassword(String password){

        return  (password.length() > 7);

//        return Pattern.compile("^(?=.*?[A-Za-z])(?=.*?[0-9]).{8,}$")
//                .matcher(password).matches();
    }

    public boolean isValidNumber(String number){

        return Pattern.compile("(0/91)?[6-9][0-9]{9}")
                .matcher(number).matches();

    }

}
