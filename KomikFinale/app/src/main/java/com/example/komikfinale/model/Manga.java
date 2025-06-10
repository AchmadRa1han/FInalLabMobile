package com.example.komikfinale.model;

import androidx.annotation.NonNull;
import androidx.room.Embedded;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Kelas Model untuk Manga. Bertindak sebagai POJO untuk data dari API (menggunakan Gson)
 * dan juga sebagai Entity untuk tabel database (menggunakan Room).
 */
@Entity(tableName = "manga_favorites")
public class Manga {

    // @PrimaryKey adalah kunci unik untuk setiap baris di tabel database.
    @PrimaryKey
    @NonNull
    @SerializedName("id")
    private String id;

    // @Embedded memungkinkan kita menyimpan objek lain (MangaAttributes)
    // seolah-olah field-fieldnya adalah kolom di tabel ini.
    @Embedded
    @SerializedName("attributes")
    private MangaAttributes attributes;

    // @Ignore memberitahu Room untuk tidak menyimpan field ini ke database,
    // karena kita hanya butuh ini saat mengambil data dari API untuk parsing gambar.
    @Ignore
    @SerializedName("relationships")
    private List<Relationship> relationships;

    // Field ini TIDAK di-ignore, agar Room menyimpannya di database.
    // Ini digunakan untuk menyimpan nama file gambar agar bisa ditampilkan saat offline.
    public String coverFilename;

    // Room memerlukan constructor kosong untuk membuat objek.
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

    // --- Kelas-kelas inner untuk data bersarang (nested) ---

    // Fokus ubah di dalam kelas ini
    public static class MangaAttributes {

        // UBAH INI: Tambahkan prefix "title_"
        @Embedded(prefix = "title_")
        @SerializedName("title")
        private Title title;

        // UBAH INI: Tambahkan prefix "description_"
        @Embedded(prefix = "description_")
        @SerializedName("description")
        private Description description;

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

    // Kelas untuk menampung data 'relationships' dari API
    public static class Relationship {
        @SerializedName("type")
        private String type;

        @SerializedName("attributes")
        private CoverAttributes attributes;

        public String getType() { return type; }
        public CoverAttributes getAttributes() { return attributes; }

        // Kelas untuk menampung nama file gambar sampul
        public static class CoverAttributes {
            @SerializedName("fileName")
            private String fileName;

            public String getFileName() { return fileName; }
        }
    }
}