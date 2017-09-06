/******************************************************************************
    Author           : Tonny
    Description      : untuk menampilkan detail pekerjaan dalam bentuk list
    History          :

******************************************************************************/
package layout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inspira.gms.IndexInternal.global;
import static com.inspira.gms.IndexInternal.jsonObject;

//import android.app.Fragment;

public class FormSalesOrderDetailJasaListFragment extends FormSalesOrderDetailItemListFragment implements View.OnClickListener{

    public FormSalesOrderDetailJasaListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_sales_order_detail_item_list, container, false);
        getActivity().setTitle("Sales Order - List Pekerjaan");
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
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    protected void refreshList(){
        strData = LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, "");
        super.refreshList();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.fab)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailJasaFragment());
        }
        else if(id==R.id.btnBack)
        {
            LibInspira.BackFragment(getActivity().getSupportFragmentManager());
        }
        else if(id==R.id.btnNext)
        {
            //pengecekan jika user tidak memilih item dan jasa namun ingin melanjutkan, maka tampilkan pesan error
            if (LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "").equals("") &&
                LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, "").equals("")){
                LibInspira.ShowLongToast(getContext(), "There is no item and pekerjaan to proceed. Please choose item or pekerjaan first.");
            }else {
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new SummarySalesOrderFragment());
            }
        }
    }
}
