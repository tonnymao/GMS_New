package com.inspira.gms;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Tonny on 7/22/2017.
 */

public class Login extends AppCompatActivity implements View.OnClickListener{
    EditText edtUsername, edtPassword;
    Button btnSubmit;
    GlobalVar global;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        global = new GlobalVar(this);
        LibInspira.setShared(global.sharedpreferences, global.shared.server, "117.102.229.10");

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);

        edtUsername.setVisibility(View.INVISIBLE);
        edtPassword.setVisibility(View.INVISIBLE);
        btnSubmit.setVisibility(View.INVISIBLE);

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

    @Override
    public void onClick(View v) {
        if(v.getId() == R.id.btnSubmit){
            String actionUrl = "Login/loginUser/";
            new loginUser().execute( actionUrl );
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
                            LibInspira.setShared(global.userpreferences, global.user.nomor, obj.getString("user_nomor"));
                            LibInspira.setShared(global.userpreferences, global.user.nomor_android, obj.getString("user_nomor_android"));
                            LibInspira.setShared(global.userpreferences, global.user.nomor_sales, obj.getString("user_nomor_sales"));
                            LibInspira.setShared(global.userpreferences, global.user.nama, obj.getString("user_nama"));
                            LibInspira.setShared(global.userpreferences, global.user.tipe, obj.getString("user_tipe"));
                            LibInspira.setShared(global.userpreferences, global.user.role, obj.getString("user_role"));
                            LibInspira.setShared(global.userpreferences, global.user.hash, obj.getString("user_hash"));

                            LibInspira.hideLoading();

                            Intent intent = new Intent(Login.this, IndexInternal.class);
                            startActivity(intent);
                            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                            finish();
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
                                LibInspira.setShared(global.userpreferences, global.user.nomor, obj.getString("user_nomor"));
                                LibInspira.setShared(global.userpreferences, global.user.nomor_android, obj.getString("user_nomor_android"));
                                LibInspira.setShared(global.userpreferences, global.user.nomor_sales, obj.getString("user_nomor_sales"));
                                LibInspira.setShared(global.userpreferences, global.user.nama, obj.getString("user_nama"));
                                LibInspira.setShared(global.userpreferences, global.user.tipe, obj.getString("user_tipe"));
                                LibInspira.setShared(global.userpreferences, global.user.role, obj.getString("user_role"));
                                LibInspira.setShared(global.userpreferences, global.user.hash, obj.getString("user_hash"));

                                Intent intent = new Intent(Login.this, IndexInternal.class);
                                startActivity(intent);
                                overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
                                finish();
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
}
