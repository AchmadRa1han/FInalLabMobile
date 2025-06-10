package com.example.komikfinale.ui.reader;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.recyclerview.widget.RecyclerView;

import com.example.komikfinale.R;
import com.example.komikfinale.data.remote.MangaApiService;
import com.example.komikfinale.data.remote.RetrofitClient;
import com.example.komikfinale.model.AtHomeServerResponse;
import com.example.komikfinale.ui.MainActivity;
import com.example.komikfinale.ui.adapter.PageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    // Kunci untuk Intent Extra
    public static final String EXTRA_CHAPTER_IDS = "extra_chapter_ids";
    public static final String EXTRA_CHAPTER_POSITION = "extra_chapter_position";

    // View Components
    private RecyclerView rvPages;
    private PageAdapter pageAdapter;
    private ProgressBar progressBar;
    private LinearLayout topControls, bottomControls;
    private ImageButton btnBack, btnHome, btnPrevChapter, btnNextChapter, btnChangeTheme;
    private TextView tvReaderTitle;

    // Data & State Management
    private List<String> pageUrls = new ArrayList<>();
    private ArrayList<String> allChapterIds;
    private int currentChapterPosition;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        // Inisialisasi semua view dari layout
        bindViews();

        // Membuat listener untuk tap pada halaman
        View.OnClickListener pageClickListener = v -> toggleControlsVisibility();

        // Menyiapkan RecyclerView dan Adapter
        pageAdapter = new PageAdapter(this, pageUrls, pageClickListener);
        rvPages.setAdapter(pageAdapter);

        // Menyiapkan semua listener untuk tombol dan scroll
        setupListeners();

        // Mengambil data dari Intent yang dikirim oleh ChapterAdapter
        allChapterIds = getIntent().getStringArrayListExtra(EXTRA_CHAPTER_IDS);
        currentChapterPosition = getIntent().getIntExtra(EXTRA_CHAPTER_POSITION, 0);

        // Memuat chapter jika datanya valid
        if (allChapterIds != null && !allChapterIds.isEmpty()) {
            loadChapter();
        }
    }

    private void bindViews() {
        rvPages = findViewById(R.id.rv_pages);
        progressBar = findViewById(R.id.progress_bar_reader);
        topControls = findViewById(R.id.top_controls);
        bottomControls = findViewById(R.id.bottom_controls);
        btnBack = findViewById(R.id.btn_back);
        btnHome = findViewById(R.id.btn_home);
        btnPrevChapter = findViewById(R.id.btn_prev_chapter);
        btnNextChapter = findViewById(R.id.btn_next_chapter);
        btnChangeTheme = findViewById(R.id.btn_change_theme);
        tvReaderTitle = findViewById(R.id.tv_reader_title);
    }

    private void setupListeners() {
        btnBack.setOnClickListener(v -> onBackPressed());

        btnHome.setOnClickListener(v -> {
            Intent intent = new Intent(this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
            startActivity(intent);
            finish();
        });

        btnChangeTheme.setOnClickListener(v -> toggleTheme());

        btnPrevChapter.setOnClickListener(v -> {
            if (currentChapterPosition > 0) {
                currentChapterPosition--;
                loadChapter();
            }
        });

        btnNextChapter.setOnClickListener(v -> {
            if (currentChapterPosition < allChapterIds.size() - 1) {
                currentChapterPosition++;
                loadChapter();
            }
        });

        // Menyembunyikan kontrol secara otomatis saat pengguna mulai scroll
        rvPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                // Jika pengguna scroll ke bawah (dy > 5) dan kontrol sedang terlihat, sembunyikan.
                if (dy > 5 && topControls.getVisibility() == View.VISIBLE) {
                    toggleControlsVisibility();
                }
            }
        });
    }

    // Memuat data untuk chapter yang sedang aktif
    private void loadChapter() {
        updateChapterNavButtons();
        tvReaderTitle.setText("Chapter " + (currentChapterPosition + 1)); // Contoh judul

        // Bersihkan halaman dari chapter sebelumnya dan reset adapter
        pageUrls.clear();
        pageAdapter.notifyDataSetChanged();
        rvPages.scrollToPosition(0); // Selalu mulai dari atas

        String chapterId = allChapterIds.get(currentChapterPosition);
        fetchChapterPages(chapterId);
    }

    // Mengambil data halaman dari API
    private void fetchChapterPages(String chapterId) {
        progressBar.setVisibility(View.VISIBLE);
        MangaApiService apiService = RetrofitClient.getClient().create(MangaApiService.class);
        apiService.getChapterPages(chapterId).enqueue(new Callback<AtHomeServerResponse>() {
            @Override
            public void onResponse(Call<AtHomeServerResponse> call, Response<AtHomeServerResponse> response) {
                progressBar.setVisibility(View.GONE);
                if (response.isSuccessful() && response.body() != null) {
                    AtHomeServerResponse data = response.body();
                    String baseUrl = data.getBaseUrl();
                    String hash = data.getChapter().getHash();
                    List<String> pageFilenames = data.getChapter().getData();

                    for (String filename : pageFilenames) {
                        String finalUrl = baseUrl + "/data/" + hash + "/" + filename;
                        pageUrls.add(finalUrl);
                    }
                    pageAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(ReaderActivity.this, "Gagal memuat halaman", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<AtHomeServerResponse> call, Throwable t) {
                progressBar.setVisibility(View.GONE);
                Toast.makeText(ReaderActivity.this, "Error: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void toggleTheme() {
        int currentNightMode = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;
        int newNightMode = (currentNightMode == Configuration.UI_MODE_NIGHT_YES) ?
                AppCompatDelegate.MODE_NIGHT_NO : AppCompatDelegate.MODE_NIGHT_YES;
        AppCompatDelegate.setDefaultNightMode(newNightMode);

        SharedPreferences.Editor editor = getSharedPreferences("theme_prefs", MODE_PRIVATE).edit();
        editor.putInt("night_mode", newNightMode);
        editor.apply();
    }

    private void toggleControlsVisibility() {
        int visibility = (topControls.getVisibility() == View.VISIBLE) ? View.GONE : View.VISIBLE;
        topControls.setVisibility(visibility);
        bottomControls.setVisibility(visibility);
    }

    private void updateChapterNavButtons() {
        // Tombol Prev akan muncul hanya jika kita tidak di chapter pertama
        btnPrevChapter.setVisibility(currentChapterPosition > 0 ? View.VISIBLE : View.INVISIBLE);

        // Tombol Next akan muncul hanya jika kita tidak di chapter terakhir
        btnNextChapter.setVisibility(currentChapterPosition < allChapterIds.size() - 1 ? View.VISIBLE : View.INVISIBLE);
    }
}