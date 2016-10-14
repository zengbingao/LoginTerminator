package com.qraved.imaginato.loginterminator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

public class MainActivity extends AppCompatActivity {
    public static final int  REQUEST_CODE_GOOGLE_PLUS=10001;

    GooglePlus googlePlus = new GooglePlus();
    GoogleApiClient mGoogleApiClient;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        SignInButton sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        mGoogleApiClient = googlePlus.initGooglePlus(MainActivity.this,sign_in_button);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("robin", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
        if(requestCode==REQUEST_CODE_GOOGLE_PLUS){
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            googlePlus.handleSignInResult(result);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }
    public void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_PLUS);
    }
}
