package info.si2.iista.volunteernetworks;

import android.app.Fragment;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 10/11/15
 * Project: Virde
 */
public class FragmentInicio extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.map_page, container, false);

        final WatchViewStub stub = (WatchViewStub) rootView.findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                // Instanciación de objetos del layout con stub.findView...
                ImageView next = (ImageView)stub.findViewById(R.id.next);

            }
        });

        return rootView;

    }

}
