package com.example.quranapp; // Ganti dengan package name aplikasi Anda

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.quranapp.fragments.SurahListFragment;
import com.example.quranapp.utils.SettingsUtils;
import com.example.quranapp.utils.ThemeUtils;
import com.example.quranapp.viewmodel.SurahViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textViewError;
    private Button buttonRefresh;
    private FloatingActionButton buttonChangeTheme;
    private SurahViewModel surahViewModel;

    private int selectedReciterIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_main);

        // Setup Toolbar
        androidx.appcompat.widget.Toolbar toolbar = findViewById(R.id.toolbarMain);
        setSupportActionBar(toolbar);

        progressBar = findViewById(R.id.progressBarMain);
        textViewError = findViewById(R.id.textViewErrorMain);
        buttonRefresh = findViewById(R.id.buttonRefreshMain);
        buttonChangeTheme = findViewById(R.id.buttonChangeTheme);


        surahViewModel = new ViewModelProvider(this).get(SurahViewModel.class);

        if (savedInstanceState == null) {
            loadSurahListFragment();
        }

        setupObservers();
        updateThemeIcon();
        setupEventListeners();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.action_select_reciter) {
            showReciterSelectionDialog();
            return true;
        }

        if (item.getItemId() == R.id.action_search_ayat) {
            Intent intent = new Intent(this, SearchActivity.class);
            startActivity(intent);
        }
        return super.onOptionsItemSelected(item);
    }

    private void showReciterSelectionDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Pilih Qari");

        Map<String, String> reciters = SettingsUtils.getAvailableReciters();
        String[] reciterNames = reciters.values().toArray(new String[0]);
        String[] reciterKeys = reciters.keySet().toArray(new String[0]);
        String currentReciterKey = SettingsUtils.getReciterPreference(this);

        int checkedItem = -1;
        for (int i = 0; i < reciterKeys.length; i++) {
            if (reciterKeys[i].equals(currentReciterKey)) {
                checkedItem = i;
                break;
            }
        }

        selectedReciterIndex = checkedItem;

        builder.setSingleChoiceItems(reciterNames, checkedItem, (dialog, which) -> {
            selectedReciterIndex = which;
        });

        builder.setPositiveButton("OK", (dialog, which) -> {
            if (selectedReciterIndex != -1) {
                String newReciterKey = reciterKeys[selectedReciterIndex];
                SettingsUtils.saveReciterPreference(MainActivity.this, newReciterKey);
                Toast.makeText(MainActivity.this, "Qari " + reciterNames[selectedReciterIndex] + " dipilih.", Toast.LENGTH_SHORT).show();
            }
            dialog.dismiss();
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void loadSurahListFragment() {
        SurahListFragment surahListFragment = new SurahListFragment();
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_main, surahListFragment);
        transaction.commit();
    }

    private void setupObservers() {

        surahViewModel.getIsLoading().observe(this, isLoading -> {
            if (isLoading != null) {
                progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
                if (isLoading) {
                    textViewError.setVisibility(View.GONE);
                    buttonRefresh.setVisibility(View.GONE);
                }
            }
        });

        surahViewModel.getErrorMessage().observe(this, errorMessage -> {
            if (errorMessage != null && !errorMessage.isEmpty()) {
                textViewError.setVisibility(View.VISIBLE);
                textViewError.setText(errorMessage);
                buttonRefresh.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.GONE);
            } else {
                textViewError.setVisibility(View.GONE);
                buttonRefresh.setVisibility(View.GONE);
            }
        });

        surahViewModel.getAllSurahs().observe(this, surahs -> {
            if (surahs != null && !surahs.isEmpty()) {
                textViewError.setVisibility(View.GONE);
                buttonRefresh.setVisibility(View.GONE);
            }
        });
    }

    private void setupEventListeners() {
        buttonRefresh.setOnClickListener(v -> {
            surahViewModel.refreshSurahs();
        });

        buttonChangeTheme.setOnClickListener(v -> {
            if (ThemeUtils.isDarkTheme(this)) {
                ThemeUtils.setLightTheme(this);
                buttonChangeTheme.setImageResource(R.drawable.ic_theme);
            } else {
                ThemeUtils.setDarkTheme(this);
                buttonChangeTheme.setImageResource(R.drawable.ic_moon);
            }
        });

        updateThemeIcon();
    }

    private void updateThemeIcon() {
        if (ThemeUtils.isDarkTheme(this)) {
            buttonChangeTheme.setImageResource(R.drawable.ic_moon);
        } else {
            buttonChangeTheme.setImageResource(R.drawable.ic_theme);
        }
    }

    @Override
    protected void onResume() {
    super.onResume();
        updateThemeIcon();
    }
}

