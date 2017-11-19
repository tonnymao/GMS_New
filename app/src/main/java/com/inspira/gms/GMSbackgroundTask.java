package com.inspira.gms;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
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
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Date;

/**
 * Created by shoma on 02/08/17.
 */

public class GMSbackgroundTask extends Service implements LocationListener {
    private static final int TWO_MINUTES = 1000 * 60 * 2;
    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;
    private LocationManager locationManager;
    private String GPSprovider;
    private String Networkprovider;
//    private Location oldLocation;
    private GlobalVar globalVar;
    private static Double oldLatitude;
    private static Double oldLongitude;
    private static int startState;
    private static int endState;
    private static double trackingRadius;
    private static long trackingInterval;
    private static String TAG;
    private static String trackingType;
    private Message message;

    @Override
    public void onCreate() {
//        if(!globalVar.settingpreferences.getString("jam_awal", "").equals(""))
//        {
            globalVar = new GlobalVar(this);
            HandlerThread thread = new HandlerThread("ServiceStartArguments",
                    Process.THREAD_PRIORITY_BACKGROUND);
            thread.start();
            TAG = GMSbackgroundTask.class.getSimpleName();
            Log.i("GMSbackgroundTask", "starting background service");
            mServiceLooper = thread.getLooper();
            mServiceHandler = new ServiceHandler(mServiceLooper);

            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

            Networkprovider = LocationManager.NETWORK_PROVIDER;
            GPSprovider = LocationManager.GPS_PROVIDER;

            trackingType = LibInspira.getShared(globalVar.settingpreferences, globalVar.settings.tracking, "");
            String[] stateTime = LibInspira.getShared(globalVar.settingpreferences, globalVar.settings.jam_awal, "").split(":");
            String stateTimeValue = stateTime[0] + stateTime[1];
            startState = Integer.valueOf(stateTimeValue);
            stateTime = LibInspira.getShared(globalVar.settingpreferences, globalVar.settings.jam_akhir, "").split(":");
            stateTimeValue = stateTime[0] + stateTime [1];
            endState = Integer.valueOf(stateTimeValue);
            trackingRadius = Double.valueOf(LibInspira.getShared(globalVar.settingpreferences, globalVar.settings.radius, ""));
            trackingInterval = Long.valueOf(LibInspira.getShared(globalVar.settingpreferences, globalVar.settings.interval, ""));

            if (!LibInspira.getShared(globalVar.datapreferences, globalVar.data.latitude, "").equals("")) {
                oldLatitude = Double.valueOf(LibInspira.getShared(globalVar.datapreferences, globalVar.data.latitude, ""));
                oldLongitude = Double.valueOf(LibInspira.getShared(globalVar.datapreferences, globalVar.data.longitude, ""));
            }
//            else if(ContextCompat.checkSelfPermission(GMSbackgroundTask.this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
//                Location location = locationManager.getLastKnownLocation(GPSprovider);
//                if (location == null && trackingType.equals("GPS and Network"))
//                    location = locationManager.getLastKnownLocation(Networkprovider);
//
//                oldLatitude = location.getLatitude();
//                oldLongitude = location.getLongitude();
//
//
//            }

            foregroundNotif("GMS Inspira", "Background Service Works Fine!"); // you can change the title and desc of the notification
//        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //onTaskRemoved(intent);

        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        mServiceHandler.sendMessage(msg);

        return super.onStartCommand(intent, flags, startId);
    }

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            Date currentDate = new Date();
            currentDate.getTime();
            String[] currentTime = currentDate.toString().substring(11, 16).split(":");
            String currentTimeValue = currentTime[0] + currentTime[1];
            locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            Log.i("GMSbackgroundTask", "current time: " + currentTimeValue + " start: " + startState + " end: " + endState);
            if (Integer.valueOf(currentTimeValue) >= startState && Integer.valueOf(currentTimeValue) <= endState) {
                if (locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER))
                    locationManager.requestLocationUpdates(GPSprovider, 0, 0, GMSbackgroundTask.this);
                if (trackingType.equals("GPS and Network"))
                    locationManager.requestLocationUpdates(Networkprovider, 0, 0, GMSbackgroundTask.this);
            }
