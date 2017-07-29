/******************************************************************************
    Author           : ADI
    Description      : Adapter untuk list
    History          : 
         1. adapter ini digunakan untuk menampilkan dalam model yang simple
         2. jika membutuhkan adapter yang expert, copy isi class ini, kemudian
            paste pada java yang bersangkutan dan rename nama classnya
******************************************************************************/
package com.inspira.gms;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class ItemListAdapter extends ArrayAdapter<ItemAdapter> {

    private List<ItemAdapter> items;
    private int layoutResourceId;
    private Context context;

    public ItemListAdapter(Context context, int layoutResourceId, List<ItemAdapter> items) {
        super(context, layoutResourceId, items);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.items = items;
    }

    public List<ItemAdapter> getItems() {
        return items;
    }

    public static class Holder {
        ItemAdapter adapterItem;
        TextView tvNama;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        Holder holder = null;

        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        row = inflater.inflate(layoutResourceId, parent, false);

        holder = new Holder();
        holder.adapterItem = items.get(position);

        holder.tvNama = (TextView)row.findViewById(R.id.tvName);

        row.setTag(holder);
        setupItem(holder);

        return row;
    }

    private void setupItem(Holder holder) {
        holder.tvNama.setText(holder.adapterItem.getName());
    }
}
