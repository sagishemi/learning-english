package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.content.Intent; // ייבוא מחלקה למעבר בין מסכים
import android.os.Bundle; // ייבוא מחלקה לניהול מצב הפרגמנט
import android.view.LayoutInflater; // ייבוא מחלקה לטעינת קובץ עיצוב XML
import android.view.View; // ייבוא מחלקת הבסיס לרכיבי תצוגה
import android.view.ViewGroup; // ייבוא מחלקה לניהול קבוצות רכיבים
import android.widget.Button; // ייבוא רכיב כפתור
import android.widget.EditText; // ייבוא רכיב להזנת טקסט
import android.widget.Toast; // ייבוא רכיב להצגת הודעות קופצות

import androidx.annotation.NonNull; // הערה המציינת שפרמטר לא יכול להיות null
import androidx.annotation.Nullable; // הערה המציינת שפרמטר יכול להיות null
import androidx.fragment.app.Fragment; // ייבוא מחלקת הבסיס לפרגמנטים

import com.google.firebase.auth.FirebaseAuth; // ייבוא רכיב האימות של Firebase

public class LoginFragment extends Fragment { // הגדרת פרגמנט האחראי על מסך ההתחברות

    private FirebaseAuth mAuth; // משתנה לניהול אימות המשתמש מול Firebase
    private EditText etEmail, etPassword; // משתנים לשדות הזנת אימייל וסיסמה
    private Button btnLogin; // משתנה לכפתור ההתחברות

    public LoginFragment() {
        // בנאי ריק הנדרש עבור פרגמנטים
    }

    @Override // פונקציה ליצירת התצוגה של הפרגמנט
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // טעינת קובץ העיצוב XML של הפרגמנט והחזרתו
        return inflater.inflate(R.layout.fragment_login, container, false);
    }

    @Override // פונקציה שנקראת לאחר שהתצוגה נוצרה בהצלחה
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // קריאה לפונקציית האב

        // 1. אתחול אובייקט האימות של Firebase
        mAuth = FirebaseAuth.getInstance();

        // 2. מציאת רכיבי הממשק בתוך ה-View שנוצר
        etEmail = view.findViewById(R.id.etEmailAddress); // שדה אימייל
        etPassword = view.findViewById(R.id.etNumberPassword); // שדה סיסמה
        btnLogin = view.findViewById(R.id.btnLogin); // כפתור התחברות

        // 3. הגדרת מאזין ללחיצה על כפתור ההתחברות
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                performLogin(); // קריאה לפונקציית ביצוע ההתחברות
            }
        });
    }

    private void performLogin() { // פונקציה לניהול תהליך ההתחברות
        String email = etEmail.getText().toString().trim(); // קבלת האימייל מהשדה וניקוי רווחים
        String password = etPassword.getText().toString().trim(); // קבלת הסיסמה מהשדה וניקוי רווחים

        if (email.isEmpty() || password.isEmpty()) { // בדיקה אם אחד השדות ריק
            Toast.makeText(getContext(), "Please enter details", Toast.LENGTH_SHORT).show(); // הצגת הודעת שגיאה
            return; // עצירת הפונקציה
        }

        // ביצוע התחברות מול Firebase באמצעות אימייל וסיסמה
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> { // הגדרת פעולה לסיום התהליך
                    if (task.isSuccessful()) { // אם ההתחברות הצליחה
                        // מעבר למסך הראשי (MainActivity)
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);
                        requireActivity().finish(); // סגירת המסך הנוכחי
                    } else { // אם ההתחברות נכשלה
                        // הצגת הודעת שגיאה עם סיבת הכישלון מהמערכת
                        Toast.makeText(getContext(), "Login failed: " + task.getException().getMessage(),
                                Toast.LENGTH_SHORT).show();
                    }
                });
    }
}
