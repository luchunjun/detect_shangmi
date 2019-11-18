package com.lu.portable.detect.view;

import android.content.Context;
import android.graphics.Typeface;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.widget.TextView;

public class IconTextView extends TextView {
    public IconTextView(Context context) {
        super(context);
    }

    public IconTextView(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
     setTypeface(Typeface.createFromAsset(context.getAssets(), "font/iconfont.ttf"));
    }
}
