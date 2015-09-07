package info.si2.iista.volunteernetworks;

import android.os.Bundle;
import android.os.Handler;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import info.si2.iista.bolunteernetworks.apiclient.Item;
import info.si2.iista.bolunteernetworks.apiclient.ItemSync;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 7/9/15
 * Project: Virde
 */
public class SyncUserData extends AppCompatActivity implements AdapterSyncUserData.ClickListener {

    // RecyclerView
    private RecyclerView recyclerView;
    private ArrayList<ItemSync> items;

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

        /** Test Data **/

        items = new ArrayList<>();
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", true));
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", false));
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", false));
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", true));
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", true));
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", false));
        items.add(new ItemSync(Item.SYNC, "", "Nombre de campaña", "01 Ago 2015", true));

        /** End Test Data **/

        // RecyclerView
        recyclerView.setHasFixedSize(true);
        AdapterSyncUserData adapter = new AdapterSyncUserData(getApplicationContext(), items);
        adapter.setClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

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
                ((AdapterSyncUserData)recyclerView.getAdapter()).setSynchronizing(true);

                Handler handler = new Handler();

                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        ((AdapterSyncUserData)recyclerView.getAdapter()).setSynchronizing(false);
                    }
                }, 3000);

                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onHomeItemClick(View view, int position) {

    }

}
