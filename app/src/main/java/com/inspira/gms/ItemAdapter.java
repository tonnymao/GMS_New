/******************************************************************************
 Author           : ADI
 Description      : Adapter untuk list
 History          :
     1. adapter ini digunakan untuk menampilkan dalam model yang simple
     2. jika membutuhkan adapter yang expert, copy isi class ini, kemudian
        paste pada java yang bersangkutan dan rename nama classnya
 ******************************************************************************/

package com.inspira.gms;

public class ItemAdapter {

    private String nomor;
    private String name;

    public ItemAdapter(String nomor, String name) {
        this.setNomor(nomor);
        this.setName(name);
    }

    public String getNomor() {
        return nomor;
    }

    public void setNomor(String _param) {
        this.nomor = _param;
    }

    public String getName() {
        return name;
    }

    public void setName(String _param) {
        this.name = _param;
    }

}
