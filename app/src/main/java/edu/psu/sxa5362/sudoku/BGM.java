package edu.psu.sxa5362.sudoku;

import android.app.Activity;
import android.app.Service;
import android.content.Intent;
import android.media.MediaPlayer;
import android.os.IBinder;

import androidx.annotation.Nullable;

public class BGM extends Service {
    private MediaPlayer musicPlayer;
    public static boolean isPlaying = false;

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
        String bgm = intent.getStringExtra("bgm");
        if (bgm == null || bgm.equals("") || bgm.equals("Faded")){
            musicPlayer = MediaPlayer.create(this, R.raw.faded);
        }
        else if (bgm.equals("Firefly")){
            musicPlayer = MediaPlayer.create(this, R.raw.firefly);
        }
        else if (bgm.equals("Nekozilla")){
            musicPlayer = MediaPlayer.create(this, R.raw.nekozilla);
        }
        else if (bgm.equals("Puzzle")){
            musicPlayer = MediaPlayer.create(this, R.raw.puzzle);
        }
        else if (bgm.equals("Shallow")){
            musicPlayer = MediaPlayer.create(this, R.raw.shallow);
        }
        else if (bgm.equals("Throwback")){
            musicPlayer = MediaPlayer.create(this, R.raw.throwback);
        }
        musicPlayer.setLooping(true);
        musicPlayer.start();
        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        if (musicPlayer != null){
            musicPlayer.stop();
        }
        super.onDestroy();
    }
}
