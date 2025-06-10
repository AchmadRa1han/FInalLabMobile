package com.example.komikfinale.ui.home;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
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

    // View untuk Pagination
    private LinearLayout paginationControls;
    private Button btnPrevPage, btnNextPage;
    private TextView tvPageNumber;

    // Variabel untuk Filter Genre
    private final List<Genre> allGenres = new ArrayList<>();
    private boolean[] checkedGenres;
    private final ArrayList<Integer> selectedGenreIndices = new ArrayList<>();

    private static final int ITEMS_PER_PAGE = 20;

    public HomeFragment() {
        super(R.layout.fragment_home);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        bindViews(view);
        setupRecyclerView();

        homeViewModel = new ViewModelProvider(this).get(HomeViewModel.class);
        observeViewModel();
        setupListeners();
    }

    private void setupListeners() {
        btnRefresh.setOnClickListener(v -> homeViewModel.fetchMangaData());
        btnNextPage.setOnClickListener(v -> homeViewModel.nextPage());
        btnPrevPage.setOnClickListener(v -> homeViewModel.prevPage());
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        menu.clear();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                homeViewModel.setQuery(query);
                searchView.clearFocus();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String newText) { return false; }
        });

        searchItem.setOnActionExpandListener(new MenuItem.OnActionExpandListener() {
            @Override
            public boolean onMenuItemActionExpand(MenuItem item) { return true; }
            @Override
            public boolean onMenuItemActionCollapse(MenuItem item) {
                homeViewModel.setQuery(null);
                return true;
            }
        });
    }

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

        return super.onOptionsItemSelected(item);
    }

    private void bindViews(View view) {
        recyclerView = view.findViewById(R.id.rv_manga);
        progressBar = view.findViewById(R.id.progress_bar);
        errorLayout = view.findViewById(R.id.layout_error);
        btnRefresh = view.findViewById(R.id.btn_refresh);
        paginationControls = view.findViewById(R.id.pagination_controls);
        btnPrevPage = view.findViewById(R.id.btn_prev_page);
        btnNextPage = view.findViewById(R.id.btn_next_page);
        tvPageNumber = view.findViewById(R.id.tv_page_number);
    }

    private void setupRecyclerView() {
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        adapter = new MangaAdapter(getContext(), new ArrayList<>(), R.id.homeFragment);
        recyclerView.setAdapter(adapter);
    }

    private void observeViewModel() {
        homeViewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            if (isLoading) {
                recyclerView.setVisibility(View.GONE);
                errorLayout.setVisibility(View.GONE);
                paginationControls.setVisibility(View.GONE);
            }
        });

        homeViewModel.getMangaList().observe(getViewLifecycleOwner(), mangaList -> {
            progressBar.setVisibility(View.GONE);
            if (mangaList != null) {
                recyclerView.setVisibility(View.VISIBLE);
                errorLayout.setVisibility(View.GONE);
                paginationControls.setVisibility(View.VISIBLE);
                adapter.updateMangaList(mangaList);

                // Perbaikan terakhir: otomatis scroll ke atas saat pindah halaman
                recyclerView.scrollToPosition(0);

                tvPageNumber.setText("Page " + homeViewModel.getCurrentPage());
                btnPrevPage.setEnabled(homeViewModel.getCurrentPage() > 1);
                btnNextPage.setEnabled(mangaList.size() == ITEMS_PER_PAGE);

            } else {
                recyclerView.setVisibility(View.GONE);
                paginationControls.setVisibility(View.GONE);
                errorLayout.setVisibility(View.VISIBLE);
            }
        });

        homeViewModel.getGenreList().observe(getViewLifecycleOwner(), genres -> {
            if (genres != null) {
                allGenres.clear();
                allGenres.addAll(genres);
            }
        });
    }

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

