/******************************************************************************
 Author           : Tonny
 Description      : filter untuk stock monitoring
 History          :

 ******************************************************************************/
package layout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.inspira.gms.ItemListAdapter;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import static com.inspira.gms.IndexInternal.global;

//import android.app.Fragment;

public class FilterStockFragment extends Fragment implements View.OnClickListener{
    private Spinner spGudang, spKategori, spBentuk, spSurface, spJenis, spGrade;
    private EditText edtBarang, edtUkuran1, edtUkuran2, edtTebal, edtMotif;
    private DatePickerDialog dp;
    private TextView tvFilterStockDate;
    private List<String> kodegudanglist;

    public FilterStockFragment() {
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
        View v = inflater.inflate(R.layout.fragment_filter_stock, container, false);
        getActivity().setTitle("Filter Stock Monitoring");
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
        spKategori = (Spinner) getView().findViewById(R.id.spKategori);
        spBentuk = (Spinner) getView().findViewById(R.id.spBentuk);
        spSurface = (Spinner) getView().findViewById(R.id.spSurface);
        spJenis = (Spinner) getView().findViewById(R.id.spJenis);
        spGrade = (Spinner) getView().findViewById(R.id.spGrade);
        spGudang = (Spinner) getView().findViewById(R.id.spGudang);
        edtBarang = (EditText) getView().findViewById(R.id.edtBarang);
        edtTebal = (EditText) getView().findViewById(R.id.edtTebal);
        edtMotif = (EditText) getView().findViewById(R.id.edtMotif);
        edtUkuran1 = (EditText) getView().findViewById(R.id.edtUkuran1);
        edtUkuran2 = (EditText) getView().findViewById(R.id.edtUkuran2);
        tvFilterStockDate = (TextView) getView().findViewById(R.id.tvFilterStockDate);
        tvFilterStockDate.setOnClickListener(this);

        // Define DatePicker
        Calendar newCalendar = Calendar.getInstance();
        dp = new DatePickerDialog(getActivity(), R.style.DialogTheme, new DatePickerDialog.OnDateSetListener() {

            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                try {
                    String date = year + "-" + (monthOfYear+1) + "-" + dayOfMonth;
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    Date newdate = sdf.parse(date);
                    date = sdf.format(newdate);

                    tvFilterStockDate.setText(date);
                }
                catch(Exception e)
                {
                    e.printStackTrace();
                }
            }
        },newCalendar.get(Calendar.YEAR), newCalendar.get(Calendar.MONTH), newCalendar.get(Calendar.DAY_OF_MONTH));

        getView().findViewById(R.id.btnFilterUpdate).setOnClickListener(this);
        String actionUrl = "Stock/getKategori/";
        new checkKategori().execute( actionUrl );

        actionUrl = "Stock/getGudang/";
        new checkGudang().execute( actionUrl );

        actionUrl = "Stock/getBentuk/";
        new checkBentuk().execute( actionUrl );

        actionUrl = "Stock/getSurface/";
        new checkSurface().execute( actionUrl );

        actionUrl = "Stock/getJenis/";
        new checkJenis().execute( actionUrl );

        actionUrl = "Stock/getGrade/";
        new checkGrade().execute( actionUrl );

        refreshList("all");

