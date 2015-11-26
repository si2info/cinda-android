package info.si2.iista.volunteernetworks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
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
import info.si2.iista.volunteernetworks.apiclient.ItemServer;
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
public class Servers extends AppCompatActivity implements DialogFragmentAddServer.DialogFragmentAddServerListener, AdapterServers.DeleteServerDialogFragment.DeleteServerDialogListener,
        OnDBApiResult, OnApiClientResult {

    // RecyclerView
    private RecyclerView recyclerView;
    private ArrayList<ItemServer> items;
    private AdapterServers adapter;

    // Dialog
    private ProgressDialogFragment dialog;

    // Server
    private String oldUrlServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_view);

        // Action Bar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        items = new ArrayList<>();

        // RecyclerView
        recyclerView.setHasFixedSize(true);
        adapter = new AdapterServers(this, items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

        DBVirde.getInstance(this).selectServers();

    }

    public void addItem (View view) {

        DialogFragment dialog = new DialogFragmentAddServer();
        dialog.show(getFragmentManager(), "AddServerDialogFragment");

    }

    public void removeItem () {

        int position = items.size()-1;
        items.remove(position);
        adapter.notifyItemRemoved(position);

    }

    public void changeServer (int position) {

        ItemServer item = items.get(position);
        item.setActive(true);
        DBVirde.getInstance(this).updateServer(item);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
//        getMenuInflater().inflate(R.menu.menu_campaign_sync, menu);
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

    /** Dialog delete server **/
    @Override
    public void onDialogPositiveClick(int position) {

        // DB
        int idServer = items.get(position).getId();
        DBVirde.getInstance(Servers.this).deleteServer(idServer);

        // RecyclerView
        items.remove(position);
        adapter.notifyItemRemoved(position);

    }

    /** Dialog add server **/
    @Override
    public void onDialogPositiveClick(String name) {

        // Dialog
        dialog = new ProgressDialogFragment();
        dialog.show(getFragmentManager(), getString(R.string.checkingServer));

        // RecyclerView
        items.add(new ItemServer(-1, Item.SERVER, name, "", true));
        adapter.notifyItemInserted(items.size()-1);

        // Save old server. If new server isn't correct, set old server.
        oldUrlServer = Util.getPreference(this, getString(R.string.server));

        // Set main server in app
        setMainServer(name);

        // Test if its a correct server
        checkServer();

    }

    /**
     * Set the main server in app
     * @param newServer New url server
     */
    private void setMainServer (String newServer) {

        SharedPreferences sharedPref = getSharedPreferences(getString(R.string.userPreferences), Context.MODE_PRIVATE);

        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString(getString(R.string.server), newServer);
        editor.apply();

        Virde.getInstance(this).setActiveServer(newServer);

    }

    /**
     * Check if server is valid
     */
    public void checkServer () {

        // Data
        String mail, name, deviceID;

        // User data from SharedPreferences
        name = Util.getPreference(this, getString(R.string.username));
        mail = Util.getPreference(this, getString(R.string.mail));
        deviceID = Util.getPreference(this, getString(R.string.device_id));

        // Get token
        Virde.getInstance(this).userRegister(name, mail, deviceID);

    }

    /** Data Base **/

    @Override
    public void onDBApiInsertResult(Result result) {
        switch (result.getResultFrom()) {
            case DBVirde.FROM_INSERT_SERVER:
                if (result.isError()) {
                    Log.e("DBVirde", "Server not inserted");
                } else {
                    items.get(items.size()-1).setId(result.getCodigoError());
                    Util.saveIntPreference(Servers.this, getString(R.string.id_server), result.getCodigoError());

                    adapter.notifyDataSetChanged();

                    // Reset app with new server
                    Intent intent = new Intent(Servers.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
                break;
        }
    }

    @Override
    public void onDBApiSelectResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case DBVirde.FROM_SELECT_SERVERS:
                if (result.first.isError()) {
                    Log.e("DBVirde", "Server not selected");
                } else {

                    for (Object object : result.second) {
                        items.add((ItemServer)object);
                        adapter.notifyItemInserted(items.size()-1);
                    }

                }
                break;
        }
    }

    @Override
    public void onDBApiUpdateResult(Result result) {
        switch (result.getResultFrom()) {
            case DBVirde.FROM_DELETE_SERVER:
                if (result.isError()) {
                    Log.e("DBVirde", "Server not deleted");
                }
                break;
            case DBVirde.FROM_UPDATE_SERVER:
                if (result.isError()) {
                    Log.e("DBVirde", "Server not deleted");
                } else {

                    Util.saveIntPreference(Servers.this, getString(R.string.id_server), result.getCodigoError());

                    // Reset app with new server
                    Intent intent = new Intent(Servers.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
                break;
        }
    }

    /** Api Client **/

    @Override
    public void onApiClientRequestResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) { // TODO relizar comprobación con petición de datos del servidor
            case Virde.FROM_USER_REGISTER:
                if (result.first.isError()) { // Invalid server
                    removeItem();
                    setMainServer(oldUrlServer);
                    Virde.getInstance(this).setActiveServer(oldUrlServer);
                    Log.e("Virde", "Incorrect Server");
                    Util.makeToast(getApplicationContext(), getString(R.string.invalidUrl), 0);
                } else {

                    // DB
                    DBVirde.getInstance(this).insertServer(items.get(items.size()-1));

                }
                dialog.dismiss();
                break;
        }
    }

    /** ProgressDialog **/

    public static class ProgressDialogFragment extends DialogFragment {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {

            final ProgressDialog dialog = new ProgressDialog(getActivity());
            dialog.setMessage(getString(R.string.checkingServer));
            dialog.setIndeterminate(true);
            this.setCancelable(false);
            return dialog;
        }

    }

}
