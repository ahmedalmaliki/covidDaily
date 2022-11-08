package com.example.coviddaily;

import android.app.Activity;

import androidx.lifecycle.LifecycleOwner;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.example.coviddaily.db.HistoryDataEntry;
import com.example.coviddaily.models.LastTenDaysCount;
import com.example.coviddaily.models.TodayCount;
import com.example.coviddaily.repositories.MainRepo;

import org.json.JSONException;

import java.util.ArrayList;
import java.util.List;

public class MainViewModel extends ViewModel implements MainRepo.RepoCallBack {
    private MutableLiveData<TodayCount> mTodayCount;
    private MutableLiveData<LastTenDaysCount> mLastTenDaysCount;
    private Activity activity;
    private static final String TAG = "MainModelViewDebug";

    public void init() throws JSONException {
        mTodayCount = new MutableLiveData<>();
        mLastTenDaysCount = new MutableLiveData<>();
        MainRepo mainRepo = MainRepo.getInstance(getActivity()); //getting a singleton instance of the repository.
        mainRepo.setRepoCallback(this); //setting the repository callback interface
        mainRepo.getDataFromRepo(); //if there is internet connection then make request to api, else: get data from DB



    }


    public LiveData<TodayCount> getTodayCount() {
        return mTodayCount;
    }
    public LiveData<LastTenDaysCount> getLastTenDaysCount() {
        return mLastTenDaysCount;
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }


    @Override
    public void getHistoryLiveData(LiveData<List<HistoryDataEntry>> liveData) throws JSONException {
        //gets the data of the last 10 days
        liveData.observe((LifecycleOwner) activity, new Observer<List<HistoryDataEntry>>() {
            @Override
            public void onChanged(List<HistoryDataEntry> list) {
                try {
                    setHistoryCounts(list);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        });


    }

    @Override
    public void getCurrentDayCountAndDate(String count, String date) {
        //getting latest count update
        mTodayCount.setValue(new TodayCount(count, date));
    }


    public void setHistoryCounts(List<HistoryDataEntry> list) throws JSONException {
            mLastTenDaysCount.setValue(new LastTenDaysCount(getLastTenDaysDailyCount(list), getLastTenDaysDates(list)));

    }

    private ArrayList<String> getLastTenDaysDates(List<HistoryDataEntry> list) throws JSONException {
        //returns a list of the last ten days dates
        ArrayList<String> dates = new ArrayList<>();
        for (int i = 0; i < list.size() ; i++ ){
            dates.add(String.valueOf(list.get(i).getDate()));
        }
        return dates;
    }

    private ArrayList<String> getLastTenDaysDailyCount(List<HistoryDataEntry> list) throws JSONException {
        //returns a list of the last ten days count
        ArrayList<String> counts = new ArrayList<>();
        for (int i = 0; i < list.size() ; i++ ){
            counts.add(String.valueOf(list.get(i).getCounts()));
        }
        return counts;
    }



}
