package com.example.coviddaily.models;

import java.util.ArrayList;

public class LastTenDaysCount {
    private ArrayList<String> counts ;
    private ArrayList<String> dates ;

    public LastTenDaysCount(ArrayList<String> counts, ArrayList<String> dates) {
        this.counts = counts;
        this.dates = dates;
    }

    public ArrayList<String> getCounts() {
        return counts;
    }

    public ArrayList<String> getDates() {
        return dates;
    }
}
