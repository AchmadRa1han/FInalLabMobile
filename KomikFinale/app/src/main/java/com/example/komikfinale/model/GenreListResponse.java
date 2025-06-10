package com.example.komikfinale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class GenreListResponse {
    @SerializedName("data")
    private List<Genre> data;
    public List<Genre> getData() { return data; }
}