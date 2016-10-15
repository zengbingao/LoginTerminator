package com.qraved.imaginato.loginterminator;

import android.app.Activity;
import android.content.Intent;
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

import static com.qraved.imaginato.loginterminator.MainActivity.REQUEST_CODE_GOOGLE_PLUS;

/**login in with GooglePlus
 * Created by robin on 2016/10/14.
 */

class GooglePlus {
    private static MainActivity mainActivity;
    private static GoogleApiClient mGoogleApiClient;

    static void initGooglePlus(Activity activity, SignInButton sign_in_button) {
        mainActivity = (MainActivity) activity;
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .requestId()
                //.requestIdToken(); need server token to set 
                .build();

        mGoogleApiClient = new GoogleApiClient
                .Builder(activity)
                .addConnectionCallbacks(new MyGoogleConnectionCallbacks())
                .addOnConnectionFailedListener(new MyGoogleConnectionFaliedListeners())
                .enableAutoManage((FragmentActivity) activity, new MyGoogleConnectionFaliedListeners())/* FragmentActivity *//* OnConnectionFailedListener */
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();


        sign_in_button.setSize(SignInButton.SIZE_STANDARD);
        sign_in_button.setScopes(gso.getScopeArray());
        sign_in_button.setOnClickListener(new MyClickListener());
    }

    private static class MyGoogleConnectionCallbacks implements GoogleApiClient.ConnectionCallbacks {

        @Override
        public void onConnected(@Nullable Bundle bundle) {
            Log.i("robin", "google登录-->onConnected,bundle==" + bundle);
        }

        @Override
        public void onConnectionSuspended(int i) {
            Log.i("robin", "google登录-->onConnectionSuspended,i==" + i);
        }
    }

    private static class MyGoogleConnectionFaliedListeners implements GoogleApiClient.OnConnectionFailedListener {

        @Override
        public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
            Log.i("robin", "google登录-->onConnectionFailed,connectionResult==" + connectionResult);
        }
    }

    private static class MyClickListener implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            Log.i("robin", "点击了登录按钮");
            Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
            mainActivity.startActivityForResult(signInIntent, REQUEST_CODE_GOOGLE_PLUS);
        }
    }
    
    static void handleSignInResult(Intent data) {
        Log.i("robin", "handleSignInResult执行了");
        GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
        if (result.isSuccess()) {
            Log.i("robin", "handleSignInResult-->success");
            GoogleSignInAccount acct = result.getSignInAccount();
            if (acct != null) {
                /**click this link to see the detail doc of information
                 * <p href="https://developers.google.com/android/reference/com/google/android/gms/auth/api/signin/GoogleSignInAccount</p>
                 */
                Toast.makeText(mainActivity, "用户名是:" + acct.getDisplayName() + ",用户email是:" + acct.getEmail() + ",用户头像是:" + acct.getPhotoUrl() +
                        ",用户Id是:" + acct.getId()+",用户Token是"+acct.getIdToken()+",ServerAuthCode是"+acct.getServerAuthCode(), Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(mainActivity, "failed,result is :" + result.getStatus(), Toast.LENGTH_LONG).show();
        }
    }
    static void initGooglePlusOnstart(){
        if(mGoogleApiClient!=null){
            mGoogleApiClient.connect();
        }
    }
    static void initGooglePlusOnstop(){
        if (mGoogleApiClient!=null&&mGoogleApiClient.isConnected()) {
            mGoogleApiClient.disconnect();
        }
    }

}
