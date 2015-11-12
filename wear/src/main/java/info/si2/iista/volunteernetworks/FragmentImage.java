package info.si2.iista.volunteernetworks;

import android.app.Fragment;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 11/11/15
 * Project: Virde
 */
public class FragmentImage extends Fragment {

    private ImageView mapImage;
    private ProgressBar progress;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        ViewGroup rootView = (ViewGroup) inflater.inflate(
                R.layout.activity_main, container, false);

        final WatchViewStub stub = (WatchViewStub) rootView.findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {

                mapImage = (ImageView) stub.findViewById(R.id.imageMap);
//                progress = (ProgressBar) stub.findViewById(R.id.progressBar);

                // Obtener tile para imagen de fondo
                if (getArguments() != null)
                    if (getArguments().containsKey("mapImage")) {
                        byte[] byteArray = getArguments().getByteArray("mapImage");
                        if (byteArray != null) {
                            Bitmap imagen = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);
                            mapImage.setImageBitmap(imagen);
//                            progress.setVisibility(View.GONE);
                        }
                    }

            }
        });

        return rootView;

    }

}