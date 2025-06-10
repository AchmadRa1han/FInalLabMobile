package com.example.komikfinale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ChapterListResponse {
    @SerializedName("data")
    private List<Chapter> data;

    public List<Chapter> getData() { return data; }
}