package com.lu.portable.detect.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.widget.ImageView;

public class RoundedImageView extends ImageView {
    public RoundedImageView(Context paramContext) {
        super(paramContext);
    }

    public RoundedImageView(Context paramContext, AttributeSet paramAttributeSet) {
        super(paramContext, paramAttributeSet);
    }

    public RoundedImageView(Context paramContext, AttributeSet paramAttributeSet, int paramInt) {
        super(paramContext, paramAttributeSet, paramInt);
    }

    public static Bitmap getCroppedBitmap(Bitmap bitmap, int paramInt) {
        if ((bitmap.getWidth() != paramInt) || (bitmap.getHeight() != paramInt))
            bitmap = Bitmap.createScaledBitmap(bitmap, paramInt, paramInt, false);
        while (true) {
            Bitmap localBitmap = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Bitmap.Config.ARGB_8888);
            Canvas localCanvas = new Canvas(localBitmap);
            Paint localPaint = new Paint();
            Rect localRect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
            localPaint.setAntiAlias(true);
            localPaint.setFilterBitmap(true);
            localPaint.setDither(true);
            localCanvas.drawARGB(0, 0, 0, 0);
            localPaint.setColor(Color.parseColor("#BAB399"));
            localCanvas.drawCircle(bitmap.getWidth() / 2 + 0.7F, bitmap.getHeight() / 2 + 0.7F, bitmap.getWidth() / 2 + 0.1F, localPaint);
            localPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
            localCanvas.drawBitmap(bitmap, localRect, localRect, localPaint);
            return localBitmap;
        }
    }

    protected void onDraw(Canvas canvas) {
        Drawable localObject = getDrawable();
        Bitmap bitmap = ((BitmapDrawable) localObject).getBitmap().copy(Bitmap.Config.ARGB_8888, true);
        int i = getWidth();
        //getHeight();
        canvas.drawBitmap(getCroppedBitmap(bitmap, i), 0.0F, 0.0F, null);
    }
}