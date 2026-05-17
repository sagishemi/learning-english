package com.example.sagiproject; // הגדרת החבילה של הפרויקט

public class RoundData { // הגדרת מחלקה המייצגת נתונים של סיבוב בודד במשחק
    public String[] left; // מערך של מחרוזות עבור המילים שיופיעו בצד שמאל (למשל באנגלית)
    public String[] right; // מערך של מחרוזות עבור המילים שיופיעו בצד ימין (למשל בעברית)
    public int[] correctMapping; // מערך של מספרים שלמים המייצג את המיפוי הנכון בין צד שמאל לצד ימין

    public RoundData(String[] left, String[] right, int[] correctMapping) { // בנאי (Constructor) המקבל את כל נתוני הסיבוב
        this.left = left; // השמת מערך המילים השמאלי למשתנה של המחלקה
        this.right = right; // השמת מערך המילים הימני למשתנה של המחלקה
        this.correctMapping = correctMapping; // השמת מערך המיפוי הנכון למשתנה של המחלקה
    }
}
