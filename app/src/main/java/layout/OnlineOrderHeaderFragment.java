/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan header dari online order
 History          :

 ******************************************************************************/
package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;
import static com.inspira.gms.IndexInternal.global;

public class OnlineOrderHeaderFragment extends Fragment{
    private TextView tvCustomer, tvDate, tvGrandTotal, tvDiscNominal, tvPPNNominal, tvSubtotal;
    private EditText etDisc, etPPN;
    private Button btnSave;
    public OnlineOrderHeaderFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_online_order_header, container, false);
        getActivity().setTitle("Online Order - Header");
        return v;
    }


    /*****************************************************************************/
    //OnAttach dijalankan pada saat fragment ini terpasang pada Activity penampungnya
    /*****************************************************************************/
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    //added by Tonny @15-Jul-2017
    //untuk mapping UI pada fragment, jangan dilakukan pada OnCreate, tapi dilakukan pada onActivityCreated
    @Override
    public void onActivityCreated(Bundle bundle){
        super.onActivityCreated(bundle);
        tvDate = (TextView) getView().findViewById(R.id.tvDate);
        tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_date, ""));
        tvCustomer = (TextView) getView().findViewById(R.id.tvCustomer);
        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_customer_nama, ""));
        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);
        tvSubtotal.setText(LibInspira.delimeter(getSubtotal().toString()));
        etDisc = (EditText) getView().findViewById(R.id.etDisc);
        etDisc.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_disc, "0")));
        etPPN = (EditText) getView().findViewById(R.id.etPPN);
        etPPN.setText(LibInspira.delimeter(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_ppn, "0")));
        tvDiscNominal = (TextView) getView().findViewById(R.id.tvDiscNominal);
        tvPPNNominal = (TextView) getView().findViewById(R.id.tvPPNNominal);
        tvGrandTotal = (TextView) getView().findViewById(R.id.tvGrandTotal);
        btnSave = (Button) getView().findViewById(R.id.btnSave);
        btnSave.setVisibility(View.GONE);

        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_total, String.valueOf(getGrandTotal()));
        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_disc, "0");
        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_disc_nominal, "0");
        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_ppn, "0");
        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_ppn_nominal, "0");

        etDisc.addTextChangedListener(new TextWatcher() {
                  @Override
                  public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

                  }

                  @Override
                  public void afterTextChanged(Editable editable) {
                      LibInspira.formatNumberEditText(etDisc, this, true, false);
                      tvDiscNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
                      tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
                      LibInspira.setShared(global.temppreferences, global.temp.onlineorder_total, String.valueOf(getGrandTotal()));
                      LibInspira.setShared(global.temppreferences, global.temp.onlineorder_disc, etDisc.getText().toString().replace(",", ""));
                      LibInspira.setShared(global.temppreferences, global.temp.onlineorder_disc_nominal, tvDiscNominal.getText().toString().replace(",", ""));
                  }
              });

        etPPN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etPPN, this, true, false);
                tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
                tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
                LibInspira.setShared(global.temppreferences, global.temp.onlineorder_total, String.valueOf(getGrandTotal()));
                LibInspira.setShared(global.temppreferences, global.temp.onlineorder_ppn, etPPN.getText().toString().replace(",", ""));
                LibInspira.setShared(global.temppreferences, global.temp.onlineorder_ppn_nominal, tvPPNNominal.getText().toString().replace(",", ""));
            }
        });
    }

    //untuk mendapatkan nominal diskon
    private Double getNominalDiskon(){
        Double discNominal = 0.0;
        if (tvSubtotal.getText().toString().equals("")){
            tvSubtotal.setText("0.0");
        }else if (etDisc.getText().toString().equals("")){
            etDisc.setText("0.0");
        }else {
            if (!tvSubtotal.getText().toString().equals("")) {
                Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
                Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
                discNominal = disc * subtotal / 100;
            }
        }
        return discNominal;
    }

    //untuk mendapatkan nominal ppn (setelah diskon)
    private Double getNominalPPN(){
        Double ppnNominal = 0.0;
        if (tvSubtotal.getText().toString().equals("")){
            tvSubtotal.setText("0.0");
        }else if(etPPN.getText().toString().equals("")){
            etPPN.setText("0.0");
        }else if (etDisc.getText().toString().equals("")){
            etDisc.setText("0.0");
        }else {
            Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
            Double ppn = Double.parseDouble(etPPN.getText().toString().replace(",", ""));
            Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
            Double discNominal = disc * subtotal / 100;
            ppnNominal = ppn * (subtotal - discNominal) / 100;
        }
        return ppnNominal;
    }

    private Double getSubtotal(){
        return Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_subtotal, ""));
    }

    private Double getGrandTotal(){
//        Double grandtotal = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_subtotal, "")) - Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_disc_nominal,"")) +
//                Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_ppn_nominal, ""));
        Double grandtotal = getSubtotal() - getNominalDiskon() + getNominalPPN();
        return grandtotal;
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

}
