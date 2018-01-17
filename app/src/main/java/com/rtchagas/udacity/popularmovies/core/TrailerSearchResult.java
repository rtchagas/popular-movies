package com.rtchagas.udacity.popularmovies.core;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;
import java.util.List;

public class TrailerSearchResult implements Serializable {

    private final static long serialVersionUID = 7081971646669960400L;

    @SerializedName("id")
    private int id;

    @SerializedName("results")
    private List<Trailer> trailers = null;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public List<Trailer> getTrailers() {
        return trailers;
    }

    public void setTrailers(List<Trailer> trailers) {
        this.trailers = trailers;
    }
}
