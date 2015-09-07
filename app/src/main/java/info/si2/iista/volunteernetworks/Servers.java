package info.si2.iista.volunteernetworks;

import android.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import info.si2.iista.bolunteernetworks.apiclient.Item;
import info.si2.iista.bolunteernetworks.apiclient.ItemServer;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class Servers extends AppCompatActivity implements DialogFragmentAddServer.DialogFragmentAddServerListener, AdapterServers.DeleteServerDialogFragment.DeleteServerDialogListener {

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

        /** Test Data **/

        items = new ArrayList<>();
        items.add(new ItemServer(Item.SERVER, "http://www.whatever.com/apiverdi"));
        items.add(new ItemServer(Item.SERVER, "http://www.whatever.com/apiverdi"));
        items.add(new ItemServer(Item.SERVER, "http://www.whatever.com/apiverdi"));
        items.add(new ItemServer(Item.SERVER, "http://www.whatever.com/apiverdi"));
        items.add(new ItemServer(Item.SERVER, "http://www.whatever.com/apiverdi"));

        /** End Test Data **/

        // RecyclerView
        recyclerView.setHasFixedSize(true);
        adapter = new AdapterServers(this, items);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    public void addItem (View view) {

        DialogFragment dialog = new DialogFragmentAddServer();
        dialog.show(getFragmentManager(), "AddServerDialogFragment");

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

        items.remove(position);
        adapter.notifyItemRemoved(position);

    }

    @Override
    public void onDialogPositiveClick(String name) {

        items.add(0, new ItemServer(Item.SERVER, name));
        adapter.notifyItemInserted(0);

    }

}
