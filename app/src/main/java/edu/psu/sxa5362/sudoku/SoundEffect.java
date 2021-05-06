package edu.psu.sxa5362.sudoku;

import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class SoundEffect extends Service {
    private MediaPlayer musicPlayer;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        String soundEffect = intent.getStringExtra("soundEffect");
        if (soundEffect.equals("right")){
            musicPlayer = MediaPlayer.create(this, R.raw.right);
        }
        else {
            musicPlayer = MediaPlayer.create(this, R.raw.wrong);
        }
        musicPlayer.setLooping(false);
        musicPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        musicPlayer.stop();
    }
}
