package edu.psu.sxa5362.sudoku;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class Firebase {
    public static DatabaseReference getDatabaseReference(String path1){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(path1);
        return databaseReference;
    }

    public static DatabaseReference getDatabaseReference(String path1, String path2){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(path1).child(path2);
        return databaseReference;
    }

    public static DatabaseReference getDatabaseReference(String path1, String path2, String path3){
        DatabaseReference databaseReference = FirebaseDatabase.getInstance().getReference().child(path1).child(path2).child(path3);
        return databaseReference;
    }

    public static void insert(String path, Object object){
        Map map = new HashMap();
        map.put(path, object);
        DatabaseReference mRef = FirebaseDatabase.getInstance().getReference();
        mRef.updateChildren(map);
    }
}
