package com.example.komikfinale.ui.adapter;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.navigation.Navigation;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.komikfinale.R;
import com.example.komikfinale.model.Manga;

import java.util.List;

public class MangaAdapter extends RecyclerView.Adapter<MangaAdapter.MangaViewHolder> {

    private final List<Manga> mangaList;
    private final Context context;
    private final int currentFragmentId; // Untuk menyimpan dari fragment mana adapter ini dipanggil

    /**
     * Constructor yang sudah diperbarui untuk menerima ID fragment.
     * @param context Context dari fragment.
     * @param mangaList Daftar manga yang akan ditampilkan.
     * @param fragmentId ID dari fragment (contoh: R.id.homeFragment).
     */
    public MangaAdapter(Context context, List<Manga> mangaList, int fragmentId) {
        this.context = context;
        this.mangaList = mangaList;
        this.currentFragmentId = fragmentId;
    }

    @NonNull
    @Override
    public MangaViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_manga, parent, false);
        return new MangaViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MangaViewHolder holder, int position) {
        Manga manga = mangaList.get(position);

        // Mengatur judul manga
        if (manga.getAttributes() != null && manga.getAttributes().getTitle() != null) {
            holder.tvTitle.setText(manga.getAttributes().getTitle().getEn());
        }

        // --- LOGIKA CERDAS UNTUK MENAMPILKAN GAMBAR ---
        String coverFilename = manga.coverFilename; // 1. Coba ambil nama file dari field yang sudah disimpan (untuk data dari database)

        // 2. Jika kosong (misal, data dari API), cari dari 'relationships'
        if (coverFilename == null && manga.getRelationships() != null) {
            for (Manga.Relationship rel : manga.getRelationships()) {
                if ("cover_art".equals(rel.getType())) {
                    if (rel.getAttributes() != null) {
                        coverFilename = rel.getAttributes().getFileName();
                        manga.coverFilename = coverFilename; // Penting: Simpan ke objek untuk penggunaan nanti (saat difavoritkan)
                        break;
                    }
                }
            }
        }

        // 3. Tampilkan gambar jika nama file ada
        if (coverFilename != null) {
            String coverUrl = "https://uploads.mangadex.org/covers/" + manga.getId() + "/" + coverFilename + ".256.jpg";
            Glide.with(context)
                    .load(coverUrl)
                    .placeholder(R.drawable.ic_image_placeholder)
                    .error(R.drawable.ic_image_placeholder)
                    .into(holder.ivCover);
        } else {
            // 4. Jika tetap tidak ada, tampilkan placeholder
            holder.ivCover.setImageResource(R.drawable.ic_image_placeholder);
        }
        // --- AKHIR DARI LOGIKA GAMBAR ---


        // --- LOGIKA NAVIGASI DINAMIS ---
        holder.itemView.setOnClickListener(v -> {
            Bundle bundle = new Bundle();
            bundle.putString("mangaId", manga.getId());

            // Pilih action navigasi yang benar berdasarkan dari fragment mana ia dipanggil
            if (currentFragmentId == R.id.homeFragment) {
                Navigation.findNavController(v).navigate(R.id.action_homeFragment_to_detailsFragment, bundle);
            } else if (currentFragmentId == R.id.libraryFragment) {
                Navigation.findNavController(v).navigate(R.id.action_libraryFragment_to_detailsFragment, bundle);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mangaList.size();
    }

    public void updateMangaList(List<Manga> newMangaList) {
        this.mangaList.clear();
        this.mangaList.addAll(newMangaList);
        notifyDataSetChanged();
    }

    public static class MangaViewHolder extends RecyclerView.ViewHolder {
        ImageView ivCover;
        TextView tvTitle;

        public MangaViewHolder(@NonNull View itemView) {
            super(itemView);
            ivCover = itemView.findViewById(R.id.iv_manga_cover);
            tvTitle = itemView.findViewById(R.id.tv_manga_title);
        }
    }
}