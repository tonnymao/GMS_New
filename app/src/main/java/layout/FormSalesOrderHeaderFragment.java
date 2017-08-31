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

public class FormSalesOrderHeaderFragment extends Fragment implements View.OnClickListener{

    private TextView tvDate, tvCustomer, tvSales, tvBroker, tvValuta;
    private Button btnNext;

    public FormSalesOrderHeaderFragment() {
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
        View v = inflater.inflate(R.layout.fragment_sales_order_header, container, false);
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

        tvCustomer = (TextView) getView().findViewById(R.id.tvCustomer);
        tvSales = (TextView) getView().findViewById(R.id.tvTarget);
        tvBroker = (TextView) getView().findViewById(R.id.tvBroker);
        tvValuta = (TextView) getView().findViewById(R.id.tvValuta);

        btnNext = (Button) getView().findViewById(R.id.btnNext);

        tvCustomer.setOnClickListener(this);
        tvSales.setOnClickListener(this);
        tvBroker.setOnClickListener(this);
        tvValuta.setOnClickListener(this);
        btnNext.setOnClickListener(this);

        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nama, "").toUpperCase());
        tvSales.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_sales_nama, "").toUpperCase());
        tvValuta.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, "").toUpperCase());
        tvBroker.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nama, "").toUpperCase());
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

        if(id==R.id.tvCustomer)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseCustomerFragment());
        }
        else if(id==R.id.tvTarget)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseSalesmanFragment());
        }
        else if(id==R.id.tvBroker)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseBrokerFragment());
        }
        else if(id==R.id.tvValuta)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseValutaFragment());
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
