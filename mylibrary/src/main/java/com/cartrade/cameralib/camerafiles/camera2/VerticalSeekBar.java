package com.cartrade.cameralib.camerafiles.camera2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.widget.SeekBar;

public class VerticalSeekBar extends SeekBar {

    public VerticalSeekBar(Context context) {
        super(context);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public VerticalSeekBar(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(h, w, oldh, oldw);
    }

    @Override
    protected synchronized void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec);
        setMeasuredDimension(getMeasuredHeight(), getMeasuredWidth());
    }

    protected void onDraw(Canvas c) {
        c.rotate(-90);
        c.translate(-getHeight(), 0);
        drawThumb(c); //redrawing thumb
        super.onDraw(c);
    }

    void drawThumb(Canvas canvas) {
        Drawable thumb = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.JELLY_BEAN) {
            thumb = getThumb();
        }
        if (thumb != null) {
            Rect thumbBounds = thumb.getBounds();
            canvas.save();
            canvas.rotate(90, thumbBounds.exactCenterX(), thumbBounds.exactCenterY());
            thumb.draw(canvas);
            canvas.restore();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (!isEnabled()) {
            return false;

        }
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:

            case MotionEvent.ACTION_MOVE:

            case MotionEvent.ACTION_UP:
                setProgress(getMax() - (int) (getMax() * event.getY() / getHeight()));
                onSizeChanged(getWidth(), getHeight(), 0, 0);
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return true;
    }

    @Override
    public void setThumb(Drawable thumb) {
        super.setThumb(thumb);
        thumb.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }

    @Override
    public void setProgressDrawable(Drawable d) {
        super.setProgressDrawable(d);
        d.setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_IN);
    }
}

