package com.example.komikfinale.ui.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.example.komikfinale.R;
import com.example.komikfinale.model.Chapter;
import com.example.komikfinale.ui.reader.ReaderActivity;
import java.util.ArrayList;
import java.util.List;

public class ChapterAdapter extends RecyclerView.Adapter<ChapterAdapter.ChapterViewHolder> {
    private final List<Chapter> chapterList;
    private final Context context;

    public ChapterAdapter(Context context, List<Chapter> chapterList) {
        this.context = context;
        this.chapterList = chapterList;
    }

    @NonNull
    @Override
    public ChapterViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_chapter, parent, false);
        return new ChapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ChapterViewHolder holder, int position) {
        Chapter chapter = chapterList.get(position);
        Chapter.ChapterAttributes attributes = chapter.getAttributes();

        String chapterNumber = attributes.getChapter();
        String chapterTitle = attributes.getTitle();

        // Membuat teks tampilan yang lebih informatif
        String displayText = "Chapter " + chapterNumber;
        if (chapterTitle != null && !chapterTitle.isEmpty()) {
            displayText += ": " + chapterTitle;
        }

        holder.tvChapterTitle.setText(displayText);

        // Menangani klik pada setiap item chapter
        holder.itemView.setOnClickListener(v -> {
            // 1. Kumpulkan semua ID chapter dari daftar untuk fitur Next/Prev di Reader
            ArrayList<String> chapterIds = new ArrayList<>();
            for (Chapter ch : chapterList) {
                chapterIds.add(ch.getId());
            }

            // 2. Ambil data spesifik dari chapter yang diklik untuk ditampilkan di judul Reader
            Chapter clickedChapter = chapterList.get(position);
            String clickedChapterNumber = clickedChapter.getAttributes().getChapter();
            String clickedChapterTitle = clickedChapter.getAttributes().getTitle();

            // 3. Buat Intent untuk membuka ReaderActivity
            Intent intent = new Intent(context, ReaderActivity.class);

            // 4. Kirim SEMUA data yang diperlukan ke ReaderActivity
            intent.putStringArrayListExtra(ReaderActivity.EXTRA_CHAPTER_IDS, chapterIds);
            intent.putExtra(ReaderActivity.EXTRA_CHAPTER_POSITION, position);
            intent.putExtra(ReaderActivity.EXTRA_CHAPTER_NUMBER, clickedChapterNumber);
            intent.putExtra(ReaderActivity.EXTRA_CHAPTER_TITLE, clickedChapterTitle);

            // 5. Mulai Activity
            context.startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return chapterList.size();
    }

    // Method helper untuk mengupdate daftar chapter dari fragment
    public void updateChapters(List<Chapter> newChapters) {
        chapterList.clear();
        chapterList.addAll(newChapters);
        notifyDataSetChanged();
    }

    // ViewHolder untuk setiap item chapter
    static class ChapterViewHolder extends RecyclerView.ViewHolder {
        TextView tvChapterTitle;
        public ChapterViewHolder(@NonNull View itemView) {
            super(itemView);
            tvChapterTitle = itemView.findViewById(R.id.tv_chapter_title);
        }
    }
}