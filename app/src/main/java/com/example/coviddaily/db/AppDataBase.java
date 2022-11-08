package com.example.coviddaily.db;

import android.content.Context;
import android.os.AsyncTask;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

@Database(entities = {HistoryDataEntry.class}, version = 1)
public  abstract class AppDataBase extends RoomDatabase {
    public  abstract HistoryDataDao historyDataDao();
    private static AppDataBase instance;
    public static synchronized AppDataBase getInstance(Context context){
        if (instance == null){
            instance = Room.databaseBuilder(context.getApplicationContext(),
                    AppDataBase.class, "AppDataBase").fallbackToDestructiveMigration()
                    .addCallback(roomCallback)
                    .build();
        }
        return instance;

    }
    private static RoomDatabase.Callback roomCallback = new RoomDatabase.Callback(){
        @Override
        public void onCreate(@NonNull SupportSQLiteDatabase db) {
            super.onCreate(db);
            new PopulateDataAsyncTask(instance).execute();

        }

        @Override
        public void onOpen(@NonNull SupportSQLiteDatabase db) {
            super.onOpen(db);
            new PopulateDataAsyncTask(instance).execute();

        }
    };
    private static class PopulateDataAsyncTask extends AsyncTask<Void, Void, Void>{
        private  HistoryDataDao mHistoryDataDao;

        public PopulateDataAsyncTask(AppDataBase db) {
            this.mHistoryDataDao = db.historyDataDao();
        }

        @Override
        protected Void doInBackground(Void... voids) {
            return null;
        }
    }
}
