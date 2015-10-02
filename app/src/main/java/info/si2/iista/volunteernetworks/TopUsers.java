package info.si2.iista.volunteernetworks;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

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

        RecyclerView recyclerView = (RecyclerView)findViewById(R.id.recyclerView);

        /** TEST DATA **/
        for (int i=0; i<10; i++) {
            items.add(new ItemUser(i+1, 99, "SI2 Soluciones", ""));
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

    }

}
