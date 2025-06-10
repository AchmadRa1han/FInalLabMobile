package com.example.komikfinale.ui.library;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.repository.MangaRepository;
import java.util.List;

public class LibraryViewModel extends AndroidViewModel {
    private final MangaRepository mangaRepository;
    private final LiveData<List<Manga>> favoriteMangaList;

    public LibraryViewModel(@NonNull Application application) {
        super(application);
        mangaRepository = MangaRepository.getInstance(application);
        // Langsung ambil LiveData dari repository
        favoriteMangaList = mangaRepository.getAllFavorites();
    }

    // Method untuk diobservasi oleh Fragment
    public LiveData<List<Manga>> getFavoriteMangaList() {
        return favoriteMangaList;
    }
}