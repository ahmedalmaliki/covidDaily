package com.example.coviddaily;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.coviddaily.models.LastTenDaysCount;
import com.example.coviddaily.models.TodayCount;
import com.example.coviddaily.repositories.MainRepo;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class MainViewModel extends ViewModel implements MainRepo.VolleyCallback {
    private MutableLiveData<TodayCount> mTodayCount;
    private MutableLiveData<LastTenDaysCount> mLastTenDaysCount;
    private MainRepo mainRepo;
    private Activity activity;
    private static final String TAG = "MainModelViewDebug";

    public void init(){

        mainRepo = MainRepo.getInstance();
        mainRepo.setActivity(getActivity());
        mainRepo.setVolleyCallback(this);
        mainRepo.GetInfoFromApi();
        mTodayCount = new MutableLiveData<>();
        mLastTenDaysCount = new MutableLiveData<>();


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
    public void requestSuccessAlert(JSONArray jsonArray) throws JSONException {
      setCounts(jsonArray);

    }

    public void setCounts(JSONArray jsonArray) throws JSONException {

        JSONObject jsonObject = jsonArray.getJSONObject(0);


        try {
            mTodayCount.setValue(new TodayCount(getNewCases(jsonObject), getLatestUpdatedDate(jsonObject)));
            mLastTenDaysCount.setValue(new LastTenDaysCount(getLastTenDaysDailyCount(jsonArray), getLastTenDaysDates(jsonArray)));

        }catch (Exception e){
            Log.d(TAG, e.toString());
        }


    }

    private ArrayList<String> getLastTenDaysDates(JSONArray jsonArray) throws JSONException {
        ArrayList<String> dates = new ArrayList<>();
        for (int i = 1; i <= 10 ; i++ ){
            dates.add(String.valueOf(jsonArray.getJSONObject(i).get("date")));
        }
        return dates;
    }

    private ArrayList<String> getLastTenDaysDailyCount(JSONArray jsonArray) throws JSONException {
        ArrayList<String> counts = new ArrayList<>();
        for (int i = 1; i <= 10 ; i++ ){
            counts.add(String.valueOf(jsonArray.getJSONObject(i).get("newCasesByPublishDate")));
        }
        return counts;
    }

    public String getNewCases(JSONObject jsonObject) throws JSONException {
        return String.valueOf(jsonObject.get("newCasesByPublishDate"));
    }
    public String getLatestUpdatedDate(JSONObject jsonObject) throws JSONException {
        return String.valueOf(jsonObject.get("date"));
    }


}
