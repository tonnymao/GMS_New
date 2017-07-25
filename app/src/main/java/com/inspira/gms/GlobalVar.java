package com.inspira.gms;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tonny on 7/24/2017.
 */

public class GlobalVar {
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences userpreferences;

        public GlobalVar(Context context)
        {
            sharedpreferences = context.getSharedPreferences("global", Context.MODE_PRIVATE);
            userpreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
        }
}
