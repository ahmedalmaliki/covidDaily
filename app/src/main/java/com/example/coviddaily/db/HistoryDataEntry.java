package com.example.coviddaily.db;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "LastTenDaysData")
public class HistoryDataEntry {
    @PrimaryKey(autoGenerate = true)
    private int id;
    private String date;
    private String counts;

    public HistoryDataEntry(String date, String counts) {
        this.date = date;
        this.counts = counts;
    }

    public int getId() {
        return id;
    }

    public String getDate() {
        return date;
    }

    public String getCounts() {
        return counts;
    }

    public void setId(int id) {
        this.id = id;
    }
}
