package com.example.komikfinale.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.komikfinale.model.Genre;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.repository.MangaRepository;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private final MangaRepository mangaRepository;
    private final MutableLiveData<List<Manga>> mangaList;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<List<Genre>> genreList;

    // State untuk menyimpan kondisi filter, sort, dan search saat ini
    private String currentQuery = null;
    private final Map<String, String> currentOrder = new HashMap<>();
    private List<String> currentGenreIds = new ArrayList<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mangaRepository = MangaRepository.getInstance(application);
        mangaList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        genreList = new MutableLiveData<>();

        // Mengatur urutan default
        currentOrder.put("latestUploadedChapter", "desc");
        // Memuat data awal
        fetchMangaData();
        // Memuat daftar genre untuk dialog filter
        fetchGenreList();
    }

    // --- LiveData untuk diobservasi oleh Fragment ---
    public LiveData<List<Manga>> getMangaList() { return mangaList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<List<Genre>> getGenreList() { return genreList; }

    // --- Aksi yang bisa dipanggil dari Fragment ---
    public void setQuery(String query) {
        this.currentQuery = query;
        fetchMangaData();
    }

    public void setOrder(Map<String, String> order) {
        this.currentOrder.clear();
        this.currentOrder.putAll(order);
        fetchMangaData();
    }

    public void setGenreFilter(List<String> genreIds) {
        this.currentGenreIds = genreIds;
        fetchMangaData();
    }

    private void fetchGenreList() {
        mangaRepository.getGenreList(genreList);
    }

    public void fetchMangaData() {
        mangaRepository.getMangaList(mangaList, isLoading, currentQuery, currentOrder, currentGenreIds);
    }
}