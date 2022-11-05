package com.example.coviddaily;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
import com.example.coviddaily.models.TodayCount;
import com.example.coviddaily.repositories.MainRepo;

public class MainViewModel extends ViewModel {
    private MutableLiveData<TodayCount> mTodayCount;
    private MainRepo mainRepo;
    private Activity activity;
    private static final String TAG = "MainModelViewDebug";

    public void init(){

        if (mTodayCount != null){
            return;
        }else {
            mainRepo = MainRepo.getInstance();
            mainRepo.setActivity(getActivity());
            mainRepo.GetInfoFromApi();
            mTodayCount = new MutableLiveData<>();
            try {
                mTodayCount = mainRepo.getLatestCount();
            }catch (Exception e){
                Log.d(TAG, e.toString());
            }
        }
    }

    public LiveData<TodayCount> getTodayCount() {
        return mTodayCount;
    }


    public Activity getActivity() {
        return activity;
    }

    public void setActivity(Activity activity) {
        this.activity = activity;
    }
}
