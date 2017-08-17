/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
    History          :

******************************************************************************/
package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import static com.inspira.gms.IndexInternal.RefreshUserData;
import static com.inspira.gms.IndexInternal.global;

//import android.app.Fragment;

public class DashboardInternalFragment extends Fragment implements View.OnClickListener{
    public DashboardInternalFragment() {
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
        View v = inflater.inflate(R.layout.fragment_dashboard_internal, container, false);
        getActivity().setTitle("Dashboard");
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
        getView().findViewById(R.id.btnContact).setOnClickListener(this);
        getView().findViewById(R.id.btnScheduleTask).setOnClickListener(this);
        getView().findViewById(R.id.btnPriceList).setOnClickListener(this);
        final SwipeRefreshLayout swipeRefreshLayout = (SwipeRefreshLayout) getView().findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener(){
            @Override
            public void onRefresh() {
                swipeRefreshLayout.setRefreshing(true);
                RefreshUserData();
                (new Handler()).postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                }, 3000);
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

        if(id==R.id.btnContact)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ContactFragment());
        }
        else if(id==R.id.btnScheduleTask)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ScheduleTaskFragment());
        }
        else if(id==R.id.btnPriceList)
        {
            Log.d("Crossbranch", LibInspira.getShared(global.userpreferences, global.user.role_crossbranch, ""));
            if(LibInspira.getShared(global.userpreferences, global.user.role_crossbranch, "").equals("1")){
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseCabangFragment());
            }else{
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PriceListFragment());
            }

        }
        else if(id==R.id.btnStockMonitoring)  //added by Tonny @16-Aug-2017
        {
            Log.d("Crossbranch", LibInspira.getShared(global.userpreferences, global.user.role_crossbranch, ""));
            if(LibInspira.getShared(global.userpreferences, global.user.role_crossbranch, "").equals("1")){
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new ChooseCabangFragment());
            }else{
                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new StockMonitoringFragment());  //modified by Tonny @17-Aug-2017
            }

        }
    }
}
