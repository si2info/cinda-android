package info.si2.iista.volunteernetworks;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.graphics.Palette;
import android.support.v7.widget.Toolbar;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.Fragments.MyContributions;
import info.si2.iista.volunteernetworks.Fragments.TrackingContributions;
import info.si2.iista.volunteernetworks.apiclient.ItemProfile;
import info.si2.iista.volunteernetworks.apiclient.ItemUser;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.util.BlurTransformation;
import info.si2.iista.volunteernetworks.util.CircleTransform;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 12/1/16
 * Project: Cinda
 */
public class Profile extends AppCompatActivity implements OnApiClientResult {

    // Views
    private Toolbar toolbar;
    private TabLayout tabLayout;
    private CollapsingToolbarLayout collapsingToolbarLayout;
    private ViewPager viewPager;
    private ImageView headerImage, profileImage;
    private TextView username, contributions, textError;
    private ProgressBar progressBar;

    // User
    private ItemUser user;

    // Tabs
    private String titles[] = new String[2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.perfil);

        // Views
        referenceViews();

        // Intent extras
        getIntentExtras();

        // Toolbar
        setupToolbar(toolbar);

        // User data
        setupUserDeatail();

        // Get contributions
        Virde.getInstance(this).getUserContributions(Util.getToken(this), user.getId());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        return true;
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

    /**
     * Reference all views
     */
    private void referenceViews() {

        toolbar = (Toolbar) findViewById(R.id.toolbar);
        tabLayout = (TabLayout) findViewById(R.id.tab_layout);
        collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);

        viewPager = (ViewPager) findViewById(R.id.viewpager);
        progressBar = (ProgressBar)findViewById(R.id.progressBar);

        // User
        headerImage = (ImageView)findViewById(R.id.background_image);
        profileImage = (ImageView)findViewById(R.id.profile_image);
        username = (TextView)findViewById(R.id.username);
        contributions = (TextView)findViewById(R.id.contributions);

        textError = (TextView)findViewById(R.id.text_error);

        // Activity transition
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            profileImage.setTransitionName("transitionUser");
        }

    }

    /**
     * Init Toolbar
     */
    private void setupToolbar (Toolbar toolbar) {

        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

    }

    private void setupUserDeatail () {

        // Profile image
        if (user.getImage() != null) {
            if (!user.getImage().equals("")) {
                Picasso.with(this)
                        .load(user.getImage())
                        .transform(new CircleTransform())
                        .into(profileImage);
            } else {
                Picasso.with(this)
                        .load(R.drawable.test_logo_si2)
                        .transform(new CircleTransform())
                        .into(profileImage);
            }
        }

        // Header image
        if (user.getImage() != null) {
            if (!user.getImage().equals("")) {
                Picasso.with(this).load(user.getImage())
                        .transform(new BlurTransformation(this, 20))
                        .into(headerImage, new Callback() {
                            @Override
                            public void onSuccess() {

                                Bitmap bitmap = ((BitmapDrawable) headerImage.getDrawable()).getBitmap();

                                Palette.from(bitmap).generate(new Palette.PaletteAsyncListener() {
                                    @SuppressWarnings("ResourceType")
                                    @Override
                                    public void onGenerated(Palette palette) {

                                        Palette.Swatch swatch = palette.getVibrantSwatch();
                                        Palette.Swatch swatchDark = palette.getDarkVibrantSwatch();

                                        int vibrant = 0x000000;
                                        int vibrantDark = 0x000000;
                                        if (swatch != null && swatchDark != null) {
                                            vibrant = swatch.getRgb();
                                            vibrantDark = swatchDark.getRgb();
                                        }
                                        int vibrantColor = palette.getVibrantColor(vibrant);
                                        int vibrantDarkColor = palette.getDarkVibrantColor(vibrantDark);

                                        collapsingToolbarLayout.setContentScrimColor(vibrantColor);
                                        collapsingToolbarLayout.setStatusBarScrimColor(vibrantDarkColor);

                                    }
                                });

                            }

                            @Override
                            public void onError() {

                            }
                        });
            }
        }

        username.setText(user.getUsername());

    }

    /**
     * Init ViewPager and Tabs
     */
    private void setupComponents (ArrayList<ItemProfile> contributions, ArrayList<ItemProfile> tracking) {

        setupViewPager(viewPager, contributions, tracking);

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

                viewPager.setCurrentItem(tab.getPosition());

                switch (tab.getPosition()) {
                    case 0:

                        break;
                    case 1:

                        break;
                    case 2:

                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    /**
     * Init ViewPager
     * @param viewPager View to init
     */
    private void setupViewPager (ViewPager viewPager, ArrayList<ItemProfile> contributions, ArrayList<ItemProfile> tracking) {

        titles[0] = getString(R.string.contributions);
        titles[1] = getString(R.string.trackings);

        ProfilePagerAdapter adapter = new ProfilePagerAdapter(getSupportFragmentManager());

        adapter.addFrag(MyContributions.newInstance(contributions), titles[0]);

        // Only add this tab if is my profile
        int myID = Util.getIntPreference(this, getString(R.string.idUser));
        if (myID == user.getId())
            adapter.addFrag(TrackingContributions.newInstance(tracking), titles[1]);

        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

    }

    private void getIntentExtras() {

        if (getIntent().getExtras() != null) {
            if (getIntent().getExtras().containsKey("user")) {
                user = getIntent().getExtras().getParcelable("user");
            }
        }

    }

    @Override
    public void onApiClientRequestResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case Virde.FROM_GET_USER_CONTRIBUTIONS:
                if (result.first.isError()) {
                    Util.makeToast(this, result.first.getMensaje(), 0);
                    textError.setVisibility(View.VISIBLE);
                } else {

                    int nContributions = result.second.size();
                    ArrayList<ItemProfile> contributionsArray = new ArrayList<>();
                    ArrayList<ItemProfile> tracking = new ArrayList<>();

                    for (Object object : result.second) {
                        ItemProfile item = (ItemProfile)object;
                        if (item.getTracking().equals("0")) { // Contributions
                            contributionsArray.add(item);
                        } else { // Tracking
                            tracking.add(item);
                        }
                    }

                    // nContributions
                    contributions.setText(String.format(getString(R.string.n_contributions), nContributions));

                    // ViewPager
                    setupComponents(contributionsArray, tracking);

                }
                progressBar.setVisibility(View.GONE);
                break;
        }
    }

}
