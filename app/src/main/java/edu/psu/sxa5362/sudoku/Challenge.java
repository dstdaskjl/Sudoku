package edu.psu.sxa5362.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CalendarView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.time.LocalDate;
import java.util.ArrayList;

import sun.bob.mcalendarview.MCalendarView;
import sun.bob.mcalendarview.vo.DateData;

public class Challenge extends AppCompatActivity {
    private MCalendarView calendarView;
    private Button gameButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.challenge);

        // Mark calendar
        calendarView = (MCalendarView) findViewById(R.id.challenge_calendar_daily);
        markDates();

        // Set up game button
        gameButton = findViewById(R.id.challenge_button_daily);
        gameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Challenge.this, DifficultyPopup.class));
                finish();
            }
        });

        // If the user already did daily challenge, view should be gone
        setButtonVisibility();

        // Set navigation
        setNavigationView();
    }

    private void markDates(){
        DatabaseReference databaseReference = Firebase.getDatabaseReference("Daily", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    String date = ds.getValue(String.class);
                    String[] dateArray = date.split("-");
                    calendarView.markDate(Integer.valueOf(dateArray[0]), Integer.valueOf(dateArray[1]), Integer.valueOf(dateArray[2]));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setButtonVisibility(){
        final String today = String.valueOf(LocalDate.now());
        DatabaseReference databaseReference = Firebase.getDatabaseReference("Daily", FirebaseAuth.getInstance().getCurrentUser().getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                gameButton.setVisibility(View.VISIBLE);
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (ds.getValue(String.class).equals(today)){
                        gameButton.setVisibility(View.GONE);
                        break;
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNavigationView(){
        BottomNavigationView navigationView = findViewById(R.id.fragment_navigation);
        navigationView.setSelectedItemId(R.id.navigation_challenge);
        navigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Intent intent;
                switch(item.getItemId()){
                    case R.id.navigation_home:
                        intent = new Intent(getApplicationContext(), Home.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_challenge:
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
