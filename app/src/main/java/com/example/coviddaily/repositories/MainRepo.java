package com.example.coviddaily.repositories;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.AsyncTask;
import android.util.Log;

import androidx.appcompat.app.AlertDialog;
import androidx.lifecycle.LiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.coviddaily.RequestQueueSinglton;
import com.example.coviddaily.SharedPreference;
import com.example.coviddaily.db.AppDataBase;
import com.example.coviddaily.db.HistoryDataDao;
import com.example.coviddaily.db.HistoryDataEntry;

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
    private RepoCallBack repoCallBack;
    private HistoryDataDao mHistoryDataDao;
    private LiveData<List<HistoryDataEntry>> allHistoryData;
    private SharedPreference sharedPreferences;



    public  static MainRepo getInstance(Context context){

        if (instance == null) {
            instance = new MainRepo();
            instance.context = context;


        }

            return instance;
    }


    public void getDataFromRepo() throws JSONException {
        if(isConnectedToInternet(context)) getDataFromAPI();
        else {
            getDataFromDB();}

    }

    private void getDataFromDB() throws JSONException {

        initDB();
        initSharedPreferences();
        passData();
        setAlertDialog((Activity) context, "Please check your internet connection!");

    }

    public void getDataFromAPI(){
        String url = BASE_ENDPOINT+getParams();
        JsonObjectRequest request = new JsonObjectRequest(Request.Method.GET,url,null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {

                            initDB(); //initiate the database connection
                            populateDB(getParsedJsonArray(response)); //populates database with the newest history data
                            populateSharedPreference(getParsedJsonArray(response)); //populates the local storage with current day data
                            passData();




                        } catch (JSONException e) {
                            try {
                                // in case of any issues we pass the already saved data in the database
                                passData();
                            } catch (JSONException ex) {
                                ex.printStackTrace();
                            }
                            e.printStackTrace();
                        }


                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //in case of response error
                        try {
                            passData();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

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

    private void passData() throws JSONException {
        repoCallBack.getHistoryLiveData(allHistoryData); //after that it passes history data from db to the viewmodel through the callback interface
        repoCallBack.getCurrentDayCountAndDate(sharedPreferences.returnCurrentCount(), //then it passes current day data to the local storage
                sharedPreferences.returnCurrentDate());

    }
    public static boolean isConnectedToInternet(Context context)
    {
        // Check intenet connectivity
        boolean connected = false;
        try
        {
            ConnectivityManager conMgr = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

            connected = (   conMgr.getActiveNetworkInfo() != null &&
                    conMgr.getActiveNetworkInfo().isAvailable() &&
                    conMgr.getActiveNetworkInfo().isConnected()   );
        }catch (Exception e)
        {
            return false;
        }

        return connected;

    }

    private void populateSharedPreference(JSONArray jsonArray) throws JSONException {
        initSharedPreferences();
        sharedPreferences.saveCurrentCount(String.valueOf(jsonArray.getJSONObject(0).get("newCasesByPublishDate")));
        sharedPreferences.saveCurrentDate(String.valueOf(jsonArray.getJSONObject(0).get("date")));
    }
    private void initSharedPreferences(){
        sharedPreferences = new SharedPreference(context);

    }

    private void initDB() {
        AppDataBase db = AppDataBase.getInstance(context);
        mHistoryDataDao = db.historyDataDao();
        allHistoryData = mHistoryDataDao.getAllHistoryData();
    }

    private void populateDB(JSONArray jsonArray) throws JSONException {
        //adding entries to DB
        deleteAllHistoryData(); //removing old data and adding the new
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




   public interface RepoCallBack {
        void getHistoryLiveData(LiveData<List<HistoryDataEntry>> liveData) throws JSONException;
        void getCurrentDayCountAndDate(String count, String date);

   }

    public void setRepoCallback(RepoCallBack repoCallBack) {
        this.repoCallBack= repoCallBack;
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
    private void setAlertDialog(Activity context, String message) {
        if (!context.isFinishing() ) {
            new AlertDialog.Builder(context)
                    .setTitle("Connection Error.")
                    .setMessage(message)

                    // A null listener allows the button to dismiss the dialog and take no further action.
                    .setNegativeButton(android.R.string.ok, null)
                    .setIcon(android.R.drawable.ic_dialog_alert)
                    .show();
        }
    }
}
