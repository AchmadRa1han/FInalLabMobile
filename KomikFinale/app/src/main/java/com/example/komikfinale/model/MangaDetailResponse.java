package com.example.komikfinale.model;
import com.google.gson.annotations.SerializedName;

public class MangaDetailResponse {
    @SerializedName("data")
    private Manga data;

    public Manga getData() {
        return data;
    }
}