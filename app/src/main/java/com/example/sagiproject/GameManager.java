package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import java.util.ArrayList; // ייבוא מחלקת רשימה דינמית
import java.util.Collections; // ייבוא מחלקה לפעולות על אוספים (כמו ערבוב)
import java.util.List; // ייבוא ממשק רשימה

public class GameManager { // הגדרת המחלקה שמנהלת את לוגיקת המשחק
    public static final int LEVEL_EASY = 1, LEVEL_HARD = 2; // הגדרת קבועים עבור רמות קושי

    public RoundData currentRound; // אובייקט המחזיק את נתוני הסיבוב הנוכחי
    public int matchedCount = 0; // משתנה המונה כמה זוגות הותאמו בהצלחה בסיבוב הנוכחי
    
    private final WordRepository wordRepository; // רפרנס למחסן המילים
    private int selectedLeftIndex = -1; // משתנה השומר את האינדקס שנבחר בצד שמאל (-1 אומר שכלום לא נבחר)
    private final boolean[] matchedLeft = new boolean[4], matchedRight = new boolean[4]; // מערכים למעקב אחרי מילים שכבר הותאמו
    private int level; // משתנה השומר את רמת הקושי הנוכחית

    public GameManager(WordRepository repository) { // בנאי המקבל את מחסן המילים
        this.wordRepository = repository; // אתחול מחסן המילים
    }

    public void startNewGame(int level, String name) { // פונקציה להתחלת משחק חדש
        this.level = level; // הגדרת רמת הקושי
    }

    public RoundData startRound(int startIndex) { // פונקציה להתחלת סיבוב חדש
        selectedLeftIndex = -1; // איפוס הבחירה בצד שמאל
        matchedCount = 0; // איפוס מונה ההתאמות
        for (int i = 0; i < 4; i++) matchedLeft[i] = matchedRight[i] = false; // איפוס מערכי המעקב (כל המילים לא הותאמו)

        List<WordPair> all = wordRepository.getWordsByLevel(level); // שליבת כל המילים המתאימות לרמת הקושי
        if (all == null || all.size() < 4) return null; // אם אין מספיק מילים, החזר null

        String[] left = new String[4], right = new String[4]; // יצירת מערכים למילים שיוצגו על המסך
        List<WordPair> chosen = new ArrayList<>(); // רשימה למילים שנבחרו לסיבוב הזה
        List<String> rightList = new ArrayList<>(); // רשימה זמנית למילים בצד ימין לצורך ערבוב

        for (int i = 0; i < 4; i++) { // לולאה לבחירת 4 מילים לסיבוב
            WordPair wp = all.get((startIndex + i) % all.size()); // בחירת מילה מהרשימה הכללית (שימוש במודולו למניעת חריגה)
            chosen.add(wp); // הוספת המילה לרשימת הנבחרות
            left[i] = wp.english; // השמת המילה באנגלית למערך השמאלי
            rightList.add(wp.hebrew); // הוספת התרגום לעברית לרשימת הימנית
        }

        Collections.shuffle(rightList); // ערבוב המילים בצד ימין כדי שלא יהיו מול התרגום שלהן
        right = rightList.toArray(new String[0]); // המרת הרשימה המעורבבת למערך

        int[] mapping = new int[4]; // מערך שישמור את המיפוי הנכון (אינדקס שמאל -> אינדקס ימין)
        for (int i = 0; i < 4; i++) { // לולאה למציאת המיקום הנכון של כל תרגום
            for (int j = 0; j < 4; j++) { // לולאה פנימית על המערך הימני
                if (right[j].equals(chosen.get(i).hebrew)) { mapping[i] = j; break; } // אם מצאנו את התרגום המתאים, שמור את האינדקס
            }
        }

        currentRound = new RoundData(left, right, mapping); // יצירת אובייקט הסיבוב עם הנתונים שהכנו
        return currentRound; // החזרת נתוני הסיבוב
    }

    public void selectLeft(int index) { // פונקציה לטיפול בבחירת מילה בצד שמאל
        if (index >= 0 && index < 4 && !matchedLeft[index]) selectedLeftIndex = index; // אם האינדקס תקין והמילה לא הותאמה בעבר, שמור את הבחירה
    }

    public MatchResult selectRight(int rightIdx) { // פונקציה לטיפול בבחירת מילה בצד ימין (בדיקת התאמה)
        if (selectedLeftIndex == -1 || matchedRight[rightIdx]) // אם לא נבחרה מילה משמאל או שהמילה מימין כבר הותאמה
            return new MatchResult(false, matchedCount == 4, 0, null, null); // החזר תוצאה שלילית

        int leftIdx = selectedLeftIndex; // שמירת האינדקס השמאלי שנבחר
        selectedLeftIndex = -1; // איפוס הבחירה (כדי שהמשתמש יצטרך לבחור שוב משמאל)
        boolean correct = currentRound.correctMapping[leftIdx] == rightIdx; // בדיקה האם המיפוי נכון לפי נתוני הסיבוב

        if (correct) { // אם ההתאמה נכונה
            matchedLeft[leftIdx] = matchedRight[rightIdx] = true; // סמן את שתי המילים כ"הותאמו"
            matchedCount++; // הגדל את מונה ההתאמות
        }
        return new MatchResult(correct, matchedCount == 4, 0, leftIdx, rightIdx); // החזר את תוצאת הבדיקה
    }
}
