package info.si2.iista.volunteernetworks;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemCampaign;
import info.si2.iista.volunteernetworks.apiclient.ItemServer;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.Util;

public class MainActivity extends AppCompatActivity implements AdapterHome.ClickListener, OnApiClientResult,
        OnDBApiResult, SwipeRefreshLayout.OnRefreshListener {

    // Views
    private RecyclerView recyclerView;
    private TextView serverUrl;
    private TextView serverDesc;
    private RelativeLayout permissionLayout;

    // OnActivityResult
    private static final int FROM_SEE_CAMPAIGN = 1;

    // RecyclerView
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private AdapterHome adapter;
    private ArrayList<ItemCampaign> items = new ArrayList<>();

    // Flags
    private boolean userCanOperate;

    // Animate server info
    private RelativeLayout serverInfo;
    private static final Interpolator INTERPOLATOR = new FastOutSlowInInterpolator();
    private int sizeInfoServer = 60;
    private int sizeDescServer = -40;
    private int sizeInfoDescServer = 300;
    private long duration = 400L;
    private boolean isAnimatingInfoOut;   // Info
    private boolean isAnimatingInfoIn;    // Info
    private boolean isInfoShowing = true; // Info
    private boolean isAnimatingDescOut;   // Description
    private boolean isAnimatingDescIn;    // Description
    private boolean isDescShowing;        // Description

    // Request
    private static final int PERMISSIONS_REQUEST_GET_ACCOUNTS = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        toolbar.setLogo(R.drawable.app_logo);
        setSupportActionBar(toolbar);

        // Views
        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh_layout);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        serverUrl = (TextView)findViewById(R.id.serverUrl);
        serverDesc = (TextView)findViewById(R.id.serverDesc);
        serverInfo = (RelativeLayout)findViewById(R.id.serverInfo);
        permissionLayout = (RelativeLayout)findViewById(R.id.permissionLayout);

        // RecyclerView
        recyclerView.setHasFixedSize(true);

        adapter = new AdapterHome(this, items);
        adapter.setClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // Contacts permission
        if (Build.VERSION.SDK_INT >= 23) {
            checkContactPermission();
        } else {
            initApplication();
        }

    }

    private void initApplication () {

        // Hide permission layout
        permissionLayout.setVisibility(View.GONE);

        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                if (dy > 3 && !isAnimatingInfoOut && isInfoShowing) {
                    if (isDescShowing) { // Descripción visible, ocultar el bloque completo
                        animateInfoOut(serverInfo, Util.convertPixelsToDp(getApplicationContext(), sizeInfoDescServer));
                    } else
                        animateInfoOut(serverInfo, sizeInfoServer);
                } else if (dy < -3 && !isAnimatingInfoIn && !isInfoShowing) {
                    animateInfoIn(serverInfo);
                }

            }
        });

        // Refresh listener
        mSwipeRefreshLayout.setColorSchemeResources(R.color.primary, R.color.primary_dark);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        // Default server
        if (!Util.getBoolPreference(this, getString(R.string.isDefaultServer))) {

            Virde.getInstance(this).getServerInfo(getString(R.string.defaultServer));

        } else {

            // Get active server
            DBVirde.getInstance(this).selectActiveServer();

        }

    }

    /**
     * Check if user grant GET_ACCOUNTS permission
     */
    @TargetApi(Build.VERSION_CODES.M)
    private void checkContactPermission () {

        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.GET_ACCOUNTS)
                != PackageManager.PERMISSION_GRANTED) {

            if (shouldShowRequestPermissionRationale(android.Manifest.permission.GET_ACCOUNTS)) {
                Util.showMessageOKCancel(this, getString(R.string.permission_contacts_explanation),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ActivityCompat.requestPermissions(MainActivity.this,
                                        new String[]{android.Manifest.permission.GET_ACCOUNTS},
                                        PERMISSIONS_REQUEST_GET_ACCOUNTS);
                            }
                        });
            }

        } else {
            initApplication();
        }

    }

    public void activatePermissionContact (View view) {
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{android.Manifest.permission.GET_ACCOUNTS},
                PERMISSIONS_REQUEST_GET_ACCOUNTS);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        Intent intent;

        switch (id) {
            case R.id.action_sync:
                intent = new Intent(this, SyncUserData.class);
                startActivity(intent);
                return true;

            case R.id.action_servers:
                intent = new Intent(this, Servers.class);
                startActivity(intent);
                return true;

            case R.id.action_about:
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void suscription (int idCampaign, boolean suscribe) {

        if (userCanOperate) {
            String token = Util.getPreference(this, getString(R.string.token));
            Virde.getInstance(this).suscription(idCampaign, token, suscribe);
        } else {
            Toast.makeText(this, getString(R.string.userCantOperate), Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public void onHomeItemClick(View view, int position) {

        // Intent to campaign
        Intent intent = new Intent(this, Campaign.class);
        intent.putExtra("campaign", items.get(position));
        intent.putExtra("position", position);
        startActivityForResult(intent, FROM_SEE_CAMPAIGN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == FROM_SEE_CAMPAIGN && resultCode == RESULT_OK) { // El usuario se suscribrió a la campaña desde el detalle de la misma

            boolean isSuscribed = data.getBooleanExtra("isSuscribed", false);
            int position = data.getIntExtra("position", -1);

            if (position != -1) {
                items.get(position).setIsSuscribe(isSuscribed);
                adapter.notifyItemChanged(position);
            }

        }

    }

    @Override
    public void onRefresh() {

        mSwipeRefreshLayout.setEnabled(false);
        Virde.getInstance(this).getListCampaigns(Util.getPreference(this, getString(R.string.token)));

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

    /** API CLIENT **/
    @Override
    public void onApiClientRequestResult(Pair<Result, ArrayList> result) {

        switch (result.first.getResultFrom()) {
            case Virde.FROM_GET_SERVER_INFO:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                } else {

                    // Add server to DB
                    ItemServer item = (ItemServer) result.second.get(0);
                    item.setActive(true);
                    DBVirde.getInstance(this).insertServer(item);

                }
                break;
            case Virde.FROM_USER_REGISTER:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                } else {
                    userCanOperate = true;
                    String token = (String) result.second.get(0);
                    Util.savePreference(this, getString(R.string.token), token);
                }
                break;
            case Virde.FROM_LIST_CAMPAIGNS:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                    mSwipeRefreshLayout.setRefreshing(false);
                    mSwipeRefreshLayout.setEnabled(true);
                } else {

                    ArrayList<ItemCampaign> itemsResult = new ArrayList<>();

                    int idServer = Util.getIntPreference(MainActivity.this, getString(R.string.id_server));

                    int i = 0;
                    for (Object item : result.second) {
                        ItemCampaign camp = (ItemCampaign)item;
                        camp.setIdServer(idServer);
                        itemsResult.add(camp);

                        if (i<items.size()) {
                            items.get(i).setTopUsers(camp.getTopUsers());
                            adapter.notifyItemChanged(i);
                        }

                        i++;

                    }

//                    adapter.notifyDataSetChanged();

                    DBVirde.getInstance(this).addCampaigns(itemsResult);

                }
                break;
            case Virde.FROM_SUSCRIBE:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {
                    int idCampaign = (Integer)result.second.get(1);
                    int position = -1;

                    for (int i=0; i<items.size(); i++) {
                        if (items.get(i).getId() == idCampaign) {
                            position = i;
                            items.get(i).setIsSuscribe(true);
                            break;
                        }
                    }

                    adapter.notifyItemChanged(position);
                }
                break;
            case Virde.FROM_UNSUSCRIBE:
                if (result.first.isError()) {
                    Toast.makeText(getApplicationContext(), result.first.getMensaje(), Toast.LENGTH_SHORT).show();
                } else {
                    int idCampaign = (Integer) result.second.get(1);
                    int position = -1;

                    for (int i = 0; i < items.size(); i++) {
                        if (items.get(i).getId() == idCampaign) {
                            position = i;
                            items.get(i).setIsSuscribe(false);
                            break;
                        }
                    }

                    adapter.notifyItemChanged(position);
                }
                break;
        }

    }

    /** DB API **/
    @Override
    public void onDBApiInsertResult(Result result) {

        switch (result.getResultFrom()) {
            case DBVirde.FROM_INSERT_CAMPAIGNS:
                if (result.isError()) {
                    Log.e("DBVirde", "Campaigns not inserted");
                } else {

                    if (items.size() > 0) { // New campaigns
                        int idCampaign = items.get(0).getId();
                        DBVirde.getInstance(this).getCampaignsFrom(idCampaign);
                    } else { // All campaigns
                        int idServer = Util.getIntPreference(MainActivity.this, getString(R.string.id_server));
                        DBVirde.getInstance(this).getCampaigns(idServer);
                    }

                }
                break;
            case DBVirde.FROM_INSERT_SERVER: // Preferences - Default Server
                if (result.isError()) {
                    Log.e("DBVirde", "Server not inserted");
                } else {
                    if (!Util.getBoolPreference(MainActivity.this, getString(R.string.isDefaultServer))) {

                        // Get active server
                        DBVirde.getInstance(this).selectActiveServer();

                    }
                }
                break;
        }

    }

    @Override
    public void onDBApiSelectResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case DBVirde.FROM_SELECT_ACTIVE_SERVER:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Server no selected from DB");
                } else {
                    if (result.second.size() > 0) {

                        // Server
                        ItemServer server = (ItemServer) result.second.get(0);

                        // Set server info
                        serverUrl.setText(server.getUrl());

                        if (server.getDescription().length() != 0)
                            serverDesc.setText(server.getDescription());
                        else
                            serverDesc.setText(getString(R.string.serverNoDesc));

                        // Shared Preferences
                        Util.initServerSharedPreferences(MainActivity.this, server);

                        // Registro de usuario
                        userCanOperate = false;
                        signUpUser();

                        // Get campaigns and feedback to user
                        doRefresh();
                        int idServer = Util.getIntPreference(MainActivity.this, getString(R.string.id_server));
                        DBVirde.getInstance(this).getCampaigns(idServer);

                    }
                }
                break;
            case DBVirde.FROM_SELECT_CAMPAIGNS:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Campaigns not selected");
                } else {

                    if (items == null)
                        items = new ArrayList<>();

                    for (Object item : result.second) {
                        addItemAndNotify((ItemCampaign) item);
                    }

                    if (items.size() == 0) {
                        doRefresh();
                        Virde.getInstance(this).getListCampaigns(Util.getPreference(this, getString(R.string.token)));
                    } else {
                        mSwipeRefreshLayout.setRefreshing(false);
                        mSwipeRefreshLayout.setEnabled(true);
                    }

                }
                break;

            case DBVirde.FROM_SELECT_CAMPAIGNS_FROM_ID:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Campaigns not selected");
                } else {

                    if (items == null)
                        items = new ArrayList<>();

                    for (Object item : result.second) {
                        addItemAndNotify((ItemCampaign) item);
                    }

                }
                mSwipeRefreshLayout.setRefreshing(false);
                mSwipeRefreshLayout.setEnabled(true);
                break;
        }
    }

    @Override
    public void onDBApiUpdateResult(Result result) {
    }

    /** Request permissions **/

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSIONS_REQUEST_GET_ACCOUNTS: {

                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) { // Permission granted
                    initApplication();
                } else {
                    permissionLayout.setVisibility(View.VISIBLE);
                }

                break;
            }
        }
    }

    /**
     * Añadir items a RecyclerView y notificar al adaptador
     * @param item ItemCampaign a añadir
     */
    public void addItemAndNotify (ItemCampaign item) {
        items.add(0, item);
        adapter.notifyItemInserted(0);
    }

    /** Bottom Bar **/
    public void showInfoServer (View view) {

        if (!isAnimatingDescOut && isDescShowing) {
            animateDescOut(view);
        } else if (!isAnimatingDescIn && !isDescShowing) {
            animateDescIn(view);
        }

    }

    /** Bottom Bar Animations **/
    public void animateInfoOut(View view, float size) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, Util.convertDpToPixel(getApplicationContext(), size));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingInfoOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingInfoOut = false;
                isInfoShowing = false;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = Util.convertDpToPixel(getApplicationContext(), -100);
                serverInfo.setLayoutParams(params);

                isDescShowing = false;

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    public void animateInfoIn(View view) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, -Util.convertDpToPixel(getApplicationContext(), sizeInfoServer));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingInfoIn = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingInfoIn = false;
                isInfoShowing = true;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = Util.convertDpToPixel(getApplicationContext(), -40);
                serverInfo.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    public void animateDescOut(final View view) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, -Util.convertDpToPixel(getApplicationContext(), sizeDescServer));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingDescOut = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingDescOut = false;
                isDescShowing = false;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                params.bottomMargin = Util.convertDpToPixel(getApplicationContext(), -40);
                serverInfo.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    public void animateDescIn(View view) {

        TranslateAnimation anim = new TranslateAnimation(0, 0, Animation.RELATIVE_TO_SELF, Util.convertDpToPixel(getApplicationContext(), sizeDescServer));

        anim.setDuration(duration);
        anim.setFillEnabled(false);
        anim.setInterpolator(INTERPOLATOR);

        anim.setAnimationListener(new Animation.AnimationListener() {
            public void onAnimationStart(Animation animation) {
                isAnimatingDescIn = true;
            }

            public void onAnimationEnd(Animation animation) {
                isAnimatingDescIn = false;
                isDescShowing = true;

                // Clear animation to prevent flicker
                serverInfo.clearAnimation();

                // Set new params
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
                serverInfo.setLayoutParams(params);

            }

            @Override
            public void onAnimationRepeat(final Animation animation) {
            }
        });

        view.startAnimation(anim);

    }

    /**
     * Registro de usuario
     */
    public void signUpUser () {

        // Data
        String mail, name = "", deviceID;

        // Device ID
        deviceID = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // User data
        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userPreferences), Context.MODE_PRIVATE);
        mail = sharedPref.getString(getString(R.string.mail), null);

        if (mail == null) { // Primer registro

            // Google accounts
            AccountManager accountManager = AccountManager.get(this);
            Account[] accounts = accountManager.getAccountsByType("com.google");

            Account account;
            if (accounts.length > 0)
                account = accounts[0];
            else
                account = null;

            if (account != null) {
                mail = account.name;
                String[] splitMail = account.name.split("@");
                name = splitMail[0];
            }

            if (mail != null && name != null && deviceID != null) {
                if (!mail.equals("") && !name.equals("") && !deviceID.equals("")) {
                    userDataToPreferences(name, mail, deviceID);
                    Virde.getInstance(this).userRegister(name, mail, deviceID);
                }
            }

        } else {

            // User data from SharedPreferences
            name = Util.getPreference(this, getString(R.string.username));
            mail = Util.getPreference(this, getString(R.string.mail));
            deviceID = Util.getPreference(this, getString(R.string.device_id));

            // Get token
            Virde.getInstance(this).userRegister(name, mail, deviceID);

        }

    }

    /**
     * Guardado de datos de usuario a SharedPreferences
     * @param username Nombre de usuario
     * @param mail Mail de usuario
     * @param deviceID ID del dispositivo en uso
     */
    public void userDataToPreferences (String username, String mail, String deviceID) {

        Util.savePreference(this, getString(R.string.username), username);
        Util.savePreference(this, getString(R.string.mail), mail);
        Util.savePreference(this, getString(R.string.device_id), deviceID);

    }

    @Override
    protected void onResume() {
        super.onResume();
        Util.restoreModelPreferences(this);
    }
}
