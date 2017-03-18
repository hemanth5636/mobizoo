package com.jpmc.hemanth.mobizoo.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.ButtonBarLayout;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.jpmc.hemanth.mobizoo.R;

import org.json.JSONException;
import org.json.JSONObject;

import utilities.Constants;
import utilities.MySingleton;

public class EditUserDetails extends AppCompatActivity implements View.OnClickListener {
    static boolean new_user = false;
    EditText first_name, last_name, details, mobile_number;
    RadioButton gender_male, gender_female;
    Button save_details;
    String mobile;
    String userId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_user_details);
        save_details = (Button) findViewById(R.id.button_save_details);
        first_name = (EditText) findViewById(R.id.editText_first_name);
        last_name = (EditText) findViewById(R.id.editText_last_name);
        details = (EditText) findViewById(R.id.editText_details);
        mobile_number = (EditText) findViewById(R.id.editText_mobile_number);
        gender_female = (RadioButton) findViewById(R.id.radioButton2_gender_female);
        gender_male = (RadioButton) findViewById(R.id.radioButton_gender_male);

        Intent intent = this.getIntent();
        new_user = intent.getBooleanExtra("new_user", false);
        save_details.setOnClickListener(this);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading details.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_USER_DETASILS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                progressDialog.dismiss();
                try {
                    JSONObject jsonObject = new JSONObject(response);
                    JSONObject usrObj = jsonObject.getJSONObject("user");
                    if (TextUtils.isEmpty(usrObj.getString("first_name"))) {
                        first_name.setText("Enter First Name");
                    }
                    else {
                        first_name.setText(usrObj.getString("first_name"));
                    }
                    if (TextUtils.isEmpty(usrObj.getString("last_name"))) {
                        last_name.setText(usrObj.getString("Enter Last Name"));
                    }
                    else {
                        last_name.setText(usrObj.getString("last_name"));
                    }
                    if (TextUtils.isEmpty(usrObj.getString("details")))
                        details.setText("Enter Details");
                    else
                        details.setText(usrObj.getString("details"));
                    if (TextUtils.isEmpty(usrObj.getString("mobile")))
                        mobile_number.setText("Enter Mobile Number");
                    else
                        mobile_number.setText(usrObj.getString("mobile"));

                    if (usrObj.get("gender") == null) {
                        gender_male.setChecked(false);
                        gender_female.setChecked(false);
                    }
                    else if(usrObj.getInt("gender") == 0)
                    {
                        gender_female.setChecked(true);
                        gender_male.setChecked(false);
                    }
                    else {
                        gender_female.setChecked(false);
                        gender_male.setChecked(true);
                    }
                    mobile = usrObj.getString("mobile");
                    userId = usrObj.getString("id");
                } catch (JSONException e) {
                    Log.e("edit user: on response","json exception"+e.getMessage());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
            progressDialog.dismiss();
                Log.e("fetch user details","volley error"+ error.getMessage());
                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_edit_user_details),"No network connection",Snackbar.LENGTH_LONG);
            }
        });
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);
        if (new_user) {

        }
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.button_save_details) {
            final ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setMessage("saving details...");
            progressDialog.setCancelable(false);
            progressDialog.show();
            JSONObject patchObj = null;
            try {
                 patchObj = new JSONObject();
                patchObj.put("first_name",first_name.getText());
                patchObj.put("last_name", last_name.getText());
                patchObj.put("details", details.getText());
                patchObj.put("mobile", mobile_number.getText());
                if(gender_female.isChecked())
                    patchObj.put("gender", 0);
                if (gender_male.isChecked())
                    patchObj.put("gender", 1);

            } catch (JSONException e) {
                progressDialog.dismiss();
                Log.e("savedetails","jsonexception "+e.getMessage());
            }
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.PATCH,
                    Constants.BASE_URL + "/api/user_api/user/" + userId + "/?format=json&type=user", patchObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {

                    if(!mobile.equals(mobile_number.getText().toString())) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());
                        builder.setMessage("An OTP will be sent the registered mobile number");
                        builder.setCancelable(false);
                        builder.setTitle("Alert");
                        builder.setPositiveButton("ok", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.REQUEST_OTP_URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                try {
                                    progressDialog.dismiss();
                                    JSONObject jsonObject = new JSONObject(response);
                                    if (jsonObject.getBoolean("success") ) {
                                       // Toast.makeText(getApplicationContext(), "OTP successfully sent to "+mobile_number.getText().toString(), Toast.LENGTH_LONG).show();
                                        Intent intent = new Intent(getApplicationContext(), Otp_verify.class);
                                        intent.putExtra("mobile",mobile_number.getText().toString());
                                        startActivity(intent);
                                    }
                                    else {
                                        Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_edit_user_details), jsonObject.getString("details"), Snackbar.LENGTH_LONG);
                                    }
                                } catch (JSONException e) {
                                    Log.e("request OTP"," json error " + e.getMessage());
                                }
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                progressDialog.dismiss();
                                Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_edit_user_details),"error in network connection",Snackbar.LENGTH_LONG);
                            }
                        });

                    }
                    else {
                        progressDialog.dismiss();
                    }
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_edit_user_details),"Successfully saved",Snackbar.LENGTH_LONG);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    progressDialog.dismiss();
                    Log.e("save user details","volley error"+ error.getMessage());
                    Snackbar snackbar = Snackbar.make(findViewById(R.id.activity_edit_user_details),"error in network connection",Snackbar.LENGTH_LONG);
                }
            });
            MySingleton.getInstance(this).addToRequestQueue(jsonObjectRequest);
        }
    }
}
