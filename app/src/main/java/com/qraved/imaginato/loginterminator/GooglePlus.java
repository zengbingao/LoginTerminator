package com.qraved.imaginato.loginterminator;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;

/**
 * Created by robin on 2016/10/14.
 */

class GooglePlus {
    private MainActivity activity;
    GoogleApiClient initGooglePlus(Activity activity, SignInButton sign_in_button) {
        this.activity = (MainActivity) activity;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                .build();

        GoogleApiClient mGoogleApiClient = new GoogleApiClient
                .Builder(activity)
                .addConnectionCallbacks(new MyGoogleConnectionCallbacks())
                .addOnConnectionFailedListener(new MyGoogleConnectionFaliedListeners())
                .enableAutoManage((FragmentActivity) activity, new MyGoogleConnectionFaliedListeners())/* FragmentActivity *//* OnConnectionFailedListener */
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();

        
        sign_in_button.setSize(SignInButton.SIZE_STANDARD);
        sign_in_button.setScopes(gso.getScopeArray());
        sign_in_button.setOnClickListener(new MyClickListener());
        return mGoogleApiClient;
    }
    private class MyGoogleConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks{

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i("robin","google登录-->onConnected,bundle=="+bundle);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i("robin","google登录-->onConnectionSuspended,i=="+i);
        }
    }
    private class MyGoogleConnectionFaliedListeners implements GoogleApiClient.OnConnectionFailedListener{

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i("robin","google登录-->onConnectionFailed,connectionResult=="+connectionResult);
        }
    }
    private class MyClickListener implements View.OnClickListener{
        @Override
        public void onClick(View v) {
            Log.i("robin","点击了登录按钮");
            activity.signIn();
        }
    }
    
    void handleSignInResult(GoogleSignInResult result){
        Log.i("robin","handleSignInResult执行了");
        if(result.isSuccess()){
            Log.i("robin", "成功");
            GoogleSignInAccount acct = result.getSignInAccount();
            if(acct!=null){
                Log.i("robin", "用户名是:" + acct.getDisplayName());
                Log.i("robin", "用户email是:" + acct.getEmail());
                Log.i("robin", "用户头像是:" + acct.getPhotoUrl());
                Log.i("robin", "用户Id是:" + acct.getId());//之后就可以更新UI了
                Toast.makeText(activity,"用户名是:" + acct.getDisplayName()+"\n用户email是:" + acct.getEmail()+"\n用户头像是:" + acct.getPhotoUrl()+ "\n用户Id是:" + acct.getId(),Toast.LENGTH_LONG).show();
            }
        }else{
            Toast.makeText(activity,"登录失败,结果是:"+result.getStatus(),Toast.LENGTH_LONG).show();
            Log.i("robin", "没有成功"+result.getStatus());
        }
    }

}
