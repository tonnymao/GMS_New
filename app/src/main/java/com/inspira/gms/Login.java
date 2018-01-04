package com.inspira.gms;

import android.*;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by Tonny on 7/22/2017.
 */

public class Login extends AppCompatActivity implements View.OnClickListener{
    EditText edtUsername, edtPassword;
    Button btnSubmit;
    GlobalVar global;

    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        global = new GlobalVar(this);
        LibInspira.setShared(global.sharedpreferences, global.shared.server, "vpn.inspiraworld.com:99");
//        LibInspira.setShared(global.sharedpreferences, global.shared.server, "www.gmst.co.id:10505");

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);

        edtUsername.setVisibility(View.INVISIBLE);
        edtPassword.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);

        String urlversion = "Login/getVersion/";
        new getVersion().execute( urlversion );

        if(LibInspira.getShared(global.userpreferences,global.user.hash,"").equals(""))
        {
            edtUsername.setVisibility(View.VISIBLE);
            edtPassword.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.d("hash", LibInspira.getShared(global.userpreferences,global.user.hash,""));
            String actionUrl = "Login/checkUser/";
            new checkUser().execute( actionUrl );
        }

        // made by Shodiq
        //modified by ADI @01-Sep-2017
        // Permission for enabling location feature only for SDK Marshmallow | Android 6
