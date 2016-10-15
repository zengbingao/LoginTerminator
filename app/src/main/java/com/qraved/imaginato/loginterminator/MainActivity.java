package com.qraved.imaginato.loginterminator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.SignInButton;

import static com.qraved.imaginato.loginterminator.GooglePlus.handleSignInResult;
import static com.qraved.imaginato.loginterminator.GooglePlus.initGooglePlus;
import static com.qraved.imaginato.loginterminator.GooglePlus.initGooglePlusOnstart;
import static com.qraved.imaginato.loginterminator.GooglePlus.initGooglePlusOnstop;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GOOGLE_PLUS = 10001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignInButton sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        initGooglePlus(MainActivity.this, sign_in_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("robin", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
        if (requestCode == REQUEST_CODE_GOOGLE_PLUS) {
            handleSignInResult(data);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        initGooglePlusOnstart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        initGooglePlusOnstop();
    }
    
}
