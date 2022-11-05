package com.example.coviddaily;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import java.util.Timer;
import java.util.TimerTask;

public class SplashScreenActivity extends AppCompatActivity {
    //the activity runs the splash screen in the beginning.
    private Timer timer; // This timer controls the time the splash screen stays up.
    private  Context context;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        initHooks();

    }

    private void initHooks() {
        context = this;
        timer = new Timer();
        timer.schedule(new RemindTask(), 3000); // The timer is scheduled for three seconds.
    }
    class RemindTask extends TimerTask{

        @Override
        public void run() {
            this.moveToMainActivity();
        }
        private void moveToMainActivity() {
            //After the timer ends the UI moves to the Log in  activity.
            Intent intent = new Intent(context, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
            startActivity(intent);
            finish();
        }
    }


}