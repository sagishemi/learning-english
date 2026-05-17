package com.example.sagiproject.gemini; // הגדרת החבילה עבור רכיבי ה-AI

import android.graphics.Bitmap; // ייבוא מחלקה לטיפול בתמונות

import androidx.annotation.NonNull; // הערה המציינת שפרמטר לא יכול להיות null

import com.google.ai.client.generativeai.GenerativeModel; // ייבוא מחלקת המודל הגנרטיבי של Google
import com.google.ai.client.generativeai.type.Content; // ייבוא סוג נתונים לתוכן (טקסט/תמונה)
import com.google.ai.client.generativeai.type.GenerateContentResponse; // ייבוא אובייקט התגובה מה-AI
import com.google.ai.client.generativeai.type.ImagePart; // ייבוא חלק המייצג תמונה בתוכן
import com.google.ai.client.generativeai.type.Part; // ייבוא ממשק בסיס לחלקי תוכן
import com.google.ai.client.generativeai.type.TextPart; // ייבוא חלק המייצג טקסט בתוכן

import java.util.ArrayList; // ייבוא רשימה דינמית
import java.util.List; // ייבוא ממשק רשימה

import kotlin.Result; // ייבוא מחלקת תוצאה של Kotlin (עבור ה-SDK)
import kotlin.coroutines.Continuation; // ייבוא ממשק להמשכיות של Coroutines
import kotlin.coroutines.CoroutineContext; // ייבוא הקשר עבור Coroutines
import kotlin.coroutines.EmptyCoroutineContext; // ייבוא הקשר ריק

public class GeminiManager { // הגדרת מחלקה לניהול התקשורת עם Gemini AI
    public static final String API_KEY = "AIzaSyCuXe_BCAnmPDnh_kVwDWZDHj0jTiPb00Q"; // מפתח ה-API לגישה לשירותי Google AI
    private static GeminiManager instance; // משתנה סטטי להחזקת מופע יחיד (Singleton)
    private GenerativeModel gemini; // אובייקט המודל של Gemini

    private GeminiManager() { // בנאי פרטי לאתחול המודל
        // אתחול המודל עם שם המודל ומפתח ה-API
        gemini = new GenerativeModel(
                "gemini-1.5-flash", // שם המודל (עודכן לגרסה נפוצה)
                API_KEY
        );
    }

    public static GeminiManager getInstance() { // פונקציה סטטית לקבלת המופע של המנהל
        if (null == instance) { // אם המופע עדיין לא נוצר
            instance = new GeminiManager(); // צור מופע חדש
        }
        return instance; // החזר את המופע הקיים
    }

    public void sendMessage(String prompt, GeminiCallback callback) { // פונקציה לשליחת הודעת טקסט ל-AI
        gemini.generateContent(prompt, // קריאה לפעולת יצירת תוכן מהמודל
                new Continuation<GenerateContentResponse>() { // יצירת אובייקט שמחכה לתשובה (Callback של Coroutine)
                    @NonNull
                    @Override
                    public CoroutineContext getContext() { // הגדרת ההקשר שבו תבוצע הפעולה
                        return EmptyCoroutineContext.INSTANCE; // שימוש בהקשר ריק
                    }

                    @Override
                    public void resumeWith(@NonNull Object result) { // פונקציה שנקראת כשהתשובה חוזרת
                        if (result instanceof Result.Failure) { // אם התרחשה שגיאה בתהליך
                            callback.onError(((Result.Failure) result).exception); // דווח על השגיאה דרך ה-callback
                        } else { // אם התקבלה תשובה מוצלחת
                            callback.onSuccess(((GenerateContentResponse) result).getText()); // שלח את הטקסט שנוצר ל-callback
                        }
                    }
                }
        );
    }

    public void sendMessageWithPhoto(String prompt, Bitmap photo, GeminiCallback callback) { // פונקציה לשליחת טקסט ותמונה יחד
        List<Part> parts = new ArrayList<Part>(); // יצירת רשימה לחלקי ההודעה
        parts.add(new TextPart(prompt)); // הוספת החלק הטקסטואלי
        parts.add(new ImagePart(photo)); // הוספת החלק של התמונה
        Content[] content = new Content[1]; // יצירת מערך תוכן
        content[0] = new Content(parts); // הכנסת החלקים לתוכן

        gemini.generateContent(content, // קריאה ליצירת תוכן עם קלט מולטי-מודאלי
                new Continuation<GenerateContentResponse>() { // הגדרת אובייקט ההמשכיות
                    @NonNull
                    @Override
                    public CoroutineContext getContext() {
                        return EmptyCoroutineContext.INSTANCE;
                    }

                    @Override
                    public void resumeWith(@NonNull Object result) {
                        if (result instanceof Result.Failure) {
                            callback.onError(((Result.Failure) result).exception);
                        } else {
                            callback.onSuccess(((GenerateContentResponse) result).getText());
                        }
                    }
                }
        );
    }
}
