package com.rv150.mobilization.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Map;

/**
 * Created by ivan on 11.04.17.
 */

public class SupportedLanguages {
    @SerializedName("langs")
    @Expose
    private Map<String, String> langs = null;

    public Map<String, String> getLangs() {
        return langs;
    }

    public void setLangs(Map<String, String> langs) {
        this.langs = langs;
    }
}