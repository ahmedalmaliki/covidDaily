package com.example.coviddaily.models;

import androidx.lifecycle.ViewModel;

import java.util.Date;

public class TodayCount {
    private String count;
    private String date;


    public TodayCount(String count, String date) {
        this.count = count;
        this.date = date;

    }

    public String getCount() {
        return count;
    }


    public void setCount(String count) {
        this.count = count;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}