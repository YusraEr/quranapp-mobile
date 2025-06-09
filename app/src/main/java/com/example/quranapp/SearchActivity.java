package com.example.quranapp;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.appcompat.widget.Toolbar;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.quranapp.adapter.SearchResultAdapter;
import com.example.quranapp.utils.ThemeUtils;
import com.example.quranapp.viewmodel.SearchViewModel;

public class SearchActivity extends AppCompatActivity {

    private SearchViewModel viewModel;
    private SearchResultAdapter adapter;
    private RecyclerView recyclerView;
    private TextView infoTextView;
    private ProgressBar progressBar;
    private SearchView searchView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_search);

        Toolbar toolbar = findViewById(R.id.toolbarSearch);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        recyclerView = findViewById(R.id.rv_search_results);
        infoTextView = findViewById(R.id.textViewSearchInfo);
        progressBar = findViewById(R.id.progressBarSearch);

        adapter = new SearchResultAdapter(this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        viewModel = new ViewModelProvider(this).get(SearchViewModel.class);
        observeViewModel();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.search_menu, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        searchView = (SearchView) searchItem.getActionView();

        searchView.setIconified(false);
        searchView.setQueryHint("Cari terjemahan ayat...");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                viewModel.searchAyats(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });
        return true;
    }

    private void observeViewModel() {
        viewModel.isLoading.observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if(isLoading) {
                recyclerView.setVisibility(View.GONE);
                infoTextView.setVisibility(View.GONE);
            }
        });

        viewModel.searchResults.observe(this, results -> {
            viewModel.searchCompleted();

            if (results != null && !results.isEmpty()) {
                adapter.setData(results, searchView.getQuery().toString());
                recyclerView.setVisibility(View.VISIBLE);
                infoTextView.setVisibility(View.GONE);
            } else if (results != null) {
                recyclerView.setVisibility(View.GONE);
                infoTextView.setText("Tidak ada hasil untuk '" + searchView.getQuery().toString() + "'");
                infoTextView.setVisibility(View.VISIBLE);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}