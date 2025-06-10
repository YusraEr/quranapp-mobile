package com.example.quranapp.data.remote.model;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class SurahResponse {

    @SerializedName("code")
    private int code;

    @SerializedName("message")
    private String message;

    @SerializedName("data")
    private List<Surah> data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public List<Surah> getData() {
        return data;
    }

    public void setData(List<Surah> data) {
        this.data = data;
    }
}
