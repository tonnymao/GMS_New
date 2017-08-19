/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
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
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

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
    private Spinner spKategori, spBentuk, spSurface, spJenis, spGrade;
    private DatePickerDialog dp;
    private TextView tvFilterStockDate;

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

        actionUrl = "Stock/getBentuk/";
        new checkBentuk().execute( actionUrl );

        actionUrl = "Stock/getSurface/";
        new checkSurface().execute( actionUrl );

        actionUrl = "Stock/getJenis/";
        new checkJenis().execute( actionUrl );

        actionUrl = "Stock/getGrade/";
        new checkGrade().execute( actionUrl );

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
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    ArrayAdapter<String> adapter;
                    List<String> list;
                    list = new ArrayList<>();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            Log.d("status", "UPDATING");
                            //LibInspira.setShared(global.stockmonitoringpreferences, global.stock.filterKategori, obj.getString("kategori"));
                            list.add(obj.getString("kategori"));
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting kategori");
                        }
                    }
                    Log.d("list: ", list.get(0));
                    adapter = new ArrayAdapter<>(getContext(),
                            android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spKategori.setAdapter(adapter);
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
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    ArrayAdapter<String> adapter;
                    List<String> list;
                    list = new ArrayList<String>();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            //LibInspira.setShared(global.stockmonitoringpreferences, global.stock.filterKategori, obj.getString("kategori"));
                            list.add(obj.getString("bentuk"));
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting bentuk");
                        }
                    }
                    Log.d("list: ", list.get(0));
                    adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spBentuk.setAdapter(adapter);
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
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    ArrayAdapter<String> adapter;
                    List<String> list;
                    list = new ArrayList<String>();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            list.add(obj.getString("surface"));
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting surface");
                        }
                    }
                    adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spSurface.setAdapter(adapter);
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
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    ArrayAdapter<String> adapter;
                    List<String> list;
                    list = new ArrayList<String>();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            list.add(obj.getString("jenis"));
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting jenis");
                        }
                    }
                    adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spJenis.setAdapter(adapter);
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
                JSONArray jsonarray = new JSONArray(result);
                if (jsonarray.length() > 0) {
                    ArrayAdapter<String> adapter;
                    List<String> list;
                    list = new ArrayList<String>();
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if (!obj.has("query")) {
                            list.add(obj.getString("grade"));
                        }else{
                            LibInspira.ShowShortToast(getContext(), "Failed getting grade");
                        }
                    }
                    adapter = new ArrayAdapter<String>(getContext(),
                            android.R.layout.simple_spinner_item, list);
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    spGrade.setAdapter(adapter);
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

        }
        else if (id == R.id.tvFilterStockDate){
            dp.show();
        }
    }
}
