package com.example.sagiproject; // הגדרת החבילה של הפרויקט

import android.animation.ValueAnimator; // ייבוא מחלקה ליצירת אנימציות של ערכים
import android.content.Context; // ייבוא הקשר האפליקציה
import android.graphics.Canvas; // ייבוא מחלקת קנבס לציור דו-מימדי
import android.graphics.Paint; // ייבוא מחלקת "מכחול" להגדרת סגנון הציור (צבע, עובי וכו')
import android.util.AttributeSet; // ייבוא מחלקה לטיפול במאפייני XML של ה-View
import android.view.View; // ייבוא מחלקת הבסיס לכל רכיבי הממשק
import androidx.annotation.Nullable; // הערה המציינת שפרמטר יכול להיות null
import java.util.ArrayList; // ייבוא רשימה דינמית
import java.util.List; // ייבוא ממשק רשימה

public class GameBoardView extends View { // הגדרת רכיב תצוגה מותאם אישית לציור על המסך

    // מחלקה פנימית פשוטה לייצוג קו בודד שצויר על הלוח
    private static class Line {
        float x1, y1, x2, y2; // נקודות התחלה וסיום של הקו
        float progress = 0; // משתנה המייצג את התקדמות האנימציה (מ-0 עד 1)
        boolean isCorrect; // האם הקו מייצג התאמה נכונה (יקבע את צבעו)

        Line(float x1, float y1, float x2, float y2, boolean isCorrect) { // בנאי לאתחול נתוני הקו
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.isCorrect = isCorrect;
        }
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG); // יצירת אובייקט ציור עם החלקת קצוות
    private final List<Line> lines = new ArrayList<>(); // רשימה שתחזיק את כל הקווים שצריך לצייר כרגע

    public GameBoardView(Context context, @Nullable AttributeSet attrs) { // בנאי שנקרא כשה-View נוצר מה-XML
        super(context, attrs); // קריאה לבנאי של מחלקת האב
        paint.setStrokeWidth(10f); // הגדרת עובי הקו ל-10 פיקסלים
        paint.setStyle(Paint.Style.STROKE); // הגדרת סגנון הציור לקווי מתאר (ולא מילוי)
    }

    public void resetLines() { // פונקציה לניקוי כל הקווים מהמסך
        lines.clear(); // ריקון הרשימה
        invalidate(); // קריאה למערכת לצייר מחדש את ה-View (ניקוי התצוגה)
    }

    public void addAnimatedLineBetweenButtons(View b1, View b2, boolean correct) { // פונקציה לציור קו מונפש בין שני כפתורים
        int[] loc1 = new int[2], loc2 = new int[2], myLoc = new int[2]; // מערכים לשמירת מיקומי הרכיבים על המסך
        b1.getLocationInWindow(loc1); // קבלת המיקום של כפתור 1 בחלון
        b2.getLocationInWindow(loc2); // קבלת המיקום של כפתור 2 בחלון
        getLocationInWindow(myLoc); // קבלת המיקום של ה-View הנוכחי (הלוח) בחלון

        // חישוב נקודות האמצע של הכפתורים ביחס ללוח הציור
        float x1 = loc1[0] - myLoc[0] + b1.getWidth() / 2f;
        float y1 = loc1[1] - myLoc[1] + b1.getHeight() / 2f;
        float x2 = loc2[0] - myLoc[0] + b2.getWidth() / 2f;
        float y2 = loc2[1] - myLoc[1] + b2.getHeight() / 2f;

        Line line = new Line(x1, y1, x2, y2, correct); // יצירת אובייקט קו חדש
        lines.add(line); // הוספת הקו לרשימת הציור

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f).setDuration(300); // יצירת אנימטור שנע בין 0 ל-1 במשך 300 מילי-שניות
        anim.addUpdateListener(a -> { // הגדרת פעולה שתקרה בכל שלב באנימציה
            line.progress = (float) a.getAnimatedValue(); // עדכון אחוז ההתקדמות של הקו
            invalidate(); // דרישה לצייר מחדש את המסך כדי לראות את השינוי
        });
        anim.start(); // התחלת האנימציה

        if (!correct) { // אם ההתאמה שגויה, הקו צריך להיעלם אחרי זמן קצר
            postDelayed(() -> { // הגדרת פעולה בעיכוב
                lines.remove(line); // הסרת הקו מהרשימה
                invalidate(); // ציור מחדש (הקו ייעלם)
            }, 500); // עיכוב של חצי שנייה
        }
    }

    @Override
    protected void onDraw(Canvas canvas) { // הפונקציה המרכזית שאחראית על הציור בפועל
        super.onDraw(canvas); // קריאה לפונקציית הציור של האב
        for (Line l : lines) { // מעבר על כל הקווים ברשימה
            paint.setColor(l.isCorrect ? 0xFF00AA00 : 0xFFCC0000); // קביעת צבע: ירוק לנכון, אדום לשגוי
            // חישוב נקודת הקצה הנוכחית של הקו לפי התקדמות האנימציה
            float currentX = l.x1 + (l.x2 - l.x1) * l.progress;
            float currentY = l.y1 + (l.y2 - l.y1) * l.progress;
            canvas.drawLine(l.x1, l.y1, currentX, currentY, paint); // ציור הקו על הקנבס
        }
    }
}
