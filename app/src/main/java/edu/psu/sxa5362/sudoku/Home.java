package edu.psu.sxa5362.sudoku;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class Home extends AppCompatActivity {
    private String preference = "Sudoku";
    private CustomSharedPreferences customSharedPreferences;
    private CustomSharedPreferences setting;

    private Button continueButton;
    private Button newButton;
    private ImageButton settingButton;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home);

        // Get views and sharedpreferences
        initialize();

        // Check previous unfinished game
        continueButton.setVisibility(View.GONE);
        if (customSharedPreferences.contains("difficulty")){
            continueButton.setVisibility(View.VISIBLE);
            continueButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Home.this, SudokuResume.class));
                    finish();
                }
            });
        }

        // New game
        newButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, DifficultyPopup.class));
                finish();
            }
        });

        // Setting
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Home.this, Setting.class));
                finish();
            }
        });

        // Set navigation
        setNavigationView();

        // Set background music
        playBackgroundMusic();
    }

    private void initialize(){
        customSharedPreferences = new CustomSharedPreferences(this, preference);
        setting = new CustomSharedPreferences(this, "Setting");
        continueButton = findViewById(R.id.home_button_continue);
        settingButton = findViewById(R.id.home_ibutton_setting);
        newButton = findViewById(R.id.home_button_new);
    }

    private void playBackgroundMusic(){
        if (setting.getBoolean("bgm") == true){
            if (BGM.isPlaying == false){
                String song = setting.getString("song");
                Intent intent = new Intent(this, BGM.class);
                intent.putExtra("bgm", song);
                startService(intent);
                BGM.isPlaying = true;
            }
        }
    }

    private void setNavigationView(){
        navigationView = findViewById(R.id.fragment_navigation);
        navigationView.setSelectedItemId(R.id.navigation_home);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_home:
                        return true;
                    case R.id.navigation_challenge:
                        intent = new Intent(getApplicationContext(), Challenge.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_ranking:
                        intent = new Intent(getApplicationContext(), Ranking.class);
                        startActivity(intent);
                        return true;
                }
                return true;
            }
        });
    }

}
