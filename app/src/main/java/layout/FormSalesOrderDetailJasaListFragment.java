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
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private Button btnBack, btnNext;
    private FloatingActionButton fab;
    InsertData insertData;

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
        btnNext.setText("Save");
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
            //LibInspira.alertbox("Adding New Sales Order", "Do you want to add the current data?", this, , null);
            LibInspira.alertbox("Adding New Sales Order", "Do you want to add the current data?", getActivity(), new Runnable(){
                public void run() {
                    sendData();
                    LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailJasaListFragment());
                }
            }, null);
        }
    }

    //added by Tonny @02-Sep-2017
    //untuk menjalankan perintah send data ke web service
    private void sendData(){
        String actionUrl = "Order/insertNewOrderJual/";
        insertData = new InsertData();
        insertData.execute(actionUrl);
    }

    private class InsertData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            //---------------------------------------------HEADER-----------------------------------------------------//
//                jsonObject.put("kode", LibInspira.getShared(global.temppreferences, global.temp.nomorsales, ""));
//                jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("nomorcustomer", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("kodecustomer", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("nomorbroker", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("kodebroker", LibInspira.getShared(global.temppreferences, global.temp.nomorsales, ""));
//                jsonObject.put("nomorsales", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("kodesales", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("subtotaljasa", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("subtotalbiaya", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("disc", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("discnominal", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("dpp", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("ppn", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("ppnnominal", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("totalrp", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("pembuat", LibInspira.getShared(global.temppreferences, global.temp.nomorsales, ""));
//                jsonObject.put("nomorcabang", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("cabang", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("valuta", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("jenispenjualan", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("isbarangimport", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("isppn", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
            //-------------------------------------------------------------------------------------------------------//
            //---------------------------------------------DETAIL----------------------------------------------------//
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try
            {
                String totalomzet = "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            totalomzet = (obj.getString("totalomzet"));

                            if(totalomzet.equals("")) totalomzet = "null";
                        }
                    }
                    //tvTotalOmzet.setText("Rp. " + LibInspira.delimeter(totalomzet));
                }
                //tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                //tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

}
