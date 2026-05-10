package com.example.sagiproject;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import androidx.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class GameBoardView extends View {

    // מחלקה פנימית פשוטה לייצוג קו
    private static class Line {
        float x1, y1, x2, y2;
        float progress = 0;
        boolean isCorrect;

        Line(float x1, float y1, float x2, float y2, boolean isCorrect) {
            this.x1 = x1; this.y1 = y1; this.x2 = x2; this.y2 = y2;
            this.isCorrect = isCorrect;
        }
    }

    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final List<Line> lines = new ArrayList<>();

    public GameBoardView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        paint.setStrokeWidth(10f);
        paint.setStyle(Paint.Style.STROKE);
    }

    public void resetLines() {
        lines.clear();
        invalidate();
    }

    public void addAnimatedLineBetweenButtons(View b1, View b2, boolean correct) {
        int[] loc1 = new int[2], loc2 = new int[2], myLoc = new int[2];
        b1.getLocationInWindow(loc1);
        b2.getLocationInWindow(loc2);
        getLocationInWindow(myLoc);

        float x1 = loc1[0] - myLoc[0] + b1.getWidth() / 2f;
        float y1 = loc1[1] - myLoc[1] + b1.getHeight() / 2f;
        float x2 = loc2[0] - myLoc[0] + b2.getWidth() / 2f;
        float y2 = loc2[1] - myLoc[1] + b2.getHeight() / 2f;

        Line line = new Line(x1, y1, x2, y2, correct);
        lines.add(line);

        ValueAnimator anim = ValueAnimator.ofFloat(0f, 1f).setDuration(300);
        anim.addUpdateListener(a -> {
            line.progress = (float) a.getAnimatedValue();
            invalidate();
        });
        anim.start();

        if (!correct) {
            postDelayed(() -> {
                lines.remove(line);
                invalidate();
            }, 500);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Line l : lines) {
            paint.setColor(l.isCorrect ? 0xFF00AA00 : 0xFFCC0000);
            float currentX = l.x1 + (l.x2 - l.x1) * l.progress;
            float currentY = l.y1 + (l.y2 - l.y1) * l.progress;
            canvas.drawLine(l.x1, l.y1, currentX, currentY, paint);
        }
    }
}
