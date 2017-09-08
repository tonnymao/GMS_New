/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan preview salesorder sebelum melakukan insert data
 History          :

 ******************************************************************************/
package layout;

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
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import static com.inspira.gms.IndexInternal.global;
import static com.inspira.gms.IndexInternal.jsonObject;

//import android.app.Fragment;

public class SummarySalesOrderFragment extends Fragment implements View.OnClickListener{
    private TextView tvCustomer, tvBroker, tvValuta, tvDate, tvSubtotal, tvGrandTotal, tvDiscNominal, tvPPNNominal;
    private EditText etDisc, etPPN;
    private Button btnSave;
    private RequestFormatSetting requestFormatSetting;
    private CheckCounter checkCounter;
    private InsertingData insertingData;

    public SummarySalesOrderFragment() {
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
        View v = inflater.inflate(R.layout.fragment_summary_sales_order, container, false);
        getActivity().setTitle("Sales Order Summary");
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
        tvDate = (TextView) getView().findViewById(R.id.tvDate);
        tvCustomer = (TextView) getView().findViewById(R.id.tvCustomer);
        tvBroker = (TextView) getView().findViewById(R.id.tvBroker);
        tvValuta = (TextView) getView().findViewById(R.id.tvValuta);
        tvSubtotal = (TextView) getView().findViewById(R.id.tvSubtotal);
        tvGrandTotal = (TextView) getView().findViewById(R.id.tvGrandTotal);
        tvDiscNominal = (TextView) getView().findViewById(R.id.tvDiscNominal);
        tvPPNNominal = (TextView) getView().findViewById(R.id.tvPPNNominal);
        etPPN = (EditText) getView().findViewById(R.id.etPPN);
        etDisc = (EditText) getView().findViewById(R.id.etDisc);
        btnSave = (Button) getView().findViewById(R.id.btnSave);

        etDisc.setText("0");
        etPPN.setText("0");
        tvDate.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_date, ""));
        tvCustomer.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nama, ""));
        tvBroker.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nama, ""));
        tvValuta.setText(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, ""));
        tvSubtotal.setText(LibInspira.delimeter(getSubtotal().toString()));
        tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));

        btnSave.setOnClickListener(this);
        etDisc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etDisc, this, true, false);
                tvDiscNominal.setText(LibInspira.delimeter(getNominalDiskon().toString()));
                tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
                tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_disc, etDisc.getText().toString().replace(",", ""));
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_disc_nominal, tvDiscNominal.getText().toString().replace(",", ""));
            }
        });

        etPPN.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                LibInspira.formatNumberEditText(etPPN, this, true, false);
                tvPPNNominal.setText(LibInspira.delimeter(getNominalPPN().toString()));
                tvGrandTotal.setText("Rp. " + LibInspira.delimeter(getGrandTotal().toString()));
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_ppn, etPPN.getText().toString().replace(",", ""));
                LibInspira.setShared(global.temppreferences, global.temp.salesorder_ppn_nominal, tvPPNNominal.getText().toString().replace(",", ""));
            }
        });

    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();

        if(id==R.id.btnSave)
        {
            LibInspira.alertBoxYesNo("Save Sales Order", "Do you want to add the current data?", getActivity(), new Runnable() {
                public void run() {
                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_subtotal, getSubtotal().toString());
                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_total, getGrandTotal().toString());
                    sendData();
                    //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailJasaListFragment());
                }
            }, new Runnable() {
                public void run() {}
            });
            //LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new FormSalesOrderDetailJasaFragment());
        }
        else if(id==R.id.btnBack)
        {
            LibInspira.BackFragment(getActivity().getSupportFragmentManager());
        }
    }

    //untuk mendapatkan nominal diskon
    private Double getNominalDiskon(){
        Double discNominal = 0.0;
        if (tvSubtotal.getText().toString().equals("")){
            tvSubtotal.setText("0.0");
        }else if (etDisc.getText().toString().equals("")){
            etDisc.setText("0.0");
        }else {
            if (!tvSubtotal.getText().toString().equals("")) {
                Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
                Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
                discNominal = disc * subtotal / 100;
            }
        }
        return discNominal;
    }

    //untuk mendapatkan nominal ppn (setelah diskon)
    private Double getNominalPPN(){
        Double ppnNominal = 0.0;
        if (tvSubtotal.getText().toString().equals("")){
            tvSubtotal.setText("0.0");
        }else if(etPPN.getText().toString().equals("")){
            etPPN.setText("0.0");
        }else if (etDisc.getText().toString().equals("")){
            etDisc.setText("0.0");
        }else {
            Double subtotal = Double.parseDouble(tvSubtotal.getText().toString().replace(",", ""));
            Double ppn = Double.parseDouble(etPPN.getText().toString().replace(",", ""));
            Double disc = Double.parseDouble(etDisc.getText().toString().replace(",", ""));
            Double discNominal = disc * subtotal / 100;
            ppnNominal = ppn * (subtotal - discNominal) / 100;
        }
        return ppnNominal;
    }
    //untuk mendapatkan nominal subtotal dari item dan pekerjaan
    private Double getSubtotal(){
        String data = LibInspira.getShared(global.temppreferences, global.temp.salesorder_item, "");;
        Double dblSubtotal = 0.0;
        Double dblItemSubtotal = 0.0;
        Double dblPekerjaanSubtotal = 0.0;
        Double dblFeeSubtotal = 0.0;
        String[] pieces = data.trim().split("\\|");
        if((pieces.length > 1 && !pieces[0].equals(""))){
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");
                    String fee = parts[6];
                    String subtotal = parts[8];
                    if(fee.equals("")) fee = "0";
                    if(subtotal.equals("")) subtotal = "0";
                    dblFeeSubtotal = dblFeeSubtotal+ Double.parseDouble(fee);
                    dblItemSubtotal = dblItemSubtotal + Double.parseDouble(subtotal);
                    dblSubtotal = dblSubtotal + Double.parseDouble(subtotal);
                    Log.d("subtotal item [" + i + "]", subtotal);
                }
            }
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_item_subtotal, dblItemSubtotal.toString());
        }

        data = LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan, "");;
        pieces = data.trim().split("\\|");
        if((pieces.length > 1 && !pieces[0].equals(""))){
            for(int i=0 ; i < pieces.length ; i++){
                if(!pieces[i].equals(""))
                {
                    String[] parts = pieces[i].trim().split("\\~");
                    String fee = parts[6];
                    String subtotal = parts[8];
                    if(fee.equals("")) fee = "0";
                    if(subtotal.equals("")) subtotal = "0";
                    LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, subtotal);
                    dblFeeSubtotal = dblFeeSubtotal+ Double.parseDouble(fee);
                    dblPekerjaanSubtotal = dblPekerjaanSubtotal + Double.parseDouble(subtotal);
                    dblSubtotal = dblSubtotal + Double.parseDouble(subtotal);
                }
            }
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, dblPekerjaanSubtotal.toString());
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_subtotal_fee, dblFeeSubtotal.toString());
        }
        return dblSubtotal;
    }

    private Double getGrandTotal(){
        Double grandtotal = getSubtotal() - getNominalDiskon() + getNominalPPN();
        return grandtotal;
    }


    //added by Tonny @02-Sep-2017
    //untuk menjalankan perintah send data ke web service
    private void sendData(){
        String actionUrl = "Order/getFormatSettingSalesOrder/";
        requestFormatSetting = new RequestFormatSetting();
        requestFormatSetting.execute(actionUrl);
    }

    //added by Tonny @04-Sep-2017
    //class yang digunakan untuk request format setting untuk autogenerate kode order jual
    private class RequestFormatSetting extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {

            //---------------------------------------------HEADER-----------------------------------------------------//
//                jsonObject.put("kode", LibInspira.getShared(global.temppreferences, global.temp.nomorsales, ""));
//                jsonObject.put("tanggal", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("nomorcustomer", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("kodecustomer", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("nomorbroker", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("kodebroker", LibInspira.getShared(global.temppreferences, global.temp.nomorsales, ""));
//                jsonObject.put("nomorsales", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("kodesales", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("subtotaljasa", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("subtotalbiaya", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("disc", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("discnominal", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("dpp", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("ppn", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("ppnnominal", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("totalrp", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("pembuat", LibInspira.getShared(global.temppreferences, global.temp.nomorsales, ""));
//                jsonObject.put("nomorcabang", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("cabang", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("valuta", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("jenispenjualan", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
//                jsonObject.put("isbarangimport", LibInspira.getShared(global.temppreferences, global.temp.enddate, ""));
//                jsonObject.put("isppn", LibInspira.getShared(global.temppreferences, global.temp.bulantahun, ""));
            //-------------------------------------------------------------------------------------------------------//
            //---------------------------------------------DETAIL----------------------------------------------------//
            jsonObject = new JSONObject();

            String strNomor = "";
            //jika bukan PPN, maka filter nomor pada formatsetting adalah 319
            if (LibInspira.getShared(global.temppreferences, global.temp.salesorder_isPPN, "").equals("0")){
                strNomor = "319";
            }else{  //jika PPN, maka filter nomor pada formatsetting adalah 320
                strNomor = "320";
            }
            try {
                jsonObject.put("nomor", strNomor);
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
                String formatsetting = "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            formatsetting = (obj.getString("formatsetting"));
                            if(formatsetting.equals("")) formatsetting = "null";
                        }
                    }
                    if(!formatsetting.equals(LibInspira.getShared(global.datapreferences, global.data.salesorder_formatsetting, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.salesorder_formatsetting,
                                formatsetting
                        );
                    }

                    String actionUrl = "Order/getCounter/";
                    checkCounter = new CheckCounter();
                    checkCounter.execute(actionUrl);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    //added by Tonny @04-Sep-2017
    //class yang digunakan untuk request nomot urut kode
    private class CheckCounter extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            //melakukan split pada salesorder_formatsetting berdasarkan char ","
            String data = LibInspira.getShared(global.datapreferences, global.data.salesorder_formatsetting, "");
            String[] pieces = data.trim().split("\\,");
            jsonObject = new JSONObject();
            //jika data tidak valid, maka batalkan request
            if(pieces.length > 1 && !pieces[0].equals(""))
            {
                //LibInspira.setShared(global.datapreferences, global.data.salesorder_nomorurut, "1");
                LibInspira.setShared(global.datapreferences, global.data.salesorder_prefix_kode, pieces[0]);
                LibInspira.setShared(global.datapreferences, global.data.salesorder_length_kode, pieces[1]);
                LibInspira.setShared(global.datapreferences, global.data.salesorder_formatdate_kode, pieces[2]);
                LibInspira.setShared(global.datapreferences, global.data.salesorder_header_kode, pieces[3]);
                LibInspira.setShared(global.datapreferences, global.data.salesorder_detail_kode, pieces[4]);
                Log.d("Prefix ", LibInspira.getShared(global.datapreferences, global.data.salesorder_prefix_kode, ""));
                Log.d("FormatDate ", LibInspira.getShared(global.datapreferences, global.data.salesorder_formatdate_kode, ""));
            }else{
                this.cancel(true);
            }

            try {
                jsonObject.put("kode", LibInspira.getShared(global.datapreferences, global.data.salesorder_header_kode, ""));
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
                String counter = "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            counter = (obj.getString("counter"));
                            if(counter.equals("")) counter = "0";
                        }
                    }

                    //ditambah 1
                    String nomorurut = Integer.toString(Integer.parseInt(counter) + 1);

                    if(!nomorurut.equals(LibInspira.getShared(global.datapreferences, global.data.salesorder_nomorurut_kode, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.salesorder_nomorurut_kode,
                                nomorurut
                        );
                    }
                    //createAutogenerate();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }



        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    //added by Tonny @04-Sep-2017
    //class yang digunakan untuk insert data
    private class InsertingData extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {

            //---------------------------------------------HEADER-----------------------------------------------------//
            try {
                jsonObject.put("kode", getAutogenerate());
                jsonObject.put("tanggal", LibInspira.getCurrentDate());
                jsonObject.put("nomorcustomer", LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_nomor, ""));
                jsonObject.put("kodecustomer", LibInspira.getShared(global.temppreferences, global.temp.salesorder_customer_kode, ""));
                jsonObject.put("nomorbroker", LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_nomor, ""));
                jsonObject.put("kodebroker", LibInspira.getShared(global.temppreferences, global.temp.salesorder_broker_kode, ""));
                jsonObject.put("nomorsales", LibInspira.getShared(global.userpreferences, global.user.nomor_sales, ""));
                jsonObject.put("kodesales", LibInspira.getShared(global.userpreferences, global.user.kode_sales, ""));
                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.salesorder_subtotal, ""));
                jsonObject.put("subtotaljasa", LibInspira.getShared(global.temppreferences, global.temp.salesorder_pekerjaan_subtotal, ""));
                jsonObject.put("subtotalbiaya", LibInspira.getShared(global.temppreferences, global.temp.salesorder_subtotal_fee, ""));
                jsonObject.put("disc", LibInspira.getShared(global.temppreferences, global.temp.salesorder_disc, ""));
                jsonObject.put("discnominal", LibInspira.getShared(global.temppreferences, global.temp.salesorder_disc_nominal, ""));
                //jsonObject.put("dpp", LibInspira.getShared(global.temppreferences, global.temp.salesorder_subtotal, ""));
                jsonObject.put("dpp", LibInspira.getShared(global.temppreferences, global.temp.salesorder_total, ""));
                jsonObject.put("ppn", LibInspira.getShared(global.temppreferences, global.temp.salesorder_ppn, ""));
                jsonObject.put("ppnnominal", LibInspira.getShared(global.temppreferences, global.temp.salesorder_ppn_nominal, ""));
                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.salesorder_total, ""));
                jsonObject.put("totalrp", Double.toString(getGrandTotal() * Double.parseDouble(LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_kurs, ""))));
                jsonObject.put("pembuat", LibInspira.getShared(global.userpreferences, global.user.nama, ""));
                jsonObject.put("nomorcabang", LibInspira.getShared(global.userpreferences, global.user.cabang, ""));
                jsonObject.put("cabang", LibInspira.getShared(global.temppreferences, global.user.namacabang, ""));
                jsonObject.put("valuta", LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_nama, ""));
                jsonObject.put("kurs", LibInspira.getShared(global.temppreferences, global.temp.salesorder_valuta_kurs, ""));
                jsonObject.put("jenispenjualan", "Material");
                jsonObject.put("isbarangimport", LibInspira.getShared(global.temppreferences, global.temp.salesorder_import, ""));
                jsonObject.put("isppn", LibInspira.getShared(global.temppreferences, global.temp.salesorder_isPPN, ""));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //-------------------------------------------------------------------------------------------------------//
            //---------------------------------------------DETAIL----------------------------------------------------//
            jsonObject = new JSONObject();

            String strNomor = "";
            //jika bukan PPN, maka filter nomor pada formatsetting adalah 319
            if (LibInspira.getShared(global.temppreferences, global.temp.salesorder_isPPN, "").equals("0")){
                strNomor = "319";
            }else{  //jika PPN, maka filter nomor pada formatsetting adalah 320
                strNomor = "320";
            }
            try {
                jsonObject.put("nomor", strNomor);
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
                String formatsetting = "";
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            formatsetting = (obj.getString("formatsetting"));
                            if(formatsetting.equals("")) formatsetting = "null";
                        }
                    }
                    if(!formatsetting.equals(LibInspira.getShared(global.datapreferences, global.data.salesorder_formatsetting, "")))
                    {
                        LibInspira.setShared(
                                global.datapreferences,
                                global.data.salesorder_formatsetting,
                                formatsetting
                        );
                    }

                    String actionUrl = "Order/getCounter/";
                    checkCounter = new CheckCounter();
                    checkCounter.execute(actionUrl);
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            //tvInformation.setVisibility(View.VISIBLE);
        }
    }

    private String getAutogenerate(){
        //format yang diinginkan
        String formatLengthNumerator = "%" + LibInspira.getShared(global.datapreferences, global.data.salesorder_length_kode, "") + "d";
        //prefix-[2digit cabang]/[2 digit tahun][2 digit bulan]/[nomor urut]
        String kodeHeaderOrderJual = LibInspira.getShared(global.datapreferences, global.data.salesorder_prefix_kode, "") + "-" +
                String.format("%02d", Integer.parseInt(LibInspira.getShared(global.userpreferences, global.user.cabang, ""))) + "/" +
                LibInspira.getCurrentDate(LibInspira.getShared(global.datapreferences, global.data.salesorder_formatdate_kode, "")) + "/" +
                String.format(formatLengthNumerator, Integer.parseInt(LibInspira.getShared(global.datapreferences, global.data.salesorder_nomorurut_kode, "")));
        //LibInspira.ShowShortToast(getContext(), kodeHeaderOrderJual);
        return kodeHeaderOrderJual;
    }
}
