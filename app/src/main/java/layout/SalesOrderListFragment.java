/******************************************************************************
    Author           : ADI
    Description      : untuk menampilkan detail item dalam bentuk list
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
import android.widget.RelativeLayout;
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

public class SalesOrderListFragment extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private Button btnBack, btnNext;
    protected String actionUrl = "Order/getSalesOrderList/";
    private CheckData checkData;
    private RelativeLayout relativeLayout;
    private FloatingActionButton fab;

    public SalesOrderListFragment() {
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
        View v = inflater.inflate(R.layout.fragment_choose, container, false);
        getActivity().setTitle("Sales Order List");
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
        list = new ArrayList<ItemAdapter>();

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        fab.setOnClickListener(this);

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_salesorder, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        relativeLayout = (RelativeLayout) getView().findViewById(R.id.rlFooter);
        relativeLayout.setVisibility(View.GONE);

        refreshList();

        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") || LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval"))
        {
            fab.setVisibility(View.GONE);
        }
        else
        {
            fab.setVisibility(View.VISIBLE);
        }

        checkData = new CheckData();
        checkData.execute( actionUrl );
    }

    protected void onCancelRequest(){
        checkData.cancel(true);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        onCancelRequest();
    }

    private class CheckData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("cabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("proyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("ppn"))
                {
                    jsonObject.put("nomorsales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                    jsonObject.put("approve", "0");
                    jsonObject.put("kode", "SOP-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("proyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("nonppn"))
                {
                    jsonObject.put("nomorsales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                    jsonObject.put("approve", "0");
                    jsonObject.put("kode", "OJP-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("nonproyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("ppn"))
                {
                    jsonObject.put("nomorsales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                    jsonObject.put("approve", "0");
                    jsonObject.put("kode", "SO-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("nonproyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("nonppn"))
                {
                    jsonObject.put("nomorsales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                    jsonObject.put("approve", "0");
                    jsonObject.put("kode", "OJN-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("proyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval"))
                {
                    jsonObject.put("approve", "0");
                    jsonObject.put("kode", "OJP-|SOP-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("proyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval"))
                {
                    jsonObject.put("approve", "1");
                    jsonObject.put("kode", "OJP-|SOP-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("nonproyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval"))
                {
                    jsonObject.put("approve", "0");
                    jsonObject.put("kode", "OJN-|SO-");
                }
                else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_proyek, "").equals("nonproyek") && LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval"))
                {
                    jsonObject.put("approve", "1");
                    jsonObject.put("kode", "OJN-|SO-");
                }


            } catch (JSONException e) {
                e.printStackTrace();
            }
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try
            {
                String tempData= "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            String kode = (obj.getString("kode"));
                            String tanggal = (obj.getString("tanggal"));
                            String nomorcabang = (obj.getString("nomorcabang"));
                            String cabang = (obj.getString("cabang"));
                            String nomorcustomer = (obj.getString("nomorcustomer"));
                            String kodecustomer = (obj.getString("kodecustomer"));
                            String namacustomer = (obj.getString("namacustomer"));

                            if(kode.equals("null")) kode = "";
                            if(tanggal.equals("null")) tanggal = "";
                            if(nomorcabang.equals("null")) nomorcabang = "";
                            if(cabang.equals("null")) cabang = "";
                            if(nomorcustomer.equals("null")) nomorcustomer = "";
                            if(kodecustomer.equals("null")) kodecustomer = "";
                            if(namacustomer.equals("null")) namacustomer = "";

                            tempData = tempData + kode + "~" + tanggal + "~" + nomorcabang + "~" + cabang + "~" + nomorcustomer + "~" + kodecustomer + "~" + namacustomer + "|";
                        }
                    }

                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.salesorder_list_item, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.salesorder_list_item,
                                tempData
                        );
                        refreshList();
                    }
                }
                LibInspira.hideLoading();
                //tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.hideLoading();
                //tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "Adding new data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.fab)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderHeaderFragment());
        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data = LibInspira.getShared(global.datapreferences, global.data.salesorder_list_item, "");
        String[] pieces = data.trim().split("\\|");
        if(pieces.length==1 && pieces[0].equals(""))
        {
            //do nothing
        }
        else
        {
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");

                    String kode = parts[0];
                    String tanggal = parts[1];
                    String nomorcabang = parts[2];
                    String cabang = parts[3];
                    String nomorcustomer = parts[4];
                    String kodecustomer = parts[5];
                    String namacustomer = parts[6];

                    ItemAdapter dataItem = new ItemAdapter();
                    dataItem.setKode(kode);
                    dataItem.setTanggal(tanggal);
                    dataItem.setNomorCabang(nomorcabang);
                    dataItem.setCabang(cabang);
                    dataItem.setNomorCustomer(nomorcustomer);
                    dataItem.setKodeCustomer(kodecustomer);
                    dataItem.setNamaCustomer(namacustomer);
                    list.add(dataItem);

                    itemadapter.add(dataItem);
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class ItemAdapter {

        private String kode;
        private String tanggal;
        private String nomorcabang;
        private String cabang;
        private String nomorcustomer;
        private String kodecustomer;
        private String namacustomer;

        public ItemAdapter() {}

        public String getKode() {return kode;}
        public void setKode(String _param) {this.kode = _param;}

        public String getTanggal() {return tanggal;}
        public void setTanggal(String _param) {this.tanggal = _param;}

        public String getNomorCabang() {return nomorcabang;}
        public void setNomorCabang(String _param) {this.nomorcabang = _param;}

        public String getCabang() {return cabang;}
        public void setCabang(String _param) {this.cabang = _param;}

        public String getNomorCustomer() {return nomorcustomer;}
        public void setNomorCustomer(String _param) {this.nomorcustomer = _param;}

        public String getKodeCustomer() {return kodecustomer;}
        public void setKodeCustomer(String _param) {this.kodecustomer = _param;}

        public String getNamaCustomer() {return namacustomer;}
        public void setNamaCustomer(String _param) {this.namacustomer = _param;}
    }

    public class ItemListAdapter extends ArrayAdapter<ItemAdapter> {

        private List<ItemAdapter> items;
        private int layoutResourceId;
        private Context context;

        public ItemListAdapter(Context context, int layoutResourceId, List<ItemAdapter> items) {
            super(context, layoutResourceId, items);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.items = items;
        }

        public List<ItemAdapter> getItems() {
            return items;
        }

        public class Holder {
            ItemAdapter adapterItem;
            TextView tvKode, tvTanggal, tvCabang, tvCustomer;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Holder holder = null;

            if(row==null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new Holder();
            holder.adapterItem = items.get(position);

            holder.tvKode = (TextView)row.findViewById(R.id.tvKode);
            holder.tvTanggal = (TextView)row.findViewById(R.id.tvTanggal);
            holder.tvCabang = (TextView)row.findViewById(R.id.tvCabang);
            holder.tvCustomer = (TextView)row.findViewById(R.id.tvCustomer);

            row.setTag(holder);
            setupItem(holder);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //LibInspira.ShowLongToast(context, "coba");
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvKode.setText(holder.adapterItem.getKode().toUpperCase());
            holder.tvTanggal.setText(holder.adapterItem.getTanggal().toUpperCase());
            holder.tvCabang.setText(holder.adapterItem.getCabang().toUpperCase());
            holder.tvCustomer.setText(holder.adapterItem.getNamaCustomer().toUpperCase());
        }
    }
}
