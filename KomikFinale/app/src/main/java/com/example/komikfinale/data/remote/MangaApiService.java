package com.example.komikfinale.data.remote;

import com.example.komikfinale.model.AtHomeServerResponse;
import com.example.komikfinale.model.ChapterListResponse;
import com.example.komikfinale.model.MangaDetailResponse;
import com.example.komikfinale.model.MangaListResponse;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * Interface ini mendefinisikan semua endpoint API yang akan digunakan dalam aplikasi.
 */
public interface MangaApiService {

    /**
     * Mengambil daftar manga.
     * @param coverArt dengan nilai "cover_art", meminta data gambar sampul.
     * @param limit jumlah manga yang ingin diambil.
     * @return sebuah objek Call yang berisi MangaListResponse.
     */
    @GET("manga")
    Call<MangaListResponse> getMangaList(
            @Query("includes[]") String coverArt,
            @Query("limit") int limit // <-- Parameter limit ada di sini
    );

    /**
     * Mengambil informasi detail dari satu manga spesifik berdasarkan ID-nya.
     * @param mangaId ID dari manga yang ingin diambil detailnya.
     * @param coverArt dengan nilai "cover_art", meminta data gambar sampul.
     * @return sebuah objek Call yang berisi MangaDetailResponse.
     */
    @GET("manga/{id}")
    Call<MangaDetailResponse> getMangaDetails(
            @Path("id") String mangaId,
            @Query("includes[]") String coverArt // <-- Hapus parameter limit dari sini
    );
    @GET
    Call<ChapterListResponse> getChapterList(@Url String url);

    @GET("/at-home/server/{chapterId}")
    Call<AtHomeServerResponse> getChapterPages(@Path("chapterId") String chapterId);

}