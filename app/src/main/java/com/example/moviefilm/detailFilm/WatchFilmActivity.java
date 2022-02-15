package com.example.moviefilm.detailFilm;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.moviefilm.R;
import com.example.moviefilm.databinding.ActivityWatchFilmBinding;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.YouTubePlayer;
import com.pierfrancescosoffritti.androidyoutubeplayer.core.player.listeners.AbstractYouTubePlayerListener;

public class WatchFilmActivity extends AppCompatActivity {
    ActivityWatchFilmBinding binding;
    private String videoId = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_watch_film);
        getData();
        getLifecycle().addObserver(binding.youtubePlayerView);
        binding.youtubePlayerView.exitFullScreen();
        binding.youtubePlayerView.addYouTubePlayerListener(new AbstractYouTubePlayerListener() {
            @Override
            public void onReady(@NonNull YouTubePlayer youTubePlayer) {
                super.onReady(youTubePlayer);
                youTubePlayer.loadVideo(videoId.equals("") ? "kim9qcN3CqE" : videoId, 0);
            }
        });
    }

    private void getData() {
        Bundle bundle = getIntent().getExtras();
        if (bundle != null)
            videoId = bundle.getString(DetailFilmActivity.VIDEO_ID, "");
    }

    @Override
    protected void onStop() {
        super.onStop();
        binding.youtubePlayerView.release();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding.youtubePlayerView.release();
    }
}