package com.example.gaugerotation;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.Nullable;

public class MainActivity extends Activity implements SensorEventListener {
    SensorManager mSensorManager = null;
    Sensor mSensor;
    float[] floats = new float[3];

    TextView x, y, z;
    GaugeRotation gaugeRotation;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        x = findViewById(R.id.x);
        y = findViewById(R.id.y);
        z = findViewById(R.id.z);
        gaugeRotation = findViewById(R.id.gaugeRotation);
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mSensorManager.registerListener(this, mSensor, Sensor.TYPE_ACCELEROMETER);
    }

    private void updata() {
        x.setText("X轴： " + (int) ((Math.toDegrees(floats[0]) + 360) % 360));
        y.setText("Y轴： " + (int) (Math.toDegrees(floats[1]) - 360));
        z.setText("Z轴： " + (int) (Math.toDegrees(floats[2])));
        gaugeRotation.setRotate(floats[0]);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            floats[0] = event.values[0];
            floats[1] = event.values[1];
            floats[2] = event.values[2];
            updata();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
