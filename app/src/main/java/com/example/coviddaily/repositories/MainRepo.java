package com.example.coviddaily.repositories;

import android.app.Activity;
import android.util.Log;

import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import com.example.coviddaily.RequestQueueSinglton;
import com.example.coviddaily.models.TodayCount;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainRepo {
    private final static String BASE_ENDPOINT = "https://api.coronavirus.data.gov.uk/v1/data";
    private static MainRepo instance;
    private final static String TAG = "RepoDebug";
    private Activity activity;
    private VolleyCallback volleyCallback;
    MutableLiveData<TodayCount> data;




    public  static MainRepo getInstance(){
        if (instance == null) {
            instance = new MainRepo();
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
                            Log.d(TAG, response.toString());
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

        RequestQueueSinglton.getInstance(getActivity()).addToRequestQueue(request); //queuing the request


    }

    private JSONArray getParsedJsonArray(JSONObject response) throws JSONException {
        return  (JSONArray) response.get("data");
    }


    private String getParams() {
        return "?filters=areaName=England;areaType=nation&structure={\"date\":\"date\",\"name\":\"areaName\",\"code\":\"areaCode\",\"newCasesByPublishDate\":\"newCasesByPublishDate\",\"cumCasesByPublishDate\":\"cumCasesByPublishDate\",\"newDeaths28DaysByPublishDate\":\"newDeaths28DaysByPublishDate\",\"cumDeaths28DaysByPublishDate\":\"cumDeaths28DaysByPublishDate\"}";
    }


    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }

   public interface VolleyCallback{
        void requestSuccessAlert(JSONArray response) throws JSONException;
   }

    public void setVolleyCallback(VolleyCallback volleyCallback) {
        this.volleyCallback = volleyCallback;
    }
}
