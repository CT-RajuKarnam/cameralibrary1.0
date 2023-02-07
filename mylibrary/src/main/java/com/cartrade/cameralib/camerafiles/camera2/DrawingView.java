package com.cartrade.cameralib.camerafiles.camera2;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.view.View;

import androidx.core.content.ContextCompat;

import com.cartrade.cameralib.R;


public class DrawingView extends View {
    private boolean haveTouch = false;
    private Rect touchArea;

    public DrawingView(Context context, AttributeSet attrs) {
        super(context, attrs);
        haveTouch = false;
    }

    public void setHaveTouch(boolean val, Rect rect) {
        haveTouch = val;
        touchArea = rect;
    }

    @Override
    public void onDraw(Canvas canvas) {
        if (haveTouch) {
            Drawable d = ContextCompat.getDrawable(getContext(), R.mipmap.focus_focused);
            d.setBounds(touchArea.left, touchArea.top, touchArea.right, touchArea.bottom);
            d.draw(canvas);
        }
    }

}