package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.content.Intent; // ייבוא מחלקה למעבר בין מסכים
import android.os.Bundle; // ייבוא מחלקה לניהול מצב המסך
import android.widget.FrameLayout; // ייבוא רכיב מכולה להצגת פרגמנטים

import androidx.activity.EdgeToEdge; // ייבוא תמיכה בתצוגה מקצה לקצה
import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת הבסיס למסכים
import androidx.core.graphics.Insets; // ייבוא לניהול רווחים מהקצוות
import androidx.core.view.ViewCompat; // ייבוא תמיכה בגרסאות ישנות לרכיבי תצוגה
import androidx.core.view.WindowInsetsCompat; // ייבוא לניהול אזורי מערכת (כמו שורת מצב)
import androidx.fragment.app.Fragment; // ייבוא מחלקת הבסיס לפרגמנטים
import androidx.fragment.app.FragmentTransaction; // ייבוא מחלקה לביצוע פעולות החלפה בין פרגמנטים

import com.google.android.material.tabs.TabLayout; // ייבוא רכיב לשוניות (Tabs)
import com.google.firebase.auth.FirebaseAuth; // ייבוא רכיב האימות של Firebase
import com.google.firebase.auth.FirebaseUser; // ייבוא אובייקט המייצג משתמש מחובר

public class StartActivity extends AppCompatActivity { // הגדרת מסך הכניסה/הרשמה של האפליקציה

    FrameLayout frameLayout; // משתנה לרכיב שיחזיק את הפרגמנטים (התחברות או הרשמה)
    TabLayout tabLayout; // משתנה לרכיב הלשוניות למעבר בין התחברות להרשמה

    @Override // דריסת פונקציית היצירה של המסך
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState); // קריאה לפונקציית האב
        EdgeToEdge.enable(this); // הפעלת תצוגה מקצה לקצה
        setContentView(R.layout.activity_start); // חיבור קובץ העיצוב למסך

        frameLayout = findViewById(R.id.frameLayout); // מציאת ה-FrameLayout לפי ה-ID
        tabLayout = findViewById(R.id.tabLayout); // מציאת ה-TabLayout לפי ה-ID

        // הצגת פרגמנט ההתחברות כברירת מחדל בעת פתיחת המסך
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, new LoginFragment()).
                addToBackStack(null). // הוספה למחסנית החזרה (Back Stack)
                commit(); // ביצוע הפעולה

        // הגדרת מאזין לשינויים בלשוניות (מעבר בין 'התחברות' ל'הרשמה')
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {

            @Override
            public void onTabSelected(TabLayout.Tab tab) { // פונקציה שנקראת כשנבחרת לשונית
                Fragment fragment = null; // משתנה שיחזיק את הפרגמנט שנבחר

                switch (tab.getPosition()) { // בדיקה מהו מיקום הלשונית שנבחרה
                    case 0: // לשונית ראשונה
                        fragment = new LoginFragment(); // יצירת פרגמנט התחברות
                        break;
                    case 1: // לשונית שנייה
                        fragment = new RegistrationFragment(); // יצירת פרגמנט הרשמה
                        break;
                }

                // החלפת הפרגמנט הנוכחי בזה שנבחר עם אנימציית מעבר
                getSupportFragmentManager().beginTransaction().replace(R.id.frameLayout, fragment).
                        setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN).
                        commit();
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {} // כשעוזבים לשונית (לא בשימוש)

            @Override
            public void onTabReselected(TabLayout.Tab tab) {} // כשלוחצים שוב על לשונית קיימת (לא בשימוש)
        });

    }

    @Override // פונקציה שנקראת כשהמסך הופך לנראה למשתמש
    protected void onStart() {
        super.onStart(); // קריאה לפונקציית האב
        // בדיקה האם כבר יש משתמש שמחובר לאפליקציה
        FirebaseAuth mAuth = FirebaseAuth.getInstance(); // קבלת מופע האימות
        FirebaseUser currentUser = mAuth.getCurrentUser(); // קבלת המשתמש הנוכחי
        if(currentUser != null){ // אם נמצא משתמש מחובר
            Intent intent = new Intent(StartActivity.this, MainActivity.class); // יצירת כוונה לעבור למסך הראשי
            startActivity(intent); // ביצוע המעבר
            finish(); // סגירת המסך הנוכחי כדי שלא יוכלו לחזור אליו עם כפתור 'חזרה'
        }
    }

}
