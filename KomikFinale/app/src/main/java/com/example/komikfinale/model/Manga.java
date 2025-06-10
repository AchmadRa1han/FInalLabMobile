package com.example.komikfinale.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

@Entity(tableName = "manga_favorites")
public class Manga {

    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    @Embedded
    @SerializedName("attributes")
    private MangaAttributes attributes;

    @Ignore
    @SerializedName("relationships")
    private List<Relationship> relationships;

    public String coverFilename;

    public Manga() {
        // Diperlukan oleh Room
    }

    // --- Getter dan Setter ---

    @NonNull
    public String getId() { return id; }
    public void setId(@NonNull String id) { this.id = id; }
    public MangaAttributes getAttributes() { return attributes; }
    public void setAttributes(MangaAttributes attributes) { this.attributes = attributes; }
    public List<Relationship> getRelationships() { return relationships; }
    public void setRelationships(List<Relationship> relationships) { this.relationships = relationships; }
    public String getCoverFilename() { return coverFilename; }
    public void setCoverFilename(String coverFilename) { this.coverFilename = coverFilename; }

    // --- Kelas-kelas inner ---

    public static class MangaAttributes {

        @Embedded(prefix = "title_")
        @SerializedName("title")
        private Title title;

        @Embedded(prefix = "description_")
        @SerializedName("description")
        private Description description;

        // --- PERBAIKAN DI BAGIAN INI ---

        // 1. Tambahkan anotasi @Ignore agar Room tidak mencoba menyimpan List ini
        @Ignore
        @SerializedName("tags")
        private List<Tag> tags;

        // 2. Tambahkan Getter dan Setter untuk tags
        public List<Tag> getTags() { return tags; }
        public void setTags(List<Tag> tags) { this.tags = tags; }

        // --- AKHIR DARI PERBAIKAN ---

        public Title getTitle() { return title; }
        public void setTitle(Title title) { this.title = title; }
        public Description getDescription() { return description; }
        public void setDescription(Description description) { this.description = description; }
    }

    public static class Title {
        @SerializedName("en")
        private String en;
        public String getEn() { return en; }
        public void setEn(String en) { this.en = en; }
    }

    public static class Description {
        @SerializedName("en")
        private String en;
        public String getEn() { return en; }
        public void setEn(String en) { this.en = en; }
    }

    // 3. Pastikan kelas Tag didefinisikan di sini
    public static class Tag {
        @SerializedName("attributes")
        private TagAttributes attributes;
        public TagAttributes getAttributes() { return attributes; }

        public static class TagAttributes {
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

    public static class Relationship {
        @SerializedName("type")
        private String type;
        @SerializedName("attributes")
        private CoverAttributes attributes;
        public String getType() { return type; }
        public CoverAttributes getAttributes() { return attributes; }

        public static class CoverAttributes {
            @SerializedName("fileName")
            private String fileName;
            public String getFileName() { return fileName; }
        }
    }
}