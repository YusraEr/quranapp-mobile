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
import androidx.appcompat.app.AppCompatDelegate;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.quranapp.fragments.SurahListFragment;
import com.example.quranapp.utils.SettingsUtils;
import com.example.quranapp.utils.ThemeUtils;
import com.example.quranapp.viewmodel.SurahViewModel;

import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private ProgressBar progressBar;
    private TextView textViewError;
    private Button buttonRefresh;
    private Button buttonChangeTheme;
    private SurahViewModel surahViewModel;

    private int selectedReciterIndex = -1; // Untuk menyimpan pilihan sementara di dialog

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
        setupEventListeners();
    }

    // --- Inflate Menu ---
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        return true;
    }

    // --- Handle Menu Item Clicks ---
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

        selectedReciterIndex = checkedItem; // Simpan index awal

        builder.setSingleChoiceItems(reciterNames, checkedItem, (dialog, which) -> {
            selectedReciterIndex = which; // Update index pilihan sementara
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
        transaction.replace(R.id.fragment_container_main, surahListFragment); // Pastikan ID container ini ada di activity_main.xml
        // transaction.addToBackStack(null); // Opsional, tergantung kebutuhan navigasi
        transaction.commit();
    }

    private void setupObservers() {

        // Contoh jika SurahViewModel memiliki LiveData untuk loading dan error yang relevan untuk MainActivity
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

        // Mengamati daftar surah untuk mengetahui apakah data berhasil dimuat
        // (dan menyembunyikan error jika berhasil)
        surahViewModel.getAllSurahs().observe(this, surahs -> {
            if (surahs != null && !surahs.isEmpty()) {
                textViewError.setVisibility(View.GONE);
                buttonRefresh.setVisibility(View.GONE);
                // SurahListFragment akan menangani penampilan data
            }
        });
    }

    private void setupEventListeners() {
        buttonRefresh.setOnClickListener(v -> {
            // Memanggil metode refresh di ViewModel
            surahViewModel.refreshSurahs();
        });

        buttonChangeTheme.setOnClickListener(v -> {
            if (ThemeUtils.isDarkTheme(this)) {
                ThemeUtils.setLightTheme(this);
            } else {
                ThemeUtils.setDarkTheme(this);
            }
        });
    }

    // Metode ini bisa dipanggil oleh fragment jika fragment yang menangani tombol refresh
    public void showLoading() {
        progressBar.setVisibility(View.VISIBLE);
        textViewError.setVisibility(View.GONE);
        buttonRefresh.setVisibility(View.GONE);
    }

    public void showError(String message) {
        progressBar.setVisibility(View.GONE);
        textViewError.setVisibility(View.VISIBLE);
        textViewError.setText(message);
        buttonRefresh.setVisibility(View.VISIBLE);
    }

    public void hideErrorAndLoading() {
        progressBar.setVisibility(View.GONE);
        textViewError.setVisibility(View.GONE);
        buttonRefresh.setVisibility(View.GONE);
    }
}