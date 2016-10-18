package com.qraved.imaginato.loginterminator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

import io.fabric.sdk.android.Fabric;

/**
 * login in with twitter use fabric
 * Created by robin on 2016/10/15.
 */

public class TwitterHelper {
    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.https://fabric.io/kits/android/twitterkit
    private static TwitterLoginButton mLoginButton;
    static boolean ISFROMCUSTOMTWITTERBUTTON = false;

    static void initFabric(Context context) {
        String TWITTER_KEY = context.getResources().getString(R.string.twitter_key);
        String TWITTER_SECRET = context.getResources().getString(R.string.twitter_secret);
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(context, new com.twitter.sdk.android.Twitter(authConfig));
        Log.i("robin", "ready to load fabric");
    }

    static void initTwitter(TwitterLoginButton loginButton,final MainActivity mainActivity) {
        mLoginButton = loginButton;
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("robin", "click twitter button to login with twitter");
                ISFROMCUSTOMTWITTERBUTTON = false;
            }
        });
        loginButton.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> result) {
                Log.i("robin", "twitter success");
                // The TwitterSession is also available through:(TwitterSession也可以通过下面的方式获得)
                // TwitterHelper.getInstance().core.getSessionManager().getActiveSession()
                TwitterSession session = result.data;
                String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                Toast.makeText(mainActivity, msg, Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                Log.d("robin", "Login with TwitterHelper failure", exception);
            }
        });
    }
    static void initTwitterEmail( final TwitterAuthClient customAuthClient,final MainActivity mainActivity) {
        customAuthClient.requestEmail(com.twitter.sdk.android.Twitter.getSessionManager().getActiveSession(), new Callback<String>() {
            @Override
            public void success(Result<String> result) {
                // Do something with the result, which provides the email address
                Log.i("robin","initTwitterEmail-->success-->result=="+result);
                Toast.makeText(mainActivity,result.toString(),Toast.LENGTH_LONG).show();
            }

            @Override
            public void failure(TwitterException exception) {
                // Do something on failure
                Log.i("robin","initTwitterEmail-->failure-->result=="+exception);
            }
        });
    }

    static void initTwitterOnActivityResult(int requestCode, int resultCode, Intent data) {
        // Make sure that the loginButton hears the result from any Activity that it triggered.
        //从源码可以看出来requestCode=140（可能是因为Twitter限制140字符的推文的缘故吧-_-!!）
        mLoginButton.onActivityResult(requestCode, resultCode, data);
    }

    static void initCustomTwitter(final Activity activity, View view, final TwitterAuthClient customAuthClient) {
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("robin", "click text to login with custom twitter");
                ISFROMCUSTOMTWITTERBUTTON = true;
                customAuthClient.authorize(activity, new Callback<TwitterSession>() {
                    @Override
                    public void success(Result<TwitterSession> result) {
                        Log.i("robin", "twitter success");
                        // The TwitterSession is also available through:(TwitterSession也可以通过下面的方式获得)
                        // TwitterHelper.getInstance().core.getSessionManager().getActiveSession()
                        TwitterSession session = result.data;
                        String msg = "@" + session.getUserName() + " logged in! (#" + session.getUserId() + ")";
                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void failure(TwitterException exception) {
                        Log.d("robin", "Login with TwitterHelper failure", exception);
                    }
                });
            }
        });
    }

    static void initCustomTwitterOnActivityResult(int requestCode, int resultCode, Intent data, TwitterAuthClient customAuthClient) {
        customAuthClient.onActivityResult(requestCode, resultCode, data);
    }
}
