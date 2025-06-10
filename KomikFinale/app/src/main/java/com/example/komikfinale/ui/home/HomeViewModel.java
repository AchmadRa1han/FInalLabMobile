package com.example.komikfinale.ui.home;

import android.app.Application; // PERUBAHAN DI SINI
import androidx.annotation.NonNull; // PERUBAHAN DI SINI
import androidx.lifecycle.AndroidViewModel; // PERUBAHAN DI SINI
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.repository.MangaRepository;
import java.util.List;

// PERUBAHAN DI SINI: Ganti 'ViewModel' menjadi 'AndroidViewModel'
public class HomeViewModel extends AndroidViewModel {
    private MangaRepository mangaRepository;
    private MutableLiveData<List<Manga>> mangaList;
    private MutableLiveData<Boolean> isLoading;

    // PERUBAHAN DI SINI: Constructor sekarang menerima Application
    public HomeViewModel(@NonNull Application application) {
        super(application); // Panggil super constructor

        // PERUBAHAN DI SINI: Panggil getInstance dengan application context
        mangaRepository = MangaRepository.getInstance(application);
        mangaList = new MutableLiveData<>();
        isLoading = new MutableLiveData<>();
        fetchMangaData();
    }

    public LiveData<List<Manga>> getMangaList() {
        return mangaList;
    }

    public LiveData<Boolean> getIsLoading() {
        return isLoading;
    }

    public void fetchMangaData() {
        mangaRepository.getMangaList(mangaList, isLoading);
    }
}