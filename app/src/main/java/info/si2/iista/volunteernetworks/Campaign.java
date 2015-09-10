package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import info.si2.iista.volunteernetworks.util.CircleTransform;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 4/9/15
 * Project: Virde
 */
public class Campaign extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.campaign);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // CollapsingToolbar
        CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("A B C D E F G H I J K L M N Ñ O P Q R S T U V W X Y Z");

        // Views
        TextView objective = (TextView)findViewById(R.id.objective);
        TextView geoArea = (TextView)findViewById(R.id.geoArea);
        TextView dates = (TextView)findViewById(R.id.dates);
        LinearLayout contributions = (LinearLayout)findViewById(R.id.contributions);

        // TODO - Prueba para añadir contribuciones del usuario
        for (int i=0; i<5; i++) {
            View v = getLayoutInflater().inflate(R.layout.item_campaign_user, null);

            // Views
            ImageView imgUser = (ImageView)v.findViewById(R.id.imgUser);
            TextView title = (TextView)v.findViewById(R.id.title);
            TextView description = (TextView)v.findViewById(R.id.description);

            Picasso.with(this)
                    .load(R.drawable.test_logo_si2)
                    .transform(new CircleTransform())
                    .resize(150, 150)
                    .into(imgUser);

            contributions.addView(v);
        }

    }

    public void addContribution (View view) {

        Intent intent = new Intent(this, Contribution.class);
        startActivity(intent);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
