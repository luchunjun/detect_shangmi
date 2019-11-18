package com.lu.portable.detect.view;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.TranslateAnimation;

public class ScrollView extends android.widget.ScrollView {
    private static final int ANIM_TIME = 200;
    private static final float MOVE_FACTOR = 0.3F;
    private static final String TAG = "ElasticScrollView";
    private boolean canPullDown = false;
    private boolean canPullUp = false;
    private View contentView;
    private boolean isMoved = false;
    private int offset;
    private Rect originalRect = new Rect();
    private float startY;

    public ScrollView(Context paramContext) {
        super(paramContext);
    }

    public ScrollView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    private boolean isCanPullDown() {
        return (getScrollY() == 0) || (this.contentView.getHeight() < getHeight() + getScrollY());
    }

    private boolean isCanPullUp() {
        return this.contentView.getHeight() <= getHeight() + getScrollY();
    }

    public boolean dispatchTouchEvent(MotionEvent paramMotionEvent) {
        int j = 0;
        if (this.contentView == null)
            return super.dispatchTouchEvent(paramMotionEvent);
        switch (paramMotionEvent.getAction()) {
            default:
            case 0:
            case 1:
            case 2:
        }
        while (true) {
            //return super.dispatchTouchEvent(paramMotionEvent);
            this.canPullDown = isCanPullDown();
            this.canPullUp = isCanPullUp();
            this.startY = paramMotionEvent.getY();
//            continue;
//            if (!this.isMoved)
//                continue;
            TranslateAnimation localTranslateAnimation = new TranslateAnimation(0.0F, 0.0F, this.contentView.getTop(), this.originalRect.top);
            localTranslateAnimation.setDuration(200L);
            this.contentView.startAnimation(localTranslateAnimation);
            this.contentView.layout(this.originalRect.left, this.originalRect.top, this.originalRect.right, this.originalRect.bottom);
            this.canPullDown = false;
            this.canPullUp = false;
            this.isMoved = false;
//            continue;
            if ((!this.canPullDown) && (!this.canPullUp)) {
                this.startY = paramMotionEvent.getY();
                this.canPullDown = isCanPullDown();
                this.canPullUp = isCanPullUp();
                continue;
            }
            int k = (int) (paramMotionEvent.getY() - this.startY);
            int i;
            if (((!this.canPullDown) || (k <= 0)) && ((!this.canPullUp) || (k >= 0))) {
                i = j;
                if (this.canPullUp) {
                    i = j;
                    if (!this.canPullDown) ;
                }
            } else {
                i = 1;
            }
            if (i == 0)
                continue;
            this.offset = (int) (k * 0.3F);
            this.contentView.layout(this.originalRect.left, this.originalRect.top + this.offset, this.originalRect.right, this.originalRect.bottom + this.offset);
            this.isMoved = true;
        }
    }

    protected void onFinishInflate() {
        super.onFinishInflate();
        if (getChildCount() > 0)
            this.contentView = getChildAt(0);
    }

    protected void onLayout(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
        super.onLayout(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4);
        if (this.contentView == null)
            return;
        this.originalRect.set(this.contentView.getLeft(), this.contentView.getTop(), this.contentView.getRight(), this.contentView.getBottom());
    }

    public interface StartDropedListener {
        void run();
    }
}