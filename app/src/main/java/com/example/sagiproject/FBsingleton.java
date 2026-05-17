package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.content.Context; // ייבוא הקשר של האפליקציה (Context)
import androidx.annotation.NonNull; // הערה המציינת שפרמטר לא יכול להיות null
import com.google.firebase.auth.FirebaseAuth; // ספריית האימות של Firebase
import com.google.firebase.database.DataSnapshot; // אובייקט שמכיל נתונים שנקראו מ-Firebase
import com.google.firebase.database.DatabaseError; // אובייקט לטיפול בשגיאות בסיס נתונים
import com.google.firebase.database.DatabaseReference; // הפניה למיקום מסוים בבסיס הנתונים
import com.google.firebase.database.FirebaseDatabase; // הגישה הראשית לבסיס הנתונים של Firebase
import com.google.firebase.database.ValueEventListener; // מאזין לשינויים בנתונים בזמן אמת
import java.util.ArrayList; // שימוש ברשימה דינמית (ArrayList)

public class FBsingleton { // הצהרה על מחלקת הסינגלטון (מופע יחיד)
    private static FBsingleton instance; // משתנה סטטי שיחזיק את המופע היחיד של המחלקה
    public ArrayList<WordPair> allWords = new ArrayList<>(); // רשימה שתחזיק את כל זוגות המילים של המשתמש
    public int currentIndex = 0; // משתנה לשמירת הניקוד או האינדקס הנוכחי של המשתמש
    
    public interface OnDataLoadedListener { void onLoaded(); } // הגדרת "חוזה" (Interface) שיודיע מתי הנתונים נטענו
    private OnDataLoadedListener listener; // משתנה שיחזיק את המאזין לטעינת הנתונים

    private FBsingleton() { // בנאי (Constructor) פרטי - כדי שלא יהיה ניתן ליצור מופע מחוץ למחלקה
        loadData(); // קריאה לפונקציה שטוענת את הנתונים מ-Firebase
    }

    public static FBsingleton getInstance(Context ctx) { // פונקציה סטטית לקבלת המופע של המחלקה
        if (instance == null) instance = new FBsingleton(); // אם אין מופע - צור אחד חדש
        return instance; // החזר את המופע הקיים
    }

    public void setOnDataLoadedListener(OnDataLoadedListener l) { // פונקציה להגדרת המאזין לטעינת נתונים
        this.listener = l; // שמירת המאזין שנשלח
        if (!allWords.isEmpty() && listener != null) listener.onLoaded(); // אם הנתונים כבר קיימים, הפעל את המאזין מיד
    }

    private void loadData() { // פונקציה לטעינת נתונים מ-Firebase
        String uid = FirebaseAuth.getInstance().getUid(); // קבלת ה-ID הייחודי של המשתמש המחובר
        if (uid == null) return; // אם אין משתמש מחובר - צא מהפונקציה
        DatabaseReference userRef = FirebaseDatabase.getInstance().getReference("records/" + uid); // הפניה למיקום הנתונים של המשתמש
        userRef.addValueEventListener(new ValueEventListener() { // הוספת מאזין לשינויים בנתונים במיקום זה
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) { // פונקציה שנקראת כשהנתונים משתנים או נטענים
                if (!snapshot.exists()) return; // אם אין נתונים במיקום הזה - צא
                allWords.clear(); // נקה את רשימת המילים הנוכחית כדי למלא אותה מחדש
                for (DataSnapshot ds : snapshot.child("Words").getChildren()) { // לולאה על כל המילים שנמצאות תחת "Words"
                    WordPair wp = ds.getValue(WordPair.class); // המרת הנתון מהמסד לאובייקט מסוג WordPair
                    if (wp != null) allWords.add(wp); // אם ההמרה הצליחה, הוסף לרשימה
                }
                Integer score = snapshot.child("MyScore").getValue(Integer.class); // קריאת הניקוד מהמסד
                currentIndex = (score != null) ? score : 0; // עדכון הניקוד המקומי (אם ריק - שים 0)
                if (listener != null) listener.onLoaded(); // הודע למאזין שהנתונים נטענו בהצלחה
            }
            @Override public void onCancelled(@NonNull DatabaseError error) {} // טיפול במקרה של ביטול הקריאה
        });
    }

    public void initializeDefaultWords() { // פונקציה ליצירת מילים ראשוניות (ברירת מחדל)
        allWords.clear(); // ניקוי הרשימה
        allWords.add(new WordPair("Dog", "כלב")); // הוספת המילה כלב
        allWords.add(new WordPair("Cat", "חתול")); // הוספת המילה חתול
        allWords.add(new WordPair("Sun", "שמש")); // הוספת המילה שמש
        allWords.add(new WordPair("Apple", "תפוח")); // הוספת המילה תפוח
        save(); // שמירת המילים החדשות ל-Firebase
    }

    public void addWords(ArrayList<WordPair> newWords) { // פונקציה להוספת רשימת מילים חדשה
        for (WordPair nw : newWords) { // לולאה על כל מילה חדשה שהתקבלה
            boolean exists = false; // משתנה לבדיקה אם המילה כבר קיימת
            for (WordPair old : allWords) { // לולאה על המילים הקיימות
                if (old.english.equalsIgnoreCase(nw.english)) { exists = true; break; } // אם המילה קיימת
            }
            if (!exists) allWords.add(nw); // אם המילה לא קיימת - הוסף אותה לרשימה
        }
        save(); // שמור את הרשימה המעודכנת ל-Firebase
    }

    public void setScore(int score) { // פונקציה לעדכון הניקוד
        this.currentIndex = score; // עדכון המשתנה המקומי
        save(); // שמירה ל-Firebase
    }

    public void setName(String name) { // פונקציה לעדכון שם המשתמש במסד
        String uid = FirebaseAuth.getInstance().getUid(); // קבלת ה-ID של המשתמש
        if (uid != null) FirebaseDatabase.getInstance().getReference("records/" + uid + "/MyName").setValue(name); // שמירת השם במיקום המתאים
    }

    private void save() { // פונקציה פנימית ששומרת את כל המידע ל-Firebase
        String uid = FirebaseAuth.getInstance().getUid(); // קבלת ה-ID של המשתמש
        if (uid == null) return; // אם לא מחובר - אל תשמור
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("records/" + uid); // הפניה למיקום המשתמש
        ref.child("Words").setValue(allWords); // שמירת כל רשימת המילים
        ref.child("MyScore").setValue(currentIndex); // שמירת הניקוד הנוכחי
    }
}
