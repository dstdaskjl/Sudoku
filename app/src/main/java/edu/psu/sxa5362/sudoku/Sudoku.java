package edu.psu.sxa5362.sudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Sudoku extends AppCompatActivity implements GameOverDialog.GameOverButtonClickInterface, GameWinDialog.GameWinButtonClickInterface {
    private String preference = "Sudoku";
    private CustomSharedPreferences customSharedPreferences;
    private CustomSharedPreferences setting;

    private TextView difficultyView;
    private TextView mistakeView;
    private TextView timeView;
    private RecyclerView boardView;
    private Button eraseButton;
    private Button noteBlackButton;
    private Button noteBlueButton;
    private Button hintButton;
    private Button[] choiceArray;

    private int mistake;
    private int maxMistakes;
    private int hint;
    private int maxHints;
    private int seconds;
    private int minutes;
    private int[][] sudokuArray;
    private int[][] answerArray;
    private long startTime;
    private long elapsedTime;
    private long milliSecondTime;
    private boolean soundEffect;
    private String difficulty;

    private Handler handler = new Handler();
    public static ButtonInfo currentButtonInfo;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sudoku);

        // Get views and sharedpreferences
        initialize();

        // Set texts
        setTexts();

        // Get Sudoku Board
        sudokuArray = getSudokuArray();
        answerArray = getAnswerArray(sudokuArray);

        // Inflate RecyclerView
        SudokuAdapter adapter = new SudokuAdapter(sudokuArray);
        boardView.setLayoutManager(new LinearLayoutManager(this));
        boardView.setAdapter(adapter);

        // Erase Listener
        eraseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentButtonInfo != null){
                    currentButtonInfo.getButton().setText("");
                }
            }
        });

        // Note Listener
        noteBlackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentButtonInfo != null){
                    currentButtonInfo.getButton().setText("");
                    changeNoteButtonColor();
                }
            }
        });

        noteBlueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (currentButtonInfo != null){
                    currentButtonInfo.getButton().setText("");
                    changeNoteButtonColor();
                }
            }
        });

        // Hint Listener
        hintButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                if (currentButtonInfo != null){
                    if (hint > 0){
                        hint--;
                        hintButton.setText("Hint " + hint + "/" + maxHints);

                        if (soundEffect){
                            Intent intent = new Intent(Sudoku.this, SoundEffect.class);
                            intent.putExtra("soundEffect", "right");
                            startService(intent);
                        }

                        currentButtonInfo.getButton()
                                .setText(String.valueOf(answerArray[currentButtonInfo.getGroupPosition()][currentButtonInfo.getItemPosition()]));
                        currentButtonInfo.getButton().setTextColor(Color.BLACK);
                        sudokuArray[currentButtonInfo.getGroupPosition()][currentButtonInfo.getItemPosition()]
                                = answerArray[currentButtonInfo.getGroupPosition()][currentButtonInfo.getItemPosition()];

                        // Now the button is unclickable
                        currentButtonInfo.getButton().setClickable(false);
                        currentButtonInfo.changeBackgroundColor();
                        currentButtonInfo = null;

                        if (isCompleted()){
                            if (maxHints != 9999 && maxMistakes != 9999){
                                // Insert Data into Firebase
                                stopTimer();
                                final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                DatabaseReference databaseReference = Firebase.getDatabaseReference("");
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        List<Long> resultList = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.child("Record").child(difficulty).child(uid).getChildren()){
                                            resultList.add(ds.getValue(Long.class));
                                        }
                                        resultList.add(elapsedTime);
                                        Firebase.insert("Record/" + difficulty + "/" + uid, resultList);
                                        customSharedPreferences.reset();

                                        List<String> dailyList = new ArrayList<>();
                                        for (DataSnapshot ds : dataSnapshot.child("Daily").child(uid).getChildren()){
                                            dailyList.add(ds.getValue(String.class));
                                        }
                                        String today = String.valueOf(LocalDate.now());
                                        if (dailyList.contains(today) == false){
                                            dailyList.add(today);
                                            Firebase.insert("Daily/" + uid, dailyList);
                                        }

                                        new GameWinDialog().show(getSupportFragmentManager(), "gameWinDialog");
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                            else{
                                customSharedPreferences.reset();
                                new GameWinDialog().show(getSupportFragmentManager(), "gameWinDialog");
                            }
                        }
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "No more hint!", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

        // Choice Button Listener (1~9)
        for (int i = 0; i < 9; i++){
            final int input = i + 1;
            choiceArray[i].setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (currentButtonInfo != null){
                        Button button = currentButtonInfo.getButton();
                        String text = button.getText().toString();

                        // Note mode
                        if (noteBlueButton.getVisibility() == View.VISIBLE){
                            String[] note = text.split(",");
                            if (button.getTextColors().getDefaultColor() == Color.RED){
                                button.setText(String.valueOf(input));
                            }
                            else{
                                if (Arrays.asList(note).contains(String.valueOf(input)) == false){
                                    if (note.length == 3){
                                        Toast.makeText(getApplicationContext(), "You can save up to 3 numbers", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        if (text.length() == 0){
                                            button.setText(String.valueOf(input));
                                        }
                                        else{
                                            button.setText(text + "," + input);
                                        }
                                    }
                                }
                            }
                            button.setTextColor(getResources().getColor(R.color.colorPrimary, getResources().newTheme()));
                        }
                        // Answer mode
                        else{
                            button.setText(String.valueOf(input));
                            if (answerArray[currentButtonInfo.getGroupPosition()][currentButtonInfo.getItemPosition()] == input){
                                if (soundEffect){
                                    Intent intent = new Intent(Sudoku.this, SoundEffect.class);
                                    intent.putExtra("soundEffect", "right");
                                    startService(intent);
                                }
                                button.setTextColor(Color.BLACK);
                                sudokuArray[currentButtonInfo.getGroupPosition()][currentButtonInfo.getItemPosition()] = input;
                                if (isCompleted()){
                                    if (maxHints != 9999 && maxMistakes != 9999){
                                        // Insert Data into Firebase
                                        stopTimer();
                                        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        DatabaseReference databaseReference = Firebase.getDatabaseReference("");
                                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                                List<Long> resultList = new ArrayList<>();
                                                for (DataSnapshot ds : dataSnapshot.child("Record").child(difficulty).child(uid).getChildren()){
                                                    resultList.add(ds.getValue(Long.class));
                                                }
                                                resultList.add(elapsedTime);
                                                Firebase.insert("Record/" + difficulty + "/" + uid, resultList);
                                                customSharedPreferences.reset();

                                                List<String> dailyList = new ArrayList<>();
                                                for (DataSnapshot ds : dataSnapshot.child("Daily").child(uid).getChildren()){
                                                    dailyList.add(ds.getValue(String.class));
                                                }
                                                String today = String.valueOf(LocalDate.now());
                                                if (dailyList.contains(today) == false){
                                                    dailyList.add(today);
                                                    Firebase.insert("Daily/" + uid, dailyList);
                                                }

                                                new GameWinDialog().show(getSupportFragmentManager(), "gameWinDialog");
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError databaseError) {

                                            }
                                        });
                                    }
                                    else{
                                        customSharedPreferences.reset();
                                        new GameWinDialog().show(getSupportFragmentManager(), "gameWinDialog");
                                    }
                                }
                            }
                            else{
                                if (soundEffect){
                                    Intent intent = new Intent(Sudoku.this, SoundEffect.class);
                                    intent.putExtra("soundEffect", "wrong");
                                    startService(intent);
                                }
                                button.setTextColor(Color.RED);
                                mistakeView.setText("Mistake " + (++mistake) + "/" + maxMistakes);
                                if (mistake == maxMistakes){
                                    customSharedPreferences.reset();
                                    stopTimer();
                                    new GameOverDialog().show(getSupportFragmentManager(), "gameOverDialog");
                                }
                            }
                        }
                    }
                }
            });
        }

        // Timer
        startTime = SystemClock.uptimeMillis();
        handler.postDelayed(runnable, 0);
    }

    private void initialize(){
        customSharedPreferences = new CustomSharedPreferences(this, preference);
        setting = new CustomSharedPreferences(this, "Setting");
        difficultyView = findViewById(R.id.sudoku_text_difficulty);
        mistakeView = findViewById(R.id.sudoku_text_mistake);
        timeView = findViewById(R.id.sudoku_text_time);
        boardView = findViewById(R.id.sudoku_recyclerview_board);
        eraseButton = findViewById(R.id.sudoku_button_erase);
        noteBlackButton = findViewById(R.id.sudoku_button_note_black);
        noteBlueButton = findViewById(R.id.sudoku_button_note_blue);
        hintButton = findViewById(R.id.sudoku_button_hint);
        choiceArray = new Button[9];
        choiceArray[0] = findViewById(R.id.sudoku_button_select1);
        choiceArray[1] = findViewById(R.id.sudoku_button_select2);
        choiceArray[2] = findViewById(R.id.sudoku_button_select3);
        choiceArray[3] = findViewById(R.id.sudoku_button_select4);
        choiceArray[4] = findViewById(R.id.sudoku_button_select5);
        choiceArray[5] = findViewById(R.id.sudoku_button_select6);
        choiceArray[6] = findViewById(R.id.sudoku_button_select7);
        choiceArray[7] = findViewById(R.id.sudoku_button_select8);
        choiceArray[8] = findViewById(R.id.sudoku_button_select9);
    }

    private void setTexts(){
        // Difficulty
        difficulty = customSharedPreferences.getString("difficulty");
        difficultyView.setText(difficulty);

        // Mistake
        mistake = 0;
        if (setting.getBoolean("unlimitedMistakes")){
            maxMistakes = 9999;
        }
        else{
            maxMistakes = 3;
        }
        mistakeView.setText("Mistake " + mistake + "/" + maxMistakes);

        // Hint
        if (setting.getBoolean("unlimitedHints")){
            hint = 9999;
        }
        else{
            hint = 2;
        }
        maxHints = hint;
        hintButton.setText("Hint " + hint + "/" + maxHints);

        // No time?
        if (setting.getBoolean("timer")){
            timeView.setVisibility(View.VISIBLE);
        }
        else{
            timeView.setVisibility(View.INVISIBLE);
        }

        // Sound effect
        soundEffect = setting.getBoolean("soundEffect");
    }

    private int[][] getSudokuArray(){
        int numMissingBlocks = 0;
        String difficulty = customSharedPreferences.getString("difficulty");
        if (difficulty.equals("Easy")) numMissingBlocks = 40;
        if (difficulty.equals("Medium")) numMissingBlocks = 45;
        if (difficulty.equals("Hard")) numMissingBlocks = 50;

        SudokuGenerator sudokuGenerator = new SudokuGenerator(9, numMissingBlocks);
        return sudokuGenerator.generate();
    }

    private int[][] getAnswerArray(int[][] sudokuBoard){
        int[][] answerArray = new int[9][9];
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                answerArray[i][j] = sudokuBoard[i][j];
            }
        }
        SudokuSolver sudokuSolver = new SudokuSolver();
        sudokuSolver.solveSudoku(answerArray, 9);
        return answerArray;
    }

    public void changeNoteButtonColor(){
        if (noteBlueButton.getVisibility() == View.GONE){
            noteBlueButton.setVisibility(View.VISIBLE);
            noteBlackButton.setVisibility(View.GONE);
        }
        else{
            noteBlackButton.setVisibility(View.VISIBLE);
            noteBlueButton.setVisibility(View.GONE);
        }
    }

    public boolean isCompleted(){
        for(int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                if (sudokuArray[i][j] == 0){
                    return false;
                }
            }
        }
        return true;
    }

    public void stopTimer(){
        handler.removeCallbacks(runnable);
        elapsedTime = SystemClock.uptimeMillis() - startTime;
        milliSecondTime = elapsedTime;
        seconds = (int) (milliSecondTime / 1000);
        minutes = seconds / 60;
        seconds = seconds % 60;
        timeView.setText(String.format("%02d", minutes) + ":"
                + String.format("%02d", seconds));
    }

    public void saveToPreferenceOnClose(){
        customSharedPreferences.putInt("mistake", mistake);
        customSharedPreferences.putInt("hint", hint);
        customSharedPreferences.putLong("elapsedTime", elapsedTime);
        for (int i = 0; i < 9; i++){
            for (int j = 0; j < 9; j++){
                customSharedPreferences.putInt("answer" + i + j, answerArray[i][j]);
                customSharedPreferences.putInt("sudoku" + i + j, sudokuArray[i][j]);
            }
        }
    }

    @Override
    public void onGameOverButtonClick() {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }

    @Override
    public void onGameWinButtonClick() {
        Intent intent = new Intent(getApplicationContext(), Home.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        stopTimer();
        saveToPreferenceOnClose();
        startActivity(new Intent(this, Home.class));
        finish();
    }

    public Runnable runnable = new Runnable() {
        public void run() {
            milliSecondTime = SystemClock.uptimeMillis() - startTime;
            seconds = (int) (milliSecondTime / 1000);
            minutes = seconds / 60;
            seconds = seconds % 60;
            timeView.setText(String.format("%02d", minutes) + ":"
                    + String.format("%02d", seconds));
            handler.postDelayed(this, 0);
        }
    };
}
