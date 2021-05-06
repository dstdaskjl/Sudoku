package edu.psu.sxa5362.sudoku;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Landing extends AppCompatActivity {

    private EditText nicknameView;
    private Button playButton;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing);

        nicknameView = findViewById(R.id.landing_text_nickname);
        playButton = findViewById(R.id.landing_button_play);

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String name = nicknameView.getText().toString();
                if (name.equals("")){
                    nicknameView.setError("Enter Nickname");
                }
                else{
                    mAuth = FirebaseAuth.getInstance();
                    mAuth.createUserWithEmailAndPassword(name + "@email.com", "123123").addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                mUser = mAuth.getCurrentUser();
                                String uid = mUser.getUid();
                                Firebase.insert("User/" + uid, name);
                                putValuesIntoSharedPreferences();
                                startActivity(new Intent(Landing.this, Home.class));
                                finish();
                            }
                            else{
                                Toast.makeText(Landing.this, "Nickname already exists. Please try others.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();
        if (mUser != null){
            startActivity(new Intent(Landing.this, Home.class));
            finish();
        }
    }

    private void putValuesIntoSharedPreferences(){
        CustomSharedPreferences customSharedPreferences = new CustomSharedPreferences(this, "Setting");
        customSharedPreferences.putBoolean("timer", true);
        customSharedPreferences.putBoolean("unlimitedHints", false);
        customSharedPreferences.putBoolean("unlimitedMistakes", false);
        customSharedPreferences.putBoolean("soundEffect", true);
        customSharedPreferences.putBoolean("bgm", true);
        customSharedPreferences.putString("song", "Faded");
    }
}
