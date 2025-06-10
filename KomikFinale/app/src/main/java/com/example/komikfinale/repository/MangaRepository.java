package com.example.komikfinale.repository;

import android.app.Application;
import android.util.Log;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.example.komikfinale.data.local.AppDatabase;
import com.example.komikfinale.data.local.MangaDao;
import com.example.komikfinale.data.remote.MangaApiService;
import com.example.komikfinale.data.remote.RetrofitClient;
import com.example.komikfinale.model.AtHomeServerResponse;
import com.example.komikfinale.model.Chapter;
import com.example.komikfinale.model.ChapterListResponse;
import com.example.komikfinale.model.Genre;
import com.example.komikfinale.model.GenreListResponse;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.model.MangaDetailResponse;
import com.example.komikfinale.model.MangaListResponse;
import java.util.List;
import java.util.Map;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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

    /**
     * Mengambil daftar manga dengan semua parameter: search, sort, filter, dan pagination.
     * @param query Teks pencarian.
     * @param order Map untuk sorting.
     * @param genreIds List ID genre untuk filter.
     * @param offset Parameter untuk halaman (pagination).
     */
    public void getMangaList(MutableLiveData<List<Manga>> mangaList, MutableLiveData<Boolean> isLoading, String query, Map<String, String> order, List<String> genreIds, int offset) {
        isLoading.setValue(true);
        String latestOrder = order.get("latestUploadedChapter");
        String titleOrder = order.get("title");

        apiService.getMangaList(20, offset, "cover_art", query, latestOrder, titleOrder, genreIds).enqueue(new Callback<MangaListResponse>() {
            @Override
            public void onResponse(Call<MangaListResponse> call, Response<MangaListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    mangaList.postValue(response.body().getData());
                } else {
                    Log.e("MangaRepository", "Response not successful. Code: " + response.code() + " Message: " + response.message());
                    mangaList.postValue(null);
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

    public void getGenreList(MutableLiveData<List<Genre>> genreList) {
        apiService.getGenreList().enqueue(new Callback<GenreListResponse>() {
            @Override
            public void onResponse(Call<GenreListResponse> call, Response<GenreListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    genreList.postValue(response.body().getData());
                } else {
                    genreList.postValue(null);
                }
            }
            @Override
            public void onFailure(Call<GenreListResponse> call, Throwable t) {
                Log.e("MangaRepository", "Gagal mengambil daftar genre: " + t.getMessage());
                genreList.postValue(null);
            }
        });
    }

    public void getMangaDetails(String mangaId, MutableLiveData<Manga> mangaDetail, MutableLiveData<Boolean> isLoading) {
        isLoading.setValue(true);
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

    public void getChapterList(String mangaId, MutableLiveData<List<Chapter>> chapterList) {
        String url = "https://api.mangadex.org/manga/" + mangaId + "/feed?order[chapter]=asc&translatedLanguage[]=en";
        apiService.getChapterList(url).enqueue(new Callback<ChapterListResponse>() {
            @Override
            public void onResponse(Call<ChapterListResponse> call, Response<ChapterListResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    chapterList.postValue(response.body().getData());
                } else {
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

    // --- METODE UNTUK DATABASE LOKAL (ROOM) ---

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
}