package com.example.mcalcpro;

import androidx.appcompat.app.AppCompatActivity;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Locale;

import ca.roumani.i2c.MPro;

public class MainActivity extends AppCompatActivity implements TextToSpeech.OnInitListener, SensorEventListener {
    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        this.tts=new TextToSpeech(this,this);
        SensorManager sm = (SensorManager) getSystemService(SENSOR_SERVICE);
        sm.registerListener(this,
                sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),
                SensorManager.SENSOR_DELAY_NORMAL);

    }
    public void buttonClicked(View v){
        try {
            MPro mp = new MPro();
            mp.setPrinciple(((EditText) findViewById(R.id.pBox)).getText().toString());
            mp.setAmortization(((EditText) findViewById(R.id.aBox)).getText().toString());
            mp.setInterest(((EditText) findViewById(R.id.iBox)).getText().toString());
            String s = "Monthly Payment = " + mp.computePayment("%,.2f");
            s += "\n\n";
            s += "By making this payments monthly for" + mp.getAmortization() + "years,the mortgage will be paid in full.But if you terminate the mortgage on its nth anniversary, the balance still owning depends on n as shown below:";
            s += "\n\n";
            s+=String.format("%8s", "n") + String.format("%16s","Balance");
            s+="\n\n";
            for (int i = 0; i <= 5; i++) {
                s += String.format("%8d", i) + mp.outstandingAfter(i, "%,16.0f");
                s += "\n\n";
            }
            s += String.format("%8d", 10) + mp.outstandingAfter(10, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 15) + mp.outstandingAfter(15, "%,16.0f");
            s += "\n\n";
            s += String.format("%8d", 20) + mp.outstandingAfter(20, "%,16.0f");
            s += "\n\n";


            ((TextView) findViewById(R.id.result)).setText(s);
            tts.speak(s,TextToSpeech.QUEUE_FLUSH,null);
        }
        catch (Exception e){
            Toast label=Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT);
            label.show();
            // display e.getMessage();
        }


    }

    @Override
    public void onInit(int status) {
        this.tts.setLanguage(Locale.US);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        double ax = event.values[0];
        double ay = event.values[1];
        double az = event.values[2];
        double a = Math.sqrt(ax * ax + ay * ay + az * az);
        if (a > 20)
        {
            ((EditText) findViewById(R.id.pBox)).setText("");
            ((EditText) findViewById(R.id.aBox)).setText("");
            ((EditText) findViewById(R.id.iBox)).setText("");
            ((TextView) findViewById(R.id.result)).setText("");
        }
    }

    @Override
    public void onAccuracyChanged(Sensor arg0, int arg1) {

    }
}