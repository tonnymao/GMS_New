package com.inspira.gms;

import android.content.Context;
import android.content.SharedPreferences;

/**
 * Created by Tonny on 7/24/2017.
 */

public class GlobalVar {
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences userpreferences;
    public static SharedPreferences rolepreferences;
    public static SharedPreferences notifpreferences;
    public static SharedPreferences salespreferences;  //added by Tonny @01-Aug-2017
    public static SharedPreferences datapreferences;

    public static User user;
    public static Sales sales;  //added by Tonny @01-Aug-2017
    public static Data data;
    public static Shared shared;

    public static String webserviceURL = "/wsGMS/gms/index.php/api/";

        public GlobalVar(Context context)
        {
            sharedpreferences = context.getSharedPreferences("global", Context.MODE_PRIVATE);
            userpreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            rolepreferences = context.getSharedPreferences("role", Context.MODE_PRIVATE);
            notifpreferences = context.getSharedPreferences("notif", Context.MODE_PRIVATE);
            salespreferences = context.getSharedPreferences("sales", Context.MODE_PRIVATE);  //added by Tonny @01-Aug-2017
            datapreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);

            data = new Data();
            user = new User();
            sales = new Sales();  //added by Tonny @01-Aug-2017
            shared = new Shared();
        }

        public static void clearDataUser()
        {
            LibInspira.clearShared(userpreferences);
            LibInspira.clearShared(rolepreferences);
            LibInspira.clearShared(notifpreferences);
            LibInspira.clearShared(salespreferences);  //added by Tonny @01-Aug-2017
        }

        public class Shared
        {
            public String server = "server";
        }

        public class User
        {
            public String nomor = "nomor";
            public String password = "password";  //added by Tonny @30-Jul-2017
            public String nomor_android = "nomor_android";
            public String nomor_sales = "nomor_sales";
            public String nama = "nama";
            public String tipe = "tipe";
            public String role = "role";
            public String hash = "hash";
        }

        public class Data
        {
            public String user = "user"; // nomor~nama~location~hp
            public String barang = "barang"; // nomor~nama~namajual~kode
            public String schedule = "schedule";
            public String customer = "customer"; // nomor~nama~alamat~telpon~kode
            public String kota = "kota"; // nomor~nama~nomorpropinsi~kode
        }

        public class Sales  //added by Tonny @01-Aug-2017
        {
            public String target = "target";
            public String omzet = "omzet";
        }
}
