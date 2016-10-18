package com.qraved.imaginato.loginterminator;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookRequestError;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.HttpMethod;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.facebook.share.model.ShareLinkContent;
import com.facebook.share.widget.ShareDialog;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;


/**
 * Created by Alex on 2016/8/16.
 */
public class FacebookHelper {
    public CallbackManager facebookCallbackManager;


    /**
     * 初始化facebook的SDK自带按钮
     * 这里会把点击事件拦住，不会向下层的facebook button传递,然后先执行自定义的点击事件，再执行facebook button的原生点击事件
     * 可以在自定义点击事件里写一些记录追踪或者动画的代码
     * @param clickView 压在facebook原生登录按钮上面的View，用来拦截点击事件,可以传null
     * @param lbFacebook facebook原生登录按钮
     * @param addtionalClickEvent  拦截facebook原生按钮点击事件，并附加一个点击事件，可以传null
     */
    public void initFBLoginButton(View clickView, final LoginButton lbFacebook, Fragment fragment, FacebookCallback<LoginResult> facebookCallback, final Runnable addtionalClickEvent){
        initFBLoginButton(lbFacebook,fragment,facebookCallback);
        if(clickView == null)return;
        clickView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i("AlexFB","点击了facebook上面的按钮");
                signOut();//点击登录按钮先登出
                if(addtionalClickEvent != null)addtionalClickEvent.run();//先执行自定义点击事件
                lbFacebook.callOnClick();//再执行facebook登录按钮原生点击事件
            }
        });
    }

    /**
     * 初始化facebook登录按钮，需要自己写一个登录回调传进来
     * @param lbFacebook
     * @param fragment 可以传null
     * @param facebookCallback
     */
    public void initFBLoginButton(LoginButton lbFacebook, Fragment fragment, FacebookCallback<LoginResult> facebookCallback){
        lbFacebook.setReadPermissions("public_profile", "email", "user_birthday", "user_status","user_friends");
        // 如果你把登陆按钮放在一个fragment里面，就用这个方法，用来使用fragment启动facebook的activity并调用fragment的onActivityResult
        if(fragment != null)lbFacebook.setFragment(fragment);
        // Other app specific specialization
        facebookCallbackManager = CallbackManager.Factory.create();
        // z注册登录回调
        lbFacebook.registerCallback(facebookCallbackManager,facebookCallback);
    }

    /**
     * 获取facebook用户的基本信息
     * @param callback
     */
    public static void getUserFacebookBasicInfo(final FacebookUserInfoCallback callback) {
        // 获取基本文本信息
        Log.i("AlexFB", "准备获取facebook用户基本信息");
        if(callback == null)return;
        GraphRequest request = GraphRequest.newMeRequest(AccessToken.getCurrentAccessToken(), new GraphRequest.GraphJSONObjectCallback() {
            @Override
            public void onCompleted(JSONObject object, GraphResponse response) {
                if (response == null) {
                    callback.onFailed("无法获取用户基本信息");
                    return;
                }
                Log.i("AlexFB", "获取用户基本信息完毕，object是" + object);
                JSONObject responseJsonObject = response.getJSONObject();
                Log.i("AlexFB", "而response 的object是" + responseJsonObject);//这两个jsonObject是一样的
                if (responseJsonObject == null) {
                    callback.onFailed("无法获取用户基本信息2" + response.getError().getErrorType() + "   " + response.getError().getErrorMessage());
                    return;
                }
                FaceBookUserInfo userInfo = new FaceBookUserInfo();
                userInfo.id = getFacebookGraphResponseString(responseJsonObject, "id");
                userInfo.firstName = getFacebookGraphResponseString(responseJsonObject, "first_name");
                userInfo.lastName = getFacebookGraphResponseString(responseJsonObject, "last_name");
                userInfo.userName = getFacebookGraphResponseString(responseJsonObject, "name");
                userInfo.birthday = getFacebookGraphResponseString(responseJsonObject, "birthday");
                userInfo.updateTime = getFacebookGraphResponseString(responseJsonObject, "updated_time");
                userInfo.email = getFacebookGraphResponseString(responseJsonObject, "email");
                userInfo.gender = getFacebookGraphResponseString(responseJsonObject, "gender");
                callback.onCompleted(userInfo);
            }
        });
        Bundle parameters = new Bundle();
        parameters.putString("fields", "id,name,link,email,first_name,last_name,gender,locale,timezone,updated_time,verified");
        request.setParameters(parameters);
        request.executeAsync();
    }

    /**
     * 获取用户的token之后，根据此token联网获取该用户详细信息的回调函数
     */
    public interface FacebookUserInfoCallback{
        void onCompleted(FaceBookUserInfo userInfo);//成功回调
        void onFailed(String reason);//失败回调
    }
    public interface FacebookUserImageCallback{
        void onCompleted(String imageUrl);//成功回调
        void onFailed(String reason);//失败回调
    }

    /**
     * 用于存储Facebook用户基本信息的类
     */
    public static class FaceBookUserInfo {
        public String id;
        public String firstName;
        public String lastName;
        public String userName;
        public String birthday;
        public String location;
        public String updateTime;
        public String email;
        public String gender;
        public String avatar;//头像url

        @Override
        public String toString() {
            final StringBuffer sb = new StringBuffer("FaceBookUserInfo{");
            sb.append("id='").append(id).append('\'');
            sb.append(", firstName='").append(firstName).append('\'');
            sb.append(", lastName='").append(lastName).append('\'');
            sb.append(", userName='").append(userName).append('\'');
            sb.append(", birthday='").append(birthday).append('\'');
            sb.append(", location='").append(location).append('\'');
            sb.append(", updateTime='").append(updateTime).append('\'');
            sb.append(", email='").append(email).append('\'');
            sb.append(", gender='").append(gender).append('\'');
            sb.append(", avatar='").append(avatar).append('\'');
            sb.append('}');
            return sb.toString();
        }
    }

    private static String getFacebookGraphResponseString(JSONObject graphResponse, String flag) {
        String value = "";
        try {
            value = graphResponse.getString(flag);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Log.i("AlexFB","获取用户信息 flag="+flag+"   结果是"+value);
        return value;
    }

    /**
     * 通过facebook的userId,联网异步获取用户的头像url
     * @param facebookUserId
     */
    public static void getFacebookUserPictureAsync(String facebookUserId, final FacebookUserImageCallback callback) {
        Log.i("AlexFB","准备获取用户头像");
        if(callback == null || facebookUserId == null || facebookUserId.length()==0)return;
        Bundle parameters = new Bundle();
        parameters.putBoolean("redirect", false);
        parameters.putString("height", "300");
        parameters.putString("type", "normal");
        parameters.putString("width", "300");
        GraphRequest graphRequest= new GraphRequest(AccessToken.getCurrentAccessToken(), "/" + facebookUserId + "/picture", parameters, HttpMethod.GET, new GraphRequest.Callback() {
            public void onCompleted(GraphResponse response) {
                if (response == null) {
                    callback.onFailed("获取用户facebook头像失败");
                    return;
                }

                if (response.getError() != null) {
                    FacebookRequestError facebookRequestError = response.getError();
                    callback.onFailed("获取用户facebook头像失败2：：" + facebookRequestError.getErrorMessage());
                    return;
                }

                JSONObject responseJsonObject = response.getJSONObject();
                if (responseJsonObject == null) {
                    callback.onFailed("获取用户facebook头像失败3");
                    return;
                }
                Log.i("AlexFB", "facebook直接返回的头像信息是" + responseJsonObject.toString());
                String avatarUrl = "";
                try {
                    JSONObject dataJsonObject = responseJsonObject.getJSONObject("data");
                    avatarUrl = dataJsonObject.getString("url");
                    avatarUrl = URLEncoder.encode(avatarUrl, "UTF-8");
                    Log.i("AlexFB", "用户头像获取完毕 avatarUrl:" + avatarUrl);
                } catch (Exception e) {
                    callback.onFailed("获取用户facebook头像失败4"+e.getStackTrace().toString());
                }
                callback.onCompleted(avatarUrl);
            }
        }
        );
        Log.i("AlexFB","version:"+graphRequest.getVersion()+"");
        graphRequest.executeAsync();
    }

    /**
     * 解绑facebook
     */
    public static void signOut(){
        Log.i("AlexFB","准备正式解绑本地的facebook登录信息");
        try {
            if (AccessToken.getCurrentAccessToken() != null) LoginManager.getInstance().logOut();//登出facebook
        }catch(Exception ex){
            Log.i("AlexFB","登出出现异常");
        }

    }

    /**
     * 使用facebook弹出框进行分享的代码
     */
    public interface ShareFacebookCallback{
        void onSuccess();
        void onFailure(String reason);
    }

    /**
     * 通过facebook sdk的默认弹出框进行分享
     * @param activity
     */
    public static void showFacebookShareDialog(Activity activity, String link, String description, String caption, String imageUrl, ShareFacebookCallback callback) {
        if (activity == null) {
            if(callback!=null)callback.onFailure("activity 为空");
            return;
        }
        if (!ShareDialog.canShow(ShareLinkContent.class)) {
            if(callback!=null)callback.onFailure("现在不能分享");
            return;
        }
        try {
            ShareDialog shareDialog = new ShareDialog(activity);
            ShareLinkContent.Builder builder = new ShareLinkContent.Builder();
            if(!TextUtils.isEmpty(caption)) builder.setContentTitle(caption);
            if(!TextUtils.isEmpty(description)) builder.setContentDescription(description);
            if(!TextUtils.isEmpty(link))builder.setContentUrl(Uri.parse(link));
            if(!TextUtils.isEmpty(imageUrl)) builder.setImageUrl(Uri.parse(imageUrl));
            ShareLinkContent linkContent= builder.build();
            shareDialog.show(linkContent);
            if(callback!=null)callback.onSuccess();
        } catch (Exception ex) {
            if(callback!=null)callback.onFailure(ex.getStackTrace().toString());
        }
    }
}
