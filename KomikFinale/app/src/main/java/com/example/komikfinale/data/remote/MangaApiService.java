package com.example.komikfinale.data.remote;

import androidx.annotation.Nullable;

import com.example.komikfinale.model.AtHomeServerResponse;
import com.example.komikfinale.model.ChapterListResponse;
import com.example.komikfinale.model.GenreListResponse;
import com.example.komikfinale.model.MangaDetailResponse;
import com.example.komikfinale.model.MangaListResponse;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface MangaApiService {

    /**
     * Mengambil daftar manga. Versi ini menggunakan parameter yang spesifik dan nullable
     * untuk stabilitas yang lebih baik.
     * @param title Judul yang dicari (opsional).
     * @param latest Parameter untuk sorting berdasarkan update terbaru (opsional).
     * @param titleOrder Parameter untuk sorting berdasarkan judul (opsional).
     */
    @GET("manga")
    Call<MangaListResponse> getMangaList(
            @Query("limit") int limit,
            @Query("includes[]") String coverArt,
            @Query("title") @Nullable String title,
            @Query("order[latestUploadedChapter]") @Nullable String latest,
            @Query("order[title]") @Nullable String titleOrder,
            @Query("includedTags[]") @Nullable List<String> includedTags // <-- TAMBAHKAN INI
    );

    @GET("manga/tag")
    Call<GenreListResponse> getGenreList();


    /**
     * Mengambil detail dari satu manga berdasarkan ID.
     */
    @GET("manga/{id}")
    Call<MangaDetailResponse> getMangaDetails(
            @Path("id") String mangaId,
            @Query("includes[]") String coverArt
    );

    /**
     * Mengambil daftar chapter. Menggunakan @Url karena URL-nya lebih kompleks
     * dengan parameter sorting dan bahasa yang sudah kita tentukan.
     */
    @GET
    Call<ChapterListResponse> getChapterList(@Url String url);

    /**
     * Mengambil informasi server dan halaman untuk satu chapter.
     */
    @GET("/at-home/server/{chapterId}")
    Call<AtHomeServerResponse> getChapterPages(@Path("chapterId") String chapterId);
}