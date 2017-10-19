/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan detail dan summary online order
 History          :

 ******************************************************************************/
package layout;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import static com.inspira.gms.IndexExternal.global;
import static com.inspira.gms.IndexExternal.jsonObject;

public class OnlineOrderFragment extends Fragment implements View.OnClickListener{
    private Button btnApprove, btnDisapprove;
    private SetApproval setApproval;
    private boolean isApproving;

    public OnlineOrderFragment() {
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
        View v = inflater.inflate(R.layout.fragment_tab_online_order, container, false);
        getActivity().setTitle("Online Order");
        return v;
    }

    public class PagerAdapter extends FragmentStatePagerAdapter {
        int mNumOfTabs;

        public PagerAdapter(FragmentManager fm, int NumOfTabs) {
            super(fm);
            this.mNumOfTabs = NumOfTabs;
        }

        @Override
        public Fragment getItem(int position) {
            switch (position) {
                case 0:
                    OnlineOrderHeaderFragment tab0 = new OnlineOrderHeaderFragment();
                    return tab0;
                case 1:
                    OnlineOrderDetailFragment tab1 = new OnlineOrderDetailFragment();
                    return tab1;
                default:
                    return null;
            }
        }

        @Override
        public int getCount() {
            return mNumOfTabs;
        }
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
    public void onActivityCreated(final Bundle bundle){
        super.onActivityCreated(bundle);

        btnApprove = (Button) getView().findViewById(R.id.btnApprove);
        btnDisapprove = (Button) getView().findViewById(R.id.btnDisapprove);

        btnApprove.setVisibility(View.VISIBLE);
        btnApprove.setOnClickListener(this);
        btnDisapprove.setVisibility(View.GONE);
        btnDisapprove.setOnClickListener(null);

//        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval")){  //jika approve, hide dan remove listener pada btnDisapprove
//            btnApprove.setVisibility(View.VISIBLE);
//            btnApprove.setOnClickListener(this);
//            btnDisapprove.setVisibility(View.GONE);
//            btnDisapprove.setOnClickListener(null);
//        }else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){  //jika approve, hide dan remove listener pada btnApprove
//            btnApprove.setVisibility(View.GONE);
//            btnApprove.setOnClickListener(null);
//            btnDisapprove.setVisibility(View.VISIBLE);
//            btnDisapprove.setOnClickListener(this);
//        }

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tabLayout);
        final ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount()));
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Header");
        tabLayout.getTabAt(1).setText("Detail");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnApprove){
//            isApproving = true;
            String actionUrl = "Order/setApproveOnlineOrder/";
            setApproval = new SetApproval();
            setApproval.execute(actionUrl);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if(setApproval != null) setApproval.cancel(true);
    }

    private class SetApproval extends AsyncTask<String, Void, String> {
        @Override
        protected String doInBackground(String... urls) {
            jsonObject = new JSONObject();
            try {
                jsonObject.put("nomor", LibInspira.getShared(global.temppreferences, global.temp.selected_list_nomor, ""));
                jsonObject.put("disc", LibInspira.getShared(global.temppreferences, global.temp.onlineorder_disc, ""));
                jsonObject.put("discnominal", LibInspira.getShared(global.temppreferences, global.temp.onlineorder_disc_nominal, ""));
                jsonObject.put("ppn", LibInspira.getShared(global.temppreferences, global.temp.onlineorder_ppn, ""));
                jsonObject.put("ppnnominal", LibInspira.getShared(global.temppreferences, global.temp.onlineorder_ppn_nominal, ""));
                jsonObject.put("subtotal", LibInspira.getShared(global.temppreferences, global.temp.onlineorder_subtotal, ""));
                jsonObject.put("total", LibInspira.getShared(global.temppreferences, global.temp.onlineorder_total, ""));
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
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = jsonarray.length() - 1; i >= 0; i--) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            LibInspira.hideLoading();
                            LibInspira.showLongToast(getContext(), "Data has been successfully approved");
                            LibInspira.BackFragment(getFragmentManager());
                        }else{
                            LibInspira.hideLoading();
                            LibInspira.alertbox("Approving data", obj.getString("query"), getActivity(), new Runnable(){
                                public void run() {
                                    LibInspira.BackFragment(getFragmentManager());
                                }
                            }, null);
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                LibInspira.hideLoading();
                LibInspira.alertbox("Approving data", e.getMessage(), getActivity(), new Runnable(){
                    public void run() {
                        LibInspira.BackFragment(getFragmentManager());
                    }
                }, null);
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(getContext(), "Approving data", "Loading...");
        }
    }
}
