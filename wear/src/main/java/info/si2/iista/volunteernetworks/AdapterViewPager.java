package info.si2.iista.volunteernetworks;

import android.app.Fragment;
import android.app.FragmentManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.wearable.view.FragmentGridPagerAdapter;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 10/11/15
 * Project: Virde
 */
public class AdapterViewPager extends FragmentGridPagerAdapter {

    private static final int NUM_ROWS = 1;
    public static final int NUM_COLUMS = 2;

    private ArrayList<String> data;
    private Bitmap image;

    public AdapterViewPager(FragmentManager fm, ArrayList<String> data) {
        super(fm);
        this.data = data;
    }

    @Override
    public void setCurrentColumnForRow(int row, int currentColumn) {
        super.setCurrentColumnForRow(row, currentColumn);
    }

    @Override
    public Fragment getFragment(int row, int col) {

        Fragment fragment;
        Bundle args;

        if (col == 1) {
            return new FragmentInicio();
        } else {

            fragment = new FragmentImage();

            if (image != null) { // Imagen recibida

                args = new Bundle();
                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                image.compress(Bitmap.CompressFormat.PNG, 75, stream);
                byte[] byteArray = stream.toByteArray();
                args.putByteArray("mapImage", byteArray);

                fragment.setArguments(args);

            }

            return fragment;
        }

    }

    @Override
    public int getRowCount() {
        return NUM_ROWS;
    }

    @Override
    public int getColumnCount(int i) {
        return NUM_COLUMS;
    }

    public void setImageMap (Bitmap bitmap) {
        this.image = bitmap;
    }

}
