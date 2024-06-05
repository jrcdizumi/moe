package com.example.moe;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class TraceMoeResponse {

    @SerializedName("frameCount")
    private int frameCount;

    @SerializedName("error")
    private String error;

    @SerializedName("result")
    private List<Result> result;

    // getters and setters
    public List<Result> getResult() {
        return result;
    }
}

class Result {
    @SerializedName("anilist")
    private int anilist;

    @SerializedName("filename")
    private String filename;

    @SerializedName("episode")
    private String episode;

    @SerializedName("from")
    private float from;

    @SerializedName("to")
    private float to;

    @SerializedName("similarity")
    private float similarity;

    @SerializedName("video")
    private String video;

    @SerializedName("image")
    private String image;

    // getters and setters

    public String getFilename() {
        return filename;
    }

    public String getEpisode() {
        return episode;
    }

    public float getFrom() {
        return from;
    }

    public float getTo() {
        return to;
    }

    public float getSimilarity() {
        return similarity;
    }

    public String getVideo() {
        return video;
    }

    public String getImage() {
        return image;
    }
}
