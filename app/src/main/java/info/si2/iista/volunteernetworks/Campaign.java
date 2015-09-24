package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemFormContribution;
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
    private int position;

    // View
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private ImageView header;
    private RelativeLayout infoCampaign;
    private TextView title;
    private TextSwitcher description;
    private TextView geoArea;
    private TextView dates;
    private LinearLayout contributions;
    private Button suscription;

    // Flag
    private boolean fromDB;
    private boolean buttonSuscribeTouch;

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
        infoCampaign = (RelativeLayout)findViewById(R.id.infoCampaign);
        description = (TextSwitcher)findViewById(R.id.description);
        geoArea = (TextView)findViewById(R.id.campaign_geo);
        dates = (TextView)findViewById(R.id.campaign_dates);
        contributions = (LinearLayout)findViewById(R.id.contributions);
        suscription = (Button)findViewById(R.id.suscriptionButton);

        // Refresh listener
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Set the factory used to create TextViews to switch between.
        description.setFactory(mFactory);

        /*
         * Set the in and out animations. Using the fade_in/out animations
         * provided by the framework.
         */
        Animation in = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation out = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);
        description.setInAnimation(in);
        description.setOutAnimation(out);

        // Set the initial text without an animation
        description.setCurrentText(" ");

        // Suscribe / Unsuscribe
        suscription.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                setStyleButton(!campaign.isSuscribe(), suscription);

                Virde.getInstance(Campaign.this)
                        .suscription(campaign.getId(),
                                Util.getPreference(getApplicationContext(), getString(R.string.token)),
                                !campaign.isSuscribe());

                buttonSuscribeTouch = true;

            }
        });

        // Obtener datos de campaña con el ID
        if (getIntent().getExtras() != null) {
            campaign = getIntent().getParcelableExtra("campaign");
            position = getIntent().getIntExtra("position", -1);
        }

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
     * The {@link android.widget.ViewSwitcher.ViewFactory} used to create {@link android.widget.TextView}s that the
     * {@link android.widget.TextSwitcher} will switch between.
     */
    private ViewSwitcher.ViewFactory mFactory = new ViewSwitcher.ViewFactory() {

        @SuppressWarnings("deprecation")
        @Override
        public View makeView() {

            // Create a new TextView
            TextView t = new TextView(getApplicationContext());
            t.setTextAppearance(getApplicationContext(), R.style.campaign);
            return t;
        }
    };

    public void setStyleButton (boolean isSuscribe, Button view) {

        if (isSuscribe) {
            view.setBackgroundResource(R.drawable.button_unsuscribe);
            view.setTextColor(Util.getStatesUnsuscribe(this));
            view.setText(getString(R.string.unsuscribe));
        } else {
            view.setBackgroundResource(R.drawable.button_suscribe);
            view.setTextColor(Util.getStatesSuscribe(this));
            view.setText(getString(R.string.suscribe));
        }

    }

    /**
     * Obtiene la campaña seleccionada desde Internet si se dispone de él o desde DB
     * @param id ID de la campaña que se quiere
     */
    public void getCampaign (int id) {
        if (Util.checkInternetConnection(this)) {
            Virde.getInstance(this).getDataCampaign(id, Util.getPreference(this, getString(R.string.token))); // Campaign from internet
//            Virde.getInstance(this).getContributions(id); // TODO
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
    public void updateActivityInfo (final ItemCampaign item) {

        // Button suscribe / unsuscribe style
        setStyleButton(item.isSuscribe(), suscription);

        // Data campaign
        title.setText(item.getTitle());

        description.setText(formatDescription(item.getDescription()));
        description.setTag(0);
        geoArea.setText(Html.fromHtml(item.getScope()));

        String datesCampaigns = Util.parseDateToString(item.getDateStart()) + " - " + Util.parseDateToString(item.getDateEnd());
        dates.setText(datesCampaigns);

        // Header image
        Picasso.with(this)
                .load(item.getImage())
                .into(header);

        // Update itemCampaign
        if (!fromDB)
            DBVirde.getInstance(this).updateCampaign(item);

        description.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (description.getTag() == 0) { // Expand

                    description.setText(item.getDescription());
                    description.setTag(1);

                } else { // Collapse

                    description.setText(formatDescription(item.getDescription()));
                    description.setTag(0);

                }
            }
        });

    }

    /**
     * Formatea el texto reduciéndolo a 250 caracteres y añadiendo un "Continuar leyendo"
     * @param text Texto a reducir
     * @return String texto reducido + "Continuar leyendo"
     */
    public Spanned formatDescription (String text) {

        String descriptionText;
        if (text.length() > 250)
            descriptionText = text.substring(0, 250) + "... <font color=\"#333333\">" + getString(R.string.continue_reading) + "</font>";
        else
            descriptionText = text;

        return Html.fromHtml(descriptionText);

    }

    public void drawContributions (ArrayList<ItemFormContribution> contribution) {

        // TODO
        for (int i=0; i<5; i++) {
            View v = getLayoutInflater().inflate(R.layout.item_campaign_user, null);

            // Views
            ImageView imgUser = (ImageView)v.findViewById(R.id.imgUser);
            TextView title = (TextView)v.findViewById(R.id.title);
            TextView description = (TextView)v.findViewById(R.id.description);

            title.setText(contribution.get(5).getValue());


            Picasso.with(this)
                    .load(R.drawable.test_logo_si2)
                    .transform(new CircleTransform())
                    .resize(150, 150)
                    .into(imgUser);

            contributions.addView(v);
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void finishActivity () {

        Intent returnIntent = new Intent();

        if (buttonSuscribeTouch) {
            returnIntent.putExtra("isSuscribed", campaign.isSuscribe());
            returnIntent.putExtra("position", position);
            setResult(RESULT_OK, returnIntent);
        } else {
            setResult(RESULT_CANCELED, returnIntent);
        }

        finish();
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
            case Virde.FROM_GET_CONTRIBUTIONS:

                ArrayList<ArrayList<ItemFormContribution>> contributions = result.second;

                for (ArrayList<ItemFormContribution> itemFormContributions : contributions) {



                }


                break;
            case Virde.FROM_SUSCRIBE:
            case Virde.FROM_UNSUSCRIBE:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                    setStyleButton(campaign.isSuscribe(), suscription);
                } else {

                    campaign.setIsSuscribe(!campaign.isSuscribe());

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

    @Override
    public void onBackPressed() {
        finishActivity();
    }

}
