package com.foodteam.metronome;

import android.app.Service;
import android.content.Intent;
import android.hardware.Camera;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Binder;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;

import java.util.Timer;
import java.util.TimerTask;

public class MyService extends Service {

    final String LOG_TAG = "myLogs";

    Camera camera;
    Vibrator vibrator;
    ToneGenerator toneGenerator;

    MyBinder binder = new MyBinder();
    Timer timer;
    TimerTask timerTask;
    long bpm = 100;

    boolean beep = false;
    boolean blink = false;
    boolean vibrate = true;

    public MyService() {
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return START_NOT_STICKY;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return binder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(LOG_TAG, "MyService onCreate");
        camera = Camera.open();
        schedule();
        vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
        toneGenerator = new ToneGenerator(AudioManager.STREAM_ALARM, 100);
    }

    public void schedule() {
        timer = new Timer();
        if (timerTask != null) timerTask.cancel();
        if (bpm > 0) {
            timerTask = new TimerTask() {
                public void run() {
                    Log.d(LOG_TAG, "run");
                    if (blink) {
                        Camera.Parameters p = camera.getParameters();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
                        camera.setParameters(p);
                        camera.startPreview();
                        p.setFlashMode(Camera.Parameters.FLASH_MODE_OFF);
                        camera.setParameters(p);
                        camera.stopPreview();
                    }
                    if (vibrate) {
                        vibrator.vibrate(100);

                    }
                    if (beep) {
                        toneGenerator.startTone(ToneGenerator.TONE_CDMA_ALERT_CALL_GUARD, 100);
                    }

                }
            };
            timer.schedule(timerTask, 1000, 60000 / bpm);
        }
    }

    long setBPM(long gap) {
        bpm = gap;
        schedule();
        return bpm;
    }

    public void releaseCamera() {
        camera.stopPreview();
        camera.release();
    }

    public void cancelTimer() {
        timer.cancel();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        releaseCamera();
    }

    public void setBeep(boolean b) {
        beep = b;
    }

    public void setBlink(boolean b) {
        blink = b;
    }

    public void setVibratep(boolean b) {
        vibrate = b;
    }

    class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
}
