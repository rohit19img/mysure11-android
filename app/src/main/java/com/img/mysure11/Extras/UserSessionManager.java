package com.img.mysure11.Extras;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

import com.img.mysure11.Activity.LoginActivity;

import java.util.HashMap;


public class UserSessionManager {

    SharedPreferences sharedPreferences;
    Editor editor;
    Context _context;
    int PRIVATE_MODE = 0;


    private static final String PREFER_NAME = "AndroidExamplePref";

    private static final String IS_LOGIN = "IsLoggedIn";
    private static final String Name = "Name";
    private static final String Email = "Email";
    private static final String Mobile = "Mobile";
    private static final String TeamName = "TeamName";
    private static final String State = "State";
    private static final String userName = "userName";
    private static final String ReferalCode= "referal code";
    private static final String image= "image url";
    private static final String verified= "verified";
    private static final String UserType= "userType";

    private static final String MobileVerified= "mobile verified";
    private static final String emailVerified= "email verified";
    private static final String PANVerified= "PAN verified";
    private static final String BankVerified= "Bank verified";
    private static final String NotificationToken = "NotificationToken";
    private static final String Dob= "dob";

    private static final String otp_email = "otp_email";
    private static final String otp_password = "otp_password";
    private static final String wallet_amount = "wallet_amount";
    private static final String winningAmount = "WinningAmount";

    public static final String KEY_Id = "id";

    public UserSessionManager(Context context){
        this._context = context;
        sharedPreferences = _context.getSharedPreferences(PREFER_NAME, PRIVATE_MODE);
        editor = sharedPreferences.edit();
    }

    public void createUserLoginSession(boolean login, String id,String email,String type) {
        editor.putBoolean(IS_LOGIN, login);
        editor.putString(KEY_Id, id);
        editor.putString(Email,email);
        editor.putString(UserType,type);
        editor.commit();
    }

    public void setUserID(String id){
        editor.putString(KEY_Id, id);
        editor.commit();
    }

    public void setOtp_email(String email,String password,String code){
        editor.putString(otp_email,email);
        editor.putString(otp_password,password);
        editor.putString(ReferalCode,code);
        editor.commit();
    }

    public void setEmail(String email){
        editor.putString(Email,email);
        editor.commit();
    }

    public void setUserName(String name){
        editor.putString(userName,name);
        editor.commit();
    }

    public void setNotificationToken(String Token){
        editor.putString(NotificationToken,Token);
        editor.commit();
    }

    public void setDob(String dob){
        editor.putString(Dob,dob);
        editor.commit();
    }

    public void setVerified(boolean v){
        editor.putBoolean(verified,v);
        editor.commit();
    }

    public void setMobileVerified(boolean v){
        editor.putBoolean(MobileVerified,v);
        editor.commit();
    }

    public void setEmailVerified(boolean v){
        editor.putBoolean(emailVerified,v);
        editor.commit();
    }

    public void setPANVerified(String v){
        editor.putString(PANVerified,v);
        editor.commit();
    }

    public void setBankVerified(String v){
        editor.putString(BankVerified,v);
        editor.commit();
    }

    public void setWallet_amount(String amount){
        editor.putString(wallet_amount,amount);
        editor.commit();
    }

    public void setWinningAmount(String amount){
        editor.putString(winningAmount,amount);
        editor.commit();
    }

    public void setReferalCode(String code){
        editor.putString(ReferalCode,code);
        editor.commit();
    }

    public void setImage(String code){
        editor.putString(image,code);
        editor.commit();
    }

    public void setMobile(String mobile){
        editor.putString(Mobile,mobile);
        editor.commit();
    }

    public void setName(String name){
        editor.putString(Name,name);
        editor.commit();
    }

    public void setTeamName(String name){
        editor.putString(TeamName,name);
        editor.commit();
    }

    public void setState(String name){
        editor.putString(State,name);
        editor.commit();
    }

    public boolean checkLogin(){

        if(!this.isUserLoggedIn()){

            Intent i = new Intent(_context, LoginActivity.class);
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            _context.startActivity(i);

            return false;
        }
        return true;
    }

    public HashMap<String, String> getUserDetails(){

        HashMap<String, String> user = new HashMap<String, String>();
        user.put(KEY_Id, sharedPreferences.getString(KEY_Id, ""));

        return user;
    }


    public void logoutUser(){

        Intent i = new Intent(_context, LoginActivity.class);
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        _context.startActivity(i);

        editor.clear();
        editor.commit();
    }


    public boolean isUserLoggedIn(){
        return sharedPreferences.getBoolean(IS_LOGIN, false);
    }

    public String getUserId(){
        return sharedPreferences.getString(KEY_Id, "");
    }

    public String getMobile(){
        return sharedPreferences.getString(Mobile,"");
    }

    public String getEmail(){
        return sharedPreferences.getString(Email,"");
    }

    public String getName(){
        return sharedPreferences.getString(Name,"");
    }

    public String getTeamName(){
        return sharedPreferences.getString(TeamName,"");
    }
    public String getState(){
        return sharedPreferences.getString(State,"");
    }

    public String getReferalCode(){
        return sharedPreferences.getString(ReferalCode,"");
    }

    public String getImage(){
        return sharedPreferences.getString(image,"");
    }

    public String getOtp_email(){
        return sharedPreferences.getString(otp_email,"");
    }

    public String getOtp_password(){
        return sharedPreferences.getString(otp_password,"");
    }

    public String getUserName(){
        return sharedPreferences.getString(userName,"");
    }

    public boolean isUserVerified(){
        return sharedPreferences.getBoolean(verified,false);
    }

    public boolean isMobileVerified(){
        return sharedPreferences.getBoolean(MobileVerified,false);
    }

    public boolean isEmailVerified(){
        return sharedPreferences.getBoolean(emailVerified,false);
    }

    public String getPANVerified(){
        return sharedPreferences.getString(PANVerified,"");
    }

    public String getBankVerified(){
        return sharedPreferences.getString(BankVerified,"");
    }

    public String getNotificationToken() {
        return sharedPreferences.getString(NotificationToken,"");
    }

    public String getUserType() {
        return sharedPreferences.getString(UserType,"");
    }

    public String getWallet_amount() {
        return sharedPreferences.getString(wallet_amount,"0");
    }

    public String getWinningAmount() {
        return sharedPreferences.getString(winningAmount,"0");
    }

    public String getDob(){
        return sharedPreferences.getString(Dob,"");
    }

}
