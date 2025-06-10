package com.example.komikfinale.ui.reader;

import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.komikfinale.R;
import com.example.komikfinale.data.remote.MangaApiService;
import com.example.komikfinale.data.remote.RetrofitClient;
import com.example.komikfinale.model.AtHomeServerResponse;
import com.example.komikfinale.ui.adapter.PageAdapter;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    public static final String EXTRA_CHAPTER_ID = "extra_chapter_id";

    private RecyclerView rvPages;
    private PageAdapter pageAdapter;
    private ProgressBar progressBar;
    private List<String> pageUrls = new ArrayList<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);

        rvPages = findViewById(R.id.rv_pages);
        progressBar = findViewById(R.id.progress_bar_reader);

        pageAdapter = new PageAdapter(this, pageUrls);
        rvPages.setAdapter(pageAdapter);

        String chapterId = getIntent().getStringExtra(EXTRA_CHAPTER_ID);
        if (chapterId != null) {
            fetchChapterPages(chapterId);
        }
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
                        // Membuat URL lengkap untuk setiap halaman
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
}