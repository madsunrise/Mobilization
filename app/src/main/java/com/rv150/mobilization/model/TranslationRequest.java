package com.rv150.mobilization.model;

/**
 * Created by ivan on 22.04.17.
 */

public class TranslationRequest {
    private String fromCode;
    private String toCode;
    private String text;

    public TranslationRequest() {
    }

    public TranslationRequest(String fromCode, String toCode, String text) {
        this.fromCode = fromCode;
        this.toCode = toCode;
        this.text = text;
    }

    public String getFromCode() {
        return fromCode;
    }

    public void setFromCode(String fromCode) {
        this.fromCode = fromCode;
    }

    public String getToCode() {
        return toCode;
    }

    public void setToCode(String toCode) {
        this.toCode = toCode;
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

        TranslationRequest request = (TranslationRequest) o;

        if (!fromCode.equals(request.fromCode)) return false;
        if (!toCode.equals(request.toCode)) return false;
        return text.equals(request.text);

    }

    @Override
    public int hashCode() {
        int result = fromCode.hashCode();
        result = 31 * result + toCode.hashCode();
        result = 31 * result + text.hashCode();
        return result;
    }
}
