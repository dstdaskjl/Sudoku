package edu.psu.sxa5362.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

public class DifficultyPopup extends AppCompatActivity {
    private String preference = "Sudoku";
    private CustomSharedPreferences customSharedPreferences;

    private Button easyButton;
    private Button mediumButton;
    private Button hardButton;
    private Toolbar difficultyToolbar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.difficulty_popup);

        // Get views and sharedpreferences
        initialize();

        customSharedPreferences.reset();

        // Set toolbar
        setToolbar();

        // Set button listeners
        easyButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                customSharedPreferences.putString("difficulty", "Easy");
                startActivity(new Intent(DifficultyPopup.this, Sudoku.class));
                finish();
            }
        });
        mediumButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                customSharedPreferences.putString("difficulty", "Medium");
                startActivity(new Intent(DifficultyPopup.this, Sudoku.class));
                finish();
            }
        });
        hardButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View v) {
                customSharedPreferences.putString("difficulty", "Hard");
                startActivity(new Intent(DifficultyPopup.this, Sudoku.class));
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_difficulty, menu);
        getSupportActionBar().setTitle("Start a New Game");
        return true;
    }

    private void initialize(){
        customSharedPreferences = new CustomSharedPreferences(this, preference);
        easyButton = findViewById(R.id.difficulty_popup_button_easy);
        mediumButton = findViewById(R.id.difficulty_popup_button_medium);
        hardButton = findViewById(R.id.difficulty_popup_button_hard);
        difficultyToolbar = findViewById(R.id.difficulty_popup_toolbar);
    }

    private void setToolbar(){
        setSupportActionBar(difficultyToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);

        int width = displayMetrics.widthPixels;
        int height = displayMetrics.heightPixels;

        getWindow().setLayout(width, (int)(height*0.17));
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == android.R.id.home){
            Intent intent = new Intent(getApplicationContext(), Home.class);
            startActivity(intent);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
