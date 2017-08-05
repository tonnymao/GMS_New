package layout;


import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
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


/**
 * A simple {@link Fragment} subclass.
 */
public class SalesTargetFragment extends Fragment implements View.OnClickListener{
    EditText edtBulan, edtTahun, edtTarget;
    TextView tvSales;
    Button btnAdd, btnSet;
    ListView lvGridSales;

    private SalesTargetFragment.ItemListAdapter itemadapter;
    private ArrayList<SalesTargetFragment.ItemAdapter> list;
    public SalesTargetFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_sales_target, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getActivity().setTitle("New Sales Target");
        edtBulan = (EditText) getView().findViewById(R.id.edtBulan);
        edtTahun= (EditText) getView().findViewById(R.id.edtTahun);
        edtTarget = (EditText) getView().findViewById(R.id.edtTarget);
        tvSales = (TextView) getView().findViewById(R.id.tvSales);
        btnAdd = (Button) getView().findViewById(R.id.btnAdd);
        btnSet = (Button) getView().findViewById(R.id.btnSet);
        lvGridSales = (ListView) getView().findViewById(R.id.lvGridSales);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if(id == R.id.tvSales){
            //tampilkan browse sales
            LibInspira.ReplaceFragment(getFragmentManager(), R.id.fragment_container, new ChooseSalesmanFragment());
        }else if (id == R.id.btnAdd){
            if(edtBulan.getText().equals("") ||
            edtTahun.getText().equals("") ||
            edtTarget.getText().equals("") ||
            tvSales.getText().equals("")){
                //jika data kosong, tampilkan pesan error
                LibInspira.ShowShortToast(getContext(), "Please fill in all data");
            }else{
                //tambahkan data ke lvGridSales
                SalesTargetFragment.ItemAdapter dataItem = new SalesTargetFragment.ItemAdapter();
                dataItem.setBulan(edtBulan.getText().toString());
                dataItem.setTahun(edtTahun.getText().toString());
                dataItem.setTarget(edtTarget.getText().toString());
                dataItem.setSales(tvSales.getText().toString());
                list.add(dataItem);

                itemadapter.add(dataItem);
                itemadapter.notifyDataSetChanged();

            }
        }else if (id == R.id.btnSet){
            if(lvGridSales.getCount() > 0)
                //jika listview terdapat item, maka simpan data
                LibInspira.ShowShortToast(getContext(), "Adding new sales target...");
            else{
                //jika listview kosong, tampilkan pesan error
                LibInspira.ShowShortToast(getContext(), "There is no data to save");
            }
        }
    }

    private void refreshList()
    {
        itemadapter.clear();
        list.clear();

        String data = LibInspira.getShared(global.datapreferences, global.data.periode, "");
        String[] pieces = data.trim().split("\\|");
        if(pieces.length==1)
        {
            //tvNoData.setVisibility(View.VISIBLE);
        }
        else
        {
            //tvNoData.setVisibility(View.GONE);
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");

                    String periode = parts[0];
                    String tahun = parts[1];

                    if(periode.equals("null")) periode = "";
                    if(tahun.equals("null")) tahun = "";

                    SalesTargetFragment.ItemAdapter dataItem = new SalesTargetFragment.ItemAdapter();
                    dataItem.setBulan(periode);
                    dataItem.setTahun(tahun);
                    list.add(dataItem);

                    itemadapter.add(dataItem);
                    itemadapter.notifyDataSetChanged();
                }
            }
        }
    }

    //get set parameter sales
    public class ItemAdapter {

        private String bulan;
        private String tahun;
        private String target;
        private String sales;

        public ItemAdapter() {}

        public String getBulan() {return bulan;}
        public void setBulan(String _param) {this.bulan = _param;}

        public String getTahun() {return tahun;}
        public void setTahun(String _param) {this.tahun = _param;}

        public String getTarget() {return target;}
        public void setTarget(String _param) {this.target = _param;}

        public String getSales() {return sales;}
        public void setSales(String _param) {this.sales = _param;}
    }

    public class ItemListAdapter extends ArrayAdapter<SalesTargetFragment.ItemAdapter> {

        private List<SalesTargetFragment.ItemAdapter> items;
        private int layoutResourceId;
        private Context context;

        public ItemListAdapter(Context context, int layoutResourceId, List<SalesTargetFragment.ItemAdapter> items) {
            super(context, layoutResourceId, items);
            this.layoutResourceId = layoutResourceId;
            this.context = context;
            this.items = items;
        }

        public List<SalesTargetFragment.ItemAdapter> getItems() {
            return items;
        }

        public class Holder {
            SalesTargetFragment.ItemAdapter adapterItem;
            TextView tvPeriode;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            View row = convertView;
            SalesTargetFragment.ItemListAdapter.Holder holder = null;

            if(row==null)
            {
                LayoutInflater inflater = ((Activity) context).getLayoutInflater();
                row = inflater.inflate(layoutResourceId, parent, false);
            }

            holder = new SalesTargetFragment.ItemListAdapter.Holder();
            holder.adapterItem = items.get(position);

            holder.tvPeriode = (TextView)row.findViewById(R.id.tvName);

            row.setTag(holder);
            setupItem(holder);

//            row.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    LibInspira.ShowLongToast(context, "coba");
//                }
//            });

            return row;
        }

        private void setupItem(final SalesTargetFragment.ItemListAdapter.Holder holder) {
            holder.tvPeriode.setText(holder.adapterItem.getBulan() + " " + holder.adapterItem.getTahun());
        }
    }
}
