package com.inspira.gms;

import android.app.Notification;
import android.app.PendingIntent;
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
    private static Double oldLatitude;
    private static Double oldLongitude;

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
        Criteria criteria = new Criteria();
        locationManager.getBestProvider(criteria, true);

        //if (globalVar.settingpreferences.getString("tracking", "").equals("GPS Only"))
            provider = LocationManager.GPS_PROVIDER;


        if(ContextCompat.checkSelfPermission(GMSbackgroundTask.this, android.Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            Location location = locationManager.getLastKnownLocation(provider);
            if (location != null) {
                oldLocation = location;
            }
        }

        foregroundNotif("GMS Inspira", "Background Service Works Fine!"); // you can change the title and desc of the notification
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
                locationManager.requestLocationUpdates(provider, 0, 0, GMSbackgroundTask.this);
            }
            //stopSelf(msg.arg1); <- don't use, ur gonna kill this
        }
    }

    private class pushTrackingGPStoDB extends AsyncTask<String, Void, String> {
        private JSONObject jsonObject;
        private String nomortuser;
        private String nomorthsales;
        private boolean gpsFakeStatus = false;
        private Location location;

        public pushTrackingGPStoDB(String nomortuser, String nomorthsales, Location location) {
            super();
            this.nomortuser = nomortuser;
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
                jsonObject.put("nomortuser", nomortuser);
                jsonObject.put("nomorthsales", nomorthsales);
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("fakeGPS", String.valueOf(gpsFakeStatus));

                Log.i("GMSbackgroundTask", "user number: " + nomortuser);
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
        if(oldLatitude != null)
        {
            double currentDistrance = distance(oldLatitude, oldLongitude, location.getLatitude(), location.getLongitude());
            Log.i("GMSbackgroundTask", "Distance value: " + currentDistrance);
            if(currentDistrance > 1)
            {
                oldLatitude = location.getLatitude();
                oldLongitude = location.getLongitude();
                String actionUrl = "Sales/pushTrackingData/";
                new pushTrackingGPStoDB(globalVar.userpreferences.getString("nomor", ""), globalVar.userpreferences.getString("nomor_sales", ""), location).execute(actionUrl);
            }
            else
                Log.d("GMSbackgroundTask", "Location on radius");
        }
        else
        {
            oldLatitude = location.getLatitude();
            oldLongitude = location.getLongitude();
        }
        Log.d("GMSbackgroundTask", "Location updated");
    }

    private double distance(double oldLatitude, double oldLongitude, double newLatitude, double newLongitude) {
        //3958.75
        double earthRadius = 6371; // in miles, change to 6371 for kilometer output

        double dLat = Math.toRadians(newLatitude-oldLatitude);
        double dLng = Math.toRadians(newLongitude-oldLongitude);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);

        double a = Math.pow(sindLat, 2) + Math.pow(sindLng, 2)
                * Math.cos(Math.toRadians(oldLatitude)) * Math.cos(Math.toRadians(newLatitude));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));

        double dist = earthRadius * c;

        return dist; // output distance, in MILES
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
        foregroundNotif("GMS Inspira", "Background Service Works Fine!"); // you can change the title and desc of the notification
        super.onTaskRemoved(rootIntent);
    }

    @Override
    public void onDestroy() {
        Log.i("GMSbackgroundTask", "SERVICE DESTROYED!");
        locationManager.removeUpdates(this);
    }

    private void foregroundNotif(String title, String text) {
        int notifID = 1;
        PendingIntent notifIntent = PendingIntent.getActivity(getApplicationContext(), 0, new Intent(GMSbackgroundTask.this, GMSbackgroundTask.class), 0);
        Notification notification = new Notification.Builder(getApplicationContext())
                .setContentTitle(title)
                .setContentText(text)
                .setTicker(" ToDoList Notification")
                .setWhen(System.currentTimeMillis())
                .setContentIntent(notifIntent)
                .setSmallIcon(R.drawable.gms_logo).build();
        startForeground(notifID, notification);
    }
}
