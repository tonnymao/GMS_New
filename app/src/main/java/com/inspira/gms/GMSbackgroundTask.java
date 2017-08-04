package com.inspira.gms;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shoma on 02/08/17.
 */

public class GMSbackgroundTask extends Service implements LocationListener {
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocationManager locationManager;
    private String provider;
    private Location oldLocation;
    private GlobalVar globalVar;

    @Override
    public void onCreate() {
        globalVar = new GlobalVar(this);
        HandlerThread thread = new HandlerThread("ServiceStartArguments",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();
        Log.i("GMSbackgroundTask", "starting background service");
        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        // Define the criteria how to select the locatioin provider -> use
        // default
        Criteria criteria = new Criteria();
        locationManager.getBestProvider(criteria, true);
//        provider = locationManager.getBestProvider(criteria, false);
        provider = LocationManager.NETWORK_PROVIDER;
        if(ContextCompat.checkSelfPermission(GMSbackgroundTask.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                oldLocation = location;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //onTaskRemoved(intent);

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return START_STICKY;
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if(ContextCompat.checkSelfPermission(GMSbackgroundTask.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                Log.i("GMSbackgroundTask", "background process works fine!!!");
                locationManager.requestLocationUpdates(provider, 400, 0, GMSbackgroundTask.this);
            }
//            try {
//                while(true) {
//                    if(ContextCompat.checkSelfPermission(GMSbackgroundTask.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                        locationManager.requestLocationUpdates(provider, 0, 0, GMSbackgroundTask.this);
//                        Location location = locationManager.getLastKnownLocation(provider);
//                        if(location!=null)
//                        {
//                            Log.i("GMSbackgroundTask", "background process works fine!");
//                            if (oldLocation.getLatitude() - location.getLatitude() != 0 || oldLocation.getLongitude() - location.getLongitude() != 0) {
//                                Log.i("GMSbackgroundTask", "background process works fine!!!");
//                                onLocationChanged(location);
//                            }
//                        }
//
//                    }
//                    Thread.sleep(5000);
//                }
//            } catch (InterruptedException e) {
//                Thread.currentThread().interrupt();
//            }
            //stopSelf(msg.arg1); <- don't use, ur gonna kill this
        }
    }

    private class pushTrackingGPStoDB extends AsyncTask<String, Void, String> {
        private JSONObject jsonObject;
        private String nomorthsales;
        private boolean gpsFakeStatus = false;
        private Location location;

        public pushTrackingGPStoDB(String nomorthsales, Location location) {
            super();
            this.nomorthsales = nomorthsales;
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (LibInspira.isMockSettingsON(getApplicationContext())) gpsFakeStatus = true;
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomorthsales", nomorthsales);
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("fakeGPS", String.valueOf(gpsFakeStatus));
                Log.i("GMSbackgroundTask", "sales number: " + nomorthsales);
                Log.i("GMSbackgroundTask", "latitude: " + location.getLatitude());
                Log.i("GMSbackgroundTask", "longitude: " + location.getLongitude());
                Log.i("GMSbackgroundTask", "fakeGPS status: " + gpsFakeStatus);
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return LibInspira.executePost(GMSbackgroundTask.this, urls[0], jsonObject);
        }

        @Override
        protected void onPostExecute(String s) {
            Log.i("GMSbackgroundTask", s);
            super.onPostExecute(s);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        String actionUrl = "Sales/pushTrackingData/";
        new pushTrackingGPStoDB(globalVar.userpreferences.getString("nomor_sales", ""), location).execute(actionUrl);
    }

    @Override
    public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override
    public void onProviderEnabled(String s) {

    }

    @Override
    public void onProviderDisabled(String s) {

    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        locationManager.removeUpdates(this);
        Intent restartServiceIntent = new Intent(getApplicationContext(),this.getClass());
        restartServiceIntent.setPackage(getPackageName());
        startService(restartServiceIntent);
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.i("GMSbackgroundTask", "SERVICE DESTROYED!");
        locationManager.removeUpdates(this);
    }
}
