package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.content.Context; // ייבוא הקשר האפליקציה (Context)
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

public class RegistrationFragment extends Fragment { // הגדרת פרגמנט האחראי על מסך ההרשמה

    private FirebaseAuth mAuth; // משתנה לניהול אימות המשתמש מול Firebase
    private EditText etEmail, etPassword, etName; // משתנים לשדות הזנת אימייל, סיסמה ושם
    private Button btnRegister; // משתנה לכפתור ההרשמה
    private Context context; // משתנה לשמירת ההקשר של האפליקציה

    public RegistrationFragment() {
        // בנאי ריק הנדרש עבור פרגמנטים
    }

    @Override // פונקציה ליצירת התצוגה של הפרגמנט
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // טעינת קובץ העיצוב XML של הפרגמנט והחזרתו
        return inflater.inflate(R.layout.fragment_registration, container, false);
    }

    @Override // פונקציה שנקראת לאחר שהתצוגה נוצרה בהצלחה
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState); // קריאה לפונקציית האב

        // 1. אתחול אובייקט האימות של Firebase
        mAuth = FirebaseAuth.getInstance();
        
        // 2. מציאת רכיבי הממשק בתוך ה-View שנוצר
        etEmail = view.findViewById(R.id.etEmailAddress); // שדה אימייל
        etPassword = view.findViewById(R.id.etNumberPassword); // שדה סיסמה
        etName = view.findViewById(R.id.etName); // שדה שם משתמש
        btnRegister = view.findViewById(R.id.btnRegister); // כפתור הרשמה
        this.context = getContext(); // שמירת ההקשר לשימוש מאוחר יותר

        // 3. הגדרת מאזין ללחיצה על כפתור ההרשמה
        btnRegister.setOnClickListener(v -> handleRegistration());
    }

    private void handleRegistration() { // פונקציה לניהול תהליך ההרשמה
        String email = etEmail.getText().toString().trim(); // קבלת האימייל מהשדה וניקוי רווחים
        String password = etPassword.getText().toString().trim(); // קבלת הסיסמה מהשדה וניקוי רווחים
        String name = etName.getText().toString().trim(); // קבלת השם מהשדה וניקוי רווחים

        if (email.isEmpty() || password.isEmpty() || name.isEmpty()) { // בדיקה אם אחד השדות ריק
            Toast.makeText(getContext(), "All fields are required", Toast.LENGTH_SHORT).show(); // הצגת הודעת שגיאה
            return; // עצירת הפונקציה
        }

        // יצירת משתמש חדש ב-Firebase באמצעות אימייל וסיסמה
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(requireActivity(), task -> { // הגדרת פעולה לסיום התהליך
                    if (task.isSuccessful()) { // אם ההרשמה הצליחה
                        Toast.makeText(getContext(), "Registration Successful!", Toast.LENGTH_SHORT).show();

                        // אתחול הנתונים למשתמש החדש בסינגלטון
                        FBsingleton fb = FBsingleton.getInstance(context);
                        fb.setName(name); // שמירת השם ב-Firebase
                        fb.setScore(0); // איפוס הניקוד ב-Firebase
                        fb.initializeDefaultWords(); // העלאת מילות ברירת מחדל ל-Firebase

                        // מעבר למסך הראשי (MainActivity)
                        Intent intent = new Intent(getActivity(), MainActivity.class);
                        startActivity(intent);

                        if (getActivity() != null) {
                            getActivity().finish(); // סגירת מסך ההרשמה
                        }
                    } else { // אם ההרשמה נכשלה
                        // הצגת הודעת שגיאה עם סיבת הכישלון מהמערכת
                        Toast.makeText(getContext(), "Registration Failed: " +
                                task.getException().getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
    }
}
