package com.example.sagiproject; // הצהרה על החבילה של הפרויקט

public class WordPair { // הגדרת המחלקה שמייצגת זוג מילים (אנגלית ועברית)
    public String english; // משתנה למילה באנגלית
    public String hebrew; // משתנה למילה בעברית

    public WordPair(String english, String hebrew) { // בנאי (Constructor) שמקבל את שתי המילים
        this.english = english; // השמת המילה באנגלית למשתנה של המחלקה
        this.hebrew = hebrew; // השמת המילה בעברית למשתנה של המחלקה
    }

    public WordPair() {} // בנאי ריק הנדרש על ידי Firebase כדי לשחזר את האובייקט מהמסד

    public String getEnglish() { return english; } // פונקציה שמחזירה את המילה באנגלית
    public String getHebrew() { return hebrew; } // פונקציה שמחזירה את המילה בעברית
}
