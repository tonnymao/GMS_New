/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan form isian pekerjaan pada sales order
 History          :

 ******************************************************************************/
package layout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigDecimal;

import static com.inspira.gms.IndexInternal.global;
import static com.inspira.gms.IndexInternal.jsonObject;

//import android.app.Fragment;

public class FormSalesOrderDetailJasaFragment extends FormSalesOrderDetailItemFragment implements View.OnClickListener {

    public FormSalesOrderDetailJasaFragment() {
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
        View v = inflater.inflate(R.layout.fragment_sales_order_detail_item, container, false);
        getActivity().setTitle("Sales Order - New Pekerjaan");
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
    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
    }

    @Override
    protected void init(){
        etPrice.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etPrice, this, true, false);
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_price, etPrice.getText().toString().replace(",", ""));
                tvNetto.setText(etPrice.getText());
                //hitungSubtotal();
                refreshData();
            }
        });

        etFee.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etFee, this, true, false);
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_fee, etFee.getText().toString().replace(",", ""));
                //hitungSubtotal();
                refreshData();
            }
        });

        etDisc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_disc, etDisc.getText().toString().replace(",", ""));
                //hitungSubtotal();
                refreshData();
            }
        });

        etQty.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etQty, this, true, false);
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_qty, etQty.getText().toString().replace(",", ""));
                //hitungSubtotal();
                refreshData();
            }
        });

        etNotes.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_notes, etNotes.getText().toString().trim());
                refreshData();
            }
        });

        etPrice.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_price, "0"));
        etFee.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_fee, "0"));
        etDisc.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_disc, "0"));
        etQty.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_qty, "0"));
        etNotes.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_notes, ""));

        refreshData();
    }

    @Override
    protected void refreshData() {
        //super.refreshData();
        tvItem.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_nama, ""));
        tvCode.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_kode, ""));
        tvSatuan.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_satuan, ""));

        if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_nama, "").equals(""))
        {
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_price, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_price, "0");
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_fee, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_fee, "0");
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_disc, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_disc, "0");
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_qty, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_qty, "0");

            Double qty = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_qty, "0"));
            Double price = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_price, "0"));
            Double fee = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_fee, "0"));
            Double disc = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_disc, "0"));

            Double totaldisc = disc * price / 100;
            Double netto = price - totaldisc;
            Double subtotal = netto * qty + fee;

            //added by Tonny @06-Sep-2017
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, subtotal.toString());

            tvDisc.setText(LibInspira.delimeter(String.valueOf(totaldisc)));
            tvNetto.setText(LibInspira.delimeter(String.valueOf(netto)));
            tvSubtotal.setText(LibInspira.delimeter(String.valueOf(subtotal)));
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(GlobalVar.buttoneffect);
        int id = view.getId();

        LibInspira.setShared(global.sharedpreferences, global.shared.position, "salesorder");

        if (id == R.id.tvItem) {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseJasaFragment());
        } else if (id == R.id.btnAdd)  //modified by Tonny @01-Sep-2017
        {
            //urutannya: nomor~kode~nama~satuan~price~qty~fee~disc
            String strData = LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, "") + //salesorderitem di bagian depan
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_nomor, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_kode, "") + "~" +
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_nama, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_satuan, "") + "~" +
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_price, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_qty, "") + "~" +
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_fee, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_disc, "") + "~" +
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_notes, "") + "|";

            LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan, strData);
            LibInspira.BackFragment(getFragmentManager());
        }
    }
}