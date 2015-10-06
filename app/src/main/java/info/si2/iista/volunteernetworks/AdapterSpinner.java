package info.si2.iista.volunteernetworks;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 6/10/15
 * Project: Virde
 */
public class AdapterSpinner extends ArrayAdapter {

    public AdapterSpinner(Context context, int textViewResourceId, String[] items) {
        super(context, textViewResourceId, items);
    }

    public TextView getView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(Util.getRobotoLight(v.getContext()));
        return v;
    }

    public TextView getDropDownView(int position, View convertView, ViewGroup parent) {
        TextView v = (TextView) super.getView(position, convertView, parent);
        v.setTypeface(Util.getRobotoLight(v.getContext()));
        return v;
    }

}