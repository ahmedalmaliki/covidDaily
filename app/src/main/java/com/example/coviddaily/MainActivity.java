package com.example.coviddaily;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import androidx.lifecycle.ViewModelProvider;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.widget.TextView;

import com.example.coviddaily.models.TodayCount;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private TextView mDailyCounter, mLatestDataUpdateDate;
    private SwipeRefreshLayout swipeRefreshLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initHooks();
        bindings();
        mainViewModel.setActivity(this);
        mainViewModel.init();
        observeDate();
    }



    private void observeDate() {
        mainViewModel.getTodayCount().observe(this, new Observer<TodayCount>() {
            @Override
            public void onChanged(TodayCount todayCount) {
                mDailyCounter.setText(todayCount.getCount());
                mLatestDataUpdateDate.setText(new StringBuilder().append("Infected since last update on: ").append(todayCount.getDate()).toString());

            }
        });

    }

    private void initHooks() {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mDailyCounter = findViewById(R.id.daily_count);
         swipeRefreshLayout = (SwipeRefreshLayout)findViewById(R.id.swipeRefresh);
         mLatestDataUpdateDate = findViewById(R.id.latest_data_update_date);
    }
    private void bindings() {
        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            //swiping the top of the page to refresh the page
            @Override
            public void onRefresh() {
                refreshCurrActivity();
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    private void refreshCurrActivity() {
        finish();
        overridePendingTransition(0, 0);
        startActivity(getIntent());
        overridePendingTransition(0, 0);
    }


}