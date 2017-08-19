package com.inspira.gms;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.animation.AlphaAnimation;

/**
 * Created by Tonny on 7/24/2017.
 */

public class GlobalVar {
    public static SharedPreferences sharedpreferences;
    public static SharedPreferences temppreferences;  //added by ADI @19-Aug-2017 //buat data-data yang cuma sementara  //boleh di clear kapan aja
    public static SharedPreferences userpreferences;
    public static SharedPreferences rolepreferences;
    public static SharedPreferences notifpreferences;
    public static SharedPreferences salespreferences;  //added by Tonny @01-Aug-2017
    public static SharedPreferences datapreferences;
    public static SharedPreferences settingpreferences;  //added by Tonny @03-Aug-2017
    public static SharedPreferences salestargetpreferences;  //added by Tonny @07-Aug-2017
    public static SharedPreferences stockmonitoringpreferences;  //added by Tonny @18-Aug-2017

    public static User user;
    public static Sales sales;  //added by Tonny @01-Aug-2017
    public static Data data;
    public static Shared shared;
    public static Settings settings;  //added by Tonny @03-Aug-2017
    public static Stock stock;  //added by Tonny @18-Aug-2017
    public static Temp temp; //added by ADI @20-Aug-2017

    public static AlphaAnimation buttoneffect = new AlphaAnimation(1F, 0.8F);
    public static AlphaAnimation listeffect = new AlphaAnimation(1F, 0.5F);

    public static String webserviceURL = "/wsGMS/gms/index.php/api/";

        public GlobalVar(Context context)
        {
            sharedpreferences = context.getSharedPreferences("global", Context.MODE_PRIVATE);
            userpreferences = context.getSharedPreferences("user", Context.MODE_PRIVATE);
            rolepreferences = context.getSharedPreferences("role", Context.MODE_PRIVATE);
            notifpreferences = context.getSharedPreferences("notif", Context.MODE_PRIVATE);
            salespreferences = context.getSharedPreferences("sales", Context.MODE_PRIVATE);  //added by Tonny @01-Aug-2017
            datapreferences = context.getSharedPreferences("data", Context.MODE_PRIVATE);
            settingpreferences = context.getSharedPreferences("settings", Context.MODE_PRIVATE);  //added by Tonny @03-Aug-2017
            stockmonitoringpreferences = context.getSharedPreferences("stock", Context.MODE_PRIVATE);  //added by Tonny @18-Aug-2017

            data = new Data();
            user = new User();
            sales = new Sales();  //added by Tonny @01-Aug-2017
            shared = new Shared();
            settings = new Settings();  //added by Tonny @03-Aug-2017
            stock = new Stock();  //added by Tonny @18-Aug-2017
            temp = new Temp(); //added by ADI @20-Aug-2017
        }

        public static void clearDataUser()
        {
            LibInspira.clearShared(userpreferences);
            LibInspira.clearShared(rolepreferences);
            LibInspira.clearShared(notifpreferences);
            LibInspira.clearShared(salespreferences);  //added by Tonny @01-Aug-2017
            LibInspira.clearShared(settingpreferences); //added by Tonny @03-Aug-2017
        }

        public class Shared
        {
            public String server = "server";
            public String position = "position";  //added by Tonny @07-Aug-2017
            public String namasales = "namasales"; //added by Tonny @07-Aug-2017
            public String nomorsales = "nomorsales"; //added by Tonny @07-Aug-2017
            public String periode = "periode"; //added by Tonny @07-Aug-2017 digunakan pada SalesTargetMonthly
            public String tahun = "tahun"; //added by Tonny @07-Aug-2017 digunakan pada SalesTargetMonthly
        }

        public class Temp
        {
            public String scheduletask_date = "scheduletask_date";
            public String scheduletask_time = "scheduletask_time";
            public String scheduletask_reminder = "scheduletask_reminder";
            public String scheduletask_description = "scheduletask_description";
            public String scheduletask_type = "scheduletask_type";
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
            public String cabang = "cabang"; //added by ADI @07-Aug-2017
            public String role_isowner = "role_isowner"; //role untuk mengetahui user termasuk owner atau tidak (0/1)
            public String role_issales = "role_issales"; //role untuk mengetahui user termasuk sales atau tidak (0/1)
            public String role_setting = "role_setting"; //role apakah user dapat melakukan setting atau tidak (0/1)
            public String role_settingtarget = "role_settingtarget"; //role apakah user dapat melakukan setting target atau tidak (0/1)
            public String role_salesorder = "role_salesorder"; //role apakah user dapat melakukan sales order atau tidak (0/1)
            public String role_stockmonitoring = "role_stockmonitoring"; //role apakah user dapat melihat stok atau tidak (0/1)
            public String role_pricelist = "role_pricelist"; //role apakah user dapat melihat pricelist atau tidak (0/1)
            public String role_addscheduletask = "role_addscheduletask"; //role apakah user dapat melihat pricelist atau tidak (0/1)
            public String role_salestracking = "role_salestracking"; //role apakah user dapat melihat trangking dari sales atau tidak (0/1)
            public String role_hpp = "role_hpp"; //role apakah user dapat melihat HPP dari barang (0/1)
            public String role_crossbranch = "role_crossbranch"; //role apakah user dapat melihat data dari cabang lain (0/1)
        }

        public class Data
        {
            public String user = "user"; // nomor~nama~location~hp
            public String barang = "barang"; // nomor~nama~namajual~kode
            public String schedule = "schedule";
            public String customer = "customer"; // nomor~nama~alamat~telpon~kode
            public String kota = "kota"; // nomor~nama~nomorpropinsi~kode
            public String periode = "periode"; // bulan~tahun
            public String salesman = "salesman"; // nomorsales~nama   //added by Tonny @05-Aug-2017
            public String salesmanmonthly = "salesmanmonthly";  // nomorsales-nama-target  //added by Tonny @07-Aug-2017
            public String cabang = "cabang";  // nomorcabang~namacabang //added by Tonny @08-Aug-2017
            public String price = "price"; //nomor~kode~nama~harga
            public String pricehpp = "pricehpp"; //nomor~kode~nama~harga~hpp
            public String stockKategori = "stockkategori";  //added by Tonny @18-Aug-2017
            public String stockBentuk = "stockrBentuk";  //added by Tonny @19-Aug-2017
            public String stockJenis = "stockJenis";  //added by Tonny @19-Aug-2017
            public String stockGrade = "stockGrade";  //added by Tonny @19-Aug-2017
            public String stockSurface = "stockSurface";  //added by Tonny @19-Aug-2017
        }

        public class Sales  //added by Tonny @01-Aug-2017
        {
            public String target = "target";
            public String omzet = "omzet";
        }

        public class Settings  //added by Tonny @03-Aug-2017
        {
            public String interval = "interval";
            public String radius = "radius";
            public String tracking = "tracking";
            public String jam_awal = "jam_awal";
            public String jam_akhir = "jam_akhir";
        }

        public class Stock //added by Tonny @18-Aug-2017
        {
            public String filterKategori = "filterkategori";  //added by Tonny @18-Aug-2017
            public String filterBentuk = "filterBentuk";  //added by Tonny @19-Aug-2017
            public String filterJenis = "filterJenis";  //added by Tonny @19-Aug-2017
            public String filterGrade = "filterGrade";  //added by Tonny @19-Aug-2017
            public String filterSurface = "filterSurface";  //added by Tonny @19-Aug-2017
        }
}
