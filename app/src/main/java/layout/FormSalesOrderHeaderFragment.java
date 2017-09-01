/******************************************************************************
    Author           : ADI
    Description      : untuk mengisi header sales order
    History          :

******************************************************************************/
package layout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;

import com.inspira.gms.GlobalVar;
import com.inspira.gms.IndexInternal;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import static com.inspira.gms.IndexInternal.global;

//import android.app.Fragment;

public class FormSalesOrderHeaderFragment extends Fragment implements View.OnClickListener{

    private TextView tvDate, tvCustomer, tvSales, tvBroker, tvValuta;
    private Button btnNext;
    private DatePickerDialog dp;

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
        getActivity().setTitle("Filter Sales Order");
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
        tvSales = (TextView) getView().findViewById(R.id.tvSales);
        tvBroker = (TextView) getView().findViewById(R.id.tvBroker);
        tvValuta = (TextView) getView().findViewById(R.id.tvValuta);
        tvDate = (TextView) getView().findViewById(R.id.tvDate); //added by Tonny @30-Aug-2017
        btnNext = (Button) getView().findViewById(R.id.btnNext);

        tvCustomer.setOnClickListener(this);
        tvSales.setOnClickListener(this);
        tvBroker.setOnClickListener(this);
        tvValuta.setOnClickListener(this);
        tvDate.setOnClickListener(this);  //added by Tonny @30-Aug-2017
        btnNext.setOnClickListener(this);

        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nama, "").toUpperCase());
        tvSales.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_sales_nama, "").toUpperCase());
        tvValuta.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, "").toUpperCase());
        tvBroker.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nama, "").toUpperCase());


        if (!LibInspira.getShared(global.temppreferences, global.temp.salesorder_date, "").equals("")){
            tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_date, ""));
        }
        // Declare DatePicker
        Calendar newCalendar = Calendar.getInstance();
        dp = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String date = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date newdate = sdf.parse(date);
                    date = sdf.format(newdate);

                    tvDate.setText(date);
                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_date, tvDate.getText().toString());
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));
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
        else if(id==R.id.tvSales)
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
        else if(id==R.id.tvDate) {  //added by Tonny @30-Aug-2017
            dp.show();
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
