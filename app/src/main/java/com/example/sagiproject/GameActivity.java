package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.os.Bundle; // ייבוא מחלקה לניהול מצב המסך
import android.util.Log; // ייבוא מחלקה לרישום לוגים (שגיאות/מידע)
import android.widget.Button; // ייבוא רכיב כפתור
import android.widget.Toast; // ייבוא רכיב להצגת הודעות קופצות קצרות
import androidx.appcompat.app.AppCompatActivity; // ייבוא מחלקת הבסיס למסכים
import com.example.sagiproject.gemini.GeminiCallback; // ייבוא ממשק תגובה מה-AI
import com.example.sagiproject.gemini.GeminiManager; // ייבוא מנהל הקשר עם Gemini AI
import java.util.ArrayList; // ייבוא רשימה דינמית

public class GameActivity extends AppCompatActivity { // הגדרת מחלקת מסך המשחק

    private Button[] leftBtns  = new Button[4]; // מערך לכפתורי המילים באנגלית (צד שמאל)
    private Button[] rightBtns = new Button[4]; // מערך לכפתורי המילים בעברית (צד ימין)

    private GameBoardView boardView; // רפרנס לתצוגה המותאמת אישית לציור קווים
    WordRepository wordRepository = new WordRepository(); // יצירת אובייקט מחסן המילים

    private GameManager gameManager; // רפרנס למנהל לוגיקת המשחק
    private FBsingleton fb; // רפרנס לסינגלטון הנתונים מול Firebase

    @Override // דריסת פונקציית היצירה של המסך
    protected void onCreate(Bundle savedInstanceState) { 
        super.onCreate(savedInstanceState); // קריאה לפונקציית האב
        setContentView(R.layout.activity_game); // הגדרת קובץ העיצוב של המסך

        initViews(); // קריאה לפונקציה המאתחלת את רכיבי הממשק

        fb = FBsingleton.getInstance(this); // קבלת המופע של FBsingleton
        gameManager = new GameManager(wordRepository); // יצירת מנהל המשחק עם המחסן
        gameManager.startNewGame(GameManager.LEVEL_EASY, "Player"); // התחלת משחק חדש ברמה קלה

        fb.setOnDataLoadedListener(() -> runOnUiThread(this::refreshGameData)); // הגדרת מאזין שיפעיל רענון נתונים כשהמידע נטען מהמסד

        for (int i = 0; i < 4; i++) { // לולאה להגדרת מאזיני לחיצה לכל 8 הכפתורים
            final int index = i; // שמירת האינדקס כקבוע עבור ה-lambda
            leftBtns[i].setOnClickListener(v -> onLeftClicked(index)); // הגדרת לחיצה על כפתור שמאלי
            rightBtns[i].setOnClickListener(v -> onRightClicked(index)); // הגדרת לחיצה על כפתור ימני
        }
    }

    private void initViews() { // פונקציה למציאת רכיבי ה-UI בקובץ ה-XML
        boardView = findViewById(R.id.boardView); // מציאת ה-View של הלוח

        for (int i = 0; i < 4; i++) { // לולאה למציאת הכפתורים לפי ה-ID שלהם באופן דינמי
            leftBtns[i] = findViewById(getResources().getIdentifier("btnL" + i, "id", getPackageName())); 
            rightBtns[i] = findViewById(getResources().getIdentifier("btnR" + i, "id", getPackageName()));
        }
    }

    private void refreshGameData() { // פונקציה לרענון נתוני המשחק והצגת סיבוב חדש
        if (fb.allWords != null && !fb.allWords.isEmpty()) { // אם יש מילים ב-Firebase
            wordRepository.getEasyWords().clear(); // נקה את המחסן המקומי
            wordRepository.getEasyWords().addAll(fb.allWords); // הוסף את כל המילים מהמסד למחסן
        }

        if (wordRepository.getEasyWords().size() < fb.currentIndex + 4) { // בדיקה אם חסרות מילים להמשך המשחק
            requestNewWordsFromAI(); // אם חסר, בקש מילים חדשות מה-AI
            return; // עצור את הפונקציה עד שהמילים יגיעו
        }

        RoundData round = gameManager.startRound(fb.currentIndex); // התחלת סיבוב חדש במיקום הנוכחי
        applyRoundToButtons(round); // הצגת המילים על הכפתורים
    }

    private void requestNewWordsFromAI() { // פונקציה לבקשת מילים מ-Gemini
        String prompt = "Return ONLY 4 pairs of English-Hebrew words. Format: Eng - Heb | Eng - Heb. Avoid: " 
                        + wordListToString(wordRepository.getEasyWords()); // יצירת הבקשה ל-AI כולל רשימת מילים קיימות למניעת כפילות

        GeminiManager.getInstance().sendMessage(prompt, new GeminiCallback() { // שליחת ההודעה ל-AI
            @Override
            public void onSuccess(String response) { // טיפול במקרה של הצלחה
                runOnUiThread(() -> { // הרצה על ה-UI Thread כי עדכון נתונים משפיע על הממשק
                    ArrayList<WordPair> newPairs = parseRobust(response); // פירוח תגובת ה-AI לאובייקטים
                    if (newPairs.size() >= 4) fb.addWords(newPairs); // אם התקבלו מספיק מילים, הוסף אותן למסד
                });
            }
            @Override public void onError(Throwable t) { Log.e("Sagi", "AI Error", t); } // רישום שגיאת מערכת
            @Override public void onError(Exception e) { Log.e("Sagi", "AI Error", e); } // רישום שגיאת קוד
        });
    }

