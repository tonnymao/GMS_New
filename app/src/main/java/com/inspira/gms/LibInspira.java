/******************************************************************************
    Author           : Tonny
    Description      : Library Inspira
    History          :
        o> 08-Jul-2017 (Tonny)
           * Create New
******************************************************************************/
package com.inspira.gms;

import android.app.Activity;
import android.app.Application;
import android.app.Fragment;  // is the Fragment class in the native version of the Android SDK. It was introduced in Android 3 (API 11)
//import android.support.v4.app.Fragment; // is the Fragment class for compatibility for older version < API 11
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.ContextWrapper;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

/**
 * Created by Tonny on 7/8/2017.
 */

public class LibInspira extends Application{

    public static void GoToActivity(String _activityName){

    }
    public static void GoToActivity(Integer _activityIndex){

    }
    public static void ReplaceFragment(FragmentManager _fragmentManager, Integer _fragmentContainerID, Fragment _fragmentName){
        FragmentTransaction fragmentTransaction = _fragmentManager.beginTransaction();
        fragmentTransaction.replace(_fragmentContainerID, _fragmentName);
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.commit();
    }
    public static void AddFragment(FragmentManager _fragmentManager, Integer _fragmentContainerID, Fragment _fragmentName){
        FragmentTransaction fragmentTransaction = _fragmentManager.beginTransaction();
        fragmentTransaction.add(_fragmentContainerID, _fragmentName);
        //fragmentTransaction.addToBackStack(null);  //remarked by Tonny @08-Jul-2017, masih belum tau apakah diperlukan
        fragmentTransaction.commit();
    }

    public static void RemoveFragment(FragmentManager _fragmentManager, Integer _fragmentContainerID, Fragment _fragmentName){
        FragmentTransaction fragmentTransaction = _fragmentManager.beginTransaction();
        fragmentTransaction.remove(_fragmentName);
        fragmentTransaction.commit();
    }

    public static void ShowShortToast(Context _context, String _message){
        Context context = _context;
        int duration = Toast.LENGTH_SHORT;
        Toast toast = Toast.makeText(context, _message, duration);
        toast.show();
    }

    public static void ShowLongToast(Context _context, String _message){
        Context context = _context;
        int duration = Toast.LENGTH_LONG;
        Toast toast = Toast.makeText(context, _message, duration);
        toast.show();
    }
}
