package fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.os.TraceCompat;
import android.support.v7.widget.ButtonBarLayout;
import android.support.v7.widget.CardView;
import android.text.Layout;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.RelativeLayout;

import com.jpmc.hemanth.mobizoo.R;


public class SendMoneyFragmaent extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    EditText editTextAmount, editTextMessage, editTextMobile, editTextUpiAddress, editTextHoldersName, editTextAccountNo, editTextIFSCCode;
    ImageView imageViewContacts;
    Button buttonSend;

    RelativeLayout layout_bank_details;
    RadioButton radioButtonBankDetails, radioButtonMobile, radioButtonUpi;
    CardView card_view_mobile, card_view_upi;

    // TODO: Rename and change types of parameters
    private int page;
    private String title;


    public SendMoneyFragmaent() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SendMoneyFragmaent.
     */
    // TODO: Rename and change types and number of parameters
    public static SendMoneyFragmaent newInstance(int param1, String param2) {
        SendMoneyFragmaent fragment = new SendMoneyFragmaent();
        Bundle args = new Bundle();
        args.putInt("page", param1);
        args.putString("title", param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            page = getArguments().getInt("page");
            title = getArguments().getString("title");
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_send_money_fragmaent, container, false);
        radioButtonBankDetails = (RadioButton) view.findViewById(R.id.radioButton_bank_details);
        radioButtonMobile = (RadioButton) view.findViewById(R.id.radioButton_mobile);
        radioButtonUpi = (RadioButton) view.findViewById(R.id.radioButton_upi);

        card_view_mobile = (CardView) view.findViewById(R.id.card_view_mobile);
        layout_bank_details = (RelativeLayout) view.findViewById(R.id.layout_bank_details);
        card_view_upi = (CardView) view.findViewById(R.id.card_view_upi);

        layout_bank_details.setVisibility(View.GONE);
        card_view_upi.setVisibility(View.GONE);
        card_view_mobile.setVisibility(View.VISIBLE);

        radioButtonMobile.setChecked(true);
        radioButtonUpi.setChecked(false);
        radioButtonBankDetails.setChecked(false);
        radioButtonBankDetails.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButtonBankDetails.isChecked()) {
                    layout_bank_details.setVisibility(View.VISIBLE);
                    card_view_upi.setVisibility(View.GONE);
                    card_view_mobile.setVisibility(View.GONE);

                    radioButtonMobile.setChecked(false);
                    radioButtonUpi.setChecked(false);
                    radioButtonBankDetails.setChecked(true);
                }
            }
        });

        radioButtonUpi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(radioButtonUpi.isChecked()) {
                    layout_bank_details.setVisibility(View.GONE);
                    card_view_upi.setVisibility(View.VISIBLE);
                    card_view_mobile.setVisibility(View.GONE);

                    radioButtonMobile.setChecked(false);
                    radioButtonUpi.setChecked(true);
                    radioButtonBankDetails.setChecked(false);
                }
            }
        });

        radioButtonMobile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                layout_bank_details.setVisibility(View.GONE);
                card_view_upi.setVisibility(View.GONE);
                card_view_mobile.setVisibility(View.VISIBLE);

                radioButtonMobile.setChecked(true);
                radioButtonUpi.setChecked(false);
                radioButtonBankDetails.setChecked(false);
            }
        });

        editTextAmount = (EditText) view.findViewById(R.id.editText_amount);
        buttonSend = (Button) view.findViewById(R.id.button_send_money);
        buttonSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (TextUtils.isEmpty(editTextAmount.getText())) {
                    Snackbar.make(v, "please enter amount", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        return view;
    }

}

