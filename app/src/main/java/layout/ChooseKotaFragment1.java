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

public class ChooseKotaFragment1 extends ChooseFragment implements View.OnClickListener{

    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;

    private String actionUrlGet;
    private int jumlahData;
    private String keyData;

    public ChooseKotaFragment1() {
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
        getActivity().setTitle("Kota");
        return v;
    }

    @Override
    public void setInit()
    {
        super.setInit();
        actionUrlGet = "Master/getKota";
        jumlahData = 4;
        keyData = global.data.kota;

        list = new ArrayList<ItemAdapter>();
        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);
    }

    @Override
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

    @Override
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

    private class ItemAdapter {

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

    private class ItemListAdapter extends ArrayAdapter<ItemAdapter> {

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