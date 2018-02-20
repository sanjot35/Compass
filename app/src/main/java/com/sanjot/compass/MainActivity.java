package com.sanjot.compass;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private ImageView imageView ;
    private float[] mgravity = new float[3];
    private float[] mgeomagnetic = new float[3];
    private float azimuth = 0f;
    private float currectazimuth = 0f;
    private SensorManager msensorManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        imageView = (ImageView)findViewById(R.id.compass);
        msensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);

    }
    @Override
    protected void onResume()
    {
        super.onResume();
        msensorManager.registerListener(this,msensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD),SensorManager.SENSOR_DELAY_GAME );
        msensorManager.registerListener(this,msensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME );

    }
    @Override
    protected void onPause()
    {
        super.onPause();
        msensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        final float alpha = 0.97f;
        synchronized (this)
        {
            if(event.sensor.getType()== Sensor.TYPE_ACCELEROMETER)
            {
                mgravity[0]=alpha*mgravity[0]+(1-alpha)*event.values[0];
                mgravity[1]=alpha*mgravity[1]+(1-alpha)*event.values[1];
                mgravity[2]=alpha*mgravity[2]+(1-alpha)*event.values[2];
            }
            if(event.sensor.getType()== Sensor.TYPE_MAGNETIC_FIELD)
            {
                mgeomagnetic[0]=alpha*mgeomagnetic[0]+(1-alpha)*event.values[0];
                mgeomagnetic[1]=alpha*mgeomagnetic[1]+(1-alpha)*event.values[1];
                mgeomagnetic[2]=alpha*mgeomagnetic[2]+(1-alpha)*event.values[2];
            }
            float R[] = new float[9];
            float I[] = new float[9];
            boolean success = SensorManager.getRotationMatrix(R,I,mgravity,mgeomagnetic);
            if(success)
            {
                float orientation[] =new float[3];
                SensorManager.getOrientation(R,orientation);
                azimuth =(float)Math.toDegrees(orientation[0]);
                azimuth=(azimuth+360)%360;
                Animation anim = new RotateAnimation(-currectazimuth,-azimuth,Animation.RELATIVE_TO_SELF,0.5f,Animation.RELATIVE_TO_SELF,0.5f);
                currectazimuth = azimuth;

                anim.setDuration(600);
                anim.setRepeatCount(0);
                anim.setFillAfter(true);

                imageView.startAnimation(anim);
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
