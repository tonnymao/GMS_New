/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.app.Fragment;

public class ChooseFragment extends Fragment implements View.OnClickListener{
    protected EditText etSearch;
    protected ImageButton ibtnSearch;

    protected TextView tvInformation, tvNoData;
    protected ListView lvSearch;
    protected ItemListAdapter itemadapter;
    protected ArrayList<ItemAdapter> list;

    protected GlobalVar global;
    protected JSONObject jsonObject;
    protected String actionUrlGet;
    protected int jumlahData;
    protected String keyData;

    public ChooseFragment() {
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
        getActivity().setTitle("Choose");
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

        global = new GlobalVar(getActivity());
        setInit();

        ((RelativeLayout) getView().findViewById(R.id.rlSearch)).setVisibility(View.VISIBLE);
        tvInformation = (TextView) getView().findViewById(R.id.tvInformation);
        tvNoData = (TextView) getView().findViewById(R.id.tvNoData);
        etSearch = (EditText) getView().findViewById(R.id.etSearch);

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

        refreshList();

        new getData().execute( actionUrlGet );
    }

    public void setInit()
    {
        actionUrlGet = "Master/getKota";
        jumlahData = 4;
        keyData = global.data.kota;

        list = new ArrayList<ItemAdapter>();
        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
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
            search();
        }
    }

    public void search()
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
                if(LibInspira.contains(list.get(ctr).getNama(),etSearch.getText().toString() ))
                {
                    itemadapter.add(list.get(ctr));
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    protected void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data = LibInspira.getShared(global.datapreferences, keyData, "");
        String[] pieces = data.trim().split("\\|");
        if(pieces.length==1 && pieces[0].equals(""))
        {
            tvNoData.setVisibility(View.VISIBLE);
        }
        else
        {
            tvNoData.setVisibility(View.GONE);
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");

                    if(parts.length==jumlahData)
                    {
                        setData(parts);
                    }
                }
            }
        }
    }

    public void setData(String[] parts)
    {
        String nomor = parts[0];
        String nama = parts[1];
        String nomorpropinsi = parts[2];
        String kode = parts[3];

        if(nomor.equals("null")) nomor = "";
        if(nama.equals("null")) nama = "";
        if(nomorpropinsi.equals("null")) nomorpropinsi = "";
        if(kode.equals("null")) kode = "";

        ItemAdapter dataItem = new ItemAdapter();
        dataItem.setNomor(nomor);
        dataItem.setNama(nama);
        dataItem.setNomorpropinsi(nomorpropinsi);
        dataItem.setKode(kode);

        list.add(dataItem);
        itemadapter.add(dataItem);
        itemadapter.notifyDataSetChanged();
    }

    public String setTempData(JSONObject obj)
    {
        String tempData= "";
        try
        {
            String nomor = (obj.getString("nomor"));
            String nama = (obj.getString("nama"));
            String nomorpropinsi = (obj.getString("nomorpropinsi"));
            String kode = (obj.getString("kode"));

            if(nomor.equals("")) nomor = "null";
            if(nama.equals("")) nama = "null";
            if(nomorpropinsi.equals("")) nomorpropinsi = "null";
            if(kode.equals("")) kode = "null";
            tempData = nomor + "~" + nama + "~" + nomorpropinsi + "~" + kode + "|";
        }
        catch(Exception e)
        {
            e.printStackTrace();
            tvInformation.animate().translationYBy(-80);
        }


        return  tempData;
    }

    protected class getData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
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
                            tempData = tempData + setTempData(obj);
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, keyData, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                keyData,
                                tempData
                        );
                        refreshList();
                    }
                }
                tvInformation.animate().translationYBy(-80);
            }
            catch(Exception e)
            {
                e.printStackTrace();
                tvInformation.animate().translationYBy(-80);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            tvInformation.setVisibility(View.VISIBLE);
        }
    }

    public class ItemAdapter {

        private String nomor;
        private String nama;
        private String nomorpropinsi;
        private String kode;
        private Boolean isChoosen = false;

        public ItemAdapter() {}

        public String getNomor() {return nomor;}
        public void setNomor(String _param) {this.nomor = _param;}

        public String getNama() {return nama;}
        public void setNama(String _param) {this.nama = _param;}

        public String getNomorpropinsi() {return nomorpropinsi;}
        public void setNomorpropinsi(String _param) {this.nomorpropinsi = _param;}

        public String getKode() {return kode;}
        public void setKode(String _param) {this.kode = _param;}

        public Boolean getChoosen() {return isChoosen;}
        public void setChoosen(Boolean _param) {this.isChoosen = _param;}
    }

    protected class ItemListAdapter extends ArrayAdapter<ItemAdapter> {

        private List<ItemAdapter> items;
        private int layoutResourceId;
        private Context context;

        private ItemListAdapter(Context context, int layoutResourceId, List<ItemAdapter> items) {
            super(context, layoutResourceId, items);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.items = items;
        }

        private List<ItemAdapter> getItems() {
            return items;
        }

        private class Holder {
            ItemAdapter adapterItem;
            TextView tvNama;
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

            holder.tvNama = (TextView)row.findViewById(R.id.tvName);

            row.setTag(holder);
            setupItem(holder, row);

            final Holder finalHolder = holder;
            final View finalRow = row;
            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if(finalHolder.adapterItem.getChoosen())
                    {
                        finalHolder.adapterItem.setChoosen(false);
                        finalRow.setBackgroundColor(getResources().getColor(R.color.colorBackground));
                    }
                    else
                    {
                        finalHolder.adapterItem.setChoosen(true);
                        finalRow.setBackgroundColor(getResources().getColor(R.color.colorAccentDanger));
                    }
                    LibInspira.ShowLongToast(context, "coba");
                }
            });

            return row;
        }

        private void setupItem(final Holder holder, final View row) {
            holder.tvNama.setText(holder.adapterItem.getNama().toUpperCase());
            if(holder.adapterItem.getChoosen())
            {
                row.setBackgroundColor(getResources().getColor(R.color.colorAccentDanger));
            }
            else
            {
                row.setBackgroundColor(getResources().getColor(R.color.colorBackground));
            }
        }
    }
}
