package com.img.mysure11.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.img.mysure11.R;

public class WelcomeActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
    }

    public void register(View v){
        startActivity(new Intent(WelcomeActivity.this,RegisterActivity.class));
    }

    public void login(View v){
        startActivity(new Intent(WelcomeActivity.this,LoginActivity.class));
    }

    public void PLAY(View v){
        startActivity(new Intent(WelcomeActivity.this,DumyHomeActivity.class));
    }


}
