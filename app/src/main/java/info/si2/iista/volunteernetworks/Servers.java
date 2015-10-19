package info.si2.iista.volunteernetworks;

import android.app.DialogFragment;
import android.content.Intent;
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
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class Servers extends AppCompatActivity implements DialogFragmentAddServer.DialogFragmentAddServerListener, AdapterServers.DeleteServerDialogFragment.DeleteServerDialogListener,
        OnDBApiResult {

    // RecyclerView
    private RecyclerView recyclerView;
    private ArrayList<ItemServer> items;
    private AdapterServers adapter;

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

    @Override
    public void onDialogPositiveClick(int position) {

        // DB
        int idServer = items.get(position).getId();
        DBVirde.getInstance(Servers.this).deleteServer(idServer);

        // RecyclerView
        items.remove(position);
        adapter.notifyItemRemoved(position);

    }

    @Override
    public void onDialogPositiveClick(String name) {

        // RecyclerView
        items.add(new ItemServer(-1, Item.SERVER, name, "", true));
        adapter.notifyItemInserted(items.size()-1);

        // DB
        DBVirde.getInstance(this).insertServer(items.get(items.size()-1));

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
                    Log.e("DBVirde", "Server not inserted");
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

                    // Reset app with new server
                    Intent intent = new Intent(Servers.this, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();

                }
                break;
        }
    }

}
