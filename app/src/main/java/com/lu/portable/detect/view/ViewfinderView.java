package com.lu.portable.detect.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.view.View;

public final class ViewfinderView extends View {
    private final Paint paint;
    private final Paint paintLine;
    public int b;
    public Rect frame;
    public int l;
    public int length = 0;
    public int r;
    public int t;
    int h;
    int w;
    private boolean boo;

    public ViewfinderView(Context paramContext, int paramInt1, int paramInt2, boolean paramBoolean) {
        super(paramContext);
        this.w = paramInt1;
        this.h = paramInt2;
        this.boo = paramBoolean;
        this.paint = new Paint();
        this.paintLine = new Paint();
    }

    public void onDraw(Canvas canvas) {
        int i = canvas.getWidth();
        int j = canvas.getHeight();
        if (this.boo)
            if ((j < 1080) || (j > 1620))
                this.length = (j / 4);
        while (true) {
            this.l = (this.w / 2 - this.length);
            this.r = (this.w / 2 + this.length);
            this.t = (this.h / 2 - this.length);
            this.b = (this.h / 2 + this.length);
            this.frame = new Rect(this.l, this.t, this.r, this.b);
            this.paint.setColor(Color.argb(128, 0, 0, 0));
            canvas.drawRect(0.0F, 0.0F, i, this.frame.top, this.paint);
            canvas.drawRect(0.0F, this.frame.top, this.frame.left, this.frame.bottom + 1, this.paint);
            canvas.drawRect(this.frame.right + 1, this.frame.top, i, this.frame.bottom + 1, this.paint);
            canvas.drawRect(0.0F, this.frame.bottom + 1, i, j, this.paint);
            this.paintLine.setColor(Color.rgb(0, 255, 0));
            this.paintLine.setStrokeWidth(4.0F);
            this.paintLine.setAntiAlias(true);
            canvas.drawLine(this.l, this.t, this.l + 50, this.t, this.paintLine);
            canvas.drawLine(this.l, this.t, this.l, this.t + 50, this.paintLine);
            canvas.drawLine(this.r, this.t, this.r - 50, this.t, this.paintLine);
            canvas.drawLine(this.r, this.t, this.r, this.t + 50, this.paintLine);
            canvas.drawLine(this.l, this.b, this.l + 50, this.b, this.paintLine);
            canvas.drawLine(this.l, this.b, this.l, this.b - 50, this.paintLine);
            canvas.drawLine(this.r, this.b, this.r - 50, this.b, this.paintLine);
            canvas.drawLine(this.r, this.b, this.r, this.b - 50, this.paintLine);
            if (frame == null) {
                return;
            }
            this.length = 250;
            if ((i < 1080) || (i > 1620)) {
                this.length = (i / 4);
                continue;
            }
            length = 250;
        }
    }
}