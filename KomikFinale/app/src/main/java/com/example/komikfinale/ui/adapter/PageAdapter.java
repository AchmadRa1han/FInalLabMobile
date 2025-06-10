package com.example.komikfinale.ui.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.example.komikfinale.R;
import java.util.List;

public class PageAdapter extends RecyclerView.Adapter<PageAdapter.PageViewHolder> {

    private final List<String> pageUrls;
    private final Context context;
    private final View.OnClickListener clickListener; // <-- TAMBAHKAN INI

    // UBAH CONSTRUCTOR UNTUK MENERIMA ONCLICKLISTENER
    public PageAdapter(Context context, List<String> pageUrls, View.OnClickListener clickListener) {
        this.context = context;
        this.pageUrls = pageUrls;
        this.clickListener = clickListener; // <-- SIMPAN LISTENERNYA
    }

    @NonNull
    @Override
    public PageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.list_item_page, parent, false);
        return new PageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PageViewHolder holder, int position) {
        // Atur listener untuk setiap halaman gambar
        holder.itemView.setOnClickListener(clickListener);

        Glide.with(context)
                .load(pageUrls.get(position))
                .placeholder(R.drawable.ic_image_placeholder)
                .into(holder.ivPage);
    }

    @Override
    public int getItemCount() {
        return pageUrls.size();
    }

    static class PageViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPage;
        public PageViewHolder(@NonNull View itemView) {
            super(itemView);
            ivPage = itemView.findViewById(R.id.iv_page);
        }
    }
}