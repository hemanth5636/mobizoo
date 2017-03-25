package com.jpmc.hemanth.mobizoo.activities;

import android.content.Context;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jpmc.hemanth.mobizoo.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pojo.BankListPojo;
import utilities.Constants;
import utilities.MySingleton;
import utilities.UserSessionDetails;

public class ManageBankDetailsActivity extends AppCompatActivity {
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    FloatingActionButton fab;
    private RecyclerView.LayoutManager mLayoutManager;
    TextView noBankList;
    BankListPojo bankListPojo;
    List<BankListPojo> list = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_bank_details);
        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_banks);
        fab = (FloatingActionButton) findViewById(R.id.floatingActionButton_add_bank);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Add_bank_details.class);
                startActivity(intent);
            }
        });
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        noBankList = (TextView) findViewById(R.id.textView_no_bank_details);

        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_BANKS_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                JSONObject jsonObject;
                try {
                    jsonObject = new JSONObject(response);
                    JSONArray jsonArray = jsonObject.getJSONArray("objects");
                    for (int j = 0; j<jsonArray.length(); j++ ) {
                        JSONObject data = jsonArray.getJSONObject(j);
                        bankListPojo = new BankListPojo();
                        bankListPojo.setAccount_no(data.getString("account_no"));
                        bankListPojo.setAccount_state(data.getInt("account_state"));
                        bankListPojo.setHolder_name(data.getString("holder_name"));
                        bankListPojo.setId(data.get("id").toString());
                        bankListPojo.setResource_url(data.getString("resource_uri"));
                        bankListPojo.setUser(data.getString("user"));
                        bankListPojo.setBank_name(data.getJSONObject("bank").getString("name"));
                        list.add(bankListPojo);
                    }
                    if (jsonArray.length() == 0) {
                        noBankList.setVisibility(View.VISIBLE);
                        mRecyclerView.setVisibility(View.GONE);
                    }
                    else {
                        noBankList.setVisibility(View.GONE);
                        mRecyclerView.setVisibility(View.VISIBLE);
                        mAdapter = new MyAdapter(list, getApplicationContext());
                        mRecyclerView.setAdapter(mAdapter);
                    }
                } catch (JSONException e) {
                    Log.e("manage bank details ","json error "+e.getMessage()+e.getStackTrace().toString());
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>();
                UserSessionDetails obj = new UserSessionDetails();
                obj.initilisePrefer(UserSessionDetails.USER_PREFER, getApplicationContext());
                Log.e("sessionid",obj.getSessionKey());
                headers.put("Cookie","sessionid="+obj.getSessionKey());
                return headers;

            } };
        MySingleton.getInstance(this).addToRequestQueue(stringRequest);

    }

    private class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
        List<BankListPojo> list1;
        Context context;
        public MyAdapter(List<BankListPojo> p, Context context) {
            list1 = p;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.mange_bank_item_view, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {
            BankListPojo obj = list1.get(position);
            holder.bank_name.setText(obj.getBank_name());
            holder.account_no.setText(obj.getAccount_no());
            holder.holder_name.setText(obj.getHolder_name());
            holder.delete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
            holder.cointainer.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });
        }

        @Override
        public int getItemCount() {
            return list1.size();
        }


        public class ViewHolder extends RecyclerView.ViewHolder {
            TextView bank_name, holder_name, account_no;
            Button delete;
            Switch activate;
            View cointainer;
            public ViewHolder(View itemView) {
                super(itemView);
                bank_name = (TextView) itemView.findViewById(R.id.manage_bank_name);
                holder_name = (TextView) itemView.findViewById(R.id.manage_bank_holder_name);
                account_no = (TextView) itemView.findViewById(R.id.manage_bank_account_no);
                delete = (Button) itemView.findViewById(R.id.button_delete_bank);
                activate = (Switch) itemView.findViewById(R.id.switch1);
                cointainer = itemView;
            }
        }
    }
}

