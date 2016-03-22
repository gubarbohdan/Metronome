package com.foodteam.metronome;

import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ToggleButton;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.buttonStart)
    Button btnStart;
    @Bind(R.id.buttonStop)
    Button btnStop;
    @Bind(R.id.seekBar)
    SeekBar seekBar;
    @Bind(R.id.twBPM)
    TextView twBPM;
    @Bind(R.id.toggleButton)
    ToggleButton toggleButtonVibrate;
    @Bind(R.id.toggleButton2)
    ToggleButton toggleButtonFlash;
    @Bind(R.id.toggleButton3)
    ToggleButton toggleButtonBeep;

    Intent intent;
    ServiceConnection sconn;
    MyService myService;
    boolean bound = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        disableAll();

        intent = new Intent(this, MyService.class);
        sconn = new ServiceConnection() {
            @Override
            public void onServiceConnected(ComponentName name, IBinder service) {
                myService = ((MyService.MyBinder) service).getService();
                myService.schedule();
                bound = true;
            }

            @Override
            public void onServiceDisconnected(ComponentName name) {
                bound = false;
            }
        };
        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                twBPM.setText(Integer.toString(progress) + " bpm");
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                myService.setBPM(seekBar.getProgress());
            }
        });

        seekBar.setProgress(100);

        toggleButtonFlash.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myService.setBlink(isChecked);
            }
        });

        toggleButtonVibrate.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myService.setVibratep(isChecked);
            }
        });

        toggleButtonBeep.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                myService.setBeep(isChecked);
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
        bindService(intent, sconn, 0);
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (!bound) return;
        unbindService(sconn);
        bound = false;
    }

    @OnClick(R.id.buttonStart)
    public void startClick(View view) {
        startService(intent);
        enableAll();
        if (myService != null) {
            myService.schedule();
        }
    }

    @OnClick(R.id.buttonStop)
    public void stopClick(View view) {
//        myService.releaseCamera();
        myService.cancelTimer();
        disableAll();
        if (!bound) return;
        unbindService(sconn);

        bound = false;
    }


    private void enableAll() {
        seekBar.setEnabled(true);
        toggleButtonBeep.setEnabled(true);
        toggleButtonVibrate.setEnabled(true);
        toggleButtonFlash.setEnabled(true);
    }

    private void disableAll() {
        seekBar.setEnabled(false);
        toggleButtonBeep.setEnabled(false);
        toggleButtonVibrate.setEnabled(false);
        toggleButtonFlash.setEnabled(false);
    }

}
