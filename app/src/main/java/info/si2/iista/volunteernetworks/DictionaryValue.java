package info.si2.iista.volunteernetworks;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Pair;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.ArrayList;

import info.si2.iista.volunteernetworks.apiclient.Dictionary;
import info.si2.iista.volunteernetworks.apiclient.ItemDictionary;
import info.si2.iista.volunteernetworks.apiclient.Result;
import info.si2.iista.volunteernetworks.database.DBVirde;
import info.si2.iista.volunteernetworks.database.OnDBApiResult;
import info.si2.iista.volunteernetworks.util.Util;

/**
 * Developer: Jose Miguel Mingorance
 * Date: 15/12/15
 * Project: Shiari
 */
public class DictionaryValue extends AppCompatActivity implements AdapterDictionary.OnItemClickListener, OnDBApiResult {

    // Data
    private Dictionary dictionary;
    private ArrayList<ItemDictionary> searched;

    // Views
    private RelativeLayout infoDictionary;
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
            getSupportActionBar().setElevation(0);
        }

        // Get dictionary from DB
        if (getIntent().getExtras() != null) {
            int idDictionary = getIntent().getExtras().getInt("code");
            DBVirde.getInstance(this).selectDictionary(idDictionary);
        }

        // Dictionary
//        dictionary = Contribution.dictionary;

        // Views
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);

        // RecyclerView
        ArrayList<ItemDictionary> items = new ArrayList<>();
        adapter = new AdapterDictionary(this, items);
        adapter.setOnItemClickListener(this);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);

    }

    private void initView () {

        // Views
        infoDictionary = (RelativeLayout)findViewById(R.id.dictionaryInfo);
        TextView dictionaryName = (TextView)findViewById(R.id.dictionaryName);
        ImageView info = (ImageView)findViewById(R.id.infoDictionary);

        // Set dictionary info
        dictionaryName.setText(dictionary.getName());
        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InfoDialogFragment dialog = InfoDialogFragment.newInstance(dictionary.getName(), dictionary.getDescription());
                dialog.show(getFragmentManager(), "Info");
            }
        });

        // Init search contents
        searched = new ArrayList<>();
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
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {

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
                return true;
            }
        });

        MenuItemCompat.setOnActionExpandListener(menu.getItem(0), new MenuItemCompat.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                infoDictionary.setVisibility(View.GONE);
                isSearchActive = true;
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                infoDictionary.setVisibility(View.VISIBLE);
                isSearchActive = false;
                return true;
            }
        });

    }

    @Override
    public void onItemClickListener(View view, int position) {

        String codeTerm;
        String term;

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

    /** DB **/
    @Override
    public void onDBApiInsertResult(Result result) {

    }

    @Override
    public void onDBApiSelectResult(Pair<Result, ArrayList> result) {
        switch (result.first.getResultFrom()) {
            case DBVirde.FROM_SELECT_DICTIONARY:
                if (result.first.isError()) {
                    Util.makeToast(this, result.first.getMensaje(), 0);
                } else {

                    if (result.second.size() > 0) {
                        dictionary = (Dictionary) result.second.get(0);
                        initView();
                    }

                }
                break;
        }
    }

    @Override
    public void onDBApiUpdateResult(Result result) {

    }

    /** Dialog - Info **/
    public static class InfoDialogFragment extends DialogFragment {

        private String title, message;

        static InfoDialogFragment newInstance(String title, String message) {
            InfoDialogFragment f = new InfoDialogFragment();

            // Supply num input as an argument.
            Bundle args = new Bundle();
            args.putString("title", title);
            args.putString("message", message);
            f.setArguments(args);

            return f;
        }

        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            title = getArguments().getString("title");
            message = getArguments().getString("message");
        }

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the Builder class for convenient dialog construction
            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            builder.setTitle(title);
            builder.setMessage(message);
            // Create the AlertDialog object and return it
            return builder.create();
        }
    }

}
