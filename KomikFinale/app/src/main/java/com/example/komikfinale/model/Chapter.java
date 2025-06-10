package com.example.komikfinale.model;

import com.google.gson.annotations.SerializedName;

public class Chapter {
    @SerializedName("id")
    private String id;

    @SerializedName("attributes")
    private ChapterAttributes attributes;

    public String getId() { return id; }
    public ChapterAttributes getAttributes() { return attributes; }

    public static class ChapterAttributes {
        @SerializedName("chapter")
        private String chapter;

        @SerializedName("title")
        private String title;

        public String getChapter() { return chapter; }
        public String getTitle() { return title; }
    }
}