package com.example.komikfinale.ui.home;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.repository.MangaRepository;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HomeViewModel extends AndroidViewModel {
    private final MangaRepository mangaRepository;
    private final MutableLiveData<List<Manga>> mangaList;
    private final MutableLiveData<Boolean> isLoading;

    // State untuk menyimpan kondisi sorting dan searching saat ini
    private String currentQuery = null; // null berarti tidak sedang mencari
    private final Map<String, String> currentOrder = new HashMap<>();

    public HomeViewModel(@NonNull Application application) {
        super(application);
        mangaRepository = MangaRepository.getInstance(application);
        mangaList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();

        // Mengatur urutan default saat pertama kali dibuka: Update Terbaru
        currentOrder.put("latestUploadedChapter", "desc");
        // Langsung ambil data dengan state default
        fetchMangaData();
    }

    // --- LiveData untuk diobservasi oleh Fragment ---
    public LiveData<List<Manga>> getMangaList() {
        return mangaList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    // --- Aksi yang bisa dipanggil dari Fragment ---

    /**
     * Mengatur query pencarian baru dan memicu pengambilan data ulang.
     * @param query Teks yang dicari. Beri nilai null untuk menghapus filter pencarian.
     */
    public void setQuery(String query) {
        this.currentQuery = query;
        fetchMangaData();
    }

    /**
     * Mengatur urutan (sorting) baru dan memicu pengambilan data ulang.
     * @param order Map yang berisi parameter order untuk API.
     */
    public void setOrder(Map<String, String> order) {
        this.currentOrder.clear();
        this.currentOrder.putAll(order);
        fetchMangaData();
    }

    /**
     * Method pusat untuk mengambil data dari repository berdasarkan state saat ini.
     */
    public void fetchMangaData() {
        mangaRepository.getMangaList(mangaList, isLoading, currentQuery, currentOrder);
    }
}