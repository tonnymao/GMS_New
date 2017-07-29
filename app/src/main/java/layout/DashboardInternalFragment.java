/******************************************************************************
    Author           : ADI
    Description      : dashboard untuk internal
    History          :

******************************************************************************/
package layout;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

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
        ((RelativeLayout) getView().findViewById(R.id.btnContact)).setOnClickListener(this);
//        RelativeLayout btnSalesSchedule = (RelativeLayout) getView().findViewById(R.id.btnSalesSchedule);
//        RelativeLayout btnSalesOrder = (RelativeLayout) getView().findViewById(R.id.btnSalesOrder);

        //Untuk menambahkan fungsi button pada Menu
//        btnSalesNavigation.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //untuk melakukan replace fragment
//                LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new SalesNavigationFragment());
//            }
//        });
//        btnSalesSchedule.setOnClickListener(null);
//        btnSalesOrder.setOnClickListener(null);
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
    }
}
