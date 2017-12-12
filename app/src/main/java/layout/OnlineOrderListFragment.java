/******************************************************************************
    Author           : Tonny
    Description      : untuk menampilkan list shopping cart dari customer
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

public class OnlineOrderListFragment extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    protected String actionUrl;
    private CheckData checkData;
    private RelativeLayout relativeLayout;
    private GetHeaderData getHeaderData;

    public OnlineOrderListFragment() {
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
        getActivity().setTitle("Online Order List");
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

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_salesorder, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        relativeLayout = (RelativeLayout) getView().findViewById(R.id.rlFooter);
        relativeLayout.setVisibility(View.GONE);

        refreshList();

        if(LibInspira.getShared(global.userpreferences, global.user.tipe, "").equals("0")){
            actionUrl = "Order/getOnlineOrderList/";  //added by Tonny @17-Sep-2017
        }
        checkData = new CheckData();
        checkData.execute( actionUrl );
    }

    protected void onCancelRequest(){
        if (checkData != null) {
            checkData.cancel(true);
        }
        if (getHeaderData != null) {
            getHeaderData.cancel(true);
        }
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
                jsonObject.put("nomorcustomer", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                if(LibInspira.getShared(global.temppreferences, global.temp.order_status, "").equals("pending")){
                    jsonObject.put("approve", 0);
                }else if(LibInspira.getShared(global.temppreferences, global.temp.order_status, "").equals("approved")){
                    jsonObject.put("approve", 1);
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
                            //nomor~tanggal~nomorcustomer~kodecustomer~namacustomer~disc~discnominal~ppn~ppnnominal~subtotal
                            String nomor = (obj.getString("nomor"));
                            String tanggal = (obj.getString("tanggal"));
                            String nomorcustomer = (obj.getString("nomorcustomer"));
                            String kodecustomer = (obj.getString("kodecustomer"));
                            String namacustomer = (obj.getString("namacustomer"));
                            String disc = (obj.getString("disc"));
                            String discnominal = (obj.getString("discnominal"));
                            String ppn = (obj.getString("ppn"));
                            String ppnnominal = (obj.getString("ppnnominal"));
                            String subtotal = (obj.getString("subtotal"));

                            if(nomor.equals("null")) nomor = "";
                            if(tanggal.equals("null")) tanggal = "";
                            if(nomorcustomer.equals("null")) nomorcustomer = "";
                            if(kodecustomer.equals("null")) kodecustomer = "";
                            if(namacustomer.equals("null")) namacustomer = "";
                            if(disc.equals("null")) disc = "";
                            if(discnominal.equals("null")) discnominal = "";
                            if(ppn.equals("null")) ppn = "";
                            if(ppnnominal.equals("null")) ppnnominal = "";
                            if(subtotal.equals("null")) subtotal = "";

                            tempData = tempData + nomor + "~" + tanggal + "~" + nomorcustomer + "~" + kodecustomer + "~" + namacustomer + "~" + disc + "~" +
                                    discnominal + "~" + ppn + "~" + ppnnominal + "~" + subtotal + "|";
                        }
                    }

                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.onlineorder_list, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.onlineorder_list,
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
            LibInspira.showLoading(getContext(), "Loading data", "Loading...");
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

//        if(id==R.id.fab)
//        {
//            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderHeaderFragment());
//        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data = LibInspira.getShared(global.datapreferences, global.data.onlineorder_list, "");
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
                    String[] parts = pieces[i].trim().split("~");
                    String nomor = parts[0];
                    String tanggal = parts[1];
                    String nomorcustomer = parts[2];
                    String kodecustomer = parts[3];
                    String namacustomer = parts[4];
                    String disc = parts[5];
                    String discnominal = parts[6];
                    String ppn = parts[7];
                    String ppnnominal = parts[8];
                    String subtotal = parts[9];

                    ItemAdapter dataItem = new ItemAdapter();
                    dataItem.setNomor(nomor);  //added by Tonny @16-Sep-2017
                    dataItem.setTanggal(tanggal);
                    dataItem.setNomorCustomer(nomorcustomer);
                    dataItem.setKodeCustomer(kodecustomer);
                    dataItem.setNamaCustomer(namacustomer);
                    dataItem.setSubtotal(subtotal);
                    list.add(dataItem);

                    itemadapter.add(dataItem);
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    public class ItemAdapter {

        private String nomor;  //added by Tonny @16-Sep-2017
        private String kode;
        private String tanggal;
        private String nomorcabang;
        private String cabang;
        private String nomorcustomer;
        private String kodecustomer;
        private String namacustomer;
        private String disc;
        private String discnominal;
        private String ppn;
        private String ppnnominal;
        private String subtotal;

        public ItemAdapter() {}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

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

        public String getDisc() {return disc;}
        public void setDisc(String _param) {this.disc = _param;}

        public String getDiscnominal() {return discnominal;}
        public void setDiscnominal(String _param) {this.discnominal = _param;}

        public String getPPN() {return ppn;}
        public void setPPN(String _param) {this.ppn = _param;}

        public String getPPNNominal() {return ppnnominal;}
        public void setPPNNominal(String _param) {this.ppnnominal = _param;}

        public String getSubtotal() {return subtotal;}
        public void setSubtotal(String _param) {this.subtotal = _param;}
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

            final Holder finalHolder = holder;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Log.d("Selected Online Order: ", finalHolder.adapterItem.getNomor());
                    LibInspira.setShared(global.temppreferences, global.temp.selected_list_nomor, finalHolder.adapterItem.getNomor());
//                    LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new OnlineOrderFragment());
                    actionUrl = "Order/getOnlineOrderHeader";
                    getHeaderData = new GetHeaderData();
                    getHeaderData.execute(actionUrl);
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvKode.setText(holder.adapterItem.getKodeCustomer().toUpperCase());
            holder.tvTanggal.setText(holder.adapterItem.getTanggal().toUpperCase());
//            holder.tvCabang.setText(holder.adapterItem.getCabang().toUpperCase());
            holder.tvCustomer.setText(holder.adapterItem.getNamaCustomer().toUpperCase());
        }
    }

    private class GetHeaderData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomor", LibInspira.getShared(global.temppreferences, global.temp.selected_list_nomor, ""));
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
//                String tempData= "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            //nomor~tanggal~nomorcustomer~kodecustomer~namacustomer~subtotal
                            String nomor = (obj.getString("nomor"));
                            String tanggal = (obj.getString("tanggal"));
                            String nomorcustomer = (obj.getString("nomorcustomer"));
                            String kodecustomer = (obj.getString("kodecustomer"));
                            String namacustomer = (obj.getString("namacustomer"));
                            String disc = (obj.getString("disc"));
                            String discnominal = (obj.getString("discnominal"));
                            String ppn = (obj.getString("ppn"));
                            String ppnnominal = (obj.getString("ppnnominal"));
                            String subtotal = (obj.getString("subtotal"));

                            if(nomor.equals("null")) nomor = "";
                            if(tanggal.equals("null")) tanggal = "";
                            if(nomorcustomer.equals("null")) nomorcustomer = "";
                            if(kodecustomer.equals("null")) kodecustomer = "";
                            if(namacustomer.equals("null")) namacustomer = "";
                            if(disc.equals("null")) disc = "";
                            if(discnominal.equals("null")) discnominal = "";
                            if(ppn.equals("null")) ppn = "";
                            if(ppnnominal.equals("null")) ppnnominal = "";
                            if(subtotal.equals("null")) subtotal = "";

                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_date, tanggal);
                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_customer_nama, namacustomer);
                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_disc, disc);
                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_disc_nominal, discnominal);
                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_ppn, ppn);
                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_ppn_nominal, ppnnominal);
                            LibInspira.setShared(global.temppreferences, global.temp.onlineorder_subtotal, subtotal);
                        }
                    }
                }
                LibInspira.hideLoading();
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new OnlineOrderFragment());
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
            LibInspira.showLoading(getContext(), "Loading the data", "Loading...");
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }
}
