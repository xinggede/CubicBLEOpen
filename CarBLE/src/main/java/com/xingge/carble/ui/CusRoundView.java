package com.xingge.carble.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.xingge.carble.R;
import com.xingge.carble.util.Tool;

public class CusRoundView extends View {

    private float w, r;
    private int lineWidth = 20, padding = 10;
    private Bitmap bitmap;
    private int degrees = 0;
    private String[] location = new String[]{"北", "东北", "东", "东南", "南", "西南", "西", "西北"};

    public CusRoundView(Context context) {
        super(context);
        init();
    }

    public CusRoundView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CusRoundView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private Paint paint;
    private Paint textPaint;

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.mipmap.arrow);
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStyle(Paint.Style.STROKE);

        textPaint = new Paint();
        textPaint.setColor(0xffbb0ac7);
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(Tool.dip2px(getContext(), 12));
    }

    public void setDegrees(int degrees) {
        this.degrees = degrees;
        invalidate();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w / 2;
        r = this.w - getWH(location[0], textPaint).height() - padding * 3;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        paint.setColor(Color.RED);
        paint.setStrokeWidth(padding);
        canvas.drawCircle(w, w, r, paint);

        textPaint.setColor(Color.BLACK);
        String str = "航向" + degrees + "°";
        Rect rt = getWH(str, textPaint);
        float x = w - rt.width() / 2;
        float y = w + bitmap.getHeight() / 2 + rt.height();
        canvas.drawText(str, 0, 2, x, y, textPaint);
        textPaint.setColor(0xffbb0ac7);
        Rect h = getWH("航向", textPaint);
        canvas.drawText(str, 2, str.length(), x + h.width() + 5, y, textPaint);


        canvas.drawBitmap(bitmap, w - bitmap.getWidth() / 2, w - bitmap.getHeight() / 2, paint);

        paint.setStrokeWidth(padding / 2);
        paint.setColor(Color.BLUE);
        canvas.save();
        canvas.rotate(-degrees, w, w);
        for (int i = 0; i < 8; i++) {
            if (i % 2 == 0) {
                textPaint.setColor(0xffbb0ac7);
                textPaint.setTextSize(Tool.dip2px(getContext(), 16));
            } else {
                textPaint.setColor(Color.BLACK);
                textPaint.setTextSize(Tool.dip2px(getContext(), 12));
            }
            canvas.drawLine(w, w - r, w, w - r + lineWidth, paint);
            String text = String.valueOf(i * 45);
            Rect rect = getWH(text, textPaint);
            canvas.drawText(text, w - rect.width() / 2, w - r + lineWidth + rect.height() + 5, textPaint);


            rect = getWH(location[i], textPaint);
            canvas.drawText(location[i], w - rect.width() / 2, padding + rect.height() - 5, textPaint);

            canvas.rotate(45, w, w);
        }
        canvas.restore();
    }


    private Rect getWH(String text, Paint p) {
        Rect rect = new Rect();
        p.getTextBounds(text, 0, text.length(), rect);
        return rect;
    }
}