//        if (Build.VERSION.SDK_INT >= 23)
//            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
//                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1600);
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M)
        {
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
            {
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE, android.Manifest.permission.READ_EXTERNAL_STORAGE, android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION}, 3);
            }
        }
    }

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSubmit){
            String actionUrl = "Login/loginUser/";
            new loginUser().execute( actionUrl );
        }
    }

    private void setdatauser(JSONObject obj)
    {
        try
        {
            LibInspira.setShared(global.userpreferences, global.user.nomor, obj.getString("user_nomor"));
            LibInspira.setShared(global.userpreferences, global.user.nomor_sales, obj.getString("user_nomor_sales"));
            LibInspira.setShared(global.userpreferences, global.user.kode, obj.getString("user_kode_sales"));  //added by Tonny @05-Sep-2017
            LibInspira.setShared(global.userpreferences, global.user.password, obj.getString("user_password"));  //added by Tonny @30-Jul-2017
            LibInspira.setShared(global.userpreferences, global.user.nama, obj.getString("user_nama"));
            LibInspira.setShared(global.userpreferences, global.user.tipe, obj.getString("user_tipe"));
            LibInspira.setShared(global.userpreferences, global.user.role, obj.getString("user_role"));
            LibInspira.setShared(global.userpreferences, global.user.hash, obj.getString("user_hash"));
            LibInspira.setShared(global.userpreferences, global.user.cabang, obj.getString("user_cabang"));
            LibInspira.setShared(global.userpreferences, global.user.namacabang, obj.getString("user_nama_cabang"));
            LibInspira.setShared(global.userpreferences, global.user.telp, obj.getString("user_telp"));

            LibInspira.setShared(global.userpreferences, global.user.role_isowner, obj.getString("role_isowner"));
            LibInspira.setShared(global.userpreferences, global.user.role_issales, obj.getString("role_issales"));
            LibInspira.setShared(global.userpreferences, global.user.role_setting, obj.getString("role_setting"));
            LibInspira.setShared(global.userpreferences, global.user.role_settingtarget, obj.getString("role_settingtarget"));
            LibInspira.setShared(global.userpreferences, global.user.role_salesorder, obj.getString("role_salesorder"));
            LibInspira.setShared(global.userpreferences, global.user.role_stockmonitoring, obj.getString("role_stockmonitoring"));
            LibInspira.setShared(global.userpreferences, global.user.role_pricelist, obj.getString("role_pricelist"));
            LibInspira.setShared(global.userpreferences, global.user.role_addscheduletask, obj.getString("role_addscheduletask"));
            LibInspira.setShared(global.userpreferences, global.user.role_salestracking, obj.getString("role_salestracking"));
            LibInspira.setShared(global.userpreferences, global.user.role_hpp, obj.getString("role_hpp"));
            LibInspira.setShared(global.userpreferences, global.user.role_crossbranch, obj.getString("role_crossbranch"));
            LibInspira.setShared(global.userpreferences, global.user.role_creategroup, obj.getString("role_creategroup"));

            LibInspira.setShared(global.settingpreferences, global.settings.interval, obj.getString("setting_interval"));
            LibInspira.setShared(global.settingpreferences, global.settings.radius, obj.getString("setting_radius"));
            LibInspira.setShared(global.settingpreferences, global.settings.tracking, obj.getString("setting_tracking"));
            LibInspira.setShared(global.settingpreferences, global.settings.jam_awal, obj.getString("setting_jamawal"));
            LibInspira.setShared(global.settingpreferences, global.settings.jam_akhir, obj.getString("setting_jamakhir"));
        }
        catch(Exception e)
        {
            e.printStackTrace();
            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
        }
    }

    /******************************************************************************
        Procedure : loginUser
        Author    : Tonny
        Date      : 26-Jul-2017
        Function  : Untuk berinteraksi dengan webservice untuk method loginUser
    ******************************************************************************/
    private class loginUser extends AsyncTask<String, Void, String> {
        String username = edtUsername.getText().toString();
        String password = edtPassword.getText().toString();

        JSONObject jsonObject;
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("username", username);
                jsonObject.put("password", password);
                jsonObject.put("token", LibInspira.getShared(global.userpreferences,global.user.token, ""));
                jsonObject.put("tipe", LibInspira.getShared(global.userpreferences,global.user.tipe, ""));  //added by Tonny @09-Oct-2017
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return LibInspira.executePost(Login.this, urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("resultQuery", result);
            try {
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            setdatauser(obj);

                            LibInspira.hideLoading();
//                            LibInspira.ShowLongToast(getBaseContext(), LibInspira.getShared(global.userpreferences, global.user.tipe, ""));

                            if(LibInspira.getShared(global.userpreferences, global.user.tipe, "").equals("0"))
                            {
                                Intent intent = new Intent(Login.this, IndexInternal.class);
                                startActivity(intent);
                                startService();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            }
                            else if(LibInspira.getShared(global.userpreferences, global.user.tipe, "").equals("1"))
                            {
                                Intent intent = new Intent(Login.this, IndexExternal.class);
                                startActivity(intent);
                                startService();
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
                            }
                        }
                        else
                        {
                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(Login.this, "Login", "Loading");
        }
    }

    /******************************************************************************
        Procedure : checkUser
        Author    : ADI
        Date      : 26-Jul-2017
        Function  : Untuk berinteraksi dengan webservice untuk method checkUser
    ******************************************************************************/
    private class checkUser extends AsyncTask<String, Void, String> {
        JSONObject jsonObject;
        @Override
        protected String doInBackground(String... urls) {
            try {
                jsonObject = new JSONObject();
                jsonObject.put("hash", LibInspira.getShared(global.userpreferences,global.user.hash,""));
            } catch (JSONException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return LibInspira.executePost(Login.this, urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("tes", result);
            try {
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            LibInspira.hideLoading();
                            String success = obj.getString("success");
                            if(success.equals("true")){
                                setdatauser(obj);

                                if(LibInspira.getShared(global.userpreferences, global.user.tipe, "").equals("0"))
                                {
                                    Intent intent = new Intent(Login.this, IndexInternal.class);
                                    startActivity(intent);
                                    startService();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                    finish();
                                }
                                else if(LibInspira.getShared(global.userpreferences, global.user.tipe, "").equals("1"))
                                {
                                    Intent intent = new Intent(Login.this, IndexExternal.class);
                                    startActivity(intent);
                                    startService();
                                    overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                    finish();
                                }
                            }
                            else
                            {
                                GlobalVar.clearDataUser();

                                edtUsername.setVisibility(View.VISIBLE);
                                edtPassword.setVisibility(View.VISIBLE);
                                btnSubmit.setVisibility(View.VISIBLE);
                                Toast.makeText(getBaseContext(), "User has login at another device", Toast.LENGTH_LONG).show();
                            }
                        }
                        else
                        {
                            edtUsername.setVisibility(View.VISIBLE);
                            edtPassword.setVisibility(View.VISIBLE);
                            btnSubmit.setVisibility(View.VISIBLE);
                            Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                            LibInspira.hideLoading();
                        }
                    }
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
                edtUsername.setVisibility(View.VISIBLE);
                edtPassword.setVisibility(View.VISIBLE);
                btnSubmit.setVisibility(View.VISIBLE);
                Toast.makeText(Login.this, "Login Failed", Toast.LENGTH_LONG).show();
                LibInspira.hideLoading();
            }
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LibInspira.showLoading(Login.this, "Login", "Loading");
        }
    }

    private void startService() {
        //startService(new Intent(this, LocationService.class));
        //added by Shodiq @01-Aug-2017
        // Permission for enabling location feature only for SDK Marshmallow | Android 6
        if (Build.VERSION.SDK_INT >= 23)
            if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)!= PackageManager.PERMISSION_GRANTED)
                ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, 1600);

        // made by Shodiq @8-aug-2017
        // check GPS status and ask to activate if GPS is disabled
