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

    public String getDate() {
        return date;
    }

}