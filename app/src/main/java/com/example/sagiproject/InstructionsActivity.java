package com.example.sagiproject;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

public class InstructionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_instructions);

        findViewById(R.id.btnStartFromInstructions).setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class));
            finish();
        });
    }
}
