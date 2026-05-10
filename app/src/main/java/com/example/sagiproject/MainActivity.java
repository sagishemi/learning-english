package com.example.sagiproject;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // כפתור התחלת משחק
        findViewById(R.id.btnStartGame).setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
        });

        // כפתור הוראות
        findViewById(R.id.btnInstructions).setOnClickListener(v -> {
            startActivity(new Intent(this, InstructionsActivity.class));
        });

        // כפתור התנתקות
        findViewById(R.id.btnLogOut).setOnClickListener(v -> {
            FirebaseAuth.getInstance().signOut();
            finish();
        });

        // אתחול Firebase (סינגלטון)
        FBsingleton.getInstance(this);
    }
}
