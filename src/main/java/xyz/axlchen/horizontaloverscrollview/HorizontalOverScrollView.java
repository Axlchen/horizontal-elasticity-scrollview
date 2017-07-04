package xyz.axlchen.horizontaloverscrollview;

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
import android.util.TypedValue;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.Scroller;
import android.widget.TextView;


/**
 * @auther: axl.chen
 * date: 2017-06-22
 */

public class HorizontalOverScrollView extends HorizontalScrollView {

    private static final float RATIO = 0.4f;
    private static final float RIPPLE_RATIO = 2f;
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
    private int mPadding;
    private int mOffset;
    private float mTextSize;


    public HorizontalOverScrollView(Context context) {
        this(context, null);
    }

    public HorizontalOverScrollView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HorizontalOverScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        mStringDragging = "更\n多";
        mStringDragged = "释\n放\n查\n看";

        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.HorizontalOverScrollView);
        int rippleColor = attributes.getColor(R.styleable.HorizontalOverScrollView_ripple_color, Color.GRAY);
        mTextColor = attributes.getColor(R.styleable.HorizontalOverScrollView_drag_text_color, Color.BLACK);
        if (!TextUtils.isEmpty(attributes.getString(R.styleable.HorizontalOverScrollView_dragging_text))) {
            mStringDragging = attributes.getString(R.styleable.HorizontalOverScrollView_dragging_text);
        }
        if (!TextUtils.isEmpty(attributes.getString(R.styleable.HorizontalOverScrollView_dragged_text))) {
            mStringDragged = attributes.getString(R.styleable.HorizontalOverScrollView_dragged_text);
        }
        int mTextWidth = (int) attributes.getDimension(R.styleable.HorizontalOverScrollView_msg_width,
                getResources().getDimension(R.dimen.default_msg_width));

        mTextSize = attributes.getDimension(R.styleable.HorizontalOverScrollView_msg_text_size,
                getResources().getDimension(R.dimen.default_msg_text_size));

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

        mPadding = (int) getResources().getDimension(R.dimen.default_msg_padding);
        mOffset = mTextWidth + mPadding * 2;
    }

    @Override
    public void addView(View child) {
        mContentView = child;
        mWrapView.addView(mContentView);
        mWrapView.addView(getMoreView());
        super.addView(mWrapView);
    }

    @Override
    public void addView(View child, ViewGroup.LayoutParams params) {
        child.setLayoutParams(params);
        addView(child);
    }

    private View getMoreView() {
        mMsg = new TextView(getContext());
        mMsg.setPadding(mPadding, 0, mPadding, 0);
        mMsg.setGravity(Gravity.CENTER);
        mMsg.setText(mStringDragging);
        mMsg.setTextSize(TypedValue.COMPLEX_UNIT_PX, mTextSize);
        mMsg.setTextColor(mTextColor);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(mOffset,
                ViewGroup.LayoutParams.MATCH_PARENT);
        layoutParams.rightMargin = -mOffset;
        mMsg.setLayoutParams(layoutParams);
        return mMsg;
    }

    public void setMoreActionListener(MoreActionListener listener) {
        this.mListener = listener;
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                mLastX = ev.getRawX();
                break;
            case MotionEvent.ACTION_MOVE:
                float mDeltaX = (ev.getRawX() - mLastX);
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

                            mControlPoint.x = (int) (mRect.right - mWrapView.getScrollX() * RIPPLE_RATIO);
                            mControlPoint.y = mRect.bottom / 2;
                            invalidate();
                        }
                        return true;
                    }
                } else {
                    if (!canScrollHorizontally(1) || mWrapView.getScrollX() < 0) {
                        if (!canScrollHorizontally(1)) {
                            getDrawingRect(mRect);
                        }
                        if (mWrapView.getScrollX() >= mOffset) {
                            mContentView.scrollBy((int) -mDeltaX, 0);
                            mMsg.setText(mStringDragged);
                        } else {
                            mWrapView.scrollBy((int) -mDeltaX, 0);
                            mMsg.setText(mStringDragging);

                            mControlPoint.x = (int) (mRect.right - mWrapView.getScrollX() * RIPPLE_RATIO);
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
        if (mWrapView.getScrollX() >= mOffset && mListener != null) {
            mListener.moreAction();
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        mPath.reset();
        mPath.moveTo(mRect.right, mRect.top);
        mPath.quadTo(mControlPoint.x, mControlPoint.y, mRect.right, mRect.bottom);
        canvas.drawPath(mPath, mPaint);
    }

    @Override
    public void computeScroll() {
        super.computeScroll();
        if (mScroller.computeScrollOffset()) {
            if (mContentView.getScrollX() >= 10) {
                mContentView.scrollTo(mScroller.getCurrX() - mWrapView.getScrollX(), 0);
            } else if (mContentView.getScrollX() > 0) {
                mContentView.scrollTo(0, 0);
            } else {
                mWrapView.scrollTo(mScroller.getCurrX(), 0);
                if (mWrapView.getScrollX() >= 0) {
                    mControlPoint.x = (int) (mRect.right - mWrapView.getScrollX() * RIPPLE_RATIO - 1);
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
