package info.si2.iista.volunteernetworks;

import android.os.Bundle;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Item;
import info.si2.iista.volunteernetworks.apiclient.ItemFormContribution;
import info.si2.iista.volunteernetworks.apiclient.ItemGpx;
import info.si2.iista.volunteernetworks.apiclient.ItemModel;
import info.si2.iista.volunteernetworks.apiclient.ItemModelValue;
import info.si2.iista.volunteernetworks.apiclient.ItemSync;
import info.si2.iista.volunteernetworks.apiclient.OnApiClientResult;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.apiclient.Virde;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class SyncUserData extends AppCompatActivity implements AdapterSyncUserData.ClickListener, OnDBApiResult, OnApiClientResult{

    // RecyclerView
    private RecyclerView recyclerView;
    private AdapterSyncUserData adapter;
    private ArrayList<ItemSync> items;

    // Contributions
    private ArrayList<ItemModelValue> modelValues;
    private int position = 0; // Position contribution send

    private ArrayList<ArrayList<ItemModelValue>> contributions = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        // Action Bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views
        CoordinatorLayout layout = (CoordinatorLayout)findViewById(R.id.layout);
        recyclerView = (RecyclerView)findViewById(R.id.recyclerView);
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.myFAB);

        // Remove Fab button
        layout.removeView(fab);

        // RecyclerView
        items = new ArrayList<>();
        recyclerView.setHasFixedSize(true);
        adapter = new AdapterSyncUserData(getApplicationContext(), items);
        adapter.setClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        // DB
        DBVirde.getInstance(this).selectModelValue(-1);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_campaign_sync, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.action_sync:
                if (modelValues.size() > 0)
                    syncContributions();
                if (items.size() > 0)
                    syncTrackings();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Sincronización de campañas no enviadas con servidor
     */
    public void syncContributions () {

        ((AdapterSyncUserData)recyclerView.getAdapter()).setSynchronizing(true);

        ArrayList<ItemFormContribution> formItems = new ArrayList<>();
        int idContribution = modelValues.get(0).getId();
        formItems.add(new ItemFormContribution("idCampaign", String.valueOf(modelValues.get(0).getIdModel())));
        formItems.add(new ItemFormContribution("token", Util.getPreference(this, getString(R.string.token))));
        for (int i=0; i<modelValues.size(); i++) {

            ItemFormContribution itemForm = new ItemFormContribution();
            ItemModelValue itemModel = modelValues.get(i);
            int thisIdContribution = itemModel.getId();

            if (!itemModel.isSync()) {
                if (idContribution == thisIdContribution) {

                    itemForm.setKey(itemModel.getField());
                    itemForm.setValue(itemModel.getValue());

                    if (itemModel.getFieldType().equals(ItemModel.ITEM_IMAGE)) {
                        if (itemForm.getValue().equals(""))
                            itemForm.setWithImage(false);
                        else
                            itemForm.setWithImage(true);
                    } else {
                        itemForm.setWithImage(false);
                    }

                    formItems.add(itemForm);

                    if (i + 1 < modelValues.size()) {
                        if (modelValues.get(i).getId() != modelValues.get(i + 1).getId()) { // Cambio de item

                            formItems.add(new ItemFormContribution("myPosContributionNotSend", String.valueOf(position)));
                            position++;
                            Virde.getInstance(this).sendContribution(formItems);

                            idContribution = modelValues.get(i + 1).getId();
                            formItems = new ArrayList<>();
                            formItems.add(new ItemFormContribution("idCampaign", String.valueOf(modelValues.get(i + 1).getIdModel())));
                            formItems.add(new ItemFormContribution("token", Util.getPreference(this, getString(R.string.token))));
                        }
                    } else if (i == modelValues.size() - 1) { // Último ítem
                        formItems.add(new ItemFormContribution("myPosContributionNotSend", String.valueOf(position)));
                        Virde.getInstance(this).sendContribution(formItems);
                    }

                }
            } else {
                if (i + 1 < modelValues.size()) {
                    if (modelValues.get(i).getId() != modelValues.get(i + 1).getId()) { // Cambio de item
                        idContribution = modelValues.get(i + 1).getId();
                        position++;
                    }
                }
            }
        }
        position = 0;
    }

    /**
     * Sincronización de Trackings no enviados con servidor
     */
    public void syncTrackings () {

        for (ItemSync item : items) {
            if (item.getIdGpx() != null) {
                if (!item.getIdGpx().equals("0") && !item.isSync()) {
                    int idServer = Util.getIntPreference(this, getString(R.string.id_server));
                    String idTracking = item.getIdGpx();
                    DBVirde.getInstance(this).selectGpx(idServer, idTracking);
                }
            }
        }

    }

    @Override
    public void onHomeItemClick(View view, int position) {

    }

    /** Data Base **/

    @Override
    public void onDBApiInsertResult(Result result) {

    }

    @Override
    public void onDBApiSelectResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case DBVirde.FROM_SELECT_MODELITEM:
                if (result.first.isError()) {
                    Log.e("DBCinda", "Can't select model items");
                } else {

                    modelValues = new ArrayList<>();

                    for (Object object : result.second) {
                        modelValues.add((ItemModelValue) object);
                    }

                    if (modelValues.size() > 0) {
                        int idContribution = modelValues.get(0).getId();
                        ItemSync itemSync = new ItemSync();
                        ArrayList<ItemModelValue> values = new ArrayList<>();
                        for (int i=0; i<modelValues.size(); i++) {

                            ItemModelValue value = modelValues.get(i);
                            int thisIdContribution = value.getId();

                            if (idContribution == thisIdContribution) { // Set data
                                itemSync.setType(Item.SYNC);
                                itemSync.setIsSync(value.isSync());
                                itemSync.setId(value.getIdModel());
                                setValue(itemSync, value);
                                values.add(value);

                                if (i+1 < modelValues.size()) {
                                    if (modelValues.get(i).getId() != modelValues.get(i+1).getId()) { // Cambio de item
                                        items.add(itemSync);
                                        contributions.add(values);
                                        itemSync = new ItemSync();
                                        values = new ArrayList<>();
                                        idContribution = modelValues.get(i+1).getId();
                                        adapter.notifyItemInserted(items.size()-1);
                                    }
                                } else if (i == modelValues.size()-1) { // Último ítem
                                    items.add(itemSync);
                                    contributions.add(values);
                                    adapter.notifyItemInserted(items.size() - 1);
                                }

                            }

                        }
                    }

                    DBVirde.getInstance(this).selectGpxsToSync();

                }
                break;

            case DBVirde.FROM_SELECT_GPX_TO_SYNC:
                if (result.first.isError()) {
                    Log.e("DBCinda", "Can't select model items");
                } else {

                    for (Object object : result.second) {
                        ItemSync item  = (ItemSync)object;
                        item.setTitle(getString(R.string.tracking) + " - " + item.getTitle());
                        items.add(item);
                        adapter.notifyItemInserted(items.size()-1);
                    }

                }
                break;

            case DBVirde.FROM_SELECT_GPX:
                if (result.first.isError()) {
                    Log.e("DBCinda", "Can't select model item");
                } else {
                    if (result.second.size() > 0) {
                        ItemGpx itemGpx = (ItemGpx) result.second.get(0);
                        itemGpx.setIdVolunteer(Util.getIntPreference(this, getString(R.string.idUser)));
                        Virde.getInstance(this).sendGpxContribution(itemGpx);
                    }
                }
                break;
        }
    }

    @Override
    public void onDBApiUpdateResult(Result result) {
        switch (result.getResultFrom()) {
            case DBVirde.FROM_UPDATE_MODELITEM:
                if (result.isError()) {
                    Log.e("DBCinda", "Contribution not update");
                }
                break;
            case DBVirde.FROM_UPDATE_SYNC_GPX:
                if (result.isError()) {
                    Log.e("DBCinda", "Contribution GPX not update");
                }
                break;
        }
    }

    /** Util **/
    public void setValue (ItemSync itemSync, ItemModelValue value) {

        switch (value.getFieldType()) {
            case ItemModel.ITEM_CAMPAIGN_NAME:
                itemSync.setTitle(value.getValue());
                break;
            case ItemModel.ITEM_IMAGE:
                itemSync.setImgCampaign(value.getValue());
                break;
            case ItemModel.ITEM_DATE:
                Log.d("DATE", value.getValue());
                break;
            case ItemModel.ITEM_DATE_SEND:
                itemSync.setDate(value.getValue());
                break;
            case ItemModel.ITEM_URL_SERVER:
                itemSync.setUrl(value.getValue());
                break;
        }

    }

    /** API Client **/

    @Override
    public void onApiClientRequestResult(Pair<Result, ArrayList> result) {

        ItemSync item;
        int position = -1;

        switch (result.first.getResultFrom()) {
            case Virde.FROM_SEND_CONTRIBUTION:

                // Position item sent
                position = result.first.getCodigoError();

                item = items.get(position);
                item.setIsSynchronizing(false);

                if (result.first.isError()) {
                    Log.e("Cinda", "Contribution not send");
                    item.setIsSync(false);
                } else {
                    item.setIsSync(true);

                    for (ItemModelValue value : contributions.get(position)) {
                        value.setIsSync(true);
                    }

                    DBVirde.getInstance(SyncUserData.this).updateModelValue(contributions.get(position));

                }
                adapter.notifyItemChanged(position);
                break;

            case Virde.FROM_SEND_GPX_CONTRIBUTION:

                // Id item sent
                String idGpx = (String) result.second.get(0);

                // Search item position
                for (int i=0; i<items.size(); i++) {
                    String id = items.get(i).getIdGpx();
                    if (idGpx.equals(id)){
                        position = i;
                        break;
                    }
                }

                // State item
                item = items.get(position);
                item.setIsSynchronizing(false);

                if (result.first.isError()) {
                    Log.e("Cinda", result.first.getMensaje());
                    item.setIsSync(false);
                } else {
                    if (position != -1) {

                        // Feedback to user
                        item = items.get(position);
                        item.setIsSynchronizing(false);
                        item.setIsSync(true);

                        // Sync contribution in local db
                        ItemGpx itemGpx = new ItemGpx();
                        itemGpx.setId(idGpx);
                        itemGpx.setSync(true);
                        DBVirde.getInstance(this).updateSyncGpx(itemGpx);

                    }
                }
                adapter.notifyItemChanged(position);
                break;
        }
    }

}
