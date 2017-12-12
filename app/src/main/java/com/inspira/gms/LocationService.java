package com.inspira.gms;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.os.SystemClock;
import android.provider.Settings;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

//import static com.inspira.gms.IndexInternal.global;

public class LocationService extends Service implements LocationListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    IBinder mBinder;

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    GoogleApiClient mGoogleApiClient;
    Location mLastLocation;
    LocationRequest mLocationRequest;

    private double getLatitude;
    private double getLongitude;

    private boolean isMock;
    private GlobalVar global;
    private boolean isNewLocation;
    private boolean canSave;

    private String trackingMode, jam_awal, jam_akhir;

    public static JSONObject jsonObject;

    DatabaseReference mRootRef = FirebaseDatabase.getInstance().getReference();
    DatabaseReference mLocationRef = mRootRef.child("location");

    @Override
    public IBinder onBind(Intent intent) {
        // A client is binding to the service with bindService()
        return mBinder;
    }

    @Override
    public void onCreate() {
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);
        global = new GlobalVar(this);

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            checkLocationPermission();
        }

        //Initialize Google Play Services
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    == PackageManager.PERMISSION_GRANTED) {
                buildGoogleApiClient();
            }
        }
        else {
            buildGoogleApiClient();
        }

        Log.d("locationService", "created");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
//        trackingMode = settingsharedpreferences.getString("tracking", "");
//        jam_awal = settingsharedpreferences.getString("jam_awal", "0");
//        jam_akhir = settingsharedpreferences.getString("jam_akhir", "0");

        isMock = false;
        isNewLocation = true;

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        DatabaseReference mChildRef = mLocationRef.child(LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
        mChildRef.child("lat").setValue("0");
        mChildRef.child("lon").setValue("0");

        Intent restartServiceTask = new Intent(getApplicationContext(),this.getClass());
        restartServiceTask.setPackage(getPackageName());
        PendingIntent restartPendingIntent = PendingIntent.getService(getApplicationContext(), 1,restartServiceTask, PendingIntent.FLAG_ONE_SHOT);
        AlarmManager myAlarmService = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        myAlarmService.set(
                AlarmManager.ELAPSED_REALTIME,
                SystemClock.elapsedRealtime() + 1000,
                restartPendingIntent);

        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        DatabaseReference mChildRef = mLocationRef.child(LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
        mChildRef.child("lat").setValue("0");
        mChildRef.child("lon").setValue("0");
    }

    @Override
    public void onLocationChanged(Location location) {
        if (Build.VERSION.SDK_INT >= 18) {
            isMock = location.isFromMockProvider();
        } else {
            isMock = Settings.Secure.getString(getContentResolver(), Settings.Secure.ALLOW_MOCK_LOCATION).equals("0");
        }

        if(isMock){
            Log.d("locationService", "something");
            Toast.makeText(this, "GPS Spoofing Detected", Toast.LENGTH_LONG).show();
        }
        else{
            mLastLocation = location;

            getLatitude = location.getLatitude();
            getLongitude = location.getLongitude();

            Log.d("locationService", "onLocationChanged: ");
            if(!LibInspira.getShared(global.userpreferences, global.user.nomor, "").equals(""))
            {
                Log.d("locationService", getLatitude + ", " + getLongitude);
                DatabaseReference mChildRef = mLocationRef.child(LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
                mChildRef.child("lat").setValue(String.valueOf(getLatitude));
                mChildRef.child("lon").setValue(String.valueOf(getLongitude));

//                checkingHours();
//                if (!isMock && canSave) {
//                    Log.d("ok", getLatitude + ", " + getLongitude);
//                    DatabaseReference mChildRef = mLocationRef.child(LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
//                    mChildRef.child("lat").setValue(String.valueOf(getLatitude));
//                    mChildRef.child("lon").setValue(String.valueOf(getLongitude));
//
//                    String actionUrl = "Salestracking/sendLocationBEX/";
//                    new updateLocation().execute(actionUrl);
//                }
//                else
//                {
//                    DatabaseReference mChildRef = mLocationRef.child(LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
//                    mChildRef.child("lat").setValue("0");
//                    mChildRef.child("lon").setValue("0");
//                }
            }

            isMock = false;
            isNewLocation = true;
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        Log.d("locationService", "onLocationChangedqqq: ");
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(1000);
        mLocationRequest.setFastestInterval(1000);
        mLocationRequest.setSmallestDisplacement(1);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            Log.d("locationService", "onLocationChanged: ");
            LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
        }
    }

    public void checkingHours()
    {
        try {
            SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");

            Date dateawal = sdf.parse(jam_awal);
            Date dateakhir = sdf.parse(jam_akhir);

            String date = sdf.format(new Date());
            Date datesekarang = sdf.parse(date);

            if (datesekarang.after(dateawal) && datesekarang.before(dateakhir)) {
                canSave = true;
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onConnectionSuspended(int res) {
//        mGoogleApiClient.connect();
//        Toast.makeText(this, "Google Play Services connection suspended", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
//        mGoogleApiClient.connect();
//        Toast.makeText(this, "Google Play Services connection failed", Toast.LENGTH_SHORT).show();
    }

    protected synchronized void buildGoogleApiClient() {
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
        mGoogleApiClient.connect();
    }

    private class updateLocation extends AsyncTask<String, Void, String> {
        String user_nomor = LibInspira.getShared(global.userpreferences, global.user.nomor, "");
        String latitude = String.valueOf(getLatitude);
        String longitude = String.valueOf(getLongitude);

        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("user_nomor", user_nomor);
                jsonObject.put("latitude", latitude);
                jsonObject.put("longitude", longitude);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }

            return LibInspira.executePost(getApplicationContext(), urls[0], jsonObject);
        }

        @Override
        protected void onPostExecute(String result) {
            canSave = true;
        }

        @Override
        protected void onPreExecute() {
            canSave = false;
        }
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            //stopSelf(msg.arg1); <- don't use, ur gonna kill this
        }
    }
}