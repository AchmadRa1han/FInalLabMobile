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

    // LiveData untuk diobservasi oleh UI
    private final MutableLiveData<List<Manga>> mangaList;
    private final MutableLiveData<Boolean> isLoading;
    private final MutableLiveData<List<Genre>> genreList;

    // State untuk menyimpan kondisi filter, sort, search, dan pagination saat ini
    private String currentQuery = null;
    private final Map<String, String> currentOrder = new HashMap<>();
    private List<String> currentGenreIds = new ArrayList<>();
    private int currentPage = 1;
    private int offset = 0;
    private static final int LIMIT_PER_PAGE = 20; // Jumlah item per halaman

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mangaRepository = MangaRepository.getInstance(application);
        mangaList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        genreList = new MutableLiveData<>();

        // Mengatur urutan default saat pertama kali dibuka
        currentOrder.put("latestUploadedChapter", "desc");

        // Memuat data awal dan daftar genre
        fetchMangaData();
        fetchGenreList();
    }

    // --- LiveData untuk diobservasi oleh Fragment ---
    public LiveData<List<Manga>> getMangaList() { return mangaList; }
    public LiveData<Boolean> getIsLoading() { return isLoading; }
    public LiveData<List<Genre>> getGenreList() { return genreList; }
    public int getCurrentPage() { return currentPage; }


    // --- Aksi yang bisa dipanggil dari Fragment ---

    public void nextPage() {
        currentPage++;
        offset = (currentPage - 1) * LIMIT_PER_PAGE;
        fetchMangaData();
    }

    public void prevPage() {
        if (currentPage > 1) {
            currentPage--;
            offset = (currentPage - 1) * LIMIT_PER_PAGE;
            fetchMangaData();
        }
    }

    // Saat filter, sort, atau search diubah, selalu reset ke halaman pertama
    public void setQuery(String query) {
        this.currentQuery = query;
        resetToFirstPage();
    }

    public void setOrder(Map<String, String> order) {
        this.currentOrder.clear();
        this.currentOrder.putAll(order);
        resetToFirstPage();
    }

    public void setGenreFilter(List<String> genreIds) {
        this.currentGenreIds = genreIds;
        resetToFirstPage();
    }

    private void resetToFirstPage() {
        this.currentPage = 1;
        this.offset = 0;
        fetchMangaData();
    }

    // --- Method Pusat untuk Pengambilan Data ---

    private void fetchGenreList() {
        mangaRepository.getGenreList(genreList);
    }

    public void fetchMangaData() {
        mangaRepository.getMangaList(mangaList, isLoading, currentQuery, currentOrder, currentGenreIds, offset);
    }
}