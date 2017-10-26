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
import android.widget.ImageButton;
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

public class OnlineOrderDetailFragment extends Fragment implements View.OnClickListener{
    private ListView lvSearch;
    protected ItemListAdapter itemadapter;
    protected ArrayList<ItemAdapter> list;
    protected String jenisDetail;  //added by Tonny @16-Sep-2017
    private Button btnBack, btnNext;
    private FloatingActionButton fab;
    protected String strData;
    private String tempPrice;

    public OnlineOrderDetailFragment() {
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
        getActivity().setTitle("Online Order - Detail");
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

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_item_salesorder, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        fab = (FloatingActionButton) getView().findViewById(R.id.fab);
        btnBack = (Button) getView().findViewById(R.id.btnBack);
        btnNext = (Button) getView().findViewById(R.id.btnNext);

        btnBack.setVisibility(View.GONE);
        btnNext.setVisibility(View.GONE);
        fab.setVisibility(View.GONE);

        refreshList();
        //getStrData();
        Log.d("onActivityCreated: ", "list item created");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.ibtnSearch)
        {
//            search();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        strData = LibInspira.getShared(global.temppreferences, global.temp.onlineorder_item, "");
    }

     protected void refreshList()
    {
        itemadapter.clear();
        list.clear();
        getStrData();  //added by Tonny @07-Sep-2017
        if (strData.equals("")){
            return;
        }
        String data = strData;
        String[] pieces = data.trim().split("\\|");
        if((pieces.length==1 && pieces[0].equals("")))
        {
            //do nothing
        }
        else
        {
            for(int i=0 ; i < pieces.length ; i++){
                Log.d("Index", data);
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("~");
                    Log.d("pieces: ", pieces[i]);
                    try {
                        String nomor = parts[0];
                        String nomorbarang = parts[1];
                        String kodebarang = parts[2];
                        String namabarang = parts[3];
                        String jumlah = parts[4];
                        String satuan = parts[5];
                        String price = parts[6];
                        String fee = parts[7];
                        String disc = parts[8];
                        String discnominal = parts[9];
                        String subtotal = parts[10];

                        if(nomor.equals("null")) nomor = "";
                        if(nomorbarang.equals("null")) nomor = "";
                        if(kodebarang.equals("null")) kodebarang = "";
                        if(namabarang.equals("null")) namabarang = "-";
                        if(jumlah.equals("null")) jumlah = "";
                        if(satuan.equals("null")) satuan = "";
                        if(price.equals("null")) price = "";
                        if(fee.equals("null")) fee = "";
                        if(disc.equals("null")) disc = "";
                        if(discnominal.equals("null")) discnominal = "";
                        if(subtotal.equals("null")) subtotal= "";

                        ItemAdapter dataItem = new ItemAdapter();
                        dataItem.setIndex(i);
                        dataItem.setNomor(nomor);
                        dataItem.setNama(namabarang);
                        dataItem.setKode(kodebarang);
                        dataItem.setSatuan(satuan);
                        dataItem.setPrice(price);
                        dataItem.setQty(jumlah);
                        dataItem.setFee(fee);
                        dataItem.setDisc(disc);
                        dataItem.setSubtotal(subtotal);

                        list.add(dataItem);

                        itemadapter.add(dataItem);
                        itemadapter.notifyDataSetChanged();
                    }catch (Exception e){
                        e.printStackTrace();
                        LibInspira.ShowShortToast(getContext(), "The current data is invalid. Please add new data.");
                        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_item, "");
                        strData = "";
                        refreshList();
                    }
                }
            }
        }
    }

    protected void deleteSelectedItem(int _index){
        String newdata = "";
        getStrData();
        if(!strData.equals(""))
        {
            String[] pieces = strData.trim().split("\\|");
            for(int i=0 ; i < pieces.length ; i++){
                if(i != _index)
                {
                    newdata = newdata + pieces[i] + "|";
                }
            }
        }
        setStrData(newdata);
        refreshList();
    }



    protected void getStrData(){
        strData = LibInspira.getShared(global.temppreferences, global.temp.onlineorder_item, "");
        //added by Tonny @16-Sep-2017 jika approval atau disapproval, maka hide ibtnDelete
        String ActionUrl = "Order/getOnlineOrderDetail/";
        GetList getList = new GetList();
        getList.execute(ActionUrl);
    }

    protected class GetList extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                //"nomor" == nomor header
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
                String tempData= "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            //nomorbarang~kodebarang~namabarang~satuan~price~qty~fee~disc~subtotal~notes
                            String nomor = (obj.getString("nomor"));;
                            String nomorbarang = (obj.getString("nomorbarang"));
                            String kodebarang = (obj.getString("kodebarang"));
                            String namabarang = (obj.getString("namabarang"));
                            String jumlah = (obj.getString("jumlah"));
                            String satuan = (obj.getString("satuan"));
                            String price = (obj.getString("price"));
                            String fee = (obj.getString("fee"));
                            String disc = (obj.getString("disc"));
                            String discnominal = (obj.getString("discnominal"));
                            String subtotal = (obj.getString("subtotal"));

                            if(nomor.equals("null")) nomor = "";
                            if(nomorbarang.equals("null")) nomorbarang = "";
                            if(kodebarang.equals("null")) kodebarang = "";
                            if(namabarang.equals("null")) namabarang = "-";
                            if(jumlah.equals("null")) jumlah = "";
                            if(satuan.equals("null")) satuan = "";
                            if(price.equals("null")) price = "";
                            if(fee.equals("null")) fee = "";
                            if(disc.equals("null")) disc = "";
                            if(discnominal.equals("null")) discnominal = "";
                            if(subtotal.equals("null")) subtotal= "";

                            tempData = tempData + nomor + "~" + nomorbarang + "~" + kodebarang + "~" + namabarang + "~"
                                    + jumlah + "~" + satuan + "~" + price + "~" + fee + "~" + disc + "~" + discnominal + "~" + subtotal + "|";
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.temppreferences, global.temp.onlineorder_item, "")))
                    {
                        //added by Tonny @16-Sep-2017
                        setStrData(tempData);
                        refreshList();
                    }
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

    protected void setStrData(String newdata){
        LibInspira.setShared(global.temppreferences, global.temp.onlineorder_item, newdata);
        strData = newdata;
    }

    public class ItemAdapter {

        private int index;
        private String nomor;
        private String nama;
        private String kode;
        private String satuan;
        private String price;
        private String qty;
        private String fee;
        private String disc;
        private String notes;
        private String subtotal;


        public ItemAdapter() {}

        public int getIndex() {return index;}
        public void setIndex(int _param) {this.index = _param;}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getNama() {return nama;}
        public void setNama(String _param) {this.nama = _param;}

        public String getKode() {return kode;}
        public void setKode(String _param) {this.kode = _param;}

//        public String getNomorReal() {return nomorreal;}
//        public void setNomorReal(String _param) {this.nomorreal = _param;}
//
//        public String getNamaReal() {return namareal;}
//        public void setNamaReal(String _param) {this.namareal = _param;}
//
//        public String getKodeReal() {return kodereal;}
//        public void setKodeReal(String _param) {this.kodereal = _param;}

        public String getSatuan() {return satuan;}
        public void setSatuan(String _param) {this.satuan = _param;}

        public String getPrice() {return price;}
        public void setPrice(String _param) {this.price = _param;}

        public String getQty() {return qty;}
        public void setQty(String _param) {this.qty = _param;}

        public String getFee() {return fee;}
        public void setFee(String _param) {this.fee = _param;}

        public String getDisc() {return disc;}
        public void setDisc(String _param) {this.disc = _param;}

        public String getNotes() {return notes;}
        public void setNotes(String _param) {this.notes = _param;}

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
            TextView tvKode, tvNama, tvSatuan, tvPrice, tvQty, tvFee, tvDisc, tvNotes;
            ImageButton ibtnDelete;
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

            holder.tvNama = (TextView)row.findViewById(R.id.tvNama);
            holder.tvKode = (TextView)row.findViewById(R.id.tvKode);
            holder.tvSatuan = (TextView)row.findViewById(R.id.tvSatuan);
            holder.tvPrice = (TextView)row.findViewById(R.id.tvPrice);
            holder.tvQty = (TextView)row.findViewById(R.id.tvQty);
            holder.tvFee = (TextView)row.findViewById(R.id.tvFee);
            holder.tvDisc = (TextView)row.findViewById(R.id.tvDisc);
            holder.tvNotes = (TextView)row.findViewById(R.id.tvNotes);
            holder.ibtnDelete = (ImageButton) row.findViewById(R.id.ibtnDelete);

            row.setTag(holder);
            setupItem(holder);

            final Holder finalHolder = holder;
            if(LibInspira.getShared(global.userpreferences, global.user.tipe, "").equals("0")) {  //added by Tonny @25-Oct-2017
                row.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LibInspira.showNumericInputDialog("Update Price", "Please input a new price for this item", getActivity(), getContext(), new Runnable() {
                            @Override
                            public void run() {
                                //insert ke dalam shared
                                tempPrice = LibInspira.getDialogValue();
                                if(Double.parseDouble(tempPrice) > 0){
                                    String jumlah = finalHolder.adapterItem.getQty();
                                    finalHolder.adapterItem.setPrice(tempPrice);
                                    finalHolder.adapterItem.setSubtotal(Double.toString(Double.parseDouble(jumlah) * Double.parseDouble(tempPrice)));  //subtotal pada tdcart
                                }else{
                                    LibInspira.showLongToast(getContext(), "Price must be greater than 0");
                                }
                            }
                        }, null);
                    }
                });
                holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        LibInspira.alertBoxYesNo("Delete Data", "Are you sure want to delete this data?", getActivity(), new Runnable() {
                            @Override
                            public void run() {
                                deleteSelectedItem(finalHolder.adapterItem.getIndex());
                            }
                        }, new Runnable() {
                            @Override
                            public void run() {

                            }
                        });
                    }
                });
            }else{
                holder.ibtnDelete.setVisibility(View.GONE);
            }

            //added by Tonny @16-Sep-2017 jika approval atau disapproval, maka hide ibtnDelete dan hilangkan listener click
//            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval") ||
//                    LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){
//                holder.ibtnDelete.setVisibility(View.GONE);
//            }else{
//                final Holder finalHolder = holder;
//                row.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                    }
//                });
//                holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
//                    @Override
//                    public void onClick(View view) {
//                        deleteSelectedItem(finalHolder.adapterItem.getIndex());
//                    }
//                });
//            }
            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNama.setText(holder.adapterItem.getNama().toUpperCase());
            holder.tvKode.setText(holder.adapterItem.getKode().toUpperCase());
            holder.tvPrice.setText(LibInspira.delimeter(holder.adapterItem.getPrice().toUpperCase()));
            holder.tvQty.setText(LibInspira.delimeter(holder.adapterItem.getQty().toUpperCase()) + " " + holder.adapterItem.getSatuan().toUpperCase());
            holder.tvFee.setText(LibInspira.delimeter(holder.adapterItem.getFee().toUpperCase()));
            holder.tvDisc.setText(LibInspira.delimeter(holder.adapterItem.getDisc().toUpperCase()));
            holder.tvNotes.setText(holder.adapterItem.getNotes());
        }
    }
}
