package com.rayzem.roulette;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.Random;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private static final Sector[] sectorsWheel = {
            new Sector(32, "red"), new Sector(15, "black"),
            new Sector(19, "red"), new Sector(4, "black"),
            new Sector(21,"red") , new Sector(2,"black"),
            new Sector(25, "red"), new Sector(17, "black"),
            new Sector(34, "red"), new Sector(6, "black"),
            new Sector(27, "red"), new Sector(13, "black"),
            new Sector(36, "red"), new Sector(11, "black"),
            new Sector(30, "red"), new Sector(8, "black"),
            new Sector(23, "red"), new Sector(10, "black"),
            new Sector(5, "red") , new Sector(24, "black"),
            new Sector(16, "red"), new Sector(33, "black"),
            new Sector(1, "red") , new Sector(20, "black"),
            new Sector(14, "red"), new Sector(31, "black"),
            new Sector(9, "red") , new Sector(22, "black"),
            new Sector(18, "red"), new Sector(29, "black"),
            new Sector(7, "red") , new Sector(28, "black"),
            new Sector(12, "red"), new Sector(35, "black"),
            new Sector(3, "red"),  new Sector(26, "black"),
            new Sector(0, "")
    };
    private TextView resultRouletteWheel;
    private ImageView rouletteWheel;
    //private static final Random RANDOM_NUMBER = new Random();

    private int degree = 0, lastDegreee = 0;
    //37 sector in total.

    private static final float HALF_SECTOR = 360f/ 37f / 2f;

    private SensorManager sensorManager;
    private Sensor gyroscopeSensor;

    private static final float ROTATION_THRESHOLD = 2.0f;
    private long mRotationTime = 0;
    private static final int ROTATION_WAIT_TIME_MS = 100;

    RotateAnimation rotateAnimation;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initUI();

        /*rouletteWheel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                spinWheel(rouletteWheel);
            }
        });*/
    }

    @Override
    protected void onResume() {
        super.onResume();

        sensorManager.registerListener(this, gyroscopeSensor, SensorManager.SENSOR_DELAY_NORMAL);

    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    private void initUI(){
        resultRouletteWheel = findViewById(R.id.tv_result_roulette_number);
        rouletteWheel = findViewById(R.id.roulette_wheel);

        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        gyroscopeSensor = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);

    }




    private void spinWheel(){

        lastDegreee =  degree % 360;
        Random RANDOM_NUMBER = new Random();
        degree = RANDOM_NUMBER.nextInt(360) + 720;


        //Now start the rotating animation.
        rotateAnimation = new RotateAnimation(lastDegreee, degree, RotateAnimation.RELATIVE_TO_SELF,
                0.5f, RotateAnimation.RELATIVE_TO_SELF, 0.5f);

        rotateAnimation.setDuration(3000);

        rotateAnimation.setFillAfter(true);

        rotateAnimation.setInterpolator(new DecelerateInterpolator());

        rotateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                resultRouletteWheel.setText("");
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                resultRouletteWheel.setText("Winner: "+obtainSectorNumber(360 - (degree % 360)));
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });


        rouletteWheel.startAnimation(rotateAnimation);

    }

    private String obtainSectorNumber(int degreeValue) {
        int it = 0;
        String result = null;

        do{
            float start = HALF_SECTOR * (it * 2 + 1);
            float end = HALF_SECTOR * (it * 2 + 3);

            result = (degreeValue >= start && degreeValue < end)? sectorsWheel[it].toString() : null;

            it++;

        }while(result == null && it < sectorsWheel.length);



        return result;

    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if(sensorEvent.accuracy == SensorManager.SENSOR_STATUS_UNRELIABLE){
            Log.i("Roulette", "No accuraccy");
        }

        if(sensorEvent.sensor.getType() == Sensor.TYPE_GYROSCOPE)
            rotateWheel(sensorEvent);

    }

    private void rotateWheel(SensorEvent event){
        long now = System.currentTimeMillis();

        if((now - mRotationTime) > ROTATION_WAIT_TIME_MS){
            if(Math.abs(event.values[2]) > 2)
                spinWheel();
            else if(event.values[2] <= 0 ){
                if(rotateAnimation!=null && rotateAnimation.hasEnded()){
                    rotateAnimation.cancel();
                }

            }

        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {

    }
}
