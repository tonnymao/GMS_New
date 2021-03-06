/******************************************************************************
 Author           : TONNY
 Description      : shopping cart
 History          :

 ******************************************************************************/
package layout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
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
import static com.inspira.gms.IndexExternal.global;
import static com.inspira.gms.IndexExternal.jsonObject;

//import android.app.Fragment;

public class ShoppingCartFragment extends Fragment implements View.OnClickListener{
    private EditText etSearch;
    private Cart cart;
    private ImageButton ibtnSearch;
    private CheckBox chkBox;
    private RelativeLayout rlFooter;
    private Button btnSave;
    private TextView tvInformation, tvNoData;
    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;
    private Double grandtotal;
    private Integer totalJumlah;
//    private GetData getData;
    protected String strData;
    protected String itemType;
    protected String actionUrl = "Cart/getBarang/";


    public ShoppingCartFragment() {
        // Required empty public constructor
    }

    public ShoppingCartFragment(String type) {
        itemType = type;
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
        getActivity().setTitle("Shopping Cart");
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

        getView().findViewById(R.id.rlSearch).setVisibility(View.VISIBLE);
        tvInformation = (TextView) getView().findViewById(R.id.tvInformation);
        tvNoData = (TextView) getView().findViewById(R.id.tvNoData);
        etSearch = (EditText) getView().findViewById(R.id.etSearch);
        rlFooter = (RelativeLayout) getView().findViewById(R.id.rlFooter);
        rlFooter.setVisibility(View.VISIBLE);
        chkBox = (CheckBox) getView().findViewById(R.id.chkBox);
        chkBox.setText("PPN");
        chkBox.setVisibility(View.VISIBLE);
        btnSave = (Button) getView().findViewById(R.id.btnCenter);
        btnSave.setVisibility(View.VISIBLE);
        btnSave.setText("Save");
        btnSave.setOnClickListener(this);
        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_item_cart, new ArrayList<ItemAdapter>());  //modified by Tonny @12-Oct-2017  layout list_item diganti list_item_cart
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                search();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        //Log.d("kode customer ", LibInspira.getShared(global.userpreferences, global.user.kode, ""));
        refreshList();

//        if(actionUrl.equals("")){
//            actionUrl = "Cart/getBarang/";
//        }
//        LibInspira.showShortToast(getContext(), LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
//        getData = new GetData();
//        getData.execute( actionUrl );
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id == R.id.ibtnSearch)
        {
            search();
        }else if(id == R.id.btnCenter){
            if(LibInspira.getShared(global.datapreferences, global.data.cart, "").equals("")){
                LibInspira.showLongToast(getContext(), "No data to save");
            }else{
                //added by Tonny @26-Oct-2017
                String txtPPN = "NON PPN";
                if (chkBox.isChecked()){
                    txtPPN = "PPN";
                }
                LibInspira.alertbox("Save Cart", "Do you want to save all items in this cart? \n\n" + txtPPN + "\n\nTOTAL QTY: " + totalJumlah + "\n" +
                        "TOTAL PEMBAYARAN: Rp. " + LibInspira.delimeter(String.valueOf(grandtotal)), getActivity(), new Runnable() {
                    @Override
                    public void run() {
                        Log.d("cart ", LibInspira.getShared(global.datapreferences, global.data.cart, ""));
                        String actionUrl = "Cart/addtocart";
                        cart = new Cart();
                        cart.execute(actionUrl);
                    }
                }, new Runnable() {
                    @Override
                    public void run() {
                        //do nothing
                    }
                });
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
//        if (getData != null){
//            getData.cancel(true);
//        }
    }

    private void search()
    {
        itemadapter.clear();
        for(int ctr=0;ctr<list.size();ctr++)
        {
            if(etSearch.getText().equals(""))
            {
                itemadapter.add(list.get(ctr));
                itemadapter.notifyDataSetChanged();
            }
            else
            {
                if(LibInspira.contains(list.get(ctr).getNamajual(),etSearch.getText().toString() ))
                {
                    itemadapter.add(list.get(ctr));
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();
        grandtotal = 0.0;
        totalJumlah = 0;

        String data = LibInspira.getShared(global.datapreferences, global.data.cart, "");
        String[] pieces = data.trim().split("\\|");
        try
        {
            if(pieces.length==1 && pieces[0].equals(""))
            {
                tvNoData.setVisibility(View.VISIBLE);
            }
            else
            {
                tvNoData.setVisibility(View.GONE);
                for(int i=0 ; i < pieces.length ; i++){
                    Log.d("item", pieces[i] + "a");
                    if(!pieces[i].equals(""))
                    {
                        String[] parts = pieces[i].trim().split("~");

                        String nomor = parts[0];
                        String namajual = parts[1];
                        String kode = parts[2];
                        String satuan = parts[3];
                        String hargajual = parts[4];
                        String jumlah = parts[5];
                        String subtotal = parts[6];

                        if(nomor.equals("null")) nomor = "";
                        if(namajual.equals("null")) namajual = "";
                        if(kode.equals("null")) kode = "";
                        if(satuan.equals("null")) satuan = "";
                        if(hargajual.equals("null")) hargajual = "";
                        if(jumlah.equals("null")) jumlah = "";
                        if(subtotal.equals("null")) subtotal = "";

                        ItemAdapter dataItem = new ItemAdapter();
                        dataItem.setIndex(i);
                        dataItem.setNomor(nomor);
                        dataItem.setNamajual(namajual);
                        dataItem.setKode(kode);
                        dataItem.setSatuan(satuan);
                        dataItem.setHargajual(hargajual);
                        dataItem.setSatuan(satuan);
                        dataItem.setJumlah(jumlah);
                        dataItem.setSubtotal(subtotal);
                        list.add(dataItem);

                        itemadapter.add(dataItem);
                        itemadapter.notifyDataSetChanged();

                        //added by Tonny @12-Oct-2017
                        grandtotal += Double.parseDouble(subtotal);
                        totalJumlah += Integer.parseInt(jumlah);
                    }
                }
            }

            if(itemadapter.getCount()==0)
            {
                tvNoData.setVisibility(View.VISIBLE);
            }
        }
        catch (Exception e)
        {
            tvNoData.setVisibility(View.VISIBLE);
        }
    }

    public class ItemAdapter {
        private int index;
        private String nomor;
        private String namajual;
        private String kode;
        private String satuan;
        private String hargajual;
        private String jumlah;
        private String subtotal;

        public ItemAdapter() {}

        public int getIndex() {return index;}
        public void setIndex(int _param) {this.index = _param;}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getNamajual() {return namajual;}
        public void setNamajual(String _param) {this.namajual = _param;}

        public String getKode() {return kode;}
        public void setKode(String _param) {this.kode = _param;}

        public String getSatuan() {return satuan;}
        public void setSatuan(String _param) {this.satuan = _param;}

        public String getHargajual() {return hargajual;}
        public void setHargajual(String _param) {this.hargajual = _param;}

        public String getJumlah() {return jumlah;}
        public void setJumlah(String _param) {this.jumlah = _param;}

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
            TextView tvKode, tvNama, tvSatuan, tvPrice, tvQty, tvFee, tvDisc, tvSubtotal;
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
            holder.tvSubtotal = (TextView)row.findViewById(R.id.tvSubtotal);
            holder.ibtnDelete = (ImageButton) row.findViewById(R.id.ibtnDelete);

            row.setTag(holder);
            setupItem(holder);

            final Holder finalHolder = holder;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LibInspira.showNumericInputDialog("Edit cart", "How many item do you want to buy?", getActivity(), getContext(), new Runnable() {
                        @Override
                        public void run() {
                            //edit shared jumlah
                            String nomorbarang, kodebarang, namabarang, hargabarang, jumlah, subtotal, satuan;
                            jumlah = LibInspira.getDialogValue();
                            if (Double.parseDouble(jumlah) > 0) {
                                nomorbarang = finalHolder.adapterItem.getNomor();
                                namabarang = finalHolder.adapterItem.getNamajual();
                                kodebarang = finalHolder.adapterItem.getKode();
                                hargabarang = finalHolder.adapterItem.getHargajual();
                                jumlah = LibInspira.getDialogValue();
                                satuan = finalHolder.adapterItem.getSatuan();
                                subtotal = Double.toString(Double.parseDouble(jumlah) * Double.parseDouble(hargabarang));  //subtotal untuk tdcart
                                finalHolder.adapterItem.setJumlah(jumlah);

                                //MODE EDIT
                                strData = "";
                                String[] pieces = LibInspira.getShared(global.datapreferences, global.data.cart, "").trim().split("\\|");
                                for(int i=0 ; i < pieces.length ; i++){
                                    if(i != finalHolder.adapterItem.getIndex())
                                    {
                                        strData = strData + pieces[i] + "|";
                                    }
                                    else
                                    {
                                        String tempCart = nomorbarang + "~" + namabarang + "~" + kodebarang + "~" + satuan + "~" + hargabarang + "~" + jumlah + "~" + subtotal + "|";
                                        strData = strData + tempCart;
                                        Log.d("strData edit", strData);
                                    }
                                }
                                LibInspira.setShared(global.datapreferences, global.data.cart, strData);
                            } else {
                                LibInspira.showLongToast(getContext(), "Jumlah must be greater than 0");
                            }
                        }
                    }, new Runnable() {
                        @Override
                        public void run() {
                            //do nothing
                        }
                    });
                }
            });
            holder.ibtnDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    deleteSelectedItem(finalHolder.adapterItem.getIndex());
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNama.setText(holder.adapterItem.getNamajual().toUpperCase());
//            holder.tvKeterangan.setText("Kode: " + holder.adapterItem.getKode().toUpperCase() + "\n" +
//                "Harga: Rp. " + LibInspira.delimeter(holder.adapterItem.getHargajual()) + "\n" +
//                "Qty: " + LibInspira.delimeter(holder.adapterItem.getJumlah()) + " " + holder.adapterItem.getSatuan() +"\n" +
//                "Subtotal: Rp. " + LibInspira.delimeter(holder.adapterItem.getSubtotal())
//            );
            holder.tvKode.setText(holder.adapterItem.getKode().toUpperCase());
            holder.tvPrice.setText("Rp. " + LibInspira.delimeter(holder.adapterItem.getHargajual()));
            holder.tvQty.setText(LibInspira.delimeter(holder.adapterItem.getJumlah()) + " " + holder.adapterItem.getSatuan().toUpperCase());
            holder.tvFee.setVisibility(View.GONE);
            holder.tvDisc.setVisibility(View.GONE);
            holder.tvSubtotal.setText("Rp. " + LibInspira.delimeter(holder.adapterItem.getSubtotal()));
        }
    }

    private class Cart extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("nomorcustomer", LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                jsonObject.put("kodecustomer", LibInspira.getShared(global.userpreferences, global.user.kode, ""));
                jsonObject.put("grandtotal", String.valueOf(grandtotal));
                jsonObject.put("cart", LibInspira.getShared(global.datapreferences, global.data.cart, ""));
                //added by Tonny @26-Oct-2017
                String isppn = "0";
                if(chkBox.isChecked()){
                    isppn = "1";
                }
                jsonObject.put("isppn", isppn);
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
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            LibInspira.showLongToast(getContext(), "Data has been successfully saved!");
                            LibInspira.setShared(global.datapreferences, global.data.cart, "");
                        }else{
                            Log.wtf("query", result);
                            LibInspira.showLongToast(getContext(), "Failed to save data");
                        }
                    }
                    refreshList();
                }
                tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                tvInformation.animate().translationYBy(-80);
                LibInspira.showShortToast(getContext(), "Saving data failed");
            }
            LibInspira.hideLoading();
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvInformation.setVisibility(View.VISIBLE);
            LibInspira.showLoading(getContext(), "Saving the data", "Now Loading...");
        }
    }

    protected void getStrData(){
        strData = LibInspira.getShared(global.datapreferences, global.data.cart, "");
        refreshList();
    }

    protected void setStrData(String newdata){
        LibInspira.setShared(global.datapreferences, global.data.cart, newdata);
        strData = newdata;
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
}
