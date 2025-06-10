package com.example.komikfinale.ui.home;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.komikfinale.R;
import com.example.komikfinale.ui.adapter.MangaAdapter;
import java.util.ArrayList;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRefresh;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi semua view dari layout
        bindViews(view);

        // Setup RecyclerView
        setupRecyclerView();

        // Inisialisasi ViewModel
        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);

        // Mengamati perubahan data dari ViewModel
        observeViewModel();

        // Mengatur listener untuk tombol refresh
        btnRefresh.setOnClickListener(v -> {
            errorLayout.setVisibility(View.GONE);
            progressBar.setVisibility(View.VISIBLE);
            homeViewModel.fetchMangaData();
        });
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.rv_manga);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.layout_error);
        btnRefresh = view.findViewById(R.id.btn_refresh);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Membuat adapter dengan ID fragment saat ini
        adapter = new MangaAdapter(getContext(), new ArrayList<>(), R.id.homeFragment);
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        // Mengamati status loading
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        // Mengamati daftar manga
        homeViewModel.getMangaList().observe(getViewLifecycleOwner(), mangaList -> {
            progressBar.setVisibility(View.GONE); // Pastikan progressbar hilang setelah data diterima
            if (mangaList != null) {
                // Jika data berhasil didapat, tampilkan RecyclerView
                recyclerView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                adapter.updateMangaList(mangaList);
            } else {
                // Jika data null (gagal), tampilkan tampilan error
                recyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}