/******************************************************************************
    Author           : Tonny
    Description      : Library Inspira
    History          :
        o> 08-Jul-2017 (Tonny)
           * Create New
******************************************************************************/
package com.inspira.gms;

//import android.app.Fragment;  // is the Fragment class in the native version of the Android SDK. It was introduced in Android 3 (API 11)
import android.app.ProgressDialog;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment; // is the Fragment class for compatibility for older version < API 11
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by Tonny on 7/8/2017.
 */

public class LibInspira {
    private static ProgressDialog loadingDialog;
    private static String hostUrl;

    public static void GoToActivity(String _activityName){

    }
    public static void GoToActivity(Integer _activityIndex){

    }
    public static void ReplaceFragment(FragmentManager _fragmentManager, Integer _fragmentContainerID, Fragment _fragment){
        FragmentTransaction fragmentTransaction = _fragmentManager.beginTransaction();
        fragmentTransaction.replace(_fragmentContainerID, _fragment);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }

    public static void AddFragment(FragmentManager _fragmentManager, Integer _fragmentContainerID, Fragment _fragment){
        FragmentTransaction fragmentTransaction = _fragmentManager.beginTransaction();
        fragmentTransaction.add(_fragmentContainerID, _fragment);
        //fragmentTransaction.addToBackStack(null);  //remarked by Tonny @08-Jul-2017, masih belum tau apakah diperlukan
        fragmentTransaction.commit();
    }

    public static void RemoveFragment(FragmentManager _fragmentManager, Integer _fragmentContainerID, Fragment _fragmentName){
        FragmentTransaction fragmentTransaction = _fragmentManager.beginTransaction();
        fragmentTransaction.remove(_fragmentName);
        fragmentTransaction.commit();
    }

    //added by Tonny @15-Jul-2017
    public static void ShowShortToast(Context _context, String _message){
        Context context = _context;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, _message, duration);
        toast.show();
    }

    //added by Tonny @15-Jul-2017
    public static void ShowLongToast(Context _context, String _message){
        Context context = _context;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, _message, duration);
        toast.show();
    }

    //added by Adi @24-Jul-2017
    public static String getShared(SharedPreferences _preference, String _key, String _defaultValue) {
        return _preference.getString(_key, _defaultValue);
    }

    //added by Adi @24-Jul-2017
    public static void setShared(SharedPreferences _preference, String _key, String _param) {
        SharedPreferences.Editor editor = _preference.edit();
        editor.putString(_key, _param);
        //editor.commit();
        editor.apply();
    }

    //added by Adi @24-Jul-2017
    public static void clearShared(SharedPreferences _preference) {
        //preference.edit().clear().commit();
        _preference.edit().clear().apply();
    }

    public static void hideLoading()
    {
        loadingDialog.dismiss();
    }

    public static void showLoading(Context _context, String _title, String _message)
    {
        loadingDialog = new ProgressDialog(_context);
        loadingDialog.setTitle(_title);
        loadingDialog.setMessage(_message);
        loadingDialog.setCancelable(true);
        loadingDialog.setCanceledOnTouchOutside(false);
        loadingDialog.show();
    }

    // Execute POST JSON and Retrieve Data JSON
    public static String  executePost(Context _context, String _targetURL, JSONObject _jsonObject){
        GlobalVar global = new GlobalVar(_context);
        String url = getShared(global.sharedpreferences, "server", "");
        hostUrl = "http://" + url + "/wsGMS/gms/index.php/api/";

        Log.d("host", hostUrl + _targetURL);

        InputStream inputStream = null;
        String result = "";
        try {

            // 1. create HttpClient
            HttpClient httpClient = new DefaultHttpClient();  //remarked by Tonny @24-Jul-2017 deprecated
//            HttpClient httpClient = HttpClientBuilder.create().build();  //added by Tonny @24-Jul-2017

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost( hostUrl + _targetURL );

            // 3. convert JSONObject to JSON to String
            String json = _jsonObject.toString();

            // 4. ** Alternative way to convert Person object to JSON string usin Jackson Lib
            // ObjectMapper mapper = new ObjectMapper();
            // json = mapper.writeValueAsString(person);

            // 5. set json to StringEntity
            StringEntity stringEntity = new StringEntity(json);

            // 6. set httpPost Entity
            httpPost.setEntity(stringEntity);

            // 7. Set some headers to inform server about the type of the content
            httpPost.setHeader("Accept", "application/json");
            httpPost.setHeader("Content-type", "application/json");

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpClient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);
            else
                result = "Did not work!";

        } catch (Exception e) {
            Log.d("InputStream", e.getLocalizedMessage());
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream _inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(_inputStream));
        String line = "";
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        _inputStream.close();
        return result;
    }
}
