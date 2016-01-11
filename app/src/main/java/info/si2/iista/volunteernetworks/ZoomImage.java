package info.si2.iista.volunteernetworks;

import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.widget.ImageView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 8/1/16
 * Project: Cinda
 */
public class ZoomImage extends AppCompatActivity {

    private ImageView imageZoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.zoom_image);

        // Action bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views
        imageZoom = (ImageView)findViewById(R.id.imageZoom);

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            imageZoom.setTransitionName("transitionZoom");
        }

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("title")) {
                if (getSupportActionBar() != null) {
                    String title = getIntent().getExtras().getString("title");
                    getSupportActionBar().setTitle(title);
                }
            }
            if (getIntent().getExtras().containsKey("img")) {
                Picasso.with(this)
                        .load(getIntent().getExtras().getString("img"))
                        .into(imageZoom, new Callback() {
                            @Override
                            public void onSuccess() {
                                new PhotoViewAttacher(imageZoom);
                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();

        switch (id) {
            case android.R.id.home:
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    finishAfterTransition();
                } else {
                    finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
