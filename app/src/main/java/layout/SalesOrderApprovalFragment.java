/******************************************************************************
 Author           : Tonny
 Description      : untuk menampilkan menu approval
 History          :

 ******************************************************************************/
package layout;

import android.content.Context;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.inspira.gms.R;

public class SalesOrderApprovalFragment extends Fragment implements View.OnClickListener{

    public SalesOrderApprovalFragment() {
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
        View v = inflater.inflate(R.layout.fragment_sales_order_approval, container, false);
        getActivity().setTitle("Approval Sales Order");
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
                    SummarySalesOrderFragment tab0 = new SummarySalesOrderFragment();
                    return tab0;
                case 1:
                    FormSalesOrderDetailItemListFragment tab1 = new FormSalesOrderDetailItemListFragment();
                    tab1.jenisDetail = "item";
                    return tab1;
                case 2:
                    FormSalesOrderDetailJasaListFragment tab2 = new FormSalesOrderDetailJasaListFragment();
                    tab2.jenisDetail = "jasa";
                    return tab2;
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
//                switch (position) {
//                    case 0:
//                        SummarySalesOrderFragment summary = new SummarySalesOrderFragment();
//                        summary.onActivityCreated(null);
//                    case 1:
//                        FormSalesOrderDetailItemListFragment item = new FormSalesOrderDetailItemListFragment();
//                        item.onActivityCreated(null);
//                    case 2:
//                        FormSalesOrderDetailItemListFragment jasa = new FormSalesOrderDetailItemListFragment();
//                        jasa.onActivityCreated(null);
//                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.getTabAt(0).setText("Summary");
        tabLayout.getTabAt(1).setText("Item");
        tabLayout.getTabAt(2).setText("Pekerjaan");
//        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                viewPager.setCurrentItem(tab.getPosition());
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public void onClick(View view) {
        //int id = view.getId();
    }

}
