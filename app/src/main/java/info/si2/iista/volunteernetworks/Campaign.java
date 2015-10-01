package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.AdapterContributions.ClickListener;
import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemFormContribution;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.Util;
import info.si2.iista.volunteernetworks.util.WrappingLinearLayoutManager;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 4/9/15
 * Project: Virde
 */
public class Campaign extends AppCompatActivity implements OnApiClientResult, OnDBApiResult,
        AppBarLayout.OnOffsetChangedListener, SwipeRefreshLayout.OnRefreshListener, ClickListener, TabLayout.OnTabSelectedListener {

    // Data
    private ItemCampaign campaign;
    private int position;

    // View
    private CoordinatorLayout coordinatorLayout;
    private LinearLayout topUsers;
    private NestedScrollView nestedScroll;
    private RelativeLayout content;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private ImageView header;
    private RelativeLayout infoCampaign;
    private TextView title;
    private TextSwitcher description;
    private TextView geoArea;
    private TextView dates;
    private Button suscription;

    // Flag
    private boolean fromDB;
    private boolean buttonSuscribeTouch;
    private boolean isTabsStick;
    private boolean isTopUsersVisible = true;

    // Contributions
    private TabLayout tabLayout;
    private RecyclerView recyclerContributions;
    private AdapterContributions adapter;
    private ArrayList<ItemModelValue> items = new ArrayList<>();

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
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.layout);
        topUsers = (LinearLayout)findViewById(R.id.topUsers);
        nestedScroll = (NestedScrollView)findViewById(R.id.nestedScroll);
        content = (RelativeLayout)findViewById(R.id.content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        header = (ImageView)findViewById(R.id.header);
        title = (TextView)findViewById(R.id.title);
        infoCampaign = (RelativeLayout)findViewById(R.id.infoCampaign);
        description = (TextSwitcher)findViewById(R.id.description);
        geoArea = (TextView)findViewById(R.id.campaign_geo);
        dates = (TextView)findViewById(R.id.campaign_dates);
        suscription = (Button)findViewById(R.id.suscriptionButton);
        recyclerContributions = (RecyclerView)findViewById(R.id.recyclerContributions);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);

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

        // Tabs
        initTabs();

        adapter = new AdapterContributions(this, items);
        adapter.setClickListener(this);
        recyclerContributions.setLayoutManager(new WrappingLinearLayoutManager(this));
        recyclerContributions.setNestedScrollingEnabled(false);
        recyclerContributions.setHasFixedSize(false);
        recyclerContributions.setAdapter(adapter);


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

        nestedScroll.setOnTouchListener(new View.OnTouchListener() {
            private ViewTreeObserver observer;

            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                if (observer == null) {
                    observer = nestedScroll.getViewTreeObserver();
                    observer.addOnScrollChangedListener(onScrollChangedListener);
                } else if (!observer.isAlive()) {
                    observer.removeOnScrollChangedListener(onScrollChangedListener);
                    observer = nestedScroll.getViewTreeObserver();
                    observer.addOnScrollChangedListener(onScrollChangedListener);
                }

                return false;
            }

            });

        // Top users
        initTopUsers();

    }

    public void initTopUsers () {

        int size = topUsers.getChildCount();
        RelativeLayout moreUsers = (RelativeLayout) topUsers.getChildAt(size-1);
        SelectableRoundedImageView view = (SelectableRoundedImageView) moreUsers.getChildAt(0);
        Util.tintDrawable(view.getDrawable(), ContextCompat.getColor(this, R.color.moreUsers));

    }

    /**
     * Listener para comprobar la posición de las Views y hacer que las tabs permanezcan debajo del
     * ActionBar
     */
    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
            ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {

                    int[] tabsLocation = {0,0};
                    int[] recyLocation = {0,0};
                    int[] scrollLocation = {0,0};
                    tabLayout.getLocationOnScreen(tabsLocation);
                    recyclerContributions.getLocationOnScreen(recyLocation);
                    description.getLocationOnScreen(scrollLocation);
                    int posTabs = Util.convertPixelsToDp(Campaign.this, tabsLocation[1]);
                    int posRecy = Util.convertPixelsToDp(Campaign.this, recyLocation[1]);
                    int posScroll = Util.convertPixelsToDp(Campaign.this, scrollLocation[1]);

                    Log.d("TABS Y", String.valueOf(posScroll));

                    if (posTabs <= 85 && !isTabsStick) { // Sticky Tabs

                        coordinatorLayout.post(new Runnable() {
                            @Override
                            public void run() {

                                // New LayoutParams and position
                                CoordinatorLayout.LayoutParams params = new CoordinatorLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.topMargin = Util.convertDpToPixel(Campaign.this, 54);
                                tabLayout.setLayoutParams(params);

                                // Move view
                                content.removeView(tabLayout);
                                coordinatorLayout.addView(tabLayout);

                                // Recycler position
                                RelativeLayout.LayoutParams recyclerParams = (RelativeLayout.LayoutParams) recyclerContributions.getLayoutParams();
                                recyclerParams.addRule(RelativeLayout.BELOW, infoCampaign.getId());
                                recyclerParams.topMargin = Util.convertDpToPixel(Campaign.this, 54);
                                recyclerContributions.setLayoutParams(recyclerParams);

                                isTabsStick = true;

                            }
                        });

                    } else if (posRecy >= 125 && isTabsStick) { // Restore Tabs

                        content.post(new Runnable() {
                            @Override
                            public void run() {

                                // Remove View from parent
                                coordinatorLayout.removeView(tabLayout);

                                // Restore TabLayout
                                tabLayout = new TabLayout(Campaign.this);
                                tabLayout.setId(R.id.tab_layout);
                                initTabs();

                                // Move view
                                content.addView(tabLayout);

                                // New LayoutParams and position
                                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                                params.topMargin = Util.convertDpToPixel(Campaign.this, 16);
                                params.addRule(RelativeLayout.BELOW, infoCampaign.getId());
                                tabLayout.setLayoutParams(params);

                                // Recycler position
                                RelativeLayout.LayoutParams recyclerParams = (RelativeLayout.LayoutParams) recyclerContributions.getLayoutParams();
                                recyclerParams.addRule(RelativeLayout.BELOW, tabLayout.getId());
                                recyclerParams.topMargin = Util.convertDpToPixel(Campaign.this, 0);
                                recyclerContributions.setLayoutParams(recyclerParams);

                                isTabsStick = false;

                            }
                        });

                    } else if (posScroll < 105 && isTopUsersVisible) {

                        coordinatorLayout.post(new Runnable() {
                            @Override
                            public void run() {

                                ScaleAnimation reduce =  new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                reduce.setDuration(300);
                                reduce.setFillAfter(true);

                                for (int i=0; i<topUsers.getChildCount(); i++) {
                                    topUsers.getChildAt(i).startAnimation(reduce);
                                }

                                isTopUsersVisible = false;

                            }
                        });

                    } else if (posScroll > 110 && !isTopUsersVisible) {

                        coordinatorLayout.post(new Runnable() {
                            @Override
                            public void run() {

                                ScaleAnimation grow =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                grow.setDuration(300);
                                grow.setFillAfter(true);

                                for (int i=0; i<topUsers.getChildCount(); i++) {
                                    topUsers.getChildAt(i).startAnimation(grow);
                                }

                                isTopUsersVisible = true;

                            }
                        });

                    }

                }
            };

    /**
     * Inicialización de las tabs de contribuciones
     */
    private void initTabs () {

        tabLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.defaultColor), ContextCompat.getColor(this, R.color.primary));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.myShipments)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.other)));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(this);

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
            Virde.getInstance(this).getContributions(id);
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

        nestedScroll.smoothScrollTo(0, 0);

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

                if (contributions != null) {
                    for (ArrayList<ItemFormContribution> itemFormContribution : contributions) {

                        addToContributions(itemFormContribution);

                    }

                    adapter = new AdapterContributions(Campaign.this, items);
                    recyclerContributions.setAdapter(adapter);
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

    private void addToContributions (ArrayList<ItemFormContribution> itemFormContribution) {

        // Create date
        String createDate = findCreateDate(itemFormContribution);
        if (createDate != null)
            items.add(new ItemModelValue("Title", createDate));

    }

    private String findCreateDate (ArrayList<ItemFormContribution> itemFormContribution) {

        for (ItemFormContribution item : itemFormContribution) {
            if (item.getKey().equals(ItemModel.ITEM_CREATE_DATE))
                return item.getValue();
        }

        return null;

    }

    private ItemFormContribution findFirstLabel (ArrayList<ItemFormContribution> itemFormContribution) {

        for (ItemFormContribution item : itemFormContribution) {
            if (item.getKey().equals(ItemModel.ITEM_EDIT_TEXT))
                return item;
        }

        return null;

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

    @Override
    public void onContributionItemClick(View view, int position) {

    }

    /** TABS **/
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        tab.getPosition();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

}