        spGudang.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                LibInspira.setShared(global.stockmonitoringpreferences, global.stock.kodegudang, kodegudanglist.get(position));
                Log.d("kodegudang", LibInspira.getShared(global.stockmonitoringpreferences, global.stock.kodegudang, ""));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                LibInspira.setShared(global.stockmonitoringpreferences, global.stock.kodegudang, kodegudanglist.get(0));
                Log.d("kodegudang", LibInspira.getShared(global.stockmonitoringpreferences, global.stock.kodegudang, ""));
            }
        });

        Calendar c = Calendar.getInstance();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String formattedDate = df.format(c.getTime());
        tvFilterStockDate.setText(formattedDate);
    }

    private void refreshList(String _param)
    {
        String data = "";
        Spinner spinner = spKategori;
        if(_param.equals("all")){
            refreshList("kategori");
            refreshList("bentuk");
            refreshList("jenis");
            refreshList("grade");
            refreshList("surface");
            refreshList("gudang");
        }else {
            if (_param.equals("kategori")) {
                data = LibInspira.getShared(global.datapreferences, global.data.stockKategori, "");
                Log.d("data ", data);
                spinner = spKategori;
            } else if (_param.equals("bentuk")) {
                data = LibInspira.getShared(global.datapreferences, global.data.stockBentuk, "");
                spinner = spBentuk;
            } else if (_param.equals("jenis")) {
                data = LibInspira.getShared(global.datapreferences, global.data.stockJenis, "");
                spinner = spJenis;
            } else if (_param.equals("grade")) {
                data = LibInspira.getShared(global.datapreferences, global.data.stockGrade, "");
                spinner = spGrade;
            } else if (_param.equals("surface")) {
                data = LibInspira.getShared(global.datapreferences, global.data.stockSurface, "");
                spinner = spSurface;
            } else if (_param.equals("gudang")) {
                data = LibInspira.getShared(global.datapreferences, global.data.stockGudang, "");
                spinner = spGudang;
            }

            String[] pieces = data.trim().split("\\|");

            if (pieces.length == 1 && pieces[0].equals("")) {
            } else {
                ArrayAdapter<String> adapter;
                List<String> list;
                list = new ArrayList<>();
                kodegudanglist = new ArrayList<>();
                for (int i = 0; i < pieces.length; i++) {
                    if (!pieces[i].equals("")) {
                        if(_param.equals("gudang")){
                            String[] parts = pieces[i].split("\\~");
                            kodegudanglist.add(parts[0]);
                            list.add(parts[1]);
                        }else {
                            list.add(pieces[i]);
                        }
                    }
                }
                adapter = new ArrayAdapter<>(getContext(),
                        android.R.layout.simple_spinner_item, list);
                adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                spinner.setAdapter(adapter);
            }
        }
    }

    private class checkKategori extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("filter stok", result);
            try {
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            Log.d("status", "UPDATING");
                            tempData = tempData + obj.getString("kategori") + "|";
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting kategori");
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.stockKategori, "")))
                    {
                        LibInspira.setShared(global.datapreferences, global.data.stockKategori, tempData);
                        refreshList("kategori");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class checkGudang extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            tempData = tempData + obj.getString("kodegudang") + "~" + obj.getString("namagudang") + "|";
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting kategori");
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.stockGudang, "")))
                    {
                        LibInspira.setShared(global.datapreferences, global.data.stockGudang, tempData);
                        refreshList("gudang");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class checkBentuk extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            Log.d("status", "UPDATING");
                            tempData = tempData + obj.getString("bentuk") + "|";
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting bentuk");
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.stockBentuk, "")))
                    {
                        LibInspira.setShared(global.datapreferences, global.data.stockBentuk, tempData);
                        refreshList("bentuk");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class checkSurface extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            tempData = tempData + obj.getString("surface") + "|";
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting surface");
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.stockSurface, "")))
                    {
                        LibInspira.setShared(global.datapreferences, global.data.stockSurface, tempData);
                        refreshList("surface");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class checkJenis extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            tempData = tempData + obj.getString("jenis") + "|";
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting jenis");
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.stockJenis, "")))
                    {
                        LibInspira.setShared(global.datapreferences, global.data.stockJenis, tempData);
                        refreshList("jenis");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private class checkGrade extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;

        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            try {
                String tempData = "";
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            Log.d("status", "UPDATING");
                            tempData = tempData + obj.getString("grade") + "|";
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting grade");
                        }
                    }
                    if(!tempData.equals(LibInspira.getShared(global.datapreferences, global.data.stockGrade, "")))
                    {
                        LibInspira.setShared(global.datapreferences, global.data.stockGrade, tempData);
                        refreshList("grade");
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.btnFilterUpdate){
            //menyimpan filter
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.nomorbarang, edtBarang.getText().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.kategori, spKategori.getSelectedItem().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.jenis, spJenis.getSelectedItem().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.grade, spGrade.getSelectedItem().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.bentuk, spBentuk.getSelectedItem().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.ukuran, edtUkuran1.getText().toString() + "x" + edtUkuran2.getText().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.tebal, edtTebal.getText().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.motif, edtMotif.getText().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.surface, spSurface.getSelectedItem().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.tanggal, tvFilterStockDate.getText().toString());
            LibInspira.setShared(global.stockmonitoringpreferences, global.stock.namagudang, spGudang.getSelectedItem().toString());
            LibInspira.ReplaceFragment(getFragmentManager(), R.id.fragment_container, new StockPosisiFragment());
        }
        else if (id == R.id.tvFilterStockDate){
            dp.show();
        }
    }
}
