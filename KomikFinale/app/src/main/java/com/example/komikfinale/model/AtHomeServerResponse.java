package com.example.komikfinale.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class AtHomeServerResponse {
    @SerializedName("baseUrl")
    private String baseUrl;

    @SerializedName("chapter")
    private ChapterData chapter;

    public String getBaseUrl() { return baseUrl; }
    public ChapterData getChapter() { return chapter; }

    public static class ChapterData {
        @SerializedName("hash")
        private String hash;

        @SerializedName("data")
        private List<String> data; // Daftar nama file halaman

        public String getHash() { return hash; }
        public List<String> getData() { return data; }
    }
}