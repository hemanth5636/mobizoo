package com.jpmc.hemanth.mobizoo.activities;

import android.app.ProgressDialog;
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
import com.android.volley.toolbox.JsonObjectRequest;
import com.jpmc.hemanth.mobizoo.R;

import org.json.JSONException;
import org.json.JSONObject;

import utilities.Constants;

public class Add_bank_details extends AppCompatActivity implements View.OnClickListener {
    EditText holderName, accountNumber, ifscCode;
    Button saveBankDetails;
    ProgressDialog progressDialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bank_details);
        holderName = (EditText) findViewById(R.id.editText_bank_holder_name);
        ifscCode = (EditText) findViewById(R.id.editText_bank_ifsc_code);
        accountNumber = (EditText) findViewById(R.id.editText_bank_account_no);
        saveBankDetails = (Button) findViewById(R.id.button_bank_submit);
        saveBankDetails.setOnClickListener(this);
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.button_bank_submit) {
            if (TextUtils.isEmpty(holderName.getText())) {
                Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_add_bank_details), "please enter Holder's Name", Snackbar.LENGTH_LONG);
            }
            if (TextUtils.isEmpty(accountNumber.getText())) {
                Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_add_bank_details), "please enter Account Number", Snackbar.LENGTH_LONG);
            }
            if (accountNumber.getText().length() != 11 ) {
                Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_add_bank_details), "account should have 11 Digits", Snackbar.LENGTH_LONG);
            }
            if (TextUtils.isEmpty(ifscCode.getText())) {
                Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_add_bank_details), "please enter IFSC Code", Snackbar.LENGTH_LONG);
            }
            try {
                progressDialog.setMessage("saving details...");
                progressDialog.show();
                JSONObject postObj = new JSONObject();
                postObj.put("holder_name", holderName.getText());
                postObj.put("account_no", accountNumber.getText());
                postObj.put("ifsc_code", ifscCode.getText());

                JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.POST, Constants.SAVE_BANK_DETAILS_URL, postObj, new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        progressDialog.dismiss();
                        try {
                            if(response.getBoolean("success")) {
                                Toast.makeText(getApplicationContext(), "saved successfully...", Toast.LENGTH_LONG).show();
                            }
                        } catch (JSONException e) {
                            Log.e("save_bank_details","json error "+e.getMessage());
                        }
                    }
                }, new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        progressDialog.dismiss();
                        Snackbar snackbar= Snackbar.make(findViewById(R.id.activity_add_bank_details), "No network connection", Snackbar.LENGTH_LONG);
                    }
                });
            } catch (JSONException e) {
                progressDialog.dismiss();
                Log.e("save bank detasils"," json error" + e.getMessage());
            }
        }
    }
}
