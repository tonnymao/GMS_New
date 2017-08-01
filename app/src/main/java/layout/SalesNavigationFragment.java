package layout;

import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.SupportMapFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import java.util.Map;

import static android.R.attr.contextClickable;
import static android.R.attr.fragment;

public class SalesNavigationFragment extends Fragment implements OnMapReadyCallback, android.location.LocationListener {
    private GoogleMap map;
    private MapView mapView;

    public SalesNavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sales_navigation, container, false);

        return v;
    }

    public void onActivityCreated(Bundle bundle) {
        super.onActivityCreated(bundle);
//        mapView = (MapView) getView().findViewById(R.id.Mapfragment);
//        mapView.onCreate(bundle);
//        mapView.getMapAsync(this);
        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.Mapfragment);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.Mapfragment, fragment).commit();
            fragment.getMapAsync(this);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    /*********************  ********************************************************/
    //Method di bawah ini merupakan hasil dari implements MapReadyCallBack
    /*****************************************************************************/
    @Override
    public void onLocationChanged(Location location) {

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.
        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        //googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(sydney, 16);
        googleMap.animateCamera(cameraUpdate);
        googleMap.moveCamera(cameraUpdate);
    }
}
