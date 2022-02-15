package com.example.moviefilm.detailFilm.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.moviefilm.R;
import com.example.moviefilm.base.OnClickVideoListener;
import com.example.moviefilm.detailFilm.models.Video;

import java.util.List;

public class VideoTrailerAdapter extends RecyclerView.Adapter<VideoTrailerAdapter.ViewHolder> {
    private List<Video> videoList;
    private Context context;
    private OnClickVideoListener onClickVideoListener;

    public VideoTrailerAdapter(List<Video> videoList, Context context, OnClickVideoListener onClickVideoListener) {
        this.videoList = videoList;
        this.context = context;
        this.onClickVideoListener = onClickVideoListener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_video_trailer, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Video video = videoList.get(position);
//        holder.videoView.setImageResource(video.get);

    }

    @Override
    public int getItemCount() {
        return videoList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        ImageView videoView;
        ImageView btnStart;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            videoView = itemView.findViewById(R.id.video_view);
            btnStart = itemView.findViewById(R.id.btn_start_adapter);
            btnStart.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickVideoListener.OnClickStart(getAdapterPosition());
                }
            });
        }
    }
}