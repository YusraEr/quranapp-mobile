package com.example.quranapp; // Ganti dengan package name aplikasi Anda

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.quranapp.data.remote.model.SuratNavInfo;
import com.example.quranapp.fragments.AyatListFragment;
import com.example.quranapp.utils.ThemeUtils;
import com.example.quranapp.viewmodel.AyatViewModel;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_SURAH_NUMBER = "extra_surah_number";
    public static final String EXTRA_SURAH_NAME_LATIN = "extra_surah_name_latin";

    private int surahNumber;
    private String surahNameLatin;
    private AyatViewModel ayatViewModel;
    private TextView toolbarTitle;
    private Button buttonPreviousSurah;
    private Button buttonNextSurah;

    private SuratNavInfo currentSuratSebelumnya = null;
    private SuratNavInfo currentSuratSelanjutnya = null;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ThemeUtils.applyTheme(this);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = findViewById(R.id.toolbarDetail);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowTitleEnabled(false);
        }

        toolbarTitle = findViewById(R.id.toolbarDetailTitle);
        buttonPreviousSurah = findViewById(R.id.buttonPreviousSurah);
        buttonNextSurah = findViewById(R.id.buttonNextSurah);

        Intent intent = getIntent();
        if (intent != null) {
            surahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, -1);
            surahNameLatin = intent.getStringExtra(EXTRA_SURAH_NAME_LATIN);
        }

        if (surahNumber == -1) {
            finish();
            return;
        }

        updateToolbarTitle(surahNameLatin, null); // Judul awal

        ayatViewModel = new ViewModelProvider(this).get(AyatViewModel.class);

        if (savedInstanceState == null) {
            loadAyatListFragment();
        }

        observeViewModel();
        setupNavigationButtonListeners();

        ayatViewModel.initialLoadAyats(surahNumber);
    }

    private void updateToolbarTitle(String namaLatin, String namaArab) {
        String title = namaLatin != null ? namaLatin : "Detail Surah";
        if (namaArab != null && !namaArab.isEmpty()) {
            title += " (" + namaArab + ")";
        }
        if (toolbarTitle != null) {
            toolbarTitle.setText(title);
        } else if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(title);
        }
    }


    private void observeViewModel() {
        ayatViewModel.getSurahDetail().observe(this, surahDetail -> {
            if (surahDetail == null) {
                Log.d("DetailActivityDebug", "Observer triggered: surahDetail is NULL.");
                // Hide navigation buttons when no data is available
                buttonPreviousSurah.setVisibility(View.GONE);
                buttonNextSurah.setVisibility(View.GONE);
                return;
            }

            Log.d("DetailActivityDebug", "Observer triggered: Surah " + surahDetail.getNamaLatin());

            if (surahDetail.getSuratSelanjutnya() != null) {
                Log.d("DetailActivityDebug", "  -> Surat Selanjutnya DITERIMA: " + surahDetail.getSuratSelanjutnya().getNamaLatin());
                currentSuratSelanjutnya = surahDetail.getSuratSelanjutnya();
            } else {
                Log.d("DetailActivityDebug", "  -> Surat Selanjutnya adalah NULL.");
                currentSuratSelanjutnya = null;
            }

            // Process previous surah data
            if (surahDetail.getSuratSebelumnya() != null) {
                Log.d("DetailActivityDebug", "  -> Surat Sebelumnya DITERIMA: " + surahDetail.getSuratSebelumnya().getNamaLatin());
                currentSuratSebelumnya = surahDetail.getSuratSebelumnya();
            } else {
                Log.d("DetailActivityDebug", "  -> Surat Sebelumnya adalah NULL.");
                currentSuratSebelumnya = null;
            }

            updateToolbarTitle(surahDetail.getNamaLatin(), surahDetail.getNama());

            if (currentSuratSebelumnya != null && currentSuratSebelumnya.getNomor() > 0 &&
                currentSuratSebelumnya.getNamaLatin() != null && !currentSuratSebelumnya.getNamaLatin().isEmpty()) {
                buttonPreviousSurah.setText(currentSuratSebelumnya.getNamaLatin());
                buttonPreviousSurah.setVisibility(View.VISIBLE);
            } else {
                buttonPreviousSurah.setVisibility(View.GONE);
                Log.d("DetailActivity", "Previous surah data invalid or not available");
            }

            // Update next surah button visibility and text
            if (currentSuratSelanjutnya != null && currentSuratSelanjutnya.getNomor() > 0 &&
                currentSuratSelanjutnya.getNamaLatin() != null && !currentSuratSelanjutnya.getNamaLatin().isEmpty()) {
                buttonNextSurah.setText(currentSuratSelanjutnya.getNamaLatin());
                buttonNextSurah.setVisibility(View.VISIBLE);
            } else {
                buttonNextSurah.setVisibility(View.GONE);
                Log.d("DetailActivity", "Next surah data invalid or not available: " +
                      (currentSuratSelanjutnya != null ? "nomor=" + currentSuratSelanjutnya.getNomor() +
                      ", name=" + currentSuratSelanjutnya.getNamaLatin() : "null"));
            }
        });
    }

    private void setupNavigationButtonListeners() {
        buttonPreviousSurah.setOnClickListener(v -> {
            if (currentSuratSebelumnya != null && currentSuratSebelumnya.getNomor() > 0) {
                navigateToSurah(currentSuratSebelumnya.getNomor(), currentSuratSebelumnya.getNamaLatin());
            }
        });

        buttonNextSurah.setOnClickListener(v -> {
            if (currentSuratSelanjutnya != null && currentSuratSelanjutnya.getNomor() > 0) {
                navigateToSurah(currentSuratSelanjutnya.getNomor(), currentSuratSelanjutnya.getNamaLatin());
            }
        });
    }

    private void navigateToSurah(int newSurahNumber, String newSurahNameLatin) {
        Intent intent = new Intent(this, DetailActivity.class);
        intent.putExtra(EXTRA_SURAH_NUMBER, newSurahNumber);
        intent.putExtra(EXTRA_SURAH_NAME_LATIN, newSurahNameLatin);
        startActivity(intent);
        finish();
    }

    private void loadAyatListFragment() {
        AyatListFragment ayatListFragment = AyatListFragment.newInstance(surahNumber, surahNameLatin);
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.fragment_container_detail, ayatListFragment);
        transaction.commit();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
