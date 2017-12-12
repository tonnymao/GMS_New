package layout;

import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.BounceInterpolator;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.Projection;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import java.util.ArrayList;

import static com.inspira.gms.IndexInternal.global;

public class LiveTrackingFragment extends Fragment implements OnMapReadyCallback,
        LocationListener,
        View.OnClickListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        GoogleMap.OnCameraMoveStartedListener{

    private Marker mCurrLocationMarker;
    private Marker mMyLocationMarker;
    private Polyline mDirection;
    private GoogleMap mMap;
    private ChildEventListener mChildEventListener;
    private DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    private DatabaseReference mLocationRef = mRootRef.child("location");
    private DatabaseReference mChildRef;
    private LatLng positionNow;
    private LatLng mylocation;

    GoogleApiClient mGoogleApiClient;
    LocationRequest mLocationRequest;

    private int cameratype = 2;

    public static GlobalVar global;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_salestracking, container, false);
        getActivity().setTitle("Live Tracking");

        global = new GlobalVar(getContext());
        mChildRef = mLocationRef.child(LibInspira.getShared(global.temppreferences, global.temp.selected_nomor_user, ""));
        return v;
    }

    public void onClick(View v) {
        int id = v.getId();
        v.startAnimation(global.buttoneffect);
        if(id== R.id.btn_followme)
        {
            cameratype = 1;
            moveCamera();
        }
        else if(id==R.id.btn_followtarget)
        {
            cameratype = 2;
            moveCamera();
        }
    }

    public void onMapReady(final GoogleMap map) {
        mMap = map;
        mMap.setOnCameraMoveStartedListener(this);
        mMap.setTrafficEnabled(true);
        mChildRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("lat").getValue(String.class)!=null)
                {
                    final LatLng latLng = new LatLng(Double.parseDouble(dataSnapshot.child("lat").getValue(String.class)), Double.parseDouble(dataSnapshot.child("lon").getValue(String.class)));

                    if (mCurrLocationMarker != null) {
                        mCurrLocationMarker.setPosition(latLng);

                        final Handler handler = new Handler();
                        final long start = SystemClock.uptimeMillis();
                        Projection proj = mMap.getProjection();
                        Point startPoint = proj.toScreenLocation(positionNow);
                        startPoint.offset(0, -100);
                        final LatLng startLatLng = proj.fromScreenLocation(startPoint);
                        final long duration = 1500;
                        final BounceInterpolator interpolator = new BounceInterpolator();
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                Log.d("ok", "run: ");
                                long elapsed = SystemClock.uptimeMillis() - start;
                                float t = interpolator.getInterpolation((float) elapsed / duration);
                                double lng = t * latLng.longitude + (1 - t) * positionNow.longitude;
                                double lat = t * latLng.latitude + (1 - t) * positionNow.latitude;
                                positionNow = new LatLng(lat, lng);
                                mCurrLocationMarker.setPosition(positionNow);

                                moveCamera();

                                if (t < 1.0) {
                                    // Post again 16ms later.
                                    handler.postDelayed(this, 16);
                                }
                            }
                        });
                    }
                    else
                    {
                        MarkerOptions markerOptions = new MarkerOptions();
                        markerOptions.rotation(0);
                        markerOptions.position(latLng);
                        markerOptions.title("Target");

                        mCurrLocationMarker = mMap.addMarker(markerOptions);
                        positionNow = latLng;

                        moveCamera();

                        drawDirection();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public LatLng interpolate(float fraction, LatLng a, LatLng b) {
        double lat = (b.latitude - a.latitude) * fraction + a.latitude;
        double lng = (b.longitude - a.longitude) * fraction + a.longitude;
        return new LatLng(lat, lng);
    }

    public void moveCamera()
    {
        if(cameratype==2)
        {
            if(positionNow != null)
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(positionNow));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            }
        }
        else if(cameratype==1)
        {
            if(mylocation != null)
            {
                mMap.moveCamera(CameraUpdateFactory.newLatLng(mylocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(20));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        // TODO Auto-generated method stub
        if(mMyLocationMarker!=null)
        {
            mMyLocationMarker.remove();
        }

        Double lat = location.getLatitude();
        Double lon = location.getLongitude();
        mylocation = new LatLng(lat, lon);

        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.rotation(0);
        markerOptions.position(mylocation);
        markerOptions.title("My Position");
        mMyLocationMarker = mMap.addMarker(markerOptions);

        drawDirection();
    }

    private void drawDirection()
    {
        if(mylocation != null && positionNow != null)
        {
            if(mDirection!=null) mDirection.remove();

            GoogleDirection.withServerKey(getResources().getString(R.string.google_server_key))
                    .from(mylocation)
                    .to(positionNow)
                    .transportMode(TransportMode.DRIVING)
                    .alternativeRoute(true)
                    .avoid(AvoidType.INDOOR)
                    .unit(Unit.METRIC)
                    .execute(new DirectionCallback() {
                        @Override
                        public void onDirectionSuccess(Direction direction, String rawBody) {
                            if (direction.isOK()) {
                                Route route = direction.getRouteList().get(0);
                                Leg leg = route.getLegList().get(0);

                                ArrayList<LatLng> directionPositionList = leg.getDirectionPoint();
                                PolylineOptions polylineOptions = DirectionConverter.createPolyline(getActivity(), directionPositionList, 5, Color.MAGENTA);
                                mDirection = mMap.addPolyline(polylineOptions);
                            } else {
                                // Do something
                            }
                        }

                        @Override
                        public void onDirectionFailure(Throwable t) {
                            // Do something
                            if(mDirection!=null) mDirection.remove();
                        }
                    });
        }
        else
        {
            if(mDirection!=null) mDirection.remove();
        }
    }

    @Override
    public void onDestroyView()
    {
        super.onDestroyView();
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        getView().findViewById(R.id.tl_footer).setVisibility(View.VISIBLE);
        getView().findViewById(R.id.btn_followme).setOnClickListener(this);
        getView().findViewById(R.id.btn_followtarget).setOnClickListener(this);

        FragmentManager fm = getChildFragmentManager();
        SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.map_container);
        if (fragment == null) {
            fragment = SupportMapFragment.newInstance();
            fm.beginTransaction().replace(R.id.map_container, fragment).commit();
            fragment.getMapAsync(this);
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(getActivity(),
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
        else {
            buildGoogleApiClient();
        }
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(getActivity(),
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onCameraMoveStarted(int i) {
        cameratype = 0;
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}