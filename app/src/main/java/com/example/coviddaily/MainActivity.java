package com.example.coviddaily;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.example.RecyclerViewAdapter;
import com.example.coviddaily.models.LastTenDaysCount;
import com.example.coviddaily.models.TodayCount;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private MainViewModel mainViewModel;
    private TextView mDailyCounter, mLatestDataUpdateDate;
    private SwipeRefreshLayout swipeRefreshLayout;
    private RecyclerView recyclerView;
    private RecyclerViewAdapter adapter;
    private ArrayList<String> counts = new ArrayList<>();
    private ArrayList<String> dates = new ArrayList<>();
    private final static String TAG = "MainActivityDebug";
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

                mLatestDataUpdateDate.setText(new StringBuilder().append("Infected, since the last update on: ").append(todayCount.getDate()).toString());
            }
        });
        mainViewModel.getLastTenDaysCount().observe(this, new Observer<LastTenDaysCount>() {
            @Override
            public void onChanged(LastTenDaysCount lastTenDaysCount) {
                counts = lastTenDaysCount.getCounts();
                dates = lastTenDaysCount.getDates();
                initRecyclerView();

            }
        });

    }

    private void initRecyclerView() {
        recyclerView = findViewById(R.id.prevTenDays_recyclerView);


        recyclerView.setAdapter(new RecyclerViewAdapter(counts, dates, this));
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

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