package com.example.sagiproject; // הצהרה על החבילה אליה שייכת המחלקה

import android.content.Intent; // ייבוא מחלקה למעבר בין מסכים (Activities)
import android.os.Bundle; // ייבוא מחלקה לשמירת מצב המסך והעברת נתונים
import android.widget.Button; // ייבוא רכיב כפתור מהממשק
import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת הבסיס למסכים שתומכת בגרסאות ישנות
import com.google.firebase.auth.FirebaseAuth; // ייבוא רכיב האימות של Firebase

public class MainActivity extends AppCompatActivity { // הגדרת המחלקה שיורשת מ-AppCompatActivity (מסך באנדרואיד)

    @Override // מציין שאנחנו דורסים פונקציה קיימת ממחלקת האב
    protected void onCreate(Bundle savedInstanceState) { // הפונקציה שנקראת ברגע שהמסך נוצר
        super.onCreate(savedInstanceState); // קריאה לפונקציית היצירה של מחלקת האב כדי לאתחל את המסך
        setContentView(R.layout.activity_main); // חיבור קובץ העיצוב (XML) למחלקה הזו

        // כפתור התחלת משחק
        findViewById(R.id.btnStartGame).setOnClickListener(v -> { // חיפוש הכפתור לפי ה-ID שלו והגדרת פעולה בעת לחיצה
            startActivity(new Intent(this, GameActivity.class)); // מעבר למסך GameActivity (תחילת המשחק)
        });

        // כפתור הוראות
        findViewById(R.id.btnInstructions).setOnClickListener(v -> { // חיפוש כפתור ההוראות והגדרת פעולה בעת לחיצה
            startActivity(new Intent(this, InstructionsActivity.class)); // מעבר למסך InstructionsActivity (הצגת ההוראות)
        });

        // כפתור התנתקות
        findViewById(R.id.btnLogOut).setOnClickListener(v -> { // חיפוש כפתור ההתנתקות והגדרת פעולה בעת לחיצה
            FirebaseAuth.getInstance().signOut(); // ביצוע התנתקות (Sign Out) מהמשתמש ב-Firebase
            finish(); // סגירת המסך הנוכחי (חזרה למסך הקודם, בד"כ מסך ההתחברות)
        });

        // אתחול Firebase (סינגלטון)
        FBsingleton.getInstance(this); // גישה למחלקה שמנהלת את Firebase (סינגלטון) כדי לוודא שהיא מאותחלת
    }
}
