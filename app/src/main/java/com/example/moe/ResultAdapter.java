package com.example.moe;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.MediaMetadataRetriever;
import android.media.MediaPlayer;
import android.net.Uri;
import android.util.Log;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import java.util.HashMap;
import java.util.List;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;

public class ResultAdapter extends RecyclerView.Adapter<ResultAdapter.ResultViewHolder> {

    private final List<Result> resultList;

    public ResultAdapter(List<Result> resultList) {
        this.resultList = resultList;
    }

    @NonNull
    @Override
    public ResultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.result_item, parent, false);
        return new ResultViewHolder(view);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(ResultViewHolder holder, int position) {
        Result result = resultList.get(position);
        Log.d("ResultAdapter", "Displaying result " + result);
        holder.filename.setText(result.getFilename());
        holder.episode.setText("Episode " + result.getEpisode());
        holder.similarity.setText("Similarity: " + result.getSimilarity());
        //from 和 to 是以秒为单位的，我们将其转换为分钟,同时显示秒数，如23：45
        float from = result.getFrom();
        float to = result.getTo();
        int fromMin = (int) from / 60;
        int fromSec = (int) from % 60;
        int toMin = (int) to / 60;
        int toSec = (int) to % 60;
        holder.from.setText(String.format("%02d:%02d", fromMin, fromSec));
        holder.to.setText(String.format("%02d:%02d", toMin, toSec));
        // Load image using Glide
        Glide.with(holder.image.getContext()).load(result.getImage()).into(holder.image);
        Glide.with(holder.image.getContext())
                .asBitmap()
                .load(result.getImage())
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        Drawable drawable = new BitmapDrawable(holder.image.getResources(), resource);
                        holder.videoView.setBackground(drawable);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle cleanup here
                    }
                });
//        holder.button.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Uri videoUri = Uri.parse(result.getVideo());
//                Intent intent = new Intent(Intent.ACTION_VIEW, videoUri);
//                v.getContext().startActivity(intent);
//            }
//        });
        holder.videoView.setVideoURI(Uri.parse(result.getVideo()));
        //holder.videoView.start();
    }

    @Override
    public int getItemCount() {
        return resultList.size();
    }

    public static class ResultViewHolder extends RecyclerView.ViewHolder {

        TextView filename;
        TextView episode;
        TextView similarity;
        TextView from;
        TextView to;
        //show an image
        ImageView image;
        // Define other views...
        Button button;
        VideoView videoView;
        FrameLayout frameLayout;

        GestureDetector gestureDetector;

        @SuppressLint("ClickableViewAccessibility")
        public ResultViewHolder(View itemView) {
            super(itemView);
            filename = itemView.findViewById(R.id.filename);
            episode = itemView.findViewById(R.id.episode);
            similarity = itemView.findViewById(R.id.similarity);
            from = itemView.findViewById(R.id.from);
            to = itemView.findViewById(R.id.to);
            image = itemView.findViewById(R.id.image);
//            button = itemView.findViewById(R.id.button);
            videoView = itemView.findViewById(R.id.videoView);
            frameLayout = itemView.findViewById(R.id.videoFrame);
            // Initialize other views...
            gestureDetector = new GestureDetector(itemView.getContext(), new GestureDetector.SimpleOnGestureListener() {
                @Override
                public boolean onDoubleTap(MotionEvent e) {
                    if (videoView.isPlaying()) {
                        videoView.pause();
                    } else {
                        videoView.start();
                    }
                    return true;
                }
            });

            videoView.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    gestureDetector.onTouchEvent(event);
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                @Override
                public void onPrepared(MediaPlayer mp) {
                    // Set the background of the VideoView to null
                    mp.setVolume(0, 0);
                    videoView.setBackground(null);
                    mp.start();
                }
            });
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    // Restart the video when it reaches the end
                    mp.start();
                }
            });
        }
    }
}