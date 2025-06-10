package com.example.quranapp; // Ganti dengan package name aplikasi Anda

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.quranapp.data.remote.model.SuratNavInfo;
import com.example.quranapp.fragments.AyatListFragment;
import com.example.quranapp.utils.ThemeUtils;
import com.example.quranapp.viewmodel.AyatViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class DetailActivity extends AppCompatActivity {

    public static final String EXTRA_SURAH_NUMBER = "extra_surah_number";
    public static final String EXTRA_SURAH_NAME_LATIN = "extra_surah_name_latin";

    private int surahNumber;
    private String surahNameLatin;
    private AyatViewModel ayatViewModel;
    private TextView toolbarTitle;
    private FloatingActionButton buttonPreviousSurah, buttonNextSurah, fabGoToAyat, fabToTheTop;

    private int totalAyat = 0;

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
        buttonPreviousSurah = findViewById(R.id.fabPreviousSurah);
        buttonNextSurah = findViewById(R.id.fabNextSurah);
        fabGoToAyat = findViewById(R.id.fabGoToAyat);
        fabToTheTop = findViewById(R.id.fabToTheTop);

        Intent intent = getIntent();
        if (intent != null) {
            surahNumber = intent.getIntExtra(EXTRA_SURAH_NUMBER, -1);
            surahNameLatin = intent.getStringExtra(EXTRA_SURAH_NAME_LATIN);
        }

        if (surahNumber == -1) {
            finish();
            return;
        }

        updateToolbarTitle(surahNameLatin, null);

        ayatViewModel = new ViewModelProvider(this).get(AyatViewModel.class);

        if (savedInstanceState == null) {
            loadAyatListFragment();
        }

        observeViewModel();
        setupNavigationButtonListeners();

        fabGoToAyat.setOnClickListener(v -> showGoToAyatDialog());
        fabToTheTop.setOnClickListener(v -> scrollToTopAyatList());

        ayatViewModel.initialLoadAyats(surahNumber);
    }

    @Override
    protected void onStart() {
        super.onStart();
        AyatListFragment fragment = (AyatListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_detail);
        if (fragment != null) {
            fragment.setOnScrollButtonVisibilityListener(visible -> setFabButtonsVisibility(visible));
        }
    }

    private void setFabButtonsVisibility(boolean visible) {
        float targetAlpha = visible ? 1f : 0f;
        int targetVisibility = visible ? View.VISIBLE : View.GONE;
        int duration = 250;

        boolean showPrev = currentSuratSebelumnya != null && currentSuratSebelumnya.getNomor() > 0 &&
                currentSuratSebelumnya.getNamaLatin() != null && !currentSuratSebelumnya.getNamaLatin().isEmpty();
        boolean showNext = currentSuratSelanjutnya != null && currentSuratSelanjutnya.getNomor() > 0 &&
                currentSuratSelanjutnya.getNamaLatin() != null && !currentSuratSelanjutnya.getNamaLatin().isEmpty();

        if (showPrev) {
            animateFab(buttonPreviousSurah, targetAlpha, targetVisibility, duration);
        } else {
            buttonPreviousSurah.setVisibility(View.GONE);
        }
        if (showNext) {
            animateFab(buttonNextSurah, targetAlpha, targetVisibility, duration);
        } else {
            buttonNextSurah.setVisibility(View.GONE);
        }
        animateFab(fabGoToAyat, targetAlpha, targetVisibility, duration);
        animateFab(fabToTheTop, targetAlpha, targetVisibility, duration);
    }

    private void animateFab(View fab, float targetAlpha, int targetVisibility, int duration) {
        if (fab.getVisibility() != View.VISIBLE && targetVisibility == View.VISIBLE) {
            fab.setAlpha(0f);
            fab.setVisibility(View.VISIBLE);
        }
        fab.animate()
            .alpha(targetAlpha)
            .setDuration(duration)
            .withEndAction(() -> {
                if (targetVisibility == View.GONE) {
                    fab.setVisibility(View.GONE);
                }
            })
            .start();
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
                buttonPreviousSurah.setVisibility(View.GONE);
                buttonNextSurah.setVisibility(View.GONE);
                return;
            }

            totalAyat = surahDetail.getJumlahAyat();

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
                buttonPreviousSurah.setVisibility(View.VISIBLE);
            } else {
                buttonPreviousSurah.setVisibility(View.GONE);
                Log.d("DetailActivity", "Previous surah data invalid or not available");
            }

            if (currentSuratSelanjutnya != null && currentSuratSelanjutnya.getNomor() > 0 &&
                currentSuratSelanjutnya.getNamaLatin() != null && !currentSuratSelanjutnya.getNamaLatin().isEmpty()) {
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

    private void showGoToAyatDialog() {
        android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
        builder.setTitle("Go to Ayat");

        String message = "Masukkan nomor ayat (1-" + totalAyat + ")";
        builder.setMessage(message);

        final android.widget.EditText input = new android.widget.EditText(this);
        input.setInputType(android.text.InputType.TYPE_CLASS_NUMBER);
        input.setHint("Enter ayat number");
        builder.setView(input);

        builder.setPositiveButton("Go", (dialog, which) -> {
            String value = input.getText().toString();
            if (!value.isEmpty()) {
                int ayatNumber = Integer.parseInt(value);

                if (ayatNumber < 1 || ayatNumber > totalAyat) {
                    Toast.makeText(this, "Nomor ayat harus antara 1 dan " + totalAyat, Toast.LENGTH_SHORT).show();
                } else {
                    goToAyatInFragment(ayatNumber);
                }
            }
        });

        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());
        builder.show();
    }

    private void goToAyatInFragment(int ayatNumber) {
        if (ayatNumber < 1 || ayatNumber > totalAyat) {
            Log.e("DetailActivity", "Ayat number out of bounds: " + ayatNumber + ", total ayat: " + totalAyat);
            Toast.makeText(this, "Nomor ayat harus antara 1 dan " + totalAyat, Toast.LENGTH_SHORT).show();
            return;
        }

        AyatListFragment fragment = (AyatListFragment) getSupportFragmentManager().findFragmentByTag("AyatListFragment");
        if (fragment == null) {
            fragment = (AyatListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_detail);
        }
        if (fragment != null && fragment.isAdded()) {
            fragment.scrollToAyat(ayatNumber);
        } else {
            Log.e("DetailActivity", "AyatListFragment not found or not attached");
        }
    }

    private void scrollToTopAyatList() {
        AyatListFragment fragment = (AyatListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_detail);
        if (fragment != null && fragment.isAdded()) {
            fragment.scrollToAyat(1);
        }
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
