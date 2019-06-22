package com.xingge.carble.ui;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Rect;

import com.daivd.chart.component.base.IAxis;
import com.daivd.chart.data.style.FontStyle;
import com.daivd.chart.data.style.LineStyle;
import com.daivd.chart.provider.component.level.ILevel;
import com.daivd.chart.provider.component.level.LevelLine;

public class CusLevelLine implements ILevel {

    public static int left = 0;
    public static int right = 1;
    private LineStyle lineStyle = new LineStyle();
    private double value;
    private int textDirection = left;
    private FontStyle textStyle = new FontStyle();
    private int direction = IAxis.AxisDirection.LEFT;

    public CusLevelLine(double value) {
        this.value = value;
    }


    public LineStyle getLineStyle() {
        return lineStyle;
    }

    public void setLineStyle(LineStyle lineStyle) {
        this.lineStyle = lineStyle;
    }

    @Override
    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }


    public int getTextDirection() {
        return textDirection;
    }

    public void setTextDirection(int textDirection) {
        this.textDirection = textDirection;
    }

    public FontStyle getTextStyle() {
        return textStyle;
    }

    @Override
    public int getAxisDirection() {
        return direction;
    }

    public void setDirection(int direction) {
        this.direction = direction;
    }

    public void setTextStyle(FontStyle textStyle) {
        this.textStyle = textStyle;
    }

    @Override
    public void drawLevel(Canvas canvas, Rect rect, float y, Paint paint) {
        getLineStyle().fillPaint(paint);
        Path path = new Path();
        path.moveTo(rect.left, y);
        path.lineTo(rect.right, y);
        canvas.drawPath(path, paint);
        getTextStyle().fillPaint(paint);
        float textHeight = paint.measureText("1", 0, 1);
        float startX;
        float startY = y + textHeight + getLineStyle().getWidth();
        String levelLineValue = String.valueOf((int)getValue());
        if (getTextDirection() == LevelLine.left) {
            startX = rect.left;
        } else {
            startX = rect.right - textHeight * levelLineValue.length();
        }
        canvas.drawText(levelLineValue, startX, startY, paint);
    }
}
