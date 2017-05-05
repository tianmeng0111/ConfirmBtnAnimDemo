package com.tm.example.confirmbtnanimdemo.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import com.tm.example.confirmbtnanimdemo.R;

/**
 * Created by Tian on 2017/5/4.
 */

public class BtnConfirm extends View {
    private static final String TAG = "BtnConfirm";

//    private Context mContext;

    private Paint mBgPaint;
    private Paint mTextPaint;
    private Paint mIconPaint;

    private int mWidth;
    private int mHeight;
    private RectF mRectBg;
    private float mCurrentRadius;
    private int mCurrentWidth;
    private int mTextAlpha = 255;

    private float baseline;

    private Path mIconPath = new Path();
    private PathMeasure mPathMeasure;
    private PathEffect mPathEffect;

    private ValueAnimator mCornerRadiusAnimator;
    private ValueAnimator mWidthAnimator;
    private ValueAnimator mTextAlphaAnimator;
    private ValueAnimator mIconAnimator;
    private AnimatorSet mAnimatorSet;

    private static final int DURATION = 1000;

    private boolean isStartDrawIcon = false;

    //重复点击动画不重复发生
    private boolean isAniming = false;

    /**
     * 属性
     */
    private String mText;
    private int mTextSize;
    private int mBackColor;
    private float mCornerRadius = 0;

    private static final int DEFAULT_TEXT_SIZE = 30;
    private static final int DEFAULT_BACK_COLOR = Color.parseColor("#FF4081");
    private static final int DEFAULT_CORNER_RADIUS = 0;


    public BtnConfirm(Context context) {
        super(context);
        init(context, null);
    }

    public BtnConfirm(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public BtnConfirm(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
//        mContext = context;
        if (attrs != null) {
            TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.BtnConfirm);
            mText = ta.getString(R.styleable.BtnConfirm_text);
            mTextSize = (int) ta.getDimension(R.styleable.BtnConfirm_text_size, DEFAULT_TEXT_SIZE);
            mBackColor = ta.getColor(R.styleable.BtnConfirm_back_color, DEFAULT_BACK_COLOR);
            mCornerRadius = ta.getDimension(R.styleable.BtnConfirm_corners_radius, DEFAULT_CORNER_RADIUS);

        } else {
            mText = "";
            mTextSize = DEFAULT_TEXT_SIZE;
            mBackColor = DEFAULT_BACK_COLOR;
            mCornerRadius = DEFAULT_CORNER_RADIUS;
        }
        mCurrentRadius = mCornerRadius;
        initPaint();
    }

    private void initPaint() {
        mBgPaint = new Paint();
        mBgPaint.setAntiAlias(true);
        mBgPaint.setDither(true);
        mBgPaint.setStyle(Paint.Style.FILL);
        mBgPaint.setColor(mBackColor);

        mTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setAntiAlias(true);
        mTextPaint.setDither(true);
        mTextPaint.setTextAlign(Paint.Align.CENTER);
        mTextPaint.setColor(Color.WHITE);
        mTextPaint.setTextSize(mTextSize);

        mIconPaint = new Paint();
        mIconPaint.setAntiAlias(true);
        mIconPaint.setDither(true);
        mIconPaint.setColor(Color.WHITE);
        mIconPaint.setStyle(Paint.Style.STROKE);
        mIconPaint.setStrokeWidth(10);
    }

    private void initAnim() {
        mCornerRadiusAnimator = ValueAnimator.ofFloat(mCornerRadius, mHeight / 2);
        mCornerRadiusAnimator.setDuration(500);
        mCornerRadiusAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                float animatedValue = (float) animation.getAnimatedValue();
                mCurrentRadius = animatedValue;
                invalidate();
            }
        });

        mWidthAnimator = ValueAnimator.ofInt(mWidth, mHeight);
        mWidthAnimator.setDuration(DURATION);
        mWidthAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mCurrentWidth = animatedValue;
                invalidate();
            }
        });

        mTextAlphaAnimator = ValueAnimator.ofInt(255, 0);
        mTextAlphaAnimator.setDuration(700);
        mTextAlphaAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int animatedValue = (int) animation.getAnimatedValue();
                mTextPaint.setAlpha(animatedValue);
                invalidate();
            }
        });

        mIconAnimator = ValueAnimator.ofFloat(1, 0);
        mIconAnimator.setDuration(DURATION);
        mIconAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                if (!isStartDrawIcon) {
                    initPath();
                    isStartDrawIcon = true;
                }
                float animatedValue = (float) animation.getAnimatedValue();
                mPathEffect = new DashPathEffect(new float[] {mPathMeasure.getLength(), mPathMeasure.getLength()},
                        animatedValue * mPathMeasure.getLength());
                mIconPaint.setPathEffect(mPathEffect);
                invalidate();
            }
        });

        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.play(mWidthAnimator).with(mTextAlphaAnimator)
                .with(mCornerRadiusAnimator).before(mIconAnimator);
        mAnimatorSet.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                isAniming = false;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                isAniming = true;
            }
        });
    }

    private void initPath() {
        mIconPath.moveTo(mRectBg.left + mHeight * 1 / 4, mHeight / 2);
        mIconPath.lineTo(mRectBg.left + mHeight * 9 / 20, mHeight * 13 / 20);
        mIconPath.lineTo(mRectBg.left + mHeight * 3 / 4, mHeight * 7 / 20);

        mPathMeasure = new PathMeasure(mIconPath, true);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthMode == MeasureSpec.AT_MOST) {
            float textWidth = mTextPaint.measureText(mText);
            widthSize = (int) (textWidth + mHeight) + getPaddingLeft() + getPaddingRight();
        }

        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightMode == MeasureSpec.AT_MOST) {
            heightSize = mTextSize + getPaddingTop() + getPaddingBottom();
        }
        if (heightSize > widthSize) {
            widthSize = heightSize;
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        Log.e(TAG, "w:" + w + ";h:" + h);

        mRectBg = new RectF(0, 0, mWidth, mHeight);
        mCurrentWidth = mWidth;
        Paint.FontMetricsInt fontMetrics = mTextPaint.getFontMetricsInt();
        baseline = (mRectBg.bottom + mRectBg.top - fontMetrics.bottom - fontMetrics.top) / 2;
        initAnim();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mRectBg.left = (mWidth - mCurrentWidth) / 2;
        mRectBg.right = (mWidth + mCurrentWidth) / 2;
        canvas.drawRoundRect(mRectBg, mCurrentRadius, mCurrentRadius, mBgPaint);

        canvas.drawText(mText, mRectBg.centerX(), baseline, mTextPaint);

        if (isStartDrawIcon) {
            canvas.drawPath(mIconPath, mIconPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (!isAniming) {
                setInitial();
                mAnimatorSet.start();
            }
        }
        return super.onTouchEvent(event);
    }

    public void setInitial() {
        if (isAniming) {
            return;
        }
        mCurrentRadius = mCornerRadius;
        mCurrentWidth = mWidth;
        mTextAlpha = 255;
        mTextPaint.setAlpha(mTextAlpha);
        isStartDrawIcon = false;
        invalidate();
    }

    public boolean isAniming() {
        return isAniming;
    }
}
