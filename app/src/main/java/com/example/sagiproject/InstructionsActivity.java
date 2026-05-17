package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.content.Intent; // ייבוא מחלקה למעבר בין מסכים
import android.os.Bundle; // ייבוא מחלקה לניהול מצב המסך
import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת הבסיס למסכים

public class InstructionsActivity extends AppCompatActivity { // הגדרת מסך ההוראות

    @Override // דריסת פונקציית היצירה של המסך
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לפונקציית האב
        setContentView(R.layout.activity_instructions); // חיבור קובץ העיצוב (XML) למסך

        // מציאת כפתור התחלת המשחק מתוך מסך ההוראות והגדרת מאזין ללחיצה
        findViewById(R.id.btnStartFromInstructions).setOnClickListener(v -> {
            startActivity(new Intent(this, GameActivity.class)); // מעבר ישיר למסך המשחק
            finish(); // סגירת מסך ההוראות כדי שלא יחזור אליו כשלוחצים 'חזרה' מהמשחק
        });
    }
}
