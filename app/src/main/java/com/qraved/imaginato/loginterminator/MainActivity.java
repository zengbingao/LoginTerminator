package com.qraved.imaginato.loginterminator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GOOGLE_PLUS = 10001;
    public static final int REQUEST_CODE_TWITTER = 140;//这个值一定一定要和TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE一致，否则会失败。
    TwitterAuthClient customAuthClient;//如果要在工具类里面new的话，那么这个东西就要指定为static 但是他可能会内存泄漏，所以只能在这里面去new

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        SignInButton sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        TextView textView = (TextView) findViewById(R.id.tv_1);

        GooglePlus.initGooglePlus(MainActivity.this, sign_in_button);
        Twitter.initTwitter(MainActivity.this, loginButton);//pay attention to init fabric in this method.
        customAuthClient = new TwitterAuthClient();
        Twitter.initCustomTwitter(MainActivity.this, textView, customAuthClient);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.i("robin", "requestCode==" + requestCode + ",resultCode==" + resultCode + ",data==" + data);
        if (requestCode == REQUEST_CODE_GOOGLE_PLUS) {
            GooglePlus.handleSignInResult(data);
        } else if (requestCode == REQUEST_CODE_TWITTER) {
            if (Twitter.ISFROMCUSTOMTWITTERBUTTON) {
                Twitter.initCustomTwitterOnActivityResult(requestCode, resultCode, data,customAuthClient);
            } else {
                Twitter.initTwitterOnActivityResult(requestCode, resultCode, data);
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        GooglePlus.initGooglePlusOnstart();
    }

    @Override
    protected void onStop() {
        super.onStop();
        GooglePlus.initGooglePlusOnstop();
    }

}
