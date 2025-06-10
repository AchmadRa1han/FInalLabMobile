package com.example.komikfinale.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.komikfinale.data.local.AppDatabase;
import com.example.komikfinale.data.local.MangaDao;
import com.example.komikfinale.data.remote.MangaApiService;
import com.example.komikfinale.data.remote.RetrofitClient;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.model.MangaDetailResponse;
import com.example.komikfinale.model.MangaListResponse;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import com.example.komikfinale.model.Chapter;
import com.example.komikfinale.model.ChapterListResponse;

public class MangaRepository {
    private static MangaRepository instance;
    private final MangaApiService apiService;
    private final MangaDao mangaDao;

    public static synchronized MangaRepository getInstance(Application application) {
        if (instance == null) {
            instance = new MangaRepository(application);
        }
        return instance;
    }

    private MangaRepository(Application application) {
        apiService = RetrofitClient.getClient().create(MangaApiService.class);
        AppDatabase db = AppDatabase.getDatabase(application);
        mangaDao = db.mangaDao();
    }

    // --- METODE UNTUK API (NETWORK) ---

    public void getMangaList(MutableLiveData<List<Manga>> mangaList, MutableLiveData<Boolean> isLoading) {
        isLoading.setValue(true);
        // Panggilan ini sekarang sudah benar dan cocok dengan ApiService
        apiService.getMangaList("cover_art", 40).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mangaList.postValue(response.body().getData());
                }
                isLoading.postValue(false);
            }
            @Override
            public void onFailure(Call<MangaListResponse> call, Throwable t) {
                Log.e("MangaRepository", "Gagal mengambil daftar manga: " + t.getMessage());
                mangaList.postValue(null);
                isLoading.postValue(false);
            }
        });
    }

    public void getMangaDetails(String mangaId, MutableLiveData<Manga> mangaDetail, MutableLiveData<Boolean> isLoading) {
        isLoading.setValue(true);
        // Panggilan ini sekarang sudah benar, tanpa parameter limit
        apiService.getMangaDetails(mangaId, "cover_art").enqueue(new Callback<MangaDetailResponse>() {
            @Override
            public void onResponse(Call<MangaDetailResponse> call, Response<MangaDetailResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mangaDetail.postValue(response.body().getData());
                }
                isLoading.postValue(false);
            }
            @Override
            public void onFailure(Call<MangaDetailResponse> call, Throwable t) {
                Log.e("MangaRepository", "Gagal mengambil detail: " + t.getMessage());
                mangaDetail.postValue(null);
                isLoading.postValue(false);
            }
        });
    }

    // --- METODE UNTUK DATABASE LOKAL (ROOM) ---
    // (Tidak ada perubahan di bagian ini)

    public LiveData<List<Manga>> getAllFavorites() {
        return mangaDao.getAllFavoriteManga();
    }
    public LiveData<Manga> getIsFavorite(String mangaId) {
        return mangaDao.getFavoriteMangaById(mangaId);
    }
    public void insertFavorite(Manga manga) {
        AppDatabase.databaseWriteExecutor.execute(() -> mangaDao.insert(manga));
    }
    public void deleteFavorite(String mangaId) {
        AppDatabase.databaseWriteExecutor.execute(() -> mangaDao.deleteById(mangaId));
    }
    // Method baru untuk mengambil daftar chapter
    // Di dalam kelas MangaRepository.java

    public void getChapterList(String mangaId, MutableLiveData<List<Chapter>> chapterList) {
        // --- UBAH BARIS URL DI BAWAH INI ---
        // Tambahkan '&translatedLanguage[]=id' di akhir URL
        String url = "https://api.mangadex.org/manga/" + mangaId + "/feed?order[chapter]=asc&translatedLanguage[]=en";

        // Sisa kodenya sama persis
        apiService.getChapterList(url).enqueue(new Callback<ChapterListResponse>() {
            @Override
            public void onResponse(Call<ChapterListResponse> call, Response<ChapterListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chapterList.postValue(response.body().getData());
                } else {
                    // Tambahkan ini untuk melihat jika ada error dari server
                    Log.e("MangaRepository", "Error getting chapters: " + response.code() + " - " + response.message());
                    chapterList.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<ChapterListResponse> call, Throwable t) {
                Log.e("MangaRepository", "Gagal mengambil chapter: " + t.getMessage());
                chapterList.postValue(null);
            }
        });
    }
}