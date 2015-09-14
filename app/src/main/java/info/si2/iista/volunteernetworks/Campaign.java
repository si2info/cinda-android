package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.util.CircleTransform;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 4/9/15
 * Project: Virde
 */
public class Campaign extends AppCompatActivity implements OnApiClientResult {

    private ItemCampaign campaign;

    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ImageView header;
    private TextView title;
    private TextView objective;
    private TextView geoArea;
    private TextView dates;

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
        collapsingToolbarLayout = (CollapsingToolbarLayout)findViewById(R.id.collapsing_toolbar);
        collapsingToolbarLayout.setTitle("");

        // Views
        header = (ImageView)findViewById(R.id.header);
        title = (TextView)findViewById(R.id.title);
        objective = (TextView)findViewById(R.id.objective);
        geoArea = (TextView)findViewById(R.id.geoArea);
        dates = (TextView)findViewById(R.id.dates);
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

        // Obtener datos de campaña con el ID
        if (getIntent().getExtras() != null)
            campaign = getIntent().getParcelableExtra("campaign");

        if (campaign != null) {

            // Title Campaign
            collapsingToolbarLayout.setTitle(campaign.getTitle());
            title.setText(campaign.getTitle());

            // Get campaign
            Virde.getInstance(this).getDataCampaign(campaign.getId());

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

    @Override
    public void onApiClientRequestResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case Virde.FROM_DATA_CAMPAIGN:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {

                    // Item campaign
                    ItemCampaign item = (ItemCampaign) result.second.get(0);

                    // Data campaign
                    title.setText(item.getTitle());
                    objective.setText(Html.fromHtml(String.format(getString(R.string.campaign_objective), item.getDescription())));
                    geoArea.setText(Html.fromHtml(String.format(getString(R.string.campaign_geo_area), item.getScope())));

                    String datesCampaigns = Util.parseDateToString(item.getDateStart()) + " - " + Util.parseDateToString(item.getDateEnd());
                    dates.setText(String.format(getString(R.string.campaign_dates), datesCampaigns));

                    // Header image
                    Picasso.with(this)
                            .load(item.getImage())
                            .into(header);

                }
                break;
        }
    }

}