    private ArrayList<WordPair> parseRobust(String response) { // פונקציה לפירוק (Parsing) של הטקסט מה-AI
        ArrayList<WordPair> pairs = new ArrayList<>(); // יצירת רשימה זמנית
        String clean = response.replaceAll("\\*", "").replace("\n", " ").replaceAll("\\d+\\.", ""); // ניקוי תווים מיותרים
        for (String p : clean.split("\\|")) { // פיצול לפי הסימן מפריד הזוגות
            String[] split = p.split("-"); // פיצול המילה באנגלית מהמילה בעברית
            if (split.length == 2) pairs.add(new WordPair(split[0].trim(), split[1].trim())); // הוספה לרשימה אם המבנה תקין
        }
        return pairs; // החזרת הרשימה המפורקת
    }

    private void onRightClicked(int index) { // טיפול בלחיצה על מילה בעברית
        MatchResult result = gameManager.selectRight(index); // בדיקה מול מנהל המשחק אם זו התאמה
        handleMatchVisuals(result); // עדכון הממשק (ציור קו/צבע) לפי התוצאה

        if (result.isRoundFinished) { // אם הסתיים הסיבוב (נמצאו כל 4 ההתאמות)
            fb.setScore(fb.currentIndex + 4); // עדכון הניקוד/מיקום ב-Firebase
            Toast.makeText(this, "מצוין! עוברים הלאה.", Toast.LENGTH_SHORT).show(); // הצגת הודעת עידוד
        }
    }

    private void handleMatchVisuals(MatchResult result) { // עדכון הצד הוויזואלי של ההתאמה
        for (Button b : leftBtns) b.setBackgroundResource(R.drawable.button_background); // איפוס צבע הרקע של כפתורי שמאל
        if (result.leftIndex != null && result.rightIndex != null) { // אם נבחרו שני צדדים
            boardView.addAnimatedLineBetweenButtons(leftBtns[result.leftIndex], rightBtns[result.rightIndex], result.isCorrect); // ציור קו מונפש
            if (result.isCorrect) { // אם ההתאמה נכונה
                leftBtns[result.leftIndex].setEnabled(false); // נטרול הכפתור השמאלי
                rightBtns[result.rightIndex].setEnabled(false); // נטרול הכפתור הימני
                leftBtns[result.leftIndex].setAlpha(0.5f); // הפיכת הכפתור לשקוף למחצה
                rightBtns[result.rightIndex].setAlpha(0.5f); // הפיכת הכפתור לשקוף למחצה
            }
        }
    }

    private void applyRoundToButtons(RoundData round) { // פונקציה להצבת הטקסט בתוך הכפתורים בתחילת סיבוב
        for (int i = 0; i < 4; i++) { 
            leftBtns[i].setText(round.left[i]); // הגדרת מילה באנגלית
            leftBtns[i].setEnabled(true); // הפעלת הכפתור
            leftBtns[i].setAlpha(1f); // החזרת השקיפות למלאה
            leftBtns[i].setBackgroundResource(R.drawable.button_background); // איפוס העיצוב
            rightBtns[i].setText(round.right[i]); // הגדרת מילה בעברית
            rightBtns[i].setEnabled(true); // הפעלת הכפתור
            rightBtns[i].setAlpha(1f); // החזרת השקיפות למלאה
            rightBtns[i].setBackgroundResource(R.drawable.button_background); // איפוס העיצוב
        }
        boardView.resetLines(); // ניקוי הקווים שצוירו בסיבוב הקודם
    }

    private void onLeftClicked(int index) { // טיפול בלחיצה על מילה באנגלית
        gameManager.selectLeft(index); // עדכון מנהל המשחק בבחירה
        for (Button b : leftBtns) { // מעבר על כל כפתורי שמאל
            if (b.isEnabled()) b.setBackgroundResource(R.drawable.button_background); // איפוס אלו שפעילים
        }
        leftBtns[index].setBackgroundResource(R.drawable.button_selected); // סימון הכפתור הנבחר בצבע שונה
    }

    public static String wordListToString(ArrayList<WordPair> list) { // פונקציית עזר להמרת רשימת מילים למחרוזת
        StringBuilder sb = new StringBuilder(); 
        for (WordPair wp : list) sb.append(wp.english).append(", "); // הוספת כל מילה באנגלית עם פסיק
        return sb.toString(); // החזרת המחרוזת המלאה
    }
}
