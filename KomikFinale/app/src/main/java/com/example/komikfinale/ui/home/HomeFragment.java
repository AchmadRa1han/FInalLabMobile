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
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.komikfinale.R;
import com.example.komikfinale.model.Genre;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.ui.adapter.MangaAdapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private ProgressBar progressBar;
    private LinearLayout errorLayout;
    private Button btnRefresh;

    // Variabel untuk menyimpan daftar genre dan status pilihan filter
    private final List<Genre> allGenres = new ArrayList<>();
    private boolean[] checkedGenres;
    private final ArrayList<Integer> selectedGenreIndices = new ArrayList<>();

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
        menu.clear(); // Hapus menu lama untuk mencegah duplikat
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                homeViewModel.setQuery(query);
                searchView.clearFocus(); // Sembunyikan keyboard
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) {
                return true;
            }

            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                homeViewModel.setQuery(null);
                return true;
            }
        });
    }

    // Method ini dipanggil saat salah satu item menu di ActionBar diklik
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int itemId = item.getItemId();

        if (itemId == R.id.action_filter) {
            showGenreFilterDialog();
            return true;
        }

        Map<String, String> orderMap = new HashMap<>();
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

        // Biarkan item lain (seperti ganti tema) ditangani oleh Activity
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

        // Observer untuk mengambil daftar genre dari ViewModel
        homeViewModel.getGenreList().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null) {
                allGenres.clear();
                allGenres.addAll(genres);
            }
        });
    }

    // Method untuk menampilkan dialog filter genre
    private void showGenreFilterDialog() {
        if (allGenres.isEmpty()) {
            Toast.makeText(getContext(), "Daftar genre belum termuat, coba lagi sesaat", Toast.LENGTH_SHORT).show();
            return;
        }

        String[] genreNames = new String[allGenres.size()];
        checkedGenres = new boolean[allGenres.size()];
        for (int i = 0; i < allGenres.size(); i++) {
            genreNames[i] = allGenres.get(i).getAttributes().getName().getEn();
            if (selectedGenreIndices.contains(i)) {
                checkedGenres[i] = true;
            }
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Filter Berdasarkan Genre");
        builder.setMultiChoiceItems(genreNames, checkedGenres, (dialog, which, isChecked) -> {
            if (isChecked) {
                if (!selectedGenreIndices.contains(which)) {
                    selectedGenreIndices.add(which);
                }
            } else {
                selectedGenreIndices.remove(Integer.valueOf(which));
            }
        });

        builder.setPositiveButton("Terapkan", (dialog, which) -> {
            ArrayList<String> selectedGenreIds = new ArrayList<>();
            for (int index : selectedGenreIndices) {
                selectedGenreIds.add(allGenres.get(index).getId());
            }
            homeViewModel.setGenreFilter(selectedGenreIds);
        });

        builder.setNegativeButton("Batal", (dialog, which) -> dialog.dismiss());

        builder.setNeutralButton("Reset", (dialog, which) -> {
            selectedGenreIndices.clear();
            homeViewModel.setGenreFilter(new ArrayList<>());
        });

        builder.create().show();
    }
}