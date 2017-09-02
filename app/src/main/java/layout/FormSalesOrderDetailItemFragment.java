/******************************************************************************
    Author           : ADI
    Description      : untuk menampilkan detail item pada sales order
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
import static com.inspira.gms.IndexInternal.global;

public class FormSalesOrderDetailItemFragment extends Fragment implements View.OnClickListener{

    protected TextView tvItem, tvCode, tvSatuan, tvDisc, tvNetto, tvSubtotal;
    protected EditText etQty, etDisc, etNotes, etFee, etPrice;
    protected Button btnAdd;

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
        getActivity().setTitle("Sales Order - New Item");
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
        btnAdd = (Button) getView().findViewById(R.id.btnAdd);
        btnAdd.setOnClickListener(this);

        etQty = (EditText) getView().findViewById(R.id.etQty);
        etDisc = (EditText) getView().findViewById(R.id.etDisc);
        etNotes = (EditText) getView().findViewById(R.id.etNotes);
        etFee = (EditText) getView().findViewById(R.id.etFee);
        etPrice = (EditText) getView().findViewById(R.id.etPrice);

        tvItem.setOnClickListener(this);

        init();
    }

    //added by Tonny @02-Sep-2017  untuk inisialisasi textwatcher pada komponen
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_price, etPrice.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_fee, etFee.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_disc, etDisc.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_qty, etQty.getText().toString().replace(",", ""));
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
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_notes, etNotes.getText().toString().trim());
                refreshData();
            }
        });

        etPrice.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "0"));
        etFee.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "0"));
        etDisc.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "0"));
        etQty.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "0"));
        etNotes.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_notes, ""));

        refreshData();
    }

    protected void refreshData()
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
            Double subtotal = netto * qty + fee;

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
        else if (id==R.id.btnAdd)  //modified by Tonny @01-Sep-2017
        {
//            insertOrderDetailItem = new InsertOrderDetailItem();
//            insertOrderDetailItem.execute( actionUrl );

            //urutannya: nomor~kode~nama~satuan~price~qty~fee~disc
            String strData = LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "") + //salesorderitem di bagian depan
                             LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nomor, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_kode, "") + "~" +
                             LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_satuan, "") + "~" +
                             LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_qty, "") + "~" +
                             LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_fee, "") + "~" + LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_disc, "") + "~" +
                             LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_notes, "") + "|";

            LibInspira.setShared(global.temppreferences, global.temp.salesorder_item, strData);
            LibInspira.BackFragment(getFragmentManager());
        }
    }
}
