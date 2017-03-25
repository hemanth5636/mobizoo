package com.jpmc.hemanth.mobizoo.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.jpmc.hemanth.mobizoo.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import pojo.BillItemsPojo;
import utilities.Constants;

public class BillActivity extends AppCompatActivity {
    CollapsingToolbarLayout collapsingToolbar;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;
    FloatingActionButton fab;
    private RecyclerView.LayoutManager mLayoutManager;
    List<BillItemsPojo> list =new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bill);

        final Toolbar toolbar = (Toolbar) findViewById(R.id.MyToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        collapsingToolbar =  (CollapsingToolbarLayout) findViewById(R.id.collapse_toolbar);
        collapsingToolbar.setTitle("bill");

        mRecyclerView = (RecyclerView) findViewById(R.id.recycler_view_bill_items);
        mLayoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(mLayoutManager);
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("loading details.");
        progressDialog.setCancelable(false);
        progressDialog.show();
        mAdapter = new MyAdapter(list, getApplicationContext());
        mRecyclerView.setAdapter(mAdapter);
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Constants.FETCH_BILL_URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jsonObject = new JSONObject(response);

                } catch (JSONException e) {
                    Log.e("fetching bill items", "json error : "+e.getStackTrace().toString());
                    if(progressDialog.isShowing())
                        progressDialog.dismiss();
                }
                if(progressDialog.isShowing())
                    progressDialog.dismiss();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                progressDialog.dismiss();
            }
        });

    }

    private class MyAdapter extends RecyclerView.Adapter<ViewHolder> {
        List<BillItemsPojo> list1;
        Context context;
        public MyAdapter(List<BillItemsPojo> list, Context context) {
            list1 = list;
            this.context = context;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View itemView = LayoutInflater.from(parent.getContext())
                    .inflate(R.layout.bill_item_viem, parent, false);
            return new ViewHolder(itemView);
        }

        @Override
        public void onBindViewHolder(ViewHolder holder, int position) {

        }

        @Override
        public int getItemCount() {
            return list1.size();
        }
    }

    private class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(View itemView) {
            super(itemView);
        }
    }
}
