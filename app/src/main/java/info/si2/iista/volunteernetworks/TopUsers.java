package info.si2.iista.volunteernetworks;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.ItemUser;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 2/10/15
 * Project: Virde
 */
public class TopUsers extends AppCompatActivity implements AdapterTopUsers.ClickListener {

    // RecyclerView
    private ArrayList<ItemUser> items = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recycler_topusers);

        // ActionBar
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        // Views
        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        // Top Users
        if (getIntent().getExtras() != null) {
            items = getIntent().getParcelableArrayListExtra("topUsers");
        }

        // RecyclerView
        recyclerView.setHasFixedSize(true);

        AdapterTopUsers adapter = new AdapterTopUsers(this, items);
        adapter.setClickListener(this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public void onContributionItemClick(View view, int position) {

        ImageView userImage = (ImageView)view.findViewById(R.id.userImage);
        ItemUser user = items.get(position);

        Intent intent = new Intent(this, Profile.class);
        intent.putExtra("user", user);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {

            ActivityOptionsCompat options = ActivityOptionsCompat.
                    makeSceneTransitionAnimation(this, userImage, "transitionUser");
            ActivityCompat.startActivity(this, intent, options.toBundle());

        } else {
            startActivity(intent);
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
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