//        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
//        if (locationManager.isProviderEnabled(locationManager.GPS_PROVIDER)) {
//            startService(new Intent(getApplicationContext(), GMSbackgroundTask.class));
//        } else {
//            Runnable commandOk = new Runnable() {
//                @Override
//                public void run() {
//                    Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                    startActivity(myIntent);
//                }
//            };
//            LibInspira.alertbox("Enable Location", "Your Locations Settings is disabled.\nPlease Enable Location to use this app", this, commandOk, null);
//        }
    }

    private class getVersion extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... urls) {
            JSONObject jsonObject;
            jsonObject = new JSONObject();
            return LibInspira.executePost(Login.this, urls[0], jsonObject);
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            Log.d("GETVERSION", result + "1");
            try {
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                            String version = pInfo.versionName;
                            if(!version.equals(obj.getString("version")))
                            {
                                showProgress();
                                Log.d("update", obj.getString("url"));
                                final UpdateApp atualizaApp = new UpdateApp();
                                atualizaApp.setContext(getApplicationContext());
                                atualizaApp.execute(obj.getString("url"));

                                mProgressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
                                    @Override
                                    public void onCancel(DialogInterface dialog) {
                                        atualizaApp.cancel(true);
                                    }
                                });
                            }
                            else
                            {
//                                Toast.makeText(getBaseContext(), "version same", Toast.LENGTH_LONG).show();
                                checkDone();
                            }
                        }
                        else
                        {
                            checkDone();
                        }
                    }
                }
            }catch(Exception e)
            {
                e.printStackTrace();
                Toast.makeText(getBaseContext(), "Checking version failed", Toast.LENGTH_LONG).show();
                checkDone();
            }
        }
    }

    private void checkDone()
    {
        if(LibInspira.getShared(global.userpreferences,global.user.hash,"").equals(""))
        {
            edtUsername.setVisibility(View.VISIBLE);
            edtPassword.setVisibility(View.VISIBLE);
            btnSubmit.setVisibility(View.VISIBLE);
        }
        else
        {
            Log.d("hash", LibInspira.getShared(global.userpreferences,global.user.hash,""));
            String actionUrl = "Login/checkUser/";
            new checkUser().execute( actionUrl );
        }
    }


    private ProgressDialog mSpinner;
    private void showSpinner(String t) {
        mSpinner = new ProgressDialog(this);
        mSpinner.setTitle("Downloading new version");
        mSpinner.setMessage("Please wait...");
        mSpinner.setCancelable(true);
        mSpinner.setCanceledOnTouchOutside(false);
        mSpinner.show();
    }

    private void showProgress() {
        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setMessage("Downloading new version");
        mProgressDialog.setIndeterminate(true);
        mProgressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        mProgressDialog.setCancelable(true);
        mProgressDialog.setCanceledOnTouchOutside(false);
    }

    private class UpdateApp extends AsyncTask<String, Integer, String> {

        private PowerManager.WakeLock mWakeLock;
        private Context context;

        public void setContext(Context contextf) {
            context = contextf;
        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                URL url = new URL(arg0[0]);
                HttpURLConnection c = (HttpURLConnection) url.openConnection();
                c.setRequestMethod("GET");
                c.setDoOutput(true);
                c.connect();

                String PATH = "/mnt/sdcard/Download/";
                File file = new File(PATH);
                file.mkdirs();
                File outputFile = new File(file, "update.apk");
                if (outputFile.exists()) {
                    outputFile.delete();
                }
                FileOutputStream fos = new FileOutputStream(outputFile);

                int fileLength = c.getContentLength();

                InputStream is = c.getInputStream();


                byte[] buffer = new byte[2048];
                int len1 = 0;
                long total = 0;
                while ((len1 = is.read(buffer)) != -1) {
                    total += len1;

                    if (fileLength > 0) // only if total length is known
                        publishProgress((int) (total * 100 / fileLength));

                    fos.write(buffer, 0, len1);
                }
                fos.close();
                is.close();

                if(Build.VERSION.SDK_INT>=24){
                    try{
                        Method m = StrictMode.class.getMethod("disableDeathOnFileUriExposure");
                        m.invoke(null);
                    }catch(Exception e){
                        e.printStackTrace();
                    }
                }

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setDataAndType(Uri.fromFile(new File("/mnt/sdcard/Download/update.apk")), "application/vnd.android.package-archive");
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); // without this flag android returned a intent error!
                context.startActivity(intent);


            } catch (Exception e) {
                Log.e("UpdateAPP", "Update error! " + e.getMessage());
            }
            return null;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // take CPU lock to prevent CPU from going off if the user
            // presses the power button during download
            PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
            mWakeLock = pm.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                    getClass().getName());
            mWakeLock.acquire();
            mProgressDialog.show();
        }

        @Override
        protected void onProgressUpdate(Integer... progress) {
            super.onProgressUpdate(progress);
            // if we get here, length is known, now set indeterminate to false
            mProgressDialog.setIndeterminate(false);
            mProgressDialog.setMax(100);
            mProgressDialog.setProgress(progress[0]);
        }

        @Override
        protected void onPostExecute(String result) {
            mWakeLock.release();
            mProgressDialog.dismiss();
            if (result != null)
                Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
            else
                Toast.makeText(context, "File downloaded", Toast.LENGTH_SHORT).show();
        }
    }
}
