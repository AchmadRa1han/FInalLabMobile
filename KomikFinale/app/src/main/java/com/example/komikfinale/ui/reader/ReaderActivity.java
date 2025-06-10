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
    public static final String EXTRA_CHAPTER_NUMBER = "extra_chapter_number"; // <-- KUNCI BARU
    public static final String EXTRA_CHAPTER_TITLE = "extra_chapter_title";   // <-- KUNCI BARU

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

        bindViews();

        View.OnClickListener pageClickListener = v -> toggleControlsVisibility();
        pageAdapter = new PageAdapter(this, pageUrls, pageClickListener);
        rvPages.setAdapter(pageAdapter);

        setupListeners();

        // Mengambil semua data dari Intent
        allChapterIds = getIntent().getStringArrayListExtra(EXTRA_CHAPTER_IDS);
        currentChapterPosition = getIntent().getIntExtra(EXTRA_CHAPTER_POSITION, 0);

        // --- PERBAIKAN JUDUL DI SINI ---
        String initialChapterNumber = getIntent().getStringExtra(EXTRA_CHAPTER_NUMBER);
        String initialChapterTitle = getIntent().getStringExtra(EXTRA_CHAPTER_TITLE);
        updateReaderTitle(initialChapterNumber, initialChapterTitle);
        // --- AKHIR PERBAIKAN JUDUL ---

        if (allChapterIds != null && !allChapterIds.isEmpty()) {
            loadChapter();
        }
    }

    private void updateReaderTitle(String number, String title) {
        String displayText = "Chapter " + number;
        if (title != null && !title.isEmpty()) {
            displayText += ": " + title;
        }
        tvReaderTitle.setText(displayText);
    }

    // ... (sisa semua method lain di ReaderActivity tidak ada perubahan)

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
        rvPages.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                if (dy > 5 && topControls.getVisibility() == View.VISIBLE) {
                    toggleControlsVisibility();
                }
            }
        });
    }

    private void loadChapter() {
        updateChapterNavButtons();
        // Judul sekarang tidak diatur di sini lagi untuk mencegah reset
        pageUrls.clear();
        pageAdapter.notifyDataSetChanged();
        rvPages.scrollToPosition(0);
        String chapterId = allChapterIds.get(currentChapterPosition);
        fetchChapterPages(chapterId);
    }

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
        btnPrevChapter.setVisibility(currentChapterPosition > 0 ? View.VISIBLE : View.INVISIBLE);
        btnNextChapter.setVisibility(currentChapterPosition < allChapterIds.size() - 1 ? View.VISIBLE : View.INVISIBLE);
    }
}