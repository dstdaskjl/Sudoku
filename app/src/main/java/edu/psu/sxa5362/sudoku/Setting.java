package edu.psu.sxa5362.sudoku;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class Setting extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private CustomSharedPreferences setting;

    private ImageButton backButton;
    private EditText nicknameView;
    private Button confirmButton;
    private Spinner musicSpinner;
    private Switch timerSwitch;
    private Switch hintSwitch;
    private Switch mistakeSwitch;
    private Switch soundEffectSwitch;
    private Switch bgmSwitch;

    private String nickname;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.setting);

        mAuth = FirebaseAuth.getInstance();
        mUser = mAuth.getCurrentUser();

        // Get all views
        getViews();

        // Back button listener
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Setting.this, Home.class));
                finish();
            }
        });

        // Retrieve nickname from Firebase
        setNickname();

        // Set nickname text change listener
        setNicknameChangeListener();

        // Nickname change listener
        confirmButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateNickname();
            }
        });

        // Music selection spinner listener
        musicSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String bgm = musicSpinner.getSelectedItem().toString();
                if (bgm.equals("Select a song") == false){
                    bgmSwitch.setChecked(true);
                    BGM.isPlaying = true;
                    Intent intent = new Intent(Setting.this, BGM.class);
                    stopService(intent);
                    intent.putExtra("bgm", bgm);
                    startService(intent);
                    setting.putString("song", bgm);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        // Set switch listeners
        setSwitchListeners();
    }

    private void getViews(){
        setting = new CustomSharedPreferences(this, "Setting");
        backButton = findViewById(R.id.setting_ibutton_back);
        nicknameView = findViewById(R.id.setting_edit_nickname);
        confirmButton = findViewById(R.id.setting_button_confirm);
        musicSpinner = findViewById(R.id.setting_spinner_bgm);
        timerSwitch = findViewById(R.id.setting_switch_timer);
        hintSwitch = findViewById(R.id.setting_switch_hints);
        mistakeSwitch = findViewById(R.id.setting_switch_mistakes);
        soundEffectSwitch = findViewById(R.id.setting_switch_sound_effect);
        bgmSwitch = findViewById(R.id.setting_switch_bgm);

        ArrayList<String> musicList = new ArrayList<String>(){
            {
                add("Select a song");
                add("Faded");
                add("Firefly");
                add("Nekozilla");
                add("Puzzle");
                add("Shallow");
                add("Throwback");
            }
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, musicList);
        musicSpinner.setAdapter(adapter);
    }

    private void setNickname(){
        DatabaseReference databaseReference = Firebase.getDatabaseReference("User", mUser.getUid());
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                nickname = dataSnapshot.getValue(String.class);
                nicknameView.setText(nickname);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setNicknameChangeListener(){
        nicknameView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.toString().equals(nickname)){
                    confirmButton.setEnabled(false);
                    confirmButton.setTextColor(Color.LTGRAY);
                }
                else{
                    confirmButton.setEnabled(true);
                    confirmButton.setTextColor(Color.parseColor("#2196F3"));
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    private void updateNickname(){
        final String newNickname = nicknameView.getText().toString();
        DatabaseReference databaseReference = Firebase.getDatabaseReference("User");
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for (DataSnapshot ds : dataSnapshot.getChildren()){
                    if (newNickname.equals(ds.child(ds.getKey()).getValue(String.class))){
                        new android.app.AlertDialog.Builder(Setting.this)
                                .setMessage("Nickname already exists. Please try others.")
                                .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                    @Override
                                    public void onClick(DialogInterface dialog, int id){
                                        dialog.dismiss();
                                    }
                                })
                                .create()
                                .show();
                        break;
                    }
                }
                AuthCredential credential = EmailAuthProvider.getCredential(nickname + "@email.com", "123123");
                mUser.reauthenticate(credential).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        mUser.updateEmail(newNickname + "@email.com").addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    Firebase.insert("User/" + mUser.getUid(), newNickname);
                                    new android.app.AlertDialog.Builder(Setting.this)
                                            .setMessage("Your nickname has been updated.")
                                            .setPositiveButton("Ok", new DialogInterface.OnClickListener(){
                                                @Override
                                                public void onClick(DialogInterface dialog, int id){
                                                    nickname = newNickname;
                                                    confirmButton.setEnabled(false);
                                                    confirmButton.setTextColor(Color.LTGRAY);
                                                    dialog.dismiss();
                                                }
                                            })
                                            .create()
                                            .show();
                                }
                                else{
                                    Toast.makeText(Setting.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();
                                }
                            }
                        });
                    }
                });
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void setSwitchListeners(){
        // Timer switch
        if (setting.getBoolean("timer")){
            timerSwitch.setChecked(true);
        }
        else{
            timerSwitch.setChecked(false);
        }
        timerSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setting.putBoolean("timer", true);
                }
                else{
                    setting.putBoolean("timer", false);
                }
            }
        });

        // Hint switch
        if (setting.getBoolean("unlimitedHints")){
            hintSwitch.setChecked(true);
        }
        else{
            hintSwitch.setChecked(false);
        }
        hintSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setting.putBoolean("unlimitedHints", true);
                }
                else{
                    setting.putBoolean("unlimitedHints", false);
                }
                new CustomSharedPreferences(Setting.this, "Sudoku").reset();
            }
        });

        // Mistake switch
        if (setting.getBoolean("unlimitedMistakes")){
            mistakeSwitch.setChecked(true);
        }
        else{
            mistakeSwitch.setChecked(false);
        }
        mistakeSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setting.putBoolean("unlimitedMistakes", true);
                }
                else{
                    setting.putBoolean("unlimitedMistakes", false);
                }
                new CustomSharedPreferences(Setting.this, "Sudoku").reset();
            }
        });

        // Sound effect
        if (setting.getBoolean("soundEffect")){
            soundEffectSwitch.setChecked(true);
        }
        else{
            soundEffectSwitch.setChecked(false);
        }
        soundEffectSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked){
                    setting.putBoolean("soundEffect", true);
                }
                else{
                    setting.putBoolean("soundEffect", false);
                }
            }
        });

        // Background music
        if (setting.getBoolean("bgm")){
            bgmSwitch.setChecked(true);
        }
        else{
            bgmSwitch.setChecked(false);
        }
        bgmSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent intent = new Intent(Setting.this, BGM.class);
                if (isChecked){
                    String bgm = musicSpinner.getSelectedItem().toString();
                    if (bgm.equals("Select a song") == false){
                        intent.putExtra("bgm", bgm);
                        startService(intent);
                        BGM.isPlaying = true;
                    }
                    setting.putBoolean("bgm", true);
                }
                else{
                    stopService(intent);
                    setting.putBoolean("bgm", false);
                    BGM.isPlaying = false;
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        
    }
}
