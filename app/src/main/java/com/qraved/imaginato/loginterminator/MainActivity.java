package com.qraved.imaginato.loginterminator;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.common.SignInButton;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;

public class MainActivity extends AppCompatActivity {
    public static final int REQUEST_CODE_GOOGLE_PLUS = 10001;
    public static final int REQUEST_CODE_TWITTER = 140;//这个值一定一定要和TwitterAuthConfig.DEFAULT_AUTH_REQUEST_CODE一致，否则会失败。
    TwitterAuthClient customAuthClient;//如果要在工具类里面new的话，那么这个东西就要指定为static 但是他可能会内存泄漏，所以只能在这里面去new
    private LoginButton lbFacebook;
    private AlxFacebookHelper facebookHelper = new AlxFacebookHelper();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_main);
        SignInButton sign_in_button = (SignInButton) findViewById(R.id.sign_in_button);
        TwitterLoginButton loginButton = (TwitterLoginButton) findViewById(R.id.twitter_login_button);
        TextView textView = (TextView) findViewById(R.id.tv_1);

        GooglePlus.initGooglePlus(MainActivity.this, sign_in_button);
        Twitter.initTwitter(MainActivity.this, loginButton);//pay attention to init fabric in this method.
        customAuthClient = new TwitterAuthClient();
        Twitter.initCustomTwitter(MainActivity.this, textView, customAuthClient);
        initFacebook(findViewById(R.id.iv_facebook_login));
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
        if(facebookHelper != null && facebookHelper.facebookCallbackManager!= null)facebookHelper.facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
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
    
    public void initFacebook(View mButton){
        lbFacebook = new LoginButton(this);
        facebookHelper.initFBLoginButton(mButton, lbFacebook, null, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                Log.i("AlexFB","facebook登录成功");
                if(loginResult == null) return;
                Log.i("AlexFB","token是"+loginResult.getAccessToken());
                for(String s:loginResult.getRecentlyGrantedPermissions()){
                    Log.i("AlexFB","被授予的权限是::"+s);
                }
                getFacebookUserBasicInfo();//获取用户邮箱，姓名，头像等基本信息
            }

            @Override
            public void onCancel() {
                // App code  
                Log.i("AlexFB","facebook登录被取消");
            }

            @Override
            public void onError(FacebookException error) {
                Log.i("AlexFB","facebook登录出现异常",error);

            }

        }, new Runnable() {
            @Override
            public void run() {
                Log.i("Alex","点击了tv");
                //写一些其他的点击事件，比如动画，事件追踪等  
            }
        });
    }

    private void getFacebookUserBasicInfo(){
        AlxFacebookHelper.getUserFacebookBasicInfo(new AlxFacebookHelper.FacebookUserInfoCallback() {
            @Override
            public void onCompleted(AlxFacebookHelper.FaceBookUserInfo userInfo) {
                Log.i("Alex","获取到的facebook用户信息是:::"+userInfo);
                getFacebookUserImage(userInfo.id);//获取用户头像
            }

            @Override
            public void onFailed(String reason) {
                Log.i("AlexFB","获取facebook用户信息失败::"+reason);
                AlxFacebookHelper.signOut();
            }
        });
    }

    private void getFacebookUserImage(String facebookUserId){
        AlxFacebookHelper.getFacebookUserPictureAsync(facebookUserId,new AlxFacebookHelper.FacebookUserImageCallback() {
            @Override
            public void onCompleted(String imageUrl) {
                //成功获取到了头像之后
                Log.i("Alex","用户高清头像的下载url是"+imageUrl);
            }

            @Override
            public void onFailed(String reason) {
                AlxFacebookHelper.signOut();//如果获取失败了，别忘了将整个登录结果回滚
                Log.i("AlexFB",reason);
            }
        });
    }

}
