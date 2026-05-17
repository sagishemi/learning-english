package com.example.sagiproject; // הצהרה על החבילה של הפרויקט

public class MatchResult { // הגדרת מחלקה המייצגת את התוצאה של ניסיון התאמה בין שתי מילים
    public final boolean isCorrect; // משתנה בוליאני המציין האם ההתאמה נכונה
    public final boolean isRoundFinished; // משתנה בוליאני המציין האם הסיבוב הסתיים (כל הזוגות נמצאו)
    public final int currentPlayerIndex; // אינדקס השחקן הנוכחי (רלוונטי למשחק מרובה משתתפים)
    public final Integer leftIndex; // האינדקס של המילה שנבחרה בצד שמאל
    public final Integer rightIndex; // האינדקס של המילה שנבחרה בצד ימין

    public MatchResult(boolean isCorrect, boolean isRoundFinished, int currentPlayerIndex,
                       Integer leftIndex, Integer rightIndex) { // בנאי המאתחל את כל נתוני התוצאה
        this.isCorrect = isCorrect; // השמת האם ההתאמה נכונה
        this.isRoundFinished = isRoundFinished; // השמת האם הסיבוב הסתיים
        this.currentPlayerIndex = currentPlayerIndex; // השמת אינדקס השחקן
        this.leftIndex = leftIndex; // השמת אינדקס צד שמאל
        this.rightIndex = rightIndex; // השמת אינדקס צד ימין
    }
}
