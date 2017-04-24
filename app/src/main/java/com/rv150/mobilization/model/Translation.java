package com.rv150.mobilization.model;

/**
 * Created by ivan on 24.04.17.
 */

public class Translation {
    private long id;
    private String from;
    private String to;
    private boolean favorite;

    public Translation() {
    }

    public Translation(String from, String to, boolean favorite) {
        this(-1, from, to, favorite);
    }

    public Translation(long id, String from, String to, boolean favorite) {
        this.id = id;
        this.from = from;
        this.to = to;
        this.favorite = favorite;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public boolean isFavorite() {
        return favorite;
    }

    public void setFavorite(boolean favorite) {
        this.favorite = favorite;
    }
}
