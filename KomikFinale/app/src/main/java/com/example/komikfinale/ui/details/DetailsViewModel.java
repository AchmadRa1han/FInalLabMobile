package com.example.komikfinale.ui.details;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.example.komikfinale.model.Chapter;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.repository.MangaRepository;

import java.util.List;

public class DetailsViewModel extends AndroidViewModel {
    private final MangaRepository mangaRepository;
    private final MutableLiveData<Manga> mangaDetail = new MutableLiveData<>();
    private final MutableLiveData<Boolean> isLoading = new MutableLiveData<>();
    private MutableLiveData<List<Chapter>> chapterList = new MutableLiveData<>();

    public LiveData<List<Chapter>> getChapterList() {
        return chapterList;
    }

    public DetailsViewModel(@NonNull Application application) {
        super(application);
        // Mengambil satu-satunya instance dari MangaRepository
        mangaRepository = MangaRepository.getInstance(application);
    }

    // --- LiveData untuk diobservasi oleh Fragment ---

    public LiveData<Manga> getMangaDetail() {
        return mangaDetail;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // LiveData untuk mengecek status favorit
    public LiveData<Manga> getIsFavoriteStatus(String mangaId) {
        return mangaRepository.getIsFavorite(mangaId);
    }

    // --- Aksi yang bisa dipanggil dari Fragment ---

    // Memulai proses pengambilan data detail manga dari repository
    public void fetchMangaDetails(String mangaId) {
        mangaRepository.getMangaDetails(mangaId, mangaDetail, isLoading);
    }

    public void fetchChapterList(String mangaId) {
        mangaRepository.getChapterList(mangaId, chapterList);
    }

    // Menambahkan manga ke daftar favorit
    public void addToFavorites(Manga manga) {
        mangaRepository.insertFavorite(manga);
    }

    // Menghapus manga dari daftar favorit
    public void removeFromFavorites(String mangaId) {
        mangaRepository.deleteFavorite(mangaId);
    }
}