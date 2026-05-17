package com.example.sagiproject.gemini; // הגדרת החבילה עבור רכיבי ה-AI

/**
 * ממשק (Interface) המשמש כ-Callback לקבלת תשובות מה-AI.
 * הממשק מאפשר למחלקה הקוראת לדעת מתי התקבלה תשובה מוצלחת ומתי אירעה שגיאה.
 */
public interface GeminiCallback {
    
    /**
     * פונקציה שנקראת כאשר ה-AI מחזיר תשובה בהצלחה.
     * @param result הטקסט שנוצר על ידי ה-AI.
     */
    void onSuccess(String result);

    /**
     * פונקציה שנקראת כאשר מתרחשת שגיאה חמורה (Throwable).
     * @param error אובייקט השגיאה.
     */
    void onError(Throwable error);

    /**
     * פונקציה שנקראת כאשר מתרחשת חריגה (Exception) במהלך הקריאה.
     * @param e אובייקט החריגה.
     */
    void onError(Exception e);
}
