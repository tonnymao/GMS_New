package com.inspira.gms;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.PersistableBundle;
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
        setContentView(R.layout.login);

        global = new GlobalVar(this);
        LibInspira.setShared(global.sharedpreferences, "server", "117.102.229.10");

        edtUsername = (EditText) findViewById(R.id.edtUsername);
        edtPassword = (EditText) findViewById(R.id.edtPassword);
        btnSubmit = (Button) findViewById(R.id.btnSubmit);

        btnSubmit.setOnClickListener(this);
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
            Log.d("tes", result);
            try {
                JSONArray jsonarray = new JSONArray(result);
                if(jsonarray.length() > 0){
                    for (int i = 0; i < jsonarray.length(); i++) {
                        JSONObject obj = jsonarray.getJSONObject(i);
                        if(!obj.has("query")){
                            //LibInspira.setShared(global.userpreferences, "username", obj.getString("username"));
                            LibInspira.setShared(global.userpreferences, "nomor", obj.getString("user_nomor"));
                            LibInspira.setShared(global.userpreferences, "hash", obj.getString("user_hash"));
//                            globalfunction.setShared("user", "username", username);
//                            globalfunction.setShared("user", "id", obj.getString("user_id"));
//                            globalfunction.setShared("user", "nomor", obj.getString("user_nomor"));
//                            globalfunction.setShared("user", "nama", obj.getString("user_nama"));
//                            globalfunction.setShared("user", "role", obj.getString("user_role"));
//                            globalfunction.setShared("user", "hash", obj.getString("user_hash"));
//                            globalfunction.setShared("user", "role_beritaacara", obj.getString("role_beritaacara"));
//                            globalfunction.setShared("user", "role_approveberitaacara", obj.getString("role_approveberitaacara"));
//                            globalfunction.setShared("user", "role_deliveryorder", obj.getString("role_deliveryorder"));
//                            globalfunction.setShared("user", "role_approvedeliveryorder", obj.getString("role_approvedeliveryorder"));
//                            globalfunction.setShared("user", "role_bpm", obj.getString("role_bpm"));
//                            globalfunction.setShared("user", "role_opname", obj.getString("role_opname"));

                            LibInspira.hideLoading();

                            Intent intent = new Intent(Login.this, MainActivity.class);
                            startActivity(intent);
                            //overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_right);
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
}
