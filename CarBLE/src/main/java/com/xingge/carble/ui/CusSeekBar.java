package com.xingge.carble.ui;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.xingge.carble.util.Tool;

import java.util.ArrayList;

public class CusSeekBar extends View {

    private LinearGradient linearGradient;
    private int w;
    private RectF mRect = new RectF();
    private ArrayList<RectBean> rectBeans = new ArrayList<>();
    private float r;
    private Paint mPaint, cPaint, pPaint;
    private int rectH;
    private int max = 100, progress = 0;
    private PointF point;
    private onChangedListener changedListener;
    private int lastProgress = -1;

    public CusSeekBar(Context context) {
        super(context);
        init(context);
    }

    public CusSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public CusSeekBar(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        cPaint = new Paint();
        cPaint.setStyle(Paint.Style.FILL);
        cPaint.setAntiAlias(true);
        cPaint.setColor(Color.YELLOW);

        pPaint = new Paint();
        pPaint.setStyle(Paint.Style.FILL);
        pPaint.setAntiAlias(true);
        pPaint.setColor(Color.RED);

        rectH = Tool.dip2px(context, 5);
        point = new PointF();
    }

    public void setChangedListener(onChangedListener changedListener) {
        this.changedListener = changedListener;
    }

    public void setColor(int color) {
        RectF rectF = new RectF();
        rectF.left = point.x + 1;
        rectF.top = mRect.top;
        rectF.right = point.x + 1;
        rectF.bottom = mRect.bottom;

        rectBeans.get(rectBeans.size() - 1).max = point.x;
        rectBeans.add(new RectBean(rectF, color, w - r));
    }

    public int getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;

        float x = progress * w * 1.0f / max;
        if (x < r) {
            x = r;
        } else if (x > w - r) {
            x = w - r;
        }
        point.x = x;
        rectBeans.get(rectBeans.size() - 1).rectF.right = x;
        invalidate();
    }

    public void clear() {
        if (!rectBeans.isEmpty()) {
            rectBeans.clear();
        }
        RectF pRect = new RectF();
        //进度
        pRect.left = r;
        pRect.top = mRect.top;
        pRect.right = r;
        pRect.bottom = mRect.bottom;

        rectBeans.add(new RectBean(pRect, Color.RED, w - r));
    }

    public void addProgress(int min, int progress, int color) {
        RectF rectF = new RectF();
        float x = min * w * 1.0f / max;
        if (x < r) {
            x = r;
        }
        rectF.left = x;
        rectF.top = mRect.top;
        rectF.bottom = mRect.bottom;

        float maxX = progress * w * 1.0f / max;
        if (maxX > w - r) {
            maxX = w - r;
        }
        rectF.right = maxX;
        rectBeans.add(new RectBean(rectF, color, maxX));
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        this.w = w;
        r = rectH * 2;
        point.x = r;
        point.y = h / 2;
        mRect.left = r;
        mRect.top = (h - rectH) / 2;
        mRect.right = w - r;
        mRect.bottom = mRect.top + rectH;

        RectF pRect = new RectF();
        //进度
        pRect.left = r;
        pRect.top = mRect.top;
        pRect.right = r;
        pRect.bottom = mRect.bottom;

        rectBeans.add(new RectBean(pRect, Color.RED, w - r));

        linearGradient = new LinearGradient(0, mRect.top, getWidth(), mRect.bottom, 0xaa9d64b6, 0xaae50cf1, Shader.TileMode.CLAMP);
        mPaint.setShader(linearGradient);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //画外框
//        canvas.drawRoundRect(mRect, rectH / 2, rectH / 2, mPaint);
        canvas.drawRect(mRect, mPaint);

        for (RectBean rectBean : rectBeans) {
            if (rectBean.rectF.width() > 0) {
                pPaint.setColor(rectBean.color);
                canvas.drawRect(rectBean.rectF, pPaint);
            }
        }

        //画圆点
        canvas.drawCircle(point.x, point.y, r, cPaint);
    }


    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return true;
        }
        calcPoint(event.getX(), event.getY());
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (changedListener != null) {
                    if (lastProgress != progress) {
                        changedListener.onProgressChanged(progress);
                        lastProgress = progress;
                    }
                }
                return true;

            case MotionEvent.ACTION_MOVE:
                if (changedListener != null) {
                    if (lastProgress != progress) {
                        changedListener.onProgressChanged(progress);
                        lastProgress = progress;
                    }
                }
                break;

            case MotionEvent.ACTION_UP:

                break;

            default:
                break;
        }
        return super.onTouchEvent(event);
    }

    private void calcPoint(float x, float y) {
        if (x < 0) {
            x = 0;
        } else if (x > w) {
            x = w;
        }
        progress = (int) (max * x / w);

        if (x < r) {
            x = r;
        } else if (x > w - r) {
            x = w - r;
        }
        point.x = x;

        for (int i = 0; i < rectBeans.size(); i++) {
            RectBean rectBean = rectBeans.get(i);
            if (x < rectBean.rectF.left) {
                rectBean.rectF.right = rectBean.rectF.left;
            } else if (x <= rectBean.max && x >= rectBean.rectF.left) {
                rectBean.rectF.right = x;
            } else if (x >= rectBean.max) {
                rectBean.rectF.right = rectBean.max;
            }
        }

        invalidate();
    }

    public interface onChangedListener {
        void onProgressChanged(int progress);
    }

    class RectBean {
        public RectF rectF;
        public int color;
        public float max;

        public RectBean(RectF rectF, int color, float max) {
            this.rectF = rectF;
            this.color = color;
            this.max = max;
        }
    }
}
