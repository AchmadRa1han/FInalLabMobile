package com.example.komikfinale.model;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class MangaListResponse {
    @SerializedName("data") private List<Manga> data;
    public List<Manga> getData() { return data; }
}