/******************************************************************************
    Author           : Tonny
    Description      : list data order customer untuk app external
    History          :

******************************************************************************/
package layout;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;
import static com.inspira.gms.IndexExternal.global;

public class CustomerOrderListFragment extends OnlineOrderListFragment implements View.OnClickListener{

    public CustomerOrderListFragment() {
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
        if(LibInspira.getShared(global.temppreferences, global.temp.order_status, "").equals("pending")){
            getActivity().setTitle("Pending Order List");
        }else{
            getActivity().setTitle("Approved Order List");
        }
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
        actionUrl = "Order/getCustomerOrderList/";
        super.onActivityCreated(bundle);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
//        int id = view.getId();
//
//        if(id==R.id.ibtnSearch)
//        {
//            search();
//        }
    }

//    private void search()
//    {
//        itemadapter.clear();
//        for(int ctr=0;ctr<list.size();ctr++)
//        {
//            if(etSearch.getText().equals(""))
//            {
//                if(!list.get(ctr).getNomor().equals(LibInspira.getShared(global.userpreferences, global.user.nomor_android, "")))
//                {
//                    itemadapter.add(list.get(ctr));
//                    itemadapter.notifyDataSetChanged();
//                }
//            }
//            else
//            {
//                if(LibInspira.contains(list.get(ctr).getNama(),etSearch.getText().toString() ))
//                {
//                    if(!list.get(ctr).getNomor().equals(LibInspira.getShared(global.userpreferences, global.user.nomor_android, "")))
//                    {
//                        itemadapter.add(list.get(ctr));
//                        itemadapter.notifyDataSetChanged();
//                    }
//                }
//            }
//        }
//    }

}
