package com.to8to.corp.chen.axl.widgettest.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;

import com.to8to.corp.chen.axl.widgettest.R;

/**
 * @Auther: axl.chen
 * @Date: 2017-06-22
 */

public class HorizontalOverScrollView2 extends HorizontalScrollView {

    private static final float RATIO = 0.5f;
    private float mDeltaX;
    private MoreActionListener mListener;
    private TextView mMsg;
    private LinearLayout mWrapView;
    private Scroller mScroller;
    private float mLastX;
    private View mContentView;
    private Paint mPaint;
    private Path mPath;
    private Rect mRect;
    private Point mControlPoint;
    private String mStringDragging;
    private String mStringDragged;
    private int mTextColor;


    public HorizontalOverScrollView2(Context context) {
        this(context, null);
    }

    public HorizontalOverScrollView2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalOverScrollView2(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mStringDragging = "更多";
        mStringDragged = "释放查看";

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HorizontalOverScrollView2);
        int rippleColor = attributes.getColor(R.styleable.HorizontalOverScrollView2_ripple_color, Color.GRAY);
        mTextColor = attributes.getColor(R.styleable.HorizontalOverScrollView2_drag_text_color, 0);
        if (!TextUtils.isEmpty(attributes.getString(R.styleable.HorizontalOverScrollView2_dragging_text))) {
            mStringDragging = attributes.getString(R.styleable.HorizontalOverScrollView2_dragging_text);
        }
        if (!TextUtils.isEmpty(attributes.getString(R.styleable.HorizontalOverScrollView2_dragged_text))) {
            mStringDragged = attributes.getString(R.styleable.HorizontalOverScrollView2_dragged_text);
        }

        mWrapView = new LinearLayout(context);
        mWrapView.setOrientation(LinearLayout.HORIZONTAL);
        mScroller = new Scroller(context);

        mPaint = new Paint();
        mPaint.setColor(rippleColor);
        mPaint.setStrokeWidth(1);
        mPaint.setAntiAlias(true);
        mPaint.setStyle(Paint.Style.FILL);

        mPath = new Path();
        mRect = new Rect();
        mControlPoint = new Point();
    }

    @Override
    public void addView(View child) {
        mContentView = child;
        mWrapView.addView(mContentView);
        mWrapView.addView(getMoreView());
        super.addView(mWrapView);
    }

    private View getMoreView() {
        mMsg = new TextView(getContext());
        mMsg.setPadding(10, 0, 10, 0);
        mMsg.setGravity(Gravity.CENTER);
        mMsg.setText(mStringDragging);
        mMsg.setTextSize(13);
        mMsg.setTextColor(mTextColor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(50,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.rightMargin = -70;
        mMsg.setLayoutParams(layoutParams);
        return mMsg;
    }

    public void setListener(MoreActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                mDeltaX = (ev.getRawX() - mLastX);
                mLastX = ev.getRawX();
                mDeltaX = mDeltaX * RATIO;
                if (mDeltaX > 0) {
                    if (!canScrollHorizontally(-1) || mWrapView.getScrollX() > 0) {
                        if (mContentView.getScrollX() > 0) {
                            mContentView.scrollBy((int) -mDeltaX, 0);
                            mMsg.setText(mStringDragged);
                        } else {
                            mWrapView.scrollBy((int) -mDeltaX, 0);
                            mMsg.setText(mStringDragging);

                            mControlPoint.x = mRect.right - mWrapView.getScrollX() * 2;
                            mControlPoint.y = mRect.bottom / 2;
                            invalidate();
                        }
                        return true;
                    }
                } else {
                    if (!canScrollHorizontally(1) || mWrapView.getScrollX() < 0) {
                        if (!canScrollHorizontally(1)) {
                            getDrawingRect(mRect);
//                            Log.d("HorizontalOverScrollVie", "rect:" + mRect);
                        }
                        if (mWrapView.getScrollX() >= 70) {
                            mContentView.scrollBy((int) -mDeltaX, 0);
                            mMsg.setText(mStringDragged);
                        } else {
                            mWrapView.scrollBy((int) -mDeltaX, 0);
                            mMsg.setText(mStringDragging);

                            mControlPoint.x = mRect.right - mWrapView.getScrollX() * 2;
                            mControlPoint.y = mRect.bottom / 2;
                            invalidate();
                        }
                        return true;
                    }
                }
                break;
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                checkAction();
                if (mWrapView.getScrollX() < 0) {
                    mScroller.startScroll(mWrapView.getScrollX(), 0, 0 - mWrapView.getScrollX(), 0,
                            (0 - mWrapView.getScrollX()) * 5);
                } else if (mContentView.getScrollX() > 0) {
                    mScroller.startScroll(mContentView.getScrollX() + mWrapView.getScrollX(), 0,
                            0 - mContentView.getScrollX() - mWrapView.getScrollX(), 0,
                            (mContentView.getScrollX() + mWrapView.getScrollX()) * 6);
                } else {
                    mScroller.startScroll(mWrapView.getScrollX(), 0, 0 - mWrapView.getScrollX(), 0,
                            mWrapView.getScrollX() * 5);
                }
                invalidate();
                break;
        }
        return super.onTouchEvent(ev);
    }

    private void checkAction() {
        if (mWrapView.getScrollX() >= 70 && mListener != null) {
            mListener.moreAction();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        //起点
        mPath.moveTo(mRect.right, mRect.top);
        //mPath
        mPath.quadTo(mControlPoint.x, mControlPoint.y, mRect.right, mRect.bottom);
        //画path
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mContentView.getScrollX() >= 10) {
//                Log.d("Axlchen", "inner===start:" + mScroller.getStartX() + " current:" + mScroller.getCurrX());
                mContentView.scrollTo(mScroller.getCurrX() - mWrapView.getScrollX(), 0);
            } else if (mContentView.getScrollX() > 0) {
//                Log.d("Axlchen", "inner===start:" + mScroller.getStartX() + " current:" + mScroller.getCurrX());
                mContentView.scrollTo(0, 0);
            } else {
//                Log.d("Axlchen", "outer===start:" + mScroller.getStartX() + " current:" + mScroller.getCurrX());
                mWrapView.scrollTo(mScroller.getCurrX(), 0);
                if (mWrapView.getScrollX() >= 0) {
                    mControlPoint.x = mRect.right - mWrapView.getScrollX() * 2 - 1;
                    mControlPoint.y = mRect.bottom / 2;
                }
            }
        }
        invalidate();
    }

    public interface MoreActionListener {
        void moreAction();
    }
}
