package com.jpmc.hemanth.mobizoo.activities;


import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.GoogleApiClient;
import com.jpmc.hemanth.mobizoo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import utilities.Constants;
import utilities.MySingleton;
import utilities.UserSessionDetails;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener, GoogleApiClient.OnConnectionFailedListener {

    public static final String EMAIL = "email";
    public static final String NAME = "name";
    public static final String IMG_URL = "img_url";
    String TAG = "signactivity";
    SignInButton signInButton;
    GoogleApiClient mGoogleApiClient;
    private static final int RC_SIGN_IN = 9001;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        signInButton = (SignInButton) findViewById(R.id.btn_sign_in);
        signInButton.setOnClickListener(this);

        UserSessionDetails obj = new UserSessionDetails();
        obj.initilisePrefer(UserSessionDetails.USER_PREFER, this.getApplicationContext());
        if (obj.isUserLoggedIn()) {
            Intent intent = new Intent(this, HomeScreen.class);
            startActivity(intent);
        }

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail()
                .build();

        // Build a GoogleApiClient with access to the Google Sign-In API and the
// options specified by gso.
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this /* FragmentActivity */, this /* OnConnectionFailedListener */)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.btn_sign_in:
                signIn();

                break;
        }
    }

    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            handleSignInResult(result);
        }
    }

    private void handleSignInResult(GoogleSignInResult result) {
        Log.d(TAG, "handleSignInResult:" + result.isSuccess());
        if (result.isSuccess()) {
            // Signed in successfully, show authenticated UI.
            GoogleSignInAccount acct = result.getSignInAccount();
            JSONObject postData = null;
            try {
                postData = new JSONObject();
                postData.put("email", acct.getEmail());
                postData.put("first_name",acct.getDisplayName());
                postData.put("profile_pic_url", acct.getPhotoUrl().toString());
                postData.put("user_type",0);
            } catch (JSONException error) {
                Toast.makeText(getApplicationContext(), "json error "+error.getMessage(), Toast.LENGTH_LONG).show();
            }
             JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.LOGIN_URL, postData, new Response.Listener<JSONObject>() {
                 @Override
                 public void onResponse(JSONObject jsonObject) {
                     try {

                         boolean success = jsonObject.getBoolean("success");
                         if (success) {
                             JSONObject usrObj = jsonObject.getJSONObject("user");
                            Log.e("response", jsonObject.toString());
                             UserSessionDetails obj = new UserSessionDetails();
                             obj.initilisePrefer(UserSessionDetails.USER_PREFER, getApplicationContext());
                             obj.loginUser(usrObj.getString("first_name"),usrObj.getString("email"),usrObj.getString("profile_pic_url"), jsonObject.getString("sessionKey"));
                             if(!jsonObject.getBoolean("new_user"))
                                 obj.saveMobile(usrObj.getString("mobile"));
                             Log.e("url",usrObj.getString("profile_pic_url"));
                             if (!jsonObject.getBoolean("new_user")) {
                                 Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                 intent.putExtra("new_user", jsonObject.getBoolean("new_user"));
                                 getApplicationContext().startActivity(intent);
                             }
                             else {
                                 Intent intent = new Intent(getApplicationContext(), EditUserDetails.class);
                                 intent.putExtra("new_user", jsonObject.getBoolean("new_user"));
                                 startActivity(intent);
                             }

                         }
                         else {
                             Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_login),jsonObject.getString("details"),Snackbar.LENGTH_LONG);
                         }
                     } catch (JSONException e) {
                         Toast.makeText(getApplicationContext(), "json exception"+e.getMessage(), Toast.LENGTH_LONG).show();
                     }
                 }
             }, new Response.ErrorListener() {
                 @Override
                 public void onErrorResponse(VolleyError error) {
                     Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_login),"no network connection"+error.getMessage(),Snackbar.LENGTH_LONG);
                 }
             })
             ;
            MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonObjectRequest);


            // mStatusTextView.setText(getString(R.string.signed_in_fmt, acct.getDisplayName()));
            //updateUI(true);
            //Intent intent = new Intent(this, ProfileActivity.class);
            //startActivity(intent);
        } else {
            Toast.makeText(this, "login failed",Toast.LENGTH_SHORT).show();
            //        // Signed out, show unauthenticated UI.
            //updateUI(false);
        }

    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Log.e(TAG, "connection failed. try again");
    }
}
