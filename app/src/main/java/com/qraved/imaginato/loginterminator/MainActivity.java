package com.qraved.imaginato.loginterminator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import static com.qraved.imaginato.loginterminator.GooglePlus.handleSignInResult;
import static com.qraved.imaginato.loginterminator.GooglePlus.initGooglePlus;
import static com.qraved.imaginato.loginterminator.GooglePlus.initGooglePlusOnstart;
import static com.qraved.imaginato.loginterminator.GooglePlus.initGooglePlusOnstop;
import static com.qraved.imaginato.loginterminator.Twitter.initTwitter;
import static com.qraved.imaginato.loginterminator.Twitter.initTwitterOnActivityResult;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GOOGLE_PLUS = 10001;
    public static final int REQUEST_CODE_TWITTER = 140;//这个值一定一定要和TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE一致，否则会失败。
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_main);
        SignInButton sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        
        initGooglePlus(MainActivity.this, sign_in_button);
        initTwitter(MainActivity.this,loginButton);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("robin", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
        if (requestCode == REQUEST_CODE_GOOGLE_PLUS) {
            handleSignInResult(data);
        }else if(requestCode==REQUEST_CODE_TWITTER){
            initTwitterOnActivityResult(requestCode,resultCode,data);
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
