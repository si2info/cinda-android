package info.si2.iista.volunteernetworks;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.NestedScrollView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.joooonho.SelectableRoundedImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.AdapterContributions.ClickListener;
import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemContribution;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemUser;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.ScrollAwareFABBehavior;
import info.si2.iista.volunteernetworks.util.SpannableText;
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

    // Views
    private CoordinatorLayout coordinatorLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private LinearLayout topUsers;
    private NestedScrollView nestedScroll;
    private RelativeLayout content;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AppBarLayout appBarLayout;
    private ImageView header, cover;
    private RelativeLayout infoCampaign;
    private TextView title;
    private TextView description;
    private TextView geoArea;
    private TextView dates;
    private Button suscription;
    private ProgressBar contributeProgress;
    private TextView contributeText;
    private FloatingActionButton fabAddContribution;

    // Flag
    private boolean fromDB;
    private boolean buttonSuscribeTouch;
    private boolean isTabsStick;
    private boolean isTopUsersVisible = true;
    private int fromTab = 0;

    // Contributions
    private TabLayout tabLayout;
    private RecyclerView recyclerContributions;
    private AdapterContributions adapter;
    private ArrayList<ItemContribution> myContributions = new ArrayList<>();
    private ArrayList<ItemContribution> otherContributions = new ArrayList<>();

    // Model
    private ArrayList<ItemModel> model;

    // Top users
    private ArrayList<ItemUser> users;

    // Permission Request
    private static final int PERMISSIONS_REQUEST_LOCATION = 1;

    // Data
    private int lineEndIndex = 0;

    // Push
    private boolean fromPush;
    private int idCampaign;

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
        coordinatorLayout = (CoordinatorLayout)findViewById(R.id.layout);
        topUsers = (LinearLayout)findViewById(R.id.topUsers);
        nestedScroll = (NestedScrollView)findViewById(R.id.nestedScroll);
        content = (RelativeLayout)findViewById(R.id.content);
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        appBarLayout = (AppBarLayout) findViewById(R.id.appbar);
        header = (ImageView)findViewById(R.id.header);
        cover = (ImageView)findViewById(R.id.imageCover);
        title = (TextView)findViewById(R.id.title);
        infoCampaign = (RelativeLayout)findViewById(R.id.infoCampaign);
        description = (TextView)findViewById(R.id.description);
        geoArea = (TextView)findViewById(R.id.campaign_geo);
        dates = (TextView)findViewById(R.id.campaign_dates);
        suscription = (Button)findViewById(R.id.suscriptionButton);
        recyclerContributions = (RecyclerView)findViewById(R.id.recyclerContributions);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        contributeProgress = (ProgressBar)findViewById(R.id.progressBarContributions);
        contributeText = (TextView)findViewById(R.id.textContribute);
        fabAddContribution = (FloatingActionButton)findViewById(R.id.FAB_button);

        cover.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent zoom = new Intent(Campaign.this, ZoomImage.class);
                zoom.putExtra("title", campaign.getTitle());
                zoom.putExtra("img", campaign.getImage());

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    ActivityOptionsCompat options = ActivityOptionsCompat.
                            makeSceneTransitionAnimation(Campaign.this, cover, "transitionZoom");
                    ActivityCompat.startActivity(Campaign.this, zoom, options.toBundle());
                } else {
                    startActivity(zoom);
                }

            }
        });

        // Refresh listener
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

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
        initTabs(0);

        adapter = new AdapterContributions(this, myContributions);
        adapter.setClickListener(Campaign.this);
        recyclerContributions.setLayoutManager(new WrappingLinearLayoutManager(this));
        recyclerContributions.setNestedScrollingEnabled(false);
        recyclerContributions.setHasFixedSize(false);
        recyclerContributions.setAdapter(adapter);


        // Obtener datos de campaña con el ID
        if (getIntent().getExtras() != null) {
            if (!getIntent().getExtras().containsKey("fromPush")) {
                campaign = getIntent().getParcelableExtra("campaign");
                position = getIntent().getIntExtra("position", -1);
            } else {
                fromPush = getIntent().getBooleanExtra("fromPush", false);
                idCampaign = getIntent().getIntExtra("idCampaign", -1);
            }
        }

        if (campaign != null) {

            // Title Campaign
            collapsingToolbarLayout.setTitle(campaign.getTitle());
            title.setText(campaign.getTitle());

            // Get campaign
            getCampaign(campaign.getId());

            // Get model campaign
            getModelCampaign(campaign.getId());

            // Top users
            Virde.getInstance(this).getListVolunteers(campaign.getId());

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

        if (fromPush) {
            campaign = new ItemCampaign();
            campaign.setId(idCampaign);

            // Obtener modelo de la campaña
            getModelCampaign(campaign.getId());

            // Top users
            Virde.getInstance(this).getListVolunteers(campaign.getId());

            String token = Util.getToken(this);
            Virde.getInstance(this).getDataCampaign(idCampaign, token);
        }

    }

    /**
     * Inicialización de Top Users
     */
    @SuppressLint("SetTextI18n")
    public void initTopUsers () {

        // Views
        final RelativeLayout moreUsersView = (RelativeLayout) topUsers.getChildAt(3);

        // Tint background
        SelectableRoundedImageView moreUsers = (SelectableRoundedImageView) moreUsersView.getChildAt(0);
        Util.tintDrawable(moreUsers.getDrawable(), ContextCompat.getColor(this, R.color.moreUsers));

        // Animation
        final ScaleAnimation grow =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        grow.setDuration(300);
        grow.setFillAfter(true);

        // OnClick
        topUsers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                seeMoreTopUsers(view);
            }
        });

        for (int i=0; i<topUsers.getChildCount(); i++) {
            if (i == topUsers.getChildCount()-1) { // More than 3 volunteers

                int nVolunteers = users.size() - 3;
                TextView nUsers = (TextView)moreUsersView.getChildAt(1);

                if (nVolunteers > 0) {
                    if (nVolunteers > 99)
                        nUsers.setText("+99");
                    else
                        nUsers.setText("+" + String.valueOf(users.size() - 3));

                    coordinatorLayout.post(new Runnable() {
                        @Override
                        public void run() {
                            moreUsersView.setVisibility(View.VISIBLE);
                            moreUsersView.startAnimation(grow);
                        }
                    });

                } else {
                    moreUsersView.setVisibility(View.INVISIBLE);
                }

            } else if (i < users.size()) { // The 3 first volunteers

                final SelectableRoundedImageView image = (SelectableRoundedImageView) topUsers.getChildAt(i);

                if (!users.get(i).getImage().equals(""))
                    Picasso.with(this)
                            .load(users.get(i).getImage())
                            .into(image, new com.squareup.picasso.Callback() {
                                @Override
                                public void onSuccess() {
                                    coordinatorLayout.post(new Runnable() {
                                        @Override
                                        public void run() {
                                            image.setVisibility(View.VISIBLE);
                                            image.startAnimation(grow);
                                        }
                                    });
                                }

                                @Override
                                public void onError() {

                                }
                            });

            } else { // Less than 3 users
                if (i < topUsers.getChildCount())
                    topUsers.getChildAt(i).setVisibility(View.INVISIBLE);
            }
        }

    }

    public void seeMoreTopUsers (View view) {

        Intent intent = new Intent(this, TopUsers.class);
        intent.putParcelableArrayListExtra("topUsers", users);
        startActivity(intent);

    }

    /**
     * Obtiene el modelo de la campaña por internet si lo hay y si no de base de datos si ya se visualizó
     * @param id ID del modelo de la campaña a obtener
     */
    public void getModelCampaign (int id) {
        if (Util.checkInternetConnection(this)) {
            Virde.getInstance(this).getModelCampaign(id); // Model campaign from internet
        } else {
            DBVirde.getInstance(this).selectModel(id); // Model campaign from DB
        }
    }

    /**
     * Listener para comprobar la posición de las Views y hacer que las tabs permanezcan debajo del
     * ActionBar
     */
    final ViewTreeObserver.OnScrollChangedListener onScrollChangedListener = new
            ViewTreeObserver.OnScrollChangedListener() {

                @Override
                public void onScrollChanged() {

                    final int[] tabsLocation = {0,0};
                    int[] recyLocation = {0,0};
                    int[] scrollLocation = {0,0};
                    tabLayout.getLocationOnScreen(tabsLocation);
                    recyclerContributions.getLocationOnScreen(recyLocation);
                    description.getLocationOnScreen(scrollLocation);
                    int posTabs = Util.convertPixelsToDp(Campaign.this, tabsLocation[1]);
                    int posRecy = Util.convertPixelsToDp(Campaign.this, recyLocation[1]);
                    int posScroll = Util.convertPixelsToDp(Campaign.this, scrollLocation[1]);

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
                                int selectedTab = tabLayout.getSelectedTabPosition();
                                tabLayout = new TabLayout(Campaign.this);
                                tabLayout.setId(R.id.tab_layout);
                                initTabs(selectedTab);

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

                    } else if (posScroll < 201 && isTopUsersVisible) {

                        coordinatorLayout.post(new Runnable() {
                            @Override
                            public void run() {

                                ScaleAnimation reduce =  new ScaleAnimation(1f, 0f, 1f, 0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                reduce.setDuration(300);
                                reduce.setFillAfter(true);

                                for (int i=0; i<topUsers.getChildCount(); i++) {
                                    if (users != null)
                                        if (i < users.size())
                                            topUsers.getChildAt(i).startAnimation(reduce);
                                }

                                isTopUsersVisible = false;

                            }
                        });

                    } else if (posScroll > 206 && !isTopUsersVisible) {

                        coordinatorLayout.post(new Runnable() {
                            @Override
                            public void run() {

                                ScaleAnimation grow =  new ScaleAnimation(0f, 1f, 0f, 1f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
                                grow.setDuration(300);
                                grow.setFillAfter(true);

                                for (int i=0; i<topUsers.getChildCount(); i++) {
                                    if (users != null)
                                        if (i < users.size())
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
    private void initTabs (int selectedTab) {

        tabLayout.setBackgroundColor(ContextCompat.getColor(this, android.R.color.white));
        tabLayout.setTabTextColors(ContextCompat.getColor(this, R.color.defaultColor), ContextCompat.getColor(this, R.color.primary));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.myShipments)));
        tabLayout.addTab(tabLayout.newTab().setText(getString(R.string.other)));
        TabLayout.Tab tab = tabLayout.getTabAt(selectedTab);
        if (tab != null)
            tab.select();
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);
        tabLayout.setOnTabSelectedListener(this);

    }

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
        intent.putExtra("campaignName", campaign.getTitle());
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
        collapsingToolbarLayout.setTitle(item.getTitle());
        title.setText(item.getTitle());

        description.setText(item.getDescription());
        makeTextViewResizable(description, 5, getString(R.string.viewMore), true);
        geoArea.setText(Html.fromHtml(item.getScope()));

        String datesCampaigns = Util.parseDateToString(item.getDateStart()) + " - " + Util.parseDateToString(item.getDateEnd());
        dates.setText(datesCampaigns);

        // Header image
        if (item.getCover() != null) {
            if (!item.getCover().equals("")) {
                Picasso.with(this)
                        .load(item.getCover())
                        .into(header);
            }
        }

        // Cover image
        if (item.getImage() != null) {
            if (!item.getImage().equals("")) {
                Picasso.with(this)
                        .load(item.getImage())
                        .into(cover);
            }
        } else {

            // Hide image
            cover.setVisibility(View.GONE);

            // Layout params
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) description.getLayoutParams();
            params.topMargin = Util.convertDpToPixel(this, 32);
            description.setLayoutParams(params);

        }

        // Update itemCampaign
        if (!fromDB)
            DBVirde.getInstance(this).updateCampaign(item);

        nestedScroll.smoothScrollTo(0, 0);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (campaign.haveTracking())
            getMenuInflater().inflate(R.menu.menu_campaign, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finishActivity();
                return true;

            case R.id.action_tracking:
                checkLocationPermission();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void checkLocationPermission () {

        if (Build.VERSION.SDK_INT >= 23) {

            if (ContextCompat.checkSelfPermission(this,
                    android.Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

                if (shouldShowRequestPermissionRationale(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                    Util.showMessageOKCancel(this, getString(R.string.permission_location),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    ActivityCompat.requestPermissions(Campaign.this,
                                            new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION},
                                            PERMISSIONS_REQUEST_LOCATION);
                                }
                            });
                }

            } else {
                intentToTracking();
            }

        } else {
            intentToTracking();
        }

    }

    public void intentToTracking () {

        Intent intent = new Intent(this, Tracking.class);
        intent.putExtra("idCampaign", campaign.getId());
        intent.putExtra("titleCampaign", campaign.getTitle());
        startActivity(intent);

    }

    public void finishActivity () {

        Intent returnIntent = new Intent();

        if (fromPush) {
            returnIntent.setClass(this, MainActivity.class);
            returnIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(returnIntent);
        } else if (buttonSuscribeTouch) {
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
     * Feedback to user, loading myContributions
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
                    campaign = item;
                    updateActivityInfo(item);

                }
                break;
            case Virde.FROM_GET_CONTRIBUTIONS:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {

                    int myID;
                    if (fromTab == 0) { // My contributions

                        myContributions = new ArrayList<>();

                        for (Object object : result.second) {
                            myContributions.add((ItemContribution)object);
                        }

                        adapter = new AdapterContributions(Campaign.this, myContributions);
                        adapter.setClickListener(Campaign.this);
                        recyclerContributions.setAdapter(adapter);

                        contributeText.setText(getString(R.string.my_contribute));

                    } else { // Other contributions

                        myID = Util.getIntPreference(this, getString(R.string.idUser));
                        otherContributions = new ArrayList<>();

                        for (Object object : result.second) {
                            ItemContribution contribution = (ItemContribution)object;
                            if (contribution.getAuthor_id() != myID)
                                otherContributions.add(contribution);
                        }

                        adapter = new AdapterContributions(Campaign.this, otherContributions);
                        adapter.setClickListener(Campaign.this);
                        recyclerContributions.setAdapter(adapter);

                        contributeText.setText(getString(R.string.other_contribute));

                    }

                }

                showRecyclerContributions();

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
            case Virde.FROM_MODEL_CAMPAIGN:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {

                    model = new ArrayList<>();

                    for (Object object : result.second) {
                        model.add((ItemModel) object);
                    }

                    // Model to DB
                    DBVirde.getInstance(this).insertModel(model);

                    // Get contributions
                    hideRecyclerContributions();
                    Virde.getInstance(this).getContributions(campaign.getId(), Util.getToken(getApplicationContext()));

                }
                break;
            case Virde.FROM_GET_LIST_VOLUNTEERS:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {

                    users = new ArrayList<>();

                    for (Object object : result.second) {
                        users.add((ItemUser)object);
                    }

                    initTopUsers();

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
            case DBVirde.FROM_SELECT_MODEL:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Model not selected");
                } else {

                    // Model
                    model = new ArrayList<>();

                    for (Object object : result.second) {
                        model.add((ItemModel) object);
                    }

                    // Get contributions
                    hideRecyclerContributions();
                    Virde.getInstance(this).getContributions(campaign.getId(), Util.getToken(getApplicationContext()));

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

    /** Permission Request **/
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        switch (requestCode) {
            case PERMISSIONS_REQUEST_LOCATION:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission granted
                    intentToTracking();
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
        Util.restoreModelPreferences(this);
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
        Intent intent = new Intent(this, Contribution.class);
        intent.putExtra("idCampaign", campaign.getId());
        intent.putExtra("detail", true);
        if (fromTab == 0)
            intent.putExtra("idContribution", myContributions.get(position).getId());
        else
            intent.putExtra("idContribution", otherContributions.get(position).getId());
        startActivity(intent);
    }

    /** TABS **/
    @Override
    public void onTabSelected(TabLayout.Tab tab) {

        hideRecyclerContributions();

        if (tab.getPosition() == 0) {
            fromTab = 0;
            if (myContributions.size() == 0) {
                Virde.getInstance(this).getContributions(campaign.getId(), Util.getToken(getApplicationContext()));
            } else  {
                adapter = new AdapterContributions(Campaign.this, myContributions);
                adapter.setClickListener(Campaign.this);
                recyclerContributions.setAdapter(adapter);
                showRecyclerContributions();
            }
        } else {
            fromTab = 1;
            if (otherContributions.size() == 0) {
                Virde.getInstance(this).getContributions(campaign.getId(), "");
            } else {
                adapter = new AdapterContributions(Campaign.this, otherContributions);
                adapter.setClickListener(Campaign.this);
                recyclerContributions.setAdapter(adapter);
                showRecyclerContributions();
            }
        }

        // FAB - Show button "Add contribution"
        CoordinatorLayout.LayoutParams p = (CoordinatorLayout.LayoutParams) fabAddContribution.getLayoutParams();
        ScrollAwareFABBehavior scrollAwareFABBehavior = (ScrollAwareFABBehavior) p.getBehavior();
        if (!scrollAwareFABBehavior.getIsShowing())
            scrollAwareFABBehavior.animateIn(fabAddContribution);

    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }

    public void showRecyclerContributions () {

        recyclerContributions.setVisibility(View.VISIBLE);

        if (fromTab == 0) {
            if (myContributions.size() == 0) {
                contributeText.setVisibility(View.VISIBLE);
            } else {
                contributeText.setVisibility(View.GONE);
            }
        } else {
            if (otherContributions.size() == 0) {
                contributeText.setVisibility(View.VISIBLE);
            } else {
                contributeText.setVisibility(View.GONE);
            }
        }
        contributeProgress.setVisibility(View.GONE);

    }

    public void hideRecyclerContributions () {

        recyclerContributions.setVisibility(View.GONE);
        contributeText.setVisibility(View.GONE);
        contributeProgress.setVisibility(View.VISIBLE);

    }

    public void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {

        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }

        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {

            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + "... " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    if (lineEndIndex == 0)
                        lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + "... " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });

    }

    private SpannableStringBuilder addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv,
                                                                            final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new SpannableText(true){
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, getString(R.string.viewLess), false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 5, getString(R.string.viewMore), true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }

}
