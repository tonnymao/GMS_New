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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.inspira.gms.GlobalVar;
import com.inspira.gms.IndexInternal;
import com.inspira.gms.ItemAdapter;
import com.inspira.gms.ItemListAdapter;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

//import android.app.Fragment;

public class ContactFragment extends Fragment implements View.OnClickListener{
    private EditText etSearch;
    private ImageButton ibtnSearch;

    private ListView lvSearch;
    private ItemListAdapter itemadapter;
    private ArrayList<ItemAdapter> list;

    public ContactFragment() {
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
        getActivity().setTitle("Contact");
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

        ((RelativeLayout) getView().findViewById(R.id.rlSearch)).setVisibility(View.VISIBLE);
        etSearch = (EditText) getView().findViewById(R.id.etSearch);
        ibtnSearch = (ImageButton) getView().findViewById(R.id.ibtnSearch);
        ibtnSearch.setOnClickListener(this);

        itemadapter = new ItemListAdapter(getActivity(), R.layout.list_item, new ArrayList<ItemAdapter>());
        itemadapter.clear();
        lvSearch = (ListView) getView().findViewById(R.id.lvChoose);
        lvSearch.setAdapter(itemadapter);

        String actionUrl = "Master/getContact/";
        new getData().execute( actionUrl );
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
            itemadapter.clear();
            for(int ctr=0;ctr<list.size();ctr++)
            {
                if(etSearch.getText().equals(""))
                {
                    if(!list.get(ctr).getNomor().equals(LibInspira.getShared(IndexInternal.global.userpreferences, IndexInternal.global.user.nomor_android, "")))
                    {
                        itemadapter.add(list.get(ctr));
                        itemadapter.notifyDataSetChanged();
                    }
                }
                else if(list.get(ctr).getName().toLowerCase().contains(etSearch.getText().toString().toLowerCase()))
                {
                    if(!list.get(ctr).getNomor().equals(LibInspira.getShared(IndexInternal.global.userpreferences, IndexInternal.global.user.nomor_android, "")))
                    {
                        itemadapter.add(list.get(ctr));
                        itemadapter.notifyDataSetChanged();
                    }
                }
            }
        }
    }

    private class getData extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

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
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            String nomor = (obj.getString("nomor"));
                            String name = (obj.getString("name"));

                            ItemAdapter data = new ItemAdapter();
                            data.setNomor(nomor);
                            data.setName(name);
                            data.setLocation("-");
                            data.setHp("");
                            list.add(data);

                            if(!data.getNomor().equals(LibInspira.getShared(IndexInternal.global.userpreferences, IndexInternal.global.user.nomor_android, "")))
                            {
                                itemadapter.add(data);
                                itemadapter.notifyDataSetChanged();
                            }
                        }
                    }
                }
                LibInspira.hideLoading();
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getContext(), "Contact Load Failed", Toast.LENGTH_LONG).show();
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "Contact", "Loading");
        }
    }

    public class ItemAdapter {

        private String nomor;
        private String name;
        private String hp;
        private String location;

        public ItemAdapter() {}

        public String getNomor() {
            return nomor;
        }
        public void setNomor(String _param) {
            this.nomor = _param;
        }

        public String getName() {
            return name;
        }
        public void setName(String _param) {
            this.name = _param;
        }

        public String getHp() {
            return hp;
        }
        public void setHp(String _param) {
            this.hp = _param;
        }

        public String getLocation() {
            return location;
        }
        public void setLocation(String _param) {
            this.location = _param;
        }

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
            TextView tvNama;
            TextView tvLocation;
            ImageView ivCall;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            Holder holder = null;

            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new Holder();
            holder.adapterItem = items.get(position);

            holder.tvNama = (TextView)row.findViewById(R.id.tvName);
            holder.tvLocation = (TextView)row.findViewById(R.id.tvKeterangan);
            holder.ivCall = (ImageView)row.findViewById(R.id.ivCall);

            row.setTag(holder);
            setupItem(holder);

            row.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    LibInspira.ShowLongToast(context, "coba");
                }
            });

            return row;
        }

        private void setupItem(final Holder holder) {
            holder.tvNama.setText(holder.adapterItem.getName().toUpperCase() + " " + holder.adapterItem.getHp());
            holder.tvLocation.setVisibility(View.VISIBLE);
            holder.tvLocation.setText("Location: " + holder.adapterItem.getLocation());
            if(!holder.adapterItem.getHp().equals(""))
            {
                holder.ivCall.setVisibility(View.VISIBLE);
            }

        }
    }
}
