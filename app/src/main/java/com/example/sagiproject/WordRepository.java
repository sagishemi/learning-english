package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import java.util.ArrayList; // ייבוא מחלקת רשימה דינמית
import java.util.List; // ייבוא ממשק רשימה

public class WordRepository { // הגדרת מחלקה המשמשת כמחסן זמני למילים

    private ArrayList<WordPair> easyWords = new ArrayList<>(); // רשימה שתכיל מילים ברמה קלה
    private ArrayList<WordPair> hardWords = new ArrayList<>(); // רשימה שתכיל מילים ברמה קשה

    public WordRepository() { // בנאי של המחלקה
        loadDefaultWords(); // קריאה לפונקציה שטוענת מילים ראשוניות
    }

    private void loadDefaultWords() { // פונקציה להוספת מילים קבועות מראש למחסן
        easyWords.add(new WordPair("Dog", "כלב")); // הוספת זוג מילים לרמה קלה
        easyWords.add(new WordPair("Cat", "חתול")); // הוספת זוג מילים לרמה קלה
        easyWords.add(new WordPair("Sun", "שמש")); // הוספת זוג מילים לרמה קלה
        easyWords.add(new WordPair("Apple", "תפוח")); // הוספת זוג מילים לרמה קלה

        hardWords.add(new WordPair("Environment", "סביבה")); // הוספת זוג מילים לרמה קשה
        hardWords.add(new WordPair("Experience", "חוויה")); // הוספת זוג מילים לרמה קשה
        hardWords.add(new WordPair("Knowledge", "ידע")); // הוספת זוג מילים לרמה קשה
        hardWords.add(new WordPair("Responsibility", "אחריות")); // הוספת זוג מילים לרמה קשה
    }

    public List<WordPair> getWordsByLevel(int level) { // פונקציה שמחזירה רשימת מילים לפי רמת קושי
        if (level == 1) { // אם רמת הקושי היא 1 (EASY)
            return easyWords; // החזר את רשימת המילים הקלה
        } else { // לכל רמה אחרת (כמו HARD)
            return hardWords; // החזר את רשימת המילים הקשה
        }
    }

    public ArrayList<WordPair> getEasyWords() { // פונקציה שמחזירה את הרשימה הקלה באופן ישיר
        return easyWords; // החזרת הרשימה
    }
}
