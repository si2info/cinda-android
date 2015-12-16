package info.si2.iista.volunteernetworks;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Dictionary;
import info.si2.iista.volunteernetworks.apiclient.ItemDictionary;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 15/12/15
 * Project: Shiari
 */
public class DictionaryValue extends AppCompatActivity implements AdapterDictionary.OnItemClickListener {

    // Data
    private Dictionary dictionary;
    private ArrayList<ItemDictionary> searched;

    // Views
    private RecyclerView recyclerView;

    // Adapters
    private AdapterDictionary adapter;
    private AdapterDictionary adapterSearch;

    // Flags
    private boolean isSearchActive;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dictionary_value);

        // Action Bar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle(getString(R.string.select));
        }

        // Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // Init search contents
        searched = new ArrayList<>();
        dictionary = Contribution.dictionary;
        ArrayList<ItemDictionary> items = dictionary.getTerms();

        // RecyclerView
        adapter = new AdapterDictionary(this, items);
        adapter.setOnItemClickListener(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_dictionary, menu);

        initSearchView(menu);

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

    /**
     * Init SearchView
     * @param menu Menu that contains searchView
     */
    public void initSearchView (Menu menu) {

        // Associate searchable configuration with the SearchView
        SearchManager searchManager =
                (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        final SearchView searchView =
                (SearchView) menu.findItem(R.id.search).getActionView();
        searchView.setSearchableInfo(
                searchManager.getSearchableInfo(getComponentName()));

        // Search - Listener
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                isSearchActive = true;
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

                isSearchActive = true;
                searched.clear();

                for (ItemDictionary item : dictionary.getTerms()) {
                    if (item.getName().contains(newText) || item.getDescription().contains(newText))
                        searched.add(item);
                }

                adapterSearch = new AdapterDictionary(DictionaryValue.this, searched);
                adapterSearch.setOnItemClickListener(DictionaryValue.this);
                recyclerView.setAdapter(adapterSearch);

                return true;
            }
        });

        // Close - Listener
        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                recyclerView.setAdapter(adapter);
                isSearchActive = false;
                return true;
            }
        });

    }

    @Override
    public void onItemClickListener(View view, int position) {

        String codeTerm = "";
        String term = "";

        if (isSearchActive) {
            codeTerm = searched.get(position).getCode();
            term = searched.get(position).getName();
        } else {
            codeTerm = dictionary.getTerms().get(position).getCode();
            term = dictionary.getTerms().get(position).getName();
        }

        Intent returnIntent = new Intent();
        returnIntent.putExtra("codeTerm", codeTerm);
        returnIntent.putExtra("term", term);
        setResult(RESULT_OK, returnIntent);
        finish();

    }

}
