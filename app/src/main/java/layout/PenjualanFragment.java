/******************************************************************************
    Author           : ADI
    Description      : menu pada salesorder untuk membedakan jenisnya (PPN, non PPN, Approval, dan Disapproval)
    History          :

******************************************************************************/
package layout;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import static com.inspira.gms.IndexInternal.global;
import static com.inspira.gms.IndexInternal.jsonObject;

//import android.app.Fragment;

public class PenjualanFragment extends Fragment implements View.OnClickListener{
    private Button btnPPN, btnNonPPN, btnApproval, btnDisapproval;

    public PenjualanFragment() {
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
        View v = inflater.inflate(R.layout.fragment_penjualan, container, false);
        getActivity().setTitle("Sales Order");
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

        btnPPN = (Button) getView().findViewById(R.id.btnPPN);
        btnNonPPN = (Button) getView().findViewById(R.id.btnNonPPN);
        btnApproval = (Button) getView().findViewById(R.id.btnApproval);
        btnDisapproval = (Button) getView().findViewById(R.id.btnDisapproval);

        btnPPN.setOnClickListener(this);
        btnNonPPN.setOnClickListener(this);
        btnApproval.setOnClickListener(this);
        btnDisapproval.setOnClickListener(this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        view.startAnimation(GlobalVar.buttoneffect);
        int id = view.getId();
        if(id==R.id.btnPPN)
        {
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_isPPN, "1");  //added by Tonny @04-Sep-2017
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PenjualanSubFragment());
        }
        else if(id==R.id.btnNonPPN)
        {
            LibInspira.setShared(global.temppreferences, global.temp.salesorder_isPPN, "0");  //added by Tonny @04-Sep-2017
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PenjualanSubFragment());
        }
        else if(id==R.id.btnApproval)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PenjualanSubFragment());
        }
        else if(id==R.id.btnDisapproval)
        {
            LibInspira.ReplaceFragment(getActivity().getSupportFragmentManager(), R.id.fragment_container, new PenjualanSubFragment());
        }
    }
}
