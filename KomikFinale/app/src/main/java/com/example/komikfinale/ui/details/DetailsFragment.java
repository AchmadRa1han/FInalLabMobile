package com.example.komikfinale.ui.details;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.komikfinale.R;
import com.example.komikfinale.model.Manga;
import com.example.komikfinale.ui.adapter.ChapterAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.ArrayList;

public class DetailsFragment extends Fragment {

    private DetailsViewModel viewModel;
    private TextView tvTitle, tvDescription;
    private ImageView ivCover;
    private ProgressBar progressBar;
    private FloatingActionButton fabFavorite;
    private RecyclerView rvChapters;
    private ChapterAdapter chapterAdapter;

    private String mangaId;
    private Manga currentManga;
    private boolean isFavorite = false;

    public DetailsFragment() {
        super(R.layout.fragment_details);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (getArguments() != null) {
            mangaId = getArguments().getString("mangaId");
        }

        bindViews(view);
        setupChapterRecyclerView();

        viewModel = new ViewModelProvider(this).get(DetailsViewModel.class);

        if (mangaId != null) {
            viewModel.fetchMangaDetails(mangaId);
            viewModel.fetchChapterList(mangaId);
            observeViewModel();
        }
    }

    private void bindViews(View view) {
        tvTitle = view.findViewById(R.id.tv_detail_title);
        tvDescription = view.findViewById(R.id.tv_detail_description);
        ivCover = view.findViewById(R.id.iv_detail_cover);
        progressBar = view.findViewById(R.id.progress_bar_detail);
        fabFavorite = view.findViewById(R.id.fab_favorite);
        rvChapters = view.findViewById(R.id.rv_chapters);
    }

    private void setupChapterRecyclerView() {
        chapterAdapter = new ChapterAdapter(getContext(), new ArrayList<>());
        rvChapters.setAdapter(chapterAdapter);
        // Kita tidak perlu set LayoutManager karena sudah diatur di XML
    }

    private void observeViewModel() {
        viewModel.getIsLoading().observe(getViewLifecycleOwner(), isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        });

        viewModel.getMangaDetail().observe(getViewLifecycleOwner(), manga -> {
            if (manga != null) {
                this.currentManga = manga;
                tvTitle.setText(manga.getAttributes().getTitle().getEn());

                if (manga.getAttributes().getDescription() != null) {
                    tvDescription.setText(manga.getAttributes().getDescription().getEn());
                } else {
                    tvDescription.setText("Deskripsi tidak tersedia.");
                }

                String coverFilename = manga.coverFilename;
                if (coverFilename == null && manga.getRelationships() != null) {
                    for (Manga.Relationship rel : manga.getRelationships()) {
                        if ("cover_art".equals(rel.getType())) {
                            if (rel.getAttributes() != null) {
                                coverFilename = rel.getAttributes().getFileName();
                                currentManga.coverFilename = coverFilename;
                                break;
                            }
                        }
                    }
                }

                if (coverFilename != null) {
                    String coverUrl = "https://uploads.mangadex.org/covers/" + manga.getId() + "/" + coverFilename;
                    Glide.with(requireContext()).load(coverUrl).placeholder(R.drawable.ic_image_placeholder).into(ivCover);
                } else {
                    ivCover.setImageResource(R.drawable.ic_image_placeholder);
                }
            }
        });

        viewModel.getChapterList().observe(getViewLifecycleOwner(), chapters -> {
            if (chapters != null && !chapters.isEmpty()) {
                chapterAdapter.updateChapters(chapters);
            }
        });

        viewModel.getIsFavoriteStatus(mangaId).observe(getViewLifecycleOwner(), favoriteManga -> {
            isFavorite = (favoriteManga != null);
            if (isFavorite) {
                fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_filled));
            } else {
                fabFavorite.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_favorite_border));
            }
            setupFavoriteButtonListener();
        });
    }

    private void setupFavoriteButtonListener() {
        fabFavorite.setOnClickListener(v -> {
            if (currentManga == null) return;
            if (isFavorite) {
                viewModel.removeFromFavorites(currentManga.getId());
            } else {
                viewModel.addToFavorites(currentManga);
            }
        });
    }
}