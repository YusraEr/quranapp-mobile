package com.example.quranapp; // Ganti dengan package name aplikasi Anda

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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

        View dialogView = getLayoutInflater().inflate(R.layout.dialog_go_to_ayat, null);
        builder.setView(dialogView);

        com.google.android.material.textfield.TextInputLayout inputLayout = dialogView.findViewById(R.id.input_layout);
        com.google.android.material.textfield.TextInputEditText editAyatNumber = dialogView.findViewById(R.id.edit_ayat_number);
        Button btnCancel = dialogView.findViewById(R.id.btn_cancel);
        Button btnGo = dialogView.findViewById(R.id.btn_go);

        String rangeInfo = String.format("Masukkan nomor ayat (1 - %d)", totalAyat);
        inputLayout.setHelperText(rangeInfo);

        final android.app.AlertDialog dialog = builder.create();
        dialog.show();

        btnCancel.setOnClickListener(v -> dialog.dismiss());

        btnGo.setOnClickListener(v -> {
            String value = editAyatNumber.getText().toString();
            if (!value.isEmpty()) {
                try {
                    int ayatNumber = Integer.parseInt(value);

                    // Validasi nomor ayat
                    if (ayatNumber < 1 || ayatNumber > totalAyat) {
                        inputLayout.setError("Nomor ayat harus antara 1 dan " + totalAyat);
                    } else {
                        dialog.dismiss();
                        goToAyatInFragment(ayatNumber);
                    }
                } catch (NumberFormatException e) {
                    inputLayout.setError("Masukkan angka yang valid");
                }
            } else {
                inputLayout.setError("Silakan masukkan nomor ayat");
            }
        });

        editAyatNumber.addTextChangedListener(new android.text.TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                inputLayout.setError(null);
            }

            @Override
            public void afterTextChanged(android.text.Editable s) {
                if (!s.toString().isEmpty()) {
                    try {
                        int ayatNumber = Integer.parseInt(s.toString());
                        if (ayatNumber < 1 || ayatNumber > totalAyat) {
                            inputLayout.setError("Nomor ayat harus antara 1 dan " + totalAyat);
                            btnGo.setEnabled(false);
                        } else {
                            inputLayout.setError(null);
                            btnGo.setEnabled(true);
                        }
                    } catch (NumberFormatException e) {
                        inputLayout.setError("Masukkan angka yang valid");
                        btnGo.setEnabled(false);
                    }
                } else {
                    btnGo.setEnabled(false);
                }
            }
        });

        editAyatNumber.requestFocus();
        android.view.inputmethod.InputMethodManager imm = (android.view.inputmethod.InputMethodManager) getSystemService(android.content.Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInput(android.view.inputmethod.InputMethodManager.SHOW_FORCED, 0);
    }

    private void scrollToTopAyatList() {
        AyatListFragment fragment = (AyatListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_detail);
        if (fragment != null) {
            fragment.scrollToAyat(1);
        }
    }

    private void goToAyatInFragment(int ayatNumber) {
        AyatListFragment fragment = (AyatListFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_container_detail);
        if (fragment != null) {
            fragment.scrollToAyat(ayatNumber);
        }
    }
}

