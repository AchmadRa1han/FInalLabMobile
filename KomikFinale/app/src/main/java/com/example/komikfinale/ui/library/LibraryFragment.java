package com.example.komikfinale.ui.library;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.example.komikfinale.R;
import com.example.komikfinale.ui.adapter.MangaAdapter; // Kita pakai ulang adapter yang sama
import java.util.ArrayList;

public class LibraryFragment extends Fragment {
    private LibraryViewModel viewModel;
    private RecyclerView recyclerView;
    private MangaAdapter adapter;
    private TextView tvEmpty;

    public LibraryFragment() {
        super(R.layout.fragment_library);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Inisialisasi Views
        recyclerView = view.findViewById(R.id.rv_library);
        tvEmpty = view.findViewById(R.id.tv_empty_library);

        // Setup RecyclerView
        recyclerView.setLayoutManager(new GridLayoutManager(getContext(), 2));
        // Gunakan kembali MangaAdapter yang sudah ada!
        // Ganti baris ini
        adapter = new MangaAdapter(getContext(), new ArrayList<>(), R.id.libraryFragment);
        recyclerView.setAdapter(adapter);

        // Inisialisasi ViewModel
        viewModel = new ViewModelProvider(this).get(LibraryViewModel.class);

        // Mengamati data favorit dari database
        viewModel.getFavoriteMangaList().observe(getViewLifecycleOwner(), mangaList -> {
            if (mangaList != null && !mangaList.isEmpty()) {
                recyclerView.setVisibility(View.VISIBLE);
                tvEmpty.setVisibility(View.GONE);
                adapter.updateMangaList(mangaList);
            } else {
                recyclerView.setVisibility(View.GONE);
                tvEmpty.setVisibility(View.VISIBLE);
            }
        });
    }
}