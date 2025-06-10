package com.example.komikfinale.ui.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.komikfinale.R;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.ui.adapter.MangaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

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
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Memberitahu sistem bahwa fragment ini ingin menambahkan item ke menu di ActionBar
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupRecyclerView();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeViewModel();

        btnRefresh.setOnClickListener(v -> {
            homeViewModel.fetchMangaData();
        });
    }

    // Method ini dipanggil untuk membuat menu di ActionBar
    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        // Gunakan layout menu yang sudah kita buat
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // Dipanggil saat pengguna menekan tombol enter/search di keyboard
            @Override
            public boolean onQueryTextSubmit(String query) {
                homeViewModel.setQuery(query);
                searchView.clearFocus(); // Sembunyikan keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                // Kita tidak melakukan apa-apa saat teks berubah untuk menghindari terlalu banyak panggilan API
                return false;
            }
        });

        // Listener untuk mereset daftar saat tombol search ditutup
        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true; // Izinkan search view untuk terbuka
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                // Saat search view ditutup, hapus query dan muat ulang data awal
                homeViewModel.setQuery(null);
                return true; // Izinkan search view untuk tertutup
            }
        });
    }

    // Method ini dipanggil saat salah satu item menu di ActionBar diklik
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        Map<String, String> orderMap = new HashMap<>();
        int itemId = item.getItemId();

        if (itemId == R.id.sort_by_latest) {
            orderMap.put("latestUploadedChapter", "desc");
            homeViewModel.setOrder(orderMap);
            return true;
        } else if (itemId == R.id.sort_by_alphabet_asc) {
            orderMap.put("title", "asc");
            homeViewModel.setOrder(orderMap);
            return true;
        } else if (itemId == R.id.sort_by_alphabet_desc) {
            orderMap.put("title", "desc");
            homeViewModel.setOrder(orderMap);
            return true;
        }
        // Biarkan item lain (seperti ganti tema) ditangani oleh MainActivity
        return super.onOptionsItemSelected(item);
    }

    // --- Helper Methods ---

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.rv_manga);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.layout_error);
        btnRefresh = view.findViewById(R.id.btn_refresh);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MangaAdapter(getContext(), new ArrayList<>(), R.id.homeFragment);
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            if (isLoading) {
                progressBar.setVisibility(View.VISIBLE);
                recyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        });

        homeViewModel.getMangaList().observe(getViewLifecycleOwner(), mangaList -> {
            progressBar.setVisibility(View.GONE);
            if (mangaList != null) {
                recyclerView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                adapter.updateMangaList(mangaList);
            } else {
                recyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });
    }
}