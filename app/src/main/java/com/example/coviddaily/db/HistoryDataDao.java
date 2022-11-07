package com.example.coviddaily.db;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.HashMap;
import java.util.List;

@Dao
public interface HistoryDataDao {
    @Insert
    void insert(HistoryDataEntry historyDataEntry);
    @Update
    void update(HistoryDataEntry historyDataEntry);
    @Delete
    void delete(HistoryDataEntry historyDataEntry);
    @Query("DELETE From LastTenDaysData")
    void deleteAllHistoryData();
    @Query("SELECT * From LastTenDaysData ")
    LiveData<List<HistoryDataEntry>> getAllHistoryData();
}
