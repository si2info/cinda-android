package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
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
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.CircleTransform;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 4/9/15
 * Project: Virde
 */
public class Campaign extends AppCompatActivity implements OnApiClientResult, OnDBApiResult,
        AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener {

    // Data
    private ItemCampaign campaign;

    // View
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private ImageView header;
    private LinearLayout infoCampaign;
    private TextView title;
    private TextView objective;
    private TextView geoArea;
    private TextView dates;

    // Flag
    private boolean fromDB;

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
        collapsingToolbarLayout.setTitle("");

        // Views
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        header = (ImageView)findViewById(R.id.header);
        title = (TextView)findViewById(R.id.title);
        infoCampaign = (LinearLayout)findViewById(R.id.infoCampaign);
        objective = (TextView)findViewById(R.id.objective);
        geoArea = (TextView)findViewById(R.id.geoArea);
        dates = (TextView)findViewById(R.id.dates);
        LinearLayout contributions = (LinearLayout)findViewById(R.id.contributions);

        // Refresh listener
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

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
            getCampaign(campaign.getId());

            // Feedback to user
            doRefresh();

        }

    }

    /**
     * Obtiene la campaña seleccionada desde Internet si se dispone de él o desde DB
     * @param id ID de la campaña que se quiere
     */
    public void getCampaign (int id) {
        if (Util.checkInternetConnection(this)) {
            Virde.getInstance(this).getDataCampaign(id); // Campaign from internet
        } else {
            DBVirde.getInstance(this).getCampaign(id); // Campaign from DB
        }
    }

    /**
     * Intent a Contribution class para añadir una nueva contribución a esta campaña
     * @param view FloatingActionButton
     */
    public void addContribution (View view) {

        Intent intent = new Intent(this, Contribution.class);
        intent.putExtra("idCampaign", campaign.getId());
        startActivity(intent);

    }

    /**
     * Actualiza la vista con la información de la campaña
     * @param item ItemCampaign
     */
    public void updateActivityInfo (ItemCampaign item) {

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

        // Update itemCampaign
        if (!fromDB)
            DBVirde.getInstance(this).updateCampaign(item);

        // Animation
        animateActivityInfo();

    }

    /**
     * Animación de info de campaña
     */
    public void animateActivityInfo () {

        infoCampaign.setVisibility(View.VISIBLE);
        Animation animation = AnimationUtils.loadAnimation(this, R.anim.anim_info_campaign);
        animation.setDuration(400);
        infoCampaign.setAnimation(animation);
        infoCampaign.animate();
        animation.start();

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
    public void onRefresh() {
        mSwipeRefreshLayout.setEnabled(false);
        getCampaign(campaign.getId());
    }

    /**
     * Feedback to user, loading items
     */
    public void doRefresh () {
        mSwipeRefreshLayout.post(new Runnable() {
            @Override
            public void run() {
                mSwipeRefreshLayout.setRefreshing(true);
                mSwipeRefreshLayout.setEnabled(false);
            }
        });
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
                    updateActivityInfo(item);

                }
                break;
        }
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDBApiInsertResult(Result result) {
    }

    @Override
    public void onDBApiSelectResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case DBVirde.FROM_SELECT_CAMPAIGN:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Campaign not available");
                    throw new RuntimeException("Error to select ItemCampaign");
                } else {

                    fromDB = true;
                    ItemCampaign item = (ItemCampaign) result.second.get(0);
                    updateActivityInfo(item);

                }
                break;
        }
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    @Override
    public void onDBApiUpdateResult(Result result) {
        switch (result.getResultFrom()) {
            case DBVirde.FROM_UPDATE_CAMPAIGN:
                if (result.isError()) {
                    Log.e("DBVirde", "Campaign not updated");
                    throw new RuntimeException("Error to update ItemCampaign");
                }
                break;
        }
    }

    @Override
    public void onOffsetChanged(AppBarLayout appBarLayout, int i) {
        mSwipeRefreshLayout.setEnabled(i == 0);
    }

    @Override
    protected void onResume() {
        super.onResume();
        appBarLayout.addOnOffsetChangedListener(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        appBarLayout.removeOnOffsetChangedListener(this);
    }

}
