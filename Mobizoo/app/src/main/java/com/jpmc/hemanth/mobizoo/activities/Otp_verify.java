package com.jpmc.hemanth.mobizoo.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jpmc.hemanth.mobizoo.R;

import org.json.JSONException;
import org.json.JSONObject;

import utilities.Constants;
import utilities.MySingleton;

public class Otp_verify extends AppCompatActivity implements View.OnClickListener {
    ProgressDialog progressDialog;
    Button verify, resend;
    EditText otp;
    String mobileNum;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otp_verify);
        verify = (Button) findViewById(R.id.button_verfy);
        resend = (Button) findViewById(R.id.button_resend);
        otp = (EditText) findViewById(R.id.editText_otp);
        progressDialog.setCancelable(false);
        verify.setOnClickListener(this);
        resend.setOnClickListener(this);
        mobileNum = this.getIntent().getStringExtra("mobile");
        Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_otp_verify), "OTP successfully sent to "+mobileNum, Snackbar.LENGTH_LONG);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_verfy) {
            if(TextUtils.isEmpty(otp.getText())) {
                Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_otp_verify), "please enter OTP", Snackbar.LENGTH_LONG);
            }
            else {
                progressDialog.setMessage("verifying OTP...");
                progressDialog.show();
                StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.VERIFY_OTP_URL+otp.getText().toString()+"/?format=json&type=user", new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        progressDialog.dismiss();
                        try {
                            JSONObject jsonObject = new JSONObject(response);
                            if(jsonObject.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "OTP successfully verified", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), HomeScreen.class);
                                startActivity(intent);
                            }
                            else {
                                Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_otp_verify), "incorrect OTP entered", Snackbar.LENGTH_LONG);
                            }
                        } catch (JSONException e) {
                            Log.e("verify otp","json exception "+e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Log.e("verify otp","volley error "+error.getMessage());
                    }
                });
                MySingleton.getInstance(this).addToRequestQueue(stringRequest);
            }
        }
        else if(v.getId() == R.id.button_resend) {
            progressDialog.setMessage("requesting OTP...");
            progressDialog.show();
            StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.RESEND_OTP_URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    progressDialog.dismiss();
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getBoolean("success")) {
                            Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_otp_verify), "OTP resend successful", Snackbar.LENGTH_LONG);
                        }
                        else
                        {
                            Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_otp_verify), jsonObject.getString("details"), Snackbar.LENGTH_LONG);
                        }
                    } catch (JSONException e) {
                        Log.e("resend otp", "json exception " + e.getMessage());
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.e("resend otp","volley error "+error.getMessage());
                    Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_otp_verify), "No network connection", Snackbar.LENGTH_LONG);
                }
            });
            MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        }
    }
}
