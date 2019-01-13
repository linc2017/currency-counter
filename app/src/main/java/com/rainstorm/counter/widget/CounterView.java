package com.rainstorm.counter.widget;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.rainstorm.counter.R;
import com.rainstorm.counter.utils.MathUtil;

import java.util.HashMap;
import java.util.Map;

import static android.graphics.Paint.ANTI_ALIAS_FLAG;

/**
 * @description Currency counter view
 * @author liys
 */
public class CounterView extends View{
    private TextPaint textPaint;           // Text paint
    private float textHeight;             // Text height
    private float textSize = 0;           // Text size
    private int textColor = Color.BLACK; // Text color
    private float charSpace;             // Character space
    private float charWidth;             // Character width
    private float textTop;
    private float textBottom;
    private float numberRectPaddingTopAndBottom;
    private Paint rectPaint;             // Rect paint
    private Map<Integer, ValueAnimator> animatorMap = new HashMap<>();
    private Map<Integer, Integer> previousNumberMap = new HashMap<>();
    private String text = "";
    private String oldText = "";

    public CounterView(Context context) {
        super(context);
        init(null,0);
    }

    public CounterView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public CounterView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }
    
    private void init(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(
                attrs, R.styleable.CounterView, defStyle, 0);
        textSize = a.getDimension(
                R.styleable.CounterView_textSize,
                textSize);
        textColor = a.getColor(
                R.styleable.CounterView_textColor,
                textColor);
        charSpace = a.getDimension(
                R.styleable.CounterView_charSpace,
                charSpace);
        a.recycle();
        // Init text paint
        textPaint = new TextPaint(ANTI_ALIAS_FLAG);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        Typeface typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/CircularStd-Book.otf");
        textPaint.setTypeface(typeface);
        Paint.FontMetrics fontMetrics = textPaint.getFontMetrics();
        textHeight = fontMetrics.descent;
        textTop = fontMetrics.top;
        textBottom = fontMetrics.bottom;
        numberRectPaddingTopAndBottom = MathUtil.add(Math.abs(MathUtil.sub(textTop, fontMetrics.ascent)), 
                Math.abs(MathUtil.sub(textBottom, textHeight)));
        // Init rect paint
        rectPaint = new Paint(ANTI_ALIAS_FLAG);
        rectPaint.setStyle(Paint.Style.STROKE);
        rectPaint.setStrokeWidth(1);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (heightMode == MeasureSpec.UNSPECIFIED || heightMode == MeasureSpec.AT_MOST) {
            heightSize = (int) MathUtil.add(textSize, Math.abs(numberRectPaddingTopAndBottom));
        }
        setMeasuredDimension(widthSize, heightSize);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if(TextUtils.isEmpty(text))
            return;
        float startX = 50; // X-coordinate
        float baseY = MathUtil.sub(textSize, MathUtil.sub(textBottom, textHeight));
        float startY = baseY; // Y-coordinate

        for (int i = 0; i < text.length(); i++) {
            textPaint.setTextSize(textSize);
            charWidth = textPaint.measureText(text.substring(i, i + 1));
            // Draw rect for number character
            boolean needDrawRect = false;
            float rectSideLength = textSize > charWidth ? textSize : charWidth;
            float startRectX = startX;
            if (Character.isDigit(text.charAt(i))) {
                needDrawRect = true;
                float left = startX;
                float top = Math.abs(numberRectPaddingTopAndBottom / 2);
                float right = MathUtil.add(startX, Math.abs(rectSideLength)) + 1;
                float bottom = MathUtil.add(baseY, Math.abs(numberRectPaddingTopAndBottom / 2)) + 1;
                RectF rect = new RectF(left, top, right, bottom);
                canvas.drawRect(rect,rectPaint);
                startX = MathUtil.add(startX, Math.abs(MathUtil.sub(rectSideLength, charWidth) / 2));
            } else {
                // Make non-number character a little smaller
                textPaint.setTextSize(MathUtil.mul(textSize, (float) 0.8));
            }
            // Draw text
            if (animatorMap.containsKey(i)) {
                // If we change from 1 to 6,scope = 6 - 1,means we will scroll from 1 to 6,
                int scope = 9;
                if (previousNumberMap.containsKey(i)) {
                    if(Integer.parseInt(text.substring(i, i + 1)) > previousNumberMap.get(i)) {
                        scope = Integer.parseInt(text.substring(i, i + 1)) - previousNumberMap.get(i);
                    } else {
                        scope = Integer.parseInt(text.substring(i, i + 1)) - previousNumberMap.get(i) + 10;   
                    }
                }
                float progress = (float) animatorMap.get(i).getAnimatedValue();
                if (progress > 0) {
                    float offsetY = MathUtil.mul(textSize * scope, progress);
                    startY = MathUtil.sub(MathUtil.sub(textSize, textHeight / 2), offsetY);
                }
                float rollingY = startY;
                for (int j = 0; j <= scope; j++) {
                    int rollingNumber = getRollingNumber(Integer.parseInt(text.substring(i, i + 1)), -j);
                    canvas.drawText(rollingNumber + "", startX, rollingY, textPaint);
                    rollingY += textSize;
                }
            } else {
                startY = baseY;
                canvas.drawText(text.substring(i, i + 1), startX, startY, textPaint);   
            }
            // Compute X-coordinate for the next character
            if (needDrawRect) {
                startX = MathUtil.add(startRectX, MathUtil.add(rectSideLength, charSpace));
            } else {
                startX = MathUtil.add(startX, MathUtil.add(charWidth, charSpace));   
            }
        }
        // Invalidate animations
        boolean end = true;
        for (Map.Entry<Integer, ValueAnimator> entry : animatorMap.entrySet()) {
            if (entry.getValue().isRunning()) {
                end = false;
            }
        }
        if (!end) {
            invalidate();
        }
    }

    /**
     * Handle input string text
     * 
     * @param inputText
     */
    public void setText(String inputText) {
        if (TextUtils.isEmpty(inputText))
            return;
        this.text = inputText;
        
        // Set animations
        setAnimations(inputText);
        // Backup old numbers of oldText,and update oldText
        backupTextInfo(inputText);
    }

    /**
     * Set animations
     * 
     * @param text
     */
    private void setAnimations(String text) {
        // Clear old animations
        for (Map.Entry<Integer, ValueAnimator> entry : animatorMap.entrySet()) {
            entry.getValue().cancel();
        }
        animatorMap.clear();

        // Add new animations
        if (!TextUtils.isEmpty(oldText)) {
            if ((text.length() == oldText.length()) && text.substring(0, 1).equals(oldText.substring(0, 1))) {
                for (int i = text.length() - 1; i >= 0; i--) {
                    if (!text.substring(i, i + 1).equals(oldText.substring(i, i + 1))) {
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(500);
                        valueAnimator.start();
                        animatorMap.put(i, valueAnimator);
                    }
                }
            } else if (text.length() != oldText.length()) {
                for (int i = text.length() - 1; i >= 0; i--) {
                    if (Character.isDigit(text.charAt(i))) {
                        ValueAnimator valueAnimator = ValueAnimator.ofFloat(1, 0).setDuration(500);
                        valueAnimator.start();
                        animatorMap.put(i, valueAnimator);
                    }
                }
            }
        }
        invalidate();
    }

    /**
     * Backup old numbers of oldText,and update oldText
     * 
     * @param text
     */
    private void backupTextInfo(String text) {
        previousNumberMap.clear();
        for (int j = 0; j < oldText.length(); j++) {
            if (Character.isDigit(oldText.charAt(j))) {
                previousNumberMap.put(j, Integer.parseInt(oldText.substring(j, j + 1)));
            }
        }
        oldText = text;
    }
    
    /**
     * Get a number on the rolling path
     * 
     * @param number
     * @param position
     * @return result Must be a positive number
     */
    private int getRollingNumber(int number, int position) {
        int result = 0;
        if (number + position > 9) {
            result = number + position - 10;
        } else if (number + position < 0) {
            result = number + position + 10;
        } else {
            result = number + position;
        }
        return result;
    }
}
