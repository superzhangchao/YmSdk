package com.ym.game.plugin.google.login;

import android.accounts.Account;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;
import com.google.firebase.auth.GoogleAuthProvider;
import com.ym.game.sdk.common.base.config.ErrorCode;
import com.ym.game.sdk.common.base.interfaces.CallBackListener;
import com.ym.game.sdk.common.utils.ResourseIdUtils;

import java.util.Map;

import androidx.annotation.NonNull;

public class GoogleLogin {
    private String TAG = "zc GoogleLogin";
    private volatile static GoogleLogin INSTANCE;
    private GoogleSignInClient mGoogleSignInClient;
    private FirebaseAuth mAuth;
    private static final int RC_SIGN_IN = 9001;
    private CallBackListener mLoginBackListener;
    private CallBackListener mLogoutBackListener;
    private CallBackListener mRevokeAccessListener;
    private boolean isInit = false;
    private GoogleLogin(){}

    public static GoogleLogin getInstance(){
        if (INSTANCE==null){
            synchronized (GoogleLogin.class){
                if (INSTANCE==null){
                    INSTANCE = new GoogleLogin();
                }
            }
        }
        return INSTANCE;
    }

    public void initlogin(Context context){
        isInit = true;
        // [START config_signin]
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(ResourseIdUtils.getStringId("default_web_client_id")))
                .requestEmail()
                .build();
        // [END config_signin]

        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);

        // [START initialize_auth]
        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
    }

    public void login(Context context, Map<String,Object> map, CallBackListener callBackListener){
        if (!isInit){
            initlogin(context);
        }
        mLoginBackListener = callBackListener;
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser!=null){
            updateUI(context,currentUser);
        }else{
            Intent signInIntent = mGoogleSignInClient.getSignInIntent();
            ((Activity) context).startActivityForResult(signInIntent, RC_SIGN_IN);
        }
    }
    public void logout(Context context, CallBackListener callBackListener){
        if (!isInit){
            initlogin(context);
        }
        mLogoutBackListener = callBackListener;
        mAuth.signOut();
//        FirebaseAuth.getInstance().signOut();

        // Google sign out
        mGoogleSignInClient.signOut().addOnCompleteListener(((Activity) context),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(context,null);
                    }
                });
    }
    public void revokeAccess(Context context,CallBackListener callBackListener){
        if (!isInit){
            initlogin(context);
        }
        mRevokeAccessListener = callBackListener;
        // Firebase sign out
        mAuth.signOut();

        // Google revoke access
        mGoogleSignInClient.revokeAccess().addOnCompleteListener(((Activity) context),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        updateUI(context,null);
                    }
                });
    }


    public void onActivityResult(Context context,int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Account account1 = account.getAccount();

//                Log.d(TAG, "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(context,account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
//                Log.w(TAG, "Google sign in failed", e);
                // [START_EXCLUDE]
                updateUI(context,null);
                // [END_EXCLUDE]
            }
        }
    }
    // [START auth_with_google]
    private void firebaseAuthWithGoogle(Context context, String idToken) {
        // [START_EXCLUDE silent]

        // [END_EXCLUDE]
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(((Activity) context), new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
//                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            updateUI(context, user);
                        } else {
                            // If sign in fails, display a message to the user.
//                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //TODO:
                            Exception exception = task.getException();
                            updateUI(context, null);
                        }

                        // [START_EXCLUDE]
//                        hideProgressBar();
                        // [END_EXCLUDE]
                    }
                });
    }

    // [END auth_with_google]
    private void updateUI(Context context,FirebaseUser user) {
        if (user != null) {
            //TODO:可以登录游戏
            // Name, email address, and profile photo Url
            String name = user.getDisplayName();
            String email = user.getEmail();
            Uri photoUrl = user.getPhotoUrl();
            user.getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                String idToken = task.getResult().getToken();
                                // Send token to your backend via HTTPS
                                // TODO:去服务器验证
                                String uid = user.getUid();
                                if (uid!=null){
                                    mLoginBackListener.onSuccess(uid);

                                }else {
                                    mLoginBackListener.onFailure(ErrorCode.FAILURE,context.getString(ResourseIdUtils.getStringId("ym_google_logintfail")));
                                }
                                mLoginBackListener = null;
                            } else {
                                // Handle error -> task.getException();//
                                mLoginBackListener.onFailure(ErrorCode.FAILURE,task.getException().getMessage());
                                mLoginBackListener = null;
                            }
                        }
                    });
            // Check if user's email is verified
            boolean emailVerified = user.isEmailVerified();
            // The user's ID, unique to the Firebase project. Do NOT use this value to
            // authenticate with your backend server, if you have one. Use
            // FirebaseUser.getIdToken() instead.
            String uid = user.getUid();
            Log.i(TAG, "updateUI: loginstate");
        } else {
            //TODO:注销登录状态
            if (mLoginBackListener!=null){
                mLoginBackListener.onFailure(ErrorCode.FAILURE,context.getString(ResourseIdUtils.getStringId("ym_google_logintfail")));
                mLoginBackListener = null;
            }else if (mLogoutBackListener!=null){
                String logoutMessage = context.getString(ResourseIdUtils.getStringId("ym_google_logoutsuccess"));
                mLogoutBackListener.onSuccess(logoutMessage);
                mLogoutBackListener = null;
                Log.i(TAG, "updateUI: logout");
            }else if (mRevokeAccessListener!=null){
                String revokeMessage = context.getString(ResourseIdUtils.getStringId("ym_google_revokeaccesssuccess"));
                mRevokeAccessListener.onSuccess(revokeMessage);
                mRevokeAccessListener = null;
            }
        }
    }
}

