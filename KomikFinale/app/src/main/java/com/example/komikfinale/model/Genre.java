package com.example.komikfinale.model;

import com.google.gson.annotations.SerializedName;

public class Genre {
    @SerializedName("id")
    private String id;
    @SerializedName("attributes")
    private Attributes attributes;

    public String getId() { return id; }
    public Attributes getAttributes() { return attributes; }

    public static class Attributes {
        @SerializedName("name")
        private Name name;
        public Name getName() { return name; }
    }

    public static class Name {
        @SerializedName("en")
        private String en;
        public String getEn() { return en; }
    }
}