//            LibInspira.ShowLongToast(getApplicationContext(), LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
            if (LibInspira.getShared(globalVar.userpreferences, globalVar.user.nomor, "").equals(""))
                stopSelf(msg.arg1);
            message = msg;
            //stopSelf(msg.arg1); <- don't use, ur gonna kill this
        }
    }

    private class pushTrackingGPStoDB extends AsyncTask<String, Void, String> {
        private JSONObject jsonObject;
        private String nomortuser;
        private boolean gpsFakeStatus = false;
        private Location location;

        public pushTrackingGPStoDB(String nomortuser, Location location) {
            super();
            this.nomortuser = nomortuser;
            this.location = location;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            gpsFakeStatus = LibInspira.isMockSettingsON(getApplicationContext());
        }

        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("nomortuser", nomortuser);
                jsonObject.put("latitude", location.getLatitude());
                jsonObject.put("longitude", location.getLongitude());
                jsonObject.put("fakeGPS", String.valueOf(gpsFakeStatus));

                Log.i("GMSbackgroundTask", "user number: " + nomortuser);
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
            LibInspira.setShared(globalVar.datapreferences, globalVar.data.latitude, location.getLatitude() + "");
            LibInspira.setShared(globalVar.datapreferences, globalVar.data.longitude, location.getLongitude() + "");
            LibInspira.makeNotification(getApplication(), 2, "Insert Location", s, null);
//            LibInspira.ShowLongToast(getApplicationContext(), "location inserted " + s);
            super.onPostExecute(s);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        Log.i("GMSbackgroundTask", "provider: " + location.getProvider());
        Log.i("GMSbackgroundTask", "latitude: " + location.getLatitude());
        Log.i("GMSbackgroundTask", "longitude: " + location.getLongitude());
        Log.i("GMSbackgroundTask", "Accuracy: " + location.getAccuracy());
        Log.i("GMSbackgroundTask", "old latitude: " + oldLatitude);
        Log.i("GMSbackgroundTask", "old longitude: " + oldLongitude);

        if(oldLatitude == null)
        {
            String actionUrl = "Sales/pushTrackingData/";
            new pushTrackingGPStoDB(LibInspira.getShared(globalVar.userpreferences, globalVar.user.nomor, ""), location).execute(actionUrl);
            oldLatitude = location.getLatitude();
            oldLongitude = location.getLongitude();
        } else {
            boolean currentDistranceState = distanceOverRadius(oldLatitude, oldLongitude, location.getLatitude(), location.getLongitude(), trackingRadius);
//            boolean goodLocation = isBetterLocation(location, oldLocation);
//            Log.i("GMSbackgroundTask", "Good Location: " + goodLocation);
            Log.i("GMSbackgroundTask", "Distance value: " + currentDistranceState);
//            LibInspira.ShowLongToast(getApplicationContext(), location.getProvider() + " | " + location.getAccuracy());
            LibInspira.makeNotification(getApplication(), 1, "Location",
                    "provider: " + location.getProvider() + "\naccuracy: " + location.getAccuracy() + "\nin radius: " + currentDistranceState,
                    null);
//            LibInspira.ShowLongToast(getApplicationContext(), "location value: " + currentDistranceState);
            if (currentDistranceState) {
                oldLatitude = location.getLatitude();
                oldLongitude = location.getLongitude();
                String actionUrl = "Sales/pushTrackingData/";
                new pushTrackingGPStoDB(LibInspira.getShared(globalVar.userpreferences, globalVar.user.nomor, ""), location).execute(actionUrl);
                Log.d("GMSbackgroundTask", "Location on radius");
            }
        }
        Log.d("GMSbackgroundTask", "Location updated");
        try {
//            LibInspira.ShowLongToast(getApplicationContext(), LibInspira.getShared(global.userpreferences, global.user.nomor, ""));
            if (LibInspira.getShared(globalVar.userpreferences, globalVar.user.nomor, "").equals(""))
                stopSelf(message.arg1);
//            LibInspira.ShowLongToast(getApplicationContext(), "sleeps " + trackingInterval);
            Thread.sleep(trackingInterval);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    private boolean distanceOverRadius(double oldLatitude, double oldLongitude, double newLatitude, double newLongitude, double radiusInMetre) {
        double theta = oldLongitude - newLongitude;
        double dist = Math.sin(deg2rad(oldLatitude))
                * Math.sin(deg2rad(newLatitude))
                + Math.cos(deg2rad(oldLatitude))
                * Math.cos(deg2rad(newLatitude))
                * Math.cos(deg2rad(theta));
        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515; // distance in Kilometers
        dist = dist * 1000; // distance in meters
        Log.i("GMSbackgroundTask", "Distance value: " + dist);
//        LibInspira.ShowLongToast(getApplicationContext(), "old location: " + oldLatitude + " - " + oldLongitude);
//        LibInspira.ShowLongToast(getApplicationContext(), "new location: " + newLatitude + " - " + newLongitude);
//        LibInspira.ShowLongToast(getApplicationContext(), "location distance: " + dist);
//        LibInspira.ShowLongToast(getApplicationContext(), "location radius: " + radiusInMetre);

        return dist > radiusInMetre;
    }

    private double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    private double rad2deg(double rad) {
        return (rad * 180.0 / Math.PI);
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

    protected boolean isBetterLocation(Location location, Location currentBestLocation) {
        if (currentBestLocation == null) {
            return true;
        }

        long timeDelta = location.getTime() - currentBestLocation.getTime();
        boolean isSignificantlyNewer = timeDelta > TWO_MINUTES;
        boolean isSignificantlyOlder = timeDelta < -TWO_MINUTES;
        boolean isNewer = timeDelta > 0;

        if (isSignificantlyNewer) {
            return true;
        } else if (isSignificantlyOlder) {
            return false;
        }

        int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
        boolean isLessAccurate = accuracyDelta > 0;
        boolean isMoreAccurate = accuracyDelta < 0;
        boolean isSignificantlyLessAccurate = accuracyDelta > 200;

        boolean isFromSameProvider = isSameProvider(location.getProvider(),
                currentBestLocation.getProvider());

        if (isMoreAccurate) {
            return true;
        } else if (isNewer && !isLessAccurate) {
            return true;
        } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
            return true;
        }
        return false;
    }

    /** Checks whether two providers are the same */
    private boolean isSameProvider(String provider1, String provider2) {
        if (provider1 == null) {
            return provider2 == null;
        }
        return provider1.equals(provider2);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        // AlarmManager for background service
        if (!LibInspira.getShared(globalVar.userpreferences, globalVar.user.nomor, "").equals("")) {
            Intent service = new Intent(getApplicationContext(), trackerBroadcastReciver.class);
            final PendingIntent pIntent = PendingIntent.getBroadcast(this, 88088, service, PendingIntent.FLAG_UPDATE_CURRENT);
            long firstMillis = System.currentTimeMillis();
            AlarmManager alarm = (AlarmManager) this.getSystemService(Context.ALARM_SERVICE);
            alarm.setInexactRepeating(AlarmManager.RTC_WAKEUP, firstMillis, 5000, pIntent);
            Log.v(TAG, "Task Removed, restarting service");
        }
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
                .setSmallIcon(R.drawable.logo).build();
        startForeground(notifID, notification);
    }
}
