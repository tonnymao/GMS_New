/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
    History          :

******************************************************************************/
package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import static com.inspira.gms.IndexInternal.global;

//import android.app.Fragment;

public class FormSalesOrderDetailItemFragment extends Fragment implements View.OnClickListener{

    private TextView tvItem, tvCode, tvSatuan, tvPrice, tvFee, tvDisc, tvNetto, tvSubtotal;
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
        tvPrice = (TextView) getView().findViewById(R.id.tvPrice);
        tvFee = (TextView) getView().findViewById(R.id.tvFee);
        tvDisc = (TextView) getView().findViewById(R.id.tvDisc);
        tvNetto = (TextView) getView().findViewById(R.id.tvNetto);
        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);

        tvItem.setOnClickListener(this);

        refreshData();
    }

    private void refreshData()
    {
        tvItem.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_nama, ""));
        tvCode.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_kode, ""));
        tvSatuan.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_satuan, ""));
        tvPrice.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_item_price, ""));

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
