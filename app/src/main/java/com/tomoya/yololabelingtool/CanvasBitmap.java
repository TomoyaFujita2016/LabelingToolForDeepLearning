package com.tomoya.yololabelingtool;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;
import android.widget.ImageView;

/**
 * Created by Tomoya on 2017/05/19.
 */

public class CanvasBitmap {

    private Bitmap bitmap, tmpBitmap;
    private ImageView imageView;
    private Activity activity;
    private Canvas canvas;
    private Paint paintCrossHair, paintRect;

    public int[] bmpSize;
    public int[] crossHairXY;
    public int[] rectStartXY, rectEndXY;

    CanvasBitmap(Bitmap bitmap, ImageView imageView, Activity activity) {
        Log.d("Constructor", "Loaded Constructor");

        this.bitmap = bitmap;
        this.imageView = imageView;
        this.activity = activity;

        crossHairXY = new int[2];
        rectStartXY = new int[2];
        rectEndXY = new int[2];

        canvas = new Canvas(bitmap);
        canvas.drawBitmap(bitmap, 0, 0, null);


        bmpSize = new int[2];
        bmpSize[0] = bitmap.getWidth();
        bmpSize[1] = bitmap.getHeight();

        tmpBitmap = this.bitmap.copy(Bitmap.Config.ARGB_8888, true);

        paintCrossHair = new Paint();
        paintCrossHair.setStyle(Paint.Style.STROKE);
        paintCrossHair.setAntiAlias(true);

        paintRect = new Paint();
        paintRect.setStyle(Paint.Style.STROKE);
        paintRect.setAntiAlias(true);


    }


    private int[] GLtoLC(int[] XY) {
        Rect rectView = new Rect();
        imageView.getGlobalVisibleRect(rectView);

        XY[0] -= rectView.left;
        XY[1] -= rectView.top;
        XY[0] = XY[0] * bitmap.getWidth() / imageView.getWidth();
        XY[1] = XY[1] * bitmap.getHeight() / imageView.getHeight();

        return XY;
    }

    private int[] ToInner(int[] XY) {
        XY = GLtoLC(XY);
        if (XY[0] < 0) {
            XY[0] = 0;
        }
        if (bitmap.getWidth() < XY[0]) {
            XY[0] = bitmap.getWidth();
        }
        if (XY[1] < 0) {
            XY[1] = 0;
        }
        if (bitmap.getHeight() < XY[1]) {
            XY[1] = bitmap.getHeight();
        }
        return XY;
    }

    public void drawCrossHair(int[] XY, int color, int thickness) {
        XY = ToInner(XY);
        crossHairXY[0] = XY[0];
        crossHairXY[1] = XY[1];

        paintCrossHair.setColor(color);
        paintCrossHair.setStrokeWidth(thickness);

        canvas.drawBitmap(tmpBitmap, 0, 0, null);
        canvas.drawLine(0, XY[1], bitmap.getWidth(), XY[1], paintCrossHair);
        canvas.drawLine(XY[0], 0, XY[0], bitmap.getHeight(), paintCrossHair);
        imageView.setImageBitmap(bitmap);


    }

    public void drawRectangle(int[] startXY, int[] endXY, int color, int thickness) {
        startXY = ToInner(startXY);
        rectStartXY[0] = startXY[0];
        rectStartXY[1] = startXY[1];
        endXY = ToInner(endXY);
        rectEndXY[0] = endXY[0];
        rectEndXY[1] = endXY[1];

        paintRect.setColor(color);
        paintRect.setStrokeWidth(thickness);
        paintRect.setColor(color);

        canvas.drawBitmap(tmpBitmap, 0, 0, null);
        canvas.drawLine(startXY[0], startXY[1], startXY[0], endXY[1], paintRect);    // (|  )
        canvas.drawLine(startXY[0], startXY[1], endXY[0], startXY[1], paintRect);    //(upper side ---)
        canvas.drawLine(startXY[0], endXY[1], endXY[0], endXY[1], paintRect);        //(lower side ---)
        canvas.drawLine(endXY[0], startXY[1], endXY[0], endXY[1], paintRect);      //(  |)

        imageView.setImageBitmap(bitmap);

    }


}