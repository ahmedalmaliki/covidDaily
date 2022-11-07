package com.example.coviddaily.repositories;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.coviddaily.RequestQueueSinglton;
import com.example.coviddaily.db.AppDataBase;
import com.example.coviddaily.db.HistoryDataDao;
import com.example.coviddaily.db.HistoryDataEntry;
import com.example.coviddaily.models.TodayCount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainRepo {
    private final static String BASE_ENDPOINT = "https://api.coronavirus.data.gov.uk/v1/data";
    private static MainRepo instance;
    private final static String TAG = "RepoDebug";
    private Context context;
    private VolleyCallback volleyCallback;
    private HistoryDataDao mHistoryDataDao;



    public  static MainRepo getInstance(Context context){
        if (instance == null) {
            instance = new MainRepo();
            instance.context = context;
        }
            return instance;
    }

    public void GetInfoFromApi(){

        String url = BASE_ENDPOINT+getParams();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            initDB();
                            populateDB(getParsedJsonArray(response));
                            Log.d(TAG, String.valueOf("llllllllllllllll"+mHistoryDataDao.getAllHistoryData()));
                            volleyCallback.requestSuccessAlert(getParsedJsonArray(response));


                        } catch (JSONException e) {
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {


                        Log.d(TAG, error.toString());


                    }
                }) {

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headerMap = new HashMap<String, String>();

                return headerMap;
            }
        };

        RequestQueueSinglton.getInstance(context).addToRequestQueue(request); //queuing the request


    }

    private void initDB() {
        AppDataBase db = AppDataBase.getInstance(context);
        mHistoryDataDao = db.historyDataDao();
    }

    private void populateDB(JSONArray jsonArray) throws JSONException {
        deleteAllHistoryData();
        for (int i = 1; i <= 10 ; i++ ){
            insert(new HistoryDataEntry(String.valueOf(jsonArray.getJSONObject(i).get("newCasesByPublishDate")),
                    String.valueOf(jsonArray.getJSONObject(i).get("date"))) );
        }


    }

    private JSONArray getParsedJsonArray(JSONObject response) throws JSONException {
        return  (JSONArray) response.get("data");
    }


    private String getParams() {
        return "?filters=areaName=England;areaType=nation&structure={\"date\":\"date\",\"name\":\"areaName\",\"code\":\"areaCode\",\"newCasesByPublishDate\":\"newCasesByPublishDate\",\"cumCasesByPublishDate\":\"cumCasesByPublishDate\",\"newDeaths28DaysByPublishDate\":\"newDeaths28DaysByPublishDate\",\"cumDeaths28DaysByPublishDate\":\"cumDeaths28DaysByPublishDate\"}";
    }




   public interface VolleyCallback{
        void requestSuccessAlert(JSONArray response) throws JSONException;
   }

    public void setVolleyCallback(VolleyCallback volleyCallback) {
        this.volleyCallback = volleyCallback;
    }

    //insert
    public void insert (HistoryDataEntry historyDataEntry){
        new InsertAsyncTask(mHistoryDataDao).execute(historyDataEntry);
    }
    //delete
    public void delete (HistoryDataEntry historyDataEntry){
        new DeleteAsyncTask(mHistoryDataDao).execute(historyDataEntry);
    }
    //update
    public void update (HistoryDataEntry historyDataEntry){
        new UpdateAsyncTask(mHistoryDataDao).execute(historyDataEntry);
    }
    //deleteAllWords
    public void  deleteAllHistoryData (){
        new DeleteAllHistoryDataAsyncTask(mHistoryDataDao).execute();
    }
    private static class InsertAsyncTask extends AsyncTask<HistoryDataEntry, Void, Void> {
        private  HistoryDataDao mHistoryDataDao;

        public InsertAsyncTask(HistoryDataDao mHistoryDataDao) {
            this.mHistoryDataDao = mHistoryDataDao;
        }

        @Override
        protected Void doInBackground(HistoryDataEntry... historyDataEntries) {
            mHistoryDataDao.insert(historyDataEntries[0]);
            return null;
        }
    }
    private static class DeleteAsyncTask extends AsyncTask<HistoryDataEntry, Void, Void> {
        private  HistoryDataDao mHistoryDataDao;

        public DeleteAsyncTask(HistoryDataDao mHistoryDataDao) {
            this.mHistoryDataDao = mHistoryDataDao;
        }

        @Override
        protected Void doInBackground(HistoryDataEntry... historyDataEntries) {
            mHistoryDataDao.delete(historyDataEntries[0]);
            return null;
        }
    }
    private static class UpdateAsyncTask extends AsyncTask<HistoryDataEntry, Void, Void> {
        private  HistoryDataDao mHistoryDataDao;

        public UpdateAsyncTask(HistoryDataDao mHistoryDataDao) {
            this.mHistoryDataDao = mHistoryDataDao;
        }

        @Override
        protected Void doInBackground(HistoryDataEntry... historyDataEntries) {
            mHistoryDataDao.update(historyDataEntries[0]);
            return null;
        }
    }
    private static class DeleteAllHistoryDataAsyncTask extends AsyncTask<Void, Void, Void> {
        private  HistoryDataDao mHistoryDataDao;

        public DeleteAllHistoryDataAsyncTask(HistoryDataDao mHistoryDataDao) {
            this.mHistoryDataDao = mHistoryDataDao;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            mHistoryDataDao.deleteAllHistoryData();
            return null;
        }
    }
}
