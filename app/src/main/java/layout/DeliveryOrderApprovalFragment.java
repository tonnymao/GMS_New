/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan menu approval pada delivery order
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

import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DeliveryOrderApprovalFragment extends Fragment implements View.OnClickListener{
    private Button btnApprove, btnDisapprove;
    private SetApproval setApproval;
    private boolean isApproving;

    private GlobalVar global;
    private JSONObject jsonObject;
    public DeliveryOrderApprovalFragment() {
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
        View v = inflater.inflate(R.layout.fragment_tab_delivery_order_approval, container, false);
        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval")){
            getActivity().setTitle("Approval Delivery Order");
        }else{
            getActivity().setTitle("Disapproval Delivery Order");
        }
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
                    FormDODetailItemListFragment tab0 = new FormDODetailItemListFragment();
//                    tab0.jenisDetail = "item";
                    return tab0;
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
        global = new GlobalVar(getActivity());

        btnApprove = (Button) getView().findViewById(R.id.btnApprove);
        btnDisapprove = (Button) getView().findViewById(R.id.btnDisapprove);

        if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval")){  //jika approve, hide dan remove listener pada btnDisapprove
            btnApprove.setVisibility(View.VISIBLE);
            btnApprove.setOnClickListener(this);
            btnDisapprove.setVisibility(View.GONE);
            btnDisapprove.setOnClickListener(null);
        }else if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("disapproval")){  //jika approve, hide dan remove listener pada btnApprove
            btnApprove.setVisibility(View.GONE);
            btnApprove.setOnClickListener(null);
            btnDisapprove.setVisibility(View.VISIBLE);
            btnDisapprove.setOnClickListener(this);
        }

        TabLayout tabLayout = (TabLayout) getView().findViewById(R.id.tabLayout);
        final ViewPager viewPager = (ViewPager) getView().findViewById(R.id.viewpager);

        viewPager.setAdapter(new PagerAdapter
                (getFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Item List");
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if(id == R.id.btnApprove){
            isApproving = true;
            String actionUrl = "Order/setApproveDO/";
            setApproval = new SetApproval();
            setApproval.execute(actionUrl);
        }else if(id == R.id.btnDisapprove){
            isApproving = false;
            String actionUrl = "Order/setDisapproveDO/";
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
                jsonObject.put("nomor", LibInspira.getShared(global.temppreferences, global.temp.salesorder_selected_list_nomor, ""));
                jsonObject.put("username", LibInspira.getShared(global.userpreferences, global.user.nama, ""));
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
                        if(!obj.has("error")){
                            LibInspira.hideLoading();
                            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval")){
                                LibInspira.ShowShortToast(getContext(), "Data has been successfully approved");
                            }else{
                                LibInspira.ShowShortToast(getContext(), "Data has been successfully disapproved");
                            }
                            LibInspira.BackFragment(getFragmentManager());
                        }else{
                            LibInspira.hideLoading();
                            String strAlert;
                            if(LibInspira.getShared(global.temppreferences, global.temp.salesorder_type_task, "").equals("approval")){
                                strAlert = "Approving data";
                            }else{
                                strAlert = "Disapproving data";
                            }
                            LibInspira.alertbox(strAlert, obj.getString("error"), getActivity(), new Runnable(){
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
