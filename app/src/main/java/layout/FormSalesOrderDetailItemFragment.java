/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
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

import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import java.math.BigDecimal;

import static com.inspira.gms.IndexInternal.global;

//import android.app.Fragment;

public class FormSalesOrderDetailItemFragment extends Fragment implements View.OnClickListener{

    private TextView tvItem, tvCode, tvSatuan, tvDisc, tvNetto, tvSubtotal;
    private EditText etQty, etDisc, etNotes, etFee, etPrice;
    private Button btnAdd;

    public FormSalesOrderDetailItemFragment() {
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
        getActivity().setTitle("Sales Order");
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

        tvItem = (TextView) getView().findViewById(R.id.tvItem);
        tvCode = (TextView) getView().findViewById(R.id.tvCode);
        tvSatuan = (TextView) getView().findViewById(R.id.tvSatuan);
        tvDisc = (TextView) getView().findViewById(R.id.tvDisc);
        tvNetto = (TextView) getView().findViewById(R.id.tvNetto);
        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);

        etQty = (EditText) getView().findViewById(R.id.etQty);
        etDisc = (EditText) getView().findViewById(R.id.etDisc);
        etNotes = (EditText) getView().findViewById(R.id.etNotes);
        etFee = (EditText) getView().findViewById(R.id.etFee);
        etPrice = (EditText) getView().findViewById(R.id.etPrice);

        tvItem.setOnClickListener(this);

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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_price, etPrice.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_fee, etFee.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_disc, etDisc.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_qty, etQty.getText().toString().replace(",", ""));
                refreshData();
            }
        });

        etPrice.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "0"));
        etFee.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "0"));
        etDisc.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "0"));
        etQty.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "0"));

        refreshData();
    }

    private void refreshData()
    {
        tvItem.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama, ""));
        tvCode.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_kode, ""));
        tvSatuan.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_satuan, ""));

        if(!LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama, "").equals(""))
        {
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_price, "0");
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_fee, "0");
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_disc, "0");
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "").equals("")) LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_qty, "0");

            Double qty = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "0"));
            Double price = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "0"));
            Double fee = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "0"));
            Double disc = Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "0"));

            Double totaldisc = disc * price / 100;
            Double netto = price - totaldisc;
            Double subtotal = netto * qty;

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

        if(id==R.id.tvItem)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseBarangFragment());
        }
        else if(id==R.id.btnNext)
        {
            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_sales_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nomor, "").equals("") ||
                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nomor, "").equals("")
                    )
            {
                LibInspira.ShowShortToast(getContext(), "All Field Required");
            }
            else
            {
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailItemListFragment());
            }
        }
    }
}
