package edu.psu.sxa5362.sudoku;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ExpandableListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Ranking extends AppCompatActivity {

    private RankingExpandableListViewAdapter listAdapter;
    private ExpandableListView expListView;
    private BottomNavigationView navigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ranking_listview);

        // Set adapter
        DatabaseReference databaseReference = Firebase.getDatabaseReference("");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                listAdapter = new RankingExpandableListViewAdapter(Ranking.this
                        , getResultMap(dataSnapshot.child("Record")), getUidMap(dataSnapshot.child("User")));
                expListView = findViewById(R.id.ranking_listview);
                expListView.setAdapter(listAdapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        // Set navigation
        setNavigationView();
    }

    private Map<String, List<Result>> getResultMap(DataSnapshot dataSnapshot){
        Map<String, Map<String, List<Long>>> allMap = new HashMap<>();
        for (DataSnapshot outerDS : dataSnapshot.getChildren()){
            String level = outerDS.getKey();
            Map<String, List<Long>> resultMap = new HashMap<>();
            for (DataSnapshot innerDS : outerDS.getChildren()){
                String uid = innerDS.getKey();
                List<Long> times = (ArrayList) innerDS.getValue();
                resultMap.put(uid, times);
            }
            allMap.put(level, resultMap);
        }

        Map<String, List<Result>> resultMap = new HashMap<>();
        for (String level : allMap.keySet()){
            List<Result> resultList = new ArrayList<>();
            Map<String, List<Long>> timeMap = allMap.get(level);
            for (String uid : timeMap.keySet()){
                List<Long> timeList = timeMap.get(uid);
                for (long time : timeList){
                    Result result = new Result(uid, time);
                    resultList.add(result);
                }
            }
            Collections.sort(resultList, Comparator.comparing(Result::getTime).thenComparing(Result::getUid));
            if (resultList.size() > 100){
                resultMap.put(level, resultList.subList(0, 100));
            }
            else{
                resultMap.put(level, resultList);
            }
        }
        return resultMap;
    }

    private Map<String, String> getUidMap(DataSnapshot dataSnapshot){
        Map<String, String> uidMap = new HashMap<>();
        for (DataSnapshot ds : dataSnapshot.getChildren()){
            uidMap.put(ds.getKey(), ds.getValue(String.class));
        }
        return uidMap;
    }

    private void setNavigationView(){
        navigationView = findViewById(R.id.fragment_navigation);
        navigationView.setSelectedItemId(R.id.navigation_ranking);
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
                        intent = new Intent(getApplicationContext(), Challenge.class);
                        startActivity(intent);
                        return true;
                    case R.id.navigation_ranking:
                        return true;
                }
                return true;
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
