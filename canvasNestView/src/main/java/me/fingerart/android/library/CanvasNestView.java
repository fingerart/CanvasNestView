package me.fingerart.android.library;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;

/**
 * Created by FingerArt on 2016/8/9.
 */
public class CanvasNestView extends View implements Runnable {

    private int mCountPoint;
    private int mMaxDistance;
    private ArrayList<Point> mPoints;
    private int mPointColor;
    private int mLineColor;
    private boolean initialized;
    private Paint mPaint;
    private Paint mLinePaint;
    private int mWidth;
    private int mHeight;
    private Point mTouchPoint = new Point();

    public CanvasNestView(Context context) {
        this(context, null);
    }

    public CanvasNestView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public CanvasNestView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs, defStyleAttr);
    }

    private void initAttrs(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.CanvasNestView, defStyleAttr, 0);
        mCountPoint = typedArray.getInt(R.styleable.CanvasNestView_count_point, 99);
        mMaxDistance = typedArray.getInt(R.styleable.CanvasNestView_max_distance, 100);
        mPointColor = typedArray.getColor(R.styleable.CanvasNestView_color_point, context.getResources().getColor(R.color.canvas_nest_point_color));
        mLineColor = typedArray.getColor(R.styleable.CanvasNestView_color_line, context.getResources().getColor(R.color.canvas_nest_line_color));
    }

    private void initData() {
        if (initialized) return;

        mWidth = getWidth();
        mHeight = getHeight();

        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mPaint.setStrokeWidth(3);
        mPaint.setColor(mPointColor);
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setStrokeWidth(4);
        mLinePaint.setColor(mLineColor);

        mPoints = new ArrayList<>();
        for (int i = 0; i < mCountPoint; i++) {
            Point p = new Point();
            p.x = Math.random() * mWidth;
            p.y = Math.random() * mHeight;
            p.vx = (Math.random() * 2 - 1) * 3;
            p.vy = (Math.random() * 2 - 1) * 3;
            mPoints.add(p);
        }

        initialized = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        initData();
        for (int i = 0; i < mPoints.size(); i++) {
            Point p = mPoints.get(i);
            p.vx *= p.x > mWidth || p.x < 0 ? -1 : 1;
            p.vy *= p.y > mHeight || p.y < 0 ? -1 : 1;
            p.x += p.vx;
            p.y += p.vy;
            canvas.drawPoint(((float) p.x), (float) p.y, mPaint);
            for (int k = i + 1; k < mPoints.size(); k++) {
                Point pn = mPoints.get(k);
                double dist_x = p.x - pn.x;
                double dist_y = p.y - pn.y;
                double dist = Math.pow(dist_x, 2) + Math.pow(dist_y, 2);
                if (Math.sqrt(dist) < mMaxDistance) {
                    if (pn.equals(mTouchPoint) && Math.sqrt(dist) >= mMaxDistance / 2) {
                        p.x -= 0.03 * dist_x * 3;
                        p.y -= 0.03 * dist_y * 3;
                    }
                    double d = (mMaxDistance - Math.sqrt(dist)) / mMaxDistance;
                    mLinePaint.setColor(Color.argb(((int) (255 * d)), 0, 0, 0));
                    mLinePaint.setStrokeWidth((float) (d / 2));
                    canvas.drawLine(((float) p.x), ((float) p.y), ((float) pn.x), ((float) pn.y), mLinePaint);
                }
            }
        }
        postDelayed(this, 100);
    }

    @Override
    public void run() {
        removeCallbacks(this);
        postInvalidate();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mPoints.add(mTouchPoint);
            case MotionEvent.ACTION_MOVE:
                mTouchPoint.x = event.getX();
                mTouchPoint.y = event.getY();
                break;
            case MotionEvent.ACTION_UP:
                mPoints.remove(mTouchPoint);
                break;
        }
        return true;
    }
}
