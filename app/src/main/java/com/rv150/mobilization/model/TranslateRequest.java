package com.rv150.mobilization.model;

/**
 * Created by ivan on 22.04.17.
 */

public class TranslateRequest {
    private String from;
    private String to;
    private String text;

    public TranslateRequest() {
    }

    public TranslateRequest(String from, String to, String text) {
        this.from = from;
        this.to = to;
        this.text = text;
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

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        TranslateRequest request = (TranslateRequest) o;

        if (!from.equals(request.from)) return false;
        if (!to.equals(request.to)) return false;
        return text.equals(request.text);

    }

    @Override
    public int hashCode() {
        int result = from.hashCode();
        result = 31 * result + to.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
}
