package com.example.komikfinale.data.local;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import com.example.komikfinale.model.Manga;
import java.util.List;

@Dao
public interface MangaDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insert(Manga manga);

    @Query("DELETE FROM manga_favorites WHERE id = :mangaId")
    void deleteById(String mangaId);

    @Query("SELECT * FROM manga_favorites WHERE id = :mangaId")
    LiveData<Manga> getFavoriteMangaById(String mangaId);

    @Query("SELECT * FROM manga_favorites")
    LiveData<List<Manga>> getAllFavoriteManga();
}