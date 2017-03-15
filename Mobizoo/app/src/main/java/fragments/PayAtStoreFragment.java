package fragments;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.zxing.Result;
import com.jpmc.hemanth.mobizoo.R;
import com.jpmc.hemanth.mobizoo.activities.QRScannerActivity;

import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class PayAtStoreFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private ImageView imageView;
    private TextView textView;
    private ZXingScannerView mScannerView;
    private Button payToVendorButton;
    private EditText vendorCodeEdittext, amountEdittext;
    private static final String ARG_PARAM2 = "title";

    // TODO: Rename and change types of parameters
    private int page;
    private String title;



    public PayAtStoreFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PayAtStoreFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PayAtStoreFragment newInstance(int param1, String param2) {
        PayAtStoreFragment fragment = new PayAtStoreFragment();
        Bundle args = new Bundle();
        args.putInt("page", param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt("page");
            title = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pay_at_store, container, false);
        imageView = (ImageView) view.findViewById(R.id.scan_qr_image);
        textView = (TextView) view.findViewById(R.id.scan_qr_textview);

        imageView.setOnClickListener(this);
        textView.setOnClickListener(this);

        payToVendorButton = (Button) view.findViewById(R.id.pay_to_vendor_button);
        vendorCodeEdittext = (EditText) view.findViewById(R.id.edit_text_vendor_code);
        amountEdittext = (EditText) view.findViewById(R.id.editText_amount);

        payToVendorButton.setOnClickListener(this);

        return view;
    }



    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {

    }


    @Override
    public void onDetach() {
        super.onDetach();

    }


    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.scan_qr_image || v.getId() == R.id.scan_qr_textview) {
            Intent intent = new Intent(this.getContext(), QRScannerActivity.class);
            this.startActivity(intent);
        }
        else if(v.getId() == R.id.pay_to_vendor_button)
        {
            if ((TextUtils.isEmpty(vendorCodeEdittext.getText()))) {
                Snackbar.make(v, "enter a valid vendor code", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }
            if (TextUtils.isEmpty(amountEdittext.getText())) {
                Snackbar.make(v, "please enter amount", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return;
            }

        }
    }


}
