package layout;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
//import android.app.Fragment;
//import android.app.FragmentManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.SupportMapFragment;

import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.inspira.gms.GMSbackgroundTask;
import com.inspira.gms.GlobalVar;
import com.inspira.gms.LibInspira;
import com.inspira.gms.R;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static android.R.attr.contextClickable;
import static android.R.attr.fragment;

public class SalesNavigationFragment extends Fragment implements OnMapReadyCallback, android.location.LocationListener {
    private GoogleMap map;
    private MapView mapView;
    private Spinner spinner;
    private List<String> listUserNumber;
    private Calendar calendar;
    private String actionUrl;
    private EditText txtEndDate;
    private EditText txtStartDate;
    private List<Double> latitude;
    private List<Double> longitude;
    private List<String> dateLocation;
    private GoogleMap googleMap;
    protected String formatDate;
    protected SimpleDateFormat sdf;

    public SalesNavigationFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        listUserNumber = new ArrayList<>();
        actionUrl = "Sales/getTrackingData/";
        new getDataTracking(true, 0, null, null).execute(actionUrl);
        formatDate = "dd MMMM yyyy";
        sdf = new SimpleDateFormat(formatDate, Locale.US);
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
        spinner = (Spinner) getView().findViewById(R.id.edtSalesman);
        ((Button) getView().findViewById(R.id.btnSearch)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setupQuery();
            }
        });
        ((FloatingActionButton) getView().findViewById(R.id.showSearchFilter)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ((RelativeLayout) getView().findViewById(R.id.formLayout)).setVisibility(View.VISIBLE);
                ((RelativeLayout) getView().findViewById(R.id.Mapfragment)).setVisibility(View.GONE);
                FragmentManager fm = getChildFragmentManager();
                SupportMapFragment fragment = (SupportMapFragment) fm.findFragmentById(R.id.Mapfragment);
                if (fragment == null) {
                    fragment = SupportMapFragment.newInstance();
                    fm.beginTransaction().replace(R.id.Mapfragment, fragment).commit();
                    fragment.getMapAsync(SalesNavigationFragment.this);
                }
            }
        });
        calendarProcessor();
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
        this.googleMap = googleMap;
        if (dateLocation != null) {
            for (int i = 0; i < dateLocation.size(); i++) {
                String[] dateTime = dateLocation.get(i).split(" ");
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                try {
                    dateTime[0] = sdf.format(simpleDateFormat.parse(dateTime[0]));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                googleMap.addMarker(new MarkerOptions().position(
                        new LatLng(latitude.get(i), longitude.get(i))
                ).title(
                        spinner.getSelectedItem().toString()
                ).snippet(
                        "Tracking\nTanggal: " + dateTime[0] + "\nJam: " + dateTime[1]
                ));
                googleMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
                    @Override
                    public View getInfoWindow(Marker marker) {
                        return null;
                    }

                    @Override
                    public View getInfoContents(Marker marker) {
                        LinearLayout info = new LinearLayout(getContext());
                        info.setOrientation(LinearLayout.VERTICAL);

                        TextView title = new TextView(getContext());
                        title.setTextColor(Color.BLACK);
                        title.setGravity(Gravity.CENTER);
                        title.setTypeface(null, Typeface.BOLD);
                        title.setText(marker.getTitle());

                        TextView snippet = new TextView(getContext());
                        snippet.setTextColor(Color.GRAY);
                        snippet.setText(marker.getSnippet());

                        info.addView(title);
                        info.addView(snippet);

                        return info;
                    }
                });
            }
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(new LatLng(latitude.get(dateLocation.size() - 1), longitude.get(dateLocation.size() - 1)), 16);
            googleMap.animateCamera(cameraUpdate);
            googleMap.moveCamera(cameraUpdate);
            Log.i("SalesNavigationFragment", "map updated");
        }
    }

    public void setupQuery() {
        ((RelativeLayout) getView().findViewById(R.id.formLayout)).setVisibility(View.GONE);
        ((RelativeLayout) getView().findViewById(R.id.Mapfragment)).setVisibility(View.VISIBLE);
        new getDataTracking(false, spinner.getSelectedItemPosition(), txtStartDate.getText().toString(), txtEndDate.getText().toString()).execute(actionUrl);
    }

    private class getDataTracking extends AsyncTask<String, Void, String> {
        private JSONObject jsonObject;
        private boolean isUsers;
        private int indexOfUser;
        private String startDate, endDate;

        getDataTracking(boolean isUsersData, int indexOfUser, String startDate, String endDate) {
            isUsers = isUsersData;
            this.indexOfUser = indexOfUser;
            this.startDate = startDate;
            this.endDate = endDate;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("isUsers", isUsers);
                if (!isUsers) {
                    jsonObject.put("nomortuser", listUserNumber.get(indexOfUser));
                    jsonObject.put("startFilter", startDate);
                    jsonObject.put("endFilter", endDate);
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return LibInspira.executePost(getContext(), urls[0], jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            Log.d("SalesNavigationFragment", s);
            try {
                JSONArray jsonarray = new JSONArray(s);
                if(jsonarray.length() > 0) {
                    if (isUsers) {
                        List<String> list = new ArrayList<>();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject obj = jsonarray.getJSONObject(i);
                            listUserNumber.add(obj.getString("nomoruser"));
                            list.add(obj.getString("userid"));
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getContext(), android.R.layout.simple_spinner_item, list);
                        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                        spinner.setAdapter(adapter);
                    } else {
                        latitude = new ArrayList<>();
                        longitude = new ArrayList<>();
                        dateLocation = new ArrayList<>();
                        for (int i = 0; i < jsonarray.length(); i++) {
                            JSONObject obj = jsonarray.getJSONObject(i);
                            latitude.add(Double.valueOf(obj.getString("latitude")));
                            longitude.add(Double.valueOf(obj.getString("longitude")));
                            dateLocation.add(obj.getString("trackingDate"));
                        }
                        onMapReady(googleMap);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void calendarProcessor() {
        final DatePickerDialog.OnDateSetListener dateStart;
        final DatePickerDialog.OnDateSetListener dateEnd;
        calendar = Calendar.getInstance();
        txtStartDate = (EditText) getView().findViewById(R.id.startDateInput);
        txtEndDate = (EditText) getView().findViewById(R.id.endDateInput);

        txtStartDate.setText(sdf.format(calendar.getTime()));
        txtEndDate.setText(sdf.format(calendar.getTime()));

        dateStart = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                txtStartDate.setText(sdf.format(calendar.getTime()));
            }
        };

        dateEnd = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int year, int month, int day) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, day);
                txtEndDate.setText(sdf.format(calendar.getTime()));
            }
        };

        txtStartDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), dateStart, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });

        txtEndDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new DatePickerDialog(getContext(), dateEnd, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
            }
        });
    }
}